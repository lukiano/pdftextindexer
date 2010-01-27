package org.lucho.server.lucene.impl;

import java.util.Properties;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.UserAuthenticator;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.lucho.server.FileResolver;

import com.google.inject.Inject;

public class FileResolverImpl implements FileResolver {
	
	@Inject
	private Properties properties;
	
	public FileObject getBaseFolder() throws FileSystemException {
		return VFS.getManager().resolveFile(properties.getProperty("url"), getOperations());
	}

	private FileSystemOptions getOperations() throws FileSystemException {
		FileSystemOptions fileSystemOptions = new FileSystemOptions();
		UserAuthenticator userAuthenticator = new StaticUserAuthenticator("", properties.getProperty("username"), properties.getProperty("password"));
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, userAuthenticator);
		return fileSystemOptions;
	}

	public FileObject getFile(final String url) throws FileSystemException {
		return VFS.getManager().resolveFile(url, getOperations());
	}

	public FileObject getFile(final FileObject baseFile, final String name) throws FileSystemException {
		return VFS.getManager().resolveFile(baseFile, name);
	}

}
