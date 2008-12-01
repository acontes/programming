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
import java.util.concurrent.Future;


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
 */
public interface AmazonEC2 {

    /**
     * Allocate Address 
     *
     * The AllocateAddress operation acquires an elastic IP address for use with your
     * account.
     *   
     * @param request
     *          AllocateAddress Action
     * @return
     *          AllocateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AllocateAddressResponse allocateAddress(AllocateAddressRequest request) throws AmazonEC2Exception;

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
    public Future<AllocateAddressResponse> allocateAddressAsync(AllocateAddressRequest request);

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
     *          AssociateAddress Action
     * @return
     *          AssociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AssociateAddressResponse associateAddress(AssociateAddressRequest request)
            throws AmazonEC2Exception;

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
    public Future<AssociateAddressResponse> associateAddressAsync(AssociateAddressRequest request);

    /**
     * Attach Volume 
     *
     * Attach a previously created volume to a running instance.
     *   
     * @param request
     *          AttachVolume Action
     * @return
     *          AttachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AttachVolumeResponse attachVolume(AttachVolumeRequest request) throws AmazonEC2Exception;

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
    public Future<AttachVolumeResponse> attachVolumeAsync(AttachVolumeRequest request);

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
     *          AuthorizeSecurityGroupIngress Action
     * @return
     *          AuthorizeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AuthorizeSecurityGroupIngressResponse authorizeSecurityGroupIngress(
            AuthorizeSecurityGroupIngressRequest request) throws AmazonEC2Exception;

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
    public Future<AuthorizeSecurityGroupIngressResponse> authorizeSecurityGroupIngressAsync(
            AuthorizeSecurityGroupIngressRequest request);

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
     *          ConfirmProductInstance Action
     * @return
     *          ConfirmProductInstance Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ConfirmProductInstanceResponse confirmProductInstance(ConfirmProductInstanceRequest request)
            throws AmazonEC2Exception;

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
    public Future<ConfirmProductInstanceResponse> confirmProductInstanceAsync(
            ConfirmProductInstanceRequest request);

    /**
     * Create Key Pair 
     *
     * The CreateKeyPair operation creates a new 2048 bit RSA key pair and returns a
     * unique ID that can be used to reference this key pair when launching new
     * instances. For more information, see RunInstances.
     *   
     * @param request
     *          CreateKeyPair Action
     * @return
     *          CreateKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateKeyPairResponse createKeyPair(CreateKeyPairRequest request) throws AmazonEC2Exception;

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
    public Future<CreateKeyPairResponse> createKeyPairAsync(CreateKeyPairRequest request);

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
     *          CreateSecurityGroup Action
     * @return
     *          CreateSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSecurityGroupResponse createSecurityGroup(CreateSecurityGroupRequest request)
            throws AmazonEC2Exception;

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
    public Future<CreateSecurityGroupResponse> createSecurityGroupAsync(CreateSecurityGroupRequest request);

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
     *          CreateSnapshot Action
     * @return
     *          CreateSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSnapshotResponse createSnapshot(CreateSnapshotRequest request) throws AmazonEC2Exception;

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
    public Future<CreateSnapshotResponse> createSnapshotAsync(CreateSnapshotRequest request);

    /**
     * Create Volume 
     *
     * Initializes an empty volume of a given size
     *   
     * @param request
     *          CreateVolume Action
     * @return
     *          CreateVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateVolumeResponse createVolume(CreateVolumeRequest request) throws AmazonEC2Exception;

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
    public Future<CreateVolumeResponse> createVolumeAsync(CreateVolumeRequest request);

    /**
     * Delete Key Pair 
     *
     * The DeleteKeyPair operation deletes a key pair.
     *   
     * @param request
     *          DeleteKeyPair Action
     * @return
     *          DeleteKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteKeyPairResponse deleteKeyPair(DeleteKeyPairRequest request) throws AmazonEC2Exception;

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
    public Future<DeleteKeyPairResponse> deleteKeyPairAsync(DeleteKeyPairRequest request);

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
     *          DeleteSecurityGroup Action
     * @return
     *          DeleteSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSecurityGroupResponse deleteSecurityGroup(DeleteSecurityGroupRequest request)
            throws AmazonEC2Exception;

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
    public Future<DeleteSecurityGroupResponse> deleteSecurityGroupAsync(DeleteSecurityGroupRequest request);

    /**
     * Delete Snapshot 
     *
     * Deletes the snapshot identitied by snapshotId.
     *   
     * @param request
     *          DeleteSnapshot Action
     * @return
     *          DeleteSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSnapshotResponse deleteSnapshot(DeleteSnapshotRequest request) throws AmazonEC2Exception;

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
    public Future<DeleteSnapshotResponse> deleteSnapshotAsync(DeleteSnapshotRequest request);

    /**
     * Delete Volume 
     *
     * Deletes a  previously created volume. Once successfully deleted, a new volume  can be created with the same name.
     *   
     * @param request
     *          DeleteVolume Action
     * @return
     *          DeleteVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteVolumeResponse deleteVolume(DeleteVolumeRequest request) throws AmazonEC2Exception;

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
    public Future<DeleteVolumeResponse> deleteVolumeAsync(DeleteVolumeRequest request);

    /**
     * Deregister Image 
     *
     * The DeregisterImage operation deregisters an AMI. Once deregistered, instances
     * of the AMI can no longer be launched.
     *   
     * @param request
     *          DeregisterImage Action
     * @return
     *          DeregisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeregisterImageResponse deregisterImage(DeregisterImageRequest request) throws AmazonEC2Exception;

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
    public Future<DeregisterImageResponse> deregisterImageAsync(DeregisterImageRequest request);

    /**
     * Describe Addresses 
     *
     * The DescribeAddresses operation lists elastic IP addresses assigned to your
     * account.
     *   
     * @param request
     *          DescribeAddresses Action
     * @return
     *          DescribeAddresses Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAddressesResponse describeAddresses(DescribeAddressesRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeAddressesResponse> describeAddressesAsync(DescribeAddressesRequest request);

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
     *          DescribeAvailabilityZones Action
     * @return
     *          DescribeAvailabilityZones Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAvailabilityZonesResponse describeAvailabilityZones(
            DescribeAvailabilityZonesRequest request) throws AmazonEC2Exception;

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
    public Future<DescribeAvailabilityZonesResponse> describeAvailabilityZonesAsync(
            DescribeAvailabilityZonesRequest request);

    /**
     * Describe Image Attribute 
     *
     * The DescribeImageAttribute operation returns information about an attribute of
     * an AMI. Only one attribute can be specified per call.
     *   
     * @param request
     *          DescribeImageAttribute Action
     * @return
     *          DescribeImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImageAttributeResponse describeImageAttribute(DescribeImageAttributeRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeImageAttributeResponse> describeImageAttributeAsync(
            DescribeImageAttributeRequest request);

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
     *          DescribeImages Action
     * @return
     *          DescribeImages Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImagesResponse describeImages(DescribeImagesRequest request) throws AmazonEC2Exception;

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
    public Future<DescribeImagesResponse> describeImagesAsync(DescribeImagesRequest request);

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
     *          DescribeInstances Action
     * @return
     *          DescribeInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeInstancesResponse describeInstances(DescribeInstancesRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeInstancesResponse> describeInstancesAsync(DescribeInstancesRequest request);

    /**
     * Describe Key Pairs 
     *
     * The DescribeKeyPairs operation returns information about key pairs available to
     * you. If you specify key pairs, information about those key pairs is returned.
     * Otherwise, information for all registered key pairs is returned.
     *   
     * @param request
     *          DescribeKeyPairs Action
     * @return
     *          DescribeKeyPairs Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeKeyPairsResponse describeKeyPairs(DescribeKeyPairsRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeKeyPairsResponse> describeKeyPairsAsync(DescribeKeyPairsRequest request);

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
     *          DescribeSecurityGroups Action
     * @return
     *          DescribeSecurityGroups Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSecurityGroupsResponse describeSecurityGroups(DescribeSecurityGroupsRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeSecurityGroupsResponse> describeSecurityGroupsAsync(
            DescribeSecurityGroupsRequest request);

    /**
     * Describe Snapshots 
     *
     * Describes the indicated snapshots, or in lieu of that, all snapshots owned by the caller.
     *   
     * @param request
     *          DescribeSnapshots Action
     * @return
     *          DescribeSnapshots Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSnapshotsResponse describeSnapshots(DescribeSnapshotsRequest request)
            throws AmazonEC2Exception;

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
    public Future<DescribeSnapshotsResponse> describeSnapshotsAsync(DescribeSnapshotsRequest request);

    /**
     * Describe Volumes 
     *
     * Describes the status of the indicated or, in lieu of any specified,  all volumes belonging to the caller. Volumes that have been deleted are not described.
     *   
     * @param request
     *          DescribeVolumes Action
     * @return
     *          DescribeVolumes Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeVolumesResponse describeVolumes(DescribeVolumesRequest request) throws AmazonEC2Exception;

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
    public Future<DescribeVolumesResponse> describeVolumesAsync(DescribeVolumesRequest request);

    /**
     * Detach Volume 
     *
     * Detach a previously attached volume from a running instance.
     *   
     * @param request
     *          DetachVolume Action
     * @return
     *          DetachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DetachVolumeResponse detachVolume(DetachVolumeRequest request) throws AmazonEC2Exception;

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
    public Future<DetachVolumeResponse> detachVolumeAsync(DetachVolumeRequest request);

    /**
     * Disassociate Address 
     *
     * The DisassociateAddress operation disassociates the specified elastic IP
     * address from the instance to which it is assigned. This is an idempotent
     * operation. If you enter it more than once, Amazon EC2 does not return an error.
     *   
     * @param request
     *          DisassociateAddress Action
     * @return
     *          DisassociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DisassociateAddressResponse disassociateAddress(DisassociateAddressRequest request)
            throws AmazonEC2Exception;

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
    public Future<DisassociateAddressResponse> disassociateAddressAsync(DisassociateAddressRequest request);

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
     *          GetConsoleOutput Action
     * @return
     *          GetConsoleOutput Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public GetConsoleOutputResponse getConsoleOutput(GetConsoleOutputRequest request)
            throws AmazonEC2Exception;

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
    public Future<GetConsoleOutputResponse> getConsoleOutputAsync(GetConsoleOutputRequest request);

    /**
     * Modify Image Attribute 
     *
     * The ModifyImageAttribute operation modifies an attribute of an AMI.
     *   
     * @param request
     *          ModifyImageAttribute Action
     * @return
     *          ModifyImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ModifyImageAttributeResponse modifyImageAttribute(ModifyImageAttributeRequest request)
            throws AmazonEC2Exception;

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
    public Future<ModifyImageAttributeResponse> modifyImageAttributeAsync(ModifyImageAttributeRequest request);

    /**
     * Reboot Instances 
     *
     * The RebootInstances operation requests a reboot of one or more instances. This
     * operation is asynchronous; it only queues a request to reboot the specified
     * instance(s). The operation will succeed if the instances are valid and belong
     * to the user. Requests to reboot terminated instances are ignored.
     *   
     * @param request
     *          RebootInstances Action
     * @return
     *          RebootInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RebootInstancesResponse rebootInstances(RebootInstancesRequest request) throws AmazonEC2Exception;

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
    public Future<RebootInstancesResponse> rebootInstancesAsync(RebootInstancesRequest request);

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
     *          RegisterImage Action
     * @return
     *          RegisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RegisterImageResponse registerImage(RegisterImageRequest request) throws AmazonEC2Exception;

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
    public Future<RegisterImageResponse> registerImageAsync(RegisterImageRequest request);

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
     *          ReleaseAddress Action
     * @return
     *          ReleaseAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ReleaseAddressResponse releaseAddress(ReleaseAddressRequest request) throws AmazonEC2Exception;

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
    public Future<ReleaseAddressResponse> releaseAddressAsync(ReleaseAddressRequest request);

    /**
     * Reset Image Attribute 
     *
     * The ResetImageAttribute operation resets an attribute of an AMI to its default
     * value.
     * Note:
     * The productCodes attribute cannot be reset.
     *   
     * @param request
     *          ResetImageAttribute Action
     * @return
     *          ResetImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ResetImageAttributeResponse resetImageAttribute(ResetImageAttributeRequest request)
            throws AmazonEC2Exception;

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
    public Future<ResetImageAttributeResponse> resetImageAttributeAsync(ResetImageAttributeRequest request);

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
     *          RevokeSecurityGroupIngress Action
     * @return
     *          RevokeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RevokeSecurityGroupIngressResponse revokeSecurityGroupIngress(
            RevokeSecurityGroupIngressRequest request) throws AmazonEC2Exception;

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
    public Future<RevokeSecurityGroupIngressResponse> revokeSecurityGroupIngressAsync(
            RevokeSecurityGroupIngressRequest request);

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
     *          RunInstances Action
     * @return
     *          RunInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RunInstancesResponse runInstances(RunInstancesRequest request) throws AmazonEC2Exception;

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
    public Future<RunInstancesResponse> runInstancesAsync(RunInstancesRequest request);

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
     *          TerminateInstances Action
     * @return
     *          TerminateInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public TerminateInstancesResponse terminateInstances(TerminateInstancesRequest request)
            throws AmazonEC2Exception;

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
    public Future<TerminateInstancesResponse> terminateInstancesAsync(TerminateInstancesRequest request);

}