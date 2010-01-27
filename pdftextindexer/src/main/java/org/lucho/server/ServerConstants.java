package org.lucho.server;

public final class ServerConstants {

	public static final String INDEX_DIR = System.getProperty("java.io.tmpdir") + "/pdfti/indexes";
	
	public static final String SUGGEST_INDEX_DIR = System.getProperty("java.io.tmpdir") + "/pdfti/suggest";

	public static final String CONTENTS_FIELD = "contents";

	public static final String PATH_FIELD = "path";
	
	private ServerConstants() {}
}
