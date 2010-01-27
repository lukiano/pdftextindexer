package org.lucho.server.guice;

import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.ExtensionFilter;
import org.lucho.server.FileResolver;
import org.lucho.server.SearchRemoteServiceImpl;
import org.lucho.server.lucene.AnalyzerFactory;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.LuceneFactory;
import org.lucho.server.lucene.SearchFiles;
import org.lucho.server.lucene.impl.AnalyzerFactoryImpl;
import org.lucho.server.lucene.impl.FileResolverImpl;
import org.lucho.server.lucene.impl.IndexFilesImpl;
import org.lucho.server.lucene.impl.LuceneFactoryImpl;
import org.lucho.server.lucene.impl.SearchFilesImpl;
import org.lucho.server.upload.UploadServlet;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class GuiceServletModule extends ServletModule {
	
    @Override
    protected void configureServlets() {
        serve("/searchRemoteService").with(GuiceRemoteServiceServlet.class);
        serve("/uploadHandler").with(UploadServlet.class);
        serve("/main/searchRemoteService").with(GuiceRemoteServiceServlet.class);
        serve("/main/uploadHandler").with(UploadServlet.class);

        bind(SearchRemoteService.class).to(SearchRemoteServiceImpl.class);
        bind(IndexFiles.class).to(IndexFilesImpl.class).in(Scopes.SINGLETON);
        bind(SearchFiles.class).to(SearchFilesImpl.class).in(Scopes.SINGLETON);
        bind(FileFilter.class).toInstance(new ExtensionFilter());
        bind(FileItemFactory.class).to(DiskFileItemFactory.class).in(Scopes.SINGLETON);
        bind(AnalyzerFactory.class).to(AnalyzerFactoryImpl.class).in(Scopes.SINGLETON);
        bind(LuceneFactory.class).to(LuceneFactoryImpl.class).in(Scopes.SINGLETON);
        bind(FileResolver.class).to(FileResolverImpl.class).in(Scopes.SINGLETON);
        Properties properties = new Properties();
        try {
			properties.load(this.getClass().getResourceAsStream("/org/lucho/server/connection.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        bind(Properties.class).toInstance(properties);
        
    }
}
