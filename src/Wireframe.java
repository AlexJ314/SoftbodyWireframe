import java.io.*;

public class Wireframe {
  private Point3[] points;
  private int pointCount = 0;
  private Edge[] edges;
  private int edgeCount = 0;
  private boolean freeze = false;
  private String printFile = "Print file";
  private boolean doFullRepulsive = Constants.doFullRepulsive;
  private Face[] faces;
  private int faceCount = 0;
  private boolean updateFaces = true;

  public Wireframe() {
    this.points = new Point3[64];
    this.edges = new Edge[64];
    this.faces = new Face[64];
  }

  private int addEdge(Edge edge) {
    if (this.edgeCount >= this.edges.length) {
      Edge[] tmp = new Edge[64+this.edgeCount];
      for (int i = 0; i < this.edgeCount; i++) {
        tmp[i] = this.getEdge(i);
      }
      for (int i = this.edgeCount; i < 64+this.edgeCount; i++) {
        tmp[i] = null;
      }
      this.edges = tmp;
    }
    this.edges[this.edgeCount] = edge;
    this.updateFaces = true;
    return this.edgeCount++;
  }

  private int removeEdge(int index) {
    if (index >= 0 && index < this.edgeCount) {
      for (int i = index; i < this.edgeCount-1; i++) {
        this.edges[i] = this.edges[i+1];
      }
      this.edges[this.edgeCount-1] = null;
    }
    this.updateFaces = true;
    return this.edgeCount--;
  }

  public void addLoosePolygon(Point3 p, int n) {
    this.addLoosePolygon(p, n, 1.0);
  }

  public void addConnectedPolygon(Point3 p, int n) {
    this.addConnectedPolygon(p, n, 1.0);
  }

  public void addLoosePolygon(Point3 p, int n, double length) {
    Point3 first = new Point3(p);
    first.jiggle();
    this.addPoint(first);
    Point3 a = first;
    Point3 b = first;
    for (int i = 1; i < n; i++) {
      b = a;
      a = new Point3(p);
      a.jiggle();
      this.addPoint(a);
      this.addConnection(a, b, length);
    }
    this.addConnection(a, first, length);
  }

  public void addConnectedPolygon(Point3 p, int n, double length) {
    Point3 q = new Point3(p);
    q.jiggle();
    this.addPoint(q);
    this.addConnection(p, q, length);
    this.addConnectedPolygon(p, q, n, length);
  }

  public void addConnectedPolygon(Point3 p, Point3 q, int n, double length) {
    boolean disconnected = true;
    for (int i = 0; i < p.getConnectionCount(); i++) {
      if (p.getConnection(i).getPoint() == q) {
        disconnected = false;
        break;
      }
    }
    if (disconnected) {
      this.addConnection(p, q);
    }
    Point3 a = p;
    Point3 b = q;
    for (int i = 2; i < n; i++) {
      b = a;
      a = new Point3(p);
      a.jiggle();
      this.addPoint(a);
      this.addConnection(a, b, length);
    }
    this.addConnection(a, q, length);
  }

  public int addPoint(Point3 point) {
    if (this.pointCount >= this.points.length) {
      Point3[] tmp = new Point3[64+this.pointCount];
      for (int i = 0; i < this.pointCount; i++) {
        tmp[i] = this.getPoint(i);
      }
      for (int i = this.pointCount; i < 64+this.pointCount; i++) {
        tmp[i] = null;
      }
      this.points = tmp;
    }
    this.points[this.pointCount] = point;
    this.updateFaces = true;
    return this.pointCount++;
  }

  public int removePoint(Point3 p) {
    if (p == null) {
      return -1;
    }
    for (int i = 0; i < this.pointCount; i++) {
      if (p == this.points[i]) {
        while(p.getConnectionCount() > 0) {
          this.removeConnection(p, p.getConnection(0).getPoint());
        }
        for (int j = i; j < this.pointCount-1; j++) {
          this.points[j] = this.points[j+1];
        }
        this.points[this.pointCount-1] = null;
        this.pointCount--;
        this.updateFaces = true;
        return i;
      }
    }
    return -1;
  }

  public void lockPointToggle(Point3 p) {
    if (p == null) {
      return;
    }
    p.lockToggle();
  }

  public boolean getLockPointToggle(Point3 p) {
    if (p == null) {
      return false;
    }
    return p.getLockToggle();
  }

