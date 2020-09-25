import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL = true;
  // private static final boolean HORIZONTAL = false;
  // private static final RectHV RANGE = new RectHV(0, 0, 1, 1);

  private Node root;

  private class Node {
    private final Point2D point; // sorted by key
    private final boolean splitOrientation; // associated data
    private Node lb, rt; // left and right subtrees
    private int size; // number of nodes in subtree

    public Node(final Point2D p, final boolean split, final int sz) {
      point = p;
      splitOrientation = split;
      lb = null;
      rt = null;
      size = sz;
    }
  }

  // construct an empty set of points
  public KdTree() {

  }

  // **************************** IS_EMPTY ****************************
  // is the set empty?
  public boolean isEmpty() {
    return root == null;
  }

  // **************************** SIZE ****************************
  // number of points in the set
  public int size() {
    return size(root);
  }

  private int size(final Node nd) {
    return nd == null ? 0 : nd.size;
  }

  // **************************** INSERT ****************************
  // add the point to the set (if it is not already in the set)
  public void insert(final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException("calls insert() with a null key");
    root = insert(root, p, VERTICAL);
  }

  private Node insert(final Node nd, final Point2D p, final boolean isVertical) {
    if (nd == null)         return new Node(p, isVertical, 1);
    if (p.equals(nd.point)) return nd;
    final double cmp = comparePoints(p, nd);
    // what about equals to zero??
    if (cmp < 0) nd.lb = insert(nd.lb, p, !isVertical);
    else         nd.rt = insert(nd.rt, p, !isVertical);
    nd.size = 1 + size(nd.lb) + size(nd.rt);
    return nd;
  }

  // **************************** COMPARE ****************************
  private static double comparePoints(final Point2D p, final Node cmp) {
    if (cmp.splitOrientation == VERTICAL) return p.y() - cmp.point.y();
    else                                  return p.x() - cmp.point.x();
  }

  // **************************** CONTAINS ****************************
  // does the set contain point p?
  public boolean contains(final Point2D p) {
    return contains(root, p);
  }

  private boolean contains(final Node nd, final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException("argument to contains() is null");

    if (nd == null)         return false;
    if (p.equals(nd.point)) return true;

    final double cmp = comparePoints(p, nd);
    // what about equals zero??
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
  }

  // **************************** DRAW ****************************
  public void draw() {
    final ResizingArrayStack<Double> xLimits = new ResizingArrayStack<Double>();
    final ResizingArrayStack<Double> yLimits = new ResizingArrayStack<Double>();

    // StdDraw.setCanvasSize(800, 800);
    StdDraw.setScale(0, 1);
    draw(root, xLimits, yLimits);
  }

  private void draw(final Node nd, final ResizingArrayStack<Double> xLimits, final ResizingArrayStack<Double> yLimits) {
    if (nd == null)
      return;
    final double x = nd.point.x();
    final double y = nd.point.y();
    double[] stopLimits;

    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor();
    StdDraw.point(x, y);

    StdDraw.setPenRadius();
    if (nd.splitOrientation == VERTICAL) {
      stopLimits = getLimits(xLimits, x);
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(stopLimits[0], y, stopLimits[1], y);
      yLimits.push(y);
    } else {
      stopLimits = getLimits(yLimits, y);
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(x, stopLimits[0], x, stopLimits[1]);
      xLimits.push(x);
    }

    draw(nd.lb, xLimits, yLimits);
    rmLimits(nd, nd.lb, xLimits, yLimits);

    draw(nd.rt, xLimits, yLimits);
    rmLimits(nd, nd.rt, xLimits, yLimits);
  }

  private void rmLimits(final Node nd, final Node child, final ResizingArrayStack<Double> xLimits, final ResizingArrayStack<Double> yLimits) {
    if (nd.splitOrientation == VERTICAL && child != null)
      xLimits.pop();
    else if (child != null)
      yLimits.pop();
  }

  private double[] getLimits(final ResizingArrayStack<Double> limits, final double coord) {
    final double[] minMax = new double[2];
    minMax[0] = 0;
    minMax[1] = 1;
    for (final double lim : limits) {
      if (lim > minMax[0] && lim < coord) minMax[0] = lim;
      if (lim < minMax[1] && lim > coord) minMax[1] = lim;
    }
    return minMax;
  }

  // **************************** GETTER (for testing)
  // ****************************
  // private Iterable<Node> levelOrder() {
  //   final ResizingArrayQueue<Node> points = new ResizingArrayQueue<Node>();
  //   final ResizingArrayQueue<Node> q = new ResizingArrayQueue<Node>();
  //   q.enqueue(root);
  //   while (!q.isEmpty()) {
  //     final Node nd = q.dequeue();
  //     if (nd == null)
  //       continue;
  //     points.enqueue(nd);
  //     q.enqueue(nd.lb);
  //     q.enqueue(nd.rt);
  //   }
  //   return points;
  // }

  // **************************** RANGE ****************************
  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(final RectHV rect) {
    // if root, then can continue to look for more
    // if root is left of rectangle, only have to look to left
    // when intersects spliting line, have to check both subtrees
    // check first half, if no, then search next subtree
    final ResizingArrayBag<Point2D> pointsInRange = new ResizingArrayBag<>();
    range(rect, root, pointsInRange);
    return pointsInRange;
  }

  private void range(final RectHV rect, final Node nd, final ResizingArrayBag<Point2D> pointsInRange) {
    if (nd == null)
      return;
    final double distThis = rect.distanceSquaredTo(nd.point);
    final double distLft = nd.lb == null ? Double.POSITIVE_INFINITY : rect.distanceSquaredTo(nd.lb.point);
    final double distRt = nd.rt == null ? Double.POSITIVE_INFINITY : rect.distanceSquaredTo(nd.rt.point);

    if (rect.contains(nd.point)) {
      // add the point
      pointsInRange.add(nd.point);
      // check left
      if (nd.lb != null)
        range(rect, nd.lb, pointsInRange);
      // check right
      if (nd.rt != null)
        range(rect, nd.rt, pointsInRange);
    }

    // MAYBE NEED TO USE LESS OR EQUALS??
    else {
      if (distLft <= distThis)
        range(rect, nd.lb, pointsInRange);
      if (distRt <= distThis)
        range(rect, nd.rt, pointsInRange);
    }
  }

  // **************************** NEAREST ****************************
  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(final Point2D p) {
    return nearest(p, root, root.point, p.distanceSquaredTo(root.point), null);
  }

  private Point2D nearest(final Point2D queryPoint, final Node nd, final Point2D currentNearest,
      final double currentMin, final Node parent) {
    if (nd == null)
      return currentNearest;
    if (queryPoint.equals(nd.point))
      return nd.point;

    // Point2D closestOnLine;
    Point2D nearest = currentNearest;
    double min = currentMin;
    final double queryDist = queryPoint.distanceSquaredTo(nd.point);

    if (queryDist < currentMin) {
      nearest = nd.point;
      min = queryDist;
    }

    if (comparePoints(queryPoint, nd) < 0) {
      nearest = nearest(queryPoint, nd.lb, nearest, min, nd);
      min = queryPoint.distanceSquaredTo(nearest);
      nearest = nearest(queryPoint, nd.rt, nearest, min, nd, parent);
    } else {
      nearest = nearest(queryPoint, nd.rt, nearest, min, nd);
      min = queryPoint.distanceSquaredTo(nearest);
      nearest = nearest(queryPoint, nd.lb, nearest, min, nd, parent);
    }

    return nearest;
  }

  private Point2D nearest(final Point2D queryPoint, final Node child, final Point2D currentNearest,
      final double currentMin, final Node nd, final Node parent) {
    Point2D closestOnLine;
    final double limitingCoord = getLimitingCoord(queryPoint, nd, parent);
    Point2D nearest = currentNearest;

    if (nd.splitOrientation == VERTICAL) {
      closestOnLine = new Point2D(limitingCoord, nd.point.y());
      if (queryPoint.distanceSquaredTo(closestOnLine) < currentMin)
        nearest = nearest(queryPoint, child, nearest, currentMin, nd);
    } else {
      closestOnLine = new Point2D(nd.point.x(), limitingCoord);
      if (queryPoint.distanceSquaredTo(closestOnLine) < currentMin)
        nearest = nearest(queryPoint, child, nearest, currentMin, nd);
    }

    return nearest;
  }

  private double getLimitingCoord(final Point2D queryPoint, final Node nd, final Node parent) {
    if (nd.splitOrientation == VERTICAL) {
      if (parent == null)
        return queryPoint.x();
      else if (comparePoints(nd.point, parent) < 0)
        return Math.min(queryPoint.x(), parent.point.x());
      else
        return Math.max(queryPoint.x(), parent.point.x());
    } else {
      if (parent == null)
        return queryPoint.y();
      else if (comparePoints(nd.point, parent) < 0)
        return Math.min(queryPoint.y(), parent.point.y());
      else
        return Math.max(queryPoint.y(), parent.point.y());
    }
  }

  // **************************** MAIN ****************************
  // unit testing of the methods (optional)
  public static void main(final String[] args) {
    String filename = args[0];
    In in = new In(filename);
    KdTree kdTree = new KdTree();

    // test empty set
    StdOut.println("size of set:\t" + kdTree.size());
    StdOut.println("empty set?:\t" + kdTree.isEmpty());
    StdOut.println();

    // fill the set
    while (!in.isEmpty()) {
      double x = in.readDouble();
      double y = in.readDouble();
      Point2D p = new Point2D(x, y);
      kdTree.insert(p);
    }

    StdOut.println();

    // test filled set
    // StdOut.println("Points added\n---------------------");
    // RectHV RANGE = new RectHV(0, 0, 1, 1);
    // int i = 0;
    // for (Node nd : kdTree.levelOrder()) {
    //   Node leftChild = nd.lb;
    //   Node rtChild = nd.rt;
    //   StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation == VERTICAL ? "--" : "|"));
    //   StdOut.println("Children: " + (leftChild == null ? "none" : leftChild.point.toString()) + "\t"
    //   + (rtChild == null ? "none" : rtChild.point.toString()));
    //   StdOut.println("Size: " + nd.size);
    //   StdOut.println();
    // }

    StdOut.println("Points added\n---------------------");
    RectHV RANGE = new RectHV(0, 0, 1, 1);
    int i = 0;
    for (Point2D p : kdTree.range(RANGE)) StdOut.println(++i + ". " + p.toString());

    StdOut.println();

    // test draw()
    kdTree.draw();

    // test nearest()
    StdOut.println();
    Point2D test = new Point2D(0.1, 0.3);
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.ORANGE);
    test.draw();
    StdOut.println("nearest:\t" + kdTree.nearest(test));

    // test range()
    RectHV rect = new RectHV(0.25, 0.25, 0.6, 0.8);
    

    StdDraw.setPenRadius(0.005);
    StdDraw.setPenColor(StdDraw.ORANGE);
    rect.draw();

    StdOut.println("Range in rect:");
    for (Point2D point : kdTree.range(rect))
      StdOut.println(point.toString());
  }
}
