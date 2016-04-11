package gov.va.vinci.leo.model;

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

/**
 * A pojo for holding a feature name and UIMA type. For example:
 *
 * name: pattern
 * type: uima.cas.String
 */
public class FeatureNameType {

    /**
     * Name of the feature.
     */
    private String name;
    /**
     * UIMA Type of the feature.
     */
    private String type;

    /**
     * Constructor.
     *
     * @param name  Name of the feature.     e.g. - pattern
     * @param type  UIMA Type of the feature.  e.g. - uima.cas.String
     */
    public FeatureNameType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     *  Get the name of the feature.
     * @return  the name of the feature.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the feature.
     * @param name  Name of the feature.     e.g. - pattern
     */
    public FeatureNameType setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the UIMA Type of the feature.  e.g. - uima.cas.String
     * @return   the UIMA Type of the feature.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the UIMA Type of the feature.
     * @param type the UIMA Type of the feature.  e.g. - uima.cas.String
     */
    public FeatureNameType setType(String type) {
        this.type = type;
        return this;
    }
}
