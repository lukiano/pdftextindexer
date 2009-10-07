package org.lucho.client;

import java.io.Serializable;

public final class Node implements Serializable {
	
	private static final long serialVersionUID = -619275737820355862L;

	private String text;
	
	private String path;
	
	private boolean hasChildren;
	
	private Node[] children;

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setChildren(Node[] children) {
		this.children = children;
	}

	public Node[] getChildren() {
		return children;
	}
	
	public static Node create(String text) {
		Node node = new Node();
		node.setText(text);
		return node;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void hasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

}
