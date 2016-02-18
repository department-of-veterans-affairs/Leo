package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Client
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

import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;

/**
 * Extends the ConfigurationParameter_impl class to provide a constructor that allows the user to set required fields
 * from the constructor.
 *
 * @see org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl
 *
 * @author Ryan Cornia
 */
public class ConfigurationParameterImpl  extends ConfigurationParameter_impl{

    /**
     * Default constructor.
     */
    public ConfigurationParameterImpl() {
        super();
    }

    /**
     * Initialize the configuration parameter with the input values provided, rather than using setters only.
     *
     * @param name Name of the parameter.
     * @param description Description of the parameter.
     * @param type One of the parameter types defined in ConfigurationParameter.
     * @param mandatory If true then this parameter is required.
     * @param multivalued If true then this parameter refers to an array of values instead of a single value.
     * @param overrides Inherited parameters that this parameter overrides in the format of component1\parameter1.
     */
    public ConfigurationParameterImpl(String name, String description, String type, boolean mandatory, boolean multivalued, String[] overrides) {
        this.setName(name);
        this.setDescription(description);
        this.setType(type);
        this.setMandatory(mandatory);
        this.setMultiValued(multivalued);
        this.setOverrides(overrides);
    }
}
