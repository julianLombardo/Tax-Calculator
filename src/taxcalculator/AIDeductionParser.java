package taxcalculator;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIDeductionParser {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";

    /**
     * Sends a natural language description of deductions to Claude and extracts the total amount.
     * Returns the parsed total deduction amount, or -1 if parsing fails.
     */
    public static ParseResult parse(String apiKey, String description) throws Exception {
        String systemPrompt = "You are a US tax assistant. The user will describe their itemized deductions. "
                + "Your job is to identify each deduction, estimate or extract the dollar amount, "
                + "and return a clear breakdown plus the total. "
                + "IMPORTANT: On the very last line of your response, write ONLY the total as a number "
                + "in this exact format: TOTAL: <number> (e.g. TOTAL: 24500). No dollar signs, no commas.";

        String requestBody = """
                {
                  "model": "claude-haiku-4-5-20251001",
                  "max_tokens": 1024,
                  "system": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": "Here are my deductions: %s"
                    }
                  ]
                }
                """.formatted(
                escapeJson(systemPrompt),
                escapeJson(description)
        );

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("API error (HTTP " + response.statusCode() + "): " + response.body());
        }

        // Extract the text content from the response JSON
        String body = response.body();
        String text = extractTextField(body);

        // Parse the TOTAL line
        double total = extractTotal(text);

        return new ParseResult(text, total);
    }

    private static String extractTextField(String json) {
        // Find "text": "..." in the response - simple extraction
        Pattern p = Pattern.compile("\"text\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1)
                    .replace("\\n", "\n")
                    .replace("\\\"", "\"")
                    .replace("\\\\", "\\");
        }
        throw new RuntimeException("Could not parse API response");
    }

    private static double extractTotal(String text) {
        Pattern p = Pattern.compile("TOTAL:\\s*([\\d.]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        double total = -1;
        while (m.find()) {
            total = Double.parseDouble(m.group(1));
        }
        return total;
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static class ParseResult {
        private final String explanation;
        private final double totalAmount;

        public ParseResult(String explanation, double totalAmount) {
            this.explanation = explanation;
            this.totalAmount = totalAmount;
        }

        public String getExplanation() { return explanation; }
        public double getTotalAmount() { return totalAmount; }
    }
}
