package taxcalculator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.Locale;

public class ReportGenerator {

    private static final NumberFormat CF = NumberFormat.getCurrencyInstance(Locale.US);

    public static File generate(TaxInput input, TaxResult result) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html><head><meta charset='UTF-8'>\n");
        html.append("<title>Tax Report ").append(input.getTaxYear()).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; color: #333; }\n");
        html.append("h1 { color: #1565C0; border-bottom: 2px solid #1565C0; padding-bottom: 10px; }\n");
        html.append("h2 { color: #424242; margin-top: 30px; }\n");
        html.append("table { border-collapse: collapse; width: 100%; margin: 15px 0; }\n");
        html.append("th, td { padding: 10px 15px; text-align: left; border-bottom: 1px solid #ddd; }\n");
        html.append("th { background: #E3F2FD; color: #1565C0; }\n");
        html.append("tr:hover { background: #F5F5F5; }\n");
        html.append(".summary { background: #E8F5E9; padding: 20px; border-radius: 8px; margin: 20px 0; }\n");
        html.append(".summary h2 { color: #2E7D32; margin-top: 0; }\n");
        html.append(".big-number { font-size: 28px; font-weight: bold; color: #2E7D32; }\n");
        html.append(".meta { color: #757575; font-size: 14px; }\n");
        html.append(".footer { margin-top: 40px; padding-top: 20px; border-top: 1px solid #ddd; color: #999; font-size: 12px; }\n");
        html.append("</style></head><body>\n");

        html.append("<h1>Federal Income Tax Report</h1>\n");
        html.append("<p class='meta'>Tax Year: <strong>").append(input.getTaxYear()).append("</strong> | ");
        html.append("Filing Status: <strong>").append(input.getFilingStatus().getDisplayName()).append("</strong> | ");
        html.append("State: <strong>").append(StateTax.STATES.getOrDefault(input.getStateCode(), "None")).append("</strong></p>\n");

        // Income Summary
        html.append("<h2>Income Summary</h2>\n");
        html.append("<table>");
        html.append("<tr><td>Gross Income</td><td>").append(CF.format(result.getGrossIncome())).append("</td></tr>");
        html.append("<tr><td>Deduction (").append(input.getDeductionType().getDisplayName()).append(")</td><td>").append(CF.format(result.getDeduction())).append("</td></tr>");
        html.append("<tr><td><strong>Taxable Income</strong></td><td><strong>").append(CF.format(result.getTaxableIncome())).append("</strong></td></tr>");
        html.append("</table>\n");

        // Tax Breakdown
        html.append("<h2>Tax Breakdown</h2>\n");
        html.append("<table>");
        html.append("<tr><td>Federal Income Tax</td><td>").append(CF.format(result.getTotalTax())).append("</td></tr>");
        html.append("<tr><td>Tax Credits</td><td>-").append(CF.format(result.getTotalCredits())).append("</td></tr>");
        html.append("<tr><td>FICA Tax</td><td>").append(CF.format(result.getFicaTax())).append("</td></tr>");
        if (result.getSelfEmploymentTax() > 0)
            html.append("<tr><td>Self-Employment Tax</td><td>").append(CF.format(result.getSelfEmploymentTax())).append("</td></tr>");
        if (result.getCapitalGainsTax() > 0)
            html.append("<tr><td>Capital Gains Tax</td><td>").append(CF.format(result.getCapitalGainsTax())).append("</td></tr>");
        if (result.getStateTax() > 0)
            html.append("<tr><td>State Tax</td><td>").append(CF.format(result.getStateTax())).append("</td></tr>");
        html.append("</table>\n");

        // Bracket Breakdown
        html.append("<h2>Bracket Breakdown</h2>\n");
        html.append("<table><tr><th>Rate</th><th>Bracket Range</th><th>Taxable Amount</th><th>Tax</th></tr>\n");
        for (BracketResult br : result.getBracketBreakdown()) {
            String upper = (br.getUpperBound() == Double.MAX_VALUE) ? "+" : CF.format(br.getUpperBound());
            html.append("<tr><td>").append(String.format("%.0f%%", br.getRate() * 100)).append("</td>");
            html.append("<td>").append(CF.format(br.getLowerBound())).append(" \u2013 ").append(upper).append("</td>");
            html.append("<td>").append(CF.format(br.getTaxableInBracket())).append("</td>");
            html.append("<td>").append(CF.format(br.getTaxInBracket())).append("</td></tr>\n");
        }
        html.append("</table>\n");

        // Take-Home Summary
        html.append("<div class='summary'>\n");
        html.append("<h2>Take-Home Pay</h2>\n");
        html.append("<p class='big-number'>").append(CF.format(result.getTakeHomePay())).append(" / year</p>\n");
        html.append("<p>").append(CF.format(result.getTakeHomePay() / 12)).append(" / month | ");
        html.append(CF.format(result.getTakeHomePay() / 26)).append(" / biweekly</p>\n");
        html.append("<p class='meta'>Effective Tax Rate: ").append(String.format("%.2f%%", result.getEffectiveRate()));
        html.append(" | Marginal Rate: ").append(String.format("%.0f%%", result.getMarginalRate())).append("</p>\n");
        html.append("</div>\n");

        html.append("<div class='footer'>Generated by US Federal Income Tax Calculator. For informational purposes only.</div>\n");
        html.append("</body></html>\n");

        File tempFile = File.createTempFile("tax_report_", ".html");
        Files.writeString(tempFile.toPath(), html.toString());
        return tempFile;
    }
}
