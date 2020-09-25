/******************************************************************************
 *  Compilation:  javac PointSET.java
 *  Execution:    java PointSET filename.txt
 *  
 *  Initializes a set of 2-dimensional points in a cartesian plane bounded by
 *  [0, 0] to [1, 1].  Points are passed to PointSET as Point2D objects. The
 *  Point2D class (along with other dependencies of this class) can be found
 *  in Princeton University's algs4 package.
 * 
 *  The class implements the field of points using non-efficient "brute-force"
 *  methods.  It is a contrast to the more efficient KdTree class using a
 *  KdTree (a modified binary search tree) to achieve the same API.
 *
 * @author Jason Fricano
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

// SubmitWrite a data type to represent a set of points in the unit square (all points have x- and y-coordinates between 0 and 1) using a 2d-tree to support efficient range search (find all of the points contained in a query rectangle) and nearest-neighbor search (find a closest point to a query point). 
public class PointSET {
  private final SET<Point2D> set;

  /** 
   * constructs an empty set of points
   */ 
  public PointSET() {
    set = new SET<>();
  }

  /** 
   * is the set empty? 
   * @return is the set empty
  */
  public boolean isEmpty() {
    return set.isEmpty();
  }

  /**
   * number of points in the set
   * @return number of points in the set
   */
  public int size() {
    return set.size();
  }

  /**
   * adds a point to the set
   * @param p 2D point object to be added
   * @throws IllegalArgumentException if null argument is passed
   */
  public void insert(final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException();
    set.add(p);
  }

  /**
   * does the set contain point p?
   * @param p 2D-point object to be located
   * @return does the set contain point p?
   * @throws IllegalArgumentException if null argument is passed
   */
  public boolean contains(final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException();
    return set.contains(p);
  }

  /**
   * draws all the points and KdTree grid splits to StdDraw
   * uses @code {algs4.StdDraw} default canvas size
   */
  public void draw() {
    StdDraw.setPenRadius(0.01);
    for (final Point2D p : set) {
      StdDraw.point(p.x(), p.y());
    }
  }

  /**
   * All points that are inside the rectangle (or on the boundary)
   * @param rect rectangle defining the range to be searched
   * @return collection of point objects falling within the specified range
   * @throws IllegalArgumentException if null argument is passed
   */
  public Iterable<Point2D> range(final RectHV rect) {
    if (rect == null)
      throw new IllegalArgumentException();
    final ResizingArrayBag<Point2D> pointsInRect = new ResizingArrayBag<>();
    for (final Point2D p : set)
      if (rect.contains(p))
        pointsInRect.add(p);
    return pointsInRect;
  }

  /**
   * a nearest neighbor in the set to point p; null if the set is empty
   * @param p point object being queried for nearest
   * @return  point object nearest to the passed point object using Euclidean distance
   * @throws IllegalArgumentException if null argument is passed
  */
  public Point2D nearest(final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException();

    double minDist = Double.POSITIVE_INFINITY;
    Point2D nearest = null;

    for (final Point2D pCmp : set) {
      if (pCmp.equals(p))
        return pCmp;

      final double dist = pCmp.distanceSquaredTo(p);
      if (minDist == Double.POSITIVE_INFINITY || dist < minDist) {
        minDist = dist;
        nearest = pCmp;
      }
    }
    return nearest;
  }

  /**
   * unit testing
   * @param args the name of the file to be used for test input
   */
  public static void main(final String[] args) {
    String filename = args[0];
    In in = new In(filename);
    PointSET brute = new PointSET();

    // test empty set
    StdOut.println("size of set:\t" + brute.size());
    StdOut.println("empty set?:\t" + brute.isEmpty());
    StdOut.println();

    // fill the set
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      brute.insert(p);
    }

    // test filled set
    StdOut.println("Points added\n---------------------");
    int i = 0;
    for (Point2D point : brute.set)
      StdOut.println(++i + ". " + point.toString());

    StdOut.println();
    StdOut.println("size of set:\t" + brute.size());
    StdOut.println("empty set?:\t" + brute.isEmpty());
    StdOut.println("contains (false):\t" + brute.contains(new Point2D(0, 3)));
    StdOut.println("contains (true):\t" + brute.contains(new Point2D(0.5, 1)));
    StdOut.println();

    // test draw(), range()
    RectHV rect = new RectHV(0.25, 0.25, 0.8, 1.0);

    StdDraw.setCanvasSize(800, 800);
    StdDraw.setPenRadius(0.005);
    StdDraw.setScale(-.05, 1.05);
    brute.draw();
    rect.draw();

    StdOut.println("Range in rect:");
    for (Point2D point : brute.range(rect))
      StdOut.println(point.toString());

    // test nearest
    StdOut.println();
    
    Point2D test = new Point2D(0.77, 0.5);
    test.draw();
    StdOut.println("nearest:\t" + brute.nearest(test));
  }
}
