package com.brianguthrie.jpricoat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.lobobrowser.html.UserAgentContext;
import org.lobobrowser.html.domimpl.DocumentFragmentImpl;
import org.lobobrowser.html.parser.HtmlParser;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Jpricoat implements NodeList, Iterable<Node> {
	
	private List<Node> nodes = new ArrayList<Node>();

	public Jpricoat() { }
	
	public Jpricoat(Node node) {
		addAllNodes(node.getChildNodes());
	}
	
	public Node getOrigin() {
		Node origin = new DocumentFragmentImpl();
		for (Node n : this.nodes) origin.appendChild(n);
		return origin;
	}
	
	public Jpricoat(String urlString) {
		InputStream in = null;
		try {
			Document doc = createEmptyDocument(); 
			in = new URL(urlString).openConnection().getInputStream();
			new HtmlParser(createUserAgentContext(), (Document) doc).parse(in);
			addAllNodes(doc.getChildNodes());
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			try { in.close(); } catch(IOException e) { }
		}
	}
	
	public Node item(int index) {
		return this.nodes.get(index);
	}
	
	public int getLength() {
		return this.nodes.size();
	}
	
	public Iterator<Node> iterator() {
		return this.nodes.iterator();
	}
	
	private void addAllNodes(NodeList newNodes) {
		for(int i = 0; i < newNodes.getLength(); i++) {
			Node n = (Node) newNodes.item(i);
			if (!this.nodes.contains(n)) this.nodes.add(n);
		}
	}
	
	public Jpricoat search(String... searches) throws Exception {
		Jpricoat finalList = new Jpricoat();
	
		for(String search : normalizeSearchesAsXPath(searches)) {
			NodeList intermediate = (NodeList) XPathFactory.newInstance().newXPath().evaluate(search, getOrigin(), XPathConstants.NODESET);
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
	
	private static UserAgentContext createUserAgentContext() {
		SimpleUserAgentContext uacontext = new SimpleUserAgentContext();
		uacontext.setScriptingEnabled(false);
		uacontext.setExternalCSSEnabled(false);
		return uacontext;
	}
	
	private static Document createEmptyDocument() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) { return null; }
	}
		
}
