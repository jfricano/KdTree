import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL = true;
  private static final boolean HORIZONTAL = false;
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
    root = insert(root, p, HORIZONTAL);
  }

  private Node insert(final Node nd, final Point2D p, final boolean isVertical) {
    if (nd == null)         return new Node(p, isVertical, 1);
    if (p.equals(nd.point)) return nd;
    
    final double cmp = comparePoints(p, nd);
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
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
  }

  // **************************** DRAW ****************************
  public void draw() {
    final ResizingArrayStack<Double> xLimits = new ResizingArrayStack<Double>();
    final ResizingArrayStack<Double> yLimits = new ResizingArrayStack<Double>();

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

  // ********************* GETTER (for testing) *********************
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
    range(rect, root, pointsInRange, 0, 0, 1, 1);
    return pointsInRange;
  }

  private void range(final RectHV rect, 
                     final Node nd, 
                     final ResizingArrayBag<Point2D> pointsInRange,
                     final double xMin, final double yMin, final double xMax, final double yMax) {
    if (nd == null)
      return;
    if (rect.contains(nd.point))
      pointsInRange.add(nd.point);

    RectHV lbRect, rtRect;

    // create a lb rectangle
    // create a rt rectanble
    if (nd.splitOrientation == VERTICAL) {
      lbRect = new RectHV(xMin, yMin, xMax, nd.point.y());
      rtRect = new RectHV(xMin, nd.point.y(), xMax, yMax);
    } else {
      lbRect = new RectHV(xMin, yMin, nd.point.x(), yMax);
      rtRect = new RectHV(nd.point.x(), yMin, xMax, yMax);
    }

    // if the rect intersects lb, then search lb
    // if the rect intersects rt, then search rt
    if (rect.intersects(lbRect))
      range(rect, nd.lb, pointsInRange, lbRect.xmin(), lbRect.ymin(), lbRect.xmax(), lbRect.ymax());
    if (rect.intersects(rtRect))
      range(rect, nd.rt, pointsInRange, rtRect.xmin(), rtRect.ymin(), rtRect.xmax(), rtRect.ymax());
  }

  // **************************** NEAREST ****************************
  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(final Point2D p) {
    return nearest(p, root, root.point, 0, 0, 1, 1);
  }

  private Point2D nearest(final Point2D queryPoint, final Node nd, final Point2D currentNearest, final double xMin,
      final double yMin, final double xMax, final double yMax) {
    if (nd == null)
      return currentNearest;
    if (queryPoint.equals(nd.point))
      return nd.point;

    RectHV lbRect, rtRect;
    Point2D nearest = currentNearest;
    double minDist = queryPoint.distanceSquaredTo(nearest);
    final double thisDist = queryPoint.distanceSquaredTo(nd.point);

    if (thisDist < minDist) {
      nearest = nd.point;
      minDist = thisDist;
    }

    if (nd.splitOrientation == VERTICAL) {
      lbRect = new RectHV(xMin, yMin, xMax, nd.point.y());
      rtRect = new RectHV(xMin, nd.point.y(), xMax, yMax);
    } else {
      lbRect = new RectHV(xMin, yMin, nd.point.x(), yMax);
      rtRect = new RectHV(nd.point.x(), yMin, xMax, yMax);
    }

    // if queryPoint is left of node, go left
    // then go right iff the right rect is closer than current min
    if (comparePoints(queryPoint, nd) < 0) {
      nearest = nearest(queryPoint, nd.lb, nearest, lbRect.xmin(), lbRect.ymin(), lbRect.xmax(), lbRect.ymax());
      minDist = queryPoint.distanceSquaredTo(nearest);

      if (rtRect.distanceSquaredTo(queryPoint) < minDist) {
        nearest = nearest(queryPoint, nd.rt, nearest, rtRect.xmin(), rtRect.ymin(), rtRect.xmax(), rtRect.ymax());
      }
      // else if queryPoint is right of node, go right
      // then go left iff the left rect is closer than the current min
    } else {
      nearest = nearest(queryPoint, nd.rt, nearest, rtRect.xmin(), rtRect.ymin(), rtRect.xmax(), rtRect.ymax());
      minDist = queryPoint.distanceSquaredTo(nearest);

      if (lbRect.distanceSquaredTo(queryPoint) < minDist) {
        nearest = nearest(queryPoint, nd.lb, nearest, lbRect.xmin(), lbRect.ymin(), lbRect.xmax(), lbRect.ymax());
      }
    }
    return nearest;
  }

  // **************************** MAIN ****************************
  // unit testing of the methods (optional)
  public static void main(final String[] args) {
    final String filename = args[0];
    final In in = new In(filename);
    final KdTree kdTree = new KdTree();

    // test empty set
    StdOut.println("size of set:\t" + kdTree.size());
    StdOut.println("empty set?:\t" + kdTree.isEmpty());
    StdOut.println();

    // fill the set
    while (!in.isEmpty()) {
      final double x = in.readDouble();
      final double y = in.readDouble();
      final Point2D p = new Point2D(x, y);
      kdTree.insert(p);
    }

    StdOut.println();

    // test filled set
    // StdOut.println("Points added\n---------------------");
    // RectHV RANGE = new RectHV(0, 0, 1, 1);
    // int i = 0;
    // for (Node nd : kdTree.levelOrder()) {
    // Node leftChild = nd.lb;
    // Node rtChild = nd.rt;
    // StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation
    // == VERTICAL ? "--" : "|"));
    // StdOut.println("Children: " + (leftChild == null ? "none" :
    // leftChild.point.toString()) + "\t"
    // + (rtChild == null ? "none" : rtChild.point.toString()));
    // StdOut.println("Size: " + nd.size);
    // StdOut.println();
    // }

    StdOut.println("Points added\n---------------------");
    final RectHV RANGE = new RectHV(0, 0, 1, 1);
    int i = 0;
    for (final Point2D p : kdTree.range(RANGE))
      StdOut.println(++i + ". " + p.toString());

    StdOut.println();

    // test draw()
    kdTree.draw();

    // test nearest()
    StdOut.println();
    final Point2D test = new Point2D(0.1, 0.3);
    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor(StdDraw.ORANGE);
    test.draw();
    StdOut.println("nearest:\t" + kdTree.nearest(test));

    // test range()
    final RectHV rect = new RectHV(0.25, 0.25, 0.6, 0.8);

    StdDraw.setPenRadius(0.005);
    StdDraw.setPenColor(StdDraw.ORANGE);
    rect.draw();

    StdOut.println("Range in rect:");
    for (final Point2D point : kdTree.range(rect))
      StdOut.println(point.toString());
  }
}
