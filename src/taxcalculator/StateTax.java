package taxcalculator;

import java.util.LinkedHashMap;
import java.util.Map;

public class StateTax {

    public static final Map<String, String> STATES = new LinkedHashMap<>();

    static {
        STATES.put("NONE", "No State Tax");
        STATES.put("AL", "Alabama");
        STATES.put("AK", "Alaska");
        STATES.put("AZ", "Arizona");
        STATES.put("AR", "Arkansas");
        STATES.put("CA", "California");
        STATES.put("CO", "Colorado");
        STATES.put("CT", "Connecticut");
        STATES.put("DE", "Delaware");
        STATES.put("DC", "District of Columbia");
        STATES.put("FL", "Florida");
        STATES.put("GA", "Georgia");
        STATES.put("HI", "Hawaii");
        STATES.put("ID", "Idaho");
        STATES.put("IL", "Illinois");
        STATES.put("IN", "Indiana");
        STATES.put("IA", "Iowa");
        STATES.put("KS", "Kansas");
        STATES.put("KY", "Kentucky");
        STATES.put("LA", "Louisiana");
        STATES.put("ME", "Maine");
        STATES.put("MD", "Maryland");
        STATES.put("MA", "Massachusetts");
        STATES.put("MI", "Michigan");
        STATES.put("MN", "Minnesota");
        STATES.put("MS", "Mississippi");
        STATES.put("MO", "Missouri");
        STATES.put("MT", "Montana");
        STATES.put("NE", "Nebraska");
        STATES.put("NV", "Nevada");
        STATES.put("NH", "New Hampshire");
        STATES.put("NJ", "New Jersey");
        STATES.put("NM", "New Mexico");
        STATES.put("NY", "New York");
        STATES.put("NC", "North Carolina");
        STATES.put("ND", "North Dakota");
        STATES.put("OH", "Ohio");
        STATES.put("OK", "Oklahoma");
        STATES.put("OR", "Oregon");
        STATES.put("PA", "Pennsylvania");
        STATES.put("RI", "Rhode Island");
        STATES.put("SC", "South Carolina");
        STATES.put("SD", "South Dakota");
        STATES.put("TN", "Tennessee");
        STATES.put("TX", "Texas");
        STATES.put("UT", "Utah");
        STATES.put("VT", "Vermont");
        STATES.put("VA", "Virginia");
        STATES.put("WA", "Washington");
        STATES.put("WV", "West Virginia");
        STATES.put("WI", "Wisconsin");
        STATES.put("WY", "Wyoming");
    }

    public static double calculate(double taxableIncome, String stateCode) {
        if (stateCode == null || taxableIncome <= 0) return 0;

        switch (stateCode) {
            // ── No income tax ──────────────────────────────────
            case "AK": case "FL": case "NV": case "NH": case "SD":
            case "TN": case "TX": case "WA": case "WY": case "NONE":
                return 0;

            // ── Flat-rate states ───────────────────────────────
            case "AZ": return taxableIncome * 0.025;
            case "CO": return taxableIncome * 0.044;
            case "IL": return taxableIncome * 0.0495;
            case "IN": return taxableIncome * 0.0305;
            case "KY": return taxableIncome * 0.04;
            case "MA": return taxableIncome * 0.05;
            case "MI": return taxableIncome * 0.0425;
            case "MS": return flat5After10k(taxableIncome);
            case "NC": return taxableIncome * 0.0475;
            case "PA": return taxableIncome * 0.0307;
            case "UT": return taxableIncome * 0.0465;

            // ── Progressive states ─────────────────────────────
            case "AL": return calculateAL(taxableIncome);
            case "AR": return calculateAR(taxableIncome);
            case "CA": return calculateCA(taxableIncome);
            case "CT": return calculateCT(taxableIncome);
            case "DE": return calculateDE(taxableIncome);
            case "DC": return calculateDC(taxableIncome);
            case "GA": return calculateGA(taxableIncome);
            case "HI": return calculateHI(taxableIncome);
            case "ID": return calculateID(taxableIncome);
            case "IA": return calculateIA(taxableIncome);
            case "KS": return calculateKS(taxableIncome);
            case "LA": return calculateLA(taxableIncome);
            case "ME": return calculateME(taxableIncome);
            case "MD": return calculateMD(taxableIncome);
            case "MN": return calculateMN(taxableIncome);
            case "MO": return calculateMO(taxableIncome);
            case "MT": return calculateMT(taxableIncome);
            case "NE": return calculateNE(taxableIncome);
            case "NJ": return calculateNJ(taxableIncome);
            case "NM": return calculateNM(taxableIncome);
            case "NY": return calculateNY(taxableIncome);
            case "ND": return calculateND(taxableIncome);
            case "OH": return calculateOH(taxableIncome);
            case "OK": return calculateOK(taxableIncome);
            case "OR": return calculateOR(taxableIncome);
            case "RI": return calculateRI(taxableIncome);
            case "SC": return calculateSC(taxableIncome);
            case "VT": return calculateVT(taxableIncome);
            case "VA": return calculateVA(taxableIncome);
            case "WV": return calculateWV(taxableIncome);
            case "WI": return calculateWI(taxableIncome);

            default: return 0;
        }
    }

