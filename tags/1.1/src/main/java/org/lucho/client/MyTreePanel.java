package org.lucho.client;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class MyTreePanel<M extends ModelData> extends TreePanel<M> {
	
	public MyTreePanel(TreeStore<M> store) {
		super(store);
	}

	@SuppressWarnings("unchecked")
	protected void onDoubleClick(TreePanelEvent tpe) {
		  super.onDoubleClick(tpe);
		  fireEvent(Events.DoubleClick, tpe);
	}

}
