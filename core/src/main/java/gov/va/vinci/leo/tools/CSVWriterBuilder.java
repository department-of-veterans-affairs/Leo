package gov.va.vinci.leo.tools;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.PrintWriter;
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
