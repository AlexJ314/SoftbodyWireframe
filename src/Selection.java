/** 
 * @file Selection.java
 * @brief Creates a selection box
 * @author Alex Johnson
 */

public class Selection {
  private Display display;
  private int startX = 0;
  private int startY = 0;
  private int endX = 0;
  private int endY = 0;
  private Vector3[] bounds;
  private Wireframe copiedPoints = new Wireframe();

  public Selection(Display display) {
    this.display = display;
    this.deselect();
  }

  public void deselect() {
    this.startX = 0;
    this.startY = 0;
    this.endX = 0;
    this.endY = 0;
    this.bounds = new Vector3[] {new Vector3(0, 0, 0), new Vector3(0, 0, 0),
      new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0),
      new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0, 0, 0)};
  }

  public void setStart(int x, int y) {
    this.startX = x;
    this.startY = y;
  }

  public void modifyInitial(int x, int y) {
    double minDepth = Double.NaN;
    double maxDepth = Double.NaN;
    for (int i = 0; i < this.display.getWireframe().getPointCount(); i++) {
      Vector3 proj = this.display.fproject(this.display.getWireframe().getPoint(i));
      boolean inX = false;
      boolean inY = false;
      if ((proj.getX() >= this.startX-7 && proj.getX() <= x-7) ||
        (proj.getX() <= this.startX-7 && proj.getX() >= x-7)) {
        inX = true;
      }
      if ((proj.getY() >= this.startY-30 && proj.getY() <= y-30) ||
        (proj.getY() <= this.startY-30 && proj.getY() >= y-30)) {
        inY = true;
      }
      if (inX && inY) {
        double d = proj.getZ();
        if (Double.isNaN(maxDepth) || d > maxDepth) {
          maxDepth = d;
        }
        if (Double.isNaN(minDepth) || d < minDepth) {
          minDepth = d;
        }
      }
    }
    if (Double.isNaN(maxDepth)) {
      maxDepth = 1;
    } else {
      maxDepth += 0.01;
    }
    if (Double.isNaN(minDepth)) {
      minDepth = -1;
    } else {
      minDepth -= 0.01;
    }
    this.bounds[0] = this.display.get3d(this.startX, this.startY, maxDepth);
    this.bounds[1] = this.display.get3d(x, this.startY, maxDepth);
    this.bounds[2] = this.display.get3d(this.startX, y, maxDepth);
    this.bounds[3] = this.display.get3d(x, y, maxDepth);
    this.bounds[4] = this.display.get3d(this.startX, this.startY, minDepth);
    this.bounds[5] = this.display.get3d(x, this.startY, minDepth);
    this.bounds[6] = this.display.get3d(this.startX, y, minDepth);
    this.bounds[7] = this.display.get3d(x, y, minDepth);
  }

  public void modifySelection(int x, int y) {
    int bound = 0;
    Vector3 proj = null;
    Vector3 best = null;
    double d = 0;
    double distance = 0;

    best = this.display.fproject(this.bounds[0]);
    distance = Math.sqrt(Math.pow(x-7-best.getX(), 2) + Math.pow(y-30-best.getY(), 2));
    for (int i = 0; i < 8; i++) {
      proj = this.display.fproject(this.bounds[i]);
      d = Math.sqrt(Math.pow(x-7-proj.getX(), 2) + Math.pow(y-30-proj.getY(), 2));
      if (d < distance) {
        best = proj;
        distance = d;
        bound = i;
      } else if (d == distance && proj.getZ() > best.getZ()) {
        best = proj;
        distance = d;
        bound = i;
      }
    }

    Vector3 movedto = this.display.get3d(x, y, this.display.project(this.bounds[bound]).getZ());
    Vector3 movedby = movedto.sub(this.bounds[bound]);
    switch (bound) {
      case 0:
        this.bounds[1] = this.bounds[1].add(movedby.sub(((this.bounds[1].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[2] = this.bounds[2].add(movedby.sub(((this.bounds[2].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[3] = this.bounds[3].add(((this.bounds[7].sub(this.bounds[3])).norm()).proj(movedby));
        this.bounds[4] = this.bounds[4].add(movedby.sub(((this.bounds[4].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[5] = this.bounds[5].add(((this.bounds[7].sub(this.bounds[5])).norm()).proj(movedby));
        this.bounds[6] = this.bounds[6].add(((this.bounds[7].sub(this.bounds[6])).norm()).proj(movedby));
        break;
      case 1:
        this.bounds[0] = this.bounds[0].add(movedby.sub(((this.bounds[1].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[2] = this.bounds[2].add(((this.bounds[6].sub(this.bounds[2])).norm()).proj(movedby));
        this.bounds[3] = this.bounds[3].add(movedby.sub(((this.bounds[3].sub(this.bounds[1])).norm()).proj(movedby)));
        this.bounds[4] = this.bounds[4].add(((this.bounds[6].sub(this.bounds[4])).norm()).proj(movedby));
        this.bounds[5] = this.bounds[5].add(movedby.sub(((this.bounds[5].sub(this.bounds[1])).norm()).proj(movedby)));
        this.bounds[7] = this.bounds[7].add(((this.bounds[7].sub(this.bounds[6])).norm()).proj(movedby));
        break;
      case 2:
        this.bounds[0] = this.bounds[0].add(movedby.sub(((this.bounds[2].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[1] = this.bounds[1].add(((this.bounds[5].sub(this.bounds[1])).norm()).proj(movedby));
        this.bounds[3] = this.bounds[3].add(movedby.sub(((this.bounds[3].sub(this.bounds[2])).norm()).proj(movedby)));
        this.bounds[4] = this.bounds[4].add(((this.bounds[5].sub(this.bounds[4])).norm()).proj(movedby));
        this.bounds[6] = this.bounds[6].add(movedby.sub(((this.bounds[6].sub(this.bounds[2])).norm()).proj(movedby)));
        this.bounds[7] = this.bounds[7].add(((this.bounds[7].sub(this.bounds[5])).norm()).proj(movedby));
        break;
      case 3:
        this.bounds[0] = this.bounds[0].add(((this.bounds[4].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[1] = this.bounds[1].add(movedby.sub(((this.bounds[3].sub(this.bounds[1])).norm()).proj(movedby)));
        this.bounds[2] = this.bounds[2].add(movedby.sub(((this.bounds[3].sub(this.bounds[2])).norm()).proj(movedby)));
        this.bounds[5] = this.bounds[5].add(((this.bounds[5].sub(this.bounds[4])).norm()).proj(movedby));
        this.bounds[6] = this.bounds[6].add(((this.bounds[6].sub(this.bounds[4])).norm()).proj(movedby));
        this.bounds[7] = this.bounds[7].add(movedby.sub(((this.bounds[7].sub(this.bounds[3])).norm()).proj(movedby)));
        break;
      case 4:
        this.bounds[0] = this.bounds[0].add(movedby.sub(((this.bounds[4].sub(this.bounds[0])).norm()).proj(movedby)));
        this.bounds[1] = this.bounds[1].add(((this.bounds[3].sub(this.bounds[1])).norm()).proj(movedby));
        this.bounds[2] = this.bounds[2].add(((this.bounds[3].sub(this.bounds[2])).norm()).proj(movedby));
        this.bounds[5] = this.bounds[5].add(movedby.sub(((this.bounds[5].sub(this.bounds[4])).norm()).proj(movedby)));
        this.bounds[6] = this.bounds[6].add(movedby.sub(((this.bounds[6].sub(this.bounds[4])).norm()).proj(movedby)));
        this.bounds[7] = this.bounds[7].add(((this.bounds[7].sub(this.bounds[3])).norm()).proj(movedby));
        break;
      case 5:
        this.bounds[0] = this.bounds[0].add(((this.bounds[2].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[1] = this.bounds[1].add(movedby.sub(((this.bounds[5].sub(this.bounds[1])).norm()).proj(movedby)));
        this.bounds[3] = this.bounds[3].add(((this.bounds[3].sub(this.bounds[2])).norm()).proj(movedby));
        this.bounds[4] = this.bounds[4].add(movedby.sub(((this.bounds[5].sub(this.bounds[4])).norm()).proj(movedby)));
        this.bounds[6] = this.bounds[6].add(((this.bounds[6].sub(this.bounds[2])).norm()).proj(movedby));
        this.bounds[7] = this.bounds[7].add(movedby.sub(((this.bounds[7].sub(this.bounds[5])).norm()).proj(movedby)));
        break;
      case 6:
        this.bounds[0] = this.bounds[0].add(((this.bounds[1].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[2] = this.bounds[2].add(movedby.sub(((this.bounds[6].sub(this.bounds[2])).norm()).proj(movedby)));
        this.bounds[3] = this.bounds[3].add(((this.bounds[3].sub(this.bounds[1])).norm()).proj(movedby));
        this.bounds[4] = this.bounds[4].add(movedby.sub(((this.bounds[6].sub(this.bounds[4])).norm()).proj(movedby)));
        this.bounds[5] = this.bounds[5].add(((this.bounds[5].sub(this.bounds[1])).norm()).proj(movedby));
        this.bounds[7] = this.bounds[7].add(movedby.sub(((this.bounds[7].sub(this.bounds[6])).norm()).proj(movedby)));
        break;
      case 7:
        this.bounds[1] = this.bounds[1].add(((this.bounds[1].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[2] = this.bounds[2].add(((this.bounds[2].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[3] = this.bounds[3].add(movedby.sub(((this.bounds[7].sub(this.bounds[3])).norm()).proj(movedby)));
        this.bounds[4] = this.bounds[4].add(((this.bounds[4].sub(this.bounds[0])).norm()).proj(movedby));
        this.bounds[5] = this.bounds[5].add(movedby.sub(((this.bounds[7].sub(this.bounds[5])).norm()).proj(movedby)));
        this.bounds[6] = this.bounds[6].add(movedby.sub(((this.bounds[7].sub(this.bounds[6])).norm()).proj(movedby)));
        break;
      default:
        break;
    }
    this.bounds[bound] = movedto;
  }

  public boolean inBounds(Point3 p) {
    boolean in = true;
    double a, min, max;
    Vector3 plane;

    plane = this.bounds[2].sub(this.bounds[0]);
    min = plane.dot(this.bounds[0]);
    max = plane.dot(this.bounds[2]);
    a = plane.dot(p);
    if (!((min <= a && a <= max) || (max <= a && a <= min))) {
      in = false;
    }
    plane = this.bounds[1].sub(this.bounds[0]);
    min = plane.dot(this.bounds[0]);
    max = plane.dot(this.bounds[1]);
    a = plane.dot(p);
    if (!((min <= a && a <= max) || (max <= a && a <= min))) {
      in = false;
    }
    plane = this.bounds[4].sub(this.bounds[0]);
    min = plane.dot(this.bounds[0]);
    max = plane.dot(this.bounds[4]);
    a = plane.dot(p);
    if (!((min <= a && a <= max) || (max <= a && a <= min))) {
      in = false;
    }

    return in;
  }

  public Vector3[] getBounds() {
    return this.bounds;
  }

  public void copy(Wireframe w) {
    this.copiedPoints = new Wireframe();
    int c = w.getPointCount();
    Point3[] map = new Point3[2*c];
    for (int i = 0; i < c; i++) {
      Point3 p = w.getPoint(i);
      map[2*i] = p;
      if (this.inBounds(p)) {
        Point3 q = new Point3(p);
        q.sub(this.bounds[0]);
        this.copiedPoints.addPoint(q);
        map[2*i+1] = q;
      } else {
        map[2*i+1] = null;
      }
    }
    for (int i = 0; i < c; i++) {
      if (map[2*i+1] != null) {
        int cc = map[2*i].getConnectionCount();
        for (int j = 0; j < cc; j++) {
          Point3 conn = map[2*i].getConnection(j).getPoint();
          if (this.inBounds(conn) == true) {
            for (int k = 0; k < i; k++) {
              if (conn == map[2*k] && map[2*k+1] != null) {
                this.copiedPoints.addConnection(map[2*i+1], map[2*k+1], map[2*i].getConnection(j).getLength());
                break;
              }
            }
          }
        }
      }
    }
  }

  public void paste(Wireframe w, Vector3 v) {
    int c = this.copiedPoints.getPointCount();
    Point3[] map = new Point3[2*c];
    for (int i = 0; i < c; i++) {
      Point3 p = this.copiedPoints.getPoint(i);
      map[2*i] = p;
      Point3 q = new Point3(p);
      q.add(v);
      w.addPoint(q);
      map[2*i+1] = q;
    }
    for (int i = 0; i < c; i++) {
      if (map[2*i+1] != null) {
        int cc = map[2*i].getConnectionCount();
        for (int j = 0; j < cc; j++) {
          Point3 conn = map[2*i].getConnection(j).getPoint();
          for (int k = 0; k < i; k++) {
            if (conn == map[2*k]) {
              w.addConnection(map[2*i+1], map[2*k+1], map[2*i].getConnection(j).getLength());
              break;
            }
          }
        }
      }
    }
  }
}
