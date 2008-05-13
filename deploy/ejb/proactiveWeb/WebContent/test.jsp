<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	import="org.objectweb.proactive.extensions.ejb.*,javax.naming.*,java.text.*"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%!
   private ProActiveBeanInterface testObj;
   private static final String JNDI_NAME = "ProActive/StupidBean/Local";
   
   private ProActiveEjbLoaderRegistryInterface registryLoader;
   private static final String REGISTRY_JNDI_NAME = "ProActive/RegistryBean/Local";

	public void jspInit() {
		InitialContext ctx;
		try{
			ctx = new InitialContext();
			try {
				testObj = (ProActiveBeanInterface) ctx
						.lookup(JNDI_NAME);
				registryLoader = (ProActiveEjbLoaderRegistryInterface)ctx.lookup(REGISTRY_JNDI_NAME);
			}
			catch(NamingException e) {
				log( "Invalid name passed for JNDI lookup,nested exception is "  + e.getMessage() , e);
			}
		}
		catch(NamingException e) {
			log( "Could not get an initial context for JNDI lookup,nested exception is:" + e.getMessage() , e);
		}
	}%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Test for ProActive loader</title>
</head>
<body>
	<h3>Welcome!</h3>
	<h2>Let's hear what the EJB has to say!!</h2>
	<p><%= testObj.test() %></p>
	<h2>The Registry EJB started, with the following parameters:</h2>
	<table>
		<tr>
			<td>The PART URL</td>
			<td><%= registryLoader.getProActiveRuntimeURL() %></td>
		</tr>
	</table>
</body>
</html>