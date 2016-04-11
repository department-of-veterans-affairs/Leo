
/* First created by JCasGen Thu Mar 24 12:44:39 MDT 2016 */
package gov.va.vinci.leo.types;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Thu Mar 24 12:44:39 MDT 2016
 * @generated */
public class ExampleType_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ExampleType_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ExampleType_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ExampleType(addr, ExampleType_Type.this);
  			   ExampleType_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ExampleType(addr, ExampleType_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ExampleType.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("gov.va.vinci.leo.types.ExampleType");
 
  /** @generated */
  final Feature casFeat_numberOfCASesProcessed;
  /** @generated */
  final int     casFeatCode_numberOfCASesProcessed;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNumberOfCASesProcessed(int addr) {
        if (featOkTst && casFeat_numberOfCASesProcessed == null)
      jcas.throwFeatMissing("numberOfCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    return ll_cas.ll_getIntValue(addr, casFeatCode_numberOfCASesProcessed);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumberOfCASesProcessed(int addr, int v) {
        if (featOkTst && casFeat_numberOfCASesProcessed == null)
      jcas.throwFeatMissing("numberOfCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    ll_cas.ll_setIntValue(addr, casFeatCode_numberOfCASesProcessed, v);}
    
  
 
  /** @generated */
  final Feature casFeat_numberOfFilteredCASesProcessed;
  /** @generated */
  final int     casFeatCode_numberOfFilteredCASesProcessed;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNumberOfFilteredCASesProcessed(int addr) {
        if (featOkTst && casFeat_numberOfFilteredCASesProcessed == null)
      jcas.throwFeatMissing("numberOfFilteredCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    return ll_cas.ll_getIntValue(addr, casFeatCode_numberOfFilteredCASesProcessed);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumberOfFilteredCASesProcessed(int addr, int v) {
        if (featOkTst && casFeat_numberOfFilteredCASesProcessed == null)
      jcas.throwFeatMissing("numberOfFilteredCASesProcessed", "gov.va.vinci.leo.types.ExampleType");
    ll_cas.ll_setIntValue(addr, casFeatCode_numberOfFilteredCASesProcessed, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ExampleType_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_numberOfCASesProcessed = jcas.getRequiredFeatureDE(casType, "numberOfCASesProcessed", "uima.cas.Integer", featOkTst);
    casFeatCode_numberOfCASesProcessed  = (null == casFeat_numberOfCASesProcessed) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_numberOfCASesProcessed).getCode();

 
    casFeat_numberOfFilteredCASesProcessed = jcas.getRequiredFeatureDE(casType, "numberOfFilteredCASesProcessed", "uima.cas.Integer", featOkTst);
    casFeatCode_numberOfFilteredCASesProcessed  = (null == casFeat_numberOfFilteredCASesProcessed) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_numberOfFilteredCASesProcessed).getCode();

  }
}



    