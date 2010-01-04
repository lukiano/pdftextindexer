package org.lucho.server.lucene.impl;

import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;
import org.lucho.server.lucene.AnalyzerFactory;

public class AnalyzerFactoryImpl implements AnalyzerFactory {

	public Analyzer getAnalyzer() {
		Set<?> stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET; 
		return new SnowballAnalyzer(Version.LUCENE_CURRENT, "English", stopWords.toArray(new String[stopWords.size()]));
	}

}
