package taxcalculator;

import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import java.text.DecimalFormat;

public class CurrencyTextFormatter {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###");

    public static TextFormatter<String> create() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // Allow empty
            if (newText.isEmpty()) return change;
            // Strip existing commas for validation
            String stripped = newText.replaceAll(",", "");
            // Allow only digits and optional decimal
            if (!stripped.matches("\\d*\\.?\\d*")) return null;
            // Re-format with commas
            if (!stripped.isEmpty() && !stripped.equals(".")) {
                try {
                    String[] parts = stripped.split("\\.", -1);
                    long wholePart = parts[0].isEmpty() ? 0 : Long.parseLong(parts[0]);
                    String formatted = FORMATTER.format(wholePart);
                    if (parts.length > 1) {
                        formatted += "." + parts[1];
                    }
                    change.setText(formatted);
                    change.setRange(0, change.getControlText().length());
                    change.setCaretPosition(formatted.length());
                    change.setAnchor(formatted.length());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return change;
        };
        return new TextFormatter<>(filter);
    }

    public static double parse(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return Double.parseDouble(text.replaceAll("[,$\\s]", ""));
    }
}
