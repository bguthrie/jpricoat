package com.brianguthrie.jpricoat;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Ignore;
import org.junit.Test;
import org.lobobrowser.html.parser.HtmlParser;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.*;

public class JpricoatTest {
	
	@Test
	public void searchShouldBeEmptyGivenEmptyDocument() throws Exception {
		Document empty = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		assertEquals(0, new Jpricoat(empty).search("body").allNodes().getLength());
	}
	
	@Test
	public void searchShouldReturnOneElement() throws Exception {
		Node test = node("<div class=\"content\">Some content</div>");
		assertEquals(1, new Jpricoat(test).search("div.content").allNodes().getLength());
	}
	
	@Test
	public void searchShouldReturnEveryElement() throws Exception {
		Node test = node("<a href=\"http://www.google.com\">Google!</a><a href=\"http://www.thoughtworks.com\">ThoughtWorks!</a>");
		assertEquals(2, new Jpricoat(test).search("a").allNodes().getLength());
	}
	
	@Test
	public void searchShouldActOnParent() throws Exception {
		Node root = node("" + 
			"<div class=\"links\">" + 
				"<a href=\"http://www.google.com\">Google!</a>" + 
				"<a href=\"http://www.thoughtworks.com\">ThoughtWorks!</a>" +
			"</div>");
		Jpricoat rootSearch = new Jpricoat(root).search("div.links");
		assertEquals(1, rootSearch.allNodes().getLength());
		assertEquals(2, rootSearch.search("a").allNodes().getLength());
	}
	
	@Test
	public void searchShouldHandleMultipleSelectors() throws Exception {
		Node root = node("" +
				"<div class=\"content\">" +
					"<h1>A heading!</h1>" +
					"<h2>A subheading!</h2>" +
				"</div>");
		Jpricoat search = new Jpricoat(root).search("div.content").search("h1", "h2");
		assertEquals(2, search.allNodes().getLength());
	}
	
   @Test
   public void searchShouldNotReturnDuplicateNodes() throws Exception {
       Node root = node("" +
               "<div class=\"content\">" +
                       "<h1 class=\"heading\">A heading!</h1>" +
               "</div>");
       Jpricoat search = new Jpricoat(root).search(".content").search("h1", ".heading");
       assertEquals(1, search.allNodes().getLength());
   }
	
	private Node node(String markup) throws Exception {
		SimpleUserAgentContext uacontext = new SimpleUserAgentContext();
		uacontext.setScriptingEnabled(false);
		uacontext.setExternalCSSEnabled(false);
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Reader source = new StringReader("<html><head><title>A document</title></head><body>" + markup + "</body></html>");
		new HtmlParser(uacontext, doc).parse(source);
		return doc;
	}	
}
