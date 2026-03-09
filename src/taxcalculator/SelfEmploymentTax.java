package taxcalculator;

public class SelfEmploymentTax {

    private static final double NET_EARNINGS_FACTOR = 0.9235;
    private static final double SS_RATE = 0.124;
    private static final double MEDICARE_RATE = 0.029;
    private static final double ADDITIONAL_MEDICARE_RATE = 0.009;

    public static double calculateNetEarnings(double selfEmploymentIncome) {
        return selfEmploymentIncome * NET_EARNINGS_FACTOR;
    }

    private static double getAdditionalMedicareThreshold(FilingStatus status) {
        switch (status) {
            case MARRIED_JOINTLY:    return 250000.0;
            case MARRIED_SEPARATELY: return 125000.0;
            default:                 return 200000.0;
        }
    }

    /**
     * Calculate SE tax, reducing the SS wage base by W-2 wages already taxed for SS.
     */
    public static double calculate(double selfEmploymentIncome, double w2Wages,
                                    int taxYear, FilingStatus status) {
        if (selfEmploymentIncome <= 0) return 0;

        double netEarnings = calculateNetEarnings(selfEmploymentIncome);
        double ssWageBase = FICATax.getSSWageBase(taxYear);

        // SS wage base is reduced by W-2 wages already subject to SS tax
        double remainingSSBase = Math.max(0, ssWageBase - w2Wages);
        double ssTax = Math.min(netEarnings, remainingSSBase) * SS_RATE;

        double medicareTax = netEarnings * MEDICARE_RATE;

        // Additional Medicare on combined wages + SE earnings above threshold
        double threshold = getAdditionalMedicareThreshold(status);
        double combinedEarnings = w2Wages + netEarnings;
        double additionalMedicare = 0;
        if (combinedEarnings > threshold) {
            // Only the SE portion above threshold (after accounting for wages)
            double seAboveThreshold = Math.max(0, combinedEarnings - Math.max(w2Wages, threshold));
            additionalMedicare = seAboveThreshold * ADDITIONAL_MEDICARE_RATE;
        }

        return ssTax + medicareTax + additionalMedicare;
    }

    public static double deductibleHalf(double selfEmploymentIncome, double w2Wages,
                                         int taxYear, FilingStatus status) {
        return calculate(selfEmploymentIncome, w2Wages, taxYear, status) / 2.0;
    }
}
