package simulation;

public enum Network {

    Ortuzar("Ortuzar"),
    OrtuzarModified("OrtuzarModified"),
    SiouxFalls("SiouxFalls"),
    TwoNodes("TwoNodes"),
    Bypass("Bypass");

    private final String value;

    private Network(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}