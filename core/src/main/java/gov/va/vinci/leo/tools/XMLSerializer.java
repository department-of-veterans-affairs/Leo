/**
 *
 */
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

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Use SAX to serialize XML data out to a file.
 *
 * @author thomasginter
 */
public class XMLSerializer {

    /**
     * TransformerHandler for "interpretation" from DOM to SAX.
     */
    private TransformerHandler mHandler = null;

    /**
     * mTransformer for writing out xml in SAX.
     */
    private Transformer mTransformer = null;

    /**
     * OutputStream for serializing to a file.
     */
    private OutputStream mOutStream = null;

    /**
     * Writer for serializing to a file.
     */
    private Writer mWriter = null;

    /**
     * Default Constructor, setup basic XML output properties such as Encoding, Method, and Indent.
     *
     * @throws TransformerConfigurationException  if there is an exception with the Transformer configuration.
     *
     * @throws TransformerFactoryConfigurationError  if there is an exception with the Transformer Factory.
     *
     */
    public XMLSerializer() throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        mHandler = ((SAXTransformerFactory) SAXTransformerFactory.newInstance()).newTransformerHandler();
        mTransformer = mHandler.getTransformer();

        //Set output format
        mTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        mTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        mTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
        mTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    }//Default constructor

    /**
     * Constructor with file path input paramter, uses the path to setup the output stream.
     *
     * @param filename Path to the file that will be written out
     * @throws Exception if there are any Transformer or output stream exceptions.
     */
    public XMLSerializer(String filename) throws Exception {
        this();
        setOutputStream(filename);
    }//Constructor with string input

    /**
     * Constructor with File Handle input.  Uses the file handle for the output stream.
     *
     * @param fileHandle File Handle used for output stream.
     * @throws Exception if there are any Transformer or output stream exceptions.
     */
    public XMLSerializer(File fileHandle) throws Exception {
        this();
        setOutputStream(fileHandle);
    }//Constructor with File handle as input

    /**
     * Constructor with outputStream input.  Uses the Output Stream for serializing the file.
     *
     * @param outputStream OutputStream used to serialize the file.
     * @throws Exception if there are any Transformer or output stream exceptions.
     */
    public XMLSerializer(OutputStream outputStream) throws Exception {
        this();
        setOutputStream(outputStream);
    }//Constructor with OutputStream input

    /**
     * Constructor with Writer input.  Use the Writer object to serialize the file.
     *
     * @param writer Writer to be used for file output
     * @throws Exception if there are any Transformer or output stream exceptions.
     */
    public XMLSerializer(Writer writer) throws Exception {
        this();
        setWriter(writer);
    }//Constructor with Writer input

    /**
     * Set the OutputStream for this serializer from a String file name.
     *
     * @param fileName  the file name, including path, to write out to.
     * @throws java.io.FileNotFoundException if there is an error creating the file.
     */
    public void setOutputStream(String fileName) throws FileNotFoundException {
        setOutputStream(new File(fileName));
    }//setOutputStream method with string input for file name/path

    /**
     * Set the OutputStream for this serializer from a File Handle.
     *
     * @param fileHandle the file handle to write the output to.
     * @throws java.io.FileNotFoundException if there is an error creating the file.
     */
    public void setOutputStream(File fileHandle) throws FileNotFoundException {
        setOutputStream(new FileOutputStream(fileHandle));
    }//setOutputStream method with File Handle

    /**
     * Set the OutputStream for this serializer.
     *
     * @param outputStream the output stream to write the xml to.
     */
    public void setOutputStream(OutputStream outputStream) {
        mWriter = null;
        mOutStream = outputStream;
        mHandler.setResult(createSaxResult());
    }//setOutputStream method

    /**
     * Set the Writer object for this serializer.
     *
     * @param writer the writer to write the xml to.
     */
    public void setWriter(Writer writer) {
        mOutStream = null;
        mWriter = writer;
        mHandler.setResult(createSaxResult());
    }//setWriter method

    /**
     * Creates a StreamResult object from either the mOutputStream or the mWriter, whichever is NOT null.  The
     * StreamResult object is used by the handler to serialize the output to a file.
     *
     * @return null or Result object
     */
    private Result createSaxResult() {
        if (mOutStream != null) {
            return new StreamResult(mOutStream);
        } else if (mWriter != null) {
            return new StreamResult(mWriter);
        } else {
            return null;
        }
    }//createSaxResult method

    /**
     * Allow the user to specify arbitrary properties for output.
     *
     * @param name  Name of the property
     * @param value Value of the property
     */
    public void setOutputProperty(String name, String value) {
        mTransformer.setOutputProperty(name, value);
        //Reset the Result object to reflect the properties
        Result result = createSaxResult();
        if (result != null) {
            mHandler.setResult(result);
        }//if
    }//setOutputProperty method

    /**
     * Return a pointer to the TransformerHandler object created and managed by the serializer.
     *
     * @return TransformerHandler Object for writing the file
     */
    public TransformerHandler getTHandler() {
        return mHandler;
    }//getTHandler method

}//XMLSerializer class
