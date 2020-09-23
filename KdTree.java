import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

public class KdTree {
  private static final boolean VERTICAL   = true;
  private static final boolean HORIZONTAL = false;
  private Node root;

  private class Node {
    private Point2D point; // sorted by key
    private boolean splitOrientation; // associated data
    private Node lb, rt; // left and right subtrees
    private int size; // number of nodes in subtree

    public Node(Point2D p, boolean split, int sz) {
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

  // is the set empty?
  public boolean isEmpty() {
    return root == null;
  }

  // number of points in the set
  public int size() {
    return size(root);
  }

  private int size(Node x) {
    return x == null ? 0 : x.size;
  }

  // add the point to the set (if it is not already in the set)
  public void insert(Point2D p) {
    if (p == null) throw new IllegalArgumentException("calls insert() with a null key");
    root = insert(root, p, VERTICAL);
  }

  public Node insert(Node nd, Point2D p, boolean isVertical) {
    if (nd == null) return new Node(p, isVertical, 1);
    double cmp = comparePoints(p, nd.point, nd.splitOrientation);
    // what about equals to zero??
    if (cmp < 0) nd.lb = insert(nd.lb, p, !isVertical);
    else         nd.rt = insert(nd.rt, p, !isVertical);
    return nd;
  }

  private static double comparePoints(Point2D p, Point2D cmp, boolean isCmpVertical) {
    if (isCmpVertical) return p.y() - cmp.y();
    else               return p.x() - cmp.x();
  }

  // does the set contain point p?
  public boolean contains(Point2D p) {
    return contains(root, p);
  }
  
  private boolean contains(Node nd, Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to contains() is null");
    if (nd == null) return false;
    if (p.equals(nd.point)) return true;
    double cmp = comparePoints(p, nd.point, nd.splitOrientation);
    // what about equals zero??
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
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

  }
}
