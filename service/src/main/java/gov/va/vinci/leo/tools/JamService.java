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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * This is part of the Jmx Analytics Monitor package, and allows a client
 * to register with a running JAM instance anywhere on the network using HTTP
 * Web Services.
 */
public class JamService {
    /**
     * The HTTP Client used for making requests to the web services.
     */
    private HttpClient client = new HttpClient();

    /**
     * The base url of the jam server that requests will be based on.
     */
    private String jamServerBaseUrl = "";


    /**
     * The base url for the JAM server. For example: http://localhost:8080/jam/
     *
     * @param jamServerBaseUrl the base url for the JAM server to communicate with.
     */
    public JamService(String jamServerBaseUrl) {
        if (jamServerBaseUrl.endsWith("/")) {
            this.jamServerBaseUrl = jamServerBaseUrl;
        } else {
            this.jamServerBaseUrl = jamServerBaseUrl + "/";
        }
    }

    /**
     * Return the HttpClient used for communication, or null if one has not been set/created yet.
     * @return   the HttpClient used for communication
     */
    public HttpClient getClient() {
        return client;
    }

    /**
     * Set the HttpClient to use for communication. Generally not used, and the default HttpClient inside of this
     * object is used.
     * @param client An HttpClient to use for communication.
     */
    public void setClient(HttpClient client) {
        this.client = client;
    }

    /**
     * Get the JAM server base url. (ie - http://localhost:8080/jam/)
     * @return  the JAM server base url
     */
    public String getJamServerBaseUrl() {
        return jamServerBaseUrl;
    }

    /**
     * Set the JAM server base url. (ie - http://localhost:8080/jam/)
     * @param jamServerBaseUrl  the JAM server base url
     */
    public void setJamServerBaseUrl(String jamServerBaseUrl) {
        this.jamServerBaseUrl = jamServerBaseUrl;
    }

    /**
     * A web service that add a server to an existing service queue.
     *
     * @param queueName the name of the queue to add this host to.
     * @param host      the host name to add to the service queue
     * @param port      the port on the host that is added to the service queue
     * @param brokerUrl  the broker url this service is using.
     * @throws IOException if any communication exception occurs.
     */
    public void addServerToServiceQueue(final String queueName, final String brokerUrl,
                                        final String host, final int port) throws  IOException {
        String requestBody = createJmxServerXml(queueName, brokerUrl, host, port);
        PutMethod method = new PutMethod(jamServerBaseUrl + "webservice/addServerToServiceQueue");
        method.setRequestBody(requestBody);
        doHttpCall(method);
    }


    /**
     * Disable monitoring of a service queue.
     *
     * @param queueName the queue name to disable.
     * @throws IOException if any communication exception occurs.
     */
    public void disableServiceQueue(final String queueName) throws IOException {
        doHttpCall(new GetMethod(jamServerBaseUrl + "webservice/disableServiceQueue/" + URLEncoder.encode(queueName)));
    }


    /**
     * Given a queue name, determine if it already exists.
     *
     * @param queueName the ServiceQueue.queueName to check for
     * @param brokerUrl the broker url this service is using.
     * @return returns TRUE if the service queue name exists, or false if it does not.
     * @throws IOException if any communication exception occurs.
     */
    public boolean doesServiceQueueExist(final String queueName, final String brokerUrl) throws IOException {
        PutMethod method = new PutMethod(jamServerBaseUrl + "webservice/doesServiceQueueExist");

        method.setRequestBody(getServiceQueueExistRequestBody(queueName, brokerUrl));

        String result = doHttpCall(method);
        return "TRUE".equals(result);

    }


    /**
     * Enable monitoring on an existing service queue.
     *
     * @param queueName the name of the queue to enable monitoring on.
     * @throws IOException if any communication exception occurs.
     */
    public void enableServiceQueue(final String queueName) throws IOException {
        doHttpCall(new GetMethod(jamServerBaseUrl + "webservice/enableServiceQueue/" + URLEncoder.encode(queueName)));
    }

    /**
     * Registers a new service queue in Jam. Note: Service queue names MUST be unique in the jam system.
     *
     * @param queueName the name of the queue to register with.
     * @param brokerUrl the broker url this service is using.
     * @param queryIntervalInSeconds the interval in seconds to query the running service for metrics.
     * @param resetStatisticsAfterQuery if true, statistics are reset to 0 after each gather. If false, statistics are
     *                                  cumulative.
     * @param profileEnabled if true the profile is enabled for gathering, if false, it is registered, but not enabled
     *                       for gathering.
     * @throws IOException  if any communication exception occurs.
     */
    public void registerServiceQueue(final String queueName, final String brokerUrl,
                                     final int queryIntervalInSeconds, final boolean resetStatisticsAfterQuery,
                                     final boolean profileEnabled) throws IOException {
        String requestBody = getRegisterServiceRequestBody(queueName, brokerUrl, queryIntervalInSeconds, resetStatisticsAfterQuery, profileEnabled);

        PutMethod method = new PutMethod(jamServerBaseUrl + "webservice/registerServiceQueue");
        method.setRequestBody(requestBody);
        doHttpCall(method);
    }


    /**
     * Remove a host/port from a service queue.
     * <p/>
     * If the host is not part of the service queue, no-op. If it is
     * part of the Service Queue, it is removed.
     * @param queueName the name of the queue to remove.
     * @param brokerUrl the broker url this service is using.
     * @param host the host the service is running on.
     * @param port the port the service is running on.
     *
     * @throws IOException if any communication exception occurs.
     */
    public void removeServerFromServiceQueue(final String queueName, final String brokerUrl,
                                             final String host, final int port) throws IOException {
        String requestBody = createJmxServerXml(queueName, brokerUrl, host, port);
        PutMethod method = new PutMethod(jamServerBaseUrl + "webservice/removeServerFromServiceQueue");
        method.setRequestBody(requestBody);
        doHttpCall(method);
    }

