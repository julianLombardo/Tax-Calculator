package taxcalculator;

import java.util.List;

public class TaxResult {
    private final double grossIncome;
    private final double deduction;
    private final double taxableIncome;
    private final double totalTax;
    private final double effectiveRate;
    private final double marginalRate;
    private final List<BracketResult> bracketBreakdown;
    private final double totalCredits;
    private final double ficaTax;
    private final double selfEmploymentTax;
    private final double capitalGainsTax;
    private final double stateTax;
    private final double takeHomePay;

    public TaxResult(double grossIncome, double deduction, double taxableIncome,
                     double totalTax, double effectiveRate, double marginalRate,
                     List<BracketResult> bracketBreakdown, double totalCredits,
                     double ficaTax, double selfEmploymentTax, double capitalGainsTax,
                     double stateTax, double takeHomePay) {
        this.grossIncome = grossIncome;
        this.deduction = deduction;
        this.taxableIncome = taxableIncome;
        this.totalTax = totalTax;
        this.effectiveRate = effectiveRate;
        this.marginalRate = marginalRate;
        this.bracketBreakdown = bracketBreakdown;
        this.totalCredits = totalCredits;
        this.ficaTax = ficaTax;
        this.selfEmploymentTax = selfEmploymentTax;
        this.capitalGainsTax = capitalGainsTax;
        this.stateTax = stateTax;
        this.takeHomePay = takeHomePay;
    }

    public double getGrossIncome() { return grossIncome; }
    public double getDeduction() { return deduction; }
    public double getTaxableIncome() { return taxableIncome; }
    public double getTotalTax() { return totalTax; }
    public double getEffectiveRate() { return effectiveRate; }
    public double getMarginalRate() { return marginalRate; }
    public List<BracketResult> getBracketBreakdown() { return bracketBreakdown; }
    public double getTotalCredits() { return totalCredits; }
    public double getFicaTax() { return ficaTax; }
    public double getSelfEmploymentTax() { return selfEmploymentTax; }
    public double getCapitalGainsTax() { return capitalGainsTax; }
    public double getStateTax() { return stateTax; }
    public double getTakeHomePay() { return takeHomePay; }
}
