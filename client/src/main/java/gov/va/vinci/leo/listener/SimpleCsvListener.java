package gov.va.vinci.leo.listener;

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

import org.apache.uima.cas.*;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Note: This is a very simple gov.va.vinci.leo.listener to output one type of annotation and, optionally,
 * its features. It DOES NOT work well with features of Array type. If you need to
 * output features of Array type, this code simply calls toStringArray on the ArrayFS, and
 * outputs those values.
 *
 * @author vhaislcornir
 */
public class SimpleCsvListener extends BaseCsvListener {

    /**
     * The type name this annotation gov.va.vinci.leo.listener is limited to.
     */
    protected List<String> typeNames;

    /**
     * Include features on the annotations or not.
     */
    protected boolean includeFeatures = false;

    /**
     * Constructor that sets the initial outputFile, annotation type name for output, and includeFeatures flag.
     *
     * @param outputFile      File to which the annotation information will be output.
     * @param typeName        Name of the annotation type that will be output to the file. At least one type name is
     *                        required.
     * @throws java.io.IOException if there is an error writing to the file.
     */
    public SimpleCsvListener(File outputFile, String... typeName) throws IOException {
        this(outputFile, true, '\t', false, typeName);
    }

    /**
     * Constructor that sets the initial outputFile, annotation type name for output, and includeFeatures flag.
     *
     * @param outputFile      File to which the annotation information will be output.
     * @param includeHeaders  If true, a header line will be written out first.
     * @param delimeter       the csv delimeter (comma, tab, etc...)
     * @param includeFeatures If true then include the features that need to be added.
     * @param typeName        Name of the annotation type that will be output to the file. At least one type name is
     *                        required.
     * @throws java.io.IOException if there is an error writing to the file.
     */
    public SimpleCsvListener(File outputFile, boolean includeHeaders, char delimeter, boolean includeFeatures, String... typeName) throws IOException {
        super(outputFile, delimeter);

        if (includeHeaders) {
            this.writeHeaders();
        }

        if (typeName == null || typeName.length < 1) {
            throw new IllegalArgumentException("Type name is required.");
        }
        this.typeNames = Arrays.asList(typeName);
        this.includeFeatures = includeFeatures;
    }

    @Override
    protected List<String[]> getRows(CAS aCas) {

        List<String[]> rows = new ArrayList<String[]>();

        for (String singleType : typeNames) {
            Type type = aCas.getTypeSystem().getType(singleType);
            FSIndex<?> index = aCas.getAnnotationIndex(type);
            FSIterator<?> iterator = index.iterator();

            String refLoc;
            try {
                refLoc = getReferenceLocation(aCas.getJCas());
            } catch (CASException e1) {
                throw new RuntimeException(e1);
            }

            while (iterator.hasNext()) {
                Annotation a = (Annotation) iterator.next();

                List<String> row = new ArrayList<String>();

                row.add(refLoc);
                row.add("" + a.getBegin());
                row.add("" + a.getEnd());
                row.add(singleType);
                row.add(a.getCoveredText().replace("\r\n", " ").replace("\n", " ").replace("\r", " "));

                if (includeFeatures) {
                    List<Feature> features = type.getFeatures();
                    for (Feature f : features) {
                        if (f.getName().startsWith(singleType + ":")) {
                            FeatureStructure fs = null;
                            // Try to get the feature structure as an object. If it fails, it is primitive.
                            try {
                                fs = a.getFeatureValue(f);
                            } catch (Exception e) {
                                // No-op
                            }

                            if (fs != null && fs instanceof ArrayFS) {
                                String[] values = ((ArrayFS) a.getFeatureValue(f)).toStringArray();
                                String featureValue = ("[ " + f.getShortName() + " = ");
                                for (String val : values) {
                                    featureValue += val + ",";
                                }
                                featureValue += "] ";
                                row.add(featureValue);
                            } else {
                                row.add("[ " + f.getShortName() + " = " + a.getFeatureValueAsString(f) + "]");
                            }
                        }
                    }
                }
                rows.add(row.toArray(new String[row.size()]));
            }
        }

        return rows;
    }

    @Override
    protected String[] getHeaders() {
        return new String[] {"DocumentId","Start", "End", "Type", "CoveredText" };
    }

}
