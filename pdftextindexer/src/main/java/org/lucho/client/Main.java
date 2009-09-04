package org.lucho.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint {

	public void onModuleLoad() {
		// TODO Auto-generated method stub
		Button button = new Button("hello");
		Listener<ButtonEvent> listener = new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent be) {
				Window.alert(be.isRightClick()?"A Right click!":"Not a right click");
			}
		};
		button.addListener(Events.OnClick, listener);
		RootPanel.get().add(button);
	}

}