    /**
     * Removes a service queue, and all related hosts and monitoring jobs.
     *
     * @param queueName the queue name to remove.
     * @throws IOException  if any communication exception occurs.
     */
    public void removeServiceQueue(final String queueName) throws IOException {
        doHttpCall(new GetMethod(jamServerBaseUrl + "webservice/removeServiceQueue/" + URLEncoder.encode(queueName)));
    }


    /**
     * Given a queuename and broker, do an immediate gather of stats.
     * @param queueName the queue name of the service.
     * @param brokerUrl  the broker url for the service.
     * @throws IOException  if any communication exception occurs.
     */
    public void runGather(final String queueName, final String brokerUrl) throws IOException {
        String requestBody = getServiceQueueExistRequestBody(queueName, brokerUrl);
        PutMethod method = new PutMethod(jamServerBaseUrl + "webservice/runGather");
        method.setRequestBody(requestBody);
        doHttpCall(method);
    }

    /**
     * Creates the XML request used by other methods when calling the web service. The request is in the format:
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="ISO-8859-1"?>
     * <jmxServer>
     * 		<queueName>queueName</queueName>
     * 		<brokerUrl>brokerUrl</brokerUrl>
     * 		<host>host</host>
     * 		<port>port</port>
     * </jmxServer>
     * }
     * </pre>
     *
     * @param queueName  queue name for this service.
     * @param brokerUrl  UIMA broker url for this service.
     * @param host       Host the service is running on.
     * @param port       Port JMX is enabled on for the service.
     * @return the xml body.
     */
    protected String createJmxServerXml(final String queueName, final String brokerUrl,
                                        final String host, final int port) {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                "	<jmxServer>" +
                "		<queueName>" + StringEscapeUtils.escapeXml(queueName) + "</queueName>" +
                "		<brokerUrl>" + StringEscapeUtils.escapeXml(brokerUrl) + "</brokerUrl>" +
                "		<host>" + StringEscapeUtils.escapeXml(host) + "</host>" +
                "		<port>" + port + "</port>" +
                "</jmxServer>";
    }

    /**
     * Does the HTTP call and returns the response body as a string, or throws and HttpException.
     *
     * @param method The HttpMethod to call.
     * @return Response body as a string.
     * @throws IOException  if any communication exception occurs.
     * @throws HttpException If HttpStatus is NOT SC_OK, an HttpException is thrown with the error.
     */
    protected String doHttpCall(HttpMethod method) throws IOException, HttpException {
        int statusCode = client.executeMethod(method);
        if (statusCode != HttpStatus.SC_OK) {
            throw new HttpException("Method failed: " + method.getStatusLine() + " Response:" + method.getResponseBodyAsString());
        }
        return method.getResponseBodyAsString();
    }

    /**
     * Returns the xml for the registerServiceRequest method. The xml is in the format:
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="ISO-8859-1"?>
     * <serviceQueue>
     *     <queueName>queueName</queueName>
     *     <brokerUrl>brokerUrl</brokerUrl>
     *     <queryIntervalInSeconds>queryIntervalInSeconds</queryIntervalInSeconds>
     *     <resetStatisticsAfterQuery>resetStatisticsAfterQuery</resetStatisticsAfterQuery>
     *     <profileEnabled>profileEnabled</profileEnabled>
     * </serviceQueue>
     * }
     * </pre>
     *
     * @param queueName  the queue name of the service.
     * @param brokerUrl  the broker url for the service.
     * @param queryIntervalInSeconds  the interval in seconds to query the running service for metrics.
     * @param resetStatisticsAfterQuery   if true, statistics are reset to 0 after each gather. If false, statistics are
     *                                    cumulative.
     * @param profileEnabled  if true the profile is enabled for gathering, if false, it is registered, but not enabled
     *                        for gathering.
     * @return returns the register service request body in xml
     */
    protected String getRegisterServiceRequestBody(final String queueName, final String brokerUrl,
                                                   final int queryIntervalInSeconds,
                                                   final boolean resetStatisticsAfterQuery,
                                                   final boolean profileEnabled) {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> " +
                "	<serviceQueue>" +
                "		<queueName>" + StringEscapeUtils.escapeXml(queueName) + "</queueName>" +
                "		<brokerUrl>" + StringEscapeUtils.escapeXml(brokerUrl) + "</brokerUrl>" +
                "		<queryIntervalInSeconds>" + queryIntervalInSeconds + "</queryIntervalInSeconds>" +
                "		<resetStatisticsAfterQuery>" + resetStatisticsAfterQuery + "</resetStatisticsAfterQuery>" +
                "		<profileEnabled>" + profileEnabled + "</profileEnabled>" +
                "</serviceQueue>";
    }

    /**
     * Get the serviceQueueExistRequest body xml. It is in the format:
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="ISO-8859-1"?>
     * <serviceQueue>
     *     <queueName>queueName</queueName>
     *     <brokerUrl>brokerUrl</brokerUrl>
     * </serviceQueue>
     * }
     * </pre>
     *
     * @param queueName  the queue name of the service.
     * @param brokerUrl  the broker url for the service.
     * @return the xml for the serviceQueueExistRequest method.
     */
    protected String getServiceQueueExistRequestBody(final String queueName, final String brokerUrl) {
        return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><serviceQueue><queueName>"
                + StringEscapeUtils.escapeXml(queueName) + "</queueName><brokerUrl>"
                + StringEscapeUtils.escapeXml(brokerUrl) + "</brokerUrl></serviceQueue>";
    }
}
