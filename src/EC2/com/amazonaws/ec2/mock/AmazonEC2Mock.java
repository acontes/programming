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

package com.amazonaws.ec2.mock;

import com.amazonaws.ec2.model.*;
import com.amazonaws.ec2.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 *
 * AmazonEC2Mock is the implementation of AmazonEC2 based
 * on the pre-populated set of XML files that serve local data. It simulates 
 * responses from Amazon EC2 service.
 *
 * Use this to test your application without making a call to Amazon EC2 
 *
 * Note, current Mock Service implementation does not valiadate requests
 *
 */
public class AmazonEC2Mock implements AmazonEC2 {

    private final Log log = LogFactory.getLog(AmazonEC2Mock.class);
    private static JAXBContext jaxbContext;
    private static ThreadLocal<Unmarshaller> unmarshaller;
    private ExecutorService asyncExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public Thread newThread(Runnable task) {
            Thread thread = new Thread(task, "AmazonEC2Mock-Thread-" + threadNumber.getAndIncrement());
            thread.setDaemon(Boolean.TRUE);
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        }
    });

    /** Initialize JAXBContext and  Unmarshaller **/
    static {
        try {
            jaxbContext = JAXBContext
                    .newInstance("com.amazonaws.ec2.model", AmazonEC2.class.getClassLoader());
        } catch (JAXBException ex) {
            throw new ExceptionInInitializerError(ex);
        }
        unmarshaller = new ThreadLocal<Unmarshaller>() {
            protected synchronized Unmarshaller initialValue() {
                try {
                    return jaxbContext.createUnmarshaller();
                } catch (JAXBException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        };
    }

    // Public API ------------------------------------------------------------//

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
    public AllocateAddressResponse allocateAddress(AllocateAddressRequest request) throws AmazonEC2Exception {
        AllocateAddressResponse response;
        try {
            response = (AllocateAddressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("AllocateAddressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<AllocateAddressResponse> allocateAddressAsync(final AllocateAddressRequest request) {
        Future<AllocateAddressResponse> response = asyncExecutor
                .submit(new Callable<AllocateAddressResponse>() {

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
     *          AssociateAddress Action
     * @return
     *          AssociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AssociateAddressResponse associateAddress(AssociateAddressRequest request)
            throws AmazonEC2Exception {
        AssociateAddressResponse response;
        try {
            response = (AssociateAddressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("AssociateAddressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<AssociateAddressResponse> associateAddressAsync(final AssociateAddressRequest request) {
        Future<AssociateAddressResponse> response = asyncExecutor
                .submit(new Callable<AssociateAddressResponse>() {

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
     *          AttachVolume Action
     * @return
     *          AttachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AttachVolumeResponse attachVolume(AttachVolumeRequest request) throws AmazonEC2Exception {
        AttachVolumeResponse response;
        try {
            response = (AttachVolumeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("AttachVolumeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          AuthorizeSecurityGroupIngress Action
     * @return
     *          AuthorizeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public AuthorizeSecurityGroupIngressResponse authorizeSecurityGroupIngress(
            AuthorizeSecurityGroupIngressRequest request) throws AmazonEC2Exception {
        AuthorizeSecurityGroupIngressResponse response;
        try {
            response = (AuthorizeSecurityGroupIngressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream(
                            "AuthorizeSecurityGroupIngressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<AuthorizeSecurityGroupIngressResponse> authorizeSecurityGroupIngressAsync(
            final AuthorizeSecurityGroupIngressRequest request) {
        Future<AuthorizeSecurityGroupIngressResponse> response = asyncExecutor
                .submit(new Callable<AuthorizeSecurityGroupIngressResponse>() {

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
     *          ConfirmProductInstance Action
     * @return
     *          ConfirmProductInstance Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ConfirmProductInstanceResponse confirmProductInstance(ConfirmProductInstanceRequest request)
            throws AmazonEC2Exception {
        ConfirmProductInstanceResponse response;
        try {
            response = (ConfirmProductInstanceResponse) getUnmarshaller()
                    .unmarshal(
                            new InputSource(this.getClass().getResourceAsStream(
                                    "ConfirmProductInstanceResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<ConfirmProductInstanceResponse> confirmProductInstanceAsync(
            final ConfirmProductInstanceRequest request) {
        Future<ConfirmProductInstanceResponse> response = asyncExecutor
                .submit(new Callable<ConfirmProductInstanceResponse>() {

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
     *          CreateKeyPair Action
     * @return
     *          CreateKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateKeyPairResponse createKeyPair(CreateKeyPairRequest request) throws AmazonEC2Exception {
        CreateKeyPairResponse response;
        try {
            response = (CreateKeyPairResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("CreateKeyPairResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          CreateSecurityGroup Action
     * @return
     *          CreateSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSecurityGroupResponse createSecurityGroup(CreateSecurityGroupRequest request)
            throws AmazonEC2Exception {
        CreateSecurityGroupResponse response;
        try {
            response = (CreateSecurityGroupResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("CreateSecurityGroupResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<CreateSecurityGroupResponse> createSecurityGroupAsync(
            final CreateSecurityGroupRequest request) {
        Future<CreateSecurityGroupResponse> response = asyncExecutor
                .submit(new Callable<CreateSecurityGroupResponse>() {

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
     *          CreateSnapshot Action
     * @return
     *          CreateSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateSnapshotResponse createSnapshot(CreateSnapshotRequest request) throws AmazonEC2Exception {
        CreateSnapshotResponse response;
        try {
            response = (CreateSnapshotResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("CreateSnapshotResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<CreateSnapshotResponse> createSnapshotAsync(final CreateSnapshotRequest request) {
        Future<CreateSnapshotResponse> response = asyncExecutor
                .submit(new Callable<CreateSnapshotResponse>() {

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
     *          CreateVolume Action
     * @return
     *          CreateVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public CreateVolumeResponse createVolume(CreateVolumeRequest request) throws AmazonEC2Exception {
        CreateVolumeResponse response;
        try {
            response = (CreateVolumeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("CreateVolumeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          DeleteKeyPair Action
     * @return
     *          DeleteKeyPair Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteKeyPairResponse deleteKeyPair(DeleteKeyPairRequest request) throws AmazonEC2Exception {
        DeleteKeyPairResponse response;
        try {
            response = (DeleteKeyPairResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DeleteKeyPairResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          DeleteSecurityGroup Action
     * @return
     *          DeleteSecurityGroup Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSecurityGroupResponse deleteSecurityGroup(DeleteSecurityGroupRequest request)
            throws AmazonEC2Exception {
        DeleteSecurityGroupResponse response;
        try {
            response = (DeleteSecurityGroupResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DeleteSecurityGroupResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DeleteSecurityGroupResponse> deleteSecurityGroupAsync(
            final DeleteSecurityGroupRequest request) {
        Future<DeleteSecurityGroupResponse> response = asyncExecutor
                .submit(new Callable<DeleteSecurityGroupResponse>() {

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
     *          DeleteSnapshot Action
     * @return
     *          DeleteSnapshot Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteSnapshotResponse deleteSnapshot(DeleteSnapshotRequest request) throws AmazonEC2Exception {
        DeleteSnapshotResponse response;
        try {
            response = (DeleteSnapshotResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DeleteSnapshotResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DeleteSnapshotResponse> deleteSnapshotAsync(final DeleteSnapshotRequest request) {
        Future<DeleteSnapshotResponse> response = asyncExecutor
                .submit(new Callable<DeleteSnapshotResponse>() {

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
     *          DeleteVolume Action
     * @return
     *          DeleteVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeleteVolumeResponse deleteVolume(DeleteVolumeRequest request) throws AmazonEC2Exception {
        DeleteVolumeResponse response;
        try {
            response = (DeleteVolumeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DeleteVolumeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          DeregisterImage Action
     * @return
     *          DeregisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DeregisterImageResponse deregisterImage(DeregisterImageRequest request) throws AmazonEC2Exception {
        DeregisterImageResponse response;
        try {
            response = (DeregisterImageResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DeregisterImageResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DeregisterImageResponse> deregisterImageAsync(final DeregisterImageRequest request) {
        Future<DeregisterImageResponse> response = asyncExecutor
                .submit(new Callable<DeregisterImageResponse>() {

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
     *          DescribeAddresses Action
     * @return
     *          DescribeAddresses Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAddressesResponse describeAddresses(DescribeAddressesRequest request)
            throws AmazonEC2Exception {
        DescribeAddressesResponse response;
        try {
            response = (DescribeAddressesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeAddressesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeAddressesResponse> describeAddressesAsync(final DescribeAddressesRequest request) {
        Future<DescribeAddressesResponse> response = asyncExecutor
                .submit(new Callable<DescribeAddressesResponse>() {

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
     *          DescribeAvailabilityZones Action
     * @return
     *          DescribeAvailabilityZones Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeAvailabilityZonesResponse describeAvailabilityZones(
            DescribeAvailabilityZonesRequest request) throws AmazonEC2Exception {
        DescribeAvailabilityZonesResponse response;
        try {
            response = (DescribeAvailabilityZonesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream(
                            "DescribeAvailabilityZonesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeAvailabilityZonesResponse> describeAvailabilityZonesAsync(
            final DescribeAvailabilityZonesRequest request) {
        Future<DescribeAvailabilityZonesResponse> response = asyncExecutor
                .submit(new Callable<DescribeAvailabilityZonesResponse>() {

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
     *          DescribeImageAttribute Action
     * @return
     *          DescribeImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImageAttributeResponse describeImageAttribute(DescribeImageAttributeRequest request)
            throws AmazonEC2Exception {
        DescribeImageAttributeResponse response;
        try {
            response = (DescribeImageAttributeResponse) getUnmarshaller()
                    .unmarshal(
                            new InputSource(this.getClass().getResourceAsStream(
                                    "DescribeImageAttributeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeImageAttributeResponse> describeImageAttributeAsync(
            final DescribeImageAttributeRequest request) {
        Future<DescribeImageAttributeResponse> response = asyncExecutor
                .submit(new Callable<DescribeImageAttributeResponse>() {

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
     *          DescribeImages Action
     * @return
     *          DescribeImages Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeImagesResponse describeImages(DescribeImagesRequest request) throws AmazonEC2Exception {
        DescribeImagesResponse response;
        try {
            response = (DescribeImagesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeImagesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeImagesResponse> describeImagesAsync(final DescribeImagesRequest request) {
        Future<DescribeImagesResponse> response = asyncExecutor
                .submit(new Callable<DescribeImagesResponse>() {

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
     *          DescribeInstances Action
     * @return
     *          DescribeInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeInstancesResponse describeInstances(DescribeInstancesRequest request)
            throws AmazonEC2Exception {
        DescribeInstancesResponse response;
        try {
            response = (DescribeInstancesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeInstancesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeInstancesResponse> describeInstancesAsync(final DescribeInstancesRequest request) {
        Future<DescribeInstancesResponse> response = asyncExecutor
                .submit(new Callable<DescribeInstancesResponse>() {

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
     *          DescribeKeyPairs Action
     * @return
     *          DescribeKeyPairs Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeKeyPairsResponse describeKeyPairs(DescribeKeyPairsRequest request)
            throws AmazonEC2Exception {
        DescribeKeyPairsResponse response;
        try {
            response = (DescribeKeyPairsResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeKeyPairsResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeKeyPairsResponse> describeKeyPairsAsync(final DescribeKeyPairsRequest request) {
        Future<DescribeKeyPairsResponse> response = asyncExecutor
                .submit(new Callable<DescribeKeyPairsResponse>() {

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
     *          DescribeSecurityGroups Action
     * @return
     *          DescribeSecurityGroups Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSecurityGroupsResponse describeSecurityGroups(DescribeSecurityGroupsRequest request)
            throws AmazonEC2Exception {
        DescribeSecurityGroupsResponse response;
        try {
            response = (DescribeSecurityGroupsResponse) getUnmarshaller()
                    .unmarshal(
                            new InputSource(this.getClass().getResourceAsStream(
                                    "DescribeSecurityGroupsResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeSecurityGroupsResponse> describeSecurityGroupsAsync(
            final DescribeSecurityGroupsRequest request) {
        Future<DescribeSecurityGroupsResponse> response = asyncExecutor
                .submit(new Callable<DescribeSecurityGroupsResponse>() {

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
     *          DescribeSnapshots Action
     * @return
     *          DescribeSnapshots Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeSnapshotsResponse describeSnapshots(DescribeSnapshotsRequest request)
            throws AmazonEC2Exception {
        DescribeSnapshotsResponse response;
        try {
            response = (DescribeSnapshotsResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeSnapshotsResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeSnapshotsResponse> describeSnapshotsAsync(final DescribeSnapshotsRequest request) {
        Future<DescribeSnapshotsResponse> response = asyncExecutor
                .submit(new Callable<DescribeSnapshotsResponse>() {

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
     *          DescribeVolumes Action
     * @return
     *          DescribeVolumes Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DescribeVolumesResponse describeVolumes(DescribeVolumesRequest request) throws AmazonEC2Exception {
        DescribeVolumesResponse response;
        try {
            response = (DescribeVolumesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DescribeVolumesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DescribeVolumesResponse> describeVolumesAsync(final DescribeVolumesRequest request) {
        Future<DescribeVolumesResponse> response = asyncExecutor
                .submit(new Callable<DescribeVolumesResponse>() {

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
     *          DetachVolume Action
     * @return
     *          DetachVolume Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DetachVolumeResponse detachVolume(DetachVolumeRequest request) throws AmazonEC2Exception {
        DetachVolumeResponse response;
        try {
            response = (DetachVolumeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DetachVolumeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          DisassociateAddress Action
     * @return
     *          DisassociateAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public DisassociateAddressResponse disassociateAddress(DisassociateAddressRequest request)
            throws AmazonEC2Exception {
        DisassociateAddressResponse response;
        try {
            response = (DisassociateAddressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("DisassociateAddressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<DisassociateAddressResponse> disassociateAddressAsync(
            final DisassociateAddressRequest request) {
        Future<DisassociateAddressResponse> response = asyncExecutor
                .submit(new Callable<DisassociateAddressResponse>() {

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
     *          GetConsoleOutput Action
     * @return
     *          GetConsoleOutput Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public GetConsoleOutputResponse getConsoleOutput(GetConsoleOutputRequest request)
            throws AmazonEC2Exception {
        GetConsoleOutputResponse response;
        try {
            response = (GetConsoleOutputResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("GetConsoleOutputResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<GetConsoleOutputResponse> getConsoleOutputAsync(final GetConsoleOutputRequest request) {
        Future<GetConsoleOutputResponse> response = asyncExecutor
                .submit(new Callable<GetConsoleOutputResponse>() {

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
     *          ModifyImageAttribute Action
     * @return
     *          ModifyImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ModifyImageAttributeResponse modifyImageAttribute(ModifyImageAttributeRequest request)
            throws AmazonEC2Exception {
        ModifyImageAttributeResponse response;
        try {
            response = (ModifyImageAttributeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("ModifyImageAttributeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<ModifyImageAttributeResponse> modifyImageAttributeAsync(
            final ModifyImageAttributeRequest request) {
        Future<ModifyImageAttributeResponse> response = asyncExecutor
                .submit(new Callable<ModifyImageAttributeResponse>() {

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
     *          RebootInstances Action
     * @return
     *          RebootInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RebootInstancesResponse rebootInstances(RebootInstancesRequest request) throws AmazonEC2Exception {
        RebootInstancesResponse response;
        try {
            response = (RebootInstancesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("RebootInstancesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<RebootInstancesResponse> rebootInstancesAsync(final RebootInstancesRequest request) {
        Future<RebootInstancesResponse> response = asyncExecutor
                .submit(new Callable<RebootInstancesResponse>() {

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
     *          RegisterImage Action
     * @return
     *          RegisterImage Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RegisterImageResponse registerImage(RegisterImageRequest request) throws AmazonEC2Exception {
        RegisterImageResponse response;
        try {
            response = (RegisterImageResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("RegisterImageResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          ReleaseAddress Action
     * @return
     *          ReleaseAddress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ReleaseAddressResponse releaseAddress(ReleaseAddressRequest request) throws AmazonEC2Exception {
        ReleaseAddressResponse response;
        try {
            response = (ReleaseAddressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("ReleaseAddressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<ReleaseAddressResponse> releaseAddressAsync(final ReleaseAddressRequest request) {
        Future<ReleaseAddressResponse> response = asyncExecutor
                .submit(new Callable<ReleaseAddressResponse>() {

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
     *          ResetImageAttribute Action
     * @return
     *          ResetImageAttribute Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public ResetImageAttributeResponse resetImageAttribute(ResetImageAttributeRequest request)
            throws AmazonEC2Exception {
        ResetImageAttributeResponse response;
        try {
            response = (ResetImageAttributeResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("ResetImageAttributeResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<ResetImageAttributeResponse> resetImageAttributeAsync(
            final ResetImageAttributeRequest request) {
        Future<ResetImageAttributeResponse> response = asyncExecutor
                .submit(new Callable<ResetImageAttributeResponse>() {

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
     *          RevokeSecurityGroupIngress Action
     * @return
     *          RevokeSecurityGroupIngress Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RevokeSecurityGroupIngressResponse revokeSecurityGroupIngress(
            RevokeSecurityGroupIngressRequest request) throws AmazonEC2Exception {
        RevokeSecurityGroupIngressResponse response;
        try {
            response = (RevokeSecurityGroupIngressResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream(
                            "RevokeSecurityGroupIngressResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<RevokeSecurityGroupIngressResponse> revokeSecurityGroupIngressAsync(
            final RevokeSecurityGroupIngressRequest request) {
        Future<RevokeSecurityGroupIngressResponse> response = asyncExecutor
                .submit(new Callable<RevokeSecurityGroupIngressResponse>() {

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
     *          RunInstances Action
     * @return
     *          RunInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public RunInstancesResponse runInstances(RunInstancesRequest request) throws AmazonEC2Exception {
        RunInstancesResponse response;
        try {
            response = (RunInstancesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("RunInstancesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

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
     *          TerminateInstances Action
     * @return
     *          TerminateInstances Response from the service
     *
     * @throws AmazonEC2Exception
     */
    public TerminateInstancesResponse terminateInstances(TerminateInstancesRequest request)
            throws AmazonEC2Exception {
        TerminateInstancesResponse response;
        try {
            response = (TerminateInstancesResponse) getUnmarshaller().unmarshal(
                    new InputSource(this.getClass().getResourceAsStream("TerminateInstancesResponse.xml")));

            log.debug("Response from Mock Service: " + response.toXML());

        } catch (JAXBException jbe) {
            throw new AmazonEC2Exception("Unable to process mock response", jbe);
        }
        return response;
    }

    public Future<TerminateInstancesResponse> terminateInstancesAsync(final TerminateInstancesRequest request) {
        Future<TerminateInstancesResponse> response = asyncExecutor
                .submit(new Callable<TerminateInstancesResponse>() {

                    public TerminateInstancesResponse call() throws AmazonEC2Exception {
                        return terminateInstances(request);
                    }
                });
        return response;
    }

    /**
     * Get unmarshaller for current thread
     */
    private Unmarshaller getUnmarshaller() {
        return unmarshaller.get();
    }
}