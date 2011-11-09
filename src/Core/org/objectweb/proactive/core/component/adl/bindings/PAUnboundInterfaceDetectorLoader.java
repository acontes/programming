package org.objectweb.proactive.core.component.adl.bindings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Node;
import org.objectweb.fractal.adl.bindings.Binding;
import org.objectweb.fractal.adl.bindings.BindingContainer;
import org.objectweb.fractal.adl.bindings.BindingErrors;
import org.objectweb.fractal.adl.bindings.UnboundInterfaceDetectorLoader;
import org.objectweb.fractal.adl.components.Component;
import org.objectweb.fractal.adl.components.ComponentContainer;
import org.objectweb.fractal.adl.implementations.Controller;
import org.objectweb.fractal.adl.implementations.ControllerContainer;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

/**
 * The {@link PAUnboundInterfaceDetectorLoader} extends the {@link UnboundInterfaceDetectorLoader}
 * to check also bindings defined inside &lt;controller&gt; nodes.
 * 
 * @author cruz
 *
 */
public class PAUnboundInterfaceDetectorLoader extends UnboundInterfaceDetectorLoader {

	public static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_ADL);

	@Override
	public Definition load(final String name, final Map<Object, Object> context)
			throws ADLException {
		// Load the definition with the next loader.
		final Definition d = clientLoader.load(name, context);

		// Check the loaded definition.
		checkNode(d);

		return d;
	}

	/**
	 * Initiates the checking of the tree starting with the &lt;definition&gt; node,
	 * and collecting the list of unbound interfaces for each component.
	 * 
	 * Each unbound interface found is analyzed, and those which are "mandatory"
	 * generate an exception. 
	 * 
	 * @param node
	 * @throws ADLException
	 */
	
	protected void checkNode(final Object node) throws ADLException {


		// Map of unbound interfaces on each component
		Map<ComponentContainer, Set<Interface>> unboundInterfaces = new HashMap<ComponentContainer, Set<Interface>>();

		// Start checking from the root component
		if(node instanceof ComponentContainer) {
			checkNode((ComponentContainer) node, unboundInterfaces);
		}
		// FIXME In composites, if only the internal part of a mandatory interface is bound,
		// then it is removed from the unboundInterfaces and does not check if the external part is bound
		// (and viceversa)
		// However, it seems that this problem is also unchecked when starting the lifecycle (through the API)

		// Iterates in the resulting Map looking for unbound (client and mandatory) interfaces
		for(final ComponentContainer comp : unboundInterfaces.keySet()) {
			
			String componentName = null;
			if(comp instanceof Component) componentName = ((Component)comp).getName();
			else if(comp instanceof Definition) componentName = ((Definition)comp).getName();
			else componentName = comp.toString();

			Set<Interface> unbound = unboundInterfaces.get(comp);
			if(unbound != null) {
				for(Interface itf : unbound ) {
					// if the interface is optional, ignore it
					if(itf instanceof TypeInterface) {
						// default contingency is null?
						String contingency = ((TypeInterface)itf).getContingency();
						if(contingency==null || TypeInterface.MANDATORY_CONTINGENCY.equals(contingency) ) {
							logger.debug("[PAUnboundInterfaceDetectorLoader] Unbound mandatory interface. Component: "+ componentName + " Interface: "+ itf.getName() + " Contingency: "+ ((TypeInterface)itf).getContingency());
							// TODO should throw a list of errors (containing every unbound interface)
							throw new ADLException(BindingErrors.UNBOUND_CLIENT_INTERFACE, comp,
									itf, componentName);

						}
					}

				}				

			}
		}
	}


	/** 
	 * General, recursive check for ComponentContainer nodes.<br/>
	 * Collects the list of interfaces and bindings, then processes its subcomponents, and then checks the bindings.<br/>
	 * <br/>
	 * For collecting the list of subcomponents it considers NF components defined inside the &lt;controller&gt; tag.
	 * 
	 * @param node
	 * @param unboundInterfaces
	 */
	protected void checkNode(final Node node, Map<ComponentContainer, Set<Interface>> unboundInterfaces) {

		logger.debug("[PAUnboundInterfaceDetectorLoader] Checking component " + node.toString());
		// we are only interested in components
		if(!(node instanceof ComponentContainer)) {
			return;
		}
		
		// add an entry for itself to the list of unbound interfaces)
		unboundInterfaces.put((ComponentContainer) node, new HashSet<Interface>());
		
		// get the list of bindings of comp
		Set<Binding> bindings = new HashSet<Binding>();
		if(node instanceof BindingContainer) {
			for(Binding binding : ((BindingContainer)node).getBindings()) {
				bindings.add(binding);
			}
			// also check bindings described in the membrane
			if(node instanceof ControllerContainer) {
				Controller ctrl = ((ControllerContainer)node).getController();
				if(ctrl != null) {
					if(ctrl instanceof BindingContainer) {
						for(Binding binding : ((BindingContainer) ctrl).getBindings()) {
							bindings.add(binding);
						}
					}
				}
			}
		}
		
		// check each subcomponent
		if(node instanceof ComponentContainer) {
			for(Component c : ((ComponentContainer)node).getComponents()) {
				checkNode(c, unboundInterfaces);
			}
			// also check membrane components
			if(node instanceof ControllerContainer) {
				Controller ctrl = ((ControllerContainer)node).getController();
				if(ctrl != null) {
					if(ctrl instanceof ComponentContainer) {
						for(Component c : ((ComponentContainer)ctrl).getComponents()) {
							checkNode(c, unboundInterfaces);
						}
					}
				}
			}
		}

		// add the interfaces of comp to the list of unbound interfaces
		if(node instanceof InterfaceContainer) {
			Set<Interface> cItfs = unboundInterfaces.get(node);
			for(Interface itf : ((InterfaceContainer) node).getInterfaces()) {
				cItfs.add(itf);
			}
			// also add interfaces of the membrane (NF interfaces)
			if(node instanceof ControllerContainer) {
				Controller ctrl = ((ControllerContainer)node).getController();
				if(ctrl != null) {
					if(ctrl instanceof InterfaceContainer) {
						for(Interface itf : ((InterfaceContainer) ctrl).getInterfaces()) {
							cItfs.add(itf);
						}
					}
				}
			}
		}
		
		// check the bindings of comp, using the list of unbound interfaces
		for(Binding binding : bindings) {
			checkBinding((ComponentContainer) node, binding, unboundInterfaces);			
		}
		
	}
	
	protected void checkBinding(final ComponentContainer node, Binding binding, Map<ComponentContainer, Set<Interface>> unboundInterfaces) {
		logger.debug("[PAUnboundInterfaceDetectorLoader] Checking binding "+ binding.getFrom() + " --> " + binding.getTo() + " " );
		
		String fromComponentName = getComponentName(binding.getFrom());
		String toComponentName = getComponentName(binding.getTo());
		String fromInterfaceName = getInterfaceName(binding.getFrom()); 
		String toInterfaceName = getInterfaceName(binding.getTo()); 
		
		ComponentContainer fromComponent = node;		
		// the FROM component is not "this"
		if(!fromComponentName.equals("this")) {
			// find the FROM component from the subcomponents
			for(Component c : node.getComponents()) {
				if(c.getName().equals(fromComponentName)) {
					fromComponent = c;
				}
			}
			// it may also be inside the membrane
			if(node instanceof ControllerContainer) {
				Controller ctrl = ((ControllerContainer) node).getController();
				if(ctrl != null) {
					if(ctrl instanceof ComponentContainer) {
						for(Component c : ((ComponentContainer) ctrl).getComponents()) {
							if(c.getName().equals(fromComponentName)) {
								fromComponent = c;
							}
						}
					}
				}
			}
			
		}
		// find the FROM interface
		Set<Interface> fromInterfaces = unboundInterfaces.get(fromComponent);
		Interface fromInterface = getInterface(fromInterfaceName, fromInterfaces);
		// remove the interface from the list of unboundInterfaces
		if(fromInterface != null) {
			fromInterfaces.remove(fromInterface);
		}		
		
		ComponentContainer toComponent = node;
		// the TO component is not "this"
		if(!toComponentName.equals("this")) {
			// find the TO component from the subcomponents
			for(Component c : node.getComponents()) {
				if(c.getName().equals(toComponentName)) {
					toComponent = c;
				}
			}
			// it may also be inside the membrane
			if(node instanceof ControllerContainer) {
				Controller ctrl = ((ControllerContainer) node).getController();
				if(ctrl != null) {
					if(ctrl instanceof ComponentContainer) {
						for(Component c : ((ComponentContainer) ctrl).getComponents()) {
							if(c.getName().equals(toComponentName)) {
								toComponent = c;
							}
						}
					}
				}
			}
			
		}
		// find the TO interface
		Set<Interface> toInterfaces = unboundInterfaces.get(toComponent);
		Interface toInterface = getInterface(toInterfaceName, toInterfaces);
		// remove the interface from the list of unboundInterfaces
		if(toInterface != null) {
			toInterfaces.remove(toInterface);
		}
		
		
		
	}
	
	
	protected String getComponentName(String description) {
		final int i = description.indexOf('.');
		return description.substring(0, i);
	}
	
	protected String getInterfaceName(String description) {
		final int i = description.indexOf('.');
		return description.substring(i+1);
	}

	protected Interface getInterface(String itfName, Set<Interface> interfaces) {
		for(Interface itf : interfaces) {
			if(itf.getName().equals(itfName)) {
				return itf;
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	

	
}
