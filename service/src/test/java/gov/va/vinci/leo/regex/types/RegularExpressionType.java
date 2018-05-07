

/* First created by JCasGen Mon Sep 15 09:27:48 MDT 2014 */
package gov.va.vinci.leo.regex.types;

/*
 * #%L
 * Regex Annotator
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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Sep 15 09:27:48 MDT 2014
 * XML source: /var/folders/87/6grdjq5n2cl1w8lh02tn_2zc0000gn/T/leoTypeDescription_cd188d3a-f487-459e-b98b-e6bafc3f344a7516855597979299348.xml
 * @generated */
public class RegularExpressionType extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(RegularExpressionType.class);
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
  protected RegularExpressionType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public RegularExpressionType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public RegularExpressionType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public RegularExpressionType(JCas jcas, int begin, int end) {
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
  //* Feature: pattern

  /** getter for pattern - gets The regular expression pattern than matched.
   * @generated
   * @return value of the feature 
   */
  public String getPattern() {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_pattern == null)
      jcasType.jcas.throwFeatMissing("pattern", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_pattern);}
    
  /** setter for pattern - sets The regular expression pattern than matched. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPattern(String v) {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_pattern == null)
      jcasType.jcas.throwFeatMissing("pattern", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    jcasType.ll_cas.ll_setStringValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_pattern, v);}    
   
    
  //*--------------*
  //* Feature: concept

  /** getter for concept - gets A user settable concept label.
   * @generated
   * @return value of the feature 
   */
  public String getConcept() {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_concept == null)
      jcasType.jcas.throwFeatMissing("concept", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_concept);}
    
  /** setter for concept - sets A user settable concept label. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConcept(String v) {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_concept == null)
      jcasType.jcas.throwFeatMissing("concept", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    jcasType.ll_cas.ll_setStringValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_concept, v);}    
   
    
  //*--------------*
  //* Feature: group

  /** getter for group - gets The group array of values from a regex that contains group identifiers.
   * @generated
   * @return value of the feature 
   */
  public StringArray getGroup() {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_group == null)
      jcasType.jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group)));}
    
  /** setter for group - sets The group array of values from a regex that contains group identifiers. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setGroup(StringArray v) {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_group == null)
      jcasType.jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    jcasType.ll_cas.ll_setRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for group - gets an indexed value - The group array of values from a regex that contains group identifiers.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getGroup(int i) {
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_group == null)
      jcasType.jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group), i);}

  /** indexed setter for group - sets an indexed value - The group array of values from a regex that contains group identifiers.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setGroup(int i, String v) { 
    if (RegularExpressionType_Type.featOkTst && ((RegularExpressionType_Type)jcasType).casFeat_group == null)
      jcasType.jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((RegularExpressionType_Type)jcasType).casFeatCode_group), i, v);}
  }

    
