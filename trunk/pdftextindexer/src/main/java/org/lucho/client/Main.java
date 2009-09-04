package org.lucho.client;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Main implements EntryPoint {

	public void onModuleLoad() {
		Window window = new Window();
		Button button = new Button("hello");
		Listener<ButtonEvent> listener = new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent be) {
			}
		};
		window.addButton(button);
		button.addListener(Events.OnClick, listener);
		window.setTitle("Text Indexer");

		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "/uploadHandler");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		VerticalPanel panel = new VerticalPanel();
		form.setWidget(panel);

		// Create a FileUpload widget.
		FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");

		panel.add(upload);

		Button submitButton = new Button("Submit");
		submitButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				form.submit();
			}
		});
		// Add a 'submit' button.
		panel.add(submitButton);

		// Add an event handler to the form.
		form.addFormHandler(new FormHandler() {

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).
				com.google.gwt.user.client.Window.alert(event.getResults());
			}

			public void onSubmit(FormSubmitEvent event) {
				// TODO Auto-generated method stub

			}
		});

		window.add(form);
		RootPanel.get().add(window);
	}

}
