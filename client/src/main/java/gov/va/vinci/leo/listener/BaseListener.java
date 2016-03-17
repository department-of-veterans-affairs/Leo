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

import gov.va.vinci.leo.types.CSI;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * BaseListener class that other listeners can extend from to add
 * default functionality commonly used.
 */
public abstract class BaseListener extends UimaAsBaseCallbackListener {

    /**
     * The list of input types to save to the database. If set, only documents
     * with annotations of a type in the list, those annotations, and their related feature
     * are saved.
     */
    protected String[] inputType = new String[0];

    /**
     * Output Directory for this gov.va.vinci.leo.listener output if required.
     */
    protected File mOutputDir = null;
    /**
     * Number of CAS objects returned from the pipeline.
     */
    protected int numReceived = 0;
    /**
     * Number of CAS objects sent to the service for processing.
     */
    protected int numSent = 0;
    /**
     * Flag to indicate whether or not entityProcessComplete processing should
     * terminate when exceptions are received from the service.
     */
    protected boolean exitOnError = false;
    /**
     * Exceptions Received from service via entityProcessComplete event.
     */
    protected String lastException = "";
    /**
     * ID of the document received in an entityProcessComplete event.
     */
    protected String referenceID = null;
    /**
     * CSI annotation for the document being processed in an
     * entityProcessComplete event.
     */
    protected CSI docInfo = null;
    /**
     * Logging object of output.
     */
    protected Logger LOG = Logger.getLogger(this.getClass());


    /**
     * If true, the base listener will log errors via log4j.
     */
    protected boolean logErrors = false;


    /**
     * the annotation types to filter on. If annotation types are included,
     * sub-classes can call hasFilteredAnnotation on this class to determine
     * if the cas has one or more of the filter annotation types.
     * Type is the string name, ie: gov.va.vinci.uima.type.Token
     */
    protected String[] annotationTypeFilter = new String[]{};

    /**
     * Use to work around a bug in 2.4.2 where UIMA calls the  onBeforeMessageSend one extra time
     * on the last document.
     * <p/>
     * https://issues.apache.org/jira/browse/UIMA-3581?jql=project%20%3D%20UIMA%20AND%20resolution%20%3D%20Unresolved%20AND%20assignee%20%3D%20cwiklik%20ORDER%20BY%20priority%20DESC
     */
    protected String previousReferenceId = "NONE";

    /**
     * Default Constructor.
     */
    public BaseListener() {
        /** No Initialization **/
    }// default constructor

    /**
     * Called once client initialization is complete.
     *
     * @param aStatus the status of the processing.
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#initializationComplete(org.apache.uima.collection.EntityProcessStatus)
     */
    @Override
    public void initializationComplete(EntityProcessStatus aStatus) {
        if (aStatus != null && aStatus.isException()) {
            LOG.error("Exceptions thrown on getMeta call to remote service...");
            List<Exception> exceptions = aStatus.getExceptions();
            for (Exception exception : exceptions) {
                LOG.error(exception.getMessage(), exception);
            }// for
        }// if aStatus != null && isException
        LOG.info("===== Client Initialization Complete =====");
    }// initializationComplete method

    /**
     * Called before sending a CAS to the pipeline, If we were timing each CAS individually this is where
     * we would want to capture individual start times.
     *
     * @param status the status of the processing.
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#onBeforeMessageSend(org.apache.uima.aae.client.UimaASProcessStatus)
     */
    @Override
    public void onBeforeMessageSend(UimaASProcessStatus status) {
        super.onBeforeMessageSend(status);
        if (!previousReferenceId.equals(status.getCasReferenceId())) {
            if (status.getCasReferenceId() != null) {
                previousReferenceId = status.getCasReferenceId();
            }
            numSent++;
        }
    }// onBeforeMessageSend method

