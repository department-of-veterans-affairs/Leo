/**
 * NameValue.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo.model;

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

/**
 * Stores name, value pair.
 *
 * @author thomasginter
 */
public class NameValue {
    /**
     * The name of the parameter to set.
     */
    public String name = null;
    /**
     * The value to which the parameter will be set.
     */
    public Object value = null;

    /**
     * Constructor with initial name and value input arguments.
     *
     * @param name  Name of this parameter
     * @param value Value of this parameter
     */
    public NameValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }//constructor with name and value input

    /**
     * Get name.
     *
     * @return the name of this name/value
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     *
     * @param name the name of this name/value to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the value.
     *
     * @return the value of this name/value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Set the value.
     *
     * @param value the value of this name/value to set.
     */
    public void setValue(Object value) {
        this.value = value;
    }
}//NameValue class