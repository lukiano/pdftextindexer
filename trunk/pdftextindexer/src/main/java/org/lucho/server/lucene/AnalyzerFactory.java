package org.lucho.server.lucene;

import org.apache.lucene.analysis.Analyzer;

public interface AnalyzerFactory {

	Analyzer getAnalyzer();
	
}
