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
import gov.va.vinci.leo.tools.CSVWriterBuilder;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
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
     * CSVWriterBuilder used to create the CSVWriter.
     */
    protected CSVWriterBuilder writerBuilder = null;

    /**
     * CSVWriter that will format and write the output.
     */
    protected CSVWriter writer = null;

    /**
     * Include a header row at the start of the output file.
     */
    protected boolean includeHeader = false;

    /**
     * Log file handler.
     */
    private final static Logger LOG = Logger.getLogger(LeoUtils.getRuntimeClass().toString());

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
     * Constructor with File parameter.
     *
     * @param file The printwriter to write to.
     * @throws java.io.FileNotFoundException if the file is not found or can't be written to.
     */
    public BaseCsvListener(File file) throws FileNotFoundException {
        this(new PrintWriter(file));
    }

    /**
     * Constructor with an OutputStream parameter.
     *
     * @param stream The stream to write to.
     */
    public BaseCsvListener(OutputStream stream) {
        this(new PrintWriter(stream));
    }

    /**
     * Constructor with a Writer parameter.
     *
     * @param writer The writer to use for output
     */
    public BaseCsvListener(Writer writer) {
        this.writerBuilder = new CSVWriterBuilder(writer);
    }

    /**
     * Get the separator character.
     *
     * @return separator character
     */
    public char getSeparator() {
        return writerBuilder.getSeparator();
    }

    /**
     * Set the separator character.
     *
     * @param separator separator charactor
     * @return reference to this listener instance
     */
    public <T extends BaseCsvListener> T setSeparator(char separator) {
        writerBuilder.setSeparator(separator);
        return (T) this;
    }

    /**
     * Get the quote character.
     *
     * @return quote character
     */
    public char getQuotechar() {
        return writerBuilder.getQuotechar();
    }

    /**
     * Set the quote character.
     *
     * @param quotechar quote character
     * @return reference to this listener instance
     */
    public <T extends BaseCsvListener> T setQuotechar(char quotechar) {
        writerBuilder.setQuotechar(quotechar);
        return (T) this;
    }

    /**
     * Get the escape character.
     *
     * @return escape character
     */
    public char getEscapechar() {
        return writerBuilder.getEscapechar();
    }

    /**
     * Set the escape character.
     *
     * @param escapechar escape character
     * @return reference to this listener instance
     */
    public <T extends BaseCsvListener> T setEscapechar(char escapechar) {
        writerBuilder.setEscapechar(escapechar);
        return (T) this;
    }

    /**
     * Get the line ending string.
     *
     * @return line ending string
     */
    public String getLineEnd() {
        return writerBuilder.getLineEnd();
    }

    /**
     * Set the line ending.
     *
     * @param lineEnd line ending
     * @return reference to this listener instance
     */
    public <T extends BaseCsvListener> T setLineEnd(String lineEnd) {
        writerBuilder.setLineEnd(lineEnd);
        return (T) this;
    }

    /**
     * Return the flag value that if true will cause the listener to include a header row, defaults false.
     *
     * @return include header boolean
     */
    public boolean isIncludeHeader() {
        return includeHeader;
    }

    /**
     * Set the boolean flag that if true will cause the listener to include a header row.
     *
     * @param includeHeader boolean flag for header row
     * @return reference to this listener instance
     */
    public <T extends BaseCsvListener> T setIncludeHeader(boolean includeHeader) {
        this.includeHeader = includeHeader;
        return (T) this;
    }

    /**
     * Write out headers to the writerBuilder for this gov.va.vinci.leo.listener. The idea being this might
     * be called once before the process is started to write out column headers to the
     * csv file.
     *
     * @throws IOException  if the headers cannot be written.
     */
    public void writeHeaders() throws IOException {
        if(writer == null)
            writer = writerBuilder.buildCSVWriter();
        writer.writeNext(getHeaders());
        writer.flush();
    }

    /**
     * Called once client initialization is complete.
     *
     * @param aStatus the status of the processing.
     * @see UimaAsBaseCallbackListener#initializationComplete(EntityProcessStatus)
     */
    @Override
    public void initializationComplete(EntityProcessStatus aStatus) {
        super.initializationComplete(aStatus);
        if(includeHeader)
            try {
                writeHeaders();
            } catch (IOException e) {
                LOG.error("Error writing the headers!", e);
            }
    }

    /**
     * Called with a completely processed CAS.
     *
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

        if(writer == null)
            writer = writerBuilder.buildCSVWriter();
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
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}