/**
 * RegexWhitespaceTokenizer.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo.whitespace.ae;

/*
 * #%L
 * whitespaceTokenizer
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

import gov.va.vinci.leo.ae.LeoBaseAnnotator;
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic whitespace tokenizer that uses regexes instead of a dfa.
 *
 * @author thomasginter
 */
public class RegexWhitespaceTokenizer extends LeoBaseAnnotator {

    protected Pattern number	= Pattern.compile("\\d+([.]\\d+)?");
    protected Pattern word		= Pattern.compile("[a-zA-Z\\-]+('(s|ed|t|nt))?");

    @LeoConfigurationParameter(description = "The output type for words. This is the full type name, including package.", mandatory = true)
    private String wordOutputType = null;

    @LeoConfigurationParameter(description = "The output type for tokens. This is the full type name, including package.", mandatory = true)
    private String tokenOutputType = null;

    @LeoConfigurationParameter(description = "The feature on output type to set the token type", mandatory = true)
    private String tokenOutputTypeFeature = null;

    public static final int TK_NONWHITESPACE	= 0;
    public static final int TK_NUMBER  			= 1;
    public static final int TK_LETTER  			= 2;
    public static final int TK_WORD				= 3;

    protected final static Logger log = Logger.getLogger(LeoUtils.getRuntimeClass().getCanonicalName());

    /**
     * @see gov.va.vinci.leo.ae.LeoBaseAnnotator#initialize(org.apache.uima.UimaContext)
     */
    @Override
    public void initialize(UimaContext aContext)
            throws ResourceInitializationException {
        super.initialize(aContext);
    }//initialize method

    /**
     * @see gov.va.vinci.leo.ae.LeoBaseAnnotator#annotate(JCas)
     */
    @Override
    public void annotate(JCas aJCas) throws AnalysisEngineProcessException {
        Type tokenOutputAnnotationType = aJCas.getTypeSystem().getType((String) getTokenOutputType());
        Feature negationFeature = tokenOutputAnnotationType.getFeatureByBaseName((String) getTokenOutputTypeFeature());

        String docText = aJCas.getDocumentText();
        Matcher m = null;

        //number tokens
        m = number.matcher(docText);
        while(m.find()) {
            Annotation a = addOutputAnnotation((String) getTokenOutputType(), aJCas, m.start(), m.end());
            a.setIntValue(negationFeature, TK_NUMBER);
        }//while

        //word tokens
        m = word.matcher(docText);
        while(m.find()) {
            Annotation w = addOutputAnnotation((String) getWordOutputType(), aJCas, m.start(), m.end());
            Annotation t = addOutputAnnotation((String) getTokenOutputType(), aJCas, m.start(), m.end());
            t.setIntValue(negationFeature, TK_WORD);
        }//while
    }//process method

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        //Define the Type System
        TypeDescription token = new TypeDescription_impl("gov.va.vinci.leo.whitespace.types.Token", "", "uima.tcas.Annotation");
        token.addFeature("TokenType", "", "uima.cas.Integer");

        LeoTypeSystemDescription ftsd = new LeoTypeSystemDescription();
        try {
            ftsd.addType(token)
                    .addType("gov.va.vinci.leo.whitespace.types.WordToken", "Annotates collections of letters", "uima.tcas.Annotation");
        } catch (Exception e) {
            log.warn("Exception occurred generating WhitespaceTypeSystem", e);
            throw new RuntimeException(e);
        }//catch
        return ftsd;
    }

    public String getWordOutputType() {
        return wordOutputType;
    }

    public RegexWhitespaceTokenizer setWordOutputType(String wordOutputType) {
        this.wordOutputType = wordOutputType;
        return this;
    }

    public String getTokenOutputType() {
        return tokenOutputType;
    }

    public RegexWhitespaceTokenizer setTokenOutputType(String tokenOutputType) {
        this.tokenOutputType = tokenOutputType;
        return this;
    }

    public String getTokenOutputTypeFeature() {
        return tokenOutputTypeFeature;
    }

    public RegexWhitespaceTokenizer setTokenOutputTypeFeature(String tokenOutputTypeFeature) {
        this.tokenOutputTypeFeature = tokenOutputTypeFeature;
        return this;
    }
}//RegexWhitespaceTokenizer class