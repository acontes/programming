/******************************************************************************* 
 *  Copyright 2008 Amazon Technologies, Inc.
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  
 *  You may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at: http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 *  CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 *  specific language governing permissions and limitations under the License.
 * ***************************************************************************** 
 *    __  _    _  ___ 
 *   (  )( \/\/ )/ __)
 *   /__\ \    / \__ \
 *  (_)(_) \/\/  (___/
 * 
 *  Amazon EC2 Java Library
 *  API Version: 2008-05-05
 *  Generated: Thu Aug 28 20:50:13 PDT 2008 
 * 
 */

package com.amazonaws.ec2;

/**
 * Configuration for accessing Amazon EC2  service
 */
public class AmazonEC2Config {

    private String serviceVersion = "2008-05-05";
    private String serviceURL = "http://ec2.amazonaws.com";
    private String userAgent = "Amazon EC2 Java Library";
    private String signatureVersion = "1";
    private String proxyHost = null;
    private int proxyPort = -1;
    private String proxyUsername = null;
    private String proxyPassword = null;
    private int maxErrorRetry = 3;
    private int maxAsyncThreads = 30;
    private int maxAsyncQueueSize = maxAsyncThreads * 10;

    /**
     * Gets Version of the API
     *
     * @return Version of the Service
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * Gets SignatureVersion property
     *
     * @return Signature Version for signing requests
     */
    public String getSignatureVersion() {
        return signatureVersion;
    }

    /**
     * Sets SignatureVersion property
     *
     * @param signatureVersion Signature Version for signing requests
     */
    public void setSignatureVersion(String signatureVersion) {
        this.signatureVersion = signatureVersion;
    }

    /**
     * Sets SignatureVersion property and returns current AmazonEC2Config
     *
     * @param signatureVersion Signature Version for signing requests
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withSignatureVersion(String signatureVersion) {
        setSignatureVersion(signatureVersion);
        return this;
    }

    /**
     * Checks if SignatureVersion property is set
     *
     * @return true if SignatureVersion property is set
     */
    public boolean isSetSignatureVersion() {
        return true;
    }

    /**
     * Gets UserAgent property
     *
     * @return User Agent String to use when sending request
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Sets UserAgent property
     *
     * @param userAgent User Agent String to use when sending request
     *
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Sets UserAgent property and returns current AmazonEC2Config
     *
     * @param userAgent User Agent String to use when sending request
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withUserAgent(String userAgent) {
        setUserAgent(userAgent);
        return this;
    }

    /**
     * Checks if UserAgent property is set
     *
     * @return true if UserAgent property is set
     */
    public boolean isSetUserAgent() {
        return this.userAgent != null;
    }

    /**
     * Gets ServiceURL property
     *
     * @return Service Endpoint URL
     */
    public String getServiceURL() {
        return serviceURL;
    }

    /**
     * Sets ServiceURL property
     *
     * @param serviceURL Service Endpoint URL
     *
     */
    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    /**
     * Sets ServiceURL property and returns current AmazonEC2Config
     *
     * @param serviceURL Service Endpoint URL
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withServiceURL(String serviceURL) {
        setServiceURL(serviceURL);
        return this;
    }

    /**
     * Checks if ServiceURL property is set
     *
     * @return true if ServiceURL property is set
     */
    public boolean isSetServiceURL() {
        return this.serviceURL != null;
    }

    /**
     * Gets ProxyHost property
     *
     * @return Proxy Host for connection
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Sets ProxyHost property
     *
     * @param proxyHost Proxy Host for connection
     *
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Sets ProxyHost property and returns current AmazonEC2Config
     *
     * @param proxyHost Proxy Host for connection
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withProxyHost(String proxyHost) {
        setProxyHost(proxyHost);
        return this;
    }

    /**
     * Checks if ProxyHost property is set
     *
     * @return true if ProxyHost property is set
     */
    public boolean isSetProxyHost() {
        return this.proxyHost != null;
    }

    /**
     * Gets ProxyPort property
     *
     * @return Proxy Port for connection
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Sets ProxyPort property
     *
     * @param proxyPort Proxy Port for connection
     *
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * Sets ProxyPort property and returns current AmazonEC2Config
     *
     * @param proxyPort Proxy Port for connection
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withProxyPort(int proxyPort) {
        setProxyPort(proxyPort);
        return this;
    }

    /**
     * Checks if ProxyPort property is set
     *
     * @return true if ProxyPort property is set
     */
    public boolean isSetProxyPort() {
        return this.proxyPort != -1;
    }

    /**
     * Gets ProxyUsername property
     *
     * @return Proxy Username
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * Sets ProxyUsername property
     *
     * @param proxyUsername Proxy Username for connection
     *
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    /**
     * Sets ProxyUsername property and returns current AmazonEC2Config
     *
     * @param proxyUsername Proxy Username for connection
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withProxyUsername(String proxyUsername) {
        setProxyUsername(proxyUsername);
        return this;
    }

    /**
     * Checks if ProxyUsername property is set
     *
     * @return true if ProxyUsername property is set
     */
    public boolean isSetProxyUsername() {
        return this.proxyUsername != null;
    }

