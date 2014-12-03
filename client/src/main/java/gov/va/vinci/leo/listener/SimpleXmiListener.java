/**
 *
 */
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


import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.jcas.JCas;
import org.apache.uima.tools.AnnotationViewerMain;

import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;


/**
 * Store processed CAS objects in XMI files then launch the AnnotationViewer once collection
 * processing is complete.
 *
 * @author thomasginter
 */
public class SimpleXmiListener extends BaseListener {

    /**
     * Flag to launch annotation viewer when processing is complete.
     */
    private boolean launchAnnotationViewer = false;

    /**
     * Path to the aggregate descriptor file for the service.
     */
    private String mAggDesc = null;
    /**
     * Logging object of output.
     */
    public static final Logger log = Logger.getLogger(SimpleXmiListener.class.getName());

    /**
     * Constructor with output directory input.
     *
     * @param outputDir             the directory to write the final xmi files to.
     */
    public SimpleXmiListener(File outputDir) {
        super(outputDir, false);
        mOutputDir = outputDir;
    }//Constructor with String input param

    /**
     * Constructor with output directory input.
     *
     * @param outputDir              the directory to write the final xmi files to.
     * @param launchAnnotationViewer launch the UIMA annotation viewer when complete.
     */
    public SimpleXmiListener(File outputDir, boolean launchAnnotationViewer) {
        this(outputDir);
        this.launchAnnotationViewer = launchAnnotationViewer;
    }//Constructor with String input param

    /**
     * Constructor with output directory input and aggDesc.
     *
     * @param outputDir              the directory to write the final xmi files to.
     * @param aggDesc               Path to the aggregate descriptor file for the service.
     */
    public SimpleXmiListener(File outputDir, String aggDesc) {
        this(outputDir);
        mAggDesc = aggDesc;
    }//Constructor with String input param and aggDesc

    /**
     * Constructor with output directory input and aggDesc.
     *
     * @param outputDir              the directory to write the final xmi files to.
     * @param aggDesc               Path to the aggregate descriptor file for the service.
     * @param launchAnnotationViewer launch the UIMA annotation viewer when complete.
     */
    public SimpleXmiListener(File outputDir, String aggDesc, boolean launchAnnotationViewer) {
        this(outputDir, aggDesc);
        this.launchAnnotationViewer = launchAnnotationViewer;
    }

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#entityProcessComplete(org.apache.uima.cas.CAS, org.apache.uima.collection.EntityProcessStatus)
     * @param aCas      the CAS containing the processed entity and the analysis results
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);

        //Get the original file name
        String doc = null;
        JCas jcas;

        /**
         * If we have an annotation filter, check to see if this document needs processed.
         */
        if (getAnnotationTypeFilter().length > 0 && !this.hasFilteredAnnotation(aCas)) {
            return;
        }
        try {
            jcas = aCas.getJCas();
            doc = this.getReferenceLocation(jcas);
        } catch (CASException e1) {
            /**calculate the name another way**/
            log.error(e1.getMessage());
        }

        //Check for an exception
        if (aStatus != null && aStatus.isException()) {
            log.error("Exceptions thrown on getMeta call to remote service...");
            List<Exception> exceptions = aStatus.getExceptions();
            StringBuilder errors = new StringBuilder();
            for (Exception exception : exceptions) {
                log.error(exception.getMessage(), exception);
                Writer result = new StringWriter();
                PrintWriter printWriter = new PrintWriter(result);
                exception.printStackTrace(printWriter);
                errors.append(exception.toString() + "\n---\n");
                errors.append(result.toString() + "\n\n");
            }//for

            // Write exception out to file.
            BufferedWriter s = null;
            try {
                File errorOutFile = new File(mOutputDir, doc + ".err");
                s = new BufferedWriter(new FileWriter(errorOutFile));
                s.write(errors.toString());
            } catch (Exception e) {
                log.error("Write error: " + e);
            } finally {
                try {
                    s.close();
                } catch (Exception e) {
                    log.warn("Could not close stream: " + e);
                }
            }

            return;
        }//if aStatus != null && isException


        if (doc == null || doc.equals("")) {
            String casID = ((UimaASProcessStatus) aStatus).getCasReferenceId();
            doc = casID + ".xmi";
        } else {
            doc += ".xmi";
        }

        FileOutputStream xmiOut = null;
        try {
            File xmiOutFile = new File(mOutputDir, doc);
            xmiOut = new FileOutputStream(xmiOutFile);
            XmiCasSerializer.serialize(aCas, xmiOut);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                xmiOut.close();
            } catch (Exception e) {
                log.warn("Could not close file: " + e);
            }
        }

    }//entityProcessComplete method

    /**
     * @see org.apache.uima.aae.client.UimaAsBaseCallbackListener#collectionProcessComplete(org.apache.uima.collection.EntityProcessStatus)
     * @param aStatus   the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     */
    @Override
    public void collectionProcessComplete(EntityProcessStatus aStatus) {
        super.collectionProcessComplete(aStatus);

        if (launchAnnotationViewer) {
            launchAnnotationViewer();
        }
    }//collectionProcessComplete method

    /**
     * Launch the UIMA annotation viewer.
     */
    protected void launchAnnotationViewer() {
        Preferences prefs = Preferences.userRoot().node("org/apache/uima/tools/AnnotationViewer");
        if (mAggDesc != null) {
            prefs.put("taeDescriptorFile", mAggDesc);
        }//if mAggDesc != null
        if (mOutputDir != null) {
            try {
                prefs.put("inDir", mOutputDir.getCanonicalPath());
            } catch (IOException e) {
                log.error("Could not get output directory canonical path to set for annotation viewer.");
            }
        }//if mOutputDir != null
        AnnotationViewerMain avm = new AnnotationViewerMain();
        avm.setBounds(0, 0, 1000, 225);
        avm.setVisible(true);
    }

    /**
     * True if the annotation viewer should be launched after processing is complete, else false.
     * @return     True if the annotation viewer should be launched after processing is complete, else false.
     */
    public boolean isLaunchAnnotationViewer() {
        return launchAnnotationViewer;
    }

    /**
     * Set the value of launch annotation viewer. If true, when collectionProcessComplete is finished,
     * the annotation view will be launched.
     *
     * @param launchAnnotationViewer   True if the annotation viewer should be launched after processing is complete, else false.
     */
    public void setLaunchAnnotationViewer(boolean launchAnnotationViewer) {
        this.launchAnnotationViewer = launchAnnotationViewer;
    }


}//XmiUABListener class
