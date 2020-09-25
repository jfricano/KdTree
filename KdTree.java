import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL = true;
  private static final boolean HORIZONTAL = false;
  private static final RectHV RANGE = new RectHV(0, 0, 1, 1);

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
    if (p == null) 
      throw new IllegalArgumentException("calls contains() with null argument");
    return contains(root, p);
  }

  private boolean contains(final Node nd, final Point2D p) {
    if (nd == null)         return false;
    if (p.equals(nd.point)) return true;

    final double cmp = comparePoints(p, nd);
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
  }

  // **************************** DRAW ****************************
  public void draw() {
    StdDraw.setScale(0, 1);
    draw(root, RANGE);
  }

  private void draw(final Node nd, final RectHV outer) {
    if (nd == null) return;
    
    RectHV lbRect, rtRect;
    final double x = nd.point.x();
    final double y = nd.point.y();

    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor();
    StdDraw.point(x, y);

    StdDraw.setPenRadius();

    if (nd.splitOrientation == VERTICAL) {
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(outer.xmin(), y, outer.xmax(), y);
      lbRect = new RectHV(outer.xmin(), outer.ymin(), outer.xmax(), y);
      rtRect = new RectHV(outer.xmin(), y, outer.xmax(), outer.ymax());
    } else {
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(x, outer.ymin(), x, outer.ymax());
      lbRect = new RectHV(outer.xmin(), outer.ymin(), x, outer.ymax());
      rtRect = new RectHV(x, outer.ymin(), outer.xmax(), outer.ymax());
    }
    draw(nd.lb, lbRect);
    draw(nd.rt, rtRect);
  }

  // **************************** RANGE ****************************
  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(final RectHV rect) {
    // if root, then can continue to look for more
    // if root is left of rectangle, only have to look to left
    // when intersects spliting line, have to check both subtrees
    // check first half, if no, then search next subtree
    if (rect == null) 
      throw new IllegalArgumentException("calls range() with null argument");
      
    final ResizingArrayBag<Point2D> pointsInRange = new ResizingArrayBag<>();
    range(rect, root, pointsInRange, RANGE);
    return pointsInRange;
  }

  private void range(final RectHV rect, final Node nd, 
                     final ResizingArrayBag<Point2D> pointsInRange,
                     final RectHV outer) {

    if (nd == null) return;
    
    RectHV lbRect, rtRect;
    
    if (rect.contains(nd.point)) pointsInRange.add(nd.point);

    // create a lb rectangle
    // create a rt rectanble
    if (nd.splitOrientation == VERTICAL) {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), outer.xmax(), nd.point.y());
      rtRect = new RectHV(outer.xmin(), nd.point.y(), outer.xmax(), outer.ymax());
    } else {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), nd.point.x(), outer.ymax());
      rtRect = new RectHV(nd.point.x(), outer.ymin(), outer.xmax(), outer.ymax());
    }

    // if the rect intersects lb, then search lb
    // if the rect intersects rt, then search rt
    if (rect.intersects(lbRect))
      range(rect, nd.lb, pointsInRange, lbRect);
    if (rect.intersects(rtRect))
      range(rect, nd.rt, pointsInRange, rtRect);
  }

  // **************************** NEAREST ****************************
  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(final Point2D p) {
    if (p == null)   
      throw new IllegalArgumentException("calls nearest() with null argument");
    if (size() == 0) return null;
    return nearest(p, root, root.point, RANGE);
  }

  private Point2D nearest(final Point2D queryPoint, final Node nd, 
                          final Point2D currentNearest, 
                          final RectHV outer) {
    if (nd == null)                  return currentNearest;
    if (queryPoint.equals(nd.point)) return nd.point;

    RectHV lbRect, rtRect;
    Point2D nearest = currentNearest;
    double minDist = queryPoint.distanceSquaredTo(nearest);
    final double thisDist = queryPoint.distanceSquaredTo(nd.point);

    if (thisDist < minDist) {
      nearest = nd.point;
      minDist = thisDist;
    }

    if (nd.splitOrientation == VERTICAL) {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), outer.xmax(), nd.point.y());
      rtRect = new RectHV(outer.xmin(), nd.point.y(), outer.xmax(), outer.ymax());
    } else {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), nd.point.x(), outer.ymax());
      rtRect = new RectHV(nd.point.x(), outer.ymin(), outer.xmax(), outer.ymax());
    }

    // if queryPoint is left of node, go left
    // then go right iff the right rect is closer than current min
    if (comparePoints(queryPoint, nd) < 0) {
      nearest = nearest(queryPoint, nd.lb, nearest, lbRect);
      minDist = queryPoint.distanceSquaredTo(nearest);

      if (rtRect.distanceSquaredTo(queryPoint) < minDist) {
        nearest = nearest(queryPoint, nd.rt, nearest, rtRect);
      }
      // else if queryPoint is right of node, go right
      // then go left iff the left rect is closer than the current min
    } else {
      nearest = nearest(queryPoint, nd.rt, nearest, rtRect);
      minDist = queryPoint.distanceSquaredTo(nearest);

      if (lbRect.distanceSquaredTo(queryPoint) < minDist) {
        nearest = nearest(queryPoint, nd.lb, nearest, lbRect);
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
  //   final RectHV rect = new RectHV(0.25, 0.25, 0.6, 0.8);

  //   StdDraw.setPenRadius(0.005);
  //   StdDraw.setPenColor(StdDraw.ORANGE);
  //   rect.draw();

  //   StdOut.println("Range in rect:");
  //   for (final Point2D point : kdTree.range(rect))
  //     StdOut.println(point.toString());
  }
}
