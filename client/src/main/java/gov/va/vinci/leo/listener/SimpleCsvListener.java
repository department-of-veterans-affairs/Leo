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

import java.io.*;
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
     * Include features on the annotations or not.
     */
    protected boolean includeFeatures = false;

    /**
     * Constructor with File parameter.
     *
     * @param file The printwriter to write to.
     * @throws FileNotFoundException if the file is not found or can't be written to.
     */
    public SimpleCsvListener(File file) throws FileNotFoundException {
        super(file);
    }

    /**
     * Constructor with an OutputStream parameter.
     *
     * @param stream The stream to write to.
     */
    public SimpleCsvListener(OutputStream stream) {
        super(stream);
    }

    /**
     * Constructor with a Writer parameter.
     *
     * @param writer The writerBuilder to use for output
     */
    public SimpleCsvListener(Writer writer) {
        super(writer);
    }

    /**
     * Get the includeFeatures flag value, if true then include feature values in the output.
     *
     * @return includeFeatures flag value
     */
    public boolean isIncludeFeatures() {
        return includeFeatures;
    }

    /**
     * Set the includeFeatures flag value, if true then include feature values in the output.
     *
     * @param includeFeatures if true include annotation features in output
     * @return reference to this listener instance
     */
    public <T extends SimpleCsvListener> T setIncludeFeatures(boolean includeFeatures) {
        this.includeFeatures = includeFeatures;
        return (T) this;
    }

    @Override
    protected List<String[]> getRows(CAS aCas) {

        List<String[]> rows = new ArrayList<String[]>();

        for (String singleType : inputType) {
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
                            } else if(f.getRange().getName().contains("Array")) {
                                row.add("[" + f.getShortName() + " = null]");
                            } else {
                                row.add("[" + f.getShortName() + " = " + a.getFeatureValueAsString(f) + "]");
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