    /**
     * Gets ProxyPassword property
     *
     * @return Proxy Password
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * Sets ProxyPassword property
     *
     * @param proxyPassword Proxy Password for connection
     *
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * Sets ProxyPassword property and returns current AmazonEC2Config
     *
     * @param proxyPassword Proxy Password for connection
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withProxyPassword(String proxyPassword) {
        setProxyPassword(proxyPassword);
        return this;
    }

    /**
     * Checks if ProxyPassword property is set
     *
     * @return true if ProxyPassword property is set
     */
    public boolean isSetProxyPassword() {
        return this.proxyPassword != null;
    }

    /**
     * Gets MaxErrorRetry property
     *
     * @return Max number of retries on 500th errors
     */
    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    /**
     * Sets MaxErrorRetry property
     *
     * @param maxErrorRetry Max number of retries on 500th errors
     *
     */
    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
    }

    /**
     * Sets MaxErrorRetry property and returns current AmazonEC2Config
     *
     * @param maxErrorRetry Max number of retries on 500th errors
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withMaxErrorRetry(int maxErrorRetry) {
        setMaxErrorRetry(maxErrorRetry);
        return this;
    }

    /**
     * Checks if MaxErrorRetry property is set
     *
     * @return true if MaxErrorRetry property is set
     */
    public boolean isSetMaxErrorRetry() {
        return this.maxErrorRetry > 0;
    }

    /**
     * Gets MaxAsyncThreads property
     *
     * @return Max number of threads to be used for async operations
     */
    public int getMaxAsyncThreads() {
        return maxAsyncThreads;
    }

    /**
     * Sets MaxAsyncThreads property
     *
     * @param maxAsyncThreads Max number of threads to spawn for async operation.
     * Note, this parameter works in conjuction with MaxAsyncQueueSize. When
     * Max number of threads is reached, requests will be stored in the queue. 
     * If queue becomes full, request will be executed on the calling thread.
     * 
     * There is not a perfect combination of these two parameters, it will depend
     * on your particular environment. Experiment.
     * 
     */
    public void setMaxAsyncThreads(int maxAsyncThreads) {
        this.maxAsyncThreads = maxAsyncThreads;
    }

    /**
     * Sets MaxAsyncThreads property and returns current AmazonEC2Config
     *
     * @param maxAsyncThreads Max number of threads to spawn for async operation.
     * Note, this parameter works in conjuction with MaxAsyncQueueSize. When
     * Max number of threads is reached, requests will be stored in the queue.
     *  
     * If queue becomes full, request will be executed on the calling thread.
     * 
     * There is not a perfect combination of these two parameters, it will depend
     * on your particular environment. Experiment.
     * 
     *
     * @return AmazonEC2Config
     */
    public AmazonEC2Config withMaxAsyncThreads(int maxAsyncThreads) {
        setMaxAsyncThreads(maxAsyncThreads);
        return this;
    }

    /**
     * Checks if MaxAsyncThreads property is set
     *
     * @return true if MaxAsyncThreads property is set
     */
    public boolean isSetMaxAsyncThreads() {
        return this.maxAsyncThreads > 0;
    }

    /**
     * Gets MaxAsyncQueueSize property
     *
     * @return Max number of requests to be queued when max number of threads is
     * reached and all those threads are busy processing.
     */
    public int getMaxAsyncQueueSize() {
        return maxAsyncQueueSize;
    }

    /**
     * Checks if MaxAsyncQueueSize property is set
     *
     * @return true if MaxAsyncQueueSize property is set
     */
    public boolean isSetMaxAsyncQueueSize() {
        return this.maxAsyncQueueSize > 0;
    }

    /**
     * Sets MaxAsyncQueueSize property
     *
     * @param maxAsyncQueueSize Max number of requests to queue when max number of
     * threads is reached, and all threads are busy processing.
     * 
     * Note, this parameter works in conjuction with MaxAsyncThreads. 
     * 
     * When max number of threads is reached and queue is full, request will 
     * be executed on the calling thread. 
     * 
     * There is not a perfect combination of these two parameters, it will depend
     * on your particular environment. Experiment.
     *
     */
    public void setMaxAsyncQueueSize(int maxAsyncQueueSize) {
        this.maxAsyncQueueSize = maxAsyncQueueSize;
    }

    /**
     * Sets MaxAsyncQueueSize property and returns current AmazonEC2Config
     *
     * @param maxAsyncQueueSize Max number of requests to queue when max number of
     * threads is reached, and all threads are busy processing.
     * 
     * Note, this parameter works in conjuction with MaxAsyncThreads. 
     * 
     * When max number of threads is reached and queue is full, request will 
     * be executed on the calling thread. 
     * 
     * There is not a perfect combination of these two parameters, it will depend
     * on your particular environment. Experiment.
     *
     */
    public AmazonEC2Config withMaxAsyncQueueSize(int maxAsyncQueueSize) {
        setMaxAsyncQueueSize(maxAsyncQueueSize);
        return this;
    }

}
