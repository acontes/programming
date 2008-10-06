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

import com.amazonaws.ec2.model.*;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SignatureException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;




/**
 * The Amazon Elastic Compute Cloud (Amazon EC2) web service provides you with
 * the ability to execute your applications in Amazon's computing environment.
 * To use Amazon EC2 you simply:
 * 1. Create an Amazon Machine Image (AMI) containing all your software, including
 * your operating system and associated configuration settings, applications,
 * libraries, etc. Think of this as zipping up the contents of your hard drive. We
 * provide all the necessary tools to create and package your AMI.
 * 2. Upload this AMI to the Amazon S3 (Amazon Simple Storage Service) service. This
 * gives us reliable, secure access to your AMI.
 * 3. Register your AMI with Amazon EC2. This allows us to verify that your AMI has
 * been uploaded correctly and to allocate a unique identifier for it.
 * 4. Use this AMI ID and the Amazon EC2 web service APIs to run, monitor, and
 * terminate as many instances of this AMI as required.
 * You can also skip the first three steps and choose to launch an AMI that is
 * provided by Amazon or shared by another user.
 * While instances are running, you are billed for the computing and network
 * resources that they consume.
 * You can also skip the first three steps and choose to launch an AMI that is
 * provided by Amazon or shared by another user.
 * While instances are running, you are billed for the computing and network
 * resources that they consume.
 * 
 *
 *
 * AmazonEC2Client is implementation of AmazonEC2 based on the
 * Apache <a href="http://jakarta.apache.org/commons/httpclient/">HttpClient</a>.
 *
 */
public  class AmazonEC2Client implements AmazonEC2 {

    private final Log log = LogFactory.getLog(AmazonEC2Client.class);

    private String awsAccessKeyId = null;
    private String awsSecretAccessKey = null;
    private AmazonEC2Config config = null;
    private HttpClient httpClient = null;
    private ExecutorService asyncExecutor;
    private static JAXBContext  jaxbContext;
    private static ThreadLocal<Unmarshaller> unmarshaller;
    private static Pattern ERROR_PATTERN_ONE = Pattern.compile(".*\\<RequestId>(.*)\\</RequestId>.*\\<Error>" +
            "\\<Code>(.*)\\</Code>\\<Message>(.*)\\</Message>\\</Error>.*(\\<Error>)?.*",
            Pattern.MULTILINE | Pattern.DOTALL);
    private static Pattern ERROR_PATTERN_TWO = Pattern.compile(".*\\<Error>\\<Code>(.*)\\</Code>\\<Message>(.*)" +
            "\\</Message>\\</Error>.*(\\<Error>)?.*\\<RequestID>(.*)\\</RequestID>.*",
            Pattern.MULTILINE | Pattern.DOTALL);


