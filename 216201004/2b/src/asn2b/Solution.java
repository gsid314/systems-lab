package asn2b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;



public class Solution {
	
	
	class BookBT{
		
		public BookBT(String id, String course_id) {
			super();
			this.id = id;
			this.course_id = course_id;
		}
		String id;
		String course_id;
		int faults;
		
		
	}
	
	class BookMT implements Comparable<BookMT>{
		
		public BookMT(String id, String course_id, int frequency) {
			super();
			this.id = id;
			this.course_id = course_id;
			this.frequency = frequency;
		}

		String id;
		String course_id;
		int frequency;
		int faults;
		
		@Override
		public int compareTo(BookMT o) {
			// TODO Auto-generated method stub
			
			BookMT f = (BookMT)o;
		    if(this.frequency>f.frequency)
				return 1;
		    else if(this.frequency<f.frequency)
				return -1;
			return 0;
		}
		
		
	}
	
	class BookP implements Comparable<BookP>{
		
		
		public BookP(String id, int counter, String course_id) {
			super();
			this.id = id;
			this.counter = counter;
			this.course_id = course_id;
		}

		String id;
		int counter;
		String course_id;
		int faults;
		
		@Override
		public int compareTo(BookP o) {
			// TODO Auto-generated method stub
			
			BookP f = (BookP)o;
		    if(this.counter<f.counter)
				return 1;
		    else if(this.counter>f.counter)
				return -1;
			return 0;
		}
		
	}
	
	
	Queue<BookBT> a = new LinkedList<BookBT>();
	List<BookMT> b = new ArrayList<BookMT>();
	
	List<BookP> c = new ArrayList<BookP>();
	
	Map<String,Integer> count_a= new HashMap<>();
	Map<String,Integer> count_b= new HashMap<>();
	Map<String,Integer> count_c= new HashMap<>();
	int a_capacity;
	int b_capacity;
	int c_capacity;
	int a_faults=0;
	int b_faults=0;
	int c_faults=0;
	int last_book_id_bt=0;
	int last_book_id_mt=0;
	int last_book_id_p=0;
	
	void fifo() {
		
	}
	
	
	void lru() {
		
	}
	
	void lfu() {
		
	}
	
	void issueBT(String course_id) {
		Integer i =count_a.get(course_id);
		if(i==null)
			count_a.put(course_id, 0);
		else count_a.put(course_id, i+1);
	
		boolean book_found=false;
		for(BookBT p :a) {
			if(p.course_id.equals(course_id)) {
				book_found =true;
				return;
			}
		}
	
		
		if(!book_found) {
			a_faults++;
			if(a_capacity==a.size())
		       {
				BookBT removed = a.remove();
				//System.out.println("Removed from bt:"+ removed.course_id);
				a.add(new BookBT(course_id+Integer.toString(++last_book_id_bt),course_id));
		       
		       }else {
		    	   //System.out.println("Adding to tray bt:"+ course_id);
		    	   a.add(new BookBT(course_id+Integer.toString(++last_book_id_bt),course_id));
			       
		       }
		}
	}
	
	void issuePhD(String course_id) {
		
		Integer a =count_c.get(course_id);
		if(a==null)
			count_c.put(course_id, 0);
		else count_c.put(course_id, a+1);
		boolean book_found=false;
		for(BookP p :c) {
			if(p.course_id.equals(course_id)) {
				book_found =true;
				p.counter=0;
				
				
			}else {
				p.counter++;
			}
		}
		
		
		if(!book_found) {
			b_faults++;
			if(c_capacity==c.size())
		       {
				Collections.sort(c);
				BookP removed = c.remove(0);
				//System.out.println("Removed from p:"+ removed.course_id);
				for(BookP p :c) {
					p.counter++;
					
				}
				c.add(new BookP(course_id+Integer.toString(++last_book_id_mt),0,course_id));//counter=0;
		       
		       }else {
		    		for(BookP p :c) {
						p.counter++;
						
					}
		    		//System.out.println("Adding to tray p:"+ course_id);
		    		c.add(new BookP(course_id+Integer.toString(++last_book_id_mt),0,course_id));
		       }
		}
		
	}
	
	void issueMT(String course_id) {
		
		Integer a =count_b.get(course_id);
		if(a==null)
			count_b.put(course_id, 0);
		else count_b.put(course_id, a+1);
		boolean book_found=false;
		for(BookMT p :b) {
			if(p.course_id.equals(course_id)) {
				book_found =true;
				p.frequency++;
				++last_book_id_p;
			
		}
		}
		
		
		if(!book_found) {
			c_faults++;
			if(b_capacity==b.size())
		       {
				Collections.sort(b);
				 BookMT removed = b.remove(0);
				// System.out.println("Removed from mt:"+ removed.course_id);
				 b.add(new BookMT(course_id+Integer.toString(++last_book_id_p),course_id,0));
		       
		       }else {
		    	 //  System.out.println("Adding to tray mt:"+ course_id);
		    	   b.add(new BookMT(course_id+Integer.toString(++last_book_id_p),course_id,0));//freq=0;
		       }
		}
		
	}
	
	
	String get_max_key(Map<String,Integer> count)
	{
		  int max=0;
		    String max_key="";
		    for(String key :count.keySet()) {
		    	if(count.get(key)>max)
		    		{max=count.get(key);
		    		 max_key=key;
		    		}
		    }
		    
		    return max_key;
	}
	
	public static void main(String args[]) {
		

		Solution sol = new Solution();
		String regex = "^[A-Za-z0-9]+$";
	    Scanner s = new Scanner(System.in);
	    int bt_max= s.nextInt();
	    int mt_max= s.nextInt();
	    int p_max= s.nextInt();
	    sol.a_capacity= s.nextInt();
	    sol.b_capacity= s.nextInt();
	    sol.c_capacity= s.nextInt();
	    while(s.hasNext()) {
	    	String a = s.next();
	    	if(a.charAt(0)=='b') {
	    		sol.issueBT(a);	    		
	    	}else if(a.charAt(0)=='m') {
	    		sol.issueMT(a);
	    	}else if(a.charAt(0)=='p') {
	    		sol.issuePhD(a);
	    		
	    	}else if(a.equals("-d")) {
	    		break;
	    	
	    	}
	    }
	    
	    int total_faults = sol.a_faults+sol.b_faults+sol.c_faults;
	    System.out.println("Total faults Btech:"+sol.a_faults);
	    System.out.println("Total faults Mtech:"+sol.b_faults);
	    System.out.println("Total faults PhD:"+sol.c_faults);
	    System.out.println("Max issued course id for Btech:"+  sol.get_max_key(sol.count_a));
	    System.out.println("Max issued course id for Mtech:"+  sol.get_max_key(sol.count_b));
	    System.out.println("Max issued course id for PhD:"+  sol.get_max_key(sol.count_c));
	    
	    
	    System.out.println("Btech books left after simulation:");
	    for(BookBT bt:sol.a) {
	    	System.out.println(bt.id+" "+ bt.course_id);
	    }
	    
	    System.out.println("Mtech books left after simulation:");
	    for(BookMT bt:sol.b) {
	    	System.out.println(bt.id+" "+ bt.course_id);
	    }
	    
	    System.out.println("Phd books left after simulation:");
	    
	    for(BookP bt:sol.c) {
	    	System.out.println(bt.id+" "+ bt.course_id);
	    }
	    
	    
	    
	    
	  
		
	}
}
