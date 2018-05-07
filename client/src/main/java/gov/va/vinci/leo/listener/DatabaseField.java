package gov.va.vinci.leo.listener;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import org.apache.commons.validator.GenericValidator;

/**
 * Pojo for holding a database field name and type. This should be sql type, not java type.
 *
 */
public class DatabaseField {
    private String name;
    private String type;

    /**
     * Constructor
     * @param name  the database field name.
     * @param type  the type of this field in the database, for instance varchar(20)
     */
    public DatabaseField(String name, String type) {

        if (GenericValidator.isBlankOrNull(name) || GenericValidator.isBlankOrNull(type)) {
            throw new IllegalArgumentException("Both name and type are required and cannot be empty.");
        }
        this.name = name;
        this.type = type;
    }

    /**
     * Return the name of this database field.
     *
     * @return the name of this database field.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the database type for this database field.
     *
     * @return the database type for this database field.
     */
    public String getType() {
        return type;
    }

}