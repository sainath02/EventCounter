package event_counter;

import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.io.*;

class Event {
	
    public int theID;           // ID of Event
    public int count;         // count of the Event
    public Event left, right;  // links to left and right Events
    public boolean color;     // color of Event

    public Event(int theID, int count, boolean color) {
        this.theID = theID;
        this.count = count;
        this.color = color;
    }
}

public class bbst{
	
	private Event root;     // root of the BST

   /***************************************************************************
    *  Event helper methods for Event colors, Tree Traversals, Tree Height Calculation
    ***************************************************************************/
   
	private static final boolean BLACK = false;
    private static final boolean RED   = true;
    
    // Finds if the Event color is RED
    private boolean isRed(Event e) {
        if (e == null) return false;
        return e.color == RED;
    }

    // Check whether tree is Empty
    public boolean isEmpty() {
        return root == null;
    }
    
    //Inorder traversal and Preorder traversal to validate the tree structure.
    public void inorder(Event e){
    	if(e == null) return;
    	inorder(e.left);
    	//System.out.println("ID = "+ n.theID + " Value = "+ n.count);
    	System.out.print(""+e.theID+e.color + " ");
    	inorder(e.right);
    }
    public void preorder(Event e){
    	if(e == null) return;
    	System.out.print(e.theID+ " ");
    	preorder(e.left);
    	preorder(e.right);
    }
    
    //Finds the height of tree to validate the tree structure.
    public int MaxHeight(Event e){
    	if(e== null) return 0;
    	return 1+ Math.max(MaxHeight(e.left), MaxHeight(e.right));
    }
    public int MinHeight(Event e){
    	if(e == null) return 0;
    	return 1+ Math.min(MinHeight(e.left), MinHeight(e.right));
    }


   /***************************************************************************
    *  Standard BST search for searching an Event.
    *  This is called while input method is to find count
    ***************************************************************************/

    public Event get(Integer theID) {
        if (theID == null) throw new NullPointerException("argument to get() is null");
        return get(root, theID);
    }

    private Event get(Event e, Integer theID) {
        while (e != null) {
            int cmp = theID.compareTo(e.theID);
            if      (cmp < 0) e = e.left;
            else if (cmp > 0) e = e.right;
            else              return e;
        }
        return null;
    }

    public boolean contains(Integer theID) {
        return get(theID) != null;
    }
	
    /***************************************************************************
     *  Standard BST insertion.
     *  This is called while input method is to increase count of non-existing ID.
     ***************************************************************************/

	public void put(int theID, int count) {
        if ( count <= 0) {
            System.out.println(0);
            return;
        }
        root = put(root, theID, count);
        root.color = BLACK; //Make sures the root node is always black after modifications
    }
    private Event put(Event e, Integer theID, int count) { 
        if (e == null) return new Event(theID, count, RED);

        int cmp = theID.compareTo(e.theID);
        if      (cmp < 0) e.left  = put(e.left,  theID, count); 
        else if (cmp > 0) e.right = put(e.right, theID, count); 
        else              e.count   = count;

        // adjusts the tree if any right-leaning links
        if (isRed(e.right) && !isRed(e.left))      e = rotateLeft(e);
        if (isRed(e.left)  &&  isRed(e.left.left)) e = rotateRight(e);
        if (isRed(e.left)  &&  isRed(e.right))     flipColors(e);
        return e;
    }
        
    /***************************************************************************
     *  Red-black tree node deletion.
     ***************************************************************************/
    
