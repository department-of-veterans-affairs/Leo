package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * User: Thomas Ginter
 * Date: 11/1/13
 * Time: 1:35 PM
 */
public class ExampleWhitespaceTokenizer extends LeoBaseAnnotator {
    /**
     * Set of stop words to exclude from the document results
     */
    private HashSet<String> stopWordSet = new HashSet<String>();

    /**
     * Token type definitions
     */
    public static final int TK_SPECIAL 		= 0;
    public static final int TK_NUMBER  		= 1;
    public static final int TK_LETTER  		= 2;
    public static final int TK_WORD    		= 3;
    public static final int TK_WHITESPACE 	= 4;
    public static final int TK_PUNCTUATION	= 5;
    public static final int TK_NEWLINE		= 6;
    public static final int TK_CONTROL		= 7;
    public static final int TK_IDENTIFIER	= 8;
    public static final int TK_INVALID		= -1;
    public static final int TK_UNKNOWN		= -2;


    /**
     * Current token type being processed
     */
    private int mTokenType;

    private boolean filterStopWords = false;

    /**
     * The output type for tokens. This is the full type name, including package.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected String tokenOutputType = null;


    @LeoConfigurationParameter(mandatory = true)
    protected String tokenOutputTypeFeature;

    /**
     * The output type for words. This is the full type name, including package.
     */
    @LeoConfigurationParameter(mandatory = true)
    protected String wordOutputType = null;

    /**
     * The full path to a stop words file. Words in the stop word file are not annotated as word token if found
     * in the document.
     */
    @LeoConfigurationParameter
    protected String stopWordsFile = null;

    protected final static Logger logger = Logger.getLogger(ExampleWhitespaceTokenizer.class.getCanonicalName());

    /**
     * Type system constants
     */
    public static final String TOKEN_OUTPUT_TYPE_NAME         = "gov.va.vinci.leo.types.Token";
    public static final String TOKEN_OUTPUT_TYPE_FEATURE_NAME = "TokenType";
    public static final String WORD_OUTPUT_TYPE_NAME          = "gov.va.vinci.leo.types.WordToken";

    public ExampleWhitespaceTokenizer() {
        /** Default for UIMA Init **/
    }

    public ExampleWhitespaceTokenizer(String tokenOutputType, String tokenOutputTypeFeature, String wordOutputType, String stopWordsFile) {
        this.tokenOutputType = tokenOutputType;
        this.tokenOutputTypeFeature = tokenOutputTypeFeature;
        this.wordOutputType = wordOutputType;
        this.stopWordsFile = stopWordsFile;
    }