    // ── Helpers ────────────────────────────────────────────────────

    private static double applyBrackets(double income, double[][] brackets) {
        double tax = 0;
        for (double[] b : brackets) {
            double lower = b[0], upper = b[1], rate = b[2];
            if (income <= lower) break;
            tax += (Math.min(income, upper) - lower) * rate;
        }
        return tax;
    }

    private static double flat5After10k(double income) {
        // Mississippi: 0% up to $10,000, 5% above
        if (income <= 10000) return 0;
        return (income - 10000) * 0.05;
    }

    // ── Individual state calculators (2024 rates, single filer) ───

    private static double calculateAL(double income) {
        return applyBrackets(income, new double[][]{
            {0, 500, 0.02}, {500, 3000, 0.04}, {3000, Double.MAX_VALUE, 0.05}
        });
    }

    private static double calculateAR(double income) {
        return applyBrackets(income, new double[][]{
            {0, 4400, 0.02}, {4400, 8800, 0.04}, {8800, Double.MAX_VALUE, 0.044}
        });
    }

    private static double calculateCA(double income) {
        return applyBrackets(income, new double[][]{
            {0, 10412, 0.01}, {10412, 24684, 0.02}, {24684, 38959, 0.04},
            {38959, 54081, 0.06}, {54081, 68350, 0.08}, {68350, 349137, 0.093},
            {349137, 418961, 0.103}, {418961, 698271, 0.113},
            {698271, 1000000, 0.123}, {1000000, Double.MAX_VALUE, 0.133}
        });
    }

    private static double calculateCT(double income) {
        return applyBrackets(income, new double[][]{
            {0, 10000, 0.02}, {10000, 50000, 0.045}, {50000, 100000, 0.055},
            {100000, 200000, 0.06}, {200000, 250000, 0.065},
            {250000, 500000, 0.069}, {500000, Double.MAX_VALUE, 0.0699}
        });
    }

    private static double calculateDE(double income) {
        return applyBrackets(income, new double[][]{
            {0, 2000, 0.0}, {2000, 5000, 0.022}, {5000, 10000, 0.039},
            {10000, 20000, 0.048}, {20000, 25000, 0.052},
            {25000, 60000, 0.0555}, {60000, Double.MAX_VALUE, 0.066}
        });
    }

    private static double calculateDC(double income) {
        return applyBrackets(income, new double[][]{
            {0, 10000, 0.04}, {10000, 40000, 0.06}, {40000, 60000, 0.065},
            {60000, 250000, 0.085}, {250000, 500000, 0.0925},
            {500000, 1000000, 0.0975}, {1000000, Double.MAX_VALUE, 0.1075}
        });
    }

    private static double calculateGA(double income) {
        return applyBrackets(income, new double[][]{
            {0, 750, 0.01}, {750, 2250, 0.02}, {2250, 3750, 0.03},
            {3750, 5250, 0.04}, {5250, 7000, 0.05},
            {7000, Double.MAX_VALUE, 0.0549}
        });
    }

    private static double calculateHI(double income) {
        return applyBrackets(income, new double[][]{
            {0, 2400, 0.014}, {2400, 4800, 0.032}, {4800, 9600, 0.055},
            {9600, 14400, 0.064}, {14400, 19200, 0.068},
            {19200, 24000, 0.072}, {24000, 36000, 0.076},
            {36000, 48000, 0.079}, {48000, 150000, 0.0825},
            {150000, 175000, 0.09}, {175000, 200000, 0.10},
            {200000, Double.MAX_VALUE, 0.11}
        });
    }

    private static double calculateID(double income) {
        return applyBrackets(income, new double[][]{
            {0, 1662, 0.01}, {1662, 4987, 0.03}, {4987, 8312, 0.045},
            {8312, Double.MAX_VALUE, 0.058}
        });
    }

    private static double calculateIA(double income) {
        return applyBrackets(income, new double[][]{
            {0, 6210, 0.044}, {6210, 31050, 0.0482},
            {31050, Double.MAX_VALUE, 0.057}
        });
    }

    private static double calculateKS(double income) {
        return applyBrackets(income, new double[][]{
            {0, 15000, 0.031}, {15000, 30000, 0.0525},
            {30000, Double.MAX_VALUE, 0.057}
        });
    }

    private static double calculateLA(double income) {
        return applyBrackets(income, new double[][]{
            {0, 12500, 0.0185}, {12500, 50000, 0.035},
            {50000, Double.MAX_VALUE, 0.0425}
        });
    }

    private static double calculateME(double income) {
        return applyBrackets(income, new double[][]{
            {0, 24500, 0.058}, {24500, 58050, 0.0675},
            {58050, Double.MAX_VALUE, 0.0715}
        });
    }

    private static double calculateMD(double income) {
        return applyBrackets(income, new double[][]{
            {0, 1000, 0.02}, {1000, 2000, 0.03}, {2000, 3000, 0.04},
            {3000, 100000, 0.0475}, {100000, 125000, 0.05},
            {125000, 150000, 0.0525}, {150000, 250000, 0.055},
            {250000, Double.MAX_VALUE, 0.0575}
        });
    }

