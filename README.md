# KdTree

Project for Princeton's Data Structures and Algorithms course.  The topic for this week is binary search trees and their geometric applications.

Note:  These files depend on libraries imported from Princeton University's open-source edu.princeton.cs.algs4 package (included in this repo as algs4.jar).  

<ul>
  <li><a target="_blank" href="https://algs4.cs.princeton.edu/code/" >Info and how to install environment</a></li>
  <li><a href="https://algs4.cs.princeton.edu/code/javadoc/" target="_blank" rel="noopener noreferrer">algs4.jar Documentation</a></li>
  <li><a href="https://algs4.cs.princeton.edu/code/algs4.jar">Dowload Princeton's algs4.jar package</a></li>
</ul>

The KdTree data type represents a set of points in the unit square and uses a 2d-tree to support efficient range search and nearest-neighbor search. 

The immutable data type <a href="https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/Point2D.html">Point2D</a> (part of algs4.jar) represents points in the plane. 

The immutable data type <a href="https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/RectHV.html">RectHV</a> (part of algs4.jar) represents axis-aligned rectangles. 

<h2>PointSET.java</h2>

<b>Brute-force implementation.</b> The mutable data type PointSET.java represents a set of points in the unit square. The following API implements a red–black BST using the <a href="https://algs4.cs.princeton.edu/code/javadoc/edu/princeton/cs/algs4/SET.html">SET</a> class (part of algs4.jar):

        public PointSET()                           // construct an empty set of points 
        public boolean isEmpty()                    // is the set empty? 
        public int size()                           // number of points in the set 
        public void insert(Point2D p)               // add the point to the set (if it is not already in the set)
        public boolean contains(Point2D p)          // does the set contain point p? 
        public void draw()                          // draw all points to standard draw 
        public Iterable<Point2D> range(RectHV rect) // all points that are inside the rectangle (or on the boundary) 
        public Point2D nearest(Point2D p)           // a nearest neighbor in the set to point p; null if the set is empty 
        public static void main(String[] args)      // unit testing of the methods (optional) 
	
<h2>KdTree.java</h2>

<b>2d-tree implementation.</b> The mutable data type KdTree.java uses a 2d-tree to implement the same API (but replace PointSET with KdTree). A 2d-tree is a generalization of a BST to two-dimensional keys. The BST is built with points in the nodes, using the x- and y-coordinates of the points as keys in strictly alternating sequence.

<i>Search and insert.</i> The algorithms for search and insert are similar to those for BSTs, but at each child, the split orientation alternates from horizontal to vertical.

<i>Draw.</i> A 2d-tree divides the unit square in a simple way: all the points to the left of the root go in the left subtree; all those to the right go in the right subtree; and so forth, recursively. The draw() method draws all of the points to standard draw in black and the subdivisions in red (for vertical splits) and blue (for horizontal splits).

The prime advantage of a 2d-tree over a BST is that it supports efficient implementation of range search and nearest-neighbor search. 
<ul>
<li><i>Range Search.</i> To find all points contained in a given query rectangle, the method starts at the root and recursively searches for points in <em>both</em> subtrees by pruning: if the query rectangle does nto intersect the rectangle corresponding to a node, there is no need to explore that node or its subtrees.</li>
<li><i>Nearest-neighbor search.</i> To find a closest point to a given query point, this method starts at the root and recusively searches in <em>both</em> subtrees by pruing: if the closest point discovered so far is closer than the distance between the query point and the rectangle corresponding to a node, there is no need to explore that node or its subtrees.  Thus, the method is designed to always first search the substree that is on the same side of a split as the query point.</li></ul>

<h2>Data for Testing</h2>
The subfolder 'data' contains data to construct and test the above classes.