    /**
     * Read in the words from the stop words file
     *
     * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(org.apache.uima.UimaContext)
     */
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        if (StringUtils.isNotBlank(stopWordsFile))
        {
            //Read in the file and populate the set
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(getResourceAsInputStream(stopWordsFile)));
                String line = null;
                while((line = br.readLine()) != null) {
                    if(StringUtils.isNotBlank(line)) {
                        stopWordSet.add(line.toLowerCase().trim());
                    }//if
                }//while
                br.close();
                filterStopWords = true;
            } catch (Exception e) {
                throw new ResourceInitializationException(e);
            } finally {
                try { br.close(); } catch (IOException e) {
                    /** Just trying to close the handle **/
                    logger.warn("Could not close buffered reader: " + e);
                }
            }//finally
        }
    }//initialize method

    /**
     * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        super.process(aJCas);

        Feature tokenTypeFeature = aJCas.getTypeSystem().getType(tokenOutputType).getFeatureByBaseName(tokenOutputTypeFeature);

        char[] documentChars = aJCas.getDocumentText().toCharArray();
        int documentLength = documentChars.length;
        int tokenBegin = TK_UNKNOWN;
        char currCh = '0';
        int currPos = 0;
        this.mTokenType = TK_UNKNOWN;

        //Build tokens based on character classifications
        while (currPos < documentLength) {
            //get current and next characters
            currCh = documentChars[currPos];

            //get current character type
            int currChType = characterType(currCh);
            switch (currChType) {
                case TK_LETTER:
                    if (tokenBegin == TK_UNKNOWN) {
                        tokenBegin = currPos;
                        this.mTokenType = TK_WORD;
                    } else if(this.mTokenType == TK_NUMBER) {
                        //Create a Number Token
                        createToken(aJCas, tokenBegin, currPos, this.mTokenType, tokenTypeFeature);
                        //Identifier is a combination of letters and numbers
                        this.mTokenType = TK_IDENTIFIER;
                    }
                    break;
                case TK_NUMBER:
                    if(tokenBegin == TK_UNKNOWN) {
                        tokenBegin = currPos;
                        this.mTokenType = TK_NUMBER;
                    } else if (this.mTokenType == TK_WORD) {
                        //Token is an identifier if a combination of letters and numbers
                        this.mTokenType = TK_IDENTIFIER;
                    }
                    break;
                case TK_PUNCTUATION:
                    if(tokenBegin != TK_UNKNOWN) {
                        if (!(this.mTokenType == TK_WORD && currCh == '\'')) {
                            //not a contraction so end the current token, then create one for this punctuation
                            createToken(aJCas, tokenBegin, currPos, this.mTokenType, tokenTypeFeature);
                            tokenBegin = TK_UNKNOWN;
                            this.mTokenType = TK_UNKNOWN;
                        }//if not a contraction
                    }//if token already started
                    createToken(aJCas, currPos, currPos + 1, TK_PUNCTUATION, tokenTypeFeature);
                    break;
                case TK_CONTROL:
                case TK_UNKNOWN:
                    //End the current token if one is in progress then create token for current char
                    if(tokenBegin != TK_UNKNOWN) {
                        createToken(aJCas, tokenBegin, currPos, this.mTokenType, tokenTypeFeature);
                    }
                    createToken(aJCas, currPos, currPos + 1, currChType, tokenTypeFeature);
                    tokenBegin = TK_UNKNOWN;
                    this.mTokenType = TK_UNKNOWN;
                    break;
                default:
                    //end the current open token
                    if(tokenBegin != TK_UNKNOWN) {
                        createToken(aJCas, tokenBegin, currPos, this.mTokenType, tokenTypeFeature);
                        tokenBegin = TK_UNKNOWN;
                        this.mTokenType = TK_UNKNOWN;
                    }//if tokenBegin != TK_UNKNOWN
            }//switch
            currPos++;
        }//while currPos < documentLength

        //We reached the end of the document, close any open tokens
        if(tokenBegin != TK_UNKNOWN) {
            createToken(aJCas, tokenBegin, currPos, this.mTokenType, tokenTypeFeature);
            tokenBegin = TK_UNKNOWN;
            this.mTokenType = TK_UNKNOWN;
        }
    }//process method



    /**
     * Create a Token annotation in the CAS using the begin and end points, type feature
     * will also be set
     *
     * @param aJCas current CAS
     * @param begin start index of the annotation
     * @param end end index of the annotation
     * @param tokenType type of Token being created
     */
    private void createToken (JCas aJCas, int begin, int end, int tokenType, Feature tokenTypeFeature) throws AnalysisEngineProcessException {
        Annotation token = this.addOutputAnnotation(tokenOutputType, aJCas, begin, end);
        token.setIntValue(tokenTypeFeature, tokenType);



        //Create the word annotation if not in the stopWordSet
        if(tokenType == TK_WORD) {
            if(!filterStopWords) {
                this.addOutputAnnotation(wordOutputType, aJCas, begin, end);
            } else {
                String coveredText = token.getCoveredText();
                if(StringUtils.isNotBlank(coveredText) && !stopWordSet.contains(coveredText.toLowerCase().trim())) {
                    //Create the WordToken annotation
                    this.addOutputAnnotation(wordOutputType, aJCas, begin, end);
                }//if there is covered text and the word is not in the stopWordSet
            }//else
        }//if
    }//createToken method

    /**
     * Given a character c return the type definition from the
     * list of public static type definitions in this class.
     *
     * @param c
     * @return type definition for the character c
     */
    private static int characterType(char c) {
        switch (Character.getType(c)) {
            //letters
            case Character.UPPERCASE_LETTER:
            case Character.LOWERCASE_LETTER:
            case Character.TITLECASE_LETTER:
            case Character.MODIFIER_LETTER:
            case Character.OTHER_LETTER:
            case Character.NON_SPACING_MARK:
            case Character.ENCLOSING_MARK:
            case Character.COMBINING_SPACING_MARK:
            case Character.PRIVATE_USE:
            case Character.SURROGATE:
            case Character.MODIFIER_SYMBOL:
                return TK_LETTER;
            //numbers
            case Character.DECIMAL_DIGIT_NUMBER:
            case Character.LETTER_NUMBER:
            case Character.OTHER_NUMBER:
                return TK_NUMBER;
            //Regular Whitespace
            case Character.SPACE_SEPARATOR:
                return TK_WHITESPACE;
            //Punctuation
            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
                return TK_PUNCTUATION;
            //Simple NewLine
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
                return TK_NEWLINE;
            //Other types of "control" characters
            case Character.CONTROL:
                if (c == '\n' || c == '\r')
                    return TK_NEWLINE;
                if(Character.isWhitespace(c))  //Tab char is a "Control" character
                    return TK_WHITESPACE;
                return TK_CONTROL;
            default:
                if(Character.isWhitespace(c)) {
                    return TK_WHITESPACE;
                }//if
                return TK_UNKNOWN;
        }//switch
    }//characterType method


    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        LeoTypeSystemDescription ftsd = new LeoTypeSystemDescription();
        try {
            ftsd.addType(TypeDescriptionBuilder.create(TOKEN_OUTPUT_TYPE_NAME, "", "uima.tcas.Annotation")
                                               .addFeature(TOKEN_OUTPUT_TYPE_FEATURE_NAME, "", "uima.cas.Integer")
                                               .getTypeDescription())
                .addType(WORD_OUTPUT_TYPE_NAME, "Annotates collections of letters", "uima.tcas.Annotation");
        } catch (Exception e) {
            logger.warn("Exception occurred generating WhitespaceTypeSystem", e);
            throw new RuntimeException(e);
        }//catch
        return ftsd;
    }

}
