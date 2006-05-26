package nonregressiontest.component.collectiveitf.multicast;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.MulticastBindingController;

import com.sun.mail.handlers.multipart_mixed;

import testsuite.test.Assertions;

public class TesterImpl implements Tester, BindingController,
		MulticastBindingController {

	MulticastTestItf clientItf;

	MulticastTestItf multicastClientItf = null;

	public void testConnectedServerMulticastItf() {

		List<WrappedInteger> listParameter = new ArrayList<WrappedInteger>();
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			listParameter.add(i, new WrappedInteger(i));
		}
		List<WrappedInteger> result;
		result = clientItf.testBroadcast_Param(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.contains(new WrappedInteger(i)));
		}

		result = clientItf.testBroadcast_Method(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.contains(new WrappedInteger(i)));
		}

		result = clientItf.testOneToOne_Param(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.get(i).equals(new WrappedInteger(i)));
		}

		result = clientItf.testOneToOne_Method(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.get(i).equals(new WrappedInteger(i)));
		}

		List<WrappedInteger> listForRoundRobin = new ArrayList<WrappedInteger>();
		for (int i = 0; i < Test.NB_CONNECTED_ITFS + 1; i++) {
			listForRoundRobin.add(i, new WrappedInteger(i));
		}
		result = clientItf.testRoundRobin_Param(listForRoundRobin);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS + 1);

		result = clientItf.testRoundRobin_Method(listForRoundRobin);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS + 1);

		result = clientItf.testAllStdModes_Param(listParameter, listParameter,
				listParameter, listParameter, new WrappedInteger(42));
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);

		result = clientItf.testCustom_Param(listForRoundRobin);
		Assertions.assertTrue(result.size() == 1);
		Assertions.assertTrue(result.get(0).equals(listForRoundRobin.get(0)));

		result = clientItf.testCustom_Method(listForRoundRobin);
		Assertions.assertTrue(result.size() == 1);
		Assertions.assertTrue(result.get(0).equals(listForRoundRobin.get(0)));

	}

	public void testOwnClientMulticastItf() {
		List<WrappedInteger> listParameter = new ArrayList<WrappedInteger>();
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			listParameter.add(i, new WrappedInteger(i));
		}
		List<WrappedInteger> result;
		result = multicastClientItf.testBroadcast_Param(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.contains(new WrappedInteger(i))); // do
																			// not
																			// know
																			// the
																			// ordering
																			// ...
																			// ?
		}

		result = multicastClientItf.testBroadcast_Method(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.contains(new WrappedInteger(i))); // do
																			// not
																			// know
																			// the
																			// ordering
																			// ...
																			// ?
		}

		result = multicastClientItf.testOneToOne_Param(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.contains(new WrappedInteger(i))); // do
																			// not
																			// know
																			// the
																			// ordering
																			// ...
																			// ?
		}

		result = multicastClientItf.testOneToOne_Method(listParameter);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);
		for (int i = 0; i < Test.NB_CONNECTED_ITFS; i++) {
			Assertions.assertTrue(result.get(i).equals(new WrappedInteger(i))); // do
																				// not
																				// know
																				// the
																				// ordering
																				// ...
																				// ?
		}

		List<WrappedInteger> listForRoundRobin = new ArrayList<WrappedInteger>();
		for (int i = 0; i < Test.NB_CONNECTED_ITFS + 1; i++) {
			listForRoundRobin.add(i, new WrappedInteger(i));
		}
		result = multicastClientItf.testRoundRobin_Param(listForRoundRobin);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS + 1);

		result = multicastClientItf.testRoundRobin_Method(listForRoundRobin);
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS + 1);

		result = multicastClientItf.testAllStdModes_Param(listParameter,
				listParameter, listParameter, listParameter,
				new WrappedInteger(42));
		Assertions.assertTrue(result.size() == Test.NB_CONNECTED_ITFS);

		result = multicastClientItf.testCustom_Param(listForRoundRobin);
		Assertions.assertTrue(result.size() == 1);
		Assertions.assertTrue(result.get(0).equals(listForRoundRobin.get(0)));

		result = multicastClientItf.testCustom_Method(listForRoundRobin);
		Assertions.assertTrue(result.size() == 1);
		Assertions.assertTrue(result.get(0).equals(listForRoundRobin.get(0)));

	}


	/*
	 * @see org.objectweb.fractal.api.control.BindingController#bindFc(java.lang.String,
	 *      java.lang.Object)
	 */
	public void bindFc(String clientItfName, Object serverItf)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {

		if (clientItfName.equals("clientItf")) {
				clientItf = (MulticastTestItf) serverItf;
		}
		else if ("multicastClientItf".equals(clientItfName)) {
			multicastClientItf = (MulticastTestItf)serverItf;
		}
		
		else {
			throw new NoSuchInterfaceException(clientItfName);
		}
	}

	/*
	 * @see org.objectweb.fractal.api.control.BindingController#listFc()
	 */
	public String[] listFc() {

		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @see org.objectweb.fractal.api.control.BindingController#lookupFc(java.lang.String)
	 */
	public Object lookupFc(String clientItfName)
			throws NoSuchInterfaceException {

		if ("clientItf".equals(clientItfName)) {
			return clientItf;
		}
		if ("multicastClientItf".equals(clientItfName)) {
			return multicastClientItf;
		}
		throw new NoSuchInterfaceException(clientItfName);
		
	}

	/*
	 * @see org.objectweb.fractal.api.control.BindingController#unbindFc(java.lang.String)
	 */
	public void unbindFc(String clientItfName) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {

		// TODO Auto-generated method stub

	}

	/*
	 * @see org.objectweb.proactive.core.component.controller.MulticastBindingController#getMulticastFcItfRef(java.lang.String)
	 */
	public Object getMulticastFcItfRef(String itfName)
			throws NoSuchInterfaceException {

		return multicastClientItf;
	}

	/*
	 * @see org.objectweb.proactive.core.component.controller.MulticastBindingController#setMulticastFcItfRef(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setMulticastFcItfRef(String itfName, Object itfRef) {

		if ("multicastClientItf".equals(itfName)
				&& (itfRef instanceof MulticastTestItf)) {
			multicastClientItf = (MulticastTestItf) itfRef;
		} else {
			throw new ProActiveRuntimeException(
					"cannot find multicast interface " + itfName);
		}
	}

}
