package css2xpath;

import java.io.CharArrayReader;
import java.io.IOException;

import org.w3c.css.sac.*;

public class XPathConverter {
	private Selector selector;
	
	public XPathConverter(Parser parser, String css) throws CSSException {
		SelectorList selectors = null;
		try {
			selectors = parser.parseSelectors(new InputSource(new CharArrayReader(css.toCharArray())));
			if (selectors.getLength() != 1) {
				throw new CSSException("Expected only one CSS selector, got " + selectors.getLength() + " for selector " + css);
			}
		} catch(IOException e) { }
		this.selector = selectors.item(0);
	}
	
	public XPathConverter(Selector selector) {
		this.selector = selector;
	}
	
	public Selector getSelector() { return this.selector; }
	
	public String toXPath() {
		switch(this.selector.getSelectorType()) {
		case Selector.SAC_CONDITIONAL_SELECTOR:
			return conditionalSelectorToXPath();
		case Selector.SAC_ANY_NODE_SELECTOR:
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
		case Selector.SAC_CHILD_SELECTOR:
		case Selector.SAC_COMMENT_NODE_SELECTOR:
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

	private String conditionalSelectorToXPath() {
		ConditionalSelector selector = (ConditionalSelector) this.selector;
		Condition condition = selector.getCondition();
		
		switch(condition.getConditionType()) {
		case Condition.SAC_CLASS_CONDITION:
			String value = ((AttributeCondition) condition).getValue();
			return "//*[contains(concat(' ', @class, ' '), ' " + value + " ')]";
		default: return "";
		}
	}
}
