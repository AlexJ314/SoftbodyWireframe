public class Point3 {
  private double x, y, z;
  private Connection[] connections;
  private int connectionCount = 0;
  private Force force;
  private int highlight = 0;
  private boolean lock = false;
  private boolean grabbed = false;

  public Point3(Vector3 v) {
    this.setX(v.getX());
    this.setY(v.getY());
    this.setZ(v.getZ());
    this.connections = new Connection[8];
    for (int i = 0; i < 8; i++) {
      this.connections[i] = null;
    }
    this.force = new Force(0, 0, 0);
  }

  public Point3(Point3 p) {
    this.setX(p.getX());
    this.setY(p.getY());
    this.setZ(p.getZ());
    this.connections = new Connection[8];
    for (int i = 0; i < 8; i++) {
      this.connections[i] = null;
    }
    this.lock = p.getLockToggle();
    this.force = new Force(0, 0, 0);
  }

  public Point3(double x, double y, double z) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
    this.connections = new Connection[8];
    for (int i = 0; i < 8; i++) {
      this.connections[i] = null;
    }
    this.force = new Force(0, 0, 0);
  }

  public void setX(double x) {
    this.x = x;
  }

  public void setY(double y) {
    this.y = y;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public double getX() {
    return this.x;
  }

  public double getY() {
    return this.y;
  }

  public double getZ() {
    return this.z;
  }

  public int getConnectionCount() {
    return this.connectionCount;
  }

  public Connection getConnection(int index) {
    if (index >= 0 && index < this.getConnectionCount()) {
      return this.connections[index];
    }
    return null;
  }

  private int incConnectionCount() {
    return this.connectionCount++;
  }

  private int decConnectionCount() {
    return this.connectionCount--;
  }

  private int setConnection(int index, Connection e) {
    if (index >= 0 && index < this.connections.length) {
      this.connections[index] = e;
      return index;
    }
    return -1;
  }

  public int addConnection(Connection e) {
    if (this.connectionCount >= this.connections.length) {
      Connection[] tmp = new Connection[8+this.connectionCount];
      for (int i = 0; i < this.connectionCount; i++) {
        tmp[i] = this.getConnection(i);
      }
      for (int i = this.connectionCount; i < 8+this.connectionCount; i++) {
        tmp[i] = null;
      }
      this.connections = tmp;
    }
    this.setConnection(this.connectionCount, e);
    return this.incConnectionCount();
  }

  public int removeConnection(int index) {
    if (index >= 0 && index < this.connectionCount) {
      this.setConnection(index, null);
      for (int i = index; i < this.connectionCount - 1; i++) {
        this.setConnection(i, this.getConnection(i+1));
      }
      return this.decConnectionCount();
    }
    return -1;
  }

  public Force getForce() {
    return this.force;
  }

  public void applyForce(double coeff) {
    if (this.lock == false && this.grabbed == false) {
      this.setX(this.getX() + coeff*this.getForce().getX());
      this.setY(this.getY() + coeff*this.getForce().getY());
      this.setZ(this.getZ() + coeff*this.getForce().getZ());
      this.force.dampen(Constants.damp);
    } else {
      this.force.reset();
    }
  }

  public void calculateEdgeForce() {
    for (int i = 0; i < this.connectionCount; i++) {
      if (Constants.doLinear) {
        this.getForce().linear(this, this.getConnection(i));
      }
      if (Constants.doAngular) {
        for (int j = 0; j < this.connectionCount; j++) {
          if (i != j) {
            this.getForce().angular(this, this.getConnection(i).getPoint(),
              this.getConnection(j).getPoint());
          }
        }
      }
    }
  }

  public void jiggle() {
    this.setX(this.getX() + (2*Math.random()-1));
    this.setY(this.getY() + (2*Math.random()-1));
    this.setZ(this.getZ() + (2*Math.random()-1));
  }

  public int setHighlight(int h) {
    return this.highlight = h;
  }

  public int resetHighlight() {
    return this.highlight = 0;
  }

  public int getHighlight() {
    return this.highlight;
  }

  public int nextHighlight() {
    this.highlight++;
    this.highlight %= 3;
    if (this.highlight == 0) {
      this.highlight = 1;
    }
    return this.highlight;
  }

  public int previousHighlight() {
    this.highlight--;
    this.highlight %= 3;
    if (this.highlight == 0) {
      this.highlight = 1;
    }
    return this.highlight;
  }

  public double distance(Point3 p) {
    if (p != null) {
      return Math.sqrt(Math.pow(this.getX() - p.getX(), 2) +
        Math.pow(this.getY() - p.getY(), 2) +
        Math.pow(this.getZ() - p.getZ(), 2));
    }
    return 0;
  }

  public void lockToggle() {
    this.lock = !this.lock;
  }

  public void setLock(boolean b) {
    this.lock = b;
  }

  public boolean getLockToggle() {
    return this.lock;
  }

  public String toString() {
    return String.format("(%f %f %f)", this.getX(), this.getY(), this.getZ());
  }

  public void grabedToggle() {
    this.grabbed = !this.grabbed;
  }

  public void setGrabbed(boolean b) {
    this.grabbed = b;
  }

  public boolean getGrabbed() {
    return this.grabbed;
  }

  public void add(Vector3 v) {
    this.x += v.getX();
    this.y += v.getY();
    this.z += v.getZ();
  }

  public void sub(Vector3 v) {
    this.x -= v.getX();
    this.y -= v.getY();
    this.z -= v.getZ();
  }

  public void add(Point3 p) {
    this.add(new Vector3(p));
  }

  public void sub(Point3 p) {
    this.sub(new Vector3(p));
  }
}
