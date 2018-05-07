

/* First created by JCasGen Thu Mar 24 12:44:39 MDT 2016 */
package gov.va.vinci.leo.types;

/*
 * #%L
 * Leo Service
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
 * Updated by JCasGen Thu Mar 24 12:44:39 MDT 2016
 * XML source: /var/folders/tf/nfj9wlxx2b9ffpk4jdw2010c0000gn/T/leoTypeDescription_bbb593ac-d71c-4c63-96f9-a3d880ea7e4c7330589818184084663.xml
 * @generated */
public class ExampleType extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ExampleType.class);
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
  protected ExampleType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ExampleType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ExampleType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ExampleType(JCas jcas, int begin, int end) {
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
  //* Feature: numberOfCASesProcessed

  /** getter for numberOfCASesProcessed - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumberOfCASesProcessed() {
    if (ExampleType_Type.featOkTst && ((ExampleType_Type)jcasType).casFeat_numberOfCASesProcessed == null)
      jcasType.jcas.throwFeatMissing("numberOfCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ExampleType_Type)jcasType).casFeatCode_numberOfCASesProcessed);}
    
  /** setter for numberOfCASesProcessed - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumberOfCASesProcessed(int v) {
    if (ExampleType_Type.featOkTst && ((ExampleType_Type)jcasType).casFeat_numberOfCASesProcessed == null)
      jcasType.jcas.throwFeatMissing("numberOfCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    jcasType.ll_cas.ll_setIntValue(addr, ((ExampleType_Type)jcasType).casFeatCode_numberOfCASesProcessed, v);}    
   
    
  //*--------------*
  //* Feature: numberOfFilteredCASesProcessed

  /** getter for numberOfFilteredCASesProcessed - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumberOfFilteredCASesProcessed() {
    if (ExampleType_Type.featOkTst && ((ExampleType_Type)jcasType).casFeat_numberOfFilteredCASesProcessed == null)
      jcasType.jcas.throwFeatMissing("numberOfFilteredCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ExampleType_Type)jcasType).casFeatCode_numberOfFilteredCASesProcessed);}
    
  /** setter for numberOfFilteredCASesProcessed - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumberOfFilteredCASesProcessed(int v) {
    if (ExampleType_Type.featOkTst && ((ExampleType_Type)jcasType).casFeat_numberOfFilteredCASesProcessed == null)
      jcasType.jcas.throwFeatMissing("numberOfFilteredCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    jcasType.ll_cas.ll_setIntValue(addr, ((ExampleType_Type)jcasType).casFeatCode_numberOfFilteredCASesProcessed, v);}    
  }

    