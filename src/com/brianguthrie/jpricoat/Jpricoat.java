package com.brianguthrie.jpricoat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.lobobrowser.html.domimpl.DocumentFragmentImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Jpricoat implements NodeList, Iterable<Node> {
	
	private Node origin;

	public Jpricoat() {
		this.origin = new DocumentFragmentImpl();
	}
	
	public Jpricoat(Node node) {
		this.origin = node;
	}
	
	public Node item(int index) {
		return origin.getChildNodes().item(index);
	}
	
	public int getLength() {
		return origin.getChildNodes().getLength();
	}
	
	public Iterator<Node> iterator() {
		final NodeList children = this.origin.getChildNodes();
		return new Iterator<Node>() {
			private int i = 0;
			
			public boolean hasNext() { return this.i < children.getLength(); }
			public Node next() { Node child = children.item(i); i++; return child; }
			public void remove() { }			
		};
	}
	
	private void addAllNodes(NodeList newNodes) {
		for(int i = 0; i < newNodes.getLength(); i++) {
			Node n = (Node) newNodes.item(i);
			origin.appendChild(n);
		}
	}
	
	public Jpricoat search(String... searches) throws Exception {
		Jpricoat finalList = new Jpricoat();
	
		for(String search : normalizeSearchesAsXPath(searches)) {
			NodeList intermediate = (NodeList) XPathFactory.newInstance().newXPath().evaluate(search, this.origin, XPathConstants.NODESET);
			finalList.addAllNodes(intermediate);
		}
		
		return finalList;
	}
	
	private static List<String> normalizeSearchesAsXPath(String... searches) {
		List<String> normalizedPaths = new ArrayList<String>();
		for(String search : searches) {
			if(isXPath(search)) {
				normalizedPaths.add(search);
			} else {
				for (String selector : search.split(",\\s*")) {
					normalizedPaths.add(new XPathConverter(selector).toXPath());
				}
			}
		}
		return normalizedPaths;
	}
	
	private static boolean isXPath(String search) {
		return search.matches("^(\\.\\/|\\/)");
	}
	
}
