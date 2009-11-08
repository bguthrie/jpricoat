package css2xpath;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import com.steadystate.css.*;
import com.steadystate.css.parser.*;

public class XPathConverterTest {

	@Test
	public void anyNode() {
		assertXPath("//*", "*");
	}
	
	@Test
	public void singleElement() {
		assertXPath("//a", "a");
	}
	
	@Test
	public void singleClass() {
        assertXPath("//*[contains(concat(' ', @class, ' '), ' red ')]", ".red");
	}
	
	@Test
	public void singleId() {
		assertXPath("//*[@id = 'navbar']", "#navbar");
	}
	
	@Test
	public void elementWithAttribute() {
		assertXPath("//a[@href]", "a[href]");
		assertXPath("//a[@href = 'http://google.com']", "a[href='http://google.com']");
		assertXPath("//a[contains(concat(\" \", @href, \" \"),concat(\" \", 'http://google.com', \" \"))]", "a[href~='http://google.com']");
	}
	
	@Test
	public void multipleAttributeSelector() {
		assertXPath("//input[@type = 'text' and @name = 'login']", "input[type='text'][name='login']");
	}
		
	@Test
	public void classWithDescendantClass() {
		assertXPath("//*[contains(concat(' ', @class, ' '), ' red ')]//*[contains(concat(' ', @class, ' '), ' blue ')]", ".red .blue");
	}
	
	@Test
	public void classWithChildClass() {
		assertXPath("//ul/li", "ul > li");
	}
	
	@Test
	public void idWithDescendantClass() {
		assertXPath("//*[@id = 'black']//*[contains(concat(' ', @class, ' '), ' blue ')]", "#black .blue");
	}
	
	@Test 
	public void classWithDescendantElement() {
		assertXPath("//*[contains(concat(' ', @class, ' '), ' yellow ')]//p", ".yellow p");
	}
	
	@Test
	public void elementWithFirstPseudoClass() {
		assertXPath("//ul//li[position() = 1]", "ul li:first");
	}
	
	@Test
	public void andConditionSelector() {
		assertXPath("//*[contains(concat(' ', @class, ' '), ' some_class ') and position() = 1]", ".some_class:first");
	}
	
	@Ignore @Test
	public void elementWithFirstChildPseudoClass() {
		assertXPath("//ul[contains(concat(' ', @class, ' '), ' foo ')]//*[position() = 1 and self::li]", "ul.foo li:first-child");
	}
	
	@Test
	public void unadornedLastPseudoClass() {
		assertXPath("//*[position() = last()]", ":last");
	}
	
	@Test
	public void elementWithGenericPseudoClass() {
		assertXPath("//a[hover(.)]", "a:hover");
		assertXPath("//a[visited(.)]", "a:visited");
	}
	
	@Test
	public void directAdjacentElementSelector() {
		assertXPath("//p/following-sibling::*[1]/self::p", "p + p");
	}
	
	public void assertXPath(String expectedXPath, String css) {
		assertEquals(expectedXPath, new XPathConverter(new SACParserCSS2(), css).toXPath());
	}
	
}
