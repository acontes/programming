package org.objectweb.proactive.extra.security.xacml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.PDP;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.FilePolicyModule;
import com.sun.xacml.finder.impl.SelectorModule;

public class TestPDP {

	// this is the actual PDP object we'll use for evaluation
	private PDP pdp = null;

	/**
	 * Default constructor. This creates a <code>SimplePDP</code> with a
	 * <code>PDP</code> based on the configuration defined by the runtime
	 * property com.sun.xcaml.PDPConfigFile.
	 */
	public TestPDP() throws Exception {
		// load the configuration
		ConfigurationStore store = new ConfigurationStore();

		// use the default factories from the configuration
		store.useDefaultFactories();

		// get the PDP configuration's and setup the PDP
		pdp = new PDP(store.getDefaultPDPConfig());
	}

	/**
	 * Constructor that takes an array of filenames, each of which contains an
	 * XACML policy, and sets up a <code>PDP</code> with access to these
	 * policies only. The <code>PDP</code> is configured programatically to
	 * have only a few specific modules.
	 * 
	 * @param policyFiles
	 *            an arry of filenames that specify policies
	 */
	public TestPDP(String policyFile) throws Exception {
		// Create a PolicyFinderModule and initialize it...in this case,
		// we're using the sample FilePolicyModule that is pre-configured
		// with a set of policies from the filesystem
		FilePolicyModule filePolicyModule = new FilePolicyModule();
		filePolicyModule.addPolicy(policyFile);

		// next, setup the PolicyFinder that this PDP will use
		PolicyFinder policyFinder = new PolicyFinder();
		Set<PolicyFinderModule> policyModules = new HashSet<PolicyFinderModule>();
		policyModules.add(filePolicyModule);
		policyFinder.setModules(policyModules);

		// now setup attribute finder modules for the current date/time and
		// AttributeSelectors (selectors are optional, but this project does
		// support a basic implementation)
		CurrentEnvModule envAttributeModule = new CurrentEnvModule();
		SelectorModule selectorAttributeModule = new SelectorModule();

		// Setup the AttributeFinder just like we setup the PolicyFinder. Note
		// that unlike with the policy finder, the order matters here. See the
		// the javadocs for more details.
		AttributeFinder attributeFinder = new AttributeFinder();
		List<AttributeFinderModule> attributeModules = new ArrayList<AttributeFinderModule>();
		attributeModules.add(envAttributeModule);
		attributeModules.add(selectorAttributeModule);
		attributeFinder.setModules(attributeModules);

		// Try to load the time-in-range function, which is used by several
		// of the examples...see the documentation for this function to
		// understand why it's provided here instead of in the standard
		// code base.
		// FunctionFactoryProxy proxy =
		// StandardFunctionFactory.getNewFactoryProxy();
		// FunctionFactory factory = proxy.getConditionFactory();
		// factory.addFunction(new TimeInRangeFunction());
		// FunctionFactory.setDefaultFactory(proxy);

		// finally, initialize our pdp
		pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
	}

	/**
	 * Evaluates the given request and returns the Response that the PDP will
	 * hand back to the PEP.
	 * 
	 * @param requestFile
	 *            the name of a file that contains a Request
	 * 
	 * @return the result of the evaluation
	 * 
	 * @throws IOException
	 *             if there is a problem accessing the file
	 * @throws ParsingException
	 *             if the Request is invalid
	 */
	public ResponseCtx evaluate(RequestCtx request) throws ParsingException {
		// evaluate the request
		return pdp.evaluate(request);
	}
}
