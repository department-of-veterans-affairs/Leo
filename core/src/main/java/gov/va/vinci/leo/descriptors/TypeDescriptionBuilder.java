package gov.va.vinci.leo.descriptors;

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

import org.apache.uima.resource.metadata.AllowedValue;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.apache.uima.resource.metadata.impl.TypeDescription_impl;

/**
 * Improve the Type building system in UIMA with a builder. To build a type, call create() first, then
 * the type metadata. A simple example:
 * <p/>
 * <pre>
 *        TypeDescriptionBuilder.create("gov.va.vinci.test.Type", "My Test Type", "uima.tcas.Annotation")
 * 								.addFeature("myFeature", "Feature", "String")
 * 								.addFeature("myFeature 2", "Feature 2", "String")
 * 								.getTypeDescription();
 * </pre>
 *
 * @author vhaislcornir
 */
public class TypeDescriptionBuilder {

    /**
     * The underlying type description this builder represents.
     */
    TypeDescription t = new TypeDescription_impl();

    /**
     * private constructor so it is not constructed outside of the create() method.
     */
    private TypeDescriptionBuilder() {

    }


    /**
     * @param aFeatureName name of feature to add
     * @param aDescription verbose description of the feature
     * @param aRangeTypeName name of feature's range type
     * @return  this type description builder
     */
    public TypeDescriptionBuilder addFeature(String aFeatureName, String aDescription, String aRangeTypeName) {
        t.addFeature(aFeatureName, aDescription, aRangeTypeName);
        return this;
    }

    /**
     * @param aFeatureName name of feature to add
     * @param aDescription verbose description of the feature
     * @param aRangeTypeName name of feature's range type
     * @param aElementTypeName type of element expected to be contained in the array or list
     * @param aMultipleReferencesAllowed whether an array or list that's assigned to this feature can also be referenced from another feature. This is a Boolean object so that the null value can be used to represent the case where the user has not specified a value.
     * @return  this type description builder
     */
    public TypeDescriptionBuilder addFeature(String aFeatureName, String aDescription, String aRangeTypeName, String aElementTypeName, Boolean aMultipleReferencesAllowed) {
        t.addFeature(aFeatureName, aDescription, aRangeTypeName, aElementTypeName, aMultipleReferencesAllowed);
        return this;
    }

    /**
     * Create a new builder. A create method must be called to instantiate a builder before any other operations.
     *
     * @return the TypeDescriptionBuilder
     */
    public static TypeDescriptionBuilder create() {
        return new TypeDescriptionBuilder();
    }

    /**
     * Create a new builder. A create method must be called to instantiate a builder before any other operations.
     *
     * @param aName          the type name, a class name, for instance gov.va.vinci.type.MyType
     * @param aDescription   a description of the type, for instance "My Type"
     * @param aSupertypeName the super type this type extends. Usually uima.tcas.Annotation
     * @return the TypeDescriptionBuilder
     */
    public static TypeDescriptionBuilder create(String aName, String aDescription, String aSupertypeName) {
        TypeDescriptionBuilder b = new TypeDescriptionBuilder();
        b.setName(aName);
        b.setDescription(aDescription);
        b.setSupertypeName(aSupertypeName);
        return b;
    }

    /**
     * Get the TypeDescription this builder has created.
     *
     * @return the TypeDescription created by this builder. This will never be null.
     */
    public TypeDescription getTypeDescription() {
        return t;
    }

    /**
     * Set the allowed values in this type description.
     * @param aAllowedValues the allowed values in this type description.
     * @return  this TypeDescriptionBuilder
     */
    public TypeDescriptionBuilder setAllowedValues(AllowedValue[] aAllowedValues) {
        t.setAllowedValues(aAllowedValues);
        return this;
    }

    /**
     * Set a description for this type description.
     * @param description a description for this type description.
     * @return  this TypeDescriptionBuilder
     */
    public TypeDescriptionBuilder setDescription(String description) {
        t.setDescription(description);
        return this;
    }

    /**
     * Set the feature descriptions for this type description.
     * @param aFeatures the feature descriptions for this type description.
     * @return  this TypeDescriptionBuilder
     */
    public TypeDescriptionBuilder setFeatures(FeatureDescription[] aFeatures) {
        t.setFeatures(aFeatures);
        return this;
    }

    /**
     * Set a name for this type description.
     * @param name a name for this type description.
     * @return  this TypeDescriptionBuilder
     */
    public TypeDescriptionBuilder setName(String name) {
        t.setName(name);
        return this;
    }

    /**
     * Set a super type for this type description.   (e.g. - gov.va.vinci.types.MySuperType)
     * @param name the name of the super type for this description
     * @return  this TypeDescriptionBuilder
     */
    public TypeDescriptionBuilder setSupertypeName(String name) {
        t.setSupertypeName(name);
        return this;
    }

}
