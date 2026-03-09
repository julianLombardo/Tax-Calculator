package taxcalculator;

public class CapitalGainsTax {

    private static final double NIIT_RATE = 0.038;
    private static final double NIIT_THRESHOLD_SINGLE = 200000.0;
    private static final double NIIT_THRESHOLD_JOINT = 250000.0;

    public static double calculate(double longTermCapitalGains, double taxableIncome,
                                    FilingStatus status, int taxYear) {
        if (longTermCapitalGains <= 0) return 0;

        double[] thresholds = getThresholds(status, taxYear);
        double rate0Limit = thresholds[0];
        double rate15Limit = thresholds[1];

        // taxableIncome here is ordinary taxable income (without cap gains)
        double ordinaryIncome = taxableIncome;
        double tax = 0;
        double remaining = longTermCapitalGains;

        // 0% bracket: fills up from ordinary income to rate0Limit
        if (ordinaryIncome < rate0Limit) {
            double zeroSpace = rate0Limit - ordinaryIncome;
            double atZero = Math.min(remaining, zeroSpace);
            remaining -= atZero;
        }

        // 15% bracket
        if (remaining > 0) {
            double combinedSoFar = ordinaryIncome + (longTermCapitalGains - remaining);
            double fifteenSpace = rate15Limit - Math.max(combinedSoFar, rate0Limit);
            if (fifteenSpace > 0) {
                double atFifteen = Math.min(remaining, fifteenSpace);
                tax += atFifteen * 0.15;
                remaining -= atFifteen;
            }
        }

        // 20% bracket
        if (remaining > 0) {
            tax += remaining * 0.20;
        }

        return tax + calculateNIIT(longTermCapitalGains, ordinaryIncome + longTermCapitalGains, status);
    }

    private static double calculateNIIT(double investmentIncome, double agi, FilingStatus status) {
        double threshold;
        switch (status) {
            case MARRIED_JOINTLY:    threshold = NIIT_THRESHOLD_JOINT; break;
            case MARRIED_SEPARATELY: threshold = 125000.0; break;
            default:                 threshold = NIIT_THRESHOLD_SINGLE; break;
        }
        if (agi <= threshold) return 0;
        double excess = agi - threshold;
        return Math.min(investmentIncome, excess) * NIIT_RATE;
    }

    private static double[] getThresholds(FilingStatus status, int taxYear) {
        if (taxYear == 2025) {
            switch (status) {
                case MARRIED_JOINTLY: return new double[]{96700, 600050};
                case MARRIED_SEPARATELY: return new double[]{48350, 300000};
                case HEAD_OF_HOUSEHOLD: return new double[]{64750, 566700};
                default: return new double[]{48350, 533400};
            }
        } else if (taxYear == 2024) {
            switch (status) {
                case MARRIED_JOINTLY: return new double[]{94050, 583750};
                case MARRIED_SEPARATELY: return new double[]{47025, 291850};
                case HEAD_OF_HOUSEHOLD: return new double[]{63000, 551350};
                default: return new double[]{47025, 518900};
            }
        } else { // 2023
            switch (status) {
                case MARRIED_JOINTLY: return new double[]{89250, 553850};
                case MARRIED_SEPARATELY: return new double[]{44625, 276900};
                case HEAD_OF_HOUSEHOLD: return new double[]{59750, 523050};
                default: return new double[]{44625, 492300};
            }
        }
    }
}
