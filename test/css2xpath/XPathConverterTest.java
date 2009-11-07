package css2xpath;

import org.junit.Test;
import static org.junit.Assert.*;
import com.steadystate.css.*;
import com.steadystate.css.parser.*;

public class XPathConverterTest {

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
		assertXPath("//a[@href = 'http://google.com']", "a[href='http://google.com']");
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
	public void unadornedLastPseudoClass() {
		assertXPath("//*[position() = last()]", ":last");
	}
	
	@Test
	public void elementWithGenericPseudoClass() {
		assertXPath("//a[hover(.)]", "a:hover");
	}
	
	public void assertXPath(String expectedXPath, String css) {
		assertEquals(expectedXPath, new XPathConverter(new SACParserCSS2(), css).toXPath());
	}
	
}
