
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
 * Updated by JCasGen Mon Sep 15 09:27:48 MDT 2014
 * @generated */
public class RegularExpressionType_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (RegularExpressionType_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = RegularExpressionType_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new RegularExpressionType(addr, RegularExpressionType_Type.this);
  			   RegularExpressionType_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new RegularExpressionType(addr, RegularExpressionType_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = RegularExpressionType.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.leo.regex.types.RegularExpressionType");
 
  /** @generated */
  final Feature casFeat_pattern;
  /** @generated */
  final int     casFeatCode_pattern;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPattern(int addr) {
        if (featOkTst && casFeat_pattern == null)
      jcas.throwFeatMissing("pattern", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pattern);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPattern(int addr, String v) {
        if (featOkTst && casFeat_pattern == null)
      jcas.throwFeatMissing("pattern", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    ll_cas.ll_setStringValue(addr, casFeatCode_pattern, v);}
    
  
 
  /** @generated */
  final Feature casFeat_concept;
  /** @generated */
  final int     casFeatCode_concept;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getConcept(int addr) {
        if (featOkTst && casFeat_concept == null)
      jcas.throwFeatMissing("concept", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return ll_cas.ll_getStringValue(addr, casFeatCode_concept);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setConcept(int addr, String v) {
        if (featOkTst && casFeat_concept == null)
      jcas.throwFeatMissing("concept", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    ll_cas.ll_setStringValue(addr, casFeatCode_concept, v);}
    
  
 
  /** @generated */
  final Feature casFeat_group;
  /** @generated */
  final int     casFeatCode_group;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getGroup(int addr) {
        if (featOkTst && casFeat_group == null)
      jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    return ll_cas.ll_getRefValue(addr, casFeatCode_group);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGroup(int addr, int v) {
        if (featOkTst && casFeat_group == null)
      jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    ll_cas.ll_setRefValue(addr, casFeatCode_group, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getGroup(int addr, int i) {
        if (featOkTst && casFeat_group == null)
      jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_group), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_group), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_group), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setGroup(int addr, int i, String v) {
        if (featOkTst && casFeat_group == null)
      jcas.throwFeatMissing("group", "gov.va.vinci.leo.regex.types.RegularExpressionType");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_group), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_group), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_group), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public RegularExpressionType_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pattern = jcas.getRequiredFeatureDE(casType, "pattern", "uima.cas.String", featOkTst);
    casFeatCode_pattern  = (null == casFeat_pattern) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pattern).getCode();

 
    casFeat_concept = jcas.getRequiredFeatureDE(casType, "concept", "uima.cas.String", featOkTst);
    casFeatCode_concept  = (null == casFeat_concept) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_concept).getCode();

 
    casFeat_group = jcas.getRequiredFeatureDE(casType, "group", "uima.cas.StringArray", featOkTst);
    casFeatCode_group  = (null == casFeat_group) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_group).getCode();

  }
}



    
