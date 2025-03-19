/** 
 * @file Force.java
 * @brief Calculates the force between two points
 * @author Alex Johnson
 */

public class Force {
  private double x, y, z;
  private double K = Constants.linearForce;
  private double FR = Constants.fullRepulsiveForce;
  private double SR = Constants.smallRepulsiveForce;
  private double A = Constants.angularForce;

  public Force() {
    this.reset();
  }

  public Force(double x, double y, double z) {
    this.setX(x);
    this.setY(y);
    this.setZ(z);
  }

  public void reset() {
    this.setX(0);
    this.setY(0);
    this.setZ(0);
  }

  public void dampen(double c) {
    this.setX(this.getX()*c);
    this.setY(this.getY()*c);
    this.setZ(this.getZ()*c);
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

  public synchronized double setX(double f) {
    return this.x = f;
  }

  public synchronized double setY(double f) {
    return this.y = f;
  }

  public synchronized double setZ(double f) {
    return this.z = f;
  }

  public synchronized double addX(double f) {
    return this.x += f;
  }

  public synchronized double addY(double f) {
    return this.y += f;
  }

  public synchronized double addZ(double f) {
    return this.z += f;
  }

  public void linear(Point3 point, Connection connection) {
    Point3 q = connection.getPoint();

    double f = this.K*(connection.getLength()-point.distance(q));

    Vector3 force = new Vector3(point.getX() - q.getX(),
      point.getY() - q.getY(), point.getZ() - q.getZ()).norm();

    double fx = f*force.getX();
    double fy = f*force.getY();
    double fz = f*force.getZ();

    point.getForce().addX(fx);
    point.getForce().addY(fy);
    point.getForce().addZ(fz);
  }

  public void fullRepulsive(Point3 p, Point3 q) {
    double f = this.FR/(1+Math.pow(p.distance(q), 2));
    this.repulsive(p, q, f);
  }

  public void smallRepulsive(Point3 p, Point3 q) {
    double f = this.SR/p.distance(q);
    this.repulsive(p, q, f);
  }

  public void repulsive(Point3 p, Point3 q, double f) {
    if (p == null || q == null) {
      return;
    }
    if (p.getConnectionCount() < 1 || q.getConnectionCount() < 1 || p == q) {
      return;
    }
    Vector3 force = new Vector3(p.getX() - q.getX(),
      p.getY() - q.getY(), p.getZ() - q.getZ()).norm();

    double fx = f*force.getX();
    double fy = f*force.getY();
    double fz = f*force.getZ();

    p.getForce().addX(fx);
    p.getForce().addY(fy);
    p.getForce().addZ(fz);
    q.getForce().addX(-fx);
    q.getForce().addY(-fy);
    q.getForce().addZ(-fz);
  }

  public void angular(Point3 m, Point3 p, Point3 q) {
    if (p == m || p == q || q == m) {
      return;
    }
    Vector3 vm = new Vector3(m);
    Vector3 vp = new Vector3(p).sub(vm);
    Vector3 vq = new Vector3(q).sub(vm);
    double theta = vp.angle(vq);
    double f = 1-theta/Math.PI;
    double dp = m.distance(p);
    double dq = m.distance(q);
    double fp = this.A*f/dp;
    double fq = this.A*f/dq;

    Vector3 u = vp.ortho(vq).norm();

    double angle = 0.5;

    Vector3 prx = new Vector3(
      Math.pow(u.getX(), 2)*(1-Math.cos(angle))+Math.cos(angle),
      u.getX()*u.getY()*(1-Math.cos(angle))-u.getZ()*Math.sin(angle),
      u.getX()*u.getZ()*(1-Math.cos(angle))+u.getY()*Math.sin(angle)
      );
    Vector3 pry = new Vector3(
      u.getX()*u.getY()*(1-Math.cos(angle))+u.getZ()*Math.sin(angle),
      Math.pow(u.getY(), 2)*(1-Math.cos(angle))+Math.cos(angle),
      u.getY()*u.getZ()*(1-Math.cos(angle))-u.getX()*Math.sin(angle)
      );
    Vector3 prz = new Vector3(
      u.getX()*u.getZ()*(1-Math.cos(angle))-u.getY()*Math.sin(angle),
      u.getY()*u.getZ()*(1-Math.cos(angle))+u.getX()*Math.sin(angle),
      Math.pow(u.getZ(), 2)*(1-Math.cos(angle))+Math.cos(angle)
      );
    u = u.mul(-1);
    Vector3 qrx = new Vector3(
      Math.pow(u.getX(), 2)*(1-Math.cos(angle))+Math.cos(angle),
      u.getX()*u.getY()*(1-Math.cos(angle))-u.getZ()*Math.sin(angle),
      u.getX()*u.getZ()*(1-Math.cos(angle))+u.getY()*Math.sin(angle)
      );
    Vector3 qry = new Vector3(
      u.getX()*u.getY()*(1-Math.cos(angle))+u.getZ()*Math.sin(angle),
      Math.pow(u.getY(), 2)*(1-Math.cos(angle))+Math.cos(angle),
      u.getY()*u.getZ()*(1-Math.cos(angle))-u.getX()*Math.sin(angle)
      );
    Vector3 qrz = new Vector3(
      u.getX()*u.getZ()*(1-Math.cos(angle))-u.getY()*Math.sin(angle),
      u.getY()*u.getZ()*(1-Math.cos(angle))+u.getX()*Math.sin(angle),
      Math.pow(u.getZ(), 2)*(1-Math.cos(angle))+Math.cos(angle)
      );

    q.getForce().addX(-fp*vp.dot(prx));
    q.getForce().addY(-fp*vp.dot(pry));
    q.getForce().addZ(-fp*vp.dot(prz));
    p.getForce().addX(-fq*vq.dot(qrx));
    p.getForce().addY(-fq*vq.dot(qry));
    p.getForce().addZ(-fq*vq.dot(qrz));
    //m.getForce().addX(fp*vp.dot(prx)+fq*vq.dot(qrx));
    //m.getForce().addY(fp*vp.dot(pry)+fq*vq.dot(qry));
    //m.getForce().addZ(fp*vp.dot(prz)+fq*vq.dot(qrz));
  }
}
