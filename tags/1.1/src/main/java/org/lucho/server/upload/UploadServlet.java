package org.lucho.server.upload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.lucho.client.Constants;
import org.lucho.server.lucene.IndexFiles;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UploadServlet extends HttpServlet {
	
	@Inject
	private ServletContext servletContext;
	
	@Inject
	private FileItemFactory fileItemFactory;

	@Inject
	private IndexFiles indexFiles;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4549942139175529422L;

	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType("text/html");

		FileItem[] items = getFileItem(req);
		if (items == null || items[0] == null || items[1] == null) {
			resp.getWriter().write("NO-SCRIPT-DATA");
			return;
		}
		String realFolder = this.getServletContext().getRealPath(items[1].getString());
		String targetName = stripPath(items[0].getName());
		File targetFile = new File(realFolder, targetName); 
		if (targetFile.exists() || !targetFile.createNewFile()) {
			resp.getWriter().write("Sorry, a file with that name already exists in the selected folder");
			return;
		}
		try {
			items[0].write(targetFile);
		} catch (Exception e) {
			e.printStackTrace();
			resp.getWriter().write("An exception occured. Message is: " + e.getMessage());
			return;
		}
		indexFiles.index(targetFile);
		resp.getWriter().write("File successfully uploaded");
	}

	String stripPath(String name) {
		// doc says that Opera may bring the full path. We only need the name
		if (name.lastIndexOf('\\') > 0)
			name = name.substring(name.indexOf('\\') + 1);
		if (name.lastIndexOf('/') > 0)
			name = name.substring(name.indexOf('/') + 1);
		return name;
	}

	@SuppressWarnings("unchecked")
	private FileItem[] getFileItem(final HttpServletRequest req) {
		ServletFileUpload upload = new ServletFileUpload(fileItemFactory);
		
		// 0: uploading file
		// 1: folder target
		FileItem[] result = new FileItem[2];

		try {
			List items = upload.parseRequest(req);
			Iterator it = items.iterator();

			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()
						&& Constants.UPLOAD_FIELD.equals(item.getFieldName())) {
					result[0] = item;
				}
				if (item.isFormField()
						&& Constants.HIDDEN_FIELD.equals(item.getFieldName())) {
					result[1] = item;
				}
			}

		} catch (FileUploadException e) {
			return null;
		}

		return result;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

}
