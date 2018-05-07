

/* First created by JCasGen Mon Aug 08 14:23:56 MDT 2016 */
package gov.va.vinci.leo.types;

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
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** CSI Annotation
 * Updated by JCasGen Mon Aug 08 14:23:56 MDT 2016
 * XML source: /var/folders/9p/6qt9dlgd1ks8d036hkqb9kz00000gn/T/leoTypeDescription_39e12376-aec5-4acf-a70c-afa4b7cffaa81105431736418235892.xml
 * @generated */
public class CSI extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CSI.class);
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
  protected CSI() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CSI(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CSI(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CSI(JCas jcas, int begin, int end) {
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
  //* Feature: ID

  /** getter for ID - gets ID
   * @generated
   * @return value of the feature 
   */
  public String getID() {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "gov.va.vinci.leo.types.CSI");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CSI_Type)jcasType).casFeatCode_ID);}
    
  /** setter for ID - sets ID 
   * @generated
   * @param v value to set into the feature 
   */
  public void setID(String v) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_ID == null)
      jcasType.jcas.throwFeatMissing("ID", "gov.va.vinci.leo.types.CSI");
    jcasType.ll_cas.ll_setStringValue(addr, ((CSI_Type)jcasType).casFeatCode_ID, v);}    
   
    
  //*--------------*
  //* Feature: Locator

  /** getter for Locator - gets Locator for document.
   * @generated
   * @return value of the feature 
   */
  public String getLocator() {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_Locator == null)
      jcasType.jcas.throwFeatMissing("Locator", "gov.va.vinci.leo.types.CSI");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CSI_Type)jcasType).casFeatCode_Locator);}
    
  /** setter for Locator - sets Locator for document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLocator(String v) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_Locator == null)
      jcasType.jcas.throwFeatMissing("Locator", "gov.va.vinci.leo.types.CSI");
    jcasType.ll_cas.ll_setStringValue(addr, ((CSI_Type)jcasType).casFeatCode_Locator, v);}    
   
    
  //*--------------*
  //* Feature: RowData

  /** getter for RowData - gets Row data for document (if any)
   * @generated
   * @return value of the feature 
   */
  public StringArray getRowData() {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_RowData == null)
      jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData)));}
    
  /** setter for RowData - sets Row data for document (if any) 
   * @generated
   * @param v value to set into the feature 
   */
  public void setRowData(StringArray v) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_RowData == null)
      jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
    jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for RowData - gets an indexed value - Row data for document (if any)
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getRowData(int i) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_RowData == null)
      jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData), i);}

  /** indexed setter for RowData - sets an indexed value - Row data for document (if any)
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setRowData(int i, String v) { 
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_RowData == null)
      jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_RowData), i, v);}
   
    
  //*--------------*
  //* Feature: PropertiesKeys

  /** getter for PropertiesKeys - gets Property keys
   * @generated
   * @return value of the feature 
   */
  public StringArray getPropertiesKeys() {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesKeys == null)
      jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys)));}
    
  /** setter for PropertiesKeys - sets Property keys 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPropertiesKeys(StringArray v) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesKeys == null)
      jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
    jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for PropertiesKeys - gets an indexed value - Property keys
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getPropertiesKeys(int i) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesKeys == null)
      jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys), i);}

  /** indexed setter for PropertiesKeys - sets an indexed value - Property keys
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setPropertiesKeys(int i, String v) { 
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesKeys == null)
      jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesKeys), i, v);}
   
    
  //*--------------*
  //* Feature: PropertiesValues

  /** getter for PropertiesValues - gets Property Values
   * @generated
   * @return value of the feature 
   */
  public StringArray getPropertiesValues() {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesValues == null)
      jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues)));}
    
  /** setter for PropertiesValues - sets Property Values 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPropertiesValues(StringArray v) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesValues == null)
      jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
    jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for PropertiesValues - gets an indexed value - Property Values
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getPropertiesValues(int i) {
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesValues == null)
      jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues), i);}

  /** indexed setter for PropertiesValues - sets an indexed value - Property Values
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setPropertiesValues(int i, String v) { 
    if (CSI_Type.featOkTst && ((CSI_Type)jcasType).casFeat_PropertiesValues == null)
      jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type)jcasType).casFeatCode_PropertiesValues), i, v);}
  }

    