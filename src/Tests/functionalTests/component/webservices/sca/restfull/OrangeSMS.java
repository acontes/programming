package functionalTests.component.webservices.sca.restfull;

public interface OrangeSMS {
	public boolean sendSMS(String id, String from, String to, String content);
}
