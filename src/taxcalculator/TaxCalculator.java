package taxcalculator;

import java.util.ArrayList;
import java.util.List;

public class TaxCalculator {

    public static TaxResult calculateTax(TaxInput input) {
        double grossIncome = input.getGrossIncome();
        FilingStatus status = input.getFilingStatus();
        int year = input.getTaxYear();

        // SE deduction reduces AGI
        double wages = Math.max(0, grossIncome - input.getSelfEmploymentIncome());
        double seDeduction = SelfEmploymentTax.deductibleHalf(input.getSelfEmploymentIncome(), wages, year, status);

        // Deduction
        double deduction;
        if (input.getDeductionType() == DeductionType.STANDARD) {
            deduction = TaxBracket.getStandardDeduction(status, year);
        } else {
            deduction = input.getItemizedAmount();
        }

        double agi = grossIncome - seDeduction;
        double taxableIncome = Math.max(0, agi - deduction);

        // Federal income tax brackets (on ordinary income only, not LTCG)
        double[] rates = TaxBracket.RATES;
        double[] thresholds = TaxBracket.getThresholds(status, year);

        List<BracketResult> breakdown = new ArrayList<>();
        double federalTax = 0;
        double marginalRate = rates[0];
        double previousThreshold = 0;

        for (int i = 0; i < rates.length; i++) {
            double upperBound = thresholds[i];
            double bracketSize = upperBound - previousThreshold;
            double taxableInBracket = 0;

            if (taxableIncome > previousThreshold) {
                taxableInBracket = Math.min(taxableIncome - previousThreshold, bracketSize);
                marginalRate = rates[i];
            }

            double taxInBracket = taxableInBracket * rates[i];
            federalTax += taxInBracket;

            breakdown.add(new BracketResult(
                    rates[i], previousThreshold, upperBound,
                    taxableInBracket, taxInBracket
            ));

            previousThreshold = upperBound;

            if (taxableIncome <= upperBound) {
                for (int j = i + 1; j < rates.length; j++) {
                    double lb = thresholds[j - 1];
                    double ub = thresholds[j];
                    breakdown.add(new BracketResult(rates[j], lb, ub, 0, 0));
                }
                break;
            }
        }

        // Capital gains tax
        double capitalGainsTax = CapitalGainsTax.calculate(
                input.getLongTermCapitalGains(), taxableIncome, status, year);

        // Credits (applied against federal income tax)
        double totalCredits = TaxCredits.calculateTotalCredits(input, agi);
        double netFederalTax = Math.max(0, federalTax - totalCredits);

        // FICA on wages (gross - SE income)
        double ficaTax = FICATax.calculate(wages, year, status);

        // Self-employment tax (SS wage base reduced by W-2 wages already taxed)
        double seTax = SelfEmploymentTax.calculate(input.getSelfEmploymentIncome(), wages, year, status);

        // State tax
        double stateTax = StateTax.calculate(taxableIncome, input.getStateCode());

        // Total tax burden (federal income tax only, excluding separately-shown items)
        double totalTax = netFederalTax;

        // Take-home = gross + capital gains - all taxes
        double totalIncome = grossIncome + input.getLongTermCapitalGains();
        double takeHome = totalIncome - netFederalTax - capitalGainsTax - ficaTax - seTax - stateTax;

        double effectiveRate = (totalIncome > 0)
                ? ((netFederalTax + capitalGainsTax + ficaTax + seTax + stateTax) / totalIncome) * 100
                : 0;

        return new TaxResult(grossIncome, deduction, taxableIncome,
                netFederalTax, effectiveRate, marginalRate * 100, breakdown,
                totalCredits, ficaTax, seTax, capitalGainsTax, stateTax, takeHome);
    }
}
