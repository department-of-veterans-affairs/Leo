package gov.va.vinci.leo;

import gov.va.vinci.leo.cr.LeoCollectionReaderInterface;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.listener.MockUimaASProcessStatus;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.AsynchAECasManager_impl;
import org.apache.uima.aae.client.UimaASProcessStatusImpl;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceManager;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gov.va.vinci.leo.SampleService.simpleServiceDefinition;

/**
 * Created by thomasginter on 3/30/16.
 */
public class MockClient extends Client {

    /**
     * Default Constructor, will try to load properties from file located at
     * conf/leo.properties.
     *
     * @param uaListeners Listeners that will catch Service callback events
     */
    public MockClient(UimaAsBaseCallbackListener... uaListeners) {
        this.mUAEngine = new LeoEngine();

        this.loadDefaults();
        if (uaListeners != null) {
            for (UimaAsBaseCallbackListener uab : uaListeners) {
                this.addUABListener(uab);
            }//for
        }//if
    }

    /**
     * Constructor with properties file input for client execution context.
     *
     * @param propertiesFile the full path to the properties file for client properties.
     * @param uaListeners    Listeners that will catch Service callback events
     * @throws Exception if there is an error loading properties.
     */
    public MockClient(String propertiesFile, UimaAsBaseCallbackListener... uaListeners) throws Exception {
        super(propertiesFile, uaListeners);
    }

    /**
     * Constructor with properties file input for client execution context, a collection reader for input, and option listeners.
     *
     * @param propertiesFile   the full path to the properties file for client properties.
     * @param collectionReader the input collection reader for this client.
     * @param uaListeners      Listeners that will catch Service callback events
     * @throws Exception if there is an error loading properties.
     */
    public MockClient(String propertiesFile, LeoCollectionReaderInterface collectionReader, UimaAsBaseCallbackListener... uaListeners) throws Exception {
        super(propertiesFile, collectionReader, uaListeners);
    }

    public LeoEngine getEngine() {
        return (LeoEngine) this.mUAEngine;
    }

    /**
     * Run with the collection reader that is already set in the client.
     *
     * @param uabs List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(UimaAsBaseCallbackListener... uabs) throws Exception {
        super.run(uabs);
    }

    /**
     * Execute the AS pipeline using the LeoCollectionReaderInterface object.
     *
     * @param collectionReader LeoCollectionReaderInterface that will produce CASes for the pipeline
     * @param uabs             List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(LeoCollectionReaderInterface collectionReader, UimaAsBaseCallbackListener... uabs) throws Exception {
        super.run(collectionReader, uabs);
    }

    /**
     * Run the pipeline on one document at a time using the document text provided.
     *
     * @param stream the input stream to read the document from.
     * @param uabs   List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(InputStream stream, UimaAsBaseCallbackListener... uabs) throws Exception {
        super.run(stream, uabs);
    }

    /**
     * Run the pipeline on one document at a time using the document text provided.
     *
     * @param documentText the document text to process.
     * @param uabs         List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(String documentText, UimaAsBaseCallbackListener... uabs) throws Exception {
        super.run(documentText, uabs);
    }

    /**
     * Run the pipeline on a single CAS object.
     *
     * @param cas  an individual cass to process through the client.
     * @param uabs List of Listeners that will catch callback events from the service
     * @throws Exception if any error occurs during processing
     */
    @Override
    public void run(CAS cas, UimaAsBaseCallbackListener... uabs) throws Exception {
        super.run(cas, uabs);
    }

    //Mock Engine methods allowing us to run as though there were a service up and running
    public static class LeoEngine extends Client.LeoEngine {

        AnalysisEngine ae = null;

        public LeoEngine() {
            try {
                ae = UIMAFramework.produceAnalysisEngine(simpleServiceDefinition().getAnalysisEngineDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void setAnalysisEngineFromDescription(LeoAEDescriptor descriptor) {
            if(descriptor == null) {
                throw new RuntimeException("Cannot create AnalysisEngine from null descriptor!");
            }
            try {
                ae = UIMAFramework.produceAnalysisEngine(descriptor.getAnalysisEngineDescription());
            } catch (ResourceInitializationException e) {
                throw new RuntimeException(e);
            }
        }

        public AnalysisEngine getAnalysisEngine() {
            return ae;
        }

        public synchronized void initialize(Map anApplicationContext) throws ResourceInitializationException {
            if (!anApplicationContext.containsKey(UimaAsynchronousEngine.ServerUri)) {
                throw new ResourceInitializationException();
            }
            if (!anApplicationContext.containsKey(UimaAsynchronousEngine.ENDPOINT)) {
                throw new ResourceInitializationException();
            }
            ResourceManager rm = null;
            if (anApplicationContext.containsKey(Resource.PARAM_RESOURCE_MANAGER)) {
                rm = (ResourceManager) anApplicationContext.get(Resource.PARAM_RESOURCE_MANAGER);
            } else {
                rm = UIMAFramework.newDefaultResourceManager();
            }
            asynchManager = new AsynchAECasManager_impl(rm);

            brokerURI = (String) anApplicationContext.get(UimaAsynchronousEngine.ServerUri);
            List listeners = this.getListeners();
            for (int i = 0; listeners != null && i < listeners.size(); i++) {
                UimaAsBaseCallbackListener listener = (UimaAsBaseCallbackListener) listeners.get(i);
                listener.initializationComplete(null);
            }
            initialized = true;
            remoteService = true;
            running = true;
            state = ClientState.RUNNING;
        }

        public synchronized void process() throws ResourceProcessException {
            try {
                List listeners = this.getListeners();
                while (collectionReader.hasNext()) {
                    CAS cas = ae.newCAS();
                    collectionReader.getNext(cas);
                    sendCAS(cas);
                }
                this.collectionProcessingComplete();
            } catch (Exception e) {
                throw new ResourceProcessException(e);
            }
        }

        public synchronized String sendCAS(CAS aCAS) throws ResourceProcessException {
            List listeners = this.getListeners();
            ae.process(aCAS);
            for (int i = 0; listeners != null && i < listeners.size(); i++) {
                UimaAsBaseCallbackListener listener = (UimaAsBaseCallbackListener) listeners.get(i);
                listener.onBeforeMessageSend(new MockUimaASProcessStatus());
                listener.entityProcessComplete(aCAS, null);
            }
            return UUID.randomUUID().toString();
        }

        public CAS getCAS() throws Exception {
            return ae.newCAS();
        }

        public synchronized void collectionProcessingComplete() throws ResourceProcessException {
            List listeners = this.getListeners();
            for (int i = 0; listeners != null && i < listeners.size(); i++) {
                UimaAsBaseCallbackListener listener = (UimaAsBaseCallbackListener) listeners.get(i);
                listener.collectionProcessComplete(null);
            }
        }

        public void stop() {
            running = false;
        }
    }
}
