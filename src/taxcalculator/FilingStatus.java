package taxcalculator;

public enum FilingStatus {
    SINGLE("Single"),
    MARRIED_JOINTLY("Married Filing Jointly"),
    MARRIED_SEPARATELY("Married Filing Separately"),
    HEAD_OF_HOUSEHOLD("Head of Household");

    private final String displayName;

    FilingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getStandardDeduction() {
        return TaxBracket.getStandardDeduction(this, 2025);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
