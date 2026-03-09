package taxcalculator;

public class FICATax {

    private static final double SS_RATE = 0.062;
    private static final double MEDICARE_RATE = 0.0145;
    private static final double ADDITIONAL_MEDICARE_RATE = 0.009;

    public static double getSSWageBase(int taxYear) {
        switch (taxYear) {
            case 2025: return 176100.0;
            case 2024: return 168600.0;
            default:   return 160200.0; // 2023
        }
    }

    private static double getAdditionalMedicareThreshold(FilingStatus status) {
        switch (status) {
            case MARRIED_JOINTLY:    return 250000.0;
            case MARRIED_SEPARATELY: return 125000.0;
            default:                 return 200000.0; // Single, HoH
        }
    }

    public static double calculateSocialSecurity(double wages, int taxYear) {
        double wageBase = getSSWageBase(taxYear);
        return Math.min(wages, wageBase) * SS_RATE;
    }

    public static double calculateMedicare(double wages, FilingStatus status) {
        double base = wages * MEDICARE_RATE;
        double threshold = getAdditionalMedicareThreshold(status);
        double additional = 0;
        if (wages > threshold) {
            additional = (wages - threshold) * ADDITIONAL_MEDICARE_RATE;
        }
        return base + additional;
    }

    public static double calculate(double wages, int taxYear, FilingStatus status) {
        return calculateSocialSecurity(wages, taxYear) + calculateMedicare(wages, status);
    }
}
