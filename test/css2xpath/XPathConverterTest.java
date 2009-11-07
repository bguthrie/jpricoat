package css2xpath;

import org.junit.Test;
import static org.junit.Assert.*;
import com.steadystate.css.*;
import com.steadystate.css.parser.*;

public class XPathConverterTest {

	@Test
	public void cssClassToXPath() {
        assertXPath("//*[contains(concat(' ', @class, ' '), ' red ')]", ".red");
	}
	
	public void assertXPath(String expectedXPath, String css) {
		assertEquals(expectedXPath, new XPathConverter(new SACParserCSS2(), css).toXPath());
	}
	
}
