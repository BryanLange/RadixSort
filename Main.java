import java.io.*;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) {
		if(args.length == 3) {
			
			// display file names on console
			for(int i=0; i<args.length; i++) {
				System.out.println("args["+ i +"] : " + args[i]);
			}
			
			// open input and output files
			Scanner inFile = null;
			PrintWriter outFile1 = null;
			PrintWriter outFile2 = null;
			try {
				inFile = new Scanner(new File(args[0]));
				outFile1 = new PrintWriter(new File(args[1]));
				outFile2 = new PrintWriter(new File(args[2]));
			} catch(IOException e) {
				System.out.println("A file was not found, program terminated.");
				System.exit(0);
			}
			
			
			// determine maximum number of digits and offset if needed
			// close and re-open inFile
			RadixSort x = new RadixSort();
			x.firstReading(inFile);
			inFile.close();
			try {
				inFile = new Scanner(new File(args[0]));
			} catch(IOException e) {
				System.out.println("A file was not found, program terminated.");
				System.exit(0);
			}
			
			
			// create stack to hold data from inFile
			x.loadStack(inFile, outFile2);
			
			
			// run Radix Sort Algorithm
			x.RxSort(outFile1, outFile2);
			
			
			// close input and output files
			inFile.close();
			outFile1.close();
			outFile2.close();
		}
		else {
			System.out.println("Invalid number of arguments.");
		}
	} // end main
	
	
	
	public static class RadixSort {
		int tableSize;
		LLQ[][] hashTable;
		int data;
		int currentTable;
		int previousTable;
		int maxDigits;
		int offSet;
		int currentDigit;
		LLStack stack;
		
		
		// constructor, initialize instance variables
		public RadixSort() {			
			tableSize = 10;
			hashTable = new LLQ[2][tableSize];
			for(int i=0; i<2; i++) {
				for(int j=0; j<tableSize; j++) {
					hashTable[i][j] = new LLQ();
				}
			}
			
			stack = new LLStack();
		} // end constructor
		
		
		// read in all data from inFile
		// determine min and max number, and offset if negative number exists
		// stores length of max number
		public void firstReading(Scanner inFile) {
			int minNum = 0, maxNum = 0;
			
			while(inFile.hasNext()) {
				data = inFile.nextInt();
				
				if(data < minNum) minNum = data;
				if(data > maxNum) maxNum = data;
			}
			
			offSet = Math.abs(minNum);
			maxNum = maxNum + offSet;
			maxDigits = getMaxDigits(maxNum);
		} // end firstReading()
		
		
		// create stack to hold data from inFile
		public void loadStack(Scanner inFile, PrintWriter debugFile) {
			while(inFile.hasNext()) {
				int data = inFile.nextInt();
				data += offSet;
				
				listNode newNode = new listNode(data);
				
				stack.push(newNode);
			}
			stack.printStack(debugFile);
		} // end loadStack()
		
		
		// move stack to first hashTable to begin sorting
		public void dumpStack(int currentTable, int currentDigit, PrintWriter debugFile) {
			while(!stack.isEmpty()) {
				listNode node = stack.pop();
				int hashIndex = getDigit(node, currentDigit);
				
				hashTable[currentTable][hashIndex].addTail(node);
			}
			
			printTable(currentTable, currentDigit, debugFile);		
		} // end dumpStack()
		
		
		// 
		public void bucketIndex() {
			
		}
		
		
		// returns the length of given integer
		public int getMaxDigits(int maxNum) {
			return String.valueOf(maxNum).length();
		} // end getMaxDigits()
		
		
		// Radix Sort algorithm
		public void RxSort(PrintWriter outFile, PrintWriter debugFile) {
			currentDigit = 0;
			currentTable = 0;
			dumpStack(currentTable, currentDigit, debugFile);
			
			for(currentDigit = 1; currentDigit < maxDigits; currentDigit++) {
				previousTable = currentTable;
				currentTable = (currentTable == 0) ? 1 : 0;
				
				for(int currentQueue = 0; currentQueue < tableSize; currentQueue++) {
					while(!hashTable[previousTable][currentQueue].isEmpty()) {
						listNode node = hashTable[previousTable][currentQueue].deleteHead();
						
						int hashIndex = getDigit(node, currentDigit);
						
						hashTable[currentTable][hashIndex].addTail(node);
					}
				}
				
				printTable(currentTable, currentDigit, debugFile);
			}
			
			printSortResult(currentTable, outFile);
		} // end RxSort()
		
		
		// returns current digit of given node's data
		public int getDigit(listNode node, int currentDigit) {
			int num = node.data;
			for(int i=0; i<currentDigit; i++) {
				if(num == 0) break;
				num /= 10;
			}
			return num%10;
		} // end getDigit()
		
		
		// prints non-empty queues of given hashTable
		public void printTable(int currentTable, int currentDigit, PrintWriter debugFile) {
			for(int i=0; i<tableSize; i++) {
				if(!hashTable[currentTable][i].isEmpty()) {
					hashTable[currentTable][i].printQueue(i, currentDigit, debugFile);					
				}
			}
		} // end printTable()
		
		
		// prints the final result of the Radix Sort
		public void printSortResult(int currentTable, PrintWriter outFile) {
			for(int i=0; i<tableSize; i++) {
				listNode spot = hashTable[currentTable][i].head;
				while(spot != null) {
					int data = spot.data - offSet;
					outFile.print(data + " ");
					spot = spot.next;
				}
			}
		} // end printSortResult()
		
		
		//=====================================================================
		public static class listNode {
			int data;
			listNode next;
			
			public listNode(int d) {
				data = d;
				next = null;
			}	
		} // end nested class: listNode
		
		
		//=====================================================================
		public static class LLStack {
			listNode top;
			
			// constructor
			public LLStack() {
				top = null;
			}
			
			// insert given node on top of stack
			public void push(listNode node) {
				if(top == null) {
					top = node;
				} else {
					node.next = top;
					top = node;
				}
			} // end push()
			
			
			// removes and returns node at top of stack
			public listNode pop() {
				listNode payload = top;
				top = top.next;
				return payload;
			} // end pop()
			
			
			// returns true if stack is empty
			public boolean isEmpty() {
				return top == null;
			} // end isEmpty()
			
			
			// prints entire stack to given outFile
			public void printStack(PrintWriter debugFile) {
				debugFile.println("*** Below is the output of the stack ***");
				debugFile.print("Top -> ");
				listNode nav = top;
				
				while(nav.next != null) {
					debugFile.print("(" + nav.data + ", " + nav.next.data + ") -> ");
					nav = nav.next;
				}
				debugFile.println("(" + nav.data + ", NULL) -> NULL");
				debugFile.println();
			} // end printStack()
			
		} // end nested class: LLStack
		
		
		//=====================================================================
		public static class LLQ {
			listNode head;
			listNode tail;
			
			// constructor
			public LLQ() {
				head = null;
				tail = null;
			}
			
			
			// add given node to the back of the queue
			public void addTail(listNode node) {
				if(isEmpty()) {
					head = node;
					tail = node;
					node.next = null;
				} else {
					tail.next = node;
					tail = node;
					node.next = null;
				}
			} // end addTail()
			
			
			// removes and returns node at front of queue
			public listNode deleteHead() {
				listNode payload = head;
				head = head.next;
				return payload;
			} // end deleteHead()
			
			
			// returns true if queue is empty
			public boolean isEmpty() {
				return head == null;
			}
			
			
			// prints entire queue to given outFile
			public void printQueue(int index, int currentDigit, PrintWriter debugFile) {
				debugFile.println("*** Below is the output of the queue " + "at index "
							+ index + ", sorting on currentDigit: " + currentDigit + " ***");
				debugFile.print("Front(" + index + ") -> ");
				listNode nav = head;
				while(nav.next != null) {
					debugFile.print("(" + nav.data + ", " + nav.next.data + ") -> ");
					nav = nav.next;
				}
				debugFile.println("(" + nav.data + ", NULL) -> NULL");
				
				
				debugFile.print("Back(" + index + ") -> ");
				debugFile.println("(" + tail.data + ", NULL) -> NULL");
				debugFile.println();
			} // end printQueue()
			
		} // end nested class: LLQ
		
	} // end class: RadixSort

} // end wrapper class: Main