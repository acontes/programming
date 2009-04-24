package functionalTests.component.webservices;

public interface HelloWorldItf extends HelloWorldItfSuperInterface {

    public void putHelloWorld();

    public void putTextToSay(String textToSay);

    public String sayText();

    public Boolean contains(String textToCheck);
}
