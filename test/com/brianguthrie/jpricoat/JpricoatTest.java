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
		assertEquals(0, new Jpricoat(empty).search("body").getLength());
	}
	
	@Test
	public void searchShouldReturnOneElement() throws Exception {
		Node test = node("<div class=\"content\">Some content</div>");
		assertEquals(1, new Jpricoat(test).search("div.content").getLength());
	}
	
	@Test
	public void searchShouldReturnEveryElement() throws Exception {
		Node test = node("<a href=\"http://www.google.com\">Google!</a><a href=\"http://www.thoughtworks.com\">ThoughtWorks!</a>");
		assertEquals(2, new Jpricoat(test).search("a").getLength());
	}
	
	@Test
	public void searchShouldActOnParent() throws Exception {
		Node root = node("" + 
			"<div class=\"links\">" + 
				"<a href=\"http://www.google.com\">Google!</a>" + 
				"<a href=\"http://www.thoughtworks.com\">ThoughtWorks!</a>" +
			"</div>");
		Jpricoat rootSearch = new Jpricoat(root).search("div.links");
		assertEquals(1, rootSearch.getLength());
		assertEquals(2, rootSearch.search("a").getLength());
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
