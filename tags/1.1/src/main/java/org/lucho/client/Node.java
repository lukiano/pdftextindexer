package org.lucho.client;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public final class Node extends BaseTreeModel implements Serializable {
	
	private static final long serialVersionUID = -619275737820355862L;

	public void setText(String text) {
		set("text", text);
	}

	public String getText() {
		return get("text");
	}

	public static Node create(String text) {
		Node node = new Node();
		node.setText(text);
		return node;
	}

	public void setPath(String path) {
		set("path", path);
	}

	public String getPath() {
		return get("path");
	}

	public void hasChildren(boolean hasChildren) {
		set("hasChildren", hasChildren);
	}

	public boolean hasChildren() {
		return get("hasChildren");
	}

}
