package taxcalculator;

public class TaxInput {
    private double grossIncome;
    private FilingStatus filingStatus;
    private DeductionType deductionType;
    private double itemizedAmount;
    private int taxYear;
    private int numberOfChildren;
    private double selfEmploymentIncome;
    private double longTermCapitalGains;
    private String stateCode;

    public TaxInput() {
        this.filingStatus = FilingStatus.SINGLE;
        this.deductionType = DeductionType.STANDARD;
        this.taxYear = 2025;
        this.stateCode = "NONE";
    }

    public double getGrossIncome() { return grossIncome; }
    public void setGrossIncome(double grossIncome) { this.grossIncome = grossIncome; }

    public FilingStatus getFilingStatus() { return filingStatus; }
    public void setFilingStatus(FilingStatus filingStatus) { this.filingStatus = filingStatus; }

    public DeductionType getDeductionType() { return deductionType; }
    public void setDeductionType(DeductionType deductionType) { this.deductionType = deductionType; }

    public double getItemizedAmount() { return itemizedAmount; }
    public void setItemizedAmount(double itemizedAmount) { this.itemizedAmount = itemizedAmount; }

    public int getTaxYear() { return taxYear; }
    public void setTaxYear(int taxYear) { this.taxYear = taxYear; }

    public int getNumberOfChildren() { return numberOfChildren; }
    public void setNumberOfChildren(int numberOfChildren) { this.numberOfChildren = numberOfChildren; }

    public double getSelfEmploymentIncome() { return selfEmploymentIncome; }
    public void setSelfEmploymentIncome(double selfEmploymentIncome) { this.selfEmploymentIncome = selfEmploymentIncome; }

    public double getLongTermCapitalGains() { return longTermCapitalGains; }
    public void setLongTermCapitalGains(double longTermCapitalGains) { this.longTermCapitalGains = longTermCapitalGains; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
}
