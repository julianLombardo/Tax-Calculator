package taxcalculator;

import java.io.*;
import java.nio.file.Files;

public class TaxFileManager {

    public static void save(TaxInput input, File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"grossIncome\": ").append(input.getGrossIncome()).append(",\n");
        sb.append("  \"filingStatus\": \"").append(input.getFilingStatus().name()).append("\",\n");
        sb.append("  \"deductionType\": \"").append(input.getDeductionType().name()).append("\",\n");
        sb.append("  \"itemizedAmount\": ").append(input.getItemizedAmount()).append(",\n");
        sb.append("  \"taxYear\": ").append(input.getTaxYear()).append(",\n");
        sb.append("  \"numberOfChildren\": ").append(input.getNumberOfChildren()).append(",\n");
        sb.append("  \"selfEmploymentIncome\": ").append(input.getSelfEmploymentIncome()).append(",\n");
        sb.append("  \"longTermCapitalGains\": ").append(input.getLongTermCapitalGains()).append(",\n");
        sb.append("  \"stateCode\": \"").append(input.getStateCode()).append("\"\n");
        sb.append("}\n");
        Files.writeString(file.toPath(), sb.toString());
    }

    public static TaxInput load(File file) throws IOException {
        String json = Files.readString(file.toPath());
        TaxInput input = new TaxInput();

        input.setGrossIncome(extractDouble(json, "grossIncome"));
        input.setItemizedAmount(extractDouble(json, "itemizedAmount"));
        input.setTaxYear((int) extractDouble(json, "taxYear"));
        input.setNumberOfChildren((int) extractDouble(json, "numberOfChildren"));
        input.setSelfEmploymentIncome(extractDouble(json, "selfEmploymentIncome"));
        input.setLongTermCapitalGains(extractDouble(json, "longTermCapitalGains"));

        String filingStatus = extractString(json, "filingStatus");
        if (filingStatus != null) {
            try { input.setFilingStatus(FilingStatus.valueOf(filingStatus)); } catch (Exception ignored) {}
        }

        String deductionType = extractString(json, "deductionType");
        if (deductionType != null) {
            try { input.setDeductionType(DeductionType.valueOf(deductionType)); } catch (Exception ignored) {}
        }

        String stateCode = extractString(json, "stateCode");
        if (stateCode != null) input.setStateCode(stateCode);

        return input;
    }

    private static double extractDouble(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*([\\d.\\-]+)";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) {
            try { return Double.parseDouble(m.group(1)); } catch (Exception e) { return 0; }
        }
        return 0;
    }

    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        if (m.find()) return m.group(1);
        return null;
    }
}
