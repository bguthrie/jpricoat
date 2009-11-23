package com.brianguthrie.jpricoat;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.lobobrowser.html.domimpl.DocumentFragmentImpl;
import org.lobobrowser.html.parser.HtmlParser;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class Jpricoat implements IJpricoat {
	
	private Node wrappedNode;
	
	public Jpricoat(Node node) {
		this.wrappedNode = node;
	}
	
	public Jpricoat(String urlString) {
		try {
			SimpleUserAgentContext context = new SimpleUserAgentContext();
			context.setScriptingEnabled(false);
			context.setExternalCSSEnabled(false);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			URL url = new URL(urlString);
			InputStream in = url.openStream();
			Reader reader = new InputStreamReader(in, "ISO-8859-1");
			HtmlParser parser = new HtmlParser(context, doc);
			parser.parse(reader);
			this.wrappedNode = doc;
			in.close();			
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public IJpricoat search(List<String> selectors) {
		try {
			Jpricoat coating = new Jpricoat(new DocumentFragmentImpl());
			for (String selector : normalizeSearchesAsXPath(selectors)) {
				NodeList foundNodes = (NodeList) XPathFactory.newInstance().newXPath().evaluate(selector, this.wrappedNode, XPathConstants.NODESET);
				coating.appendAllNodes(foundNodes);
			}
			return coating;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public IJpricoat search(String... selectors) {
		return this.search(new ArrayList<String>(Arrays.asList(selectors)));
	}

	private void appendAllNodes(NodeList nodesToAdd) {
		HumaneNodeList existingNodes = new HumaneNodeList(this.getChildNodes());
		HumaneNodeList newNodes = new HumaneNodeList(nodesToAdd);
		for (Node newNode : newNodes) {
			if (!existingNodes.contains(newNode)) {
				this.appendChild(newNode);
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
	
	public Iterator<Node> iterator() {
		return new HumaneNodeList(this.getChildNodes()).iterator();
	}
	
	//
	// Begin delegated methods. Ick.
	//

	public Node appendChild(Node newChild) throws DOMException {
		return wrappedNode.appendChild(newChild);
	}

	public Node cloneNode(boolean deep) {
		return wrappedNode.cloneNode(deep);
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return wrappedNode.compareDocumentPosition(other);
	}

	public NamedNodeMap getAttributes() {
		return wrappedNode.getAttributes();
	}

	public String getBaseURI() {
		return wrappedNode.getBaseURI();
	}

	public NodeList getChildNodes() {
		return wrappedNode.getChildNodes();
	}

	public Object getFeature(String feature, String version) {
		return wrappedNode.getFeature(feature, version);
	}

	public Node getFirstChild() {
		return wrappedNode.getFirstChild();
	}

	public Node getLastChild() {
		return wrappedNode.getLastChild();
	}

	public String getLocalName() {
		return wrappedNode.getLocalName();
	}

	public String getNamespaceURI() {
		return wrappedNode.getNamespaceURI();
	}

	public Node getNextSibling() {
		return wrappedNode.getNextSibling();
	}

	public String getNodeName() {
		return wrappedNode.getNodeName();
	}

	public short getNodeType() {
		return wrappedNode.getNodeType();
	}

	public String getNodeValue() throws DOMException {
		return wrappedNode.getNodeValue();
	}

	public Document getOwnerDocument() {
		return wrappedNode.getOwnerDocument();
	}

	public Node getParentNode() {
		return wrappedNode.getParentNode();
	}

	public String getPrefix() {
		return wrappedNode.getPrefix();
	}

	public Node getPreviousSibling() {
		return wrappedNode.getPreviousSibling();
	}

	public String getTextContent() throws DOMException {
		return wrappedNode.getTextContent();
	}

	public Object getUserData(String key) {
		return wrappedNode.getUserData(key);
	}

	public boolean hasAttributes() {
		return wrappedNode.hasAttributes();
	}

	public boolean hasChildNodes() {
		return wrappedNode.hasChildNodes();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return wrappedNode.insertBefore(newChild, refChild);
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return wrappedNode.isDefaultNamespace(namespaceURI);
	}

	public boolean isEqualNode(Node arg) {
		return wrappedNode.isEqualNode(arg);
	}

	public boolean isSameNode(Node other) {
		return wrappedNode.isSameNode(other);
	}

	public boolean isSupported(String feature, String version) {
		return wrappedNode.isSupported(feature, version);
	}

	public String lookupNamespaceURI(String prefix) {
		return wrappedNode.lookupNamespaceURI(prefix);
	}

	public String lookupPrefix(String namespaceURI) {
		return wrappedNode.lookupPrefix(namespaceURI);
	}

	public void normalize() {
		wrappedNode.normalize();
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return wrappedNode.removeChild(oldChild);
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return wrappedNode.replaceChild(newChild, oldChild);
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		wrappedNode.setNodeValue(nodeValue);
	}

	public void setPrefix(String prefix) throws DOMException {
		wrappedNode.setPrefix(prefix);
	}

	public void setTextContent(String textContent) throws DOMException {
		wrappedNode.setTextContent(textContent);
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return wrappedNode.setUserData(key, data, handler);
	}
}