    private static double calculateMN(double income) {
        return applyBrackets(income, new double[][]{
            {0, 30070, 0.0535}, {30070, 98760, 0.068},
            {98760, 183340, 0.0785}, {183340, Double.MAX_VALUE, 0.0985}
        });
    }

    private static double calculateMO(double income) {
        return applyBrackets(income, new double[][]{
            {0, 1207, 0.02}, {1207, 2414, 0.025}, {2414, 3621, 0.03},
            {3621, 4828, 0.035}, {4828, 6035, 0.04}, {6035, 7242, 0.045},
            {7242, 8449, 0.05}, {8449, Double.MAX_VALUE, 0.0495}
        });
    }

    private static double calculateMT(double income) {
        return applyBrackets(income, new double[][]{
            {0, 20500, 0.047}, {20500, Double.MAX_VALUE, 0.059}
        });
    }

    private static double calculateNE(double income) {
        return applyBrackets(income, new double[][]{
            {0, 3700, 0.0246}, {3700, 22170, 0.0351},
            {22170, 35730, 0.0501}, {35730, Double.MAX_VALUE, 0.0584}
        });
    }

    private static double calculateNJ(double income) {
        return applyBrackets(income, new double[][]{
            {0, 20000, 0.014}, {20000, 35000, 0.0175}, {35000, 40000, 0.035},
            {40000, 75000, 0.05525}, {75000, 500000, 0.0637},
            {500000, 1000000, 0.0897}, {1000000, Double.MAX_VALUE, 0.1075}
        });
    }

    private static double calculateNM(double income) {
        return applyBrackets(income, new double[][]{
            {0, 5500, 0.017}, {5500, 11000, 0.032},
            {11000, 16000, 0.047}, {16000, 210000, 0.049},
            {210000, Double.MAX_VALUE, 0.059}
        });
    }

    private static double calculateNY(double income) {
        return applyBrackets(income, new double[][]{
            {0, 8500, 0.04}, {8500, 11700, 0.045}, {11700, 13900, 0.0525},
            {13900, 80650, 0.0585}, {80650, 215400, 0.0625},
            {215400, 1077550, 0.0685}, {1077550, 5000000, 0.0965},
            {5000000, 25000000, 0.103}, {25000000, Double.MAX_VALUE, 0.109}
        });
    }

    private static double calculateND(double income) {
        return applyBrackets(income, new double[][]{
            {0, 44725, 0.0}, {44725, 225975, 0.0195},
            {225975, Double.MAX_VALUE, 0.025}
        });
    }

    private static double calculateOH(double income) {
        // Ohio: 0% under $26,050
        if (income <= 26050) return 0;
        return applyBrackets(income, new double[][]{
            {26050, 100000, 0.02765}, {100000, Double.MAX_VALUE, 0.035}
        });
    }

    private static double calculateOK(double income) {
        return applyBrackets(income, new double[][]{
            {0, 1000, 0.0025}, {1000, 2500, 0.0075}, {2500, 3750, 0.0175},
            {3750, 4900, 0.0275}, {4900, 7200, 0.0375},
            {7200, Double.MAX_VALUE, 0.0475}
        });
    }

    private static double calculateOR(double income) {
        return applyBrackets(income, new double[][]{
            {0, 4050, 0.0475}, {4050, 10200, 0.0675},
            {10200, 125000, 0.0875}, {125000, Double.MAX_VALUE, 0.099}
        });
    }

    private static double calculateRI(double income) {
        return applyBrackets(income, new double[][]{
            {0, 73450, 0.0375}, {73450, 166950, 0.0475},
            {166950, Double.MAX_VALUE, 0.0599}
        });
    }

    private static double calculateSC(double income) {
        return applyBrackets(income, new double[][]{
            {0, 3460, 0.0}, {3460, 17330, 0.03},
            {17330, Double.MAX_VALUE, 0.064}
        });
    }

    private static double calculateVT(double income) {
        return applyBrackets(income, new double[][]{
            {0, 45400, 0.0335}, {45400, 110050, 0.066},
            {110050, 229550, 0.076}, {229550, Double.MAX_VALUE, 0.0875}
        });
    }

    private static double calculateVA(double income) {
        return applyBrackets(income, new double[][]{
            {0, 3000, 0.02}, {3000, 5000, 0.03},
            {5000, 17000, 0.05}, {17000, Double.MAX_VALUE, 0.0575}
        });
    }

    private static double calculateWV(double income) {
        return applyBrackets(income, new double[][]{
            {0, 10000, 0.0236}, {10000, 25000, 0.0315},
            {25000, 40000, 0.0354}, {40000, 60000, 0.0472},
            {60000, Double.MAX_VALUE, 0.0512}
        });
    }

    private static double calculateWI(double income) {
        return applyBrackets(income, new double[][]{
            {0, 14320, 0.035}, {14320, 28640, 0.044},
            {28640, 315310, 0.053}, {315310, Double.MAX_VALUE, 0.0765}
        });
    }
}
