/** 
 * @file Vector3.java
 * @brief A vector in 3d space
 * @author Alex Johnson
 */

public class Vector3 {
  private double x, y, z, magnitude;

  public Vector3(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.recalcMagnitude();
  }

  public Vector3(Point3 p) {
    this.x = p.getX();
    this.y = p.getY();
    this.z = p.getZ();
    this.recalcMagnitude();
  }

  public Vector3(Vector3 v) {
    this.x = v.getX();
    this.y = v.getY();
    this.z = v.getZ();
    this.recalcMagnitude();
  }

  private void recalcMagnitude() {
    this.magnitude = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2) + Math.pow(this.z, 2));
  }

  public Vector3 proj(Vector3 v) {
    if (this.getX() == 0 && this.getY() == 0 && this.getZ() == 0) {
      return new Vector3(0, 0, 0);
    }
    //return this.mul(this.dot(v)/this.dot(this));
    return this.mul(this.dot(v)/Math.pow(this.dot(this), 2));
  }

  public double dot(Vector3 v) {
    return this.getX()*v.getX()+this.getY()*v.getY()+this.getZ()*v.getZ();
  }

  public double dot(Point3 p) {
    return this.getX()*p.getX()+this.getY()*p.getY()+this.getZ()*p.getZ();
  }

  public Vector3 mul(double a) {
    return new Vector3(a*this.getX(), a*this.getY(), a*this.getZ());
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

  public void setX(double x) {
    this.x = x;
    this.recalcMagnitude();
  }

  public void setY(double y) {
    this.y = y;
    this.recalcMagnitude();
  }

  public void setZ(double z) {
    this.z = z;
    this.recalcMagnitude();
  }

  public double getMagnitude() {
    return this.magnitude;
  }

  public Vector3 norm() {
    if (this.magnitude != 0) {
      return this.mul(1/this.getMagnitude());
    }
    return new Vector3(0, 0, 0);
  }

  public Vector3 sub(Vector3 v) {
    return new Vector3(this.getX()-v.getX(), this.getY()-v.getY(), this.getZ()-v.getZ());
  }

  public Vector3 add(Vector3 v) {
    return new Vector3(this.getX()+v.getX(), this.getY()+v.getY(), this.getZ()+v.getZ());
  }

  public Vector3 ortho(Vector3 vb) {
    Vector3 va = new Vector3(this);
    Vector3 O = new Vector3(1, 1, 1);
    if (va.getX() != 0) {  // + ? ?, ? ? ?
      va = va.mul(1/va.getX());  // 1 ? ?, ? ? ?
        vb = vb.sub(va.mul(vb.getX()));  // 1 ? ?, 0 ? ?
        if (vb.getY() != 0) {  // 1 ? ?, 0 + ?
          vb = vb.mul(1/vb.getY());  // 1 ? ?, 0 1 ?
          double z = 1;
          double y = -z*vb.getZ();
          double x = -(y*va.getY() + z*va.getZ());
          O = new Vector3(x, y, z);
        } else {  // 1 ? ?, 0 0 ?
          double z = 0;
          if (vb.getZ() == 0) {
            z = 1;
          }
          double y = 1;
          double x = -(y*va.getY() + z*va.getZ());
          O = new Vector3(x, y, z);
        }
    } else {  // 0 ? ?, ? ? ?
      if (vb.getX() != 0) {  // 0 ? ?, + ? ?
        vb = vb.mul(1/vb.getX());  // 0 ? ?, 1 ? ?
        if (va.getY() != 0) {  // 0 + ?, 1 ? ?
          va = va.mul(1/va.getY());  // 0 1 ?, 1 ? ?
          double z = 1;
          double y = -z*va.getZ();
          double x = -(y*vb.getY() + z*vb.getZ());
          O = new Vector3(x, y, z);
        } else { // 0 0 ?, 1 ? ?
          double z = 0;
          if (va.getZ() == 0) {
            z = 1;
          }
          double y = 1;
          double x = -(y*vb.getY() + z*vb.getZ());
          O = new Vector3(x, y, z);
        }
      } else {  // 0 ? ?, 0 ? ?
        if (va.getY() != 0) {  // 0 + ?, 0 ? ?
          va = va.mul(1/va.getY());  // 0 1 ?, 0 ? ?
          vb = vb.sub(va.mul(vb.getY()));  // 0 1 ?, 0 0 ?
          double z = 0;
          if (vb.getZ() == 0) {
            z = 1;
          }
          double y = -z*va.getZ();
          double x = 1;
          O = new Vector3(x, y, z);
        } else {  // 0 0 ?, 0 ? ?
          if (vb.getY() != 0) {  // 0 0 ?, 0 + ?
            vb = vb.mul(1/vb.getY());  // 0 0 ?, 0 1 ?
            double z = 0;
            if (va.getZ() == 0) {
              z = 1;
            }
            double y = -z*vb.getZ();
            double x = 1;
            O = new Vector3(x, y, z);
          } else {  // 0 0 ?, 0 0 ?
            double z = 0;
            if (va.getZ() == 0 && vb.getZ() == 0) {
              z = 1;
            }
            double y = 1;
            double x = 1;
            O = new Vector3(x, y, z);
          }
        }
      }
    }
    return O;
  }
  
  public double angle(Vector3 v, Point3 p) {
    return Math.acos((
      (this.getX()-p.getX())*(v.getX()-p.getX())+
      (this.getY()-p.getY())*(v.getY()-p.getY())+
      (this.getZ()-p.getZ())*(v.getZ()-p.getZ()))/(
      Math.sqrt(Math.pow(this.getX()-p.getX(), 2) +
      Math.pow(this.getY()-p.getY(), 2) +
      Math.pow(this.getZ()-p.getZ(), 2))*
      Math.sqrt(Math.pow(v.getX()-p.getX(), 2) +
      Math.pow(v.getY()-p.getY(), 2) +
      Math.pow(v.getZ()-p.getZ(), 2))));
  }

  public double angle(Vector3 v) {
    return Math.acos((
      this.getX()*v.getX()+
      this.getY()*v.getY()+
      this.getZ()*v.getZ())/(
      Math.sqrt(Math.pow(this.getX(), 2) +
      Math.pow(this.getY(), 2) +
      Math.pow(this.getZ(), 2))*
      Math.sqrt(Math.pow(v.getX(), 2) +
      Math.pow(v.getY(), 2) +
      Math.pow(v.getZ(), 2))));
  }

  public boolean equals(Vector3 v) {
    if (v == null) {
      return false;
    }
    return this.getX() == v.getX() && this.getY() == v.getY() &&
      this.getZ() == v.getZ();
  }

  public String toString() {
    return String.format("(%f %f %f [%f])", this.getX(), this.getY(), this.getZ(), this.getMagnitude());
  }
}
