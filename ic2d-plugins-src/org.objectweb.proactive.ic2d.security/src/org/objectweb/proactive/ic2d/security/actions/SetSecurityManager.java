package org.objectweb.proactive.ic2d.security.actions;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.objectweb.proactive.core.security.Communication;
import org.objectweb.proactive.core.security.PolicyRule;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.core.security.securityentity.CertificatedRuleEntity;
import org.objectweb.proactive.core.security.securityentity.RuleEntities;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;
import org.objectweb.proactive.ic2d.jmxmonitoring.perspective.MonitoringPerspective;
import org.objectweb.proactive.ic2d.jmxmonitoring.view.MonitoringView;
import org.objectweb.proactive.ic2d.security.core.KeystoreUtils;
import org.objectweb.proactive.ic2d.security.core.SimplePolicyRule;
import org.objectweb.proactive.ic2d.security.perspectives.SecurityPerspective;
import org.objectweb.proactive.ic2d.security.views.PolicyEditorView;

public class SetSecurityManager extends Action implements IActionExtPoint {
	public static final String SET_SECURITY_MANAGER = "Set Security Manager";

	private AbstractData object;

	public SetSecurityManager() {
		System.out.println("SetSecurityManager.SetSecurityManager()");

		setId(SET_SECURITY_MANAGER);
		setToolTipText("Import SM from Policy view");
		setText("Import SM from Policy view");
		setEnabled(false);
	}

	@Override
	public void run() {
		System.out.println("SetSecurityManager.run()");

		IWorkbench iworkbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = null;
		try {
			page = iworkbench.showPerspective(SecurityPerspective.ID,
					iworkbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		IViewPart part = null;
		try {
			part = page.showView(PolicyEditorView.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PolicyEditorView pev = (PolicyEditorView) part;
		
		try {
			iworkbench.showPerspective(MonitoringPerspective.ID,
					iworkbench.getActiveWorkbenchWindow()).showView(MonitoringView.ID);
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (WorkbenchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		KeyStore keystore = null;
		try {
			keystore = KeystoreUtils.createKeystore(pev.getKeystore(), pev
					.getKeysToKeep());
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<PolicyRule> policyRules = new ArrayList<PolicyRule>();
		for (SimplePolicyRule policy : pev.getRt().getRules()) {
			PolicyRule pr = new PolicyRule();
			RuleEntities entitiesFrom = new RuleEntities();
			for (String name : policy.getFrom()) {
				try {
					entitiesFrom.add(new CertificatedRuleEntity(
							SecurityConstants.typeToInt(name.substring(0, name
									.indexOf(':'))), keystore, name
									.substring(name.indexOf(':') + 1)));
				} catch (UnrecoverableKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pr.setEntitiesFrom(entitiesFrom);
			RuleEntities entitiesTo = new RuleEntities();
			for (String name : policy.getTo()) {
				try {
					entitiesTo.add(new CertificatedRuleEntity(SecurityConstants
							.typeToInt(name.substring(0, name.indexOf(':'))),
							keystore, name.substring(name.indexOf(':') + 1)));
				} catch (UnrecoverableKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pr.setEntitiesTo(entitiesTo);
			pr.setAocreation(policy.isAoCreation());
			pr.setMigration(policy.isMigration());
			pr.setCommunicationRulesReply(new Communication(policy.isReply(),
					policy.getRepAuth(), policy.getRepConf(), policy
							.getRepInt()));
			pr.setCommunicationRulesRequest(new Communication(policy
					.isRequest(), policy.getReqAuth(), policy.getReqConf(),
					policy.getReqInt()));

			policyRules.add(pr);
		}
		
		RuleEntities users = new RuleEntities();
		for (String user : pev.getRt().getAuthorizedUsers()) {
			try {
				users.add(new CertificatedRuleEntity(SecurityConstants
						.typeToInt(user.substring(0, user.indexOf(':'))),
						keystore, user.substring(user.indexOf(':') + 1)));
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		PolicyServer ps = new PolicyServer(keystore, policyRules);
		ps.setApplicationName(pev.getAppName());
		ps.setAccessAuthorization(users);

		try {
			this.object
					.invoke(
							"setSecurityManager",
							new Object[] { null, ps },
							new String[] {
									"org.objectweb.proactive.core.security.securityentity.Entity",
									"org.objectweb.proactive.core.security.PolicyServer" });
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAbstractDataObject(AbstractData ref) {
		System.out.println("SetSecurityManager.setAbstractDataObject()");

		this.object = ref;
		super.setEnabled((this.object instanceof ActiveObject)
				|| (this.object instanceof RuntimeObject));
	}

	public void setActiveSelect(AbstractData ref) {
		System.out.println("SetSecurityManager.setActiveSelect()");
		// TODO Auto-generated method stub
	}

}
