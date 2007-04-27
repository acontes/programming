package org.objectweb.proactive.examples.basic;

import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptorImpl;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeImpl;
import org.objectweb.proactive.core.descriptor.data.VirtualMachine;
import org.objectweb.proactive.core.process.ExternalProcess;

ProActiveDescriptorImpl pad = new ProActiveDescriptorImpl("");

VirtualNodeImpl vn = pad.createVirtualNode("Hello", false)
VirtualMachine vm = pad.createVirtualMachine("Jvm1")
ExternalProcess process = pad.createProcess("localJVM", "org.objectweb.proactive.core.process.JVMNodeProcess")

vm.setProcess(process)
vn.setProperty("multiple")
vn.setRegistrationProtocol("rmi")
vn.addVirtualMachine(vm)

returned_pad = pad
