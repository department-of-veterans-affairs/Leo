//package gov.va.vinci.leo.tools;
//
///*
// * #%L
// * Leo Core
// * %%
// * Copyright (C) 2010 - 2017 Department of Veterans Affairs
// * %%
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * #L%
// */
//
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.*;
//
///**
// *
// *
// */
//public class EsapiValidationsIT {
//
//    @BeforeClass
//    public static void setup() {
//    }
//
//    /**
//     * Validate long: invalid input test.
//     */
//    @Test
//    public void validateLong_InvalidInput_Test() {
//
//        long output = -1;
//        String input = "9x";
//        try {
//            output = ESAPIValidator.validateLongInput(input);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            // success
//        }
//    }
//
//    /**
//     * Validate long: valid input test.
//     */
//    @Test
//    public void validateLong_ValidInput_Test() {
//
//        long output = -1;
//        String input = "9";
//        try {
//            output = ESAPIValidator.validateLongInput(input);
//            // success
//            assertEquals(9, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Validate long: valid input test.
//     */
//    @Test
//    public void validateLong_ValidInputZero_Test() {
//
//        long output = -1;
//        String input = "0";
//        try {
//            output = ESAPIValidator.validateLongInput(input);
//            // success
//            assertEquals(0, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Validate Double: invalid input test.
//     */
//    @Test
//    public void validateDouble_InvalidInput_Test() {
//
//        double output = -1;
//        String input = "9x";
//        try {
//            output = ESAPIValidator.validateDoubleInput(input);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            // success
//
//        }
//    }
//
//    /**
//     * Validate long: valid input test.
//     */
//    @Test
//    public void validateDouble_ValidInput_Test() {
//
//        double output = -1;
//        Double input = 9.25;
//        try {
//            output = ESAPIValidator.validateDoubleInput(input.toString());
//            // success
//            assertEquals(9.25, output, 0);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Validate long: valid input test.
//     */
//    @Test
//    public void validateDouble_ValidInputZero_Test() {
//
//        double output = -1;
//        Double input = 0.25;
//        try {
//            output = ESAPIValidator.validateDoubleInput(input.toString());
//            // success
//            assertEquals(0.25, output, 0);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Validate file name: no path invalid file extension test.
//     */
//    @Test
//    public void validateFileName_NoPath_InvalidFileExtension_Test() {
//
//        String output = null;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        // With bad type (file extension) Errors out/throws exception
//        String input = "someFileName.txt";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInput(input, allowedExtensions);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            assertThat(t.getMessage(), is("Invalid file name value found in input."));
//        }
//
//    }
//
//    @Test
//    public void validateFileName_NoPath_ValidFileExtension_Test() {
//
//        String output;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        // Valid file name
//        output = null;
//        String input = "someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInput(input, allowedExtensions);
//            // success
//            assertEquals(input, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Validate file name: with unexpected path and valid file extension.
//     */
//    @Test
//    public void validateFileName_WithPath_ValidFileExtension_Test() {
//
//        String output;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        // With path data (file separators) Errors out/throws exception
//        output = null;
//        String input = "somePath/someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInput(input, allowedExtensions);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            assertThat(t.getMessage(), is("Invalid file name value found in input."));
//        }
//    }
//
//    @Test
//    public void validateFileNameTest() {
//
//        String output;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        // Valid file name
//        output = null;
//        String input = "someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInput(input, allowedExtensions);
//            // success
//            assertEquals(input, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//
//        // With path data (file separators) Errors out/throws exception
//        output = null;
//        input = "somePath/someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInput(input, allowedExtensions);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            // success
//        }
//    }
//
//    /**
//     * Validate file name with path: Valid path and valid file extension.
//     */
//    @Test
//    public void validateFileNameWithPath_ValidPath_ValidFileExtension_Test() {
//
//        String output = null;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        String input = "C:/tmp/someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInputWithPath(input, allowedExtensions);
//            assertEquals(input, output);
//        } catch (Throwable t) {
//        }
//    }
//
//    /**
//     * Validate file name with path: Valid path and invalid file extension.
//     */
//    @Test
//    public void validateFileNameWithPath_ValidPath_InvalidFileExtension_Test() {
//
//        String output = null;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        String input = "C:/tmp/someFileName.txt";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInputWithPath(input, allowedExtensions);
//            fail("Expected an RuntimeException to be thrown");
//        } catch (Throwable t) {
//            assertThat(t.getMessage(), is("Invalid file name value found in input."));
//        }
//    }
//
//    /**
//     * Validate file name with path:  Invalid path and valid file extension.
//     */
//    @Test
//    public void validateFileNameWithPath_InvalidPath_ValidFileExtension_Test() {
//
//        String output = null;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        String input = "C:/\\r\\n\\.%/:*\"<>|tmp/someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInputWithPath(input, allowedExtensions);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            assertTrue(t.getMessage().contains("Illegal char"));
//        }
//    }
//
//    /**
//     * Validate file name with path:  Invalid path and invalid file extension.
//     */
//    @Test
//    public void validateFileNameWithPath_InvalidPath_InvalidFileExtension_Test() {
//
//        String output = null;
//        List<String> allowedExtensions = new ArrayList<String>();
//
//        String input = "C:/\\r\\n\\.%/:*\"<>|tmp/someFileName.tmp";
//        allowedExtensions.add("tmp");
//        try {
//            output = ESAPIValidator.validateFileNameInputWithPath(input, allowedExtensions);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            // success
//            assertTrue(t.getMessage().contains("Illegal char"));
//        }
//    }
//
//
//    /**
//     * Validate ACCESS_CONTROL_DB: invalid input.
//     */
//    @Test
//    public final void validateAccessControlDb_InvalidInput_Test() {
//        String output = null;
//        String input = "*xx";
//
//        try {
//            output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.ACCESS_CONTROL_DB);
//            fail("No exception thrown for invalid input character(s).");
//        } catch (Throwable t) {
//            assertTrue(t.getMessage().contains("Invalid characters found in input"));
//        }
//    }
//
//    /**
//     * Validate ACCESS_CONTROL_DB valid input test.
//     */
//    @Test
//    public final void validateAccessControlDb_ValidInputTest() {
//        String output = null;
//        String input = "xx";
//
//        try {
//            output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.ACCESS_CONTROL_DB);
//            assertEquals(input, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//
//    @Test
//    public void validateCrossSiteScriptingPersistentTest() {
//
//    }
//
//    /**
//     * Test method validateString for case CrossSiteScriptingReflected.
//     *
//     * */
//    @Test
//    public void validateCrossSiteScriptingReflected_ValidInput_Test() {
//        String output = null;
//
//        String input = "This is a valid input with a lot of characters include the word script";
//        try {
//            output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.CROSS_SITE_SCRIPTING_REFLECTED);
//            assertEquals(input, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Test method validateString for case CrossSiteScriptingReflected.
//     *
//     * */
//    @Test
//    public void validateCrossSiteScriptingReflected_ValidInput_WithLessThan_Test() {
//        String output = null;
//
//        String input = "This is a valid input with a lot of characters include < and the word script";
//        try {
//            output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.CROSS_SITE_SCRIPTING_REFLECTED);
//
//            assertEquals(input, output);
//        } catch (Throwable t) {
//            fail("Exception thrown for valid input character(s).");
//        }
//    }
//
//    /**
//     * Test method validateString for case CrossSiteScriptingReflected.
//     *
//     * */
//    @Test
//    public void validateCrossSiteScriptingReflected_InvalidInput_Test() {
//        String output = null;
//        String input = "This is a invalid input with a lot of characters include the tag <script";
//        try {
//            output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.CROSS_SITE_SCRIPTING_REFLECTED);
//        } catch (RuntimeException t) {
//            assertTrue(t.getMessage().contains("Invalid characters found in input"));
//        }
//    }
//
//
//    /**
//     * Test method validateString for case CommandInjection.
//     *
//     * */
//    @Test
//    public void validateCommandInjectionTest() {
//
//    }
//
//    /**
//     * Test method validateString for case DenialOfServiceRegExp.
//     *
//     * */
//    @Test
//    public void validateDenialOfServiceRegExpTest() {
//
//    }
//
//    /**
//     * Test method validateString for case JsonInjection.
//     *
//     * */
//    @Test
//    public void validateJsonInjectionTest() {
//
//    }
//
//    /**
//     * Test method validateString for case LogForging.
//     *
//     * */
//    @Test
//    public final void validateLogForgingTest() {
//        String output = null;
//        String input = null;
//
//        input = "xx xxx\r\nxxx";
//        output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.LOG_FORGING);
//        assertEquals("xx xxx  xxx", output);
//
//        output = null;
//        input = "xx xxxo xxx";
//        output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.LOG_FORGING);
//        assertEquals(input, output);
//
//        output = null;
//        input = "** xx^o xxx yyyyyyyyyyyyyyyyyyyyyyyyyyy ,./432";
//        output = ESAPIValidator.validateStringInput(input, ESAPIValidationType.LOG_FORGING);
//        assertEquals(input, output);
//    }
//
//    /**
//     * Test method validateString for case OpenRedirect.
//     *
//     * */
//    @Test
//    public void validateOpenRedirectTest() {
//
//    }
//
//    /**
//     * Test method validateString for case PathManipulation.
//     *
//     * */
//    @Test
//    public void validatePathManipulationTest() {
//
//    }
//
//    /**
//     * Test method validateString for case PortabilityFlawFileSeparator.
//     *
//     * */
//    @Test
//    public void validatePortabilityFlawFileSeparatorTest() {
//
//    }
//
//    /**
//     * Test method validateString for case PortabilityFlawLocale.
//     *
//     * */
//    @Test
//    public void validatePortabilityFlawLocaleTest() {
//
//    }
//
//    /**
//     * Test method validateString for case PrivacyViolation.
//     *
//     * */
//    @Test
//    public void validatePrivacyViolationTest() {
//
//    }
//
//    /**
//     * Test method validateString for case SqlInjection.
//     *
//     * */
//    @Test
//    public void validateSqlInjectionTest() {
//
//    }
//
//    /**
//     * Test method validateString for case SystemInformationLeakExternal.
//     *
//     * */
//    @Test
//    public void validateSystemInformationLeakExternalTest() {
//
//    }
//
//    /**
//     * Test method validateString for case XmlExtEntityInj.
//     *
//     * */
//    @Test
//    public void validateXmlExtEntityInjTest() {
//
//    }
//
//}
