package css2xpath;

import java.io.CharArrayReader;
import java.io.IOException;

import org.w3c.css.sac.*;

import com.steadystate.css.parser.selectors.ChildSelectorImpl;

public class XPathConverter {
	private Selector _selector;
	
	public XPathConverter(Parser parser, String css) throws CSSException {
		SelectorList selectors = null;
		try {
			selectors = parser.parseSelectors(new InputSource(new CharArrayReader(css.toCharArray())));
			if (selectors.getLength() != 1) {
				throw new CSSException("Expected only one CSS selector, got " + selectors.getLength() + " for selector " + css);
			}
		} catch(IOException e) { }
		this._selector = selectors.item(0);
	}
	
	public XPathConverter(Selector selector) {
		this._selector = selector;
	}
	
	public Selector getSelector() { return this._selector; }
	
	public String toXPath() {
		return this.toXPath("//");
	}
	
	public String toXPath(String prefix) {
		if (this._selector == null) return prefix;
		
		switch(this._selector.getSelectorType()) {
		case Selector.SAC_CONDITIONAL_SELECTOR:
			ConditionalSelector selector = (ConditionalSelector) this._selector;
			return new XPathConverter(selector.getSimpleSelector()).toXPath(prefix) + "[" + conditionalSelectorToXPath(selector.getCondition()) + "]";
		case Selector.SAC_ANY_NODE_SELECTOR:
			throw new RuntimeException("Any node selector not supported");
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
			throw new RuntimeException("CDATA node selector");
		case Selector.SAC_CHILD_SELECTOR:
			return childSelectorToXPath((DescendantSelector) this._selector, prefix);
		case Selector.SAC_COMMENT_NODE_SELECTOR:
			throw new RuntimeException("Comment node selector not supported");
		case Selector.SAC_DESCENDANT_SELECTOR:
			return descendantSelectorToXPath((DescendantSelector) this._selector, prefix);
		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
			throw new RuntimeException("Direct adjacent node selector not supported");
		case Selector.SAC_ELEMENT_NODE_SELECTOR:
			return elementNodeSelectorToXPath((ElementSelector) this._selector, prefix);
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

	private String childSelectorToXPath(DescendantSelector selector, String prefix) {
		return new XPathConverter(selector.getAncestorSelector()).toXPath(prefix) + new XPathConverter(selector.getSimpleSelector()).toXPath("/");
	}

	private String descendantSelectorToXPath(DescendantSelector selector, String prefix) {
		return new XPathConverter(selector.getAncestorSelector()).toXPath(prefix) + new XPathConverter(selector.getSimpleSelector()).toXPath("//"); 
	}

	private String elementNodeSelectorToXPath(ElementSelector selector, String prefix) {
		return prefix + selector.toString();
	}

	private String conditionalSelectorToXPath(Condition condition) {
		switch(condition.getConditionType()) {
		case Condition.SAC_AND_CONDITION:
			throw new RuntimeException("And condition not supported");
		case Condition.SAC_ATTRIBUTE_CONDITION:
			return attributeConditionToXPath((AttributeCondition) condition);
		case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION:
			throw new RuntimeException("Begin hyphen attribute condition not supported");
		case Condition.SAC_CLASS_CONDITION:
			return classConditionToXPath((AttributeCondition) condition);
		case Condition.SAC_CONTENT_CONDITION:
			throw new RuntimeException("Content condition not supported");
		case Condition.SAC_ID_CONDITION:
			return idConditionToXPath((AttributeCondition) condition); 
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
			return pseudoClassConditionToXPath((AttributeCondition) condition);
		default: return "";
		}
	}
	
	private enum PseudoClass {
		FIRST("first"), LAST("last"), NONE("");
		private String className;
		private PseudoClass(String className) { this.className = className; }
		public static PseudoClass forName(String name) {
			for (PseudoClass pc : values()) {
				if (pc.className.equals(name)) return pc;
			}
			return NONE;
		}
	}
	
	private String pseudoClassConditionToXPath(AttributeCondition condition) {
		switch(PseudoClass.forName(condition.getValue())) {
		case FIRST: 
			return "position() = 1";
		case LAST:
			return "position() = last()";
		default: 
			return condition.getValue() + "(.)"; 
		}
	}

	private String classConditionToXPath(AttributeCondition condition) {
		return "contains(concat(' ', @class, ' '), ' " + ((AttributeCondition) condition).getValue() + " ')";
	}

	private String idConditionToXPath(AttributeCondition condition) {
		return "@id = '" + condition.getValue() + "'";
	}

	private String attributeConditionToXPath(AttributeCondition condition) {
		return "@" + condition.getLocalName() + " = '" + condition.getValue() + "'";
	}
}