    public void delete(Integer theID) {
        if (theID == null) throw new NullPointerException("argument to delete() is null");
        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, theID);
        if (!isEmpty()) root.color = BLACK;
    }

    // delete the theID-count pair with the given theID rooted at e
    private Event delete(Event e, Integer theID) { 
        if (theID.compareTo(e.theID) < 0)  {
            if (!isRed(e.left) && !isRed(e.left.left))
                e = moveRedLeft(e);
            e.left = delete(e.left, theID);
        }
        else {
            if (isRed(e.left))
                e = rotateRight(e);
            if (theID.compareTo(e.theID) == 0 && (e.right == null))
                return null;
            if (!isRed(e.right) && !isRed(e.right.left))
                e = moveRedRight(e);
            if (theID.compareTo(e.theID) == 0) {
                Event min = min(e.right);
                e.theID = min.theID;
                e.count = min.count;
                e.right = deleteMin(e.right);
            }
            else e.right = delete(e.right, theID);
        }
        return balance(e);
    }

   /***************************************************************************
    *  Red-black tree helper functions used in insertion and deletion.
    ***************************************************************************/

    // make a left-leaning link lean to the right
    private Event rotateRight(Event e) {
        Event x = e.left;
        e.left = x.right;
        x.right = e;
        x.color = x.right.color;
        x.right.color = RED;
        return x;
    }

    // make a right-leaning link lean to the left
    private Event rotateLeft(Event e) {
        Event x = e.right;
        e.right = x.left;
        x.left = e;
        x.color = x.left.color;
        x.left.color = RED;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Event e) {
        e.color = !e.color;
        e.left.color = !e.left.color;
        e.right.color = !e.right.color;
    }
    
	private Event moveRedLeft(Event e) {
        flipColors(e);
        if (isRed(e.right.left)) { 
            e.right = rotateRight(e.right);
            e = rotateLeft(e);
            flipColors(e);
        }
        return e;
    }

    private Event moveRedRight(Event e) {
        flipColors(e);
        if (isRed(e.left.left)) { 
            e = rotateRight(e);
            flipColors(e);
        }
        return e;
    }
    
    private Event deleteMin(Event e) { 
        if (e.left == null)
            return null;
        if (!isRed(e.left) && !isRed(e.left.left))
            e = moveRedLeft(e);
        e.left = deleteMin(e.left);
        return balance(e);
    }
	
	private Event balance(Event h) {
        if (isRed(h.right))                      h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))     flipColors(h);
        return h;
    }
	
	private Event min(Event x) { 
        if (x.left == null) return x; 
        else                return min(x.left); 
    }
	
    /***************************************************************************
     *  Increasing & reducing the count
     ***************************************************************************/

     public void increase(int theID, int m) {
         increase(root, theID, m);
     }

     //increases the count of give theID. Adds node to tree if ID doesn't exists.
     private void increase(Event e, Integer theID, int m) {
         while (e != null) {
             int cmp = theID.compareTo(e.theID);
             if      (cmp < 0) e = e.left;
             else if (cmp > 0) e = e.right;
             else {
            	 e.count =  e.count+m;
            	 break;
             }
         }
         if(e == null) {
        	 if(m>0){
        		 put(theID, m);
        		 System.out.println(m);
        	 } 
        	 else System.out.println(0);
        	 return ;
         }
         if(e.count <= 0){
        	 //System.out.println("Count of ID "+ theID + " is lessthan 0. Hence deleting the node");
        	 delete(theID);
        	 System.out.println(0);
         }
         else System.out.println(e.count);
     }
     
     public void reduce(int theID, int m) {
         reduce(root, theID, m);
     }
     
     // Decrease the count of give theID. Deletes the node if the count goes below 0
     private void reduce(Event e, Integer theID, int m) {
         while (e != null) {
             int cmp = theID.compareTo(e.theID);
             if      (cmp < 0) e = e.left;
             else if (cmp > 0) e = e.right;
             else {
            	 e.count =  e.count-m;
            	 break;
             }
         }
         if(e == null) {
        	 //System.out.println("Event with ID "+ theID + " is not found");
        	 System.out.println(0);
        	 return ;
         }
         //System.out.println("Value is: "+e.count);
         if(e.count <= 0){
        	 //System.out.println("Count of ID "+ theID + " is lessthan 0. Hence deleting the node");
        	 delete(theID);
        	 System.out.println(0);
         }
         else System.out.println(e.count);
     }
     
     /***************************************************************************
      *  Similar to finding inorder successor and predecessor in BST.
      *  It returns the existing event whose ID is next / previous to given ID.
      *  These methods are called when input is find next or previous.
      ***************************************************************************/

     public Event next(int theID){
         if (isEmpty()) throw new NoSuchElementException("called next() on Empty tree");
         Event e = next(root, theID);
         if (e == null) return null;
         else           return e;
     }
     
     // the smallest theID in the subtree rooted at e greater than or equal to the given theID
     private Event next(Event e, Integer theID) {  
         if (e == null) return null;
         int cmp = theID.compareTo(e.theID);
         if (cmp >= 0)  return next(e.right, theID);
         Event t = next(e.left, theID);
         if (t != null) return t; 
         else           return e;
     }
	 
	 public Event previous(int theID) {
         if (isEmpty()) throw new NoSuchElementException("called previous() on Empty tree");
         Event e = previous(root, theID);
         if (e == null) return null;
         else           return e;
     }    

     // the largest theID in the subtree rooted at e less than or equal to the given theID
     private Event previous(Event e, Integer theID) {
         if (e == null) return null;
         int cmp = theID.compareTo(e.theID);
         if (cmp <= 0)  return previous(e.left, theID);
         Event t = previous(e.right, theID);
         if (t != null) return t; 
         else           return e;
     }
      
      /***************************************************************************
      *  returns the sum of counts in given range of IDs. 
      ***************************************************************************/
      
      public long inRange(Integer k1, Integer k2){
    	  Event bk = findBreak(root, k1, k2); //finds break point in tree
    	  if(bk == null) return 0;
    	  long sum =bk.count;
    	  Event left= bk.left, right = bk.right;
    	  
    	  //calculating left sum
    	  while(left != null){
    		  int cmp= k1.compareTo(left.theID);
    		  if(cmp <= 0){
    			  sum += left.count;
        		  sum += sumUnderneath(left.right);
        		  left = left.left;
    		  }
    		  else left = left.right;
    	  }
    	  
    	  //calculating right sum
    	  while(right != null){
    		  int cmp= k2.compareTo(right.theID);
    		  if(cmp >= 0){
    			  sum += right.count;
        		  sum += sumUnderneath(right.left);
        		  right = right.right;
    		  }
    		  else right = right.left;
    	  }
    	  return sum;
      }
      
      //Find the break point where the nodes path splits.
      public Event findBreak(Event root, Integer k1, Integer k2){
    	  if(root == null) return root;
    	  int cmp1 = k2.compareTo(root.theID);
    	  int cmp2 = k1.compareTo(root.theID);
    	  if(cmp1 >= 0 && cmp2 <= 0) return root;
    	  else if(cmp1 < 0) return findBreak(root.left, k1, k2);
    	  else return findBreak(root.right, k1, k2);
      }
      
      private long sumUnderneath(Event n1){
    	  if(n1 == null) return 0;
    	  return n1.count+ sumUnderneath(n1.left)+sumUnderneath(n1.right);
      }
    
      /***************************************************************************
       *  Create tree in O(n) time with given list of events.
       *  Uses level to track the height of tree.
       *  Events of tree are made by default black.
       *  Events in last level of tree are modified to RED to maintain RB Tree properties.
       *  Returns the root node.
       ***************************************************************************/
      private static Event CreateTree(int numOfEvents) throws IOException{
    	  return CreateTree(0, numOfEvents-1, 1);
      }
      private static Event CreateTree(int st, int end, int level){
    	  if(st > end) return null;
    	  int mid = (st + end)/2;
    	  Event n1 = new Event(IDs[mid], counts[mid], BLACK);
    	  n1.left = CreateTree(st, mid-1, level+1);
    	  n1.right = CreateTree(mid+1, end, level+1);
    	  if(level == height) n1.color=RED;
    	  return n1;
      }
      
      public static int [] IDs;
      public static int [] counts;
      public static int height =0;
      public static void main(String[] args) throws IOException {
    	  String pathname = args[0];
    	  
    	//Scanner scanner = new Scanner(new File("C:\\JavaWorkSpace\\workspace\\ADS_EventCounter\\src\\test1\\file-name"));
    	File input = new File(pathname);
    	@SuppressWarnings("resource")
		Scanner scanner = new Scanner(input);
    	
    	int numOfEvents = scanner.nextInt();
    	height = (int) Math.ceil(Math.log(numOfEvents+1)/Math.log(2)) ; //used to build tree in O(n) time.
    	System.out.println(height);
    	IDs = new int[numOfEvents];
    	counts = new int[numOfEvents];
    	scanner.nextLine();
    	for(int i =0; i<numOfEvents; i++){
    		String line = scanner.nextLine();
    		StringTokenizer tokenizer = new StringTokenizer(line);
    		int ID = Integer.parseInt(tokenizer.nextToken());
    		int count = Integer.parseInt(tokenizer.nextToken());
    		IDs[i] = ID;
    		counts[i] = count;
    	}
    	
    	bbst st = new bbst();
    	st.root = CreateTree(numOfEvents);
    	st.inorder(st.root);
    	//st.preorder(st.root);
    	System.out.println("\nMax Ht: " + st.MaxHeight(st.root) + " Min Ht: " + st.MinHeight(st.root));
    	
    	// Tree construction is completed.
    	// Starts listening to standard input.
    	scanner = new Scanner(System.in);
    	while(scanner.hasNext()){
    		String s = scanner.next();
    		if(s.equals("increase")){
    			int Id = Integer.parseInt(scanner.next());
    			int count = Integer.parseInt(scanner.next());
    			//System.out.println(Id + " "+ count);
    			st.increase(Id, count);
    		}
    		if(s.equals("reduce")){
    			int Id = Integer.parseInt(scanner.next());
    			int count = Integer.parseInt(scanner.next());
    			st.reduce(Id, count);
    		}
    		if(s.equals("count")){
    			int Id = Integer.parseInt(scanner.next());
    			Event n1 = st.get(Id);
    			if(n1==null) System.out.println(0);
    			else System.out.println(n1.count);
    		}
    		if(s.equals("inrange")){
    			int Id1 = Integer.parseInt(scanner.next());
    			int Id2 = Integer.parseInt(scanner.next());
    			System.out.println(st.inRange(Id1, Id2));
    		}
    		if(s.equals("next")){
    			int Id = Integer.parseInt(scanner.next());
    			Event n1 = st.next(Id);
    			if(n1 == null) System.out.println(0+" "+0);
    			else System.out.println(n1.theID+" "+ n1.count);
    		}
    		if(s.equals("previous")){
    			int Id = Integer.parseInt(scanner.next());
    			Event n1 = st.previous(Id);
    			if(n1 == null) System.out.println(0+" "+0);
    			else System.out.println(n1.theID+" "+ n1.count);
    		}
    		if(s.equals("quit")) break;
    	}
    }
}