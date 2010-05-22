package com.brianguthrie.jpricoat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A NodeList for people who like things named "list" to maybe act like lists.
 */
public class HumaneNodeList implements NodeList, List<Node> {
	
	private List<Node> wrappedList;
	
	public HumaneNodeList() {
		this.wrappedList = new ArrayList<Node>();
	}
	
	public HumaneNodeList(NodeList list) {
		this.wrappedList = new ArrayList<Node>();
		for (int i = 0; i < list.getLength(); i++) {
			this.wrappedList.add((Node) list.item(i));
		}
	}

	public void add(int index, Node element) {
		wrappedList.add(index, element);
	}

	public boolean add(Node o) {
		return wrappedList.add(o);
	}

	public boolean addAll(Collection<? extends Node> c) {
		return wrappedList.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends Node> c) {
		return wrappedList.addAll(index, c);
	}

	public void clear() {
		wrappedList.clear();
	}

	public boolean contains(Object o) {
		return wrappedList.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return wrappedList.containsAll(c);
	}

	public boolean equals(Object o) {
		return wrappedList.equals(o);
	}

	public Node get(int index) {
		return wrappedList.get(index);
	}

	public int hashCode() {
		return wrappedList.hashCode();
	}

	public int indexOf(Object o) {
		return wrappedList.indexOf(o);
	}

	public boolean isEmpty() {
		return wrappedList.isEmpty();
	}

	public Iterator<Node> iterator() {
		return wrappedList.iterator();
	}

	public int lastIndexOf(Object o) {
		return wrappedList.lastIndexOf(o);
	}

	public ListIterator<Node> listIterator() {
		return wrappedList.listIterator();
	}

	public ListIterator<Node> listIterator(int index) {
		return wrappedList.listIterator(index);
	}

	public Node remove(int index) {
		return wrappedList.remove(index);
	}

	public boolean remove(Object o) {
		return wrappedList.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return wrappedList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return wrappedList.retainAll(c);
	}

	public Node set(int index, Node element) {
		return wrappedList.set(index, element);
	}

	public int size() {
		return wrappedList.size();
	}

	public List<Node> subList(int fromIndex, int toIndex) {
		return wrappedList.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return wrappedList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return wrappedList.toArray(a);
	}

	public int getLength() {
		return this.size();
	}

	public Node item(int index) {
		return this.get(index);
	}

}
