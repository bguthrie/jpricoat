package com.brianguthrie.jpricoat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * A Jpricoat object begins with a URL or a node that you can search with CSS or
 * XPath expressions to narrow down your list of nodes. The search method returns
 * a new Jpricoat with an empty document as its root and the found nodes appended
 * as children. Successive searches are restricted to those child nodes.
 *
 * Usage: <code>for (Node n : new Jpricoat("http://somesite.com").search(".some_link")) { ... }
 * 
 * Jpricoat objects are immutable, although the lists they return may not be.
 */
public class Jpricoat implements Iterable<Node> {
	
	private Node rootNode;
	
	/**
	 * Use the given node as the root for this Jpricoat object.
	 */
	public Jpricoat(Node node) {
		this.rootNode = node;
	}
	
	/**
	 * Retrieve, parse, and use the document at the given URL string as the root for this Jpricoat object.
	 */
	public Jpricoat(String urlString) {
		try {
			Document doc = buildNewDocument();
			InputStream in = new URL(urlString).openStream();
			HtmlParser parser = new HtmlParser(buildParserContext(), doc);
			parser.parse(new InputStreamReader(in));
			this.rootNode = doc;
			in.close();			
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Given a list of CSS or XPath selectors, returns a new Jpricoat object representing
	 * the results of the search.
	 */
	public Jpricoat search(List<String> selectors) {
		try {
			Jpricoat coating = new Jpricoat(new DocumentFragmentImpl());
			for (String selector : normalizeSearchesAsXPath(selectors)) {
				NodeList foundNodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(selector, this.rootNode, XPathConstants.NODESET);
				coating.appendAllNodes(foundNodes);
			}
			return coating;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Given a list of CSS or XPath selectors, return a new Jpricoat object representing
	 * the results of the search.
	 */
	public Jpricoat search(String... selectors) {
		return this.search(new ArrayList<String>(Arrays.asList(selectors)));
	}
	
	/**
	 * Returns an iterator for nodes represented by this object. Iterator is of type
	 * HumaneNodeList.
	 */
	public Iterator<Node> iterator() {
		return allNodes().iterator();
	}
	
	/**
	 * Returns the list of all nodes represented by this Jpricoat object.
	 */
	public HumaneNodeList allNodes() {
		return new HumaneNodeList(rootNode.getChildNodes());
	}

	private void appendAllNodes(NodeList nodesToAdd) {
		HumaneNodeList existingNodes = new HumaneNodeList(rootNode.getChildNodes());
		HumaneNodeList newNodes = new HumaneNodeList(nodesToAdd);
		for (Node newNode : newNodes) {
			if (!existingNodes.contains(newNode)) {
				rootNode.appendChild(newNode);
			}
		}
	}

	private static List<String> normalizeSearchesAsXPath(List<String> selectors) {
		List<String> normalizedPaths = new ArrayList<String>();
		for(String search : selectors) {
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
		
	private UserAgentContext buildParserContext() {
		SimpleUserAgentContext context = new SimpleUserAgentContext();
		context.setScriptingEnabled(false);
		context.setExternalCSSEnabled(false);
		return context;
	}
	
	private Document buildNewDocument() throws ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	}

}
