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
		return this.toXPath("//");
	}
	
	public String toXPath(String prefix) {
		switch(this.selector.getSelectorType()) {
		case Selector.SAC_CONDITIONAL_SELECTOR:
			return conditionalSelectorToXPath((ConditionalSelector) this.selector, prefix);
		case Selector.SAC_ANY_NODE_SELECTOR:
			throw new RuntimeException("Any node selector not supported");
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
			throw new RuntimeException("CDATA node selector");
		case Selector.SAC_CHILD_SELECTOR:
			throw new RuntimeException("Child node selector not supported");
		case Selector.SAC_COMMENT_NODE_SELECTOR:
			throw new RuntimeException("Comment node selector not supported");
		case Selector.SAC_DESCENDANT_SELECTOR:
			throw new RuntimeException("Descendant node selector not supported");
		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
			throw new RuntimeException("Direct adjacent node selector not supported");
		case Selector.SAC_ELEMENT_NODE_SELECTOR:
			return elementNodeSelectorToXPath(prefix);
		case Selector.SAC_NEGATIVE_SELECTOR:
			throw new RuntimeException("Negation selector not supported");
		case Selector.SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR:
			throw new RuntimeException("Processing instruction node selector not supported");
		case Selector.SAC_PSEUDO_ELEMENT_SELECTOR:
			throw new RuntimeException("Pesudo Element selector not supported");
		case Selector.SAC_ROOT_NODE_SELECTOR:
			throw new RuntimeException("Root node selector not supported");
		case Selector.SAC_TEXT_NODE_SELECTOR:
			throw new RuntimeException("Text node selector not supported");
		default: return "";
		}
	}

	private String elementNodeSelectorToXPath(String prefix) {
		ElementSelector selector = (ElementSelector) this.selector;
		return prefix + selector.getLocalName();
	}

	private String conditionalSelectorToXPath(ConditionalSelector selector, String prefix) {
		Condition condition = selector.getCondition();

		switch(condition.getConditionType()) {
		case Condition.SAC_AND_CONDITION:
			throw new RuntimeException("And condition not supported");
		case Condition.SAC_ATTRIBUTE_CONDITION:
			return attributeConditionSelectorToXPath(selector, (AttributeCondition) condition, prefix);
		case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION:
			throw new RuntimeException("Begin hyphen attribute condition not supported");
		case Condition.SAC_CLASS_CONDITION:
			return classConditionSelectorToXPath(selector, (AttributeCondition) condition, prefix);
		case Condition.SAC_CONTENT_CONDITION:
			throw new RuntimeException("Content condition not supported");
		case Condition.SAC_ID_CONDITION:
			return idConditionSelectorToXPath(selector, (AttributeCondition) condition, prefix); 
		case Condition.SAC_LANG_CONDITION:
			throw new RuntimeException("Lang condition not supported");
		case Condition.SAC_NEGATIVE_CONDITION:
			throw new RuntimeException("Negative condition not supported");
		case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION:
			throw new RuntimeException("One-of attribute condition not supported");
		case Condition.SAC_ONLY_CHILD_CONDITION:
			throw new RuntimeException("Only-child condition not supported");
		case Condition.SAC_ONLY_TYPE_CONDITION:
			throw new RuntimeException("Only-type condition not supported");
		case Condition.SAC_OR_CONDITION:
			throw new RuntimeException("Or condition not supported");
		case Condition.SAC_POSITIONAL_CONDITION:
			throw new RuntimeException("Positional condition not supported");
		case Condition.SAC_PSEUDO_CLASS_CONDITION:
			throw new RuntimeException("Pseudo-class condition not supported");
		default: return "";
		}
	}

	private String classConditionSelectorToXPath(ConditionalSelector selector, AttributeCondition condition, String prefix) {
		return prefix + "*[contains(concat(' ', @class, ' '), ' " + ((AttributeCondition) condition).getValue() + " ')]";
	}

	private String idConditionSelectorToXPath(ConditionalSelector selector, AttributeCondition condition, String prefix) {
		return prefix + "@id = '" + condition.getValue() + "'";
	}

	private String attributeConditionSelectorToXPath(ConditionalSelector selector, AttributeCondition condition, String prefix) {
		return prefix + new XPathConverter(selector.getSimpleSelector()).toXPath("") + "[@" + condition.getLocalName() + " = '" + condition.getValue() + "']";
	}
}
