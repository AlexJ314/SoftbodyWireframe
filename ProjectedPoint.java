public class ProjectedPoint implements Comparable<ProjectedPoint> {
  private Point3 point;
  private Vector3 vector;

  public ProjectedPoint(Point3 point) {
    this.point = point;
    this.vector = this.project(new Vector3(this.point));
  }

  public Point3 getPoint() {
    return this.point;
  }

  public Vector3 getProjection() {
    return this.vector;
  }

  public int getDepth() {
    return (int) Math.round(this.vector.getZ()*100);
  }

  public int compareTo(ProjectedPoint other) {
    return this.getDepth().compareTo(other.getDepth());
  }

  public Vector3 project(Vector v) {
    return new Vector3(v.dot(this.x), v.dot(this.y), v.dot(this.z));
  }
}
