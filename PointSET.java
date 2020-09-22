// import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
// import edu.princeton.cs.algs4.StdOut;

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
    if (p == null) throw new IllegalArgumentException();
    set.add(p);
  }

  // does the set contain point p?
  public boolean contains(Point2D p) {
    if (p == null) throw new IllegalArgumentException();
    return set.contains(p);
  }

  // draw all points to standard draw
  public void draw() {
    for (Point2D p : set) {
      StdDraw.point(p.x(), p.y());
      // StdDraw.text(p.x(), p.y() + .001, p.toString());
    }
  }

  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) throw new IllegalArgumentException();
    ResizingArrayBag<Point2D> pointsInRect = new ResizingArrayBag<>();
    for (Point2D p : set)
      if (rect.contains(p))
        pointsInRect.add(p);
    return pointsInRect;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    if (p == null) throw new IllegalArgumentException();
    
    Double minDist = null;
    Point2D nearest = null;

    for (Point2D pCmp : set) {
      if (pCmp.equals(p)) return pCmp;

      double dist = pCmp.distanceTo(p);
      if (minDist == null || dist < minDist) {
        minDist = dist;
        nearest = pCmp;
      }
    }
    return nearest;
  }

  // unit testing of the methods (optional)
  public static void main(String[] args) {
    // String filename = args[0];
    // In in = new In(filename);
    // PointSET brute = new PointSET();

    // // test empty set
    // StdOut.println("size of set:\t" + brute.size());
    // StdOut.println("empty set?:\t" + brute.isEmpty());
    // StdOut.println();

    // // fill the set
    // while (!in.isEmpty()) {
    //   double x = in.readDouble();
    //   double y = in.readDouble();
    //   Point2D p = new Point2D(x, y);
    //   brute.insert(p);
    // }

    // // test filled set
    // StdOut.println("Points added\n---------------------");
    // int i = 0;
    // for (Point2D point : brute.set)
    //   StdOut.println(++i + ". " + point.toString());

    // StdOut.println();
    // StdOut.println("size of set:\t" + brute.size());
    // StdOut.println("empty set?:\t" + brute.isEmpty());
    // StdOut.println("contains (false):\t" + brute.contains(new Point2D(0, 3)));
    // StdOut.println("contains (true):\t" + brute.contains(new Point2D(0.5, 1)));
    // StdOut.println();

    // // test draw(), range()
    // RectHV rect = new RectHV(0.25, 0.25, 0.8, 1.0);

    // StdDraw.setCanvasSize(800, 800);
    // StdDraw.setPenRadius(0.005);
    // StdDraw.setScale(-.05, 1.05);
    // brute.draw();
    // rect.draw();

    // StdOut.println("Range in rect:");
    // for (Point2D point : brute.range(rect))
    //   StdOut.println(point.toString());

    // // test nearest
    // StdOut.println();
    // Point2D test = new Point2D(0.77, 0.5);
    // test.draw();
    // StdOut.println("nearest:\t" + brute.nearest(test));
  }
}