    /** Initialize JAXBContext and  Unmarshaller **/
    static {
        try {
            jaxbContext = JAXBContext.newInstance("com.amazonaws.ec2.model", AmazonEC2.class.getClassLoader());
        } catch (JAXBException ex) {
            throw new ExceptionInInitializerError(ex);
        }
        unmarshaller = new ThreadLocal<Unmarshaller>() {
            @Override
            protected synchronized Unmarshaller initialValue() {
                try {
                    return jaxbContext.createUnmarshaller();
                } catch(JAXBException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        };
    }


    /**
     * Constructs AmazonEC2Client with AWS Access Key ID and AWS Secret Key
     *
     * @param awsAccessKeyId
     *          AWS Access Key ID
     * @param awsSecretAccessKey
     *          AWS Secret Access Key
     */
    public  AmazonEC2Client(String awsAccessKeyId,String awsSecretAccessKey) {
        this (awsAccessKeyId, awsSecretAccessKey, new AmazonEC2Config());
    }

    /**
     * Constructs AmazonEC2Client with AWS Access Key ID, AWS Secret Key
     * and max number of threads to spawn for async operations
     *
     * @param awsAccessKeyId
     *          AWS Access Key ID
     * @param awsSecretAccessKey
     *          AWS Secret Access Key
     * @param maxAsyncThreads
     *          Max number of threads to spawn for async operation.
     */
    public AmazonEC2Client(String awsAccessKeyId, String awsSecretAccessKey, int maxAsyncThreads) {
        this(awsAccessKeyId, awsSecretAccessKey, new AmazonEC2Config().withMaxAsyncThreads(maxAsyncThreads));
    }



    /**
     * Constructs AmazonEC2Client with AWS Access Key ID, AWS Secret Key
     * and AmazonEC2Config. Use AmazonEC2Config to pass additional
     * configuration that affects how service is being called.
     *
     * @param awsAccessKeyId
     *          AWS Access Key ID
     * @param awsSecretAccessKey
     *          AWS Secret Access Key
     * @param config
     *          Additional configuration options
     */
    public  AmazonEC2Client(String awsAccessKeyId, String awsSecretAccessKey,
            AmazonEC2Config config) {
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
        this.config = config;
        this.httpClient = configureHttpClient();
        this.asyncExecutor = new ThreadPoolExecutor(config.getMaxAsyncThreads(),
                config.getMaxAsyncThreads(), 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(config.getMaxAsyncQueueSize()) {

                    @Override
                    public boolean offer(Runnable task) {
                        log.debug("Maximum number of concurrent threads reached, queuing task...");
                        return super.offer(task);
                    }
                },
                new ThreadFactory() {

                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    public Thread newThread(Runnable task) {
                        Thread thread = new Thread(task, "AmazonEC2Client-Thread-" +
                                threadNumber.getAndIncrement());
                        thread.setDaemon(true);
                        if (thread.getPriority() != Thread.NORM_PRIORITY) {
                            thread.setPriority(Thread.NORM_PRIORITY);
                        }
                        log.debug("ThreadFactory created new thread: " + thread.getName());
                        return thread;
                    }
                },
                new RejectedExecutionHandler() {

                    public void rejectedExecution(Runnable task, ThreadPoolExecutor executor) {
                        log.debug("Maximum number of concurrent threads reached, and queue is full. " +
                                "Running task in the calling thread..." + Thread.currentThread().getName());
                        if (!executor.isShutdown()) {
                            task.run();
                        }
                    }
                });
    }

    // Public API ------------------------------------------------------------//


        
    /**
     * Allocate Address 
     *
     * The AllocateAddress operation acquires an elastic IP address for use with your
     * account.
     * 
     * @param request
     *          AllocateAddressRequest request
     * @return
     *          AllocateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AllocateAddressResponse allocateAddress(AllocateAddressRequest request) throws AmazonEC2Exception {
        return invoke(AllocateAddressResponse.class, convertAllocateAddress(request));
    }

    /**
     * Non-blocking Allocate Address 
     * <p/>
     * Returns <code>future</code> pointer to AllocateAddressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return AllocateAddressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;AllocateAddressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;AllocateAddressResponse&gt;&gt;();
     *  for (AllocateAddressRequest request : requests) {
     *      responses.add(client.allocateAddressAsync(request));
     *  }
     *  for (Future&lt;AllocateAddressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          AllocateAddressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          AllocateAddressRequest request
     * @return Future&lt;AllocateAddressResponse&gt; future pointer to AllocateAddressResponse
     * 
     */
    public Future<AllocateAddressResponse> allocateAddressAsync(final AllocateAddressRequest request) {
        Future<AllocateAddressResponse> response = asyncExecutor.submit(new Callable<AllocateAddressResponse>() {

            public AllocateAddressResponse call() throws AmazonEC2Exception {
                return allocateAddress(request);
            }
            });
        return response;
    }
        
    /**
     * Associate Address 
     *
     * The AssociateAddress operation associates an elastic IP address with an
     * instance.
     * If the IP address is currently assigned to another instance, the IP address is
     * assigned to the new instance. This is an idempotent operation. If you enter it
     * more than once, Amazon EC2 does not return an error.
     * 
     * @param request
     *          AssociateAddressRequest request
     * @return
     *          AssociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AssociateAddressResponse associateAddress(AssociateAddressRequest request) throws AmazonEC2Exception {
        return invoke(AssociateAddressResponse.class, convertAssociateAddress(request));
    }

    /**
     * Non-blocking Associate Address 
     * <p/>
     * Returns <code>future</code> pointer to AssociateAddressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return AssociateAddressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;AssociateAddressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;AssociateAddressResponse&gt;&gt;();
     *  for (AssociateAddressRequest request : requests) {
     *      responses.add(client.associateAddressAsync(request));
     *  }
     *  for (Future&lt;AssociateAddressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          AssociateAddressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          AssociateAddressRequest request
     * @return Future&lt;AssociateAddressResponse&gt; future pointer to AssociateAddressResponse
     * 
     */
    public Future<AssociateAddressResponse> associateAddressAsync(final AssociateAddressRequest request) {
        Future<AssociateAddressResponse> response = asyncExecutor.submit(new Callable<AssociateAddressResponse>() {

            public AssociateAddressResponse call() throws AmazonEC2Exception {
                return associateAddress(request);
            }
            });
        return response;
    }
        
    /**
     * Attach Volume 
     *
     * Attach a previously created volume to a running instance.
     * 
     * @param request
     *          AttachVolumeRequest request
     * @return
     *          AttachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AttachVolumeResponse attachVolume(AttachVolumeRequest request) throws AmazonEC2Exception {
        return invoke(AttachVolumeResponse.class, convertAttachVolume(request));
    }

    /**
     * Non-blocking Attach Volume 
     * <p/>
     * Returns <code>future</code> pointer to AttachVolumeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return AttachVolumeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;AttachVolumeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;AttachVolumeResponse&gt;&gt;();
     *  for (AttachVolumeRequest request : requests) {
     *      responses.add(client.attachVolumeAsync(request));
     *  }
     *  for (Future&lt;AttachVolumeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          AttachVolumeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          AttachVolumeRequest request
     * @return Future&lt;AttachVolumeResponse&gt; future pointer to AttachVolumeResponse
     * 
     */
    public Future<AttachVolumeResponse> attachVolumeAsync(final AttachVolumeRequest request) {
        Future<AttachVolumeResponse> response = asyncExecutor.submit(new Callable<AttachVolumeResponse>() {

            public AttachVolumeResponse call() throws AmazonEC2Exception {
                return attachVolume(request);
            }
            });
        return response;
    }
        
    /**
     * Authorize Security Group Ingress 
     *
     * The AuthorizeSecurityGroupIngress operation adds permissions to a security
     * group.
     * Permissions are specified by the IP protocol (TCP, UDP or ICMP), the source of
     * the request (by IP range or an Amazon EC2 user-group pair), the source and
     * destination port ranges (for TCP and UDP), and the ICMP codes and types (for
     * ICMP). When authorizing ICMP, -1 can be used as a wildcard in the type and code
     * fields.
     * Permission changes are propagated to instances within the security group as
     * quickly as possible. However, depending on the number of instances, a small
     * delay might occur.
     * When authorizing a user/group pair permission, GroupName,
     * SourceSecurityGroupName and SourceSecurityGroupOwnerId must be specified. When
     * authorizing a CIDR IP permission, GroupName, IpProtocol, FromPort, ToPort and
     * CidrIp must be specified. Mixing these two types of parameters is not allowed.
     * 
     * @param request
     *          AuthorizeSecurityGroupIngressRequest request
     * @return
     *          AuthorizeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AuthorizeSecurityGroupIngressResponse authorizeSecurityGroupIngress(AuthorizeSecurityGroupIngressRequest request) throws AmazonEC2Exception {
        return invoke(AuthorizeSecurityGroupIngressResponse.class, convertAuthorizeSecurityGroupIngress(request));
    }

    /**
     * Non-blocking Authorize Security Group Ingress 
     * <p/>
     * Returns <code>future</code> pointer to AuthorizeSecurityGroupIngressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return AuthorizeSecurityGroupIngressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;AuthorizeSecurityGroupIngressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;AuthorizeSecurityGroupIngressResponse&gt;&gt;();
     *  for (AuthorizeSecurityGroupIngressRequest request : requests) {
     *      responses.add(client.authorizeSecurityGroupIngressAsync(request));
     *  }
     *  for (Future&lt;AuthorizeSecurityGroupIngressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          AuthorizeSecurityGroupIngressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          AuthorizeSecurityGroupIngressRequest request
     * @return Future&lt;AuthorizeSecurityGroupIngressResponse&gt; future pointer to AuthorizeSecurityGroupIngressResponse
     * 
     */
    public Future<AuthorizeSecurityGroupIngressResponse> authorizeSecurityGroupIngressAsync(final AuthorizeSecurityGroupIngressRequest request) {
        Future<AuthorizeSecurityGroupIngressResponse> response = asyncExecutor.submit(new Callable<AuthorizeSecurityGroupIngressResponse>() {

            public AuthorizeSecurityGroupIngressResponse call() throws AmazonEC2Exception {
                return authorizeSecurityGroupIngress(request);
            }
            });
        return response;
    }
        
    /**
     * Confirm Product Instance 
     *
     * The ConfirmProductInstance operation returns true if the specified product code
     * is attached to the specified instance. The operation returns false if the
     * product code is not attached to the instance.
     * The ConfirmProductInstance operation can only be executed by the owner of the
     * AMI. This feature is useful when an AMI owner is providing support and wants to
     * verify whether a user's instance is eligible.
     * 
     * @param request
     *          ConfirmProductInstanceRequest request
     * @return
     *          ConfirmProductInstance Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ConfirmProductInstanceResponse confirmProductInstance(ConfirmProductInstanceRequest request) throws AmazonEC2Exception {
        return invoke(ConfirmProductInstanceResponse.class, convertConfirmProductInstance(request));
    }

    /**
     * Non-blocking Confirm Product Instance 
     * <p/>
     * Returns <code>future</code> pointer to ConfirmProductInstanceResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return ConfirmProductInstanceResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;ConfirmProductInstanceResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;ConfirmProductInstanceResponse&gt;&gt;();
     *  for (ConfirmProductInstanceRequest request : requests) {
     *      responses.add(client.confirmProductInstanceAsync(request));
     *  }
     *  for (Future&lt;ConfirmProductInstanceResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          ConfirmProductInstanceResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          ConfirmProductInstanceRequest request
     * @return Future&lt;ConfirmProductInstanceResponse&gt; future pointer to ConfirmProductInstanceResponse
     * 
     */
    public Future<ConfirmProductInstanceResponse> confirmProductInstanceAsync(final ConfirmProductInstanceRequest request) {
        Future<ConfirmProductInstanceResponse> response = asyncExecutor.submit(new Callable<ConfirmProductInstanceResponse>() {

            public ConfirmProductInstanceResponse call() throws AmazonEC2Exception {
                return confirmProductInstance(request);
            }
            });
        return response;
    }
        
    /**
     * Create Key Pair 
     *
     * The CreateKeyPair operation creates a new 2048 bit RSA key pair and returns a
     * unique ID that can be used to reference this key pair when launching new
     * instances. For more information, see RunInstances.
     * 
     * @param request
     *          CreateKeyPairRequest request
     * @return
     *          CreateKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateKeyPairResponse createKeyPair(CreateKeyPairRequest request) throws AmazonEC2Exception {
        return invoke(CreateKeyPairResponse.class, convertCreateKeyPair(request));
    }

    /**
     * Non-blocking Create Key Pair 
     * <p/>
     * Returns <code>future</code> pointer to CreateKeyPairResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return CreateKeyPairResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;CreateKeyPairResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;CreateKeyPairResponse&gt;&gt;();
     *  for (CreateKeyPairRequest request : requests) {
     *      responses.add(client.createKeyPairAsync(request));
     *  }
     *  for (Future&lt;CreateKeyPairResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          CreateKeyPairResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          CreateKeyPairRequest request
     * @return Future&lt;CreateKeyPairResponse&gt; future pointer to CreateKeyPairResponse
     * 
     */
    public Future<CreateKeyPairResponse> createKeyPairAsync(final CreateKeyPairRequest request) {
        Future<CreateKeyPairResponse> response = asyncExecutor.submit(new Callable<CreateKeyPairResponse>() {

            public CreateKeyPairResponse call() throws AmazonEC2Exception {
                return createKeyPair(request);
            }
            });
        return response;
    }
        
    /**
     * Create Security Group 
     *
     * The CreateSecurityGroup operation creates a new security group.
     * Every instance is launched in a security group. If no security group is
     * specified during launch, the instances are launched in the default security
     * group. Instances within the same security group have unrestricted network
     * access to each other. Instances will reject network access attempts from other
     * instances in a different security group. As the owner of instances you can
     * grant or revoke specific permissions using the AuthorizeSecurityGroupIngress
     * and RevokeSecurityGroupIngress operations.
     * 
     * @param request
     *          CreateSecurityGroupRequest request
     * @return
     *          CreateSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSecurityGroupResponse createSecurityGroup(CreateSecurityGroupRequest request) throws AmazonEC2Exception {
        return invoke(CreateSecurityGroupResponse.class, convertCreateSecurityGroup(request));
    }

    /**
     * Non-blocking Create Security Group 
     * <p/>
     * Returns <code>future</code> pointer to CreateSecurityGroupResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return CreateSecurityGroupResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;CreateSecurityGroupResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;CreateSecurityGroupResponse&gt;&gt;();
     *  for (CreateSecurityGroupRequest request : requests) {
     *      responses.add(client.createSecurityGroupAsync(request));
     *  }
     *  for (Future&lt;CreateSecurityGroupResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          CreateSecurityGroupResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          CreateSecurityGroupRequest request
     * @return Future&lt;CreateSecurityGroupResponse&gt; future pointer to CreateSecurityGroupResponse
     * 
     */
    public Future<CreateSecurityGroupResponse> createSecurityGroupAsync(final CreateSecurityGroupRequest request) {
        Future<CreateSecurityGroupResponse> response = asyncExecutor.submit(new Callable<CreateSecurityGroupResponse>() {

            public CreateSecurityGroupResponse call() throws AmazonEC2Exception {
                return createSecurityGroup(request);
            }
            });
        return response;
    }
        
    /**
     * Create Snapshot 
     *
     * Create a snapshot of the volume identified by volume ID. A volume does not have to be detached
     * at the time the snapshot is taken.
     * Important Note:
     * Snapshot creation requires that the system is in a consistent state.
     * For instance, this means that if taking a snapshot of a database, the tables must
     * be read-only locked to ensure that the snapshot will not contain a corrupted
     * version of the database.  Therefore, be careful when using this API to ensure that
     * the system remains in the consistent state until the create snapshot status
     * has returned.
     * 
     * @param request
     *          CreateSnapshotRequest request
     * @return
     *          CreateSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSnapshotResponse createSnapshot(CreateSnapshotRequest request) throws AmazonEC2Exception {
        return invoke(CreateSnapshotResponse.class, convertCreateSnapshot(request));
    }

    /**
     * Non-blocking Create Snapshot 
     * <p/>
     * Returns <code>future</code> pointer to CreateSnapshotResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return CreateSnapshotResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;CreateSnapshotResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;CreateSnapshotResponse&gt;&gt;();
     *  for (CreateSnapshotRequest request : requests) {
     *      responses.add(client.createSnapshotAsync(request));
     *  }
     *  for (Future&lt;CreateSnapshotResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          CreateSnapshotResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          CreateSnapshotRequest request
     * @return Future&lt;CreateSnapshotResponse&gt; future pointer to CreateSnapshotResponse
     * 
     */
    public Future<CreateSnapshotResponse> createSnapshotAsync(final CreateSnapshotRequest request) {
        Future<CreateSnapshotResponse> response = asyncExecutor.submit(new Callable<CreateSnapshotResponse>() {

            public CreateSnapshotResponse call() throws AmazonEC2Exception {
                return createSnapshot(request);
            }
            });
        return response;
    }
        
    /**
     * Create Volume 
     *
     * Initializes an empty volume of a given size
     * 
     * @param request
     *          CreateVolumeRequest request
     * @return
     *          CreateVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateVolumeResponse createVolume(CreateVolumeRequest request) throws AmazonEC2Exception {
        return invoke(CreateVolumeResponse.class, convertCreateVolume(request));
    }

    /**
     * Non-blocking Create Volume 
     * <p/>
     * Returns <code>future</code> pointer to CreateVolumeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return CreateVolumeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;CreateVolumeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;CreateVolumeResponse&gt;&gt;();
     *  for (CreateVolumeRequest request : requests) {
     *      responses.add(client.createVolumeAsync(request));
     *  }
     *  for (Future&lt;CreateVolumeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          CreateVolumeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          CreateVolumeRequest request
     * @return Future&lt;CreateVolumeResponse&gt; future pointer to CreateVolumeResponse
     * 
     */
    public Future<CreateVolumeResponse> createVolumeAsync(final CreateVolumeRequest request) {
        Future<CreateVolumeResponse> response = asyncExecutor.submit(new Callable<CreateVolumeResponse>() {

            public CreateVolumeResponse call() throws AmazonEC2Exception {
                return createVolume(request);
            }
            });
        return response;
    }
        
    /**
     * Delete Key Pair 
     *
     * The DeleteKeyPair operation deletes a key pair.
     * 
     * @param request
     *          DeleteKeyPairRequest request
     * @return
     *          DeleteKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteKeyPairResponse deleteKeyPair(DeleteKeyPairRequest request) throws AmazonEC2Exception {
        return invoke(DeleteKeyPairResponse.class, convertDeleteKeyPair(request));
    }

    /**
     * Non-blocking Delete Key Pair 
     * <p/>
     * Returns <code>future</code> pointer to DeleteKeyPairResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DeleteKeyPairResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DeleteKeyPairResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DeleteKeyPairResponse&gt;&gt;();
     *  for (DeleteKeyPairRequest request : requests) {
     *      responses.add(client.deleteKeyPairAsync(request));
     *  }
     *  for (Future&lt;DeleteKeyPairResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DeleteKeyPairResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DeleteKeyPairRequest request
     * @return Future&lt;DeleteKeyPairResponse&gt; future pointer to DeleteKeyPairResponse
     * 
     */
    public Future<DeleteKeyPairResponse> deleteKeyPairAsync(final DeleteKeyPairRequest request) {
        Future<DeleteKeyPairResponse> response = asyncExecutor.submit(new Callable<DeleteKeyPairResponse>() {

            public DeleteKeyPairResponse call() throws AmazonEC2Exception {
                return deleteKeyPair(request);
            }
            });
        return response;
    }
        
    /**
     * Delete Security Group 
     *
     * The DeleteSecurityGroup operation deletes a security group.
     * Note:
     * If you attempt to delete a security group that contains instances, a fault is
     * returned.
     * If you attempt to delete a security group that is referenced by another
     * security group, a fault is returned. For example, if security group B has a
     * rule that allows access from security group A, security group A cannot be
     * deleted until the allow rule is removed.
     * 
     * @param request
     *          DeleteSecurityGroupRequest request
     * @return
     *          DeleteSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSecurityGroupResponse deleteSecurityGroup(DeleteSecurityGroupRequest request) throws AmazonEC2Exception {
        return invoke(DeleteSecurityGroupResponse.class, convertDeleteSecurityGroup(request));
    }

    /**
     * Non-blocking Delete Security Group 
     * <p/>
     * Returns <code>future</code> pointer to DeleteSecurityGroupResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DeleteSecurityGroupResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DeleteSecurityGroupResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DeleteSecurityGroupResponse&gt;&gt;();
     *  for (DeleteSecurityGroupRequest request : requests) {
     *      responses.add(client.deleteSecurityGroupAsync(request));
     *  }
     *  for (Future&lt;DeleteSecurityGroupResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DeleteSecurityGroupResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DeleteSecurityGroupRequest request
     * @return Future&lt;DeleteSecurityGroupResponse&gt; future pointer to DeleteSecurityGroupResponse
     * 
     */
    public Future<DeleteSecurityGroupResponse> deleteSecurityGroupAsync(final DeleteSecurityGroupRequest request) {
        Future<DeleteSecurityGroupResponse> response = asyncExecutor.submit(new Callable<DeleteSecurityGroupResponse>() {

            public DeleteSecurityGroupResponse call() throws AmazonEC2Exception {
                return deleteSecurityGroup(request);
            }
            });
        return response;
    }
        
    /**
     * Delete Snapshot 
     *
     * Deletes the snapshot identitied by snapshotId.
     * 
     * @param request
     *          DeleteSnapshotRequest request
     * @return
     *          DeleteSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSnapshotResponse deleteSnapshot(DeleteSnapshotRequest request) throws AmazonEC2Exception {
        return invoke(DeleteSnapshotResponse.class, convertDeleteSnapshot(request));
    }

    /**
     * Non-blocking Delete Snapshot 
     * <p/>
     * Returns <code>future</code> pointer to DeleteSnapshotResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DeleteSnapshotResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DeleteSnapshotResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DeleteSnapshotResponse&gt;&gt;();
     *  for (DeleteSnapshotRequest request : requests) {
     *      responses.add(client.deleteSnapshotAsync(request));
     *  }
     *  for (Future&lt;DeleteSnapshotResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DeleteSnapshotResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DeleteSnapshotRequest request
     * @return Future&lt;DeleteSnapshotResponse&gt; future pointer to DeleteSnapshotResponse
     * 
     */
    public Future<DeleteSnapshotResponse> deleteSnapshotAsync(final DeleteSnapshotRequest request) {
        Future<DeleteSnapshotResponse> response = asyncExecutor.submit(new Callable<DeleteSnapshotResponse>() {

            public DeleteSnapshotResponse call() throws AmazonEC2Exception {
                return deleteSnapshot(request);
            }
            });
        return response;
    }
        
    /**
     * Delete Volume 
     *
     * Deletes a  previously created volume. Once successfully deleted, a new volume  can be created with the same name.
     * 
     * @param request
     *          DeleteVolumeRequest request
     * @return
     *          DeleteVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteVolumeResponse deleteVolume(DeleteVolumeRequest request) throws AmazonEC2Exception {
        return invoke(DeleteVolumeResponse.class, convertDeleteVolume(request));
    }

    /**
     * Non-blocking Delete Volume 
     * <p/>
     * Returns <code>future</code> pointer to DeleteVolumeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DeleteVolumeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DeleteVolumeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DeleteVolumeResponse&gt;&gt;();
     *  for (DeleteVolumeRequest request : requests) {
     *      responses.add(client.deleteVolumeAsync(request));
     *  }
     *  for (Future&lt;DeleteVolumeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DeleteVolumeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DeleteVolumeRequest request
     * @return Future&lt;DeleteVolumeResponse&gt; future pointer to DeleteVolumeResponse
     * 
     */
    public Future<DeleteVolumeResponse> deleteVolumeAsync(final DeleteVolumeRequest request) {
        Future<DeleteVolumeResponse> response = asyncExecutor.submit(new Callable<DeleteVolumeResponse>() {

            public DeleteVolumeResponse call() throws AmazonEC2Exception {
                return deleteVolume(request);
            }
            });
        return response;
    }
        
    /**
     * Deregister Image 
     *
     * The DeregisterImage operation deregisters an AMI. Once deregistered, instances
     * of the AMI can no longer be launched.
     * 
     * @param request
     *          DeregisterImageRequest request
     * @return
     *          DeregisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeregisterImageResponse deregisterImage(DeregisterImageRequest request) throws AmazonEC2Exception {
        return invoke(DeregisterImageResponse.class, convertDeregisterImage(request));
    }

    /**
     * Non-blocking Deregister Image 
     * <p/>
     * Returns <code>future</code> pointer to DeregisterImageResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DeregisterImageResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DeregisterImageResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DeregisterImageResponse&gt;&gt;();
     *  for (DeregisterImageRequest request : requests) {
     *      responses.add(client.deregisterImageAsync(request));
     *  }
     *  for (Future&lt;DeregisterImageResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DeregisterImageResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DeregisterImageRequest request
     * @return Future&lt;DeregisterImageResponse&gt; future pointer to DeregisterImageResponse
     * 
     */
    public Future<DeregisterImageResponse> deregisterImageAsync(final DeregisterImageRequest request) {
        Future<DeregisterImageResponse> response = asyncExecutor.submit(new Callable<DeregisterImageResponse>() {

            public DeregisterImageResponse call() throws AmazonEC2Exception {
                return deregisterImage(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Addresses 
     *
     * The DescribeAddresses operation lists elastic IP addresses assigned to your
     * account.
     * 
     * @param request
     *          DescribeAddressesRequest request
     * @return
     *          DescribeAddresses Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAddressesResponse describeAddresses(DescribeAddressesRequest request) throws AmazonEC2Exception {
        return invoke(DescribeAddressesResponse.class, convertDescribeAddresses(request));
    }

    /**
     * Non-blocking Describe Addresses 
     * <p/>
     * Returns <code>future</code> pointer to DescribeAddressesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeAddressesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeAddressesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeAddressesResponse&gt;&gt;();
     *  for (DescribeAddressesRequest request : requests) {
     *      responses.add(client.describeAddressesAsync(request));
     *  }
     *  for (Future&lt;DescribeAddressesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeAddressesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeAddressesRequest request
     * @return Future&lt;DescribeAddressesResponse&gt; future pointer to DescribeAddressesResponse
     * 
     */
    public Future<DescribeAddressesResponse> describeAddressesAsync(final DescribeAddressesRequest request) {
        Future<DescribeAddressesResponse> response = asyncExecutor.submit(new Callable<DescribeAddressesResponse>() {

            public DescribeAddressesResponse call() throws AmazonEC2Exception {
                return describeAddresses(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Availability Zones 
     *
     * The DescribeAvailabilityZones operation describes availability zones that are
     * currently available to the account and their states.
     * Availability zones are not the same across accounts. The availability zone
     * us-east-1a for account A is not necessarily the same as us-east-1a for account
     * B. Zone assignments are mapped independently for each account.
     * 
     * @param request
     *          DescribeAvailabilityZonesRequest request
     * @return
     *          DescribeAvailabilityZones Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAvailabilityZonesResponse describeAvailabilityZones(DescribeAvailabilityZonesRequest request) throws AmazonEC2Exception {
        return invoke(DescribeAvailabilityZonesResponse.class, convertDescribeAvailabilityZones(request));
    }

    /**
     * Non-blocking Describe Availability Zones 
     * <p/>
     * Returns <code>future</code> pointer to DescribeAvailabilityZonesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeAvailabilityZonesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeAvailabilityZonesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeAvailabilityZonesResponse&gt;&gt;();
     *  for (DescribeAvailabilityZonesRequest request : requests) {
     *      responses.add(client.describeAvailabilityZonesAsync(request));
     *  }
     *  for (Future&lt;DescribeAvailabilityZonesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeAvailabilityZonesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeAvailabilityZonesRequest request
     * @return Future&lt;DescribeAvailabilityZonesResponse&gt; future pointer to DescribeAvailabilityZonesResponse
     * 
     */
    public Future<DescribeAvailabilityZonesResponse> describeAvailabilityZonesAsync(final DescribeAvailabilityZonesRequest request) {
        Future<DescribeAvailabilityZonesResponse> response = asyncExecutor.submit(new Callable<DescribeAvailabilityZonesResponse>() {

            public DescribeAvailabilityZonesResponse call() throws AmazonEC2Exception {
                return describeAvailabilityZones(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Image Attribute 
     *
     * The DescribeImageAttribute operation returns information about an attribute of
     * an AMI. Only one attribute can be specified per call.
     * 
     * @param request
     *          DescribeImageAttributeRequest request
     * @return
     *          DescribeImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImageAttributeResponse describeImageAttribute(DescribeImageAttributeRequest request) throws AmazonEC2Exception {
        return invoke(DescribeImageAttributeResponse.class, convertDescribeImageAttribute(request));
    }

    /**
     * Non-blocking Describe Image Attribute 
     * <p/>
     * Returns <code>future</code> pointer to DescribeImageAttributeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeImageAttributeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeImageAttributeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeImageAttributeResponse&gt;&gt;();
     *  for (DescribeImageAttributeRequest request : requests) {
     *      responses.add(client.describeImageAttributeAsync(request));
     *  }
     *  for (Future&lt;DescribeImageAttributeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeImageAttributeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeImageAttributeRequest request
     * @return Future&lt;DescribeImageAttributeResponse&gt; future pointer to DescribeImageAttributeResponse
     * 
     */
    public Future<DescribeImageAttributeResponse> describeImageAttributeAsync(final DescribeImageAttributeRequest request) {
        Future<DescribeImageAttributeResponse> response = asyncExecutor.submit(new Callable<DescribeImageAttributeResponse>() {

            public DescribeImageAttributeResponse call() throws AmazonEC2Exception {
                return describeImageAttribute(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Images 
     *
     * The DescribeImages operation returns information about AMIs, AKIs, and ARIs
     * available to the user. Information returned includes image type, product codes,
     * architecture, and kernel and RAM disk IDs. Images available to the user include
     * public images available for any user to launch, private images owned by the
     * user making the request, and private images owned by other users for which the
     * user has explicit launch permissions.
     * Launch permissions fall into three categories:
     * Public:
     * The owner of the AMI granted launch permissions for the AMI to the all group.
     * All users have launch permissions for these AMIs.
     * Explicit:
     * The owner of the AMI granted launch permissions to a specific user.
     * Implicit:
     * A user has implicit launch permissions for all AMIs he or she owns.
     * The list of AMIs returned can be modified by specifying AMI IDs, AMI owners, or
     * users with launch permissions. If no options are specified, Amazon EC2 returns
     * all AMIs for which the user has launch permissions.
     * If you specify one or more AMI IDs, only AMIs that have the specified IDs are
     * returned. If you specify an invalid AMI ID, a fault is returned. If you specify
     * an AMI ID for which you do not have access, it will not be included in the
     * returned results.
     * If you specify one or more AMI owners, only AMIs from the specified owners and
     * for which you have access are returned. The results can include the account IDs
     * of the specified owners, amazon for AMIs owned by Amazon or self for AMIs that
     * you own.
     * If you specify a list of executable users, only users that have launch
     * permissions for the AMIs are returned. You can specify account IDs (if you own
     * the AMI(s)), self for AMIs for which you own or have explicit permissions, or
     * all for public AMIs.
     * Note:
     * Deregistered images are included in the returned results for an unspecified
     * interval after deregistration.
     * 
     * @param request
     *          DescribeImagesRequest request
     * @return
     *          DescribeImages Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImagesResponse describeImages(DescribeImagesRequest request) throws AmazonEC2Exception {
        return invoke(DescribeImagesResponse.class, convertDescribeImages(request));
    }

    /**
     * Non-blocking Describe Images 
     * <p/>
     * Returns <code>future</code> pointer to DescribeImagesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeImagesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeImagesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeImagesResponse&gt;&gt;();
     *  for (DescribeImagesRequest request : requests) {
     *      responses.add(client.describeImagesAsync(request));
     *  }
     *  for (Future&lt;DescribeImagesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeImagesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeImagesRequest request
     * @return Future&lt;DescribeImagesResponse&gt; future pointer to DescribeImagesResponse
     * 
     */
    public Future<DescribeImagesResponse> describeImagesAsync(final DescribeImagesRequest request) {
        Future<DescribeImagesResponse> response = asyncExecutor.submit(new Callable<DescribeImagesResponse>() {

            public DescribeImagesResponse call() throws AmazonEC2Exception {
                return describeImages(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Instances 
     *
     * The DescribeInstances operation returns information about instances that you
     * own.
     * If you specify one or more instance IDs, Amazon EC2 returns information for
     * those instances. If you do not specify instance IDs, Amazon EC2 returns
     * information for all relevant instances. If you specify an invalid instance ID,
     * a fault is returned. If you specify an instance that you do not own, it will
     * not be included in the returned results.
     * Recently terminated instances might appear in the returned results. This
     * interval is usually less than one hour.
     * 
     * @param request
     *          DescribeInstancesRequest request
     * @return
     *          DescribeInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeInstancesResponse describeInstances(DescribeInstancesRequest request) throws AmazonEC2Exception {
        return invoke(DescribeInstancesResponse.class, convertDescribeInstances(request));
    }

    /**
     * Non-blocking Describe Instances 
     * <p/>
     * Returns <code>future</code> pointer to DescribeInstancesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeInstancesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeInstancesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeInstancesResponse&gt;&gt;();
     *  for (DescribeInstancesRequest request : requests) {
     *      responses.add(client.describeInstancesAsync(request));
     *  }
     *  for (Future&lt;DescribeInstancesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeInstancesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeInstancesRequest request
     * @return Future&lt;DescribeInstancesResponse&gt; future pointer to DescribeInstancesResponse
     * 
     */
    public Future<DescribeInstancesResponse> describeInstancesAsync(final DescribeInstancesRequest request) {
        Future<DescribeInstancesResponse> response = asyncExecutor.submit(new Callable<DescribeInstancesResponse>() {

            public DescribeInstancesResponse call() throws AmazonEC2Exception {
                return describeInstances(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Key Pairs 
     *
     * The DescribeKeyPairs operation returns information about key pairs available to
     * you. If you specify key pairs, information about those key pairs is returned.
     * Otherwise, information for all registered key pairs is returned.
     * 
     * @param request
     *          DescribeKeyPairsRequest request
     * @return
     *          DescribeKeyPairs Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeKeyPairsResponse describeKeyPairs(DescribeKeyPairsRequest request) throws AmazonEC2Exception {
        return invoke(DescribeKeyPairsResponse.class, convertDescribeKeyPairs(request));
    }

    /**
     * Non-blocking Describe Key Pairs 
     * <p/>
     * Returns <code>future</code> pointer to DescribeKeyPairsResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeKeyPairsResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeKeyPairsResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeKeyPairsResponse&gt;&gt;();
     *  for (DescribeKeyPairsRequest request : requests) {
     *      responses.add(client.describeKeyPairsAsync(request));
     *  }
     *  for (Future&lt;DescribeKeyPairsResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeKeyPairsResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeKeyPairsRequest request
     * @return Future&lt;DescribeKeyPairsResponse&gt; future pointer to DescribeKeyPairsResponse
     * 
     */
    public Future<DescribeKeyPairsResponse> describeKeyPairsAsync(final DescribeKeyPairsRequest request) {
        Future<DescribeKeyPairsResponse> response = asyncExecutor.submit(new Callable<DescribeKeyPairsResponse>() {

            public DescribeKeyPairsResponse call() throws AmazonEC2Exception {
                return describeKeyPairs(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Security Groups 
     *
     * The DescribeSecurityGroups operation returns information about security groups
     * that you own.
     * If you specify security group names, information about those security group is
     * returned. Otherwise, information for all security group is returned. If you
     * specify a group that does not exist, a fault is returned.
     * 
     * @param request
     *          DescribeSecurityGroupsRequest request
     * @return
     *          DescribeSecurityGroups Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSecurityGroupsResponse describeSecurityGroups(DescribeSecurityGroupsRequest request) throws AmazonEC2Exception {
        return invoke(DescribeSecurityGroupsResponse.class, convertDescribeSecurityGroups(request));
    }

    /**
     * Non-blocking Describe Security Groups 
     * <p/>
     * Returns <code>future</code> pointer to DescribeSecurityGroupsResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeSecurityGroupsResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeSecurityGroupsResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeSecurityGroupsResponse&gt;&gt;();
     *  for (DescribeSecurityGroupsRequest request : requests) {
     *      responses.add(client.describeSecurityGroupsAsync(request));
     *  }
     *  for (Future&lt;DescribeSecurityGroupsResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeSecurityGroupsResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeSecurityGroupsRequest request
     * @return Future&lt;DescribeSecurityGroupsResponse&gt; future pointer to DescribeSecurityGroupsResponse
     * 
     */
    public Future<DescribeSecurityGroupsResponse> describeSecurityGroupsAsync(final DescribeSecurityGroupsRequest request) {
        Future<DescribeSecurityGroupsResponse> response = asyncExecutor.submit(new Callable<DescribeSecurityGroupsResponse>() {

            public DescribeSecurityGroupsResponse call() throws AmazonEC2Exception {
                return describeSecurityGroups(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Snapshots 
     *
     * Describes the indicated snapshots, or in lieu of that, all snapshots owned by the caller.
     * 
     * @param request
     *          DescribeSnapshotsRequest request
     * @return
     *          DescribeSnapshots Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSnapshotsResponse describeSnapshots(DescribeSnapshotsRequest request) throws AmazonEC2Exception {
        return invoke(DescribeSnapshotsResponse.class, convertDescribeSnapshots(request));
    }

    /**
     * Non-blocking Describe Snapshots 
     * <p/>
     * Returns <code>future</code> pointer to DescribeSnapshotsResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeSnapshotsResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeSnapshotsResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeSnapshotsResponse&gt;&gt;();
     *  for (DescribeSnapshotsRequest request : requests) {
     *      responses.add(client.describeSnapshotsAsync(request));
     *  }
     *  for (Future&lt;DescribeSnapshotsResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeSnapshotsResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeSnapshotsRequest request
     * @return Future&lt;DescribeSnapshotsResponse&gt; future pointer to DescribeSnapshotsResponse
     * 
     */
    public Future<DescribeSnapshotsResponse> describeSnapshotsAsync(final DescribeSnapshotsRequest request) {
        Future<DescribeSnapshotsResponse> response = asyncExecutor.submit(new Callable<DescribeSnapshotsResponse>() {

            public DescribeSnapshotsResponse call() throws AmazonEC2Exception {
                return describeSnapshots(request);
            }
            });
        return response;
    }
        
    /**
     * Describe Volumes 
     *
     * Describes the status of the indicated or, in lieu of any specified,  all volumes belonging to the caller. Volumes that have been deleted are not described.
     * 
     * @param request
     *          DescribeVolumesRequest request
     * @return
     *          DescribeVolumes Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeVolumesResponse describeVolumes(DescribeVolumesRequest request) throws AmazonEC2Exception {
        return invoke(DescribeVolumesResponse.class, convertDescribeVolumes(request));
    }

    /**
     * Non-blocking Describe Volumes 
     * <p/>
     * Returns <code>future</code> pointer to DescribeVolumesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DescribeVolumesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DescribeVolumesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DescribeVolumesResponse&gt;&gt;();
     *  for (DescribeVolumesRequest request : requests) {
     *      responses.add(client.describeVolumesAsync(request));
     *  }
     *  for (Future&lt;DescribeVolumesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DescribeVolumesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DescribeVolumesRequest request
     * @return Future&lt;DescribeVolumesResponse&gt; future pointer to DescribeVolumesResponse
     * 
     */
    public Future<DescribeVolumesResponse> describeVolumesAsync(final DescribeVolumesRequest request) {
        Future<DescribeVolumesResponse> response = asyncExecutor.submit(new Callable<DescribeVolumesResponse>() {

            public DescribeVolumesResponse call() throws AmazonEC2Exception {
                return describeVolumes(request);
            }
            });
        return response;
    }
        
    /**
     * Detach Volume 
     *
     * Detach a previously attached volume from a running instance.
     * 
     * @param request
     *          DetachVolumeRequest request
     * @return
     *          DetachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DetachVolumeResponse detachVolume(DetachVolumeRequest request) throws AmazonEC2Exception {
        return invoke(DetachVolumeResponse.class, convertDetachVolume(request));
    }

    /**
     * Non-blocking Detach Volume 
     * <p/>
     * Returns <code>future</code> pointer to DetachVolumeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DetachVolumeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DetachVolumeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DetachVolumeResponse&gt;&gt;();
     *  for (DetachVolumeRequest request : requests) {
     *      responses.add(client.detachVolumeAsync(request));
     *  }
     *  for (Future&lt;DetachVolumeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DetachVolumeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DetachVolumeRequest request
     * @return Future&lt;DetachVolumeResponse&gt; future pointer to DetachVolumeResponse
     * 
     */
    public Future<DetachVolumeResponse> detachVolumeAsync(final DetachVolumeRequest request) {
        Future<DetachVolumeResponse> response = asyncExecutor.submit(new Callable<DetachVolumeResponse>() {

            public DetachVolumeResponse call() throws AmazonEC2Exception {
                return detachVolume(request);
            }
            });
        return response;
    }
        
    /**
     * Disassociate Address 
     *
     * The DisassociateAddress operation disassociates the specified elastic IP
     * address from the instance to which it is assigned. This is an idempotent
     * operation. If you enter it more than once, Amazon EC2 does not return an error.
     * 
     * @param request
     *          DisassociateAddressRequest request
     * @return
     *          DisassociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DisassociateAddressResponse disassociateAddress(DisassociateAddressRequest request) throws AmazonEC2Exception {
        return invoke(DisassociateAddressResponse.class, convertDisassociateAddress(request));
    }

    /**
     * Non-blocking Disassociate Address 
     * <p/>
     * Returns <code>future</code> pointer to DisassociateAddressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return DisassociateAddressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;DisassociateAddressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;DisassociateAddressResponse&gt;&gt;();
     *  for (DisassociateAddressRequest request : requests) {
     *      responses.add(client.disassociateAddressAsync(request));
     *  }
     *  for (Future&lt;DisassociateAddressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          DisassociateAddressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          DisassociateAddressRequest request
     * @return Future&lt;DisassociateAddressResponse&gt; future pointer to DisassociateAddressResponse
     * 
     */
    public Future<DisassociateAddressResponse> disassociateAddressAsync(final DisassociateAddressRequest request) {
        Future<DisassociateAddressResponse> response = asyncExecutor.submit(new Callable<DisassociateAddressResponse>() {

            public DisassociateAddressResponse call() throws AmazonEC2Exception {
                return disassociateAddress(request);
            }
            });
        return response;
    }
        
    /**
     * Get Console Output 
     *
     * The GetConsoleOutput operation retrieves console output for the specified
     * instance.
     * Instance console output is buffered and posted shortly after instance boot,
     * reboot, and termination. Amazon EC2 preserves the most recent 64 KB output
     * which will be available for at least one hour after the most recent post.
     * 
     * @param request
     *          GetConsoleOutputRequest request
     * @return
     *          GetConsoleOutput Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public GetConsoleOutputResponse getConsoleOutput(GetConsoleOutputRequest request) throws AmazonEC2Exception {
        return invoke(GetConsoleOutputResponse.class, convertGetConsoleOutput(request));
    }

    /**
     * Non-blocking Get Console Output 
     * <p/>
     * Returns <code>future</code> pointer to GetConsoleOutputResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return GetConsoleOutputResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;GetConsoleOutputResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;GetConsoleOutputResponse&gt;&gt;();
     *  for (GetConsoleOutputRequest request : requests) {
     *      responses.add(client.getConsoleOutputAsync(request));
     *  }
     *  for (Future&lt;GetConsoleOutputResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          GetConsoleOutputResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          GetConsoleOutputRequest request
     * @return Future&lt;GetConsoleOutputResponse&gt; future pointer to GetConsoleOutputResponse
     * 
     */
    public Future<GetConsoleOutputResponse> getConsoleOutputAsync(final GetConsoleOutputRequest request) {
        Future<GetConsoleOutputResponse> response = asyncExecutor.submit(new Callable<GetConsoleOutputResponse>() {

            public GetConsoleOutputResponse call() throws AmazonEC2Exception {
                return getConsoleOutput(request);
            }
            });
        return response;
    }
        
    /**
     * Modify Image Attribute 
     *
     * The ModifyImageAttribute operation modifies an attribute of an AMI.
     * 
     * @param request
     *          ModifyImageAttributeRequest request
     * @return
     *          ModifyImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ModifyImageAttributeResponse modifyImageAttribute(ModifyImageAttributeRequest request) throws AmazonEC2Exception {
        return invoke(ModifyImageAttributeResponse.class, convertModifyImageAttribute(request));
    }

    /**
     * Non-blocking Modify Image Attribute 
     * <p/>
     * Returns <code>future</code> pointer to ModifyImageAttributeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return ModifyImageAttributeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;ModifyImageAttributeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;ModifyImageAttributeResponse&gt;&gt;();
     *  for (ModifyImageAttributeRequest request : requests) {
     *      responses.add(client.modifyImageAttributeAsync(request));
     *  }
     *  for (Future&lt;ModifyImageAttributeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          ModifyImageAttributeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          ModifyImageAttributeRequest request
     * @return Future&lt;ModifyImageAttributeResponse&gt; future pointer to ModifyImageAttributeResponse
     * 
     */
    public Future<ModifyImageAttributeResponse> modifyImageAttributeAsync(final ModifyImageAttributeRequest request) {
        Future<ModifyImageAttributeResponse> response = asyncExecutor.submit(new Callable<ModifyImageAttributeResponse>() {

            public ModifyImageAttributeResponse call() throws AmazonEC2Exception {
                return modifyImageAttribute(request);
            }
            });
        return response;
    }
        
    /**
     * Reboot Instances 
     *
     * The RebootInstances operation requests a reboot of one or more instances. This
     * operation is asynchronous; it only queues a request to reboot the specified
     * instance(s). The operation will succeed if the instances are valid and belong
     * to the user. Requests to reboot terminated instances are ignored.
     * 
     * @param request
     *          RebootInstancesRequest request
     * @return
     *          RebootInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RebootInstancesResponse rebootInstances(RebootInstancesRequest request) throws AmazonEC2Exception {
        return invoke(RebootInstancesResponse.class, convertRebootInstances(request));
    }

    /**
     * Non-blocking Reboot Instances 
     * <p/>
     * Returns <code>future</code> pointer to RebootInstancesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return RebootInstancesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;RebootInstancesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;RebootInstancesResponse&gt;&gt;();
     *  for (RebootInstancesRequest request : requests) {
     *      responses.add(client.rebootInstancesAsync(request));
     *  }
     *  for (Future&lt;RebootInstancesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          RebootInstancesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          RebootInstancesRequest request
     * @return Future&lt;RebootInstancesResponse&gt; future pointer to RebootInstancesResponse
     * 
     */
    public Future<RebootInstancesResponse> rebootInstancesAsync(final RebootInstancesRequest request) {
        Future<RebootInstancesResponse> response = asyncExecutor.submit(new Callable<RebootInstancesResponse>() {

            public RebootInstancesResponse call() throws AmazonEC2Exception {
                return rebootInstances(request);
            }
            });
        return response;
    }
        
    /**
     * Register Image 
     *
     * The RegisterImage operation registers an AMI with Amazon EC2. Images must be
     * registered before they can be launched. For more information, see RunInstances.
     * Each AMI is associated with an unique ID which is provided by the Amazon EC2
     * service through the RegisterImage operation. During registration, Amazon EC2
     * retrieves the specified image manifest from Amazon S3 and verifies that the
     * image is owned by the user registering the image.
     * The image manifest is retrieved once and stored within the Amazon EC2. Any
     * modifications to an image in Amazon S3 invalidates this registration. If you
     * make changes to an image, deregister the previous image and register the new
     * image. For more information, see DeregisterImage.
     * 
     * @param request
     *          RegisterImageRequest request
     * @return
     *          RegisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RegisterImageResponse registerImage(RegisterImageRequest request) throws AmazonEC2Exception {
        return invoke(RegisterImageResponse.class, convertRegisterImage(request));
    }

    /**
     * Non-blocking Register Image 
     * <p/>
     * Returns <code>future</code> pointer to RegisterImageResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return RegisterImageResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;RegisterImageResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;RegisterImageResponse&gt;&gt;();
     *  for (RegisterImageRequest request : requests) {
     *      responses.add(client.registerImageAsync(request));
     *  }
     *  for (Future&lt;RegisterImageResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          RegisterImageResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          RegisterImageRequest request
     * @return Future&lt;RegisterImageResponse&gt; future pointer to RegisterImageResponse
     * 
     */
    public Future<RegisterImageResponse> registerImageAsync(final RegisterImageRequest request) {
        Future<RegisterImageResponse> response = asyncExecutor.submit(new Callable<RegisterImageResponse>() {

            public RegisterImageResponse call() throws AmazonEC2Exception {
                return registerImage(request);
            }
            });
        return response;
    }
        
    /**
     * Release Address 
     *
     * The ReleaseAddress operation releases an elastic IP address associated with
     * your account.
     * Note:
     * Releasing an IP address automatically disassociates it from any instance with
     * which it is associated. For more information, see DisassociateAddress.
     * Important:
     * After releasing an elastic IP address, it is released to the IP address pool
     * and might no longer be available to your account. Make sure to update your DNS
     * records and any servers or devices that communicate with the address.
     * If you run this operation on an elastic IP address that is already released,
     * the address might be assigned to another account which will cause Amazon EC2 to
     * return an error.
     * 
     * @param request
     *          ReleaseAddressRequest request
     * @return
     *          ReleaseAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ReleaseAddressResponse releaseAddress(ReleaseAddressRequest request) throws AmazonEC2Exception {
        return invoke(ReleaseAddressResponse.class, convertReleaseAddress(request));
    }

    /**
     * Non-blocking Release Address 
     * <p/>
     * Returns <code>future</code> pointer to ReleaseAddressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return ReleaseAddressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;ReleaseAddressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;ReleaseAddressResponse&gt;&gt;();
     *  for (ReleaseAddressRequest request : requests) {
     *      responses.add(client.releaseAddressAsync(request));
     *  }
     *  for (Future&lt;ReleaseAddressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          ReleaseAddressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          ReleaseAddressRequest request
     * @return Future&lt;ReleaseAddressResponse&gt; future pointer to ReleaseAddressResponse
     * 
     */
    public Future<ReleaseAddressResponse> releaseAddressAsync(final ReleaseAddressRequest request) {
        Future<ReleaseAddressResponse> response = asyncExecutor.submit(new Callable<ReleaseAddressResponse>() {

            public ReleaseAddressResponse call() throws AmazonEC2Exception {
                return releaseAddress(request);
            }
            });
        return response;
    }
        
    /**
     * Reset Image Attribute 
     *
     * The ResetImageAttribute operation resets an attribute of an AMI to its default
     * value.
     * Note:
     * The productCodes attribute cannot be reset.
     * 
     * @param request
     *          ResetImageAttributeRequest request
     * @return
     *          ResetImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ResetImageAttributeResponse resetImageAttribute(ResetImageAttributeRequest request) throws AmazonEC2Exception {
        return invoke(ResetImageAttributeResponse.class, convertResetImageAttribute(request));
    }

    /**
     * Non-blocking Reset Image Attribute 
     * <p/>
     * Returns <code>future</code> pointer to ResetImageAttributeResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return ResetImageAttributeResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;ResetImageAttributeResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;ResetImageAttributeResponse&gt;&gt;();
     *  for (ResetImageAttributeRequest request : requests) {
     *      responses.add(client.resetImageAttributeAsync(request));
     *  }
     *  for (Future&lt;ResetImageAttributeResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          ResetImageAttributeResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          ResetImageAttributeRequest request
     * @return Future&lt;ResetImageAttributeResponse&gt; future pointer to ResetImageAttributeResponse
     * 
     */
    public Future<ResetImageAttributeResponse> resetImageAttributeAsync(final ResetImageAttributeRequest request) {
        Future<ResetImageAttributeResponse> response = asyncExecutor.submit(new Callable<ResetImageAttributeResponse>() {

            public ResetImageAttributeResponse call() throws AmazonEC2Exception {
                return resetImageAttribute(request);
            }
            });
        return response;
    }
        
    /**
     * Revoke Security Group Ingress 
     *
     * The RevokeSecurityGroupIngress operation revokes permissions from a security
     * group. The permissions used to revoke must be specified using the same values
     * used to grant the permissions.
     * Permissions are specified by IP protocol (TCP, UDP, or ICMP), the source of the
     * request (by IP range or an Amazon EC2 user-group pair), the source and
     * destination port ranges (for TCP and UDP), and the ICMP codes and types (for
     * ICMP).
     * Permission changes are quickly propagated to instances within the security
     * group. However, depending on the number of instances in the group, a small
     * delay is might occur, .
     * When revoking a user/group pair permission, GroupName, SourceSecurityGroupName
     * and SourceSecurityGroupOwnerId must be specified. When authorizing a CIDR IP
     * permission, GroupName, IpProtocol, FromPort, ToPort and CidrIp must be
     * specified. Mixing these two types of parameters is not allowed.
     * 
     * @param request
     *          RevokeSecurityGroupIngressRequest request
     * @return
     *          RevokeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RevokeSecurityGroupIngressResponse revokeSecurityGroupIngress(RevokeSecurityGroupIngressRequest request) throws AmazonEC2Exception {
        return invoke(RevokeSecurityGroupIngressResponse.class, convertRevokeSecurityGroupIngress(request));
    }

    /**
     * Non-blocking Revoke Security Group Ingress 
     * <p/>
     * Returns <code>future</code> pointer to RevokeSecurityGroupIngressResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return RevokeSecurityGroupIngressResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;RevokeSecurityGroupIngressResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;RevokeSecurityGroupIngressResponse&gt;&gt;();
     *  for (RevokeSecurityGroupIngressRequest request : requests) {
     *      responses.add(client.revokeSecurityGroupIngressAsync(request));
     *  }
     *  for (Future&lt;RevokeSecurityGroupIngressResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          RevokeSecurityGroupIngressResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          RevokeSecurityGroupIngressRequest request
     * @return Future&lt;RevokeSecurityGroupIngressResponse&gt; future pointer to RevokeSecurityGroupIngressResponse
     * 
     */
    public Future<RevokeSecurityGroupIngressResponse> revokeSecurityGroupIngressAsync(final RevokeSecurityGroupIngressRequest request) {
        Future<RevokeSecurityGroupIngressResponse> response = asyncExecutor.submit(new Callable<RevokeSecurityGroupIngressResponse>() {

            public RevokeSecurityGroupIngressResponse call() throws AmazonEC2Exception {
                return revokeSecurityGroupIngress(request);
            }
            });
        return response;
    }
        
    /**
     * Run Instances 
     *
     * The RunInstances operation launches a specified number of instances.
     * If Amazon EC2 cannot launch the minimum number AMIs you request, no instances
     * launch. If there is insufficient capacity to launch the maximum number of AMIs
     * you request, Amazon EC2 launches as many as possible to satisfy the requested
     * maximum values.
     * Every instance is launched in a security group. If you do not specify a
     * security group at launch, the instances start in your default security group.
     * For more information on creating security groups, see CreateSecurityGroup.
     * An optional instance type can be specified. For information about instance
     * types, see Instance Types.
     * You can provide an optional key pair ID for each image in the launch request
     * (for more information, see CreateKeyPair). All instances that are created from
     * images that use this key pair will have access to the associated public key at
     * boot. You can use this key to provide secure access to an instance of an image
     * on a per-instance basis. Amazon EC2 public images use this feature to provide
     * secure access without passwords.
     * Important:
     * Launching public images without a key pair ID will leave them inaccessible.
     * The public key material is made available to the instance at boot time by
     * placing it in the openssh_id.pub file on a logical device that is exposed to
     * the instance as /dev/sda2 (the ephemeral store). The format of this file is
     * suitable for use as an entry within ~/.ssh/authorized_keys (the OpenSSH
     * format). This can be done at boot (e.g., as part of rc.local) allowing for
     * secure access without passwords.
     * Optional user data can be provided in the launch request. All instances that
     * collectively comprise the launch request have access to this data For more
     * information, see Instance Metadata.
     * Note:
     * If any of the AMIs have a product code attached for which the user has not
     * subscribed, the RunInstances call will fail.
     * Important:
     * We strongly recommend using the 2.6.18 Xen stock kernel with the c1.medium and
     * c1.xlarge instances. Although the default Amazon EC2 kernels will work, the new
     * kernels provide greater stability and performance for these instance types. For
     * more information about kernels, see Kernels, RAM Disks, and Block Device
     * Mappings.
     * 
     * @param request
     *          RunInstancesRequest request
     * @return
     *          RunInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RunInstancesResponse runInstances(RunInstancesRequest request) throws AmazonEC2Exception {
        return invoke(RunInstancesResponse.class, convertRunInstances(request));
    }

    /**
     * Non-blocking Run Instances 
     * <p/>
     * Returns <code>future</code> pointer to RunInstancesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return RunInstancesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;RunInstancesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;RunInstancesResponse&gt;&gt;();
     *  for (RunInstancesRequest request : requests) {
     *      responses.add(client.runInstancesAsync(request));
     *  }
     *  for (Future&lt;RunInstancesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          RunInstancesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          RunInstancesRequest request
     * @return Future&lt;RunInstancesResponse&gt; future pointer to RunInstancesResponse
     * 
     */
    public Future<RunInstancesResponse> runInstancesAsync(final RunInstancesRequest request) {
        Future<RunInstancesResponse> response = asyncExecutor.submit(new Callable<RunInstancesResponse>() {

            public RunInstancesResponse call() throws AmazonEC2Exception {
                return runInstances(request);
            }
            });
        return response;
    }
        
    /**
     * Terminate Instances 
     *
     * The TerminateInstances operation shuts down one or more instances. This
     * operation is idempotent; if you terminate an instance more than once, each call
     * will succeed.
     * Terminated instances will remain visible after termination (approximately one
     * hour).
     * 
     * @param request
     *          TerminateInstancesRequest request
     * @return
     *          TerminateInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public TerminateInstancesResponse terminateInstances(TerminateInstancesRequest request) throws AmazonEC2Exception {
        return invoke(TerminateInstancesResponse.class, convertTerminateInstances(request));
    }

    /**
     * Non-blocking Terminate Instances 
     * <p/>
     * Returns <code>future</code> pointer to TerminateInstancesResponse
     * <p/>
     * If response is ready, call to <code>future.get()</code> 
     * will return TerminateInstancesResponse. 
     * <p/>
     * If response is not ready, call to <code>future.get()</code> will block the 
     * calling thread until response is returned. 
     * <p/>
     * Note, <code>future.get()</code> will throw wrapped runtime exception. 
     * <p/>
     * If service error has occured, AmazonEC2Exception can be extracted with
     * <code>exception.getCause()</code>
     * <p/>
     * Usage example for parallel processing:
     * <pre>
     *
     *  List&lt;Future&lt;TerminateInstancesResponse&gt;&gt; responses = new ArrayList&lt;Future&lt;TerminateInstancesResponse&gt;&gt;();
     *  for (TerminateInstancesRequest request : requests) {
     *      responses.add(client.terminateInstancesAsync(request));
     *  }
     *  for (Future&lt;TerminateInstancesResponse&gt; future : responses) {
     *      while (!future.isDone()) {
     *          Thread.yield();
     *      }
     *      try {
     *          TerminateInstancesResponse response = future.get();
     *      // use response
     *      } catch (Exception e) {
     *          if (e instanceof AmazonEC2Exception) {
     *              AmazonEC2Exception exception = AmazonEC2Exception.class.cast(e);
     *          // handle AmazonEC2Exception
     *          } else {
     *          // handle other exceptions
     *          }
     *      }
     *  }
     * </pre>
     *
     * @param request
     *          TerminateInstancesRequest request
     * @return Future&lt;TerminateInstancesResponse&gt; future pointer to TerminateInstancesResponse
     * 
     */
    public Future<TerminateInstancesResponse> terminateInstancesAsync(final TerminateInstancesRequest request) {
        Future<TerminateInstancesResponse> response = asyncExecutor.submit(new Callable<TerminateInstancesResponse>() {

            public TerminateInstancesResponse call() throws AmazonEC2Exception {
                return terminateInstances(request);
            }
            });
        return response;
    }


    // Private API ------------------------------------------------------------//

    /**
     * Configure HttpClient with set of defaults as well as configuration
     * from AmazonEC2Config instance
     *
     */
    private HttpClient configureHttpClient() {

        /* Set http client parameters */
        HttpClientParams httpClientParams = new HttpClientParams();
        httpClientParams.setParameter(HttpMethodParams.USER_AGENT, config.getUserAgent());
        httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER, new HttpMethodRetryHandler() {

            public boolean retryMethod(HttpMethod method, IOException exception, int executionCount) {
                if (executionCount > 3) {
                    log.debug("Maximum Number of Retry attempts reached, will not retry");
                    return false;
                }
                log.debug("Retrying request. Attempt " + executionCount);
                if (exception instanceof NoHttpResponseException) {
                    log.debug("Retrying on NoHttpResponseException");
                    return true;
                }
                if (exception instanceof InterruptedIOException) {
                    log.debug("Will not retry on InterruptedIOException", exception);
                    return false;
                }
                if (exception instanceof UnknownHostException) {
                    log.debug("Will not retry on UnknownHostException", exception);
                    return false;
                }
                if (!method.isRequestSent()) {
                    log.debug("Retrying on failed sent request");
                    return true;
                }
                return false;
            }
        });

        /* Set host configuration */
        HostConfiguration hostConfiguration = new HostConfiguration();

        /* Set connection manager parameters */
        HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
        connectionManagerParams.setConnectionTimeout(50000);
        connectionManagerParams.setSoTimeout(50000);
        connectionManagerParams.setStaleCheckingEnabled(true);
        connectionManagerParams.setTcpNoDelay(true);
        connectionManagerParams.setMaxTotalConnections(config.getMaxAsyncQueueSize());
        connectionManagerParams.setMaxConnectionsPerHost(hostConfiguration, config.getMaxAsyncQueueSize());

        /* Set connection manager */
        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(connectionManagerParams);

        /* Set http client */
        httpClient = new HttpClient(httpClientParams, connectionManager);

        /* Set proxy if configured */
        if (config.isSetProxyHost() && config.isSetProxyPort()) {
            log.info("Configuring Proxy. Proxy Host: " + config.getProxyHost() +
                    "Proxy Port: " + config.getProxyPort() );
            hostConfiguration.setProxy(config.getProxyHost(), config.getProxyPort());
            if (config.isSetProxyUsername() &&   config.isSetProxyPassword()) {
                httpClient.getState().setProxyCredentials (new AuthScope(
                                          config.getProxyHost(),
                                          config.getProxyPort()),
                                          new UsernamePasswordCredentials(
                                              config.getProxyUsername(),
                                              config.getProxyPassword()));

            }
         }

        httpClient.setHostConfiguration(hostConfiguration);
        return httpClient;
    }

    /**
     * Invokes request using parameters from parameters map.
     * Returns response of the T type passed to this method
     */
    private <T> T invoke(Class<T> clazz, Map<String, String> parameters)
            throws AmazonEC2Exception {

        String actionName = parameters.get("Action");
        T response = null;
        String responseBodyString = null;
        PostMethod method = new PostMethod(config.getServiceURL());
        int status = -1;

        log.debug("Invoking" + actionName + " request. Current parameters: " + parameters);

        try {

            /* Set content type and encoding */
            log.debug("Setting content-type to application/x-www-form-urlencoded; charset=utf-8...");
            method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            /* Add required request parameters and set request body */
            log.debug("Adding required parameters...");
            addRequiredParametersToRequest(method, parameters);
            log.debug("Done adding additional required parameteres. Parameters now: " + parameters);

            boolean shouldRetry = true;
            int retries = 0;
            do {
                log.debug("Sending Request to host:  " + config.getServiceURL());

                try {

                    /* Submit request */
                    status = httpClient.executeMethod(method);



                    /* Consume response stream */
                    responseBodyString = getResponsBodyAsString(method.getResponseBodyAsStream());

                    /* Successful response. Attempting to unmarshal into the <Action>Response type */
                    if (status == HttpStatus.SC_OK) {
                        shouldRetry = false;
                        log.debug("Received Response. Status: " + status + ". " +
                                "Response Body: " + responseBodyString);
                        if (responseBodyString.endsWith(actionName + "Response>")) {
                            responseBodyString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                            log.debug("Attempting to transform " + actionName + "Response type...");
                            responseBodyString = ResponseTransformer.transform(responseBodyString, actionName);
                            log.debug("Transformed response to: " + responseBodyString);
                        }
                        log.debug("Attempting to unmarshal into the " + actionName + "Response type...");
                        response = clazz.cast(getUnmarshaller().unmarshal(new StreamSource(new StringReader(responseBodyString))));

                        log.debug("Unmarshalled response into " + actionName + "Response type.");

                    } else { /* Unsucessful response. Attempting to unmarshall into ErrorResponse  type */

                        log.debug("Received Response. Status: " + status + ". " +
                                "Response Body: " + responseBodyString);

                        if ((status == HttpStatus.SC_INTERNAL_SERVER_ERROR
                            || status == HttpStatus.SC_SERVICE_UNAVAILABLE)
                            && pauseIfRetryNeeded(++retries)){
                            shouldRetry = true;
                        } else {
                            log.debug("Attempting to unmarshal into the ErrorResponse type...");
                            ErrorResponse errorResponse = (ErrorResponse) getUnmarshaller().unmarshal(new StreamSource(new StringReader(responseBodyString)));

                            log.debug("Unmarshalled response into the ErrorResponse type.");

                            com.amazonaws.ec2.model.Error error = errorResponse.getError().get(0);

                                    throw new AmazonEC2Exception(error.getMessage(),
                                    status,
                                    error.getCode(),
                                    error.getType(),
                                    errorResponse.getRequestId(),
                                    errorResponse.toXML());
                        }
                    }
                } catch (JAXBException je) {
                    /* Response cannot be unmarshalled neither as <Action>Response or ErrorResponse types.
                    Checking for other possible errors. */

                    log.debug ("Caught JAXBException", je);
                    log.debug("Response cannot be unmarshalled neither as " + actionName + "Response or ErrorResponse types." +
                            "Checking for other possible errors.");

                    AmazonEC2Exception awse = processErrors(responseBodyString, status);

                    throw awse;

                } catch (IOException ioe) {
                    log.error("Caught IOException exception", ioe);
                    throw new AmazonEC2Exception("Internal Error", ioe);
                } catch (Exception e) {
                    log.error("Caught Exception", e);
                    throw new AmazonEC2Exception(e);
                } finally {
                    method.releaseConnection();
                }
            } while (shouldRetry);

        } catch (AmazonEC2Exception se) {
            log.error("Caught AmazonEC2Exception", se);
            throw se;

        } catch (Throwable t) {
            log.error("Caught Exception", t);
            throw new AmazonEC2Exception(t);
        }
        return response;
    }

    /**
     * Read stream into string
     * @param input stream to read
     */
    private String getResponsBodyAsString(InputStream input) throws IOException {
        String responsBodyString = null;
        try {
            Reader reader = new InputStreamReader(input, "UTF-8");
            StringBuilder b = new StringBuilder();
            char[] c = new char[1024];
            int len;
            while (0 < (len = reader.read(c))) {
                b.append(c, 0, len);
            }
            responsBodyString = b.toString();
        } finally {
            input.close();
        }
        return responsBodyString;
    }

    /**
     * Exponential sleep on failed request. Sleeps and returns true if retry needed
     * @param retries current retry
     * @throws java.lang.InterruptedException
     */
    private boolean pauseIfRetryNeeded(int retries)
          throws InterruptedException {
        if (retries <= config.getMaxErrorRetry()) {
            long delay = (long) (Math.pow(4, retries) * 100L);
            log.debug("Retriable error detected, will retry in " + delay + "ms, attempt numer: " + retries);
            Thread.sleep(delay);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add authentication related and version parameter and set request body
     * with all of the parameters
     */
    private void addRequiredParametersToRequest(PostMethod method, Map<String, String> parameters)
            throws SignatureException {
        parameters.put("Version", config.getServiceVersion());
        parameters.put("SignatureVersion", config.getSignatureVersion());
        parameters.put("Timestamp", getFormattedTimestamp());
        parameters.put("AWSAccessKeyId",  this.awsAccessKeyId);
        parameters.put("Signature", signParameters(parameters, this.awsSecretAccessKey));
        for (Entry<String, String> entry : parameters.entrySet()) {
            method.addParameter(entry.getKey(), entry.getValue());
        }
    }

    private AmazonEC2Exception processErrors(String responseString, int status)  {
        AmazonEC2Exception ex = null;
        Matcher matcher = null;
        if (responseString != null && responseString.startsWith("<")) {
            matcher = ERROR_PATTERN_ONE.matcher(responseString);
            if (matcher.matches()) {
                ex = new AmazonEC2Exception(matcher.group(3), status,
                        matcher.group(2), "Unknown", matcher.group(1), responseString);
            } else {
                matcher = ERROR_PATTERN_TWO.matcher(responseString);
                if (matcher.matches()) {
                    ex = new AmazonEC2Exception(matcher.group(2), status,
                            matcher.group(1), "Unknown", matcher.group(4), responseString);
            } else {
                ex =  new AmazonEC2Exception("Internal Error", status);
                log.error("Service Error. Response Status: " + status);
            }
            }
        } else {
            ex =  new AmazonEC2Exception("Internal Error", status);
            log.error("Service Error. Response Status: " + status);
        }
        return ex;
    }

    /**
     * Formats date as ISO 8601 timestamp
     */
    private String getFormattedTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }

    /**
     * Computes RFC 2104-compliant HMAC signature for request parameters
     * Implements AWS Signature, as per following spec:
     *
     * If Signature Version is 0, it signs concatenated Action and Timestamp
     *
     * If Signature Version is 1, it performs the following:
     *
     * Sorts all  parameters (including SignatureVersion and excluding Signature,
     * the value of which is being created), ignoring case.
     *
     * Iterate over the sorted list and append the parameter name (in original case)
     * and then its value. It will not URL-encode the parameter values before
     * constructing this string. There are no separators.
     *
     *
     */
    private String signParameters(Map<String, String> parameters, String key)
            throws  SignatureException {

        String signatureVersion = parameters.get("SignatureVersion");
        StringBuffer data = new StringBuffer();

        if ("0".equals(signatureVersion)) {
            data.append(parameters.get("Action")).append(parameters.get("Timestamp"));
        } else if ("1".equals(signatureVersion))  {
            Map<String, String> sorted =  new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            parameters.remove("Signature");
            sorted.putAll(parameters);
            Iterator pairs = sorted.entrySet().iterator();
            while (pairs.hasNext()) {
                Map.Entry pair = (Map.Entry)pairs.next();
                data.append(pair.getKey());
                data.append(pair.getValue());
            }
        } else {
            throw new SignatureException("Invalid Signature Version specified");
        }
        return sign(data.toString(), key);
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     */
    private String sign(String data, String key) throws SignatureException {
        byte [] signature;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA1"));
                signature = Base64.encodeBase64(mac.doFinal(data.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new SignatureException("Failed to generate signature: " + e.getMessage(), e);
        }

        return new String(signature);
    }

    /**
     * Get unmarshaller for current thread
     */
    private Unmarshaller getUnmarshaller() {
        return unmarshaller.get();
    }
            /**
     * Convert AllocateAddressRequest to name value pairs
     */
    private Map<String, String> convertAllocateAddress(AllocateAddressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "AllocateAddress");

        return params;
    }
        
                            /**
     * Convert AssociateAddressRequest to name value pairs
     */
    private Map<String, String> convertAssociateAddress(AssociateAddressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "AssociateAddress");
        if (request.isSetInstanceId()) {
            params.put("InstanceId", request.getInstanceId());
        }
        if (request.isSetPublicIp()) {
            params.put("PublicIp", request.getPublicIp());
        }

        return params;
    }
        
                            /**
     * Convert AuthorizeSecurityGroupIngressRequest to name value pairs
     */
    private Map<String, String> convertAuthorizeSecurityGroupIngress(AuthorizeSecurityGroupIngressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "AuthorizeSecurityGroupIngress");
        if (request.isSetGroupName()) {
            params.put("GroupName", request.getGroupName());
        }
        if (request.isSetSourceSecurityGroupName()) {
            params.put("SourceSecurityGroupName", request.getSourceSecurityGroupName());
        }
        if (request.isSetSourceSecurityGroupOwnerId()) {
            params.put("SourceSecurityGroupOwnerId", request.getSourceSecurityGroupOwnerId());
        }
        if (request.isSetIpProtocol()) {
            params.put("IpProtocol", request.getIpProtocol());
        }
        if (request.isSetFromPort()) {
            params.put("FromPort", request.getFromPort() + "");
        }
        if (request.isSetToPort()) {
            params.put("ToPort", request.getToPort() + "");
        }
        if (request.isSetCidrIp()) {
            params.put("CidrIp", request.getCidrIp());
        }

        return params;
    }
        
                            /**
     * Convert ConfirmProductInstanceRequest to name value pairs
     */
    private Map<String, String> convertConfirmProductInstance(ConfirmProductInstanceRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "ConfirmProductInstance");
        if (request.isSetProductCode()) {
            params.put("ProductCode", request.getProductCode());
        }
        if (request.isSetInstanceId()) {
            params.put("InstanceId", request.getInstanceId());
        }

        return params;
    }
        
                            /**
     * Convert CreateKeyPairRequest to name value pairs
     */
    private Map<String, String> convertCreateKeyPair(CreateKeyPairRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "CreateKeyPair");
        if (request.isSetKeyName()) {
            params.put("KeyName", request.getKeyName());
        }

        return params;
    }
        
                            /**
     * Convert CreateSecurityGroupRequest to name value pairs
     */
    private Map<String, String> convertCreateSecurityGroup(CreateSecurityGroupRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "CreateSecurityGroup");
        if (request.isSetGroupName()) {
            params.put("GroupName", request.getGroupName());
        }
        if (request.isSetGroupDescription()) {
            params.put("GroupDescription", request.getGroupDescription());
        }

        return params;
    }
        
                            /**
     * Convert DeleteKeyPairRequest to name value pairs
     */
    private Map<String, String> convertDeleteKeyPair(DeleteKeyPairRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DeleteKeyPair");
        if (request.isSetKeyName()) {
            params.put("KeyName", request.getKeyName());
        }

        return params;
    }
        
                            /**
     * Convert DeleteSecurityGroupRequest to name value pairs
     */
    private Map<String, String> convertDeleteSecurityGroup(DeleteSecurityGroupRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DeleteSecurityGroup");
        if (request.isSetGroupName()) {
            params.put("GroupName", request.getGroupName());
        }

        return params;
    }
        
                            /**
     * Convert DeregisterImageRequest to name value pairs
     */
    private Map<String, String> convertDeregisterImage(DeregisterImageRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DeregisterImage");
        if (request.isSetImageId()) {
            params.put("ImageId", request.getImageId());
        }

        return params;
    }
        
                            /**
     * Convert DescribeAddressesRequest to name value pairs
     */
    private Map<String, String> convertDescribeAddresses(DescribeAddressesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeAddresses");
        java.util.List<String> publicIpList  =  request.getPublicIp();
        for  (String publicIp : publicIpList) { 
            params.put("PublicIp" + "."  + (publicIpList.indexOf(publicIp) + 1), publicIp);
        }	

        return params;
    }
        
                            /**
     * Convert DescribeAvailabilityZonesRequest to name value pairs
     */
    private Map<String, String> convertDescribeAvailabilityZones(DescribeAvailabilityZonesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeAvailabilityZones");
        java.util.List<String> zoneNameList  =  request.getZoneName();
        for  (String zoneName : zoneNameList) { 
            params.put("ZoneName" + "."  + (zoneNameList.indexOf(zoneName) + 1), zoneName);
        }	

        return params;
    }
        
                            /**
     * Convert DescribeImageAttributeRequest to name value pairs
     */
    private Map<String, String> convertDescribeImageAttribute(DescribeImageAttributeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeImageAttribute");
        if (request.isSetImageId()) {
            params.put("ImageId", request.getImageId());
        }
        if (request.isSetAttribute()) {
            params.put("Attribute", request.getAttribute());
        }

        return params;
    }
        
                            /**
     * Convert DescribeImagesRequest to name value pairs
     */
    private Map<String, String> convertDescribeImages(DescribeImagesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeImages");
        java.util.List<String> imageIdList  =  request.getImageId();
        for  (String imageId : imageIdList) { 
            params.put("ImageId" + "."  + (imageIdList.indexOf(imageId) + 1), imageId);
        }	
        java.util.List<String> ownerList  =  request.getOwner();
        for  (String owner : ownerList) { 
            params.put("Owner" + "."  + (ownerList.indexOf(owner) + 1), owner);
        }	
        java.util.List<String> executableByList  =  request.getExecutableBy();
        for  (String executableBy : executableByList) { 
            params.put("ExecutableBy" + "."  + (executableByList.indexOf(executableBy) + 1), executableBy);
        }	

        return params;
    }
        
                            /**
     * Convert DescribeInstancesRequest to name value pairs
     */
    private Map<String, String> convertDescribeInstances(DescribeInstancesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeInstances");
        java.util.List<String> instanceIdList  =  request.getInstanceId();
        for  (String instanceId : instanceIdList) { 
            params.put("InstanceId" + "."  + (instanceIdList.indexOf(instanceId) + 1), instanceId);
        }	

        return params;
    }
        
                            /**
     * Convert DescribeKeyPairsRequest to name value pairs
     */
    private Map<String, String> convertDescribeKeyPairs(DescribeKeyPairsRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeKeyPairs");
        java.util.List<String> keyNameList  =  request.getKeyName();
        for  (String keyName : keyNameList) { 
            params.put("KeyName" + "."  + (keyNameList.indexOf(keyName) + 1), keyName);
        }	

        return params;
    }
        
                            /**
     * Convert DescribeSecurityGroupsRequest to name value pairs
     */
    private Map<String, String> convertDescribeSecurityGroups(DescribeSecurityGroupsRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeSecurityGroups");
        java.util.List<String> groupNameList  =  request.getGroupName();
        for  (String groupName : groupNameList) { 
            params.put("GroupName" + "."  + (groupNameList.indexOf(groupName) + 1), groupName);
        }	

        return params;
    }
        
                            /**
     * Convert DisassociateAddressRequest to name value pairs
     */
    private Map<String, String> convertDisassociateAddress(DisassociateAddressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DisassociateAddress");
        if (request.isSetPublicIp()) {
            params.put("PublicIp", request.getPublicIp());
        }

        return params;
    }
        
                            /**
     * Convert GetConsoleOutputRequest to name value pairs
     */
    private Map<String, String> convertGetConsoleOutput(GetConsoleOutputRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "GetConsoleOutput");
        if (request.isSetInstanceId()) {
            params.put("InstanceId", request.getInstanceId());
        }

        return params;
    }
        
                            /**
     * Convert ModifyImageAttributeRequest to name value pairs
     */
    private Map<String, String> convertModifyImageAttribute(ModifyImageAttributeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "ModifyImageAttribute");
        if (request.isSetImageId()) {
            params.put("ImageId", request.getImageId());
        }
        if (request.isSetAttribute()) {
            params.put("Attribute", request.getAttribute());
        }
        if (request.isSetOperationType()) {
            params.put("OperationType", request.getOperationType());
        }
        java.util.List<String> userIdList  =  request.getUserId();
        for  (String userId : userIdList) { 
            params.put("UserId" + "."  + (userIdList.indexOf(userId) + 1), userId);
        }	
        java.util.List<String> userGroupList  =  request.getUserGroup();
        for  (String userGroup : userGroupList) { 
            params.put("UserGroup" + "."  + (userGroupList.indexOf(userGroup) + 1), userGroup);
        }	
        java.util.List<String> productCodeList  =  request.getProductCode();
        for  (String productCode : productCodeList) { 
            params.put("ProductCode" + "."  + (productCodeList.indexOf(productCode) + 1), productCode);
        }	

        return params;
    }
        
                            /**
     * Convert RebootInstancesRequest to name value pairs
     */
    private Map<String, String> convertRebootInstances(RebootInstancesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "RebootInstances");
        java.util.List<String> instanceIdList  =  request.getInstanceId();
        for  (String instanceId : instanceIdList) { 
            params.put("InstanceId" + "."  + (instanceIdList.indexOf(instanceId) + 1), instanceId);
        }	

        return params;
    }
        
                            /**
     * Convert RegisterImageRequest to name value pairs
     */
    private Map<String, String> convertRegisterImage(RegisterImageRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "RegisterImage");
        if (request.isSetImageLocation()) {
            params.put("ImageLocation", request.getImageLocation());
        }

        return params;
    }
        
                            /**
     * Convert ReleaseAddressRequest to name value pairs
     */
    private Map<String, String> convertReleaseAddress(ReleaseAddressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "ReleaseAddress");
        if (request.isSetPublicIp()) {
            params.put("PublicIp", request.getPublicIp());
        }

        return params;
    }
        
                            /**
     * Convert ResetImageAttributeRequest to name value pairs
     */
    private Map<String, String> convertResetImageAttribute(ResetImageAttributeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "ResetImageAttribute");
        if (request.isSetImageId()) {
            params.put("ImageId", request.getImageId());
        }
        if (request.isSetAttribute()) {
            params.put("Attribute", request.getAttribute());
        }

        return params;
    }
        
                            /**
     * Convert RevokeSecurityGroupIngressRequest to name value pairs
     */
    private Map<String, String> convertRevokeSecurityGroupIngress(RevokeSecurityGroupIngressRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "RevokeSecurityGroupIngress");
        if (request.isSetGroupName()) {
            params.put("GroupName", request.getGroupName());
        }
        if (request.isSetSourceSecurityGroupName()) {
            params.put("SourceSecurityGroupName", request.getSourceSecurityGroupName());
        }
        if (request.isSetSourceSecurityGroupOwnerId()) {
            params.put("SourceSecurityGroupOwnerId", request.getSourceSecurityGroupOwnerId());
        }
        if (request.isSetIpProtocol()) {
            params.put("IpProtocol", request.getIpProtocol());
        }
        if (request.isSetFromPort()) {
            params.put("FromPort", request.getFromPort() + "");
        }
        if (request.isSetToPort()) {
            params.put("ToPort", request.getToPort() + "");
        }
        if (request.isSetCidrIp()) {
            params.put("CidrIp", request.getCidrIp());
        }

        return params;
    }
        
                            /**
     * Convert RunInstancesRequest to name value pairs
     */
    private Map<String, String> convertRunInstances(RunInstancesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "RunInstances");
        if (request.isSetImageId()) {
            params.put("ImageId", request.getImageId());
        }
        if (request.isSetMinCount()) {
            params.put("MinCount", request.getMinCount() + "");
        }
        if (request.isSetMaxCount()) {
            params.put("MaxCount", request.getMaxCount() + "");
        }
        if (request.isSetKeyName()) {
            params.put("KeyName", request.getKeyName());
        }
        java.util.List<String> securityGroupList  =  request.getSecurityGroup();
        for  (String securityGroup : securityGroupList) { 
            params.put("SecurityGroup" + "."  + (securityGroupList.indexOf(securityGroup) + 1), securityGroup);
        }	
        if (request.isSetUserData()) {
            params.put("UserData", request.getUserData());
        }
        if (request.isSetInstanceType()) {
            params.put("InstanceType", request.getInstanceType());
        }
        if (request.isSetPlacement()) {
            Placement  placement = request.getPlacement();
            if (placement.isSetAvailabilityZone()) {
                params.put("Placement" + "." + "AvailabilityZone", placement.getAvailabilityZone());
            }
        } 
        if (request.isSetKernelId()) {
            params.put("KernelId", request.getKernelId());
        }
        if (request.isSetRamdiskId()) {
            params.put("RamdiskId", request.getRamdiskId());
        }
        java.util.List<BlockDeviceMapping> blockDeviceMappingList = request.getBlockDeviceMapping();
        for (BlockDeviceMapping blockDeviceMapping : blockDeviceMappingList) {
            if (blockDeviceMapping.isSetVirtualName()) {
                params.put("BlockDeviceMapping" + "."  + (blockDeviceMappingList.indexOf(blockDeviceMapping) + 1) + "." + "VirtualName", blockDeviceMapping.getVirtualName());
            }
            if (blockDeviceMapping.isSetDeviceName()) {
                params.put("BlockDeviceMapping" + "."  + (blockDeviceMappingList.indexOf(blockDeviceMapping) + 1) + "." + "DeviceName", blockDeviceMapping.getDeviceName());
            }

        }

        return params;
    }
        
                            /**
     * Convert TerminateInstancesRequest to name value pairs
     */
    private Map<String, String> convertTerminateInstances(TerminateInstancesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "TerminateInstances");
        java.util.List<String> instanceIdList  =  request.getInstanceId();
        for  (String instanceId : instanceIdList) { 
            params.put("InstanceId" + "."  + (instanceIdList.indexOf(instanceId) + 1), instanceId);
        }	

        return params;
    }
        
                            /**
     * Convert DeleteVolumeRequest to name value pairs
     */
    private Map<String, String> convertDeleteVolume(DeleteVolumeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DeleteVolume");
        if (request.isSetVolumeId()) {
            params.put("VolumeId", request.getVolumeId());
        }

        return params;
    }
        
                            /**
     * Convert CreateVolumeRequest to name value pairs
     */
    private Map<String, String> convertCreateVolume(CreateVolumeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "CreateVolume");
        if (request.isSetSize()) {
            params.put("Size", request.getSize());
        }
        if (request.isSetSnapshotId()) {
            params.put("SnapshotId", request.getSnapshotId());
        }
        if (request.isSetZone()) {
            params.put("Zone", request.getZone());
        }

        return params;
    }
        
                            /**
     * Convert DescribeVolumesRequest to name value pairs
     */
    private Map<String, String> convertDescribeVolumes(DescribeVolumesRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeVolumes");
        java.util.List<String> volumeIdList  =  request.getVolumeId();
        for  (String volumeId : volumeIdList) { 
            params.put("VolumeId" + "."  + (volumeIdList.indexOf(volumeId) + 1), volumeId);
        }	

        return params;
    }
        
                            /**
     * Convert DetachVolumeRequest to name value pairs
     */
    private Map<String, String> convertDetachVolume(DetachVolumeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DetachVolume");
        if (request.isSetVolumeId()) {
            params.put("VolumeId", request.getVolumeId());
        }
        if (request.isSetInstanceId()) {
            params.put("InstanceId", request.getInstanceId());
        }
        if (request.isSetDevice()) {
            params.put("Device", request.getDevice());
        }
        if (request.isSetForce()) {
            params.put("Force", request.isForce() + "");
        }

        return params;
    }
        
                            /**
     * Convert DescribeSnapshotsRequest to name value pairs
     */
    private Map<String, String> convertDescribeSnapshots(DescribeSnapshotsRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DescribeSnapshots");
        java.util.List<String> snapshotIdList  =  request.getSnapshotId();
        for  (String snapshotId : snapshotIdList) { 
            params.put("SnapshotId" + "."  + (snapshotIdList.indexOf(snapshotId) + 1), snapshotId);
        }	

        return params;
    }
        
                            /**
     * Convert DeleteSnapshotRequest to name value pairs
     */
    private Map<String, String> convertDeleteSnapshot(DeleteSnapshotRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "DeleteSnapshot");
        if (request.isSetSnapshotId()) {
            params.put("SnapshotId", request.getSnapshotId());
        }

        return params;
    }
        
                            /**
     * Convert CreateSnapshotRequest to name value pairs
     */
    private Map<String, String> convertCreateSnapshot(CreateSnapshotRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "CreateSnapshot");
        if (request.isSetVolumeId()) {
            params.put("VolumeId", request.getVolumeId());
        }

        return params;
    }
        
                            /**
     * Convert AttachVolumeRequest to name value pairs
     */
    private Map<String, String> convertAttachVolume(AttachVolumeRequest request) {
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "AttachVolume");
        if (request.isSetVolumeId()) {
            params.put("VolumeId", request.getVolumeId());
        }
        if (request.isSetInstanceId()) {
            params.put("InstanceId", request.getInstanceId());
        }
        if (request.isSetDevice()) {
            params.put("Device", request.getDevice());
        }

        return params;
    }
        
                                                                                                                                                                                                                                                                                                                                                                
    private static class ResponseTransformer {

        /**
         * Transforms XML with XSLT into string.
         * @param response XML string of the response
         * @param actionName action name to perform the transformation on
         * @return transformed string
         */
        private static String transform(String response, String actionName) {
            return transform(fromString(response),
                    fromResource(actionName), null);
        }

        /**
         * Transforms XML with XSLT into string.
         * @param xml source document
         * @param xslt source template
         * @param parameters map of parameters to pass to XSLT for transformation
         * @return transformed string
         */
        private static String transform(Source xml, Source xslt, Map<String, String> parameters) {
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer(xslt);
                StringWriter output = new StringWriter();
                if (parameters != null) {
                    for (Entry<String, String> entry : parameters.entrySet()) {
                        transformer.setParameter(entry.getKey(), entry.getValue());
                    }
                }
                transformer.transform(xml, new StreamResult(output));
                return output.toString();
            } catch (Exception ex) {
                throw new RuntimeException("XSLT transformation failed", ex);
            }
        }

        private static Source fromString(String string) {
            return new StreamSource(new StringReader(string));
        }

        private static Source fromResource(String resource) {
            return new StreamSource(AmazonEC2.class.getClassLoader()
                    .getResourceAsStream("com/amazonaws/ec2/model/"
                    + resource + "Response.xslt"));
        }
    }
}