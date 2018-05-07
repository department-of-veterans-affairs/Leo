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

import au.com.bytecode.opencsv.CSVWriter;

import java.io.Writer;

/**
 * Created by thomasginter on 3/15/16.
 */
public class CSVWriterBuilder {

    protected Writer writer = null;

    protected char separator = CSVWriter.DEFAULT_SEPARATOR;

    protected char quotechar = CSVWriter.DEFAULT_QUOTE_CHARACTER;

    protected char escapechar = CSVWriter.DEFAULT_ESCAPE_CHARACTER;

    protected String lineEnd = CSVWriter.DEFAULT_LINE_END;

    /**
     * Constructs CSVWriter using a comma for the separator.
     *
     * @param writer the writer to an underlying CSV source.
     */
    public CSVWriterBuilder(Writer writer) {
        this.writer = writer;
    }

    public char getSeparator() {
        return separator;
    }

    public CSVWriterBuilder setSeparator(char separator) {
        this.separator = separator;
        return this;
    }

    public char getQuotechar() {
        return quotechar;
    }

    public CSVWriterBuilder setQuotechar(char quotechar) {
        this.quotechar = quotechar;
        return this;
    }

    public char getEscapechar() {
        return escapechar;
    }

    public CSVWriterBuilder setEscapechar(char escapechar) {
        this.escapechar = escapechar;
        return this;
    }

    public String getLineEnd() {
        return lineEnd;
    }

    public CSVWriterBuilder setLineEnd(String lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

    public CSVWriter buildCSVWriter() {
        return new CSVWriter(writer, separator, quotechar, escapechar, lineEnd);
    }
}
