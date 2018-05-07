
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

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Aug 08 14:23:56 MDT 2016
 * @generated */
public class Token_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Token_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Token_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Token(addr, Token_Type.this);
  			   Token_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Token(addr, Token_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Token.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.leo.whitespace.types.Token");
 
  /** @generated */
  final Feature casFeat_TokenType;
  /** @generated */
  final int     casFeatCode_TokenType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTokenType(int addr) {
        if (featOkTst && casFeat_TokenType == null)
      jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.whitespace.types.Token");
    return ll_cas.ll_getIntValue(addr, casFeatCode_TokenType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTokenType(int addr, int v) {
        if (featOkTst && casFeat_TokenType == null)
      jcas.throwFeatMissing("TokenType", "gov.va.vinci.leo.whitespace.types.Token");
    ll_cas.ll_setIntValue(addr, casFeatCode_TokenType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_AnnotationFeature;
  /** @generated */
  final int     casFeatCode_AnnotationFeature;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getAnnotationFeature(int addr) {
        if (featOkTst && casFeat_AnnotationFeature == null)
      jcas.throwFeatMissing("AnnotationFeature", "gov.va.vinci.leo.whitespace.types.Token");
    return ll_cas.ll_getRefValue(addr, casFeatCode_AnnotationFeature);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnnotationFeature(int addr, int v) {
        if (featOkTst && casFeat_AnnotationFeature == null)
      jcas.throwFeatMissing("AnnotationFeature", "gov.va.vinci.leo.whitespace.types.Token");
    ll_cas.ll_setRefValue(addr, casFeatCode_AnnotationFeature, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Token_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_TokenType = jcas.getRequiredFeatureDE(casType, "TokenType", "uima.cas.Integer", featOkTst);
    casFeatCode_TokenType  = (null == casFeat_TokenType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TokenType).getCode();

 
    casFeat_AnnotationFeature = jcas.getRequiredFeatureDE(casType, "AnnotationFeature", "uima.tcas.Annotation", featOkTst);
    casFeatCode_AnnotationFeature  = (null == casFeat_AnnotationFeature) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_AnnotationFeature).getCode();

  }
}



    