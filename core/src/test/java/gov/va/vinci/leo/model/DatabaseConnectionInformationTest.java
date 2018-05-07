package gov.va.vinci.leo.model;

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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by thomasginter on 4/6/16.
 */
public class DatabaseConnectionInformationTest {

    @Test
    public void testGettersSetters() throws Exception {
        DatabaseConnectionInformation dci
                = new DatabaseConnectionInformation("driver", "url", "username", "password", "validationQuery")
                    .setDriver("com.driver.happy")
                    .setUrl("jdbc:url")
                    .setPassword("mypwd")
                    .setUsername("myuser")
                    .setValidationQuery("valQuery");
        assertEquals("com.driver.happy", dci.getDriver());
        assertEquals("jdbc:url", dci.getUrl());
        assertEquals("mypwd", dci.getPassword());
        assertEquals("myuser", dci.getUsername());
        assertEquals("valQuery", dci.getValidationQuery());
    }
}
