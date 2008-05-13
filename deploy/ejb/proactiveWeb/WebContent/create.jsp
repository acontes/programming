<%@page import="javax.naming.NamingException"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="org.objectweb.proactive.extensions.ejb.ProActiveEjbLoaderRegistryInterface"%>
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%!
   private ProActiveEjbLoaderRegistryInterface _registryLoader;
   private static final String REGISTRY_JNDI_NAME = "ProActive/RegistryBean/Local";

	public void jspInit() {
		InitialContext ctx;
		try{
			ctx = new InitialContext();
			try {
				_registryLoader = (ProActiveEjbLoaderRegistryInterface)ctx.lookup(REGISTRY_JNDI_NAME);
			}
			catch(NamingException e) {
				log( "Invalid name passed for JNDI lookup:" + REGISTRY_JNDI_NAME);
				log( "nested exception is "  + e.getMessage() , e);
			}
		}
		catch(NamingException e) {
			log( "Could not get an initial context for JNDI lookup");
			log( "nested exception is:" + e.getMessage() , e);
		}
	}%>
	
<%
	_registryLoader.setLog4jConfigFile(request.getParameter("log4jConfig"));
	_registryLoader.setvmName(request.getParameter("vmName"));

	_registryLoader.startService();
	
	response.sendRedirect("index.html");
%>
	