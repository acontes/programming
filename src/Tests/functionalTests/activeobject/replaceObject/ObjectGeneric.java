package functionalTests.activeobject.replaceObject;

public class ObjectGeneric {

    private String name;

    public ObjectGeneric(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
