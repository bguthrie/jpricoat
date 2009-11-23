package com.brianguthrie.jpricoat;

import java.util.List;

import org.w3c.dom.Node;

public interface IJpricoat extends Node {
	
	public IJpricoat search(List<String> selectors);
	public IJpricoat search(String... selectors);
	
}
