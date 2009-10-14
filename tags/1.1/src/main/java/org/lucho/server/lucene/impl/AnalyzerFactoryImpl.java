package org.lucho.server.lucene.impl;

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.lucho.server.lucene.AnalyzerFactory;

public class AnalyzerFactoryImpl implements AnalyzerFactory {

	@SuppressWarnings("unchecked")
	public Analyzer getAnalyzer() {
		Set<String> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET; 
		return new SnowballAnalyzer("English", stopWords.toArray(new String[stopWords.size()]));
	}

}
