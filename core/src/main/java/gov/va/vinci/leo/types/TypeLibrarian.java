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


import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import gov.va.vinci.leo.model.FeatureNameType;
import org.apache.uima.resource.metadata.TypeDescription;

/**
 * Static methods to help with Type creation and other methods.
 */
public class TypeLibrarian {

    /**
     * Get the validation annotation type that Siman/Chex creates.
     *
     * @return the validation annotation type that Siman/Chex creates.
     */
    public static TypeDescription getValidationAnnotationTypeSystemDescription() {
        return TypeDescriptionBuilder.create("gov.va.vinci.leo.types.ValidationAnnotation", "Validation Annotations", "uima.tcas.Annotation")
                .addFeature("ReferenceAnnotationGuid", "The GUID of the annotation this validation annotation references.", "uima.cas.String")
                .addFeature("ValidationValue", "The validation value for this annotation.", "uima.cas.String")
                .addFeature("ValidationComment", "The validation comment (if any) for this annotation.", "uima.cas.String")
                .addFeature("CreatedBy", "The userId that created this annotation.", "uima.cas.String")
                .getTypeDescription();
    }

    /**
     * Get the standard RelationshipAnnotation TypeSystem Description used within some leo projects.
     *
     * @return the standard RelationshipAnnotation TypeSystem Description used within some leo projects.
     */
    public static TypeDescription getRelationshipAnnotationTypeSystemDescription() {
        return TypeDescriptionBuilder.create("gov.va.vinci.leo.types.RelationshipAnnotation", "RelationshipAnnotation Annotation", "uima.tcas.Annotation")
                .addFeature("Source", "Source", "uima.tcas.Annotation")
                .addFeature("Target", "Target", "uima.cas.FSArray", "uima.tcas.Annotation", Boolean.FALSE)
                .getTypeDescription();
    }

    /**
     * Get the standard CSI TypeSystem Description used within most leo projects.
     *
     * @return the standard CSI TypeSystem Description used within most leo projects.
     */
    public static TypeDescription getCSITypeSystemDescription() {
        return TypeDescriptionBuilder.create("gov.va.vinci.leo.types.CSI", "CSI Annotation", "uima.tcas.Annotation")
                .addFeature("ID", "ID", "uima.cas.String")
                .addFeature("Locator", "Locator for document.", "uima.cas.String")
                .addFeature("RowData", "Row data for document (if any)", "uima.cas.StringArray")
                .addFeature("PropertiesKeys", "Property keys", "uima.cas.StringArray")
                .addFeature("PropertiesValues", "Property Values", "uima.cas.StringArray")
                .getTypeDescription();
    }

    /**
     * A method for easily building simple type system descriptions. For example:
     * <br/><br/>
     * <pre>
     * {@code
     * TypeLibrarian.getTypeSystemDescription("gov.va.vinci.examples.MyType",
     *          new FeatureNameType("pattern", "uima.cas.String"),
     *          new FeatureNameType("referenceAnnotation", "uima.tcas.Annotation"));
     * }
     * </pre>
     * @param className The type classname (e.g. - gov.va.vinci.examples.MyType)
     * @param params  The feature name type object to add to the type. For instance: new FeatureNameType("pattern", "uima.cas.String");
     *
     * @return  A type system description for the class name and feature information passed in.
     */
    public static TypeDescription getTypeSystemDescription(String className, FeatureNameType... params) {
        TypeDescriptionBuilder builder = TypeDescriptionBuilder.create().setName(className);
        for (FeatureNameType p: params) {
            builder.addFeature(p.getName(), p.getName(), p.getType());
        }
        return builder.getTypeDescription();
    }

}