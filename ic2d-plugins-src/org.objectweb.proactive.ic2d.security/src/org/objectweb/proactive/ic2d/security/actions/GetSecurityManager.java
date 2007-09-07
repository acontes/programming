package org.objectweb.proactive.ic2d.security.actions;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.ReflectionException;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.objectweb.proactive.core.security.PolicyRule;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.crypto.Session;
import org.objectweb.proactive.core.security.securityentity.RuleEntity;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;
import org.objectweb.proactive.ic2d.security.core.KeystoreUtils;
import org.objectweb.proactive.ic2d.security.core.SimplePolicyRule;
import org.objectweb.proactive.ic2d.security.perspectives.SecurityPerspective;
import org.objectweb.proactive.ic2d.security.views.PolicyEditorView;

public class GetSecurityManager extends Action implements IActionExtPoint {
	public static final String GET_SECURITY_MANAGER = "Get Security Manager";

	private AbstractData object;

	public GetSecurityManager() {
		System.out.println("GetSecurityManager.GetSecurityManager()");

		setId(GET_SECURITY_MANAGER);
		setToolTipText("Export SM to Policy view");
		setText("Export SM to Policy view");
		setEnabled(false);
	}

	@Override
	public final void run() {
		System.out.println("GetSecurityManager.run()");

		ProActiveSecurityManager psm = null;
		try {
			psm = (ProActiveSecurityManager) this.object
					.invoke(
							"getSecurityManager",
							new Object[] { null },
							new String[] { "org.objectweb.proactive.core.security.securityentity.Entity" });
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (MBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		try {
			IWorkbench iworkbench = PlatformUI.getWorkbench();
			IWorkbenchPage page = iworkbench.showPerspective(
					SecurityPerspective.ID, iworkbench
							.getActiveWorkbenchWindow());
			IViewPart part = page.showView(PolicyEditorView.ID);
			
			PolicyEditorView pev = (PolicyEditorView) part;
			PolicyServer ps = psm.getPolicyServer();

			List<SimplePolicyRule> sprl = new ArrayList<SimplePolicyRule>();
			for (PolicyRule policy : ps.getPolicies()) {
				sprl.add(prToSpr(policy));
			}
			
			List<String> users = new ArrayList<String>();
			for (RuleEntity entity : ps.getAccessAuthorizations()) {
				users.add(entity.getName());
			}
			
			List<Session> sessions = new ArrayList<Session>();
			sessions.addAll(psm.getSessions().values());
			pev.update(KeystoreUtils.listKeystore(ps.getKeyStore()), sprl, ps
					.getApplicationName(), users, sessions);
		} catch (WorkbenchException e2) {
			e2.printStackTrace();
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

	private SimplePolicyRule prToSpr(PolicyRule policy) {
		SimplePolicyRule rule = new SimplePolicyRule();
		for (RuleEntity entity : policy.getEntitiesFrom()) {
			rule.addFrom(entity.getName());
		}
		for (RuleEntity entity : policy.getEntitiesTo()) {
			rule.addTo(entity.getName());
		}
		rule.setRepAuth(policy.getCommunicationReply().getAuthentication());
		rule.setRepConf(policy.getCommunicationReply().getConfidentiality());
		rule.setRepInt(policy.getCommunicationReply().getIntegrity());
		rule.setReply(policy.getCommunicationReply().isCommunicationAllowed());
		rule.setReqAuth(policy.getCommunicationRequest().getAuthentication());
		rule.setReqConf(policy.getCommunicationRequest().getConfidentiality());
		rule.setReqInt(policy.getCommunicationRequest().getIntegrity());
		rule.setRequest(policy.getCommunicationRequest()
				.isCommunicationAllowed());
		rule.setAoCreation(policy.isAocreation());
		rule.setMigration(policy.isMigration());

		return rule;
	}

	public void setAbstractDataObject(AbstractData ref) {
		System.out.println("GetSecurityManager.setAbstractDataObject()");
		this.object = ref;
		super.setEnabled((this.object instanceof ActiveObject)
				|| (this.object instanceof RuntimeObject));
	}

	public void setActiveSelect(AbstractData ref) {
		System.out.println("GetSecurityManager.setActiveSelect()");
		// TODO Auto-generated method stub

	}
}
