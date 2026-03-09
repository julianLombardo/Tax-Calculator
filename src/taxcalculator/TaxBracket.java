package taxcalculator;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaxBracket {

    public static final double[] RATES = {0.10, 0.12, 0.22, 0.24, 0.32, 0.35, 0.37};

    // Year -> FilingStatus -> thresholds
    private static final Map<Integer, Map<FilingStatus, double[]>> THRESHOLDS = new LinkedHashMap<>();
    private static final Map<Integer, Map<FilingStatus, Double>> STANDARD_DEDUCTIONS = new LinkedHashMap<>();

    static {
        // 2025 brackets
        Map<FilingStatus, double[]> t2025 = new LinkedHashMap<>();
        t2025.put(FilingStatus.SINGLE, new double[]{11925, 48475, 103350, 197300, 250525, 626350, Double.MAX_VALUE});
        t2025.put(FilingStatus.MARRIED_JOINTLY, new double[]{23850, 96950, 206700, 394600, 501050, 751600, Double.MAX_VALUE});
        t2025.put(FilingStatus.MARRIED_SEPARATELY, new double[]{11925, 48475, 103350, 197300, 250525, 375800, Double.MAX_VALUE});
        t2025.put(FilingStatus.HEAD_OF_HOUSEHOLD, new double[]{17000, 64850, 103350, 197300, 250500, 626350, Double.MAX_VALUE});
        THRESHOLDS.put(2025, t2025);

        Map<FilingStatus, Double> d2025 = new LinkedHashMap<>();
        d2025.put(FilingStatus.SINGLE, 15000.0);
        d2025.put(FilingStatus.MARRIED_JOINTLY, 30000.0);
        d2025.put(FilingStatus.MARRIED_SEPARATELY, 15000.0);
        d2025.put(FilingStatus.HEAD_OF_HOUSEHOLD, 22500.0);
        STANDARD_DEDUCTIONS.put(2025, d2025);

        // 2024 brackets
        Map<FilingStatus, double[]> t2024 = new LinkedHashMap<>();
        t2024.put(FilingStatus.SINGLE, new double[]{11600, 47150, 100525, 191950, 243725, 609350, Double.MAX_VALUE});
        t2024.put(FilingStatus.MARRIED_JOINTLY, new double[]{23200, 94300, 201050, 383900, 487450, 731200, Double.MAX_VALUE});
        t2024.put(FilingStatus.MARRIED_SEPARATELY, new double[]{11600, 47150, 100525, 191950, 243725, 365600, Double.MAX_VALUE});
        t2024.put(FilingStatus.HEAD_OF_HOUSEHOLD, new double[]{16550, 63100, 100500, 191950, 243700, 609350, Double.MAX_VALUE});
        THRESHOLDS.put(2024, t2024);

        Map<FilingStatus, Double> d2024 = new LinkedHashMap<>();
        d2024.put(FilingStatus.SINGLE, 14600.0);
        d2024.put(FilingStatus.MARRIED_JOINTLY, 29200.0);
        d2024.put(FilingStatus.MARRIED_SEPARATELY, 14600.0);
        d2024.put(FilingStatus.HEAD_OF_HOUSEHOLD, 21900.0);
        STANDARD_DEDUCTIONS.put(2024, d2024);

        // 2023 brackets
        Map<FilingStatus, double[]> t2023 = new LinkedHashMap<>();
        t2023.put(FilingStatus.SINGLE, new double[]{11000, 44725, 95375, 182100, 231250, 578125, Double.MAX_VALUE});
        t2023.put(FilingStatus.MARRIED_JOINTLY, new double[]{22000, 89450, 190750, 364200, 462500, 693750, Double.MAX_VALUE});
        t2023.put(FilingStatus.MARRIED_SEPARATELY, new double[]{11000, 44725, 95375, 182100, 231250, 346875, Double.MAX_VALUE});
        t2023.put(FilingStatus.HEAD_OF_HOUSEHOLD, new double[]{15700, 59850, 95350, 182100, 231250, 578100, Double.MAX_VALUE});
        THRESHOLDS.put(2023, t2023);

        Map<FilingStatus, Double> d2023 = new LinkedHashMap<>();
        d2023.put(FilingStatus.SINGLE, 13850.0);
        d2023.put(FilingStatus.MARRIED_JOINTLY, 27700.0);
        d2023.put(FilingStatus.MARRIED_SEPARATELY, 13850.0);
        d2023.put(FilingStatus.HEAD_OF_HOUSEHOLD, 20800.0);
        STANDARD_DEDUCTIONS.put(2023, d2023);
    }

    public static double[] getThresholds(FilingStatus status, int year) {
        Map<FilingStatus, double[]> yearMap = THRESHOLDS.getOrDefault(year, THRESHOLDS.get(2025));
        return yearMap.get(status);
    }

    public static double[] getThresholds(FilingStatus status) {
        return getThresholds(status, 2025);
    }

    public static double getStandardDeduction(FilingStatus status, int year) {
        Map<FilingStatus, Double> yearMap = STANDARD_DEDUCTIONS.getOrDefault(year, STANDARD_DEDUCTIONS.get(2025));
        return yearMap.get(status);
    }

    public static int[] getAvailableYears() {
        return new int[]{2025, 2024, 2023};
    }
}
