package test1;

import java.io.IOException;
import java.util.*;
import java.util.Scanner;
import java.io.*;

class Node {
    public Integer key;           // key
    public int val;         // associated data
    public Node left, right;  // links to left and right subtrees
    public boolean color;     // color of parent link
    //public int N;             // subtree count

    public Node(Integer key, int val, boolean color, int N) {
        this.key = key;
        this.val = val;
        this.color = color;
        //this.N = N;
    }
}

class pair{
	public Integer key;
	public int val;
	pair(Integer x, int y){
		this.key = x;
		this.val = y;
	}
}

public class RBTreeEventCounter{
	private static final boolean BLACK = false;
    private static final boolean RED   = true;

    private Node root;     // root of the BST

   /***************************************************************************
    *  Node helper methods.
    ***************************************************************************/
    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    // number of node in subtree rooted at x; 0 if x is null

    
    public boolean isEmpty() {
        return root == null;
    }


   /***************************************************************************
    *  Standard BST search.
    ***************************************************************************/

    public Node get(Integer key) {
        if (key == null) throw new NullPointerException("argument to get() is null");
        return get(root, key);
    }

    // value associated with the given key in subtree rooted at x; null if no such key
    private Node get(Node x, Integer key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if      (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else              return x;
        }
        return null;
    }

    public boolean contains(Integer key) {
        return get(key) != null;
    }
    
    /***************************************************************************
     *  Red-black tree Traversals.
     ***************************************************************************/
    public void inorder(Node n){
    	if(n == null) return;
    	inorder(n.left);
    	//System.out.println("ID = "+ n.key + " Value = "+ n.val);
    	System.out.print(""+n.key+n.color + " ");
    	inorder(n.right);
    }
    public void preorder(Node n){
    	if(n == null) return;
    	System.out.print(n.key+ " ");
    	preorder(n.left);
    	preorder(n.right);
    }
    
    public int MaxHeight(Node n){
    	if(n== null) return 0;
    	return 1+ Math.max(MaxHeight(n.left), MaxHeight(n.right));
    }
    public int MinHeight(Node n){
    	if(n == null) return 0;
    	return 1+ Math.min(MinHeight(n.left), MinHeight(n.right));
    }

   /***************************************************************************
    *  Red-black tree insertion.
    ***************************************************************************/

    public void put(Integer key, int val) {
        if (key == null) throw new NullPointerException("first argument to put() is null");
        if ( val <= 0) {
            //delete(key);
            return;
        }

        root = put(root, key, val);
        root.color = BLACK;
        // assert check();
    }

    // insert the key-value pair in the subtree rooted at h
    private Node put(Node h, Integer key, int val) { 
        if (h == null) return new Node(key, val, RED, 1);

        int cmp = key.compareTo(h.key);
        if      (cmp < 0) h.left  = put(h.left,  key, val); 
        else if (cmp > 0) h.right = put(h.right, key, val); 
        else              h.val   = val;

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        if (isRed(h.left)  &&  isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left)  &&  isRed(h.right))     flipColors(h);
        //h.N = size(h.left) + size(h.right) + 1;