  public void addConnection(int pointA, int pointB) {
    Point3 pa = getPoint(pointA);
    Point3 pb = getPoint(pointB);
    if (pa != null && pb != null) {
      pa.addConnection(new Connection(pb));
      pb.addConnection(new Connection(pa));
      this.addEdge(new Edge(pa, pb));
    }
  }

  public void addConnection(int pointA, int pointB, double length) {
    Point3 pa = getPoint(pointA);
    Point3 pb = getPoint(pointB);
    if (length > 0 && pa != null && pb != null) {
      pa.addConnection(new Connection(length, pb));
      pb.addConnection(new Connection(length, pa));
      this.addEdge(new Edge(length, pa, pb));
    }
  }

  public void addConnection(Point3 pa, Point3 pb) {
    this.addConnection(pa, pb, 1);
  }

  public void addConnection(Point3 pa, Point3 pb, double length) {
    if (pa != null && pb == null) {
      pb = new Point3(pa.getX()+2*Math.random()-1, pa.getY()+2*Math.random()-1, pa.getZ()+2*Math.random()-1);
      this.addPoint(pb);
    } else if (pa == null && pb != null) {
      pa = new Point3(pb.getX()+2*Math.random()-1, pb.getY()+2*Math.random()-1, pb.getZ()+2*Math.random()-1);
      this.addPoint(pa);
    }
    if (pa != null && pb != null) {
      for (int i = 0; i < pa.getConnectionCount(); i++) {
        if (pa.getConnection(i).getPoint() == pb) {
          this.removeConnection(pa, pb);
          break;
        }
      }
      pa.addConnection(new Connection(length, pb));
      pb.addConnection(new Connection(length, pa));
      this.addEdge(new Edge(pa, pb));
    }
  }

  public boolean removeConnection(Point3 pa, Point3 pb) {
    if (pa != null && pb != null) {
      boolean exists = false;
      for (int i = 0; i < pa.getConnectionCount(); i++) {
        if (pa.getConnection(i).getPoint() == pb) {
          pa.removeConnection(i);
          exists = true;
          break;
        }
      }
      if (!exists) {
        return false;
      }
      for (int i = 0; i < pb.getConnectionCount(); i++) {
        if (pb.getConnection(i).getPoint() == pa) {
          pb.removeConnection(i);
          exists = true;
          break;
        }
      }
      if (!exists) {
        return false;
      }
      for (int i = 0; i < this.edgeCount; i++) {
        if ((this.getEdge(i).getPointA() == pa && this.getEdge(i).getPointB() == pb) || 
            (this.getEdge(i).getPointB() == pa && this.getEdge(i).getPointA() == pb)) {
          this.removeEdge(i);
          return true;
        }
      }
    }
    return false;
  }

  public int getPointCount() {
    return this.pointCount;
  }

  public Point3 getPoint(int index) {
    if (index >= 0 && index < this.pointCount) {
      return this.points[index];
    }
    return null;
  }

  public int getEdgeCount() {
    return this.edgeCount;
  }

  public Edge getEdge(int index) {
    if (index >= 0 && index < this.edgeCount) {
      return this.edges[index];
    }
    return null;
  }

  public void tick() {
    if (this.freeze) {
      return;
    }
    this.calculateForces();
  }

  public void calculateForces() {
    if (Constants.doLinear || Constants.doAngular) {
      for (int i = 0; i < this.pointCount; i++) {
        this.points[i].calculateEdgeForce();
      }
    }
    if (this.doFullRepulsive) {
      for (int i = 0; i < this.pointCount-1; i++) {
        for (int j = i+1; j < this.pointCount; j++) {
          if (this.points[i] != null) {
            this.points[i].getForce().fullRepulsive(this.points[i], this.points[j]);
          }
        }
      }
    }
    if (Constants.doSmallRepulsive) {
      for (int i = 0; i < this.pointCount; i++) {
        if (this.points[i] != null) {
          for (int j = 0; j < this.points[i].getConnectionCount(); j++) {
            this.points[i].getForce().smallRepulsive(this.points[i], this.points[i].getConnection(j).getPoint());
            for (int k = 0; k < this.points[i].getConnection(j).getPoint().getConnectionCount(); k++) {
              this.points[i].getForce().smallRepulsive(this.points[i], this.points[i].getConnection(j).getPoint().getConnection(k).getPoint());
            }
          }
        }
      }
    }
    for (int i = 0; i < this.pointCount; i++) {
      this.points[i].applyForce(Constants.forceCoeff);
    }
  }

  public void jiggle() {
    for (int i = 0; i < this.pointCount; i++) {
      this.points[i].jiggle();
    }
  }

  public void freeze() {
    this.freeze = !this.freeze;
  }

  public void print() {
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(this.getPrintFile(), "UTF-8");
    } catch (FileNotFoundException e) {
      System.out.printf("Print file \"%s\" not found\n", this.getPrintFile());
      return;
    } catch (UnsupportedEncodingException e) {
      System.out.println("Unsupported encoding");
      return;
    }
    for (int i = 0; i < this.pointCount; i++) {
      String out = String.valueOf(i);
      if (this.points[i].getLockToggle() == true) {
        out += "*";
      }
      out += "(";
      out += String.valueOf(this.points[i].getX());
      out += ","+String.valueOf(this.points[i].getY());
      out += ","+String.valueOf(this.points[i].getZ());
      out += ") ";
      for (int j = 0; j < this.points[i].getConnectionCount(); j++) {
        for (int k = 0; k < this.pointCount; k++) {
          if (this.points[k] == this.points[i].getConnection(j).getPoint()) {
            out += String.valueOf(k) +
            ":" +String.valueOf(this.points[i].getConnection(j).getLength()) +
            ":" + String.valueOf(this.points[i].distance(this.points[k])) +
            ":" + String.valueOf(Math.round(this.points[i].distance(this.points[k])*100)/100.0) +
            " ";
            break;
          }
        }
      }
      writer.println(out);
    }
    writer.close();
  }

  public Vector3 midpoint() {
    double mx = 0;
    double my = 0;
    double mz = 0;
    for (int i = 0; i < this.pointCount; i++) {
      mx += this.points[i].getX();
      my += this.points[i].getY();
      mz += this.points[i].getZ();
    }
    double c = this.pointCount;
    if (c == 0) {
      c = 1;
    }
    return new Vector3(mx/c, my/c, mz/c);
  }

  public String setPrintFile(String f) {
    if (f.equals("") == false) {
      this.printFile = f;
    }
    return this.printFile;
  }

  public String getPrintFile() {
    return this.printFile;
  }

  public void mergePoints(Point3 p, Point3 q) {
    this.removeConnection(p, q);
    for (int i = 0; i < q.getConnectionCount(); i++) {
      this.addConnection(p, q.getConnection(i).getPoint(),
        q.getConnection(i).getLength());
    }
    if (q.getLockToggle() == true) {
      p.setLock(true);
    }
    this.removePoint(q);
  }

  public void doFullRepulsiveToggle() {
    this.doFullRepulsive = !this.doFullRepulsive;
  }

  private void _updateFaces() {
    this.updateFaces = false;
    this.faceCount = 0;
    this.faces = new Face[64];
    /*
    For each point
      reset visited for all
      if connections < 2
        skip
      if connections == 2
        mark visited
        start new face
        add this point to current face
        next point = connected point not yet visited
      if connections > 2
        mark visited
        add this point to face
        make connections total copies of current face
        for each face in this round
          breadth first search until return to first point
            
          if face not in master face list
            add face to master list
    */
    for (int i = 0; i < this.pointCount; i++) {
      /*
      for all connections, find first branch or self
      if found branch, bfs on all branches until self
      */
    }
  }

  private void bfs(int start, int target) {
    /*
    start, step one along all connections until target
    */
  }

  public int addFace(Face face) {
    if (this.faceCount >= this.faces.length) {
      Face[] tmp = new Face[64+this.faceCount];
      for (int i = 0; i < this.faceCount; i++) {
        tmp[i] = this.getFace(i);
      }
      for (int i = this.faceCount; i < 64+this.faceCount; i++) {
        tmp[i] = null;
      }
      this.faces = tmp;
    }
    this.faces[this.faceCount] = face;
    return this.faceCount++;
  }

  public int removeFace(Face f) {
    if (f == null) {
      return -1;
    }
    for (int i = 0; i < this.faceCount; i++) {
      if (f == this.faces[i]) {
        for (int j = i; j < this.faceCount-1; j++) {
          this.faces[j] = this.faces[j+1];
        }
        this.faces[this.faceCount-1] = null;
        this.faceCount--;
        return i;
      }
    }
    return -1;
  }

  public int getFaceCount() {
    if (this.updateFaces == true) {
      this._updateFaces();
    }
    return this.faceCount;
  }

  public Face getFace(int index) {
    if (this.updateFaces == true) {
      this._updateFaces();
    }
    if (index >= 0 && index < this.faceCount) {
      return this.faces[index];
    }
    return null;
  }
}
