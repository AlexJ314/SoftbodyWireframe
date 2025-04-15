/** 
 * @file Constants.java
 * @brief Defines editable constants for the program
 * @author Alex Johnson
 */


/** @Class Constants
  * @brief Defines constants for the program
  */
public class Constants {

  /// @param int frameWidth integer width of the main window
  public static final int frameWidth = 1000;

  /// @param int frameHeight integer height of the main window
  public static final int frameHeight = 600;

  /// @param int panelWidth integer width of the simulation panel
  public static final int panelWidth = 1000;

  /** @param int panelHeight integer height of the simulation panel;
    * frameHeight - panelHeight defines the height of the control panel
    */
  public static final int panelHeight = 500;

  /// @param int delay integer millisecond delay between update ticks
  public static final int delay = 3;

  /// @param int lineScale integer multiplier to scale up displayed line width
  public static final int lineScale = 20;

  /// @param int pointSize integer size of displayed point
  public static final int pointSize = 4;

  /// @param double forceCoeff decimal coefficient in force equation
  public static final double forceCoeff = 0.05;

  /** @param double damp decimal force dampening coefficient between update
    * ticks; specifies how much of the previous tick's force is carried over
    * to the next tick
    */
  public static final double damp = 0.1;

  /// @param double linearForce decimal coefficient in linear force equation
  public static final double linearForce = 4;

  /** @param double smallRepulsiveForce decimal coefficient in
    * local repulsive force equation
    */
  public static final double smallRepulsiveForce = 1;

  /** @param double fullRepulsiveForce decimal coefficient in
    * global repulsive force equation
    */
  public static final double fullRepulsiveForce = 2;

  /// @param double angularForce decimal coefficient in angular force equation
  public static final double angularForce = 1;

  /// @param boolean doLinear do linear force calculation
  public static final boolean doLinear = true;

  /// @param boolean doSmallRepulsive do local repulsive force calculation
  public static final boolean doSmallRepulsive = true;

  /// @param boolean doFullRepulsive do global repulsive force calculation
  public static final boolean doFullRepulsive = false;

  /// @param boolean doAngular do angular force calculation
  public static final boolean doAngular = false;

  /// @param Object lock thread concurrency lock control
  public static final Object lock = new Object();

  /// @param double defaultLength decimal default length of edges or connections
  public static final double defaultLength = 1.0;
}
