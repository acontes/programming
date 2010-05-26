package functionalTests.component.webservices.sca.restfull;

import org.objectweb.fractal.api.control.AttributeController;

public interface OrangeSMSClientAttributes extends AttributeController{
	
	public String getContent();
	public String getId();
	public String getFrom() ;
	public String getTo() ;
	
	public void setContent(String content) ;
	public void setFrom(String from) ;
	public void setId(String id) ;
	public void setTo(String to) ;

}
