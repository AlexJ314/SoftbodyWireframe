/** 
  * @file Connection.java
  * @brief Connects to another Point3 point by a length,
  * similarly to a singly linked list
  * @author Alex Johnson
  */

/**
  * @class Connection
  * @brief Attribute of a Point3 point connecting it by a length
  * to another Point3 point
  */
public class Connection {

  /// @brief double length between two Point3 points
  private double length;

  /// @brief Point3 point to be connected to
  private Point3 point;


  /** @brief Constructor to connect to another Point3 point
    * @param double length of connection between points
    * @param Point3 point to connect to
    */
  public Connection(double length, Point3 point) {
    this.setLength(length);
    this.setPoint(point);
  }

  /** @brief Constructor to connect to another Point3 point with default length
    * @param Point3 point to connect to
    */
  public Connection(Point3 point) {
    this.setLength(Constants.defaultLength);
    this.setPoint(point);
  }


  /** @brief Sets or changes the length of the connection
    * @param double length of connection
    * @return double length of connection
    */
  private double setLength(double length) {
    return this.length = length;
  }

  /** @brief Sets or changes the connected Point3 point
    * @param Point3 point to connect
    * @return Point3 point to connect
    */
  private Point3 setPoint(Point3 point) {
    return this.point = point;
  }

  /** @brief Gets connection length
    * @return double length of connection
    */
  public double getLength() {
    return this.length;
  }

  /** @brief Gets connected Point3 point
    * @return Point3 connected point
    */
  public Point3 getPoint() {
    return this.point;
  }
}
