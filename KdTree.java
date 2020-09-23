import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayQueue;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL   = true;
  private static final boolean HORIZONTAL = false;
  private static final RectHV RANGE = new RectHV(0, 0, 1, 1);

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

  private int size(Node x) {
    return x == null ? 0 : x.size;
  }

  // **************************** INSERT ****************************
  // add the point to the set (if it is not already in the set)
  public void insert(Point2D p) {
    if (p == null) throw new IllegalArgumentException("calls insert() with a null key");
    root = insert(root, p, VERTICAL);
  }

  public Node insert(Node nd, Point2D p, boolean isVertical) {
    if (nd == null) return new Node(p, isVertical, 1);
    if (p.equals(nd.point)) return nd;
    double cmp = comparePoints(p, nd);
    // what about equals to zero??
    if (cmp < 0) nd.lb = insert(nd.lb, p, !isVertical);
    else         nd.rt = insert(nd.rt, p, !isVertical);
    nd.size = 1 + size(nd.lb) + size(nd.rt);
    return nd;
  }

  // **************************** COMPARE ****************************
  private static double comparePoints(Point2D p, Node cmp) {
    if (cmp.splitOrientation == VERTICAL) return p.y() - cmp.point.y();
    else                                  return p.x() - cmp.point.x();
  }

  // **************************** CONTAINS ****************************
  // does the set contain point p?
  public boolean contains(Point2D p) {
    return contains(root, p);
  }
  
  private boolean contains(Node nd, Point2D p) {
    if (p == null) throw new IllegalArgumentException("argument to contains() is null");
    if (nd == null) return false;
    if (p.equals(nd.point)) return true;
    double cmp = comparePoints(p, nd);
    // what about equals zero??
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
  }

  // draw all points to standard draw
  public void draw() {
    for (Point2D p : getAllPoints()) StdDraw.point(p.x(), p.y());
  }

  // **************************** GETTERS ****************************

  // ----------------------- points -----------------------
  private Iterable<Point2D> getAllPoints() {
    ResizingArrayBag<Point2D> bag = new ResizingArrayBag<>();
    getAllPoints(root, bag);
    return bag;
  }

  private void getAllPoints(Node nd, ResizingArrayBag<Point2D> bag) {
    if (nd == null) return;
    bag.add(nd.point);
    getAllPoints(nd.lb, bag);
    getAllPoints(nd.rt, bag); 

  }

  private Iterable<Point2D> getPoints() {
    if (isEmpty()) return new ResizingArrayQueue<>();
    return getPoints(min(), max());
  }

  // make first arg function call to make this general
  private ResizingArrayQueue<Point2D> getPoints(Node lo, Node hi) {
    if (lo == null) throw new IllegalArgumentException("first argument to keys() is null"); 
    if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");
    ResizingArrayQueue<Point2D> q = new ResizingArrayQueue<>();
    getPoints(root, q, lo, hi);
    return q;
  }

  private void getPoints(Node nd, ResizingArrayQueue<Point2D> q, Node lo, Node hi) {
    if (nd == null) return;
    double cmpLo = comparePoints(lo.point, nd);
    double cmpHi = comparePoints(hi.point, nd);
    if (cmpLo < 0) getPoints(nd.lb, q, lo, hi);
    if (cmpLo <= 0 && cmpHi >= 0) q.enqueue(nd.point);
    if (cmpHi > 0) getPoints(nd.rt, q, lo, hi);
  }

  private Iterable<Node> levelOrder() {
    ResizingArrayQueue<Node> points = new ResizingArrayQueue<Node>();
    ResizingArrayQueue<Node> q = new ResizingArrayQueue<Node>();
    q.enqueue(root);
    while (!q.isEmpty()) {
        Node nd = q.dequeue();
        if (nd == null) continue;
        points.enqueue(nd);
        q.enqueue(nd.lb);
        q.enqueue(nd.rt);
    }
    return points;
  }

  // ----------------------- nodes -----------------------
  private Iterable<Node> getAllNodes() {
    ResizingArrayBag<Node> bag = new ResizingArrayBag<>();
    getAllNodes(root, bag);
    return bag;
  }

  private void getAllNodes(Node nd, ResizingArrayBag<Node> bag) {
    if (nd == null) return;
    bag.add(nd);
    getAllNodes(nd.lb, bag);
    getAllNodes(nd.rt, bag); 
  }

  // **************************** MAX AND MIN ****************************
  private Node min() {
    if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
    return min(root);
  }

  private Node min(Node nd) {
    if (nd.lb == null) return nd;
    else               return min(nd.lb);
  }

  private Node max() {
    if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
    return max(root);
  }

  private Node max(Node nd) {
    if (nd.rt == null) return nd;
    else               return max(nd.rt);
  }

  // **************************** RANGE AND NEAREST ****************************
  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    return null;
  }

  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    return null;
  }

  // **************************** MAIN ****************************
  // unit testing of the methods (optional)
  public static void main(String[] args) {
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
    // int i = 0;
    // for (Node nd : kdTree.getAllNodes()) {
    //   Node leftChild = nd.lb;
    //   Node rtChild = nd.rt;
    //   StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation == VERTICAL ? "VERT" : "HORIZ"));
    //   StdOut.println("Children: " 
    //                  + (leftChild == null ? "none" : leftChild.point.toString())
    //                  + "\t" + (rtChild == null ? "none" : rtChild.point.toString()));
    //   StdOut.println("Size: " + nd.size);
    //   StdOut.println();
    // }

    int i = 0;
    for (Node nd : kdTree.levelOrder()) {
      Node leftChild = nd.lb;
      Node rtChild = nd.rt;
      StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation == VERTICAL ? "--" : "|"));
      // StdOut.println("Children: " 
      //                + (leftChild == null ? "none" : leftChild.point.toString())
      //                + "\t" + (rtChild == null ? "none" : rtChild.point.toString()));
      StdOut.println("Size: " + nd.size);
      StdOut.println();
    }


    // StdOut.println();

    // i = 0;
    // for (Point2D point : kdTree.getPoints())
    //   StdOut.println(++i + ". " + point.toString());

    // StdOut.println();
    // StdOut.println("size of set:\t" + kdTree.size());
    // StdOut.println("empty set?:\t" + kdTree.isEmpty());
    // StdOut.println("contains (false):\t" + kdTree.contains(new Point2D(0, 3)));
    // StdOut.println("contains (true):\t" + kdTree.contains(new Point2D(0.5, 1)));
    // StdOut.println();

    // // test draw(), range()
    // RectHV rect = new RectHV(0.25, 0.25, 0.8, 1.0);

    // StdDraw.setCanvasSize(800, 800);
    // StdDraw.setPenRadius(0.005);
    // StdDraw.setScale(-.05, 1.05);
    // kdTree.draw();
    // rect.draw();

    // StdOut.println("Range in rect:");
    // for (Point2D point : kdTree.range(rect))
    //   StdOut.println(point.toString());

    // // test nearest
    // StdOut.println();
    // Point2D test = new Point2D(0.77, 0.5);
    // test.draw();
    // StdOut.println("nearest:\t" + kdTree.nearest(test));
  }
}
