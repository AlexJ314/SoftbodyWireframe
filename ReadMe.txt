Compile with `clear; del *.class; javac Main.java` inside of `src`



Run with `java Main <path-to-file>`
  or `java Main` and then enter the path to a file to create or edit
  For example:
    `java Main obj/demo` will open the `demo` object in the `obj` folder



You may create a torus by running `largertorus.sh <r> <R>` where:
  `<r>` is the circumference of the minor radius
  `<R>` is the circumference of the major radius
  The torus will be formed of `<r> * <R>` quadrilaterals



Controls (`Screen.java` and `DisplayManager.mouseDragged(MouseEvent e).java`):
  Mouse left or right click:
    Set active point

  Left mouse drag:
    Rotate model

  Right mouse drag
    Pan model

  Scroll:
    Zoom

  `Control + Left mouse`:
    Drag first active point

  `Shift + Left mouse`:
    Create or resize selection box

  `Escape`: 
    Dismiss selection box

  `Control + R`:
    Reset zoom, pan, and rotation

  `Control + C`:
    Copy selected points and edges

  `Control + V`:
    Paste selected points and edges

  Add point (`Ctrl + A`):
    When toggled, add point at mouse click
    Note: Points with no edges will not be moved by the simulation

  Remove point (`Delete`):
    Remove first active point
    Remove selected point(s)

  Merge points (`Ctrl + M`):
    First and second active point become the same point
      Edges directly connecting the points are removed
    Selected points become the same point
      Edges directly connecting the points are removed

  Add edge (`Ctrl + E`):
    An edge is added between the first and second active point
      If an edge already exists, no edge is added
    If there is only one active point:
      An edge is added to that point, connected to a new, randomly placed point
    If there is no active point:
      An edge is added randomly about the origin with two new points

  Remove edge (`Backspace`):
    Remove edge directly connecting first and second active point
    Remove edge directly connecting selected points
    Points are not removed
    Note: Points with no edges will not be moved by the simulation

  Bisect (`Ctrl + B`):
    Bisect the edge directly connecting first and second active point
      A point and edge of the same length as the old edge are added such that:
        The old edge is now twice as long
        And the new point bisects this new edge
    If there is only one active point:
      Two edges and two points are added such that:
        An edge of default length connects to the active point and
          a new randomly placed point
        A second edge of default length connects to the new randomly placed
          point and a second randomly placed new point
    If there is no active point:
      Two edges and three points are added about the origin such that:
        An edge of default length connects two new randomly placed points
        A second edge of default length connects to the new randomly placed
          point and a third randomly placed new point
    Bisect any edges directly connecting selected points
      A point and edge of the same length as the old edge are added such that:
        The old edge is now twice as long
        And the new point bisects this new edge

  Add polygon (`Ctrl + G`):
    Adds a polygon with the desired number of edges and edge length
    If there is an active point, that point is used as a point in the polygon

  Edge count:
    Set how many edges new polygons should have

  Edge length:
    Set how long each edge should be in new polygons and new edges

  Lock x (`Ctrl + X`):
    When toggled, locks the display to only rotate about the x axis
    Unlocks the y and z axis

  Lock y (`Ctrl + Y`):
    When toggled, locks the display to only rotate about the y axis
    Unlocks the x and z axis

  Lock z (`Ctrl + Z`):
    When toggled, locks the display to only rotate about the z axis
    Unlocks the x and y axis

  Zoom in (Scroll up OR `Ctrl + =`):
    Zooms display in by 10%

  Zoom out (Scroll down OR `Ctrl + -`):
    Zooms display out by 9.0909%
    This is a multiplication of (1/1.1), which is the reciprocal of the zoom in

  <path-to-file>:
    The path to the file to be saved to

  Jiggle:
    Move all points by some random amount less than one
      Useful for getting some models unstuck

  Freeze (`Ctrl + F`):
    When toggled, prevents the simulation from moving points
    Note: Points with no edges will not be moved by the simulation

  Print (`Ctrl + P`):
    Saves the current model to <path-to-file>

  Repulse (`Ctrl + Space`):
    When toggled, all points repulse all other points exponentially
      Useful for shapes with concavities
      Slow!
      Note: Points with no edges will not be moved by the simulation

  Lock point (`Ctrl + L`):
    When toggled, sets the active point to not be moved by the simulation
    When toggled, sets selected points to not be moved by the simulation
    Note: Points with no edges will not be moved by the simulation

  Show stress:
    When toggled, change the simulation colouring such that:
      Edges under compression are coloured from black to blue
      Edges under tension are coloured from black to red



Constants (`Constants.java`):
  Initial window width:
    `frameWidth`

  Initial window height:
    `frameHeight`

  Initial display area width:
    `panelWidth`
    Note: This should match `frameWidth`

  Initial display area height:
    `panelHeight`
    Note: The control panel height is `frameHeight - panelHeight`

  Update rate in milliseconds:
    `delay`

  How many times larger to display lengths:
    `lineScale`

  How big to make points:
    `pointSize`

  How much to multiply force applied to points by:
    `forceCoeff`

  Percent of previous tick's force to add to current tick's force
    `damp`

  Spring coefficient of edges acting as a linear spring:
    `linearForce`
    Note: Points with no edges will not be moved by the simulation

  How much points directly connected by one or two edges repulse each other:
  `smallRepulsiveForce`
  Note: Points with no edges will not be moved by the simulation

  How much all points repulse each other:
    `fullRepulsiveForce`
    Note: Points with no edges will not be moved by the simulation

  Spring coefficient of two edges about a point acting as an angled spring:
    `angularForce`
    Note: Unused
    Note: Points with no edges will not be moved by the simulation

  Treat edges as linear springs:
    `doLinear`
    Note: Points with no edges will not be moved by the simulation

  Allow points directly connected by one or two edges to repulse each other:
    `doSmallRepulsive`
    Note: Points with no edges will not be moved by the simulation

  Allow all points to repulse each other:
    `doFullRepulsive`
    Note: Slow!
    Note: Points with no edges will not be moved by the simulation

  Treat edges as angular springs about a shared point:
    `doAngular`
    Note: Unused
    Note: Points with no edges will not be moved by the simulation

  Thread lock:
    `lock`
    Note: Internal
