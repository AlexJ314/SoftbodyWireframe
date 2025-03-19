public class Face {
  private Point3[] points;
  private int pointCount = 0;

  public Face() {
    this.points = new Point3[64];
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
    return this.pointCount++;
  }

  public int removePoint(Point3 p) {
    if (p == null) {
      return -1;
    }
    for (int i = 0; i < this.pointCount; i++) {
      if (p == this.points[i]) {
        for (int j = i; j < this.pointCount-1; j++) {
          this.points[j] = this.points[j+1];
        }
        this.points[this.pointCount-1] = null;
        this.pointCount--;
        return i;
      }
    }
    return -1;
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

  public boolean equals(Face f) {
    if (f == null || f.getPointCount() != this.getPointCount()) {
      return false;
    }
    for (int i = 0; i < this.getPointCount(); i++) {
      if (!f.hasPoint(this.points[i])) {
        return false;
      }
    }
    return true;
  }

  public boolean hasPoint(Point3 p) {
    if (p != null) {
      for (int i = 0; i < this.getPointCount(); i++) {
        if (this.points[i] == p) {
          return true;
        }
      }
    }
    return false;
  }
}
