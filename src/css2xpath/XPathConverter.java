package css2xpath;

import java.io.CharArrayReader;
import java.io.IOException;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

public class XPathConverter {
	private Selector selector;
	
	public XPathConverter(Parser parser, String css) throws CSSException, IOException {
		SelectorList selectors = parser.parseSelectors(new InputSource(new CharArrayReader(css.toCharArray())));
		if (selectors.getLength() > 1) {
			throw new RuntimeException("Expected only one CSS selector, got " + selectors.getLength());
		}
		this.selector = selectors.item(0);
	}
	
	public XPathConverter(Selector selector) {
		this.selector = selector;
	}
	
	public Selector getSelector() { return this.selector; }
	
	public String toXPath() {
		switch(this.selector.getSelectorType()) {
		case Selector.SAC_ANY_NODE_SELECTOR:
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
		case Selector.SAC_CHILD_SELECTOR:
		case Selector.SAC_COMMENT_NODE_SELECTOR:
		case Selector.SAC_CONDITIONAL_SELECTOR:
		case Selector.SAC_DESCENDANT_SELECTOR:
		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
		case Selector.SAC_ELEMENT_NODE_SELECTOR:
		case Selector.SAC_NEGATIVE_SELECTOR:
		case Selector.SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR:
		case Selector.SAC_PSEUDO_ELEMENT_SELECTOR:
		case Selector.SAC_ROOT_NODE_SELECTOR:
		case Selector.SAC_TEXT_NODE_SELECTOR:
		default: return "";
		}
	}
}
