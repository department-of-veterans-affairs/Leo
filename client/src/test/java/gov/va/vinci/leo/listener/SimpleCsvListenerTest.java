package gov.va.vinci.leo.listener;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimpleCsvListenerTest {
	CAS cas = null;
	

	@Before
	public void setup() throws Exception {
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(new WhitespaceTokenizer().getLeoAEDescriptor()
				.setParameterSetting("tokenOutputType", Token.class.getCanonicalName())
				.setParameterSetting("wordOutputType", WordToken.class.getCanonicalName())
				.setParameterSetting("tokenOutputTypeFeature", 3)
				.setTypeSystemDescription(new WhitespaceTokenizer().getLeoTypeSystemDescription())
				.getAnalysisEngineDescription());
		cas = CasCreationUtils.createCas(ae.getAnalysisEngineMetaData());
		cas.setDocumentText("012345678901234567890123456789");
		Map<String, Object> featureMap = new HashMap<String, Object>();
		featureMap.put("TokenType", 1);
	    addOutputAnnotation(Token.class.getCanonicalName(), cas.getJCas(), 1, 5, featureMap );
	
	}
	
	@Test 
	public void testSimple() throws IOException {
        File f = File.createTempFile("testSimple", "txt");
		SimpleCsvListener listener = new SimpleCsvListener(f, Token.class.getCanonicalName(), WordToken.class.getCanonicalName());
		listener.entityProcessComplete(cas, null);
        String b = FileUtils.readFileToString(f).trim();
		assertEquals("\"DocumentId\"\t\"Start\"\t\"End\"\t\"Type\"\t\"CoveredText\"\n" +
				"\t\"1\"\t\"5\"\t\"gov.va.vinci.leo.whitespace.types.Token\"\t\"1234\"", b.toString());
	}

	@Test
	public void testSimple2() throws IOException {
		File f = File.createTempFile("testSimple", "txt");
		SimpleCsvListener listener = new SimpleCsvListener(f, false, '|', true, Token.class.getCanonicalName(), WordToken.class.getCanonicalName());
		listener.entityProcessComplete(cas, null);
		String b = FileUtils.readFileToString(f).trim();
		assertEquals("|\"1\"|\"5\"|\"gov.va.vinci.leo.whitespace.types.Token\"|\"1234\"|\"[ TokenType = 1]\"", b.toString());
	}


	/**
	 * Create an output annotation, and add it to the cas.
	 *
	 * @param outputType The classname of the type. ie- gov.va.vinci.Token
	 * @param cas        The cas to add the output annotation to.
	 * @param begin      the start index of the annotation
	 * @param end        the end index of the annotation
	 * @param  featureNameValues a map of feature names and associated values to set on the annotation.
	 * @return  the created annotation.
	 * @throws org.apache.uima.analysis_engine.AnalysisEngineProcessException If any exception occurs when getting and instantiating the
	 *                                        output annotation type, this exception is thrown with the real exception inside of it. (analysisEngineProcessException.getCause());
	 */
	protected Annotation addOutputAnnotation(String outputType, JCas cas,
											 int begin, int end, Map<String, Object> featureNameValues) throws AnalysisEngineProcessException {
		try {
			// Get an instance of the outputType class
			Class<?> outputTypeClass = Class.forName(outputType);
			Constructor<?> con1 = outputTypeClass.getConstructor(JCas.class);
			Annotation outputAnnotation = (Annotation) con1.newInstance(cas);
			// Set the window annotation range
			outputAnnotation.setBegin(begin);
			outputAnnotation.setEnd(end);
			outputAnnotation.addToIndexes();

			/**
			 * Put in feature values if a map is passed in.
			 */
			if (featureNameValues != null) {
				for (String featureName : featureNameValues.keySet()) {
					Feature feature = outputAnnotation.getType().getFeatureByBaseName(featureName);
					if (featureNameValues.get(featureName) instanceof Boolean) {
						outputAnnotation.setBooleanValue(feature, (Boolean) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Byte) {
						outputAnnotation.setByteValue(feature, (Byte) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Double) {
						outputAnnotation.setDoubleValue(feature, (Double) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Float) {
						outputAnnotation.setFloatValue(feature, (Float) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Integer) {
						outputAnnotation.setIntValue(feature, (Integer) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Long) {
						outputAnnotation.setLongValue(feature, (Long) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof Short) {
						outputAnnotation.setShortValue(feature, (Short) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof String) {
						outputAnnotation.setStringValue(feature, (String) featureNameValues.get(featureName));
					} else if (featureNameValues.get(featureName) instanceof FeatureStructure) {
						outputAnnotation.setFeatureValue(feature, (FeatureStructure) featureNameValues.get(featureName));
					} else {
						throw new AnalysisEngineProcessException(
								new RuntimeException("Unknown feature type in map for feature name: " + featureName
										+ " value: " + featureNameValues.get(featureName)));
					}
				}
			}
			return outputAnnotation;
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
