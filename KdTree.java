/******************************************************************************
 *  Compilation:  javac KdTree.java
 *  Execution:    java KdTree filename.txt
 *  
 *  Initializes a set of 2-dimensional points in a cartesian plane bounded by
 *  [0, 0] to [1, 1].  Points are passed to KdTree as Point2D objects. The
 *  Point2D class (along with other dependencies of this class) can be found
 *  in Princeton University's algs4 package.
 * 
 *  The class implements the field of points using an efficient KdTree 
 *  implementation (a modified binary search tree).
 *
 * @author Jason Fricano
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.ResizingArrayBag;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class KdTree {
  private static final boolean VERTICAL = true;               // defines the split orientation
  private static final boolean HORIZONTAL = false;            // of a point Node
  private static final RectHV RANGE = new RectHV(0, 0, 1, 1); // defines the range of all points

  private Node root;      // root of the KdTree, points

  private class Node {
    private final Point2D point;            // key of the node
    private final boolean splitOrientation; // associated data
    private Node lb, rt;                    // left-top and right-bottom subtrees
    private int size;                       // number of nodes in subtree

    public Node(final Point2D p, final boolean split, final int sz) {
      point = p;
      splitOrientation = split;
      lb = null;
      rt = null;
      size = sz;
    }
  }

  /** 
   * constructs an empty set of points
   */ 
  public KdTree() {

  }

  // ************************** IS_EMPTY **************************
  /** 
   * is the set empty? 
   * @return is the set empty
  */
  public boolean isEmpty() {
    return root == null;
  }

  // **************************** SIZE ****************************
  /**
   * number of points in the set
   * @return number of points in the set
   */
  public int size() {
    return size(root);
  }

  // returns the size of passed node's subtree
  private int size(final Node nd) {
    return nd == null ? 0 : nd.size;
  }

  // *************************** INSERT ***************************
  /**
   * adds a point to the set
   * @param p 2D point object to be added
   * @throws IllegalArgumentException if null argument is passed
   */
  public void insert(final Point2D p) {
    if (p == null)
      throw new IllegalArgumentException("calls insert() with a null key");
    root = insert(root, p, HORIZONTAL);
  }

  // helper method for public insert() method
  // Node nd paramter is the node being processed
  // Point2D p is the point to be added
  // boolean isVertical used to switch the split orienatation
  // at each generation of a node
  private Node insert(final Node nd, final Point2D p, final boolean isVertical) {
    if (nd == null)         return new Node(p, isVertical, 1);
    if (p.equals(nd.point)) return nd;
    
    final double cmp = comparePoints(p, nd);
    if (cmp < 0) nd.lb = insert(nd.lb, p, !isVertical);
    else         nd.rt = insert(nd.rt, p, !isVertical);
    nd.size = 1 + size(nd.lb) + size(nd.rt);
    return nd;
  }

  // *************************** COMPARE **************************
  // determines whether a point falls to the lb or rt of a node
  private static double comparePoints(final Point2D p, final Node cmp) {
    if (cmp.splitOrientation == VERTICAL) return p.y() - cmp.point.y();
    else                                  return p.x() - cmp.point.x();
  }

  // ************************** CONTAINS **************************
  /**
   * does the set contain point p?
   * @param p 2D-point object to be located
   * @return does the set contain point p?
   * @throws IllegalArgumentException if null argument is passed
   */
  public boolean contains(final Point2D p) {
    if (p == null) 
      throw new IllegalArgumentException("calls contains() with null argument");
    return contains(root, p);
  }

  // public contains() helper function
  // nd is node being processed
  // p is the point being searched for
  private boolean contains(final Node nd, final Point2D p) {
    if (nd == null)         return false;
    if (p.equals(nd.point)) return true;

    final double cmp = comparePoints(p, nd);
    if (cmp < 0) return contains(nd.lb, p);
    else         return contains(nd.rt, p);
  }

  // **************************** DRAW ****************************
  /**
   * draws all the points and KdTree grid splits to StdDraw
   * uses @code {algs4.StdDraw} default canvas size
   */
  public void draw() {
    StdDraw.setScale(0, 1);
    draw(root, RANGE);
  }

  // helper function for pubilc draw()
  // nd is node being drawn
  // outer is the grid rectangle surrounding the node (created by all ancestors)
  private void draw(final Node nd, final RectHV outer) {
    if (nd == null) return;
    
    RectHV lbRect, rtRect;            // child grid rectangles
    final double x = nd.point.x();
    final double y = nd.point.y();

    StdDraw.setPenRadius(0.01);
    StdDraw.setPenColor();
    StdDraw.point(x, y);

    StdDraw.setPenRadius();

    // if vertical split, use nd's y value as the boundary of the children
    // if horizontal split, use nd's x value as the boundary of the children
    // use other existing boundaries of outer grid rectangle
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
    // recursively draw the children
    draw(nd.lb, lbRect);
    draw(nd.rt, rtRect);
  }

  // *************************** RANGE ****************************
  /**
   * All points that are inside the rectangle (or on the boundary)
   * @param rect rectangle defining the range to be searched
   * @return collection of point objects falling within the specified range
   * @throws IllegalArgumentException if null argument is passed
   */
  public Iterable<Point2D> range(final RectHV rect) {
    if (rect == null) 
      throw new IllegalArgumentException("calls range() with null argument");
      
    final ResizingArrayBag<Point2D> pointsInRange = new ResizingArrayBag<>();
    range(rect, root, pointsInRange, RANGE);
    return pointsInRange;
  }

  // helper method for public range() 
  // adds a node's point to the pointsInRange Iterable (from public method) 
  //   if the point is within the range provided
  // rect is the range to be searched
  // nd is the node being processed
  // outer is the rectangle defining the node's grid space
  private void range(final RectHV rect, final Node nd, 
                     final ResizingArrayBag<Point2D> pointsInRange,
                     final RectHV outer) {
    if (nd == null) return;
    
    RectHV lbRect, rtRect;        // children's grid spaces
    
    // add the node's point if it falls within the range
    if (rect.contains(nd.point)) pointsInRange.add(nd.point);

    // initialize grid spaces for the children
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

  // ************************** NEAREST ***************************
  /**
   * a nearest neighbor in the set to point p; null if the set is empty
   * @param p point object being queried for nearest
   * @return  point object nearest to the passed point object using Euclidean distance
   * @throws IllegalArgumentException if null argument is passed
  */
  public Point2D nearest(final Point2D p) {
    if (p == null)   
      throw new IllegalArgumentException("calls nearest() with null argument");
    if (root == null) return null;
    return nearest(p, root, root.point, RANGE);
  }

  // helper function for public nearest()
  // returns the nearest neighbor to the queried point to public nearest() call
  // accepts a node to be processed, currentNearest, and the node's rectangular grid space
  private Point2D nearest(final Point2D queryPoint, final Node nd, 
                          final Point2D currentNearest, 
                          final RectHV outer) {
    if (nd == null)                  return currentNearest;
    if (queryPoint.equals(nd.point)) return nd.point;

    RectHV lbRect, rtRect;            // children's grid rectangles
    Point2D nearest = currentNearest; // updated nearest
    double minDist = queryPoint.distanceSquaredTo(nearest);  // dist to nearest, sq for efficiency
    final double thisDist = queryPoint.distanceSquaredTo(nd.point);  // dist to node being processed

    // update nearest, min if current node contains
    if (thisDist < minDist) {
      nearest = nd.point;
      minDist = thisDist;
    }

    // initialize the left and right children's grid rectangles
    if (nd.splitOrientation == VERTICAL) {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), outer.xmax(), nd.point.y());
      rtRect = new RectHV(outer.xmin(), nd.point.y(), outer.xmax(), outer.ymax());
    } else {
      lbRect = new RectHV(outer.xmin(), outer.ymin(), nd.point.x(), outer.ymax());
      rtRect = new RectHV(nd.point.x(), outer.ymin(), outer.xmax(), outer.ymax());
    }

    // determine whether / where to recursively call nearest
    // if queryPoint is left of node, go left
    //   then go right iff the right rect is closer than current min
    // else if queryPoint is right of node, go right
    //   then go left iff the left rect is closer than the current min
    if (comparePoints(queryPoint, nd) < 0) {
      nearest = nearest(queryPoint, nd.lb, nearest, lbRect);
      minDist = queryPoint.distanceSquaredTo(nearest);
      if (rtRect.distanceSquaredTo(queryPoint) < minDist) 
        nearest = nearest(queryPoint, nd.rt, nearest, rtRect);
    } else {
      nearest = nearest(queryPoint, nd.rt, nearest, rtRect);
      minDist = queryPoint.distanceSquaredTo(nearest);
      if (lbRect.distanceSquaredTo(queryPoint) < minDist) 
        nearest = nearest(queryPoint, nd.lb, nearest, lbRect);
    }
    return nearest;
  }

  // **************************** MAIN ****************************
  /**
   * unit testing
   * @param args the name of the file to be used for test input
   */
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
    final RectHV rect = new RectHV(0.25, 0.25, 0.6, 0.8);

    StdDraw.setPenRadius();
    StdDraw.setPenColor(StdDraw.ORANGE);
    rect.draw();

    StdOut.println("Range in rect:");
    for (final Point2D point : kdTree.range(rect))
      StdOut.println(point.toString());
  }
}
