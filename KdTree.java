import java.util.NoSuchElementException;
import java.util.TreeSet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.ResizingArrayQueue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL = true;
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
    if (p == null)
      throw new IllegalArgumentException("calls insert() with a null key");
    root = insert(root, p, VERTICAL);
  }

  public Node insert(Node nd, Point2D p, boolean isVertical) {
    if (nd == null)
      return new Node(p, isVertical, 1);
    if (p.equals(nd.point))
      return nd;
    double cmp = comparePoints(p, nd);
    // what about equals to zero??
    if (cmp < 0)
      nd.lb = insert(nd.lb, p, !isVertical);
    else
      nd.rt = insert(nd.rt, p, !isVertical);
    nd.size = 1 + size(nd.lb) + size(nd.rt);
    return nd;
  }

  // **************************** COMPARE ****************************
  private static double comparePoints(Point2D p, Node cmp) {
    if (cmp.splitOrientation == VERTICAL)
      return p.y() - cmp.point.y();
    else
      return p.x() - cmp.point.x();
  }

  // **************************** CONTAINS ****************************
  // does the set contain point p?
  public boolean contains(Point2D p) {
    return contains(root, p);
  }

  private boolean contains(Node nd, Point2D p) {
    if (p == null)
      throw new IllegalArgumentException("argument to contains() is null");
    if (nd == null)
      return false;
    if (p.equals(nd.point))
      return true;
    double cmp = comparePoints(p, nd);
    // what about equals zero??
    if (cmp < 0)
      return contains(nd.lb, p);
    else
      return contains(nd.rt, p);
  }

  // **************************** DRAW ****************************
  public void draw() {
    SET<Double> xLimits = new SET<Double>();
    SET<Double> yLimits = new SET<Double>();

    StdDraw.setCanvasSize(800, 800);
    StdDraw.setScale(0, 1);
    draw(root, xLimits, yLimits);
  }

  private void draw(Node nd, SET<Double> xLimits, SET<Double> yLimits) {
    if (nd == null)
      return;
    double x = nd.point.x();
    double y = nd.point.y();
    double[] stopLimits;

    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor();
    StdDraw.point(x, y);

    StdDraw.setPenRadius();
    if (nd.splitOrientation == VERTICAL) {
      stopLimits = getLimits(xLimits, x);
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(stopLimits[0], y, stopLimits[1], y);
      yLimits.add(y);
    } else {
      stopLimits = getLimits(yLimits, y);
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(x, stopLimits[0], x, stopLimits[1]);
      xLimits.add(x);
    }

    draw(nd.lb, xLimits, yLimits);
    rmLimits(nd, nd.lb, xLimits, yLimits);

    draw(nd.rt, xLimits, yLimits);
    rmLimits(nd, nd.rt, xLimits, yLimits);
  }

  private void rmLimits(Node nd, Node child, SET<Double> xLimits, SET<Double> yLimits) {
    if (nd.splitOrientation == VERTICAL && child != null)
      xLimits.remove(child.point.x());
    else if (child != null)
      yLimits.remove(child.point.y());
  }

  private double[] getLimits(SET<Double> limits, double coord) {
    double[] minMax = new double[2];
    minMax[0] = 0;
    minMax[1] = 1;
    for (double lim : limits) {
      if (lim > minMax[0] && lim < coord)
        minMax[0] = lim;
      if (lim < minMax[1] && lim > coord)
        minMax[1] = lim;
    }
    return minMax;
  }

  // **************************** GETTERS ****************************
  // ---------------------------- points -----------------------------
  private Iterable<Point2D> getAllPoints() {
    ResizingArrayBag<Point2D> bag = new ResizingArrayBag<>();
    getAllPoints(root, bag);
    return bag;
  }

  private void getAllPoints(Node nd, ResizingArrayBag<Point2D> bag) {
    if (nd == null)
      return;
    bag.add(nd.point);
    getAllPoints(nd.lb, bag);
    getAllPoints(nd.rt, bag);
  }

  private Iterable<Point2D> getPoints() {
    if (isEmpty())
      return new ResizingArrayQueue<>();
    return getPoints(min(), max());
  }

  // make first arg function call to make this general
  private ResizingArrayQueue<Point2D> getPoints(Node lo, Node hi) {
    if (lo == null)
      throw new IllegalArgumentException("first argument to keys() is null");
    if (hi == null)
      throw new IllegalArgumentException("second argument to keys() is null");
    ResizingArrayQueue<Point2D> q = new ResizingArrayQueue<>();
    getPoints(root, q, lo, hi);
    return q;
  }

  private void getPoints(Node nd, ResizingArrayQueue<Point2D> q, Node lo, Node hi) {
    if (nd == null)
      return;
    double cmpLo = comparePoints(lo.point, nd);
    double cmpHi = comparePoints(hi.point, nd);
    if (cmpLo < 0)
      getPoints(nd.lb, q, lo, hi);
    if (cmpLo <= 0 && cmpHi >= 0)
      q.enqueue(nd.point);
    if (cmpHi > 0)
      getPoints(nd.rt, q, lo, hi);
  }

  // ---------------------------- nodes ----------------------------
  private Iterable<Node> levelOrder() {
    ResizingArrayQueue<Node> points = new ResizingArrayQueue<Node>();
    ResizingArrayQueue<Node> q = new ResizingArrayQueue<Node>();
    q.enqueue(root);
    while (!q.isEmpty()) {
      Node nd = q.dequeue();
      if (nd == null)
        continue;
      points.enqueue(nd);
      q.enqueue(nd.lb);
      q.enqueue(nd.rt);
    }
    return points;
  }

  private Iterable<Node> getAllNodes() {
    ResizingArrayBag<Node> bag = new ResizingArrayBag<>();
    getAllNodes(root, bag);
    return bag;
  }

  private void getAllNodes(Node nd, ResizingArrayBag<Node> bag) {
    if (nd == null)
      return;
    bag.add(nd);
    getAllNodes(nd.lb, bag);
    getAllNodes(nd.rt, bag);
  }

  // **************************** MAX AND MIN ****************************
  private Node min() {
    if (isEmpty())
      throw new NoSuchElementException("calls min() with empty symbol table");
    return min(root);
  }

  private Node min(Node nd) {
    if (nd.lb == null)
      return nd;
    else
      return min(nd.lb);
  }

  private Node max() {
    if (isEmpty())
      throw new NoSuchElementException("calls max() with empty symbol table");
    return max(root);
  }

  private Node max(Node nd) {
    if (nd.rt == null)
      return nd;
    else
      return max(nd.rt);
  }

  // **************************** RANGE ****************************
  // all points that are inside the rectangle (or on the boundary)
  public Iterable<Point2D> range(RectHV rect) {
    return null;
  }

  // **************************** NEAREST ****************************
  // a nearest neighbor in the set to point p; null if the set is empty
  public Point2D nearest(Point2D p) {
    return nearest(p, root, root.point, p.distanceTo(root.point), null);
  }

  private Point2D nearest(Point2D queryPoint, Node nd, Point2D currentNearest, double currentMin, Node parent) {
    if (nd == null) return currentNearest;
    if (queryPoint.equals(nd.point)) return nd.point;

    Point2D closestOnLine;
    Point2D nearest = currentNearest;
    double min = currentMin;
    double queryDist = queryPoint.distanceTo(nd.point);
    
    if (queryDist < currentMin) {
      nearest = nd.point;
      min = queryDist;
    }
    
    double limitingCoord = getLimitingCoord(queryPoint, nd, parent);
    // if the query point is left/below the node
    if (comparePoints(queryPoint, nd) < 0) {
      // check the left child
      nearest = nearest(queryPoint, nd.lb, nearest, min, nd);
      min = queryPoint.distanceTo(nearest);

      // then check the right child if there is a point that could be closer in the right child
      // this is done by checking whether the closest point in the right child branch is less than the current min
      if (nd.splitOrientation == VERTICAL) {
        closestOnLine = new Point2D(limitingCoord, nd.point.y());
        if (queryPoint.distanceTo(closestOnLine) < min)
          nearest = nearest(queryPoint, nd.rt, nearest, min, nd);
      } else {
        closestOnLine = new Point2D(nd.point.x(), limitingCoord);
        if (queryPoint.distanceTo(closestOnLine) < min)
          nearest = nearest(queryPoint, nd.rt, nearest, min, nd);
      }
    // if the query point is right / above the node
    } else {
      nearest = nearest(queryPoint, nd.rt, nearest, min, nd);
      min = queryPoint.distanceTo(nearest);

      if (nd.splitOrientation == VERTICAL) {
        closestOnLine = new Point2D(limitingCoord, nd.point.y());
        if (queryPoint.distanceTo(closestOnLine) < min)
          nearest = nearest(queryPoint, nd.lb, nearest, min, nd);
      } else {
        closestOnLine = new Point2D(nd.point.x(), limitingCoord);
        if (queryPoint.distanceTo(closestOnLine) < min)
          nearest = nearest(queryPoint, nd.lb, nearest, min, nd);
      }
    }

    return nearest;
  }

  private double getLimitingCoord(Point2D queryPoint, Node nd, Node parent) {
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
    // Node leftChild = nd.lb;
    // Node rtChild = nd.rt;
    // StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation
    // == VERTICAL ? "VERT" : "HORIZ"));
    // StdOut.println("Children: "
    // + (leftChild == null ? "none" : leftChild.point.toString())
    // + "\t" + (rtChild == null ? "none" : rtChild.point.toString()));
    // StdOut.println("Size: " + nd.size);
    // StdOut.println();
    // }

    int i = 0;
    for (Node nd : kdTree.levelOrder()) {
      Node leftChild = nd.lb;
      Node rtChild = nd.rt;
      StdOut.println(++i + ". " + nd.point.toString() + "\t" + (nd.splitOrientation == VERTICAL ? "--" : "|"));
      StdOut.println("Children: " + (leftChild == null ? "none" : leftChild.point.toString()) + "\t"
          + (rtChild == null ? "none" : rtChild.point.toString()));
      StdOut.println("Size: " + nd.size);
      StdOut.println();
    }

    // StdOut.println();

    // kdTree.draw();

    // test nearest
    StdOut.println();
    Point2D test = new Point2D(0.1, 0.3);
    // StdDraw.setPenRadius(0.01);
    // StdDraw.setPenColor(StdDraw.ORANGE);
    // test.draw();
    StdOut.println("nearest:\t" + kdTree.nearest(test));
  }
}
