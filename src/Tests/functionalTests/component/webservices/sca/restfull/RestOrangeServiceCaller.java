package functionalTests.component.webservices.sca.restfull;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.objectweb.proactive.core.component.webservices.PAWSCaller;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class RestOrangeServiceCaller implements PAWSCaller{
	private String rootUrl; 
	
	private String constructUrl(String id,String from,String to,String content)
	{
		return rootUrl+"?id=["+id+"]&from=["+from+"]&to=["+to+"]&content=["+content+"]";
	}
	
	public Object callWS(String methodName, Object[] args, Class<?> returnType) {
		if(!methodName.equals("sendSMS"))
		{
			System.err.println("NoSuchMethode on webservice "+ methodName);
		}
		if(args.length!=4)
		{
			System.err.println("Nb of arguments are not correct!");
		}
		String url = constructUrl((String)args[0], (String)args[1], (String)args[2], (String)args[3]);
		ResultatParseur resP = new ResultatParseur(url);
		resP.execute();
		int status_code = resP.getStatus_code();
		String msg=resP.getStatus_msg();
		System.out.println("return message : " +msg);
		if(status_code==200)
		{
			return true;
		}
		return false;
	}

	public void setup(Class<?> serviceClass, String wsUrl) {
		rootUrl=wsUrl;
	}

}
class ResultatParseur extends DefaultHandler
{
	private String url;
	private boolean disp_code=false;
	private boolean disp_msg=false;
	private int status_code;
	private String status_msg;
	
	public ResultatParseur(String urlToParse) {
		url=urlToParse;
	}
	
	public int getStatus_code() {
		return status_code;
	}
	
	public String getStatus_msg() {
		return status_msg;
	}
	
	public void execute()
    {
		try{	
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(this);
			xmlReader.setErrorHandler(null);//(new MyErrorHandler(System.err));
			xmlReader.parse(url);
		} catch (Throwable t) {
		    t.printStackTrace();
	    }
	}
	
	public void startDocument()
	throws SAXException {
	System.out.println("Start Document: "+url);
    }

    // debut de l'element
    public void startElement(String namespaceURI,
			     String localName, // local name
			     String rawName,   // qualified name
			     Attributes atts)throws SAXException
    {
		// recuperation du nom de l'element
		String eltName = localName;
		if ("".equals(eltName)) eltName = rawName;  
	        if(eltName.equals("status_code")) 
	        {
	            disp_code=true;
	            //System.out.print("dbt element: "+ eltName+"\n");
	        }
	        if(eltName.equals("status_msg")) 
	        {
	            disp_msg=true;
	            //System.out.print("dbt element: "+ eltName+"\n");
	        }
    }
  
    // Pour les noeuds textes
    @Override
    public void characters (char[] ch, int start, int length)
    {
        if(disp_code==true){
        	String text = new String (ch, start, length);
            if(text.trim().length()>1){
                status_code=Integer.parseInt(text);
            }
        }
        if(disp_msg==true){
        	String text = new String (ch, start, length);
            if(text.trim().length()>1){
                status_msg=text;
            }
        }
    }

   
    public void endElement(java.lang.String uri,
			   java.lang.String localName,
			   java.lang.String rawName)
	throws SAXException
    {
		String eltName = localName;
		if ("".equals(eltName)) eltName = rawName;
		if(eltName.equals("status_code")) 
	    {
	        disp_code=false;
	        //System.out.print("dbt element: "+ eltName+"\n");
	    }
	    if(eltName.equals("status_msg")) 
	    {
	        disp_msg=false;
	        //System.out.print("dbt element: "+ eltName+"\n");
	    }
        
        
    }

    // fin du document
    public void endDocument()
	throws SAXException {
	//System.out.print("End Document\n");
    }

}