package css2xpath;

import org.junit.Test;
import static org.junit.Assert.*;
import com.steadystate.css.*;
import com.steadystate.css.parser.*;

public class XPathConverterTest {

	@Test
	public void elementToXPath() {
		assertXPath("//a", "a");
	}
	
	@Test
	public void classToXPath() {
        assertXPath("//*[contains(concat(' ', @class, ' '), ' red ')]", ".red");
	}
	
	@Test
	public void idToXPath() {
		assertXPath("//@id = 'navbar'", "#navbar");
	}
	
	@Test
	public void attributeEqualityToXPath() {
		assertXPath("//a[@href = 'http://google.com']", "a[href='http://google.com']");
	}
	
	public void assertXPath(String expectedXPath, String css) {
		assertEquals(expectedXPath, new XPathConverter(new SACParserCSS2(), css).toXPath());
	}
	
}
