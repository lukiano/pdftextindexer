package org.lucho.server;

public final class ServerConstants {

	public static final String INDEX_DIR = System.getProperty("java.io.tmpdir") + "/pdfti/indexes";
	
	public static final String SUGGEST_INDEX_DIR = System.getProperty("java.io.tmpdir") + "/pdfti/suggest";

	public static final String FILES_DIR = System.getProperty("java.io.tmpdir") + "/pdfti/files";

	public static final String CONTENTS_FIELD = "contents";

	public static final String PATH_FIELD = "path";
	public static final String METADATA_PATH_FIELD = "metadata_path";
	
	private ServerConstants() {}
}
