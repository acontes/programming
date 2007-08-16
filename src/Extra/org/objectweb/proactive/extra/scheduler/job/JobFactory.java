package org.objectweb.proactive.extra.scheduler.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.objectweb.proactive.extra.scheduler.scripting.InvalidScriptException;
import org.objectweb.proactive.extra.scheduler.scripting.Script;
import org.objectweb.proactive.extra.scheduler.scripting.SimpleScript;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;
import org.objectweb.proactive.extra.scheduler.task.JavaTaskDescriptor;
import org.objectweb.proactive.extra.scheduler.task.Task;
import org.objectweb.proactive.extra.scheduler.task.TaskDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class JobFactory {

    /**
     * Singleton Pattern
     */
    private static JobFactory factory = null;
    private static final boolean VALIDATING = false;

    public static JobFactory getFactory() {
        if (factory == null) {
            factory = new JobFactory();
        }
        return factory;
    }

    private JobFactory() {}

    public Job createJob(String fileUrl)
        throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, InvalidScriptException,
            ClassNotFoundException {
        File f = new File(fileUrl);
        if (!f.exists()) {
            throw new FileNotFoundException("This file has not been found : " +
                fileUrl);
        }
        return createJob(new FileInputStream(f));
    }

    public Job createJob(InputStream input)
        throws ParserConfigurationException, SAXException, IOException,
            XPathExpressionException, InvalidScriptException,
            ClassNotFoundException {
        // Recuperation du document DOM
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(VALIDATING);
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = parser.parse(input);

        XPath xpath = XPathFactory.newInstance().newXPath();
        String name = null;
        String priority = null;
        String description = null;
        JobType jt = null;
        Map<TaskDescriptor, String> tasks = new HashMap<TaskDescriptor, String>();

        // JOB
        XPathExpression exp = xpath.compile("/job");
        Node jobNode = (Node) exp.evaluate(doc, XPathConstants.NODE);
        if (jobNode != null) {
            NamedNodeMap jobAttr = jobNode.getAttributes();
            if (jobAttr != null) {
                // JOB NAME
                Node node = jobAttr.getNamedItem("name");
                if (node != null) {
                    name = node.getNodeValue();
                }
                // JOB PRIORITY
                node = jobAttr.getNamedItem("priority");
                if (node != null) {
                    priority = node.getNodeValue();
                }
                // JOB TYPE
                node = jobAttr.getNamedItem("type");
                if (node != null) {
                    String s = node.getNodeValue();
                    jt = JobType.valueOf(s);
                    if(jt == null) {
                    	throw new SAXException("Invalid XML : Job must have a valid type");
                    }
                    
                }
                // JOB DESCRIPTION
                description = (String) xpath.evaluate("/job/description", doc,
                        XPathConstants.STRING);
                if (description != null) {
                    System.out.println("Job description = " + description);
                }
            }

            // TODO Add Env parameters
        }

        System.out.println("Job : " + name + " - priority : " + priority);

        // TASKS
        NodeList list = (NodeList) xpath.evaluate("/job/tasks/task", doc,
                XPathConstants.NODESET);
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node taskNode = list.item(i);

                // TASK PROCESS
                TaskDescriptor desc = null;
                Node process = (Node) xpath.evaluate("process/javaProcess",
                        taskNode, XPathConstants.NODE);
                if (process != null) {
                    desc = createJavaTask(process, xpath);
                }

                // TASK NAME
                desc.setName((String) xpath.evaluate("@name", taskNode,
                        XPathConstants.STRING));
                System.out.println("name = " + desc.getName());

                // TASK DESCRIPTION
                desc.setDescription((String) xpath.evaluate("description",
                        taskNode, XPathConstants.STRING));
                System.out.println("desc = " + desc.getDescription());

                // TASK FINAL
                desc.setFinalTask(
                        ((String) xpath.evaluate("@finalTask", taskNode,
                            XPathConstants.STRING)).equals("true"));
                System.out.println("final = " + desc.isFinalTask());

                // TASK RE RUNNABLE
                String rerunnable = (String) xpath.evaluate("@reRunnable", taskNode,
                        XPathConstants.STRING);
                if(rerunnable != "")
                	desc.setRerunnable(Integer.parseInt(rerunnable));
                else
                	desc.setRerunnable(0);
                System.out.println("reRun = " + desc.getRerunnable());

                // TASK RUN TIME LIMIT
                String timeLimit = (String) xpath.evaluate("@runtimeLimit",
                        taskNode, XPathConstants.STRING);
                System.out.println("time limit = " + timeLimit);
                // TODO timeLimit

                // TASK VERIF
                Node verifNode = (Node) xpath.evaluate("verifyingTask/script",
                        taskNode, XPathConstants.NODE);
                if (verifNode != null) {
                    desc.setVerifyingScript(createVerifyingScript(verifNode,
                            xpath));
                }

                // TASK PRE
                Node preNode = (Node) xpath.evaluate("preTask/script",
                        taskNode, XPathConstants.NODE);
                if (preNode != null) {
                    desc.setPreTask(createScript(preNode,
                            xpath));
                }

                // TASK POST
                Node postNode = (Node) xpath.evaluate("postTask/script",
                        taskNode, XPathConstants.NODE);
                if (postNode != null) {
                	System.out.println("POST");
                    desc.setPostTask(createScript(postNode,
                            xpath));
                }

                // TASK IO FILES
                // TODO Add IO files

                // TODO Add more options to Task

                // TASK DEPENDS
                tasks.put(desc,
                    (String) xpath.evaluate("@depends", taskNode,
                        XPathConstants.STRING));
            }
        }

        // Job creation
        Job job;
        if (jt == JobType.APPLI)
        	job = new ApplicationJob(name, getPriority(priority), -1, true, description);
        else if (jt == JobType.PARAMETER_SWIPPING)
        	job = new ParameterSwippingJob(name, getPriority(priority), -1, true, description);
        else
        	job = new TaskFlowJob(name, getPriority(priority), -1, true, description);
        // Dependencies
        HashMap<String, TaskDescriptor> depends = 
        	new HashMap<String, TaskDescriptor>();
        
        for(TaskDescriptor td : tasks.keySet())
        	depends.put(td.getName(), td);
        
        for (Entry<TaskDescriptor, String> task : tasks.entrySet()) {
            task.getKey().setJobId(job.getId());
        	String depstr = task.getValue();
        	if (!depstr.matches("[ ]*")){
	        	String[] deps = depstr.split(" ");
	        	for(int i = 0 ; i <  deps.length ; i++) {
	        		if(depends.containsKey(deps[i]))
	                    task.getKey().addDependence(depends.get(deps[i]));
	        		else
	        			System.err.println("Can't resolve dependence : "+deps[i]);
	        	}
        	}
            job.addTask(task.getKey());
        }
        
        return job;
    }

    private JobPriority getPriority(String priority) {
		if(priority.equals("highest")) return JobPriority.HIGHEST;
		else if(priority.equals("high")) return JobPriority.ABOVE_NORMAL;
		else if(priority.equals("low")) return JobPriority.BELOW_NORMAL;
		else if(priority.equals("lowest")) return JobPriority.LOWEST;
		else return JobPriority.NORMAL;
	}

	@SuppressWarnings("unchecked")
    private JavaTaskDescriptor createJavaTask(Node process, XPath xpath)
        throws XPathExpressionException, ClassNotFoundException, IOException {
        JavaTaskDescriptor desc = new JavaTaskDescriptor();
        desc.setTaskClass((Class<Task>) Class.forName(
                (String) xpath.compile("@class")
                              .evaluate(process, XPathConstants.STRING)));
        // TODO Verify that class extends Task
        System.out.println("task = " + desc.getTaskClass().getCanonicalName());
        NodeList args = (NodeList) xpath
                                        .evaluate("initParameters/parameter",process,
                XPathConstants.NODESET);
        if (args != null) {
            for (int i = 0; i < args.getLength(); i++) {
                Node arg = args.item(i);
                String name = (String) xpath.evaluate("@name", arg,
                        XPathConstants.STRING);
                String value = (String) xpath.evaluate("@value", arg,
                        XPathConstants.STRING);
                String serializedFile = (String) xpath.evaluate("@serializedFile",
                        arg, XPathConstants.STRING);
                if ((name != null) && (value != null)) {
                    desc.getArgs().put(name, value);
                } else if ((name != null) && (serializedFile != null)) {
                    FileInputStream fis = new FileInputStream(serializedFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    desc.getArgs().put(name, ois.readObject());
                }
            }
        }
        for (Entry<String, Object> entry : desc.getArgs().entrySet())
            System.out.println("arg: " + entry.getKey() + " = " +
                entry.getValue());
        return desc;
    }

    private Script<?> createScript(Node node, XPath xpath)
        throws XPathExpressionException, InvalidScriptException {
        String url = (String) xpath.evaluate("@url", node, XPathConstants.STRING);
        if (url != null && url != "") {
            try {
            	System.out.println(url);
                return new SimpleScript(new URL(url));
            } catch (MalformedURLException e) {
            	e.printStackTrace();
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        String path = (String) xpath.evaluate("@file", node,
                XPathConstants.STRING);
        if (path != null && path != "") {
            try {
            	System.out.println(path);
                return new SimpleScript(new File(path));
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        String engine = (String) xpath.evaluate("@engine", node,
                XPathConstants.STRING);
        if ((path != null && path != "") && (node.getTextContent() != null)) {
            String script = node.getTextContent();
            try {
                return new SimpleScript(script, engine);
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        throw new InvalidScriptException("The script is not valid");
    }

    private VerifyingScript createVerifyingScript(Node node, XPath xpath)
        throws XPathExpressionException, InvalidScriptException {
        String url = (String) xpath.evaluate("@url", node, XPathConstants.STRING);
        if (url != null && url != "") {
            try {
            	System.out.println(url);
                return new VerifyingScript(new URL(url));
            } catch (MalformedURLException e) {
            	e.printStackTrace();
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        String path = (String) xpath.evaluate("@file", node,
                XPathConstants.STRING);
        if (path != null && path != "") {
            try {
            	System.out.println(path);
                return new VerifyingScript(new File(path));
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        String engine = (String) xpath.evaluate("@engine", node,
                XPathConstants.STRING);
        if ((engine != null) && (node.getTextContent() != null)) {
            String script = node.getTextContent();
            try {
                return new VerifyingScript(script, engine);
            } catch (InvalidScriptException e) {
            	e.printStackTrace();
            }
        }
        throw new InvalidScriptException("The script is not valid");
    }
}
