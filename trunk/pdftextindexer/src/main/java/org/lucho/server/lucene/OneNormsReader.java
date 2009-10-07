package org.lucho.server.lucene;

import java.io.IOException;

import org.apache.lucene.index.FilterIndexReader;
import org.apache.lucene.index.IndexReader;

/**
 * Use the norms from one field for all fields. Norms are read into memory,
 * using a byte of memory per document per searched field. This can cause search
 * of large collections with a large number of fields to run out of memory. If
 * all of the fields contain only a single token, then the norms are all
 * identical, then single norm vector may be shared.
 */
class OneNormsReader extends FilterIndexReader {
	
	private String field;

	public OneNormsReader(IndexReader in, String field) {
		super(in);
		this.field = field;
	}

	public byte[] norms(String field) throws IOException {
		return in.norms(this.field);
	}
}