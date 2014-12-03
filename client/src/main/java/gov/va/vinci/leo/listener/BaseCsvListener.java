package gov.va.vinci.leo.listener;

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

import au.com.bytecode.opencsv.CSVWriter;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;

import java.io.*;
import java.util.List;

/**
 * Base gov.va.vinci.leo.listener for creating CSV output. Implementing classes
 * need to implement getHeaders() and getRows().
 * <p/>
 * If not overridden in the constructors, the default separator is a comma (,),
 * the default quote character is a quote("), the default escape character is a backslash (\),
 * and the default line ending is a newline (\n)
 */
public abstract class BaseCsvListener extends BaseListener {

    /**
     * CSV Writer for writing data out.
     */
    protected CSVWriter writer = null;

    /**
     * Printwriter the CSVWriter will use for output.
     */
    protected PrintWriter pw = null;

    /**
     * Given a cas, return one or more rows of data.
     *
     * @param cas the document cas to get results from
     * @return A list of rows of CSV data. Each row is an array of strings. If no data needs
     *         to be written for this CAS, you should return an empty list.
     */
    protected abstract List<String[]> getRows(CAS cas);

    /**
     * Returns the string list of the headers for a row.
     * ie ("col1", "id", "myValue");
     *
     * @return   the string list of the headers for a row.
     */
    protected abstract String[] getHeaders();


    /**
     * Constructor with a Writer.
     *
     * @param file The printwriter to write to.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw);
    }


    /**
     * Constructor with a Writer, specifying a separator.
     *
     * @param file        The printwriter to write to.
     * @param separator the character to use as a separator in the csv.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file, char separator) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw, separator);
    }

    /**
     * Constructor with a Writer, specifying a separator and quote character.
     *
     * @param file        The printwriter to write to.
     * @param separator the character to use as a separator in the csv.
     * @param quotechar the character to quote values with.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file, char separator, char quotechar) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw, separator, quotechar);
    }

    /**
     * Constructor with a Writer, specifying a separator, quote character, and escape character.
     *
     * @param file         The printwriter to write to.
     * @param separator  the character to use as a separator in the csv.
     * @param quotechar  the character to quote values with.
     * @param escapechar the character to escape quotechar in values.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file, char separator, char quotechar, char escapechar) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw, separator, quotechar, escapechar);
    }

    /**
     * Constructor with a Writer, specifying a separator, quote character, escape character, and line ending.
     *
     * @param file         The printwriter to write to.
     * @param separator  the character to use as a separator in the csv.
     * @param quotechar  the character to quote values with.
     * @param escapechar the character to escape quotechar in values.
     * @param lineend    the string to use as line endings.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file, char separator, char quotechar, char escapechar, String lineend) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw, separator, quotechar, escapechar, lineend);
    }


    /**
     * Constructor with a Writer, specifying a separator, quote character,  and line ending.
     *
     * @param file        The printwriter to write to.
     * @param separator the character to use as a separator in the csv.
     * @param quotechar the character to quote values with.
     * @param lineend   the string to use as line endings.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file, char separator, char quotechar, String lineend) throws FileNotFoundException {
        pw = new PrintWriter(file);
        writer = new CSVWriter(pw, separator, quotechar, lineend);
    }


    /**
     * Constructor with a Writer.
     *
     * @param stream The stream to write to.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw);
    }


    /**
     * Constructor with a Writer, specifying a separator.
     *
     * @param stream        The stream to write to.
     * @param separator the character to use as a separator in the csv.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream, char separator) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw, separator);
    }

    /**
     * Constructor with a Writer, specifying a separator and quote character.
     *
     * @param stream        The stream to write to.
     * @param separator the character to use as a separator in the csv.
     * @param quotechar the character to quote values with.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream, char separator, char quotechar) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw, separator, quotechar);
    }

    /**
     * Constructor with a Writer, specifying a separator, quote character, and escape character.
     *
     * @param stream         The stream to write to.
     * @param separator  the character to use as a separator in the csv.
     * @param quotechar  the character to quote values with.
     * @param escapechar the character to escape quotechar in values.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream, char separator, char quotechar, char escapechar) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw, separator, quotechar, escapechar);
    }

    /**
     * Constructor with a Writer, specifying a separator, quote character, escape character, and line ending.
     *
     * @param stream         The stream to write to.
     * @param separator  the character to use as a separator in the csv.
     * @param quotechar  the character to quote values with.
     * @param escapechar the character to escape quotechar in values.
     * @param lineend    the string to use as line endings.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream, char separator, char quotechar, char escapechar, String lineend) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw, separator, quotechar, escapechar, lineend);
    }


    /**
     * Constructor with a Writer, specifying a separator, quote character,  and line ending.
     *
     * @param stream        The stream to write to.
     * @param separator the character to use as a separator in the csv.
     * @param quotechar the character to quote values with.
     * @param lineend   the string to use as line endings.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(OutputStream stream, char separator, char quotechar, String lineend) throws FileNotFoundException {
        pw = new PrintWriter(stream);
        writer = new CSVWriter(pw, separator, quotechar, lineend);
    }

    /**
     * Write out headers to the writer for this gov.va.vinci.leo.listener. The idea being this might
     * be called once before the process is started to write out column headers to the
     * csv file.
     *
     * @throws IOException  if the headers cannot be written.
     */
    public void writeHeaders() throws IOException {
        writer.writeNext(getHeaders());
        writer.flush();
    }

    /**
     * Called with a completely processed CAS.
     * @param aCas   the returned cas.
     * @param aStatus the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);
        List<String[]> rows = getRows(aCas);
        if (rows == null || rows.size() < 1) {
            return;
        }

        writer.writeAll(rows);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
     * @param aStatus the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus);
        pw.flush();
        pw.close();
    }
}