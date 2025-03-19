public class Edge {
  private double length;
  private Point3 pointA, pointB;

  public Edge(double length, Point3 pointA, Point3 pointB) {
    this.setLength(length);
    this.setPointA(pointA);
    this.setPointB(pointB);
  }

  public Edge(Point3 pointA, Point3 pointB) {
    this.setLength(1);
    this.setPointA(pointA);
    this.setPointB(pointB);
  }

  private double setLength(double length) {
    return this.length = length;
  }

  private void setPointA(Point3 point) {
    this.pointA = point;
  }

  private void setPointB(Point3 point) {
    this.pointB = point;
  }

  public Point3 getPointA() {
    return this.pointA;
  }

  public Point3 getPointB() {
    return this.pointB;
  }

  public double getLength() {
    return this.length;
  }
}
