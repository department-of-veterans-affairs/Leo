

/* First created by JCasGen Fri Sep 27 09:07:07 MDT 2013 */
package gov.va.vinci.leo.test;

/*
 * #%L
 * Leo Core
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
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** My Test Type
 * Updated by JCasGen Fri Sep 27 09:07:07 MDT 2013
 * XML source: /var/folders/87/6grdjq5n2cl1w8lh02tn_2zc0000gn/T/leoTypeDescription_a7a174b0-fcf3-445f-9f8f-b3ce21fd92208799416165352735040.xml
 * @generated */
public class MyType extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(MyType.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected MyType() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public MyType(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public MyType(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public MyType(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Pattern

  /** getter for Pattern - gets Regex Pattern that matched
   * @generated */
  public String getPattern() {
    if (MyType_Type.featOkTst && ((MyType_Type)jcasType).casFeat_Pattern == null)
      jcasType.jcas.throwFeatMissing("Pattern", "gov.va.vinci.leo.test.MyType");
    return jcasType.ll_cas.ll_getStringValue(addr, ((MyType_Type)jcasType).casFeatCode_Pattern);}
    
  /** setter for Pattern - sets Regex Pattern that matched 
   * @generated */
  public void setPattern(String v) {
    if (MyType_Type.featOkTst && ((MyType_Type)jcasType).casFeat_Pattern == null)
      jcasType.jcas.throwFeatMissing("Pattern", "gov.va.vinci.leo.test.MyType");
    jcasType.ll_cas.ll_setStringValue(addr, ((MyType_Type)jcasType).casFeatCode_Pattern, v);}    
  }

    