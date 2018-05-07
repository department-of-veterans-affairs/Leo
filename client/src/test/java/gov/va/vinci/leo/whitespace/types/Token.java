

/* First created by JCasGen Mon Aug 08 14:23:56 MDT 2016 */
package gov.va.vinci.leo.whitespace.types;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 08 14:23:56 MDT 2016
 * XML source: /var/folders/9p/6qt9dlgd1ks8d036hkqb9kz00000gn/T/leoTypeDescription_39e12376-aec5-4acf-a70c-afa4b7cffaa81105431736418235892.xml
 * @generated */
public class Token extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Token.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Token(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: TokenType

  /** getter for TokenType - gets 
   * @generated
   * @return value of the feature 
   */
  public int getTokenType() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TokenType == null)
      jcasType.jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.whitespace.types.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_TokenType);}
    
  /** setter for TokenType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTokenType(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TokenType == null)
      jcasType.jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.whitespace.types.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_TokenType, v);}    
   
    
  //*--------------*
  //* Feature: AnnotationFeature

  /** getter for AnnotationFeature - gets 
   * @generated
   * @return value of the feature 
   */
  public Annotation getAnnotationFeature() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_AnnotationFeature == null)
      jcasType.jcas.throwFeatMissing("AnnotationFeature", "gov.va.vinci.leo.whitespace.types.Token");
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Token_Type)jcasType).casFeatCode_AnnotationFeature)));}
    
  /** setter for AnnotationFeature - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationFeature(Annotation v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_AnnotationFeature == null)
      jcasType.jcas.throwFeatMissing("AnnotationFeature", "gov.va.vinci.leo.whitespace.types.Token");
    jcasType.ll_cas.ll_setRefValue(addr, ((Token_Type)jcasType).casFeatCode_AnnotationFeature, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    