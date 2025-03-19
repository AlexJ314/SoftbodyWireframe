/** 
 * @file .java
 * @brief 
 * @author Alex Johnson
 */

public class Connection {
  private double length;
  private Point3 point;

  public Connection(double length, Point3 point) {
    this.setLength(length);
    this.setPoint(point);
  }

  public Connection(Point3 point) {
    this.setLength(1);
    this.setPoint(point);
  }

  private double setLength(double length) {
    return this.length = length;
  }

  private void setPoint(Point3 point) {
    this.point = point;
  }

  public Point3 getPoint() {
    return this.point;
  }

  public double getLength() {
    return this.length;
  }
}