        return h;
    }
    
    /***************************************************************************
     *  Red-black tree node deletion.
     ***************************************************************************/
    
    public void delete(Integer key) { 
        if (key == null) throw new NullPointerException("argument to delete() is null");
        //System.out.println("Integer is: " + key);
        //if (!contains(key)) return;

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, key);
        if (!isEmpty()) root.color = BLACK;
    }

    // delete the key-value pair with the given key rooted at h
    private Node delete(Node h, Integer key) { 
        if (key.compareTo(h.key) < 0)  {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, key);
        }
        else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                h.right = deleteMin(h.right);
            }
            else h.right = delete(h.right, key);
        }
        return balance(h);
    }

   /***************************************************************************
    *  Red-black tree helper functions.
    ***************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        // assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        //x.N = h.N;
        //h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        // assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        //x.N = h.N;
        //h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }
    
	private Node moveRedLeft(Node h) {

        flipColors(h);
        if (isRed(h.right.left)) { 
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private Node moveRedRight(Node h) {

        flipColors(h);
        if (isRed(h.left.left)) { 
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }
    
    private Node deleteMin(Node h) { 
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);
        return balance(h);
    }
	
	private Node balance(Node h) {
        if (isRed(h.right))                      h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))     flipColors(h);

        //h.N = size(h.left) + size(h.right) + 1;
        return h;
    }
	
	private Node min(Node x) { 
        if (x.left == null) return x; 
        else                return min(x.left); 
    }   
    /***************************************************************************
     *  Increasing & reducing the value
     ***************************************************************************/

     public void increase(Integer key, int m) {
         if (key == null) throw new NullPointerException("argument to get() is null");
         increase(root, key, m);
     }

     // value associated with the given key in subtree rooted at x; null if no such key
     private void increase(Node x, Integer key, int m) {
         while (x != null) {
             int cmp = key.compareTo(x.key);
             if      (cmp < 0) x = x.left;
             else if (cmp > 0) x = x.right;
             else {
            	 x.val =  x.val+m;
            	 break;
             }
         }
         if(x == null) {
        	 System.out.println(0);
        	 return ;
         }
         if(x.val <= 0){
        	 //System.out.println("Count of ID "+ key + " is lessthan 0. Hence deleting the node");
        	 delete(key);
        	 System.out.println(0);
         }
         else System.out.println(x.val);
     }
     
     public void reduce(Integer key, int m) {
         if (key == null) throw new NullPointerException("argument to get() is null");
         reduce(root, key, m);
     }

     // value associated with the given key in subtree rooted at x; null if no such key
     private void reduce(Node x, Integer key, int m) {
         while (x != null) {
             int cmp = key.compareTo(x.key);
             if      (cmp < 0) x = x.left;
             else if (cmp > 0) x = x.right;
             else {
            	 x.val =  x.val-m;
            	 break;
             }
         }
         if(x == null) {
        	 //System.out.println("Node with ID "+ key + " is not found");
        	 System.out.println(0);
        	 return ;
         }
         //System.out.println("Value is: "+x.val);
         if(x.val <= 0){
        	 //System.out.println("Count of ID "+ key + " is lessthan 0. Hence deleting the node");
        	 delete(key);
        	 System.out.println(0);
         }
         else System.out.println(x.val);
     }
     
     /***************************************************************************
      *  Inorder successor and predecessor in BST.
      ***************************************************************************/

      public Node successor(Integer key) {
          if (key == null) throw new NullPointerException("argument to get() is null");
          Node n = findNode(root, key);
          if(n == null) {
        	  //System.out.println("ID with key "+key+" is not found");
        	  //Node temp = findNextBig(root, key);
        	  //return temp;
        	  Node x = this.root;
        	  if(x == null) return null;
        	  int dif = Integer.MAX_VALUE;
        	  Node nextMax = null;
        	  while(x != null){
        		if(x.key > key){
        			int temp = x.key - key;
        			if(temp < dif) {
        				dif = temp;
        				nextMax = x;
        				x = x.left;
        			}
        			else break;
        		}
        		else if(x.key < key){
        			x = x.right;
        		}
        	  }
        	  return nextMax;
          }
       
          if(n.right != null){
        	  n = n.right;
        	  while(n.left != null) n = n.left;
        	  return n;
          }
          
          Node temp = root, suc = null;
          while(temp != null){
        	  int cmp = key.compareTo(temp.key);
        	  if(cmp < 0){
        		  suc = temp;
        		  temp = temp.left;
        	  }
        	  else if(cmp > 0) temp = temp.right;
        	  else break;
          }
          return suc;
      }
      
      public Node previous(Integer key) {
          if (key == null) throw new NullPointerException("argument to get() is null");
          Node n = findNode(root, key);
          if(n == null) {
//        	  System.out.println("ID with key "+key+" is not found");
//        	  return null;
        	  Node x = this.root;
        	  if(x == null) return null;
        	  int dif = Integer.MAX_VALUE;
        	  Node nextMax = null;
        	  while(x != null){
        		if(x.key < key){
        			int temp = key-x.key;
        			if(temp < dif) {
        				dif = temp;
        				nextMax = x;
        				x = x.right;
        			}
        			else break;
        		}
        		else if(x.key > key){
        			x = x.left;
        		}
        	  }
        	  return nextMax;
          }
       
          if(n.left != null){
        	  n = n.left;
        	  while(n.right != null) n = n.right;
        	  return n;
          }
          
          Node temp = root, prev = null;
          while(temp != null){
        	  int cmp = key.compareTo(temp.key);
        	  if(cmp < 0) temp = temp.left;
        	  else if(cmp > 0){
        		  prev = temp;
        		  temp = temp.right;
        	  }
        	  else break;
          }
          return prev;
      }
      
      private Node findNode(Node x, Integer key) {
          while (x != null) {
              int cmp = key.compareTo(x.key);
              if      (cmp < 0) x = x.left;
              else if (cmp > 0) x = x.right;
              else              return x;
          }
          return null;
      }
      
      /***************************************************************************
      *  Sum of count in range.
      ***************************************************************************/
      
      public long inRange(Integer k1, Integer k2){
    	  Node bk = findBreak(root, k1, k2);
    	  if(bk == null) return 0;
    	  long sum =bk.val;
    	  Node left= bk.left, right = bk.right;
    	  //calculating left sum
    	  while(left != null){
    		  int cmp= k1.compareTo(left.key);
    		  if(cmp <= 0){
    			  sum += left.val;
        		  sum += sumUnderneath(left.right);
        		  left = left.left;
    		  }
    		  else left = left.right;
    	  }
    	  //calculating right sum
    	  while(right != null){
    		  int cmp= k2.compareTo(right.key);
    		  if(cmp >= 0){
    			  sum += right.val;
        		  sum += sumUnderneath(right.left);
        		  right = right.right;
    		  }
    		  else right = right.left;
    	  }
    	  return sum;
      }
      public Node findBreak(Node root, Integer k1, Integer k2){
    	  if(root == null) return root;
    	  int cmp1 = k2.compareTo(root.key);
    	  int cmp2 = k1.compareTo(root.key);
    	  if(cmp1 >= 0 && cmp2 <= 0) return root;
    	  else if(cmp1 < 0) return findBreak(root.left, k1, k2);
    	  else return findBreak(root.right, k1, k2);
      }
      private long sumUnderneath(Node n1){
    	  if(n1 == null) return 0;
    	  return n1.val+ sumUnderneath(n1.left)+sumUnderneath(n1.right);
      }
    
      //public static HashMap<Integer, Long> map = new HashMap();
      public static ArrayList<pair> list = new ArrayList();
      public static int height =0;
    public static void main(String[] args) throws IOException { 
    	String pathname = args[0];
    	//CreateTree(pathname);
    	//RBTreeEventCounter st = new RBTreeEventCounter();
    	
    	//Scanner scanner = new Scanner(new File("C:\\JavaWorkSpace\\workspace\\ADS_EventCounter\\src\\test1\\file-name"));
    	File input = new File(pathname);
    	Scanner scanner = new Scanner(input);
    	
    	int numOfNodes = scanner.nextInt();
    	height = (int)Math.log(numOfNodes);
    	scanner.nextLine();
    	for(int i =0; i<numOfNodes; i++){
    		String line = scanner.nextLine();
    		StringTokenizer tokenizer = new StringTokenizer(line);
    		int ID = Integer.parseInt(tokenizer.nextToken());
    		int count = Integer.parseInt(tokenizer.nextToken());
    		//map.put(ID, (long)count);
    		list.add(new pair(ID, count));
    		//st.put(ID, count);
    	}
    	RBTreeEventCounter st = new RBTreeEventCounter();
    	st.root = CreateTree();
    	//st.inorder(st.root);
    	System.out.println();
    	//st.preorder(st.root);
    	System.out.println("\nMax Ht: " + st.MaxHeight(st.root) + " Min Ht: " + st.MinHeight(st.root));
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
    			//System.out.println(Id);
    			Node n1 = st.get(Id);
    			if(n1==null) System.out.println(0);
    			else System.out.println(n1.val);
    		}
    		if(s.equals("inrange")){
    			int Id1 = Integer.parseInt(scanner.next());
    			int Id2 = Integer.parseInt(scanner.next());
    			System.out.println(st.inRange(Id1, Id2));
    		}
    		if(s.equals("next")){
    			int Id = Integer.parseInt(scanner.next());
    			Node n1 = st.successor(Id);
    			if(n1 == null) System.out.println(0+" "+0);
    			else System.out.println(n1.key+" "+ n1.val);
    		}
    		if(s.equals("previous")){
    			int Id = Integer.parseInt(scanner.next());
    			Node n1 = st.previous(Id);
    			if(n1 == null) System.out.println(0+" "+0);
    			else System.out.println(n1.key+" "+ n1.val);
    		}
    		if(s.equals("quit")) break;
    	}
    }
    
    private static Node CreateTree() throws IOException{
    	System.out.println(" "+list.size());
    	return CreateTree(0, list.size()-1, 1);
    }
    private static Node CreateTree(int st, int end, int level){
    	if(st > end) return null;
    	int mid = (st + end)/2;
    	Node n1 = new Node(list.get(mid).key, list.get(mid).val, BLACK, 1);
    	//System.out.println(n1.key+ " "+ n1.val);
    	n1.left = CreateTree(st, mid-1, level+1);
    	n1.right = CreateTree(mid+1, end, level+1);
    	if(level == height) n1.color=RED;
    	return n1;
    }
}
