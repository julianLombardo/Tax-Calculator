package taxcalculator;

public enum DeductionType {
    STANDARD("Standard"),
    ITEMIZED("Itemized");

    private final String displayName;

    DeductionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
