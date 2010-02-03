package org.lucho.server.guice;

import java.io.FileFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
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
import org.lucho.server.lucene.impl.IndexFilesAsync;
import org.lucho.server.lucene.impl.IndexFilesImpl;
import org.lucho.server.lucene.impl.LuceneFactoryImpl;
import org.lucho.server.lucene.impl.SearchFilesImpl;
import org.lucho.server.upload.UploadServlet;

import com.google.inject.Scopes;
import com.google.inject.name.Names;
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
        bind(IndexFiles.class).annotatedWith(Names.named("async")).to(IndexFilesAsync.class).in(Scopes.SINGLETON);
        bind(SearchFiles.class).to(SearchFilesImpl.class).in(Scopes.SINGLETON);
        bind(FileFilter.class).toInstance(new ExtensionFilter());
        bind(FileItemFactory.class).to(DiskFileItemFactory.class).in(Scopes.SINGLETON);
        bind(AnalyzerFactory.class).to(AnalyzerFactoryImpl.class).in(Scopes.SINGLETON);
        bind(LuceneFactory.class).to(LuceneFactoryImpl.class).in(Scopes.SINGLETON);
        bind(FileResolver.class).to(FileResolverImpl.class).in(Scopes.SINGLETON);
        Properties properties = new Properties();
        try {
			properties.load(this.getClass().getResourceAsStream("/org/lucho/server/connection.properties"));
	        bind(Properties.class).toInstance(properties);
		} catch (IOException e) {
			Logger.getLogger(GuiceServletModule.class).error("Unable to load properties file", e);
		}
        bind(ExecutorService.class).toInstance(Executors.newSingleThreadExecutor());
    }
}
