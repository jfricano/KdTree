import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

// SubmitWrite a data type to represent a set of points in the unit square (all points have x- and y-coordinates between 0 and 1) using a 2d-tree to support efficient range search (find all of the points contained in a query rectangle) and nearest-neighbor search (find a closest point to a query point). 
public class PointSET {
  private SET<Point2D> set;

  // construct an empty set of points
  public PointSET() {
    set = new SET<>();
  }

  // is the set empty?
  public boolean isEmpty() {
    return set.isEmpty();
  }

  // number of points in the set
  public int size() {
    return set.size();
  }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D p) {
    set.add(p);
  }

  // does the set contain point p?
  public boolean contains(Point2D p) {
    return set.contains(p);
  }

  // draw all points to standard draw
  public void draw() {

  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    return null;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    return null;
  }

  // unit testing of the methods (optional)
  public static void main(String[] args) {
    String filename = args[0];
    In in = new In(filename);
    PointSET brute = new PointSET();

    StdOut.println("size of set:\t" + brute.size());
    StdOut.println("empty set?:\t" + brute.isEmpty());
    StdOut.println();

    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      // if (x == String.eof() )
      Point2D p = new Point2D(x, y);
      brute.insert(p);
    }

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
  }
}