    /**
     * @param aCas    the CAS containing the processed entity and the analysis results
     * @param aStatus the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS, org.apache.uima.collection.EntityProcessStatus)
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        LOG.debug("EntityProcessComplete event");
        super.entityProcessComplete(aCas, aStatus);
        numReceived++;
        if (checkForError(aCas, aStatus) && exitOnError)
            return;
        try {
            this.referenceID = getReferenceLocation(aCas.getJCas());
        } catch (CASException e) {
            this.referenceID = null;
            this.docInfo = null;
            LOG.error(e.getMessage(), e);
        }// catch
    }// entityProcessComplete

    /**
     * @param aStatus the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
     */
    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus);
        //Ensure all CAS have come back before continuing
        int count = 0;
        while (numSent != numReceived && count < 10) {
            LOG.warn("Have not recieved all CAS's from the Service, sleeping for 2 seconds");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
            count++;
        }//while
        if (numSent != numReceived)
            LOG.warn("Did not recieve all CAS's back from the service.");

        LOG.info("Collection Processing Stats: \n" + "Sent CAS Count: "
                + numSent + "\n" + "Received CAS Count: " + numReceived);
    }// collectionProcessComplete method

    /**
     * Return the output directory File object for this listener.
     *
     * @return Output Directory File object
     */
    public File getOutputDir() {
        return mOutputDir;
    }

    /**
     * Set the output directory for this listener.
     *
     * @param mOutputDir output directory
     * @return reference to this listener instance
     */
    public <T extends BaseListener> T setOutputDir(File mOutputDir) {
        this.mOutputDir = mOutputDir;
        return (T) this;
    }

    /**
     * The number of CAS objects sent to the service.
     *
     * @return number of CAS objects sent up to the service
     */
    public int getNumSent() {
        return numSent;
    }// getNumSent method

    /**
     * Return the number of CAS objects that were returned by the service.
     *
     * @return number of CAS objects received from the service
     */
    public int getNumReceived() {
        return numReceived;
    }// getNumReceived method

    /**
     * If true then the listener will exit the entityProcessComplete event when an error occurs.
     *
     * @return true if the listener should exit when errors occur.
     */
    public boolean isExitOnError() {
        return exitOnError;
    }

    /**
     * Set the flag that if true will cause the entityProcessComplete method to return when an error occurs.
     *
     * @param exitOnError value to set.
     */
    public <T extends BaseListener> T setExitOnError(boolean exitOnError) {
        this.exitOnError = exitOnError;
        return (T) this;
    }

    /**
     * Get the array of input type names this listener will use.
     *
     * @return input types array
     */
    public String[] getInputType() {
        return inputType;
    }

    /**
     * Set the array of input type names.  Extending listeners should only output annotations from this list.
     *
     * @param inputType input type name(s)
     * @return reference to this listener instance
     */
    public <T extends BaseListener> T setInputType(String...inputType) {
        this.inputType = inputType;
        return (T) this;
    }

    /**
     * Return true if an exception was received from the service when the
     * entityProcessComplete event was thrown.
     *
     * @return True if an exception was received, false otherwise
     */
    public boolean entityProcessError() {
        return StringUtils.isNotBlank(this.lastException);
    }// isEntityProcessError method

    /**
     * Return the document ID based on the CSI or SourceDocumentInformation
     * objects.
     *
     * @param jcas JCas object where the annotations are stored
     * @return String object containing the document ID or null if no ID is
     * found
     */
    protected String getReferenceLocation(JCas jcas) {
        Iterator<Annotation> it = null;
        try {
            if (jcas.getAnnotationIndex(CSI.type).iterator().hasNext()) {
                it = jcas.getAnnotationIndex(CSI.type).iterator();
                CSI srcDocInfo = (CSI) it.next();
                if (srcDocInfo != null)
                    this.docInfo = srcDocInfo;
                return srcDocInfo.getID();
            } else if (jcas.getAnnotationIndex(SourceDocumentInformation.type)
                    .iterator().hasNext()) {
                it = jcas.getAnnotationIndex(SourceDocumentInformation.type)
                        .iterator();
                SourceDocumentInformation srcDocInfo = (SourceDocumentInformation) it
                        .next();
                return srcDocInfo.getUri();
            } else {
                return null;
            }// else
        } catch (Exception e) {
            // Happens when CSI is not in the descriptor.
            return null;
        }
    }// getReferenceLocation method

    /**
     * See if the CAS has ANY annotations of the types specified in annotationTypeFilter.
     *
     * @param aCas the CAS to check
     * @return true if the CAS has ANY of the types, false if it does not
     */
    public boolean hasFilteredAnnotation(CAS aCas) {
        //Return true if there is no filter set
        if (this.annotationTypeFilter == null || this.annotationTypeFilter.length == 0)
            return true;
        //Check for one of the filter types
        for (String type : this.annotationTypeFilter) {

            Type uimaType = aCas.getTypeSystem().getType(type);

            if (aCas.getAnnotationIndex(uimaType) != null && aCas.getAnnotationIndex(uimaType).size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for an error in the return status of the processed CAS. Return true
     * if there is an error.
     *
     * @param aCas    CAS that was processed
     * @param aStatus Status object that contains the exception if one is thrown
     * @return True if an error was returned, False otherwise
     */
    protected boolean checkForError(CAS aCas, EntityProcessStatus aStatus) {
        if (aStatus != null && aStatus.isException()) {
            List<Exception> exceptions = aStatus.getExceptions();
            String errors = "";
            this.lastException = "";
            for (Exception exception : exceptions) {
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                exception.printStackTrace(printWriter);
                errors += result.toString() + "\n";
                errors += exception.toString() + "\n---\n";

            }// for

            this.lastException = errors;

            // Write exception out to file if the output directory is set.
            if (logErrors) {
                try {
                    LOG.error("ERROR processing CAS: " + this.getReferenceLocation(aCas.getJCas()) + "\n" + errors);
                } catch (CASException e) {
                    e.printStackTrace();
                }
            }// if

            return true;
        }// if aStatus != null && isException
        return false;
    }// checkForError

    /**
     * gets the list of annotation types to be filtered on. A CAS can be checked via
     * hasFilteredAnnotation(CAS aCas) against this list.
     *
     * @return the list of annotation types to be filtered on. A CAS can be checked via
     * hasFilteredAnnotation(CAS aCas) against this list.
     */
    public String[] getAnnotationTypeFilter() {
        return annotationTypeFilter;
    }

    /**
     * Sets a list of annotation types. Type is the string name, ie: gov.va.vinci.uima.type.Token
     *
     * @param annotationTypeFilter a list of annotation types. Type is the string name, ie: gov.va.vinci.uima.type.Token
     */
    public <T extends BaseListener> T setAnnotationTypeFilter(String... annotationTypeFilter) {
        this.annotationTypeFilter = annotationTypeFilter;
        return (T) this;
    }

    /**
     * If true, when the service returns an error status, it is logged via log4j.
     *
     * @return true if errors from the service are logged via log4j, false if they are not.
     */
    public boolean isLogErrors() {
        return logErrors;
    }


    /**
     * Set whether or not to log errors to log4j. If true, when the service returns an error status, it is logged via log4j.
     */
    public void setLogErrors(boolean logErrors) {
        this.logErrors = logErrors;
    }

    /**
     * See if the cas has any annotations of the input type (if input types are specified).
     *
     * @param aCas the CAS to check
     * @return true if the CAS has any annotations of type in inputType, or true if no inputTypes are specified.
     */
    protected boolean hasAnnotationsToProcess(CAS aCas) {
        //If no input types are set then process all types
        if (inputType == null || inputType.length == 0) {
            List<String> typesList = getInputTypesList(aCas);
            inputType = typesList.toArray(new String[typesList.size()]);
        }
        // See if we are looking for specific types.
        for (String type : inputType) {
            Type uimaType = aCas.getTypeSystem().getType(type);

            if (aCas.getAnnotationIndex(uimaType) != null && aCas.getAnnotationIndex(uimaType).size() > 0) {
                return true;
            }
        }
        return false;
    }

    protected List<String> getInputTypesList(CAS cas) {
        ArrayList<String> typesList = new ArrayList<>();
        if(inputType != null && inputType.length > 0) {
            typesList.addAll(Arrays.asList(inputType));
        } else {
            //Add all non uima types to the list since specific input types not set
            Iterator<Type> typeIterator = cas.getTypeSystem().getTypeIterator();
            while(typeIterator.hasNext()) {
                String typeName = typeIterator.next().getName();
                if(!typeName.startsWith("uima")) {
                    typesList.add(typeName);
                }
            }
        }
        return typesList;
    }

}// BaseListener class
