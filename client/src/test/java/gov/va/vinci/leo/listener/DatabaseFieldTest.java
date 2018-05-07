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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by ryancornia on 3/13/17.
 */
public class DatabaseFieldTest {

    @Test
    public void simplePojoTest() {
        DatabaseField f1 = new DatabaseField("t1", "date");
        assertEquals(f1.getName(), "t1");
        assertEquals(f1.getType(), "date");
    }

    @Test(expected = IllegalArgumentException.class)
    public void badConstructor1() {
        new DatabaseField(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void badConstructor2() {
        new DatabaseField("test 2", "");
    }
}
