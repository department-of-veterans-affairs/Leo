

/* First created by JCasGen Fri Nov 01 14:11:35 MDT 2013 */
package gov.va.vinci.leo.types;

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


/** 
 * Updated by JCasGen Fri Nov 01 14:11:35 MDT 2013
 * XML source: /var/folders/1s/dht183xn48l3gs7t9_63bkvc0000gn/T/leoTypeDescription_1fbdfbc7-1feb-4704-ae4c-a681e192acb61989798219653398978.xml
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
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Token() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public Token(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public Token(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public Token(JCas jcas, int begin, int end) {
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
  //* Feature: TokenType

  /** getter for TokenType - gets 
   * @generated */
  public int getTokenType() {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TokenType == null)
      jcasType.jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.types.Token");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Token_Type)jcasType).casFeatCode_TokenType);}
    
  /** setter for TokenType - sets  
   * @generated */
  public void setTokenType(int v) {
    if (Token_Type.featOkTst && ((Token_Type)jcasType).casFeat_TokenType == null)
      jcasType.jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.types.Token");
    jcasType.ll_cas.ll_setIntValue(addr, ((Token_Type)jcasType).casFeatCode_TokenType, v);}    
  }

    