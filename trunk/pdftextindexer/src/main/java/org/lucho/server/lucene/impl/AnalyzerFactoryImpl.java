package org.lucho.server.lucene.impl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.lucho.server.lucene.AnalyzerFactory;

public class AnalyzerFactoryImpl implements AnalyzerFactory {

	public Analyzer getAnalyzer() {
		return new SnowballAnalyzer("English",
				(String[])StopAnalyzer.ENGLISH_STOP_WORDS_SET.toArray());
	}

}
