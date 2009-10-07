package org.lucho.server.guice;

import java.io.FileFilter;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.PDFFilter;
import org.lucho.server.SearchRemoteServiceImpl;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.IndexFilesImpl;
import org.lucho.server.lucene.SearchFiles;
import org.lucho.server.lucene.SearchFilesImpl;
import org.lucho.server.upload.UploadServlet;

import com.google.inject.servlet.ServletModule;

public class GuiceServletModule extends ServletModule {
	
    @Override
    protected void configureServlets() {
        serve("/searchRemoteService").with(GuiceRemoteServiceServlet.class);
        serve("/uploadHandler").with(UploadServlet.class);

        bind(SearchRemoteService.class).to(SearchRemoteServiceImpl.class);
        bind(IndexFiles.class).to(IndexFilesImpl.class);
        bind(SearchFiles.class).to(SearchFilesImpl.class);
        bind(FileFilter.class).to(PDFFilter.class);
        bind(FileItemFactory.class).to(DiskFileItemFactory.class);
    }
}
