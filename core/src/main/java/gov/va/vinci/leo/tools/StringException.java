package gov.va.vinci.leo.tools;

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
 * @author Administrator
 *         This class is used to generate exception when the supplied pattern is null/empty/
 *         when input does not adhere some prescribed input syntax.
 */
public class StringException extends Exception {

    /**
     * The default serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The error message to be displayed.
     */
    private String patt = null;

    /**
     * The default constructor.
     */
    public StringException() {
        super();
    }

    /**
     * Constructor with pattern.
     *
     * @param pattern - contains the error message.
     */
    public StringException(String pattern) {
        this.patt = pattern;
    }

    /**
     * @see java.lang.Throwable#toString()
     *
     * @return This method returns the error message to the user, stating the reason of termination
     */
    public String toString() {
        if (this.patt == null)
            return "Error: The pattern cannot be empty";
        else
            return this.patt;
    }

}
