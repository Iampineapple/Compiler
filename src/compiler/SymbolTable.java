package compiler;
//This program was written by Cory Haight-Nali on or around 2 Dec 2014.
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import compiler.Type;
import compiler.Descriptor;

class StackStruct{
	//This essentially implements a linked list, modified for the nodes we're using
	//As each node has a depth value, we need a bit more data
	//This implementation is largely based upon (and modified from) the code in 
	//http://codereview.stackexchange.com/questions/52558/implementing-a-stack-in-java-for-technical-interview
	Node first;
	Node last;
	
	public StackStruct(Node f, Node l){
		first = f;
		last = l;
		first.next = last;
	}//constructor
	
	public boolean isEmpty(){
		return last != null;
	}

	public void push(int depth, Descriptor descriptor){
		last = new Node(depth, descriptor, last);
	}//push a new Node onto the stack
	
	public Descriptor pop(){
		if(isEmpty()){
			throw new IllegalStateException("That stack is empty!");
		}//If the stack is empty
		else{
			Descriptor item = last.descriptor;
			last = last.next;
			return item;
		}//else return the descriptor of the top
	}//pop the top item off the stack
	
	public Descriptor peekDescriptor(){
		if(isEmpty()){
			throw new IllegalStateException("That stack is empty!");
		}
		return last.descriptor;
	}//peek Descriptor - just look at the descriptor
	
	public int peekDepth(){
		if(isEmpty()){
			throw new IllegalStateException("That stack is empty!");
		}
		return last.itemDepth;
	}//peekDepth - just return the depth of the top item
	//The top two should probably be combined into one, and thus Node would hold just
	//one thing, which would be it's own data structure holding an int and a Descriptor
	
}//StackStruct


class Node{
	int itemDepth;
	Descriptor descriptor;
	Node next;
	
	public Node(int depth, Descriptor d, Node n){
		itemDepth = depth;
		descriptor = d;
		next = n;
	}//constructor
}//Node

public class SymbolTable{
	private int depth;
	private Hashtable<String, StackStruct> hashTableofStacks;
	private Iterator<Hashtable.StackStruct> iter;
	private StackStruct e;
	//e is a temporary variable to look at different StackStructs
	//Code for e, and the iterator taken from in-class notes and
	//http://stackoverflow.com/questions/2351331/iterating-hashtable-in-java
	
	
	public SymbolTable(){
		depth = 0;
		hashTableofStacks = new Hashtable<String, StackStruct>();
		Iterator<Entry<String, StackStruct>> iter = hashTableofStacks.entrySet().iterator();
	}//Symbol Table
	
	public boolean isEmpty(){
		return (depth == 0);
	}//isEmpty
	
	public void push(){
		depth++;
	}//push
	
	public void pop(){
		if(isEmpty()){
			throw new IllegalStateException("There are no scopes to pop");
		}
		while(iter.hasNext()){
			e = iter.next();
			if(depth == e.peekDepth()){
				e.pop();
			}//if
		}//Cycle through finding and removing all of the scopes we're popping
		depth--;
	}//pop
	
	public boolean isDeclared(String name){
		if(isEmpty()){
			throw new IllegalStateException("There are no scopes to check!");
		}
		return (hashTableofStacks.get(name) != null);
	}//isDeclared
	
	public Descriptor getDescriptor(String name){
		if(isEmpty()){
			throw new IllegalStateException("There is no scope!");
		}
		if(hashTableofStacks.get(name) == null){
			return null;
		}//if name is not declared
		else
			return hashTableofStacks.get(name).peekDescriptor();
	}//getDescriptor
	
	public boolean setDescriptor(String name, Descriptor descriptor){
		StackStruct current = hashTableofStacks.get(name);
		if(current.peekDepth() == depth){
			return false;
		}
		else{
			current.push(depth, descriptor);
		}
	}//setDescriptor
	
	public static void main(String[] args){
		SymbolTable symTab = new SymbolTable();
		symTab.push();
		
	}//main
}//SymbolTable