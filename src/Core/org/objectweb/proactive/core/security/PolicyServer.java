/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;
import org.objectweb.proactive.core.security.securityentity.Entities;
import org.objectweb.proactive.core.security.securityentity.Entity;
import org.objectweb.proactive.core.security.securityentity.RuleEntities;
import org.objectweb.proactive.core.security.securityentity.RuleEntity;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * The PolicyServer class contains entity's policy rules and application's
 * certificate and private key
 *
 */
public class PolicyServer implements Serializable, Cloneable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 6881821067929081660L;
	static Logger log = ProActiveLogger.getLogger(Loggers.SECURITY_POLICYSERVER);
//    private static int REQUIRED = 1;
//    private static int DENIED = -1;
//    private static int OPTIONAL = 0;
    protected List<PolicyRule> policyRules;
    protected RuleEntities accessAuthorizations;
    protected String policyRulesFileLocation;
    protected String applicationName;
    protected SerializableKeyStore keyStore;

    public PolicyServer() {
        ProActiveSecurity.loadProvider();
        this.policyRules = new ArrayList<PolicyRule>();
        this.accessAuthorizations = new RuleEntities();
        this.policyRulesFileLocation = new String("Undefined");
        this.applicationName = new String();
        this.keyStore = new SerializableKeyStore(null);
    }

    public PolicyServer(PolicyRule[] policyRules) {
    	this();
        this.policyRules = new ArrayList<PolicyRule>();
        for (int i = 0; i < policyRules.length; i++) {
            this.policyRules.add(policyRules[i]);
        }
    }

    public PolicyServer(ArrayList<PolicyRule> policyRules) {
    	this();
        this.policyRules = policyRules;
    }

    public PolicyServer(KeyStore keyStore, List<PolicyRule> policyRules) {
    	this();
        this.policyRules = policyRules;
        this.keyStore = new SerializableKeyStore(keyStore);
    }

//    private int convert(String name) {
//        if (name.equals("required") || name.equals("allowed") ||
//                name.equals("authorized")) {
//            return REQUIRED;
//        } else if (name.equals("denied")) {
//            return DENIED;
//        } else {
//            return OPTIONAL;
//        }
//    }

    public SecurityContext getPolicy(SecurityContext securityContext)
        throws SecurityNotAvailableException {
        Entities entitiesFrom = securityContext.getEntitiesFrom();
        Entities entitiesTo = securityContext.getEntitiesTo();

        if (this.policyRules == null) {
            ProActiveLogger.getLogger(Loggers.SECURITY_POLICY)
                           .debug("trying to find a policy whereas none has been set" +
                this + "    " + this.policyRules);
            throw new SecurityNotAvailableException();
        }

        if (ProActiveLogger.getLogger(Loggers.SECURITY_POLICYSERVER)
                               .isDebugEnabled()) {
            String s = "================================\nFrom : " +
                entitiesFrom.toString();
            s += ("\nTo : " + entitiesTo.toString());
            ProActiveLogger.getLogger(Loggers.SECURITY_POLICYSERVER)
                           .debug(s + "\n=================================\n");
        }

        // getting all rules matching the context
        List<PolicyRule> matchingRules = new ArrayList<PolicyRule>();
        for (PolicyRule policy : this.policyRules) {
            // testing if <From> tag matches <From> entities
            RuleEntities policyEntitiesFrom = policy.getEntitiesFrom();

            int matchingFrom = policyEntitiesFrom.match(entitiesFrom);

            // testing if <To> tag matches <To> entities
            RuleEntities policyEntitiesTo = policy.getEntitiesTo();

            int matchingTo = policyEntitiesTo.match(entitiesTo);

            //
            if ((matchingFrom != RuleEntity.MATCH_FAILED) &&
                    (matchingTo != RuleEntity.MATCH_FAILED)) {
                matchingRules.add(policy);
            }
        }

        // getting the most specific rule(s)
        List<PolicyRule> applicableRules = new ArrayList<PolicyRule>();
        for (PolicyRule matchingPolicy : matchingRules) {
            if (applicableRules.isEmpty()) {
                applicableRules.add(matchingPolicy);
            } else {
                boolean add = false;

                // level represents the specificity of the target entities of a
                // rule, higher level is more specific
                int fromLevel = matchingPolicy.getEntitiesFrom().getLevel();
                int toLevel = matchingPolicy.getEntitiesTo().getLevel();
                for (Iterator<PolicyRule> applicableRulesIterator = applicableRules.iterator();
                        applicableRulesIterator.hasNext();) {
                    PolicyRule applicableRule = applicableRulesIterator.next();
                    int applicableFromLevel = applicableRule.getEntitiesFrom()
                                                            .getLevel();
                    int applicableToLevel = applicableRule.getEntitiesTo()
                                                          .getLevel();

                    if ((fromLevel >= applicableFromLevel) &&
                            (toLevel >= applicableToLevel)) {
                        // current rule is more specific than the current applicableRule
                        applicableRulesIterator.remove();
                        add = true;
                    } else if ((fromLevel > applicableFromLevel) ||
                            (toLevel > applicableToLevel)) {
                        // current rule and current applicableRule both have to be applied
                        add = true;
                    }
                }
                if (add) {
                    applicableRules.add(matchingPolicy);
                }
            }
        }

        // resolving the applicable rules
        PolicyRule matchingPolicy;
        if (applicableRules.isEmpty()) {
            // defaul policy is not defined, we create one that forbids everything
            matchingPolicy = new PolicyRule();
        } else {
            matchingPolicy = PolicyRule.mergePolicies(applicableRules);
        }

        ProActiveLogger.getLogger(Loggers.SECURITY_POLICY)
                       .debug("Found Policy : " + matchingPolicy);

        Communication communication;

        // TODOSECURITY split receive of a request or a reply
        if ((securityContext.getType() == SecurityContext.COMMUNICATION_RECEIVE_REQUEST_FROM) ||
                (securityContext.getType() == SecurityContext.COMMUNICATION_RECEIVE_REPLY_FROM)) {
            communication = matchingPolicy.getCommunicationReply();
            //			communication.setCommunication(1);
            securityContext.setReceiveReply(communication);
            securityContext.setReceiveRequest(communication);
        } else {
            communication = matchingPolicy.getCommunicationRequest();
            ProActiveLogger.getLogger(Loggers.SECURITY_POLICY)
                           .debug("communication is " + communication);
            //			communication.setCommunication(1);
            securityContext.setSendReply(communication);
            securityContext.setSendRequest(communication);
        }

        if (securityContext.getType() == SecurityContext.MIGRATION_TO) {
            securityContext.setMigration(matchingPolicy.isMigration());
        }

        return securityContext;
    }

//    @Deprecated
//    public Communication getPolicyTo(String type, String virtualNodeFrom,
//        String virtualNodeTo) throws SecurityNotAvailableException {
//        // if (p == null) {
//        // logger.debug("SEcurityNamfndjdhuidss crac r cd boium");
//        // throw new SecurityNotAvailableException();
//        // }
//        if (true) {
//            throw new RuntimeException("DEPRECATED METHOD : UPDATE !!!");
//        }
//        return null;
//    }
    
    public List<PolicyRule> getPolicies() {
    	return this.policyRules;
    }

//    public int[] computePolicy(int[] from, int[] to)
//        throws ComputePolicyException {
//        // logger.info("calculating composed policy");
//        if (((from[0] == REQUIRED) && (to[0] == DENIED)) ||
//                ((from[1] == REQUIRED) && (to[1] == DENIED)) ||
//                ((from[2] == REQUIRED) && (to[2] == DENIED)) ||
//                ((from[0] == DENIED) && (to[0] == REQUIRED)) ||
//                ((from[1] == DENIED) && (to[1] == REQUIRED)) ||
//                ((from[2] == DENIED) && (to[2] == REQUIRED))) {
//            throw new ComputePolicyException("incompatible policies");
//        }
//
//        return new int[] { from[0] + to[0], from[1] + to[1], from[2] + to[2] };
//    }

    public boolean CanSendRequestTo(X509Certificate distantOA) {
        return false;
    }

    public boolean CanReceiveRequestFrom(X509Certificate distantOA) {
        return false;
    }

    public boolean CanSendReplyTo(X509Certificate distantOA) {
        return false;
    }

    public boolean CanReceiveReplyFrom(X509Certificate distantOA) {
        return false;
    }

    public boolean CanMigrateTo(X509Certificate distantOA) {
        return false;
    }

//    public boolean canMigrateTo(String type, String from, String to) {
//        try {
//            System.out.println("Migration from " + from + "to" + to);
//            ArrayList<Entity> arrayFrom = new ArrayList<Entity>();
//            ArrayList<Entity> arrayTo = new ArrayList<Entity>();
//
//            SecurityContext sc = new SecurityContext(SecurityContext.MIGRATION_TO,
//                    arrayFrom, arrayTo);
//            return getPolicy(sc).isMigration();
//        } catch (SecurityNotAvailableException e) {
//            // no security all is permitted
//            return true;
//        }
//    }
    
    public void setAccessAuthorization(RuleEntities entities) {
    	this.accessAuthorizations = entities;
    }
    
    protected boolean hasAccessRights(Entity user) {
    	if (user == null || this.accessAuthorizations == null) {
    		return false;
    	}
    	
    	return this.accessAuthorizations.contains(user);
    }
    
    public RuleEntities getAccessAuthorizations() {
    	return this.accessAuthorizations;
    }

    @Override
    public String toString() {
        String s = null;
        s = "ApplicationName : " + this.applicationName + "\nfile: " +
            this.policyRulesFileLocation + "\n";
        for (int i = 0; i < this.policyRules.size(); i++) {
            s += this.policyRules.get(i);
        }

        return s;
    }

//    // implements Serializable
//    private void writeObject(java.io.ObjectOutputStream out)
//        throws IOException {
//        if (this.keyStore != null) {
//            try {
//                ByteArrayOutputStream bout = new ByteArrayOutputStream();
//
//                keyStore.store(bout, "ha".toCharArray());
//                encodedKeyStore = bout.toByteArray();
//                keyStore = null;
//                bout.close();
//            } catch (CertificateEncodingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (KeyStoreException e) {
//                // TODO SECURITYSECURITY Auto-generated catch block
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                // TODO SECURITYSECURITY Auto-generated catch block
//                e.printStackTrace();
//            } catch (CertificateException e) {
//                // TODO SECURITYSECURITY Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        out.defaultWriteObject();
//    }
//
//    private void readObject(java.io.ObjectInputStream in)
//        throws IOException, ClassNotFoundException {
//        in.defaultReadObject();
//        if (this.encodedKeyStore != null) {
//            try {
//                this.keyStore = KeyStore.getInstance("PKCS12", "BC");
//                this.keyStore.load(new ByteArrayInputStream(
//                        this.encodedKeyStore), "ha".toCharArray());
//                this.encodedKeyStore = null;
//            } catch (KeyStoreException e) {
//                e.printStackTrace();
//            } catch (NoSuchProviderException e) {
//                e.printStackTrace();
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (CertificateException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * @param policies
     */
    public void setPolicies(List<PolicyRule> policies) {
        ProActiveLogger.getLogger(Loggers.SECURITY_POLICY)
                       .info("storing policies");
        this.policyRules = policies;
    }

    /**
     * @param uri
     */
    public void setPolicyRulesFileLocation(String uri) {
        // for debug only
        // set security file path
        this.policyRulesFileLocation = uri;
    }

    /**
     * @return application certificate
     */
    public TypedCertificate getApplicationCertificate() {
		if (this.keyStore != null) {
			try {
				return KeyStoreTools.getApplicationCertificate(this.keyStore.getKeyStore());
			} catch (KeyStoreException e) {
				e.printStackTrace();
				PolicyServer.log
						.error("Application certificate cannot be found in keystore.");
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

    /**
     * @return application certificate chain
     */
    public TypedCertificateList getApplicationCertificateChain() {
        if (this.keyStore != null) {
            try {
				return KeyStoreTools.getApplicationCertificateChain(this.keyStore.getKeyStore());
			} catch (KeyStoreException e) {
				e.printStackTrace();
				PolicyServer.log.error("Application certificate chain not found in keystore.");
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
    }

    /**
     * @return application named appName certificate
     */
    public TypedCertificate getApplicationCertificate(String appName) {
        if ((this.keyStore != null) && (appName != null)) {
            try {
                return KeyStoreTools.getCertificate(this.keyStore.getKeyStore(), SecurityConstants.ENTITY_TYPE_APPLICATION, appName);
            } catch (KeyStoreException e) {
                e.printStackTrace();
                PolicyServer.log.error("Application : " + appName +
                    " certificate not found in keystore.");
            } catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
    }

    /**
	 * Set application name
	 * 
	 * @param applicationName
	 */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    @Override
    public Object clone() {
        PolicyServer clone = null;

        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);

            out.writeObject(this);
            out.flush();
            out.close();

            bout.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                        bout.toByteArray()));

            clone = (PolicyServer) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clone;
    }

    public KeyStore getKeyStore() {
        return this.keyStore.getKeyStore();
    }

    // public void setKeyStore(KeyStore keyStore) {
    // this.keyStore = keyStore;
    // }
    public void setPKCS12Keystore(String pkcs12Keystore) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
            keyStore.load(new FileInputStream(pkcs12Keystore),
                "ha".toCharArray());
            this.keyStore = new SerializableKeyStore(keyStore);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
