package taxcalculator;

public class TaxCredits {

    // ── Child Tax Credit ──────────────────────────────────────────────
    private static final double CTC_PER_CHILD = 2000.0;
    private static final double CTC_PHASE_OUT_SINGLE = 200000.0;
    private static final double CTC_PHASE_OUT_JOINT = 400000.0;
    private static final double CTC_PHASE_OUT_RATE = 0.05;
    private static final double CTC_PHASE_OUT_INCREMENT = 1000.0;

    public static double calculateChildTaxCredit(int numberOfChildren, double agi, FilingStatus status) {
        if (numberOfChildren <= 0) return 0;

        double maxCredit = numberOfChildren * CTC_PER_CHILD;
        double threshold = (status == FilingStatus.MARRIED_JOINTLY)
                ? CTC_PHASE_OUT_JOINT : CTC_PHASE_OUT_SINGLE;

        if (agi <= threshold) return maxCredit;

        double excess = Math.ceil((agi - threshold) / CTC_PHASE_OUT_INCREMENT) * CTC_PHASE_OUT_INCREMENT;
        double reduction = excess * CTC_PHASE_OUT_RATE;
        return Math.max(0, maxCredit - reduction);
    }

    // ── Earned Income Credit (full tables: 0, 1, 2, 3+ children) ─────
    // Each EIC config: {maxCredit, phaseInRate, phaseInEnd, phaseOutStart_single, phaseOutStart_joint, phaseOutRate, incomeLimit_single, incomeLimit_joint}

    public static double calculateEarnedIncomeCredit(double earnedIncome, FilingStatus status,
                                                      int numberOfChildren, int taxYear) {
        if (status == FilingStatus.MARRIED_SEPARATELY) return 0;
        if (earnedIncome <= 0) return 0;

        boolean joint = (status == FilingStatus.MARRIED_JOINTLY);
        double[] params = getEICParams(Math.min(numberOfChildren, 3), taxYear, joint);

        double maxCredit     = params[0];
        double phaseInRate   = params[1];
        double phaseOutStart = params[2];
        double phaseOutRate  = params[3];
        double incomeLimit   = params[4];

        if (earnedIncome > incomeLimit) return 0;

        // Phase-in: credit increases as income rises
        double credit = Math.min(earnedIncome * phaseInRate, maxCredit);

        // Phase-out: credit decreases above threshold
        if (earnedIncome > phaseOutStart) {
            double reduction = (earnedIncome - phaseOutStart) * phaseOutRate;
            credit = Math.max(0, credit - reduction);
        }

        return Math.round(credit * 100.0) / 100.0;
    }

    private static double[] getEICParams(int children, int year, boolean joint) {
        // Returns: {maxCredit, phaseInRate, phaseOutStart, phaseOutRate, incomeLimit}
        if (year == 2024) {
            switch (children) {
                case 0: return new double[]{632, 0.0765, joint ? 16510 : 9800, 0.0765, joint ? 24210 : 18591};
                case 1: return new double[]{3995, 0.34, joint ? 28120 : 21560, 0.1598, joint ? 53120 : 46560};
                case 2: return new double[]{6604, 0.40, joint ? 28120 : 21560, 0.2106, joint ? 59478 : 52918};
                default: return new double[]{7430, 0.45, joint ? 28120 : 21560, 0.2106, joint ? 63398 : 56838};
            }
        } else { // 2023
            switch (children) {
                case 0: return new double[]{600, 0.0765, joint ? 15290 : 9080, 0.0765, joint ? 22610 : 17640};
                case 1: return new double[]{3995, 0.34, joint ? 26260 : 19520, 0.1598, joint ? 49622 : 43492};
                case 2: return new double[]{6604, 0.40, joint ? 26260 : 19520, 0.2106, joint ? 55768 : 49622};
                default: return new double[]{7430, 0.45, joint ? 26260 : 19520, 0.2106, joint ? 59899 : 53057};
            }
        }
    }

    // ── Total Credits ─────────────────────────────────────────────────
    public static double calculateTotalCredits(TaxInput input, double agi) {
        double ctc = calculateChildTaxCredit(input.getNumberOfChildren(), agi, input.getFilingStatus());
        double eic = calculateEarnedIncomeCredit(
                input.getGrossIncome(), input.getFilingStatus(),
                input.getNumberOfChildren(), input.getTaxYear());
        return ctc + eic;
    }
}
