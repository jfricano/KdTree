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

  }

  // does the set contain point p?
  public boolean contains(Point2D p) {
    return true;
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
