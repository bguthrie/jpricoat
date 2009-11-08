package css2xpath;

import java.io.CharArrayReader;
import java.io.IOException;
import org.w3c.css.sac.*;

/*
 * x * any element - 2
 * x E an element of type E - 1
 * x E[foo] an E element with a "foo" attribute - 2
 * x E[foo="bar"] an E element whose "foo" attribute value is exactly equal to "bar" - 2
 * x E[foo~="bar"] an E element whose "foo" attribute value is a list of whitespace-separated values, one of which is exactly equal to "bar" - 2
 * E[foo^="bar"] an E element whose "foo" attribute value begins exactly with the string "bar" - 3
 * E[foo$="bar"] an E element whose "foo" attribute value ends exactly with the string "bar" - 3
 * E[foo*="bar"] an E element whose "foo" attribute value contains the substring "bar" - 3
 * E[hfoo|="en"] an E element whose "foo" attribute has a hyphen-separated list of values beginning (from the left) with "en" - 2
 * E:root an E element, root of the document - 3
 * E:nth-child(n) an E element, the n-th child of its parent - 3
 * E:nth-last-child(n) an E element, the n-th child of its parent, counting from the last one - 3
 * E:nth-of-type(n) an E element, the n-th sibling of its type - 3
 * E:nth-last-of-type(n) an E element, the n-th sibling of its type, counting from the last one - 3
 * - E:first-child an E element, first child of its parent - 2
 * E:last-child an E element, last child of its parent - 3
 * E:first-of-type an E element, first sibling of its type - 3
 * E:last-of-type an E element, last sibling of its type - 3
 * E:only-child an E element, only child of its parent - 3
 * E:only-of-type an E element, only sibling of its type - 3
 * E:empty an E element that has no children (including text nodes) - 3
 * x E:link, E:visited an E element being the source anchor of a hyperlink of which the target is not yet visited (:link) or already visited (:visited) - 1
 * x E:active, E:hover, E:focus an E element during certain user actions - 1 and 2
 * E:target an E element being the target of the referring URI - 3
 * E:lang(fr) an element of type E in language "fr" (the document language specifies how language is determined) - 2
 * E:enabled, E:disabled a user interface element E which is enabled or disabled - 3
 * E:checked a user interface element E which is checked (for instance a radio-button or checkbox) - 3
 * E::first-line the first formatted line of an E element - 1
 * E::first-letter the first formatted letter of an E element - 1
 * E::before generated content before an E element - 2
 * E::after generated content after an E element - 2
 * x E.warning an E element whose class is "warning" (the document language specifies how class is determined). - 1
 * x E#myid an E element with ID equal to "myid". - 1
 * E:not(s) an E element that does not match simple selector s - 3
 * x E F an F element descendant of an E element - 1
 * x E > F an F element child of an E element - 2
 * x E + F an F element immediately preceded by an E element - 2
 * E ~ F an F element preceded by an E element - 3
 */
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
		if (getSelector() == null) return prefix;
		
		switch(this._selector.getSelectorType()) {
		case Selector.SAC_CONDITIONAL_SELECTOR:
			ConditionalSelector selector = (ConditionalSelector) getSelector();
			return new XPathConverter(selector.getSimpleSelector()).toXPath(prefix) + "[" + conditionalSelectorToXPath(selector.getCondition()) + "]";
		case Selector.SAC_ANY_NODE_SELECTOR:
			throw new RuntimeException("Any node selector not supported");
		case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
			throw new RuntimeException("CDATA node selector");
		case Selector.SAC_CHILD_SELECTOR:
			return childSelectorToXPath((DescendantSelector) getSelector(), prefix);
		case Selector.SAC_COMMENT_NODE_SELECTOR:
			throw new RuntimeException("Comment node selector not supported");
		case Selector.SAC_DESCENDANT_SELECTOR:
			return descendantSelectorToXPath((DescendantSelector) getSelector(), prefix);
		case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
			return directAdjacentSelectorToXPath((SiblingSelector) getSelector(), prefix);
		case Selector.SAC_ELEMENT_NODE_SELECTOR:
			return elementNodeSelectorToXPath((ElementSelector) getSelector(), prefix);
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

	private String directAdjacentSelectorToXPath(SiblingSelector selector, String prefix) {
		return new XPathConverter(selector.getSelector()).toXPath(prefix) + 
			"/following-sibling::*[1]/self::" +
			new XPathConverter(selector.getSiblingSelector()).toXPath("");
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
			return andConditionToXPath((CombinatorCondition) condition);
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
			return oneOfAttributeConditionToXPath((AttributeCondition) condition);
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
	
	private String andConditionToXPath(CombinatorCondition condition) {
		return conditionalSelectorToXPath(condition.getFirstCondition()) + " and " + conditionalSelectorToXPath(condition.getSecondCondition());
	}

	private String oneOfAttributeConditionToXPath(AttributeCondition condition) {
		return "contains(concat(\" \", @" + condition.getLocalName() + ", \" \"),concat(\" \", '" + condition.getValue() + "', \" \"))";
	}

	private String classConditionToXPath(AttributeCondition condition) {
		return "contains(concat(' ', @class, ' '), ' " + ((AttributeCondition) condition).getValue() + " ')";
	}

	private String idConditionToXPath(AttributeCondition condition) {
		return "@id = '" + condition.getValue() + "'";
	}

	private String attributeConditionToXPath(AttributeCondition condition) {
		if (condition.getValue() == null) {
			return "@" + condition.getLocalName();
		} else {
			return "@" + condition.getLocalName() + " = '" + condition.getValue() + "'";
		}
	}
	
	private enum PseudoClass {
		FIRST("first"), FIRST_CHILD("first-child"), LAST("last"), NONE("");
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
		case FIRST_CHILD:
			return "position() = 1 and self::";
		default: 
			return condition.getValue() + "(.)"; 
		}
	}

}
