package org.objectweb.proactive.ic2d.security.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.NotFoundException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.objectweb.proactive.core.security.SecurityConstants;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public abstract class PolicyTools {

	/**
	 * Write policy file
	 * 
	 * @param filePath
	 * @param applicationName
	 * @param keystorePath
	 * @param rules
	 * @throws FileNotFoundException
	 */

	public static void writePolicyFile(String filePath, PolicyFile policy)
			throws FileNotFoundException {
		StreamResult sr = new StreamResult(new File(filePath));

		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();

		// SAX2.0 ContentHandler.
		TransformerHandler th;
		try {
			th = tf.newTransformerHandler();

			Transformer serializer = th.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			th.setResult(sr);
			try {
				th.startDocument();

				policyTag(th, policy.getApplicationName(), policy.getKeystorePath(), policy.getRules(),
						policy.getAuthorizedUsers());

				th.endDocument();
			} catch (SAXException e) {
				e.printStackTrace();
			}
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		}
	}

	private static void policyTag(TransformerHandler th,
			String applicationName, String keystorePath,
			List<SimplePolicyRule> rules, List<TypedCertificate> authorizedUsers)
			throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "xmlns", "CDATA",
				"urn:proactive:security:1.0");
		atts.addAttribute("", "", "schemaVersion", "CDATA", "1.0");
		atts.addAttribute("", "", "xmlns:xsi", "CDATA",
				"http://www.w3.org/2001/XMLSchema-instance");
		atts
				.addAttribute(
						"",
						"",
						"xsi:schemaLocation",
						"CDATA",
						"urn:proactive:security:1.0 /user/nhouillo/home/ws/ProActive/src/Core/org/objectweb/proactive/core/descriptor/xml/schemas/security/1.0/security.xsd");

		th.startElement("", "", "Policy", atts);

		applicationNameTag(th, applicationName);
		keystoreTag(th, keystorePath);
		rulesTag(th, rules);
		usersTag(th, authorizedUsers);

		th.endElement("", "", "Policy");
	}

	private static void applicationNameTag(TransformerHandler th, String name)
			throws SAXException {
		th.startElement("", "", "ApplicationName", new AttributesImpl());

		th.characters(name.toCharArray(), 0, name.length());

		th.endElement("", "", "ApplicationName");
	}

	private static void keystoreTag(TransformerHandler th, String path)
			throws SAXException {
		th.startElement("", "", "PKCS12KeyStore", new AttributesImpl());

		th.characters(path.toCharArray(), 0, path.length());

		th.endElement("", "", "PKCS12KeyStore");
	}

	private static void rulesTag(TransformerHandler th,
			List<SimplePolicyRule> rules) throws SAXException {
		th.startElement("", "", "Rules", new AttributesImpl());

		for (SimplePolicyRule rule : rules) {
			ruleTag(th, rule);
		}

		th.endElement("", "", "Rules");
	}

	private static void ruleTag(TransformerHandler th, SimplePolicyRule rule)
			throws SAXException {
		th.startElement("", "", "Rule", new AttributesImpl());

		entityListTag("From", th, rule.getFrom());
		entityListTag("To", th, rule.getTo());
		communicationTag(th, rule);
		rightsTag("OACreation", th, rule.isAoCreation());
		rightsTag("Migration", th, rule.isMigration());

		th.endElement("", "", "Rule");
	}

	private static void entityListTag(String tag, TransformerHandler th,
			List<TypedCertificate> tcl) throws SAXException {
		th.startElement("", "", tag, new AttributesImpl());

		for (TypedCertificate cert : tcl) {
			entityTag(th, cert);
		}

		th.endElement("", "", tag);
	}

	private static void entityTag(TransformerHandler th, TypedCertificate cert)
			throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "type", "CDATA", SecurityConstants
				.typeToString(cert.getType()));
		atts.addAttribute("", "", "name", "CDATA", cert.getCert()
				.getSubjectX500Principal().getName());

		th.startElement("", "", "Entity", atts);
		th.endElement("", "", "Entity");
	}

	private static void communicationTag(TransformerHandler th,
			SimplePolicyRule rule) throws SAXException {
		th.startElement("", "", "Communication", new AttributesImpl());

		authorizationTag("Request", th, rule.isRequest(), rule.getReqAuth(),
				rule.getReqConf(), rule.getReqInt());
		authorizationTag("Reply", th, rule.isReply(), rule.getRepAuth(), rule
				.getRepConf(), rule.getRepInt());

		th.endElement("", "", "Communication");
	}

	private static void authorizationTag(String tag, TransformerHandler th,
			boolean permission, int auth, int conf, int integ)
			throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "value", "CDATA", permission ? "authorized"
				: "denied");

		th.startElement("", "", tag, atts);

		attributesTag(th, auth, conf, integ);

		th.endElement("", "", tag);
	}

	private static void attributesTag(TransformerHandler th, int auth,
			int conf, int integ) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		atts.addAttribute("", "", "authentication", "CDATA", RuleConstants
				.valToString(auth));
		atts.addAttribute("", "", "confidentiality", "CDATA", RuleConstants
				.valToString(conf));
		atts.addAttribute("", "", "integrity", "CDATA", RuleConstants
				.valToString(integ));

		th.startElement("", "", "Attributes", atts);
		th.endElement("", "", "Attributes");
	}

	private static void rightsTag(String tag, TransformerHandler th,
			boolean permission) throws SAXException {
		String value = permission ? "authorized" : "denied";

		th.startElement("", "", tag, new AttributesImpl());

		th.characters(value.toCharArray(), 0, value.length());

		th.endElement("", "", tag);
	}

	private static void usersTag(TransformerHandler th,
			List<TypedCertificate> authorizedUsers) throws SAXException {
		th.startElement("", "", "AccessRights", new AttributesImpl());

		for (TypedCertificate cert : authorizedUsers) {
			entityTag(th, cert);
		}

		th.endElement("", "", "AccessRights");
	}

	/**
	 * Read policy file
	 * 
	 * @param path
	 * @return
	 */
	public static PolicyFile readPolicyFile(String path,
			CertificateTreeList ctl) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		Document document = null;
		try {
			document = db.parse(path);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return getPolicies(document, ctl);
	}

	private static PolicyFile getPolicies(Document doc,
			CertificateTreeList ctl) {
		List<SimplePolicyRule> rulesList = new ArrayList<SimplePolicyRule>();
		List<TypedCertificate> users = new ArrayList<TypedCertificate>();
		String appName = new String();
		String keystore = new String();
		for (Node policy : getChildrenNamed(doc, "Policy")) {
			for (Node appNameNode : getChildrenNamed(policy, "ApplicationName")) {
				appName += appNameNode.getFirstChild().getNodeValue();
			}
			for (Node keystoreNode : getChildrenNamed(policy, "PKCS12KeyStore")) {
				keystore += keystoreNode.getFirstChild().getNodeValue();
			}
			for (Node rules : getChildrenNamed(policy, "Rules")) {
				for (Node rule : getChildrenNamed(rules, "Rule")) {
					try {
						rulesList.add(getRule(rule, ctl));
					} catch (NotFoundException e) {
						// a rule has invalid entities in From or To, it is
						// ignored
						e.printStackTrace();
					}

				}
			}
			for (Node usersNode : getChildrenNamed(policy, "AccessRights")) {
				for (Node entity : getChildrenNamed(usersNode, "Entity")) {
					int type = SecurityConstants.typeToInt(entity
							.getAttributes().getNamedItem("type")
							.getNodeValue());
					String name = entity.getAttributes().getNamedItem("name")
							.getNodeValue();
					try {
						users.add(ctl.search(name, type));
					} catch (NotFoundException e) {
						// someone will not get his access rights, too bad
						e.printStackTrace();
					}
				}
			}
		}

		return new PolicyFile(appName, keystore, rulesList, users);
	}

	private static SimplePolicyRule getRule(Node ruleNode,
			CertificateTreeList ctl) throws NotFoundException {
		SimplePolicyRule rule = new SimplePolicyRule();
		for (Node node : getChildrenNamed(ruleNode, "From")) {
			setFrom(rule, node, ctl);
		}
		for (Node node : getChildrenNamed(ruleNode, "To")) {
			setTo(rule, node, ctl);
		}
		for (Node node : getChildrenNamed(ruleNode, "Communication")) {
			setCommunication(rule, node);
		}
		for (Node node : getChildrenNamed(ruleNode, "OACreation")) {
			setAoCreation(rule, node);
		}
		for (Node node : getChildrenNamed(ruleNode, "Migration")) {
			setMigration(rule, node);
		}

		return rule;
	}

	private static void setFrom(SimplePolicyRule rule, Node fromNode,
			CertificateTreeList ctl) throws NotFoundException {
		for (Node node : getChildrenNamed(fromNode, "Entity")) {
			int type = SecurityConstants.typeToInt(node.getAttributes()
					.getNamedItem("type").getNodeValue());
			String name = node.getAttributes().getNamedItem("name")
					.getNodeValue();
			rule.addFrom(ctl.search(name, type));
		}
	}

	private static void setTo(SimplePolicyRule rule, Node toNode,
			CertificateTreeList ctl) throws NotFoundException {
		for (Node node : getChildrenNamed(toNode, "Entity")) {
			int type = SecurityConstants.typeToInt(node.getAttributes()
					.getNamedItem("type").getNodeValue());
			String name = node.getAttributes().getNamedItem("name")
					.getNodeValue();
			rule.addTo(ctl.search(name, type));
		}
	}

	private static void setCommunication(SimplePolicyRule rule, Node comNode) {
		for (Node node : getChildrenNamed(comNode, "Request")) {
			setRequest(rule, node);
		}
		for (Node node : getChildrenNamed(comNode, "Reply")) {
			setReply(rule, node);
		}
	}

	private static void setRequest(SimplePolicyRule rule, Node reqNode) {
		String permission = reqNode.getAttributes().getNamedItem("value")
				.getNodeValue();
		rule.setRequest(permission.equals("authorized"));
		for (Node node : getChildrenNamed(reqNode, "Attributes")) {
			String auth = node.getAttributes().getNamedItem("authentication")
					.getNodeValue();
			rule.setReqAuth(RuleConstants.valToInt(auth));

			String conf = node.getAttributes().getNamedItem("confidentiality")
					.getNodeValue();
			rule.setReqConf(RuleConstants.valToInt(conf));

			String integ = node.getAttributes().getNamedItem("integrity")
					.getNodeValue();
			rule.setReqInt(RuleConstants.valToInt(integ));
		}
	}

	private static void setReply(SimplePolicyRule rule, Node repNode) {
		String permission = repNode.getAttributes().getNamedItem("value")
				.getNodeValue();
		rule.setReply(permission.equals("authorized"));
		for (Node node : getChildrenNamed(repNode, "Attributes")) {
			String auth = node.getAttributes().getNamedItem("authentication")
					.getNodeValue();
			rule.setRepAuth(RuleConstants.valToInt(auth));

			String conf = node.getAttributes().getNamedItem("confidentiality")
					.getNodeValue();
			rule.setRepConf(RuleConstants.valToInt(conf));

			String integ = node.getAttributes().getNamedItem("integrity")
					.getNodeValue();
			rule.setRepInt(RuleConstants.valToInt(integ));
		}
	}

	private static void setAoCreation(SimplePolicyRule rule, Node aoNode) {
		Node node = aoNode.getChildNodes().item(0);
		rule.setAoCreation(node.getNodeValue().equals("authorized"));
	}

	private static void setMigration(SimplePolicyRule rule, Node migNode) {
		Node node = migNode.getChildNodes().item(0);
		rule.setMigration(node.getNodeValue().equals("authorized"));
	}

	private static List<Node> getChildrenNamed(Node parent, String name) {
		List<Node> list = new ArrayList<Node>();
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			Node child = parent.getChildNodes().item(i);
			if (child.getNodeName().equals(name)) {
				list.add(child);
			}
		}
		return list;
	}
}
