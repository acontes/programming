package functionalTests.component.sca.orange;

public interface OrangeSMS {
	public boolean sendSMS(String id, String from, String to, String content);
}
