

/* First created by JCasGen Wed Jan 25 11:01:45 MST 2012 */
package gov.va.vinci.leo.types;

/*
 * #%L
 * Leo
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
 * Updated by JCasGen Wed Jan 25 11:01:45 MST 2012
 * XML source: null
 *
 * @generated
 */
public class CSI extends Annotation {
    /**
     * @generated
     * @ordered
     */
    public final static int typeIndexID = JCasRegistry.register(CSI.class);
    /**
     * @generated
     * @ordered
     */
    public final static int type = typeIndexID;

    /**
     * @generated
     */
    public int getTypeIndexID() {
        return typeIndexID;
    }

    /**
     * Never called.  Disable default constructor
     *
     * @generated
     */
    protected CSI() {
    }

    /**
     * Internal - constructor used by generator
     *
     * @generated
     */
    public CSI(int addr, TOP_Type type) {
        super(addr, type);
        readObject();
    }

    /**
     * @generated
     */
    public CSI(JCas jcas) {
        super(jcas);
        readObject();
    }

    /**
     * <!-- begin-user-doc -->
     * Write your own initialization here
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    private void readObject() {
    }


    //*--------------*
    //* Feature: ID

    /**
     * getter for ID - gets ID number of the Analyte being analyzed
     *
     * @generated
     */
    public String getID() {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_ID == null)
            jcasType.jcas.throwFeatMissing("ID", "gov.va.vinci.leo.types.CSI");
        return jcasType.ll_cas.ll_getStringValue(addr, ((CSI_Type) jcasType).casFeatCode_ID);
    }

    /**
     * setter for ID - sets ID number of the Analyte being analyzed
     *
     * @generated
     */
    public void setID(String v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_ID == null)
            jcasType.jcas.throwFeatMissing("ID", "gov.va.vinci.leo.types.CSI");
        jcasType.ll_cas.ll_setStringValue(addr, ((CSI_Type) jcasType).casFeatCode_ID, v);
    }


    //*--------------*
    //* Feature: Locator

    /**
     * getter for Locator - gets File path or query information to allow us to find this record again.
     *
     * @generated
     */
    public String getLocator() {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_Locator == null)
            jcasType.jcas.throwFeatMissing("Locator", "gov.va.vinci.leo.types.CSI");
        return jcasType.ll_cas.ll_getStringValue(addr, ((CSI_Type) jcasType).casFeatCode_Locator);
    }

    /**
     * setter for Locator - sets File path or query information to allow us to find this record again.
     *
     * @generated
     */
    public void setLocator(String v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_Locator == null)
            jcasType.jcas.throwFeatMissing("Locator", "gov.va.vinci.leo.types.CSI");
        jcasType.ll_cas.ll_setStringValue(addr, ((CSI_Type) jcasType).casFeatCode_Locator, v);
    }


    //*--------------*
    //* Feature: RowData

    /**
     * getter for RowData - gets
     *
     * @generated
     */
    public StringArray getRowData() {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_RowData == null)
            jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
        return (StringArray) (jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData)));
    }

    /**
     * setter for RowData - sets
     *
     * @generated
     */
    public void setRowData(StringArray v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_RowData == null)
            jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
        jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData, jcasType.ll_cas.ll_getFSRef(v));
    }

    /**
     * indexed getter for RowData - gets an indexed value -
     *
     * @generated
     */
    public String getRowData(int i) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_RowData == null)
            jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData), i);
        return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData), i);
    }

    /**
     * indexed setter for RowData - sets an indexed value -
     *
     * @generated
     */
    public void setRowData(int i, String v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_RowData == null)
            jcasType.jcas.throwFeatMissing("RowData", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData), i);
        jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_RowData), i, v);
    }


    //*--------------*
    //* Feature: PropertiesKeys

    /**
     * getter for PropertiesKeys - gets
     *
     * @generated
     */
    public StringArray getPropertiesKeys() {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesKeys == null)
            jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
        return (StringArray) (jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys)));
    }

    /**
     * setter for PropertiesKeys - sets
     *
     * @generated
     */
    public void setPropertiesKeys(StringArray v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesKeys == null)
            jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
        jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys, jcasType.ll_cas.ll_getFSRef(v));
    }

    /**
     * indexed getter for PropertiesKeys - gets an indexed value -
     *
     * @generated
     */
    public String getPropertiesKeys(int i) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesKeys == null)
            jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys), i);
        return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys), i);
    }

    /**
     * indexed setter for PropertiesKeys - sets an indexed value -
     *
     * @generated
     */
    public void setPropertiesKeys(int i, String v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesKeys == null)
            jcasType.jcas.throwFeatMissing("PropertiesKeys", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys), i);
        jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesKeys), i, v);
    }


    //*--------------*
    //* Feature: PropertiesValues

    /**
     * getter for PropertiesValues - gets
     *
     * @generated
     */
    public StringArray getPropertiesValues() {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesValues == null)
            jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
        return (StringArray) (jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues)));
    }

    /**
     * setter for PropertiesValues - sets
     *
     * @generated
     */
    public void setPropertiesValues(StringArray v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesValues == null)
            jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
        jcasType.ll_cas.ll_setRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues, jcasType.ll_cas.ll_getFSRef(v));
    }

    /**
     * indexed getter for PropertiesValues - gets an indexed value -
     *
     * @generated
     */
    public String getPropertiesValues(int i) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesValues == null)
            jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues), i);
        return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues), i);
    }

    /**
     * indexed setter for PropertiesValues - sets an indexed value -
     *
     * @generated
     */
    public void setPropertiesValues(int i, String v) {
        if (CSI_Type.featOkTst && ((CSI_Type) jcasType).casFeat_PropertiesValues == null)
            jcasType.jcas.throwFeatMissing("PropertiesValues", "gov.va.vinci.leo.types.CSI");
        jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues), i);
        jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CSI_Type) jcasType).casFeatCode_PropertiesValues), i, v);
    }
}

    