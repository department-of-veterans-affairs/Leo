package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Core
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


/**
 * Types of ESAPI validation 
 * 
 * @author DNS   EdgecK
 *
 */
public enum ESAPIValidationType {

    ACCESS_CONTROL_DB,
    CROSS_SITE_SCRIPTING_PERSISTENT,
    CROSS_SITE_SCRIPTING_REFLECTED,
    COMMAND_INJECTION,
    DENIAL_OF_SERVICE_REG_EXP,
    JSON_INJECTION,
    LOG_FORGING,
    OPEN_REDIRECT,
    PATH_MANIPULATION,
    PORTABILITY_FLAW_FILE_SEPARATOR,
    PORTABILITY_FLAW_LOCALE,
    PRIVACY_VIOLATION,
    SQL_INJECTION,
    SYSTEM_INFORMATION_LEAK_EXTERNAL,
    XML_EXT_ENTITY_INJ
    
}
