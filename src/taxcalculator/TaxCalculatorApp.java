package taxcalculator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;

public class TaxCalculatorApp extends Application {

    private Stage primaryStage;
    private Scene mainScene;
    private boolean isDarkMode = false;

    // Input fields
    private ComboBox<FilingStatus> filingStatusCombo;
    private ComboBox<Integer> yearCombo;
    private ComboBox<String> stateCombo;
    private TextField incomeField;
    private TextField selfEmploymentField;
    private TextField capitalGainsField;
    private Spinner<Integer> childrenSpinner;
    private RadioButton standardRadio;
    private RadioButton itemizedRadio;
    private TextField itemizedField;
    private VBox itemizedBox;

    // Validation error labels
    private Label incomeError;
    private Label itemizedError;
    private Label seError;
    private Label cgError;

    // AI Deduction fields
    private VBox aiSection;
    private TextField apiKeyField;
    private TextArea aiDescriptionArea;
    private TextArea aiResponseArea;
    private Button aiParseButton;

    // Result labels
    private Label taxableIncomeLabel;
    private Label totalTaxLabel;
    private Label effectiveRateLabel;
    private Label marginalRateLabel;
    private Label creditsLabel;
    private Label ficaLabel;
    private Label seTaxLabel;
    private Label capGainsTaxLabel;
    private Label stateTaxLabel;
    private Label takeHomeAnnualLabel;
    private Label takeHomeMonthlyLabel;
    private Label takeHomeBiweeklyLabel;

    private TableView<BracketResult> bracketTable;
    private PieChart pieChart;
    private TitledPane chartPane;
    private Label statusLabel;

    private TaxResult lastResult;
    private TaxInput lastInput;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Tax Calculator Pro");

        loadPreferences();

        try {
            showIntroVideo();
        } catch (Exception e) {
            showMainCalculator();
        }
    }

    // ── Intro Video ─────────────────────────────────────────────────

    private void showIntroVideo() {
        File videoFile = resolveVideoFile();
        if (videoFile == null || !videoFile.exists()) {
            showMainCalculator();
            return;
        }

        Media media = new Media(videoFile.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);

        Label skipLabel = new Label("Click anywhere or press any key to skip");
        skipLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 12px;");

        StackPane videoRoot = new StackPane(mediaView, skipLabel);
        StackPane.setAlignment(skipLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(skipLabel, new Insets(0, 0, 15, 0));
        videoRoot.setStyle("-fx-background-color: black;");

        Scene videoScene = new Scene(videoRoot, 700, 440);

        Runnable transitionToMain = () -> {
            player.stop();
            player.dispose();
            Platform.runLater(this::showMainCalculator);
        };

        videoRoot.setOnMouseClicked(e -> transitionToMain.run());
        videoScene.setOnKeyPressed(e -> transitionToMain.run());

        player.setOnEndOfMedia(() -> {
            player.dispose();
            Platform.runLater(this::showMainCalculator);
        });

        player.setOnError(() -> {
            player.dispose();
            Platform.runLater(this::showMainCalculator);
        });

        mediaView.fitWidthProperty().bind(videoScene.widthProperty());
        mediaView.fitHeightProperty().bind(videoScene.heightProperty());
        mediaView.setPreserveRatio(true);

        primaryStage.setScene(videoScene);
        primaryStage.show();
        player.play();
    }

    private File resolveVideoFile() {
        String[] searchPaths = {
            System.getProperty("app.dir", ""),
            System.getProperty("user.dir")
        };
        for (String base : searchPaths) {
            File f = new File(base, "Logo/Gate way.mp4");
            if (f.exists()) return f;
        }
        return null;
    }

    // ── Main Calculator UI ──────────────────────────────────────────

    private void showMainCalculator() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(24, 28, 20, 28));

        root.getChildren().addAll(
            buildTopBar(),
            buildInputCard(),
            buildButtonBar(),
            buildResultsCard(),
            buildTakeHomeCard(),
            buildBracketSection(),
            buildChartSection(),
            buildFooter()
        );

        VBox.setVgrow(bracketTable, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        mainScene = new Scene(scrollPane, 740, 920);

        // Keyboard shortcut: Enter to calculate
        mainScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !e.isControlDown()) {
                calculate();
            }
        });

        applyTheme();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    // ── Top Bar ─────────────────────────────────────────────────────

    private HBox buildTopBar() {
        VBox titleBox = new VBox(2);
        Label title = new Label("Tax Calculator Pro");
        title.getStyleClass().add("title-label");
        Label subtitle = new Label("Federal & State Income Tax \u2022 2023\u20132025");
        subtitle.getStyleClass().add("subtitle-label");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button darkModeBtn = new Button(isDarkMode ? "\u2600 Light Mode" : "\u263E Dark Mode");
        darkModeBtn.getStyleClass().add("dark-mode-button");
        darkModeBtn.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            darkModeBtn.setText(isDarkMode ? "\u2600 Light Mode" : "\u263E Dark Mode");
            applyTheme();
            savePreferences();
        });

        HBox topBar = new HBox(titleBox, spacer, darkModeBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        return topBar;
    }

    // ── Input Card ──────────────────────────────────────────────────

    private VBox buildInputCard() {
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(12);
        inputGrid.setVgap(10);

        int row = 0;

        // Tax Year
        yearCombo = new ComboBox<>(FXCollections.observableArrayList(2025, 2024, 2023));
        yearCombo.setValue(2025);
        yearCombo.setMaxWidth(Double.MAX_VALUE);
        inputGrid.add(labelWithTooltip("Tax Year:", "Select the tax year for bracket thresholds and standard deduction amounts."), 0, row);
        inputGrid.add(yearCombo, 1, row++);

        // Filing Status
        filingStatusCombo = new ComboBox<>(FXCollections.observableArrayList(FilingStatus.values()));
        filingStatusCombo.setValue(FilingStatus.SINGLE);
        filingStatusCombo.setMaxWidth(Double.MAX_VALUE);
        inputGrid.add(labelWithTooltip("Filing Status:", "Your IRS filing status determines tax bracket thresholds and standard deduction."), 0, row);
        inputGrid.add(filingStatusCombo, 1, row++);

        // Gross Income
        incomeField = new TextField();
        incomeField.setPromptText("e.g. 75,000");
        incomeField.setTextFormatter(CurrencyTextFormatter.create());
        incomeError = createErrorLabel();
        inputGrid.add(labelWithTooltip("Gross Annual Income:", "Total W-2 wages, salary, and tips before any deductions."), 0, row);
        inputGrid.add(new VBox(2, incomeField, incomeError), 1, row++);

        // Self-Employment Income
        selfEmploymentField = new TextField();
        selfEmploymentField.setPromptText("0");
        selfEmploymentField.setTextFormatter(CurrencyTextFormatter.create());
        seError = createErrorLabel();
        inputGrid.add(labelWithTooltip("Self-Employment Income:", "Net profit from self-employment (Schedule C). Subject to 15.3% SE tax."), 0, row);
        inputGrid.add(new VBox(2, selfEmploymentField, seError), 1, row++);

        // Capital Gains
        capitalGainsField = new TextField();
        capitalGainsField.setPromptText("0");
        capitalGainsField.setTextFormatter(CurrencyTextFormatter.create());
        cgError = createErrorLabel();
        inputGrid.add(labelWithTooltip("Long-Term Capital Gains:", "Gains on assets held >1 year. Taxed at preferential 0%/15%/20% rates + possible 3.8% NIIT."), 0, row);
        inputGrid.add(new VBox(2, capitalGainsField, cgError), 1, row++);

        // Children
        childrenSpinner = new Spinner<>(0, 20, 0);
        childrenSpinner.setEditable(true);
        childrenSpinner.setMaxWidth(Double.MAX_VALUE);
        inputGrid.add(labelWithTooltip("Qualifying Children:", "Children under 17 for Child Tax Credit ($2,000/child). Also affects Earned Income Credit."), 0, row);
        inputGrid.add(childrenSpinner, 1, row++);

        // State
        stateCombo = new ComboBox<>();
        for (var entry : StateTax.STATES.entrySet()) {
            stateCombo.getItems().add(entry.getKey() + " - " + entry.getValue());
        }
        stateCombo.setValue("NONE - No State Tax");
        stateCombo.setMaxWidth(Double.MAX_VALUE);
        inputGrid.add(labelWithTooltip("State:", "Estimated state income tax. All 50 states + DC supported."), 0, row);
        inputGrid.add(stateCombo, 1, row++);

        // Deduction Type
        ToggleGroup deductionGroup = new ToggleGroup();
        standardRadio = new RadioButton("Standard Deduction");
        standardRadio.setToggleGroup(deductionGroup);
        standardRadio.setSelected(true);
        itemizedRadio = new RadioButton("Itemized Deduction");
        itemizedRadio.setToggleGroup(deductionGroup);
        HBox radioBox = new HBox(15, standardRadio, itemizedRadio);
        inputGrid.add(labelWithTooltip("Deduction Type:", "Standard is a fixed amount based on filing status. Itemize if your deductions exceed the standard."), 0, row);
        inputGrid.add(radioBox, 1, row++);

        // Itemized section
        itemizedField = new TextField();
        itemizedField.setPromptText("e.g. 20,000");
        itemizedField.setTextFormatter(CurrencyTextFormatter.create());
        itemizedError = createErrorLabel();
        Label itemizedLabel = new Label("Itemized Amount:");
        itemizedLabel.getStyleClass().add("field-label");
        HBox itemizedRow = new HBox(10, itemizedLabel, itemizedField);
        itemizedRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(itemizedField, Priority.ALWAYS);

        aiSection = buildAISection();

        itemizedBox = new VBox(10, new VBox(2, itemizedRow, itemizedError), aiSection);
        itemizedBox.setVisible(false);
        itemizedBox.setManaged(false);
        inputGrid.add(itemizedBox, 0, row, 2, 1);

        deductionGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean show = (newVal == itemizedRadio);
            itemizedBox.setVisible(show);
            itemizedBox.setManaged(show);
        });

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(190);
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        inputGrid.getColumnConstraints().addAll(col0, col1);

        VBox card = new VBox(12, inputGrid);
        card.getStyleClass().add("input-card");
        return card;
    }

    // ── Button Bar ──────────────────────────────────────────────────

    private HBox buildButtonBar() {
        Button calculateButton = new Button("Calculate Tax");
        calculateButton.getStyleClass().add("calculate-button");
        calculateButton.setOnAction(e -> calculate());

        Button resetButton = new Button("Reset");
        resetButton.getStyleClass().add("reset-button");
        resetButton.setOnAction(e -> resetAll());

        Button saveButton = new Button("Save");
        saveButton.getStyleClass().add("save-load-button");
        saveButton.setOnAction(e -> saveInputs());

        Button loadButton = new Button("Load");
        loadButton.getStyleClass().add("save-load-button");
        loadButton.setOnAction(e -> loadInputs());

        Button reportButton = new Button("Generate Report");
        reportButton.getStyleClass().add("report-button");
        reportButton.setOnAction(e -> generateReport());

        // Status indicator
        statusLabel = new Label("");
        statusLabel.getStyleClass().add("footer-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(8, calculateButton, resetButton, spacer, saveButton, loadButton, reportButton);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    // ── Results Card ────────────────────────────────────────────────

    private VBox buildResultsCard() {
        Label resultsTitle = new Label("Tax Summary");
        resultsTitle.getStyleClass().add("section-title");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(8);

        taxableIncomeLabel = createResultLabel();
        totalTaxLabel = createResultLabel();
        effectiveRateLabel = createResultLabel();
        marginalRateLabel = createResultLabel();
        creditsLabel = createResultLabel();
        ficaLabel = createResultLabel();
        seTaxLabel = createResultLabel();
        capGainsTaxLabel = createResultLabel();
        stateTaxLabel = createResultLabel();

        // Two-column results layout
        int r = 0;
        grid.add(resultRow("Taxable Income", taxableIncomeLabel), 0, r);
        grid.add(resultRow("Effective Rate", effectiveRateLabel), 1, r++);
        grid.add(resultRow("Federal Income Tax", totalTaxLabel), 0, r);
        grid.add(resultRow("Marginal Rate", marginalRateLabel), 1, r++);
        grid.add(resultRow("Tax Credits", creditsLabel), 0, r);
        grid.add(resultRow("FICA Tax", ficaLabel), 1, r++);
        grid.add(resultRow("Self-Employment Tax", seTaxLabel), 0, r);
        grid.add(resultRow("Capital Gains Tax", capGainsTaxLabel), 1, r++);
        grid.add(resultRow("State Tax", stateTaxLabel), 0, r);

        ColumnConstraints half = new ColumnConstraints();
        half.setPercentWidth(50);
        grid.getColumnConstraints().addAll(half, half);

        VBox card = new VBox(10, resultsTitle, grid);
        card.getStyleClass().add("results-card");
        return card;
    }

    private VBox resultRow(String labelText, Label valueLabel) {
        Label l = new Label(labelText);
        l.setStyle("-fx-font-size: 12px;");
        HBox row = new HBox(8, l, valueLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return new VBox(row);
    }

    // ── Take-Home Card ──────────────────────────────────────────────

    private VBox buildTakeHomeCard() {
        Label title = new Label("Take-Home Pay");
        title.getStyleClass().add("section-title");

        takeHomeAnnualLabel = new Label("\u2014");
        takeHomeAnnualLabel.getStyleClass().add("take-home-value");

        takeHomeMonthlyLabel = createResultLabel();
        takeHomeBiweeklyLabel = createResultLabel();

        HBox periods = new HBox(30,
            labeledValue("Monthly", takeHomeMonthlyLabel),
            labeledValue("Biweekly", takeHomeBiweeklyLabel)
        );

        VBox card = new VBox(8, title, takeHomeAnnualLabel, periods);
        card.getStyleClass().add("take-home-card");
        return card;
    }

    private VBox labeledValue(String label, Label value) {
        Label l = new Label(label);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #78909C;");
        return new VBox(2, l, value);
    }

    // ── Bracket Table ───────────────────────────────────────────────

    private VBox buildBracketSection() {
        Label title = new Label("Bracket Breakdown");
        title.getStyleClass().add("subsection-title");

        bracketTable = new TableView<>();
        bracketTable.setPlaceholder(new Label("Enter income and click Calculate"));
        bracketTable.setPrefHeight(220);

        TableColumn<BracketResult, String> rateCol = new TableColumn<>("Rate");
        rateCol.setCellValueFactory(d ->
                new SimpleStringProperty(String.format("%.0f%%", d.getValue().getRate() * 100)));
        rateCol.setPrefWidth(60);

        TableColumn<BracketResult, String> rangeCol = new TableColumn<>("Bracket Range");
        rangeCol.setCellValueFactory(d -> {
            double lb = d.getValue().getLowerBound();
            double ub = d.getValue().getUpperBound();
            String upper = (ub == Double.MAX_VALUE) ? "+" : currencyFormat.format(ub);
            return new SimpleStringProperty(currencyFormat.format(lb) + " \u2013 " + upper);
        });
        rangeCol.setPrefWidth(200);

        TableColumn<BracketResult, String> taxableCol = new TableColumn<>("Taxable Amount");
        taxableCol.setCellValueFactory(d ->
                new SimpleStringProperty(currencyFormat.format(d.getValue().getTaxableInBracket())));
        taxableCol.setPrefWidth(130);

        TableColumn<BracketResult, String> taxCol = new TableColumn<>("Tax");
        taxCol.setCellValueFactory(d ->
                new SimpleStringProperty(currencyFormat.format(d.getValue().getTaxInBracket())));
        taxCol.setPrefWidth(110);

        @SuppressWarnings("unchecked")
        var columns = bracketTable.getColumns();
        columns.addAll(rateCol, rangeCol, taxableCol, taxCol);
        bracketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        return new VBox(8, title, bracketTable);
    }

    // ── Pie Chart ───────────────────────────────────────────────────

    private TitledPane buildChartSection() {
        pieChart = new PieChart();
        pieChart.setTitle("Tax Distribution");
        pieChart.getStyleClass().add("chart");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(320);

        chartPane = new TitledPane("Tax Distribution Chart", pieChart);
        chartPane.setExpanded(false);
        return chartPane;
    }

    // ── Footer ──────────────────────────────────────────────────────

    private HBox buildFooter() {
        Label disclaimer = new Label("For informational purposes only. Consult a tax professional for official advice.");
        disclaimer.getStyleClass().add("footer-label");

        Label shortcut = new Label("Press Enter to calculate");
        shortcut.getStyleClass().add("footer-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(shortcut, spacer, disclaimer);
        footer.setPadding(new Insets(8, 0, 0, 0));
        return footer;
    }

    // ── AI Section ──────────────────────────────────────────────────

    private VBox buildAISection() {
        Label aiTitle = new Label("AI Deduction Assistant");
        aiTitle.getStyleClass().add("ai-title");

        Label aiHint = new Label("Describe your deductions in plain English and AI will calculate the total.");
        aiHint.getStyleClass().add("ai-hint");
        aiHint.setWrapText(true);

        Label apiKeyLabel = new Label("Claude API Key:");
        apiKeyField = new PasswordField();
        apiKeyField.setPromptText("sk-ant-...");
        HBox apiKeyRow = new HBox(10, apiKeyLabel, apiKeyField);
        apiKeyRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(apiKeyField, Priority.ALWAYS);

        aiDescriptionArea = new TextArea();
        aiDescriptionArea.setPromptText(
                "e.g. I paid $12,000 in mortgage interest, $4,500 in state income taxes, "
                        + "$3,000 in property taxes, and donated $2,000 to charity.");
        aiDescriptionArea.setPrefRowCount(3);
        aiDescriptionArea.setWrapText(true);

        aiParseButton = new Button("Ask AI to Calculate Deductions");
        aiParseButton.getStyleClass().add("ai-button");
        aiParseButton.setOnAction(e -> runAIParse());

        aiResponseArea = new TextArea();
        aiResponseArea.setEditable(false);
        aiResponseArea.setPrefRowCount(4);
        aiResponseArea.setWrapText(true);
        aiResponseArea.setVisible(false);
        aiResponseArea.setManaged(false);
        aiResponseArea.getStyleClass().add("ai-response");

        VBox section = new VBox(8, aiTitle, aiHint, apiKeyRow, aiDescriptionArea, aiParseButton, aiResponseArea);
        section.getStyleClass().add("ai-section");
        return section;
    }

    private void runAIParse() {
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            showAlert("API Key Required", "Please enter your Claude API key to use the AI assistant.");
            return;
        }

        String description = aiDescriptionArea.getText().trim();
        if (description.isEmpty()) {
            showAlert("Description Required", "Please describe your deductions for the AI to analyze.");
            return;
        }

        aiParseButton.setDisable(true);
        aiParseButton.setText("Analyzing...");
        aiResponseArea.setVisible(true);
        aiResponseArea.setManaged(true);
        aiResponseArea.setText("Sending to Claude...");

        Thread thread = new Thread(() -> {
            try {
                AIDeductionParser.ParseResult result = AIDeductionParser.parse(apiKey, description);
                Platform.runLater(() -> {
                    aiResponseArea.setText(result.getExplanation());
                    if (result.getTotalAmount() >= 0) {
                        itemizedField.setText(String.format("%.0f", result.getTotalAmount()));
                    }
                    aiParseButton.setDisable(false);
                    aiParseButton.setText("Ask AI to Calculate Deductions");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    aiResponseArea.setText("Error: " + ex.getMessage());
                    aiParseButton.setDisable(false);
                    aiParseButton.setText("Ask AI to Calculate Deductions");
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // ── UI Helpers ──────────────────────────────────────────────────

    private Label labelWithTooltip(String text, String tooltipText) {
        Label label = new Label(text);
        label.getStyleClass().add("field-label");

        Label help = new Label(" (?)");
        help.getStyleClass().add("help-tooltip");
        Tooltip tip = new Tooltip(tooltipText);
        tip.setWrapText(true);
        tip.setMaxWidth(280);
        help.setTooltip(tip);

        HBox box = new HBox(0, label, help);
        box.setAlignment(Pos.CENTER_LEFT);

        Label wrapper = new Label();
        wrapper.setGraphic(box);
        return wrapper;
    }

    private Label createErrorLabel() {
        Label label = new Label();
        label.getStyleClass().add("error-label");
        label.setVisible(false);
        label.setManaged(false);
        return label;
    }

    private Label createResultLabel() {
        Label label = new Label("\u2014");
        label.getStyleClass().add("result-value");
        return label;
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void clearErrors() {
        for (Label err : new Label[]{incomeError, itemizedError, seError, cgError}) {
            err.setVisible(false);
            err.setManaged(false);
        }
    }

    // ── Calculate ───────────────────────────────────────────────────

    private double parseField(TextField field, Label errorLabel, String fieldName) {
        String text = field.getText().replaceAll("[,$\\s]", "");
        if (text.isEmpty()) return 0;
        try {
            double val = Double.parseDouble(text);
            if (val < 0) throw new NumberFormatException();
            return val;
        } catch (NumberFormatException e) {
            showError(errorLabel, "Please enter a valid positive number for " + fieldName + ".");
            return -1;
        }
    }

    private void calculate() {
        clearErrors();

        double grossIncome = parseField(incomeField, incomeError, "gross income");
        if (grossIncome < 0) return;
        if (grossIncome == 0 && incomeField.getText().replaceAll("[,$\\s]", "").isEmpty()) {
            showError(incomeError, "Please enter your gross income.");
            return;
        }

        double seIncome = parseField(selfEmploymentField, seError, "self-employment income");
        if (seIncome < 0) return;

        double ltcg = parseField(capitalGainsField, cgError, "capital gains");
        if (ltcg < 0) return;

        double itemizedAmount = 0;
        if (itemizedRadio.isSelected()) {
            itemizedAmount = parseField(itemizedField, itemizedError, "itemized deductions");
            if (itemizedAmount < 0) return;
        }

        lastInput = buildTaxInput(grossIncome, seIncome, ltcg, itemizedAmount);
        lastResult = TaxCalculator.calculateTax(lastInput);

        // Populate results
        taxableIncomeLabel.setText(currencyFormat.format(lastResult.getTaxableIncome()));
        totalTaxLabel.setText(currencyFormat.format(lastResult.getTotalTax()));
        effectiveRateLabel.setText(String.format("%.2f%%", lastResult.getEffectiveRate()));
        marginalRateLabel.setText(String.format("%.0f%%", lastResult.getMarginalRate()));
        creditsLabel.setText("-" + currencyFormat.format(lastResult.getTotalCredits()));
        ficaLabel.setText(currencyFormat.format(lastResult.getFicaTax()));
        seTaxLabel.setText(currencyFormat.format(lastResult.getSelfEmploymentTax()));
        capGainsTaxLabel.setText(currencyFormat.format(lastResult.getCapitalGainsTax()));
        stateTaxLabel.setText(currencyFormat.format(lastResult.getStateTax()));

        takeHomeAnnualLabel.setText(currencyFormat.format(lastResult.getTakeHomePay()) + " / year");
        takeHomeMonthlyLabel.setText(currencyFormat.format(lastResult.getTakeHomePay() / 12));
        takeHomeBiweeklyLabel.setText(currencyFormat.format(lastResult.getTakeHomePay() / 26));

        ObservableList<BracketResult> items =
                FXCollections.observableArrayList(lastResult.getBracketBreakdown());
        bracketTable.setItems(items);

        updatePieChart(lastResult);
    }

    private TaxInput buildTaxInput(double grossIncome, double seIncome, double ltcg, double itemizedAmount) {
        TaxInput input = new TaxInput();
        input.setGrossIncome(grossIncome);
        input.setFilingStatus(filingStatusCombo.getValue());
        input.setDeductionType(standardRadio.isSelected() ? DeductionType.STANDARD : DeductionType.ITEMIZED);
        input.setItemizedAmount(itemizedAmount);
        input.setTaxYear(yearCombo.getValue());
        input.setNumberOfChildren(childrenSpinner.getValue());
        input.setSelfEmploymentIncome(seIncome);
        input.setLongTermCapitalGains(ltcg);

        String stateSelection = stateCombo.getValue();
        input.setStateCode(stateSelection.split(" - ")[0]);
        return input;
    }

    private void updatePieChart(TaxResult result) {
        pieChart.getData().clear();

        if (result.getTotalTax() > 0)
            pieChart.getData().add(new PieChart.Data("Federal Tax " + currencyFormat.format(result.getTotalTax()), result.getTotalTax()));
        if (result.getFicaTax() > 0)
            pieChart.getData().add(new PieChart.Data("FICA " + currencyFormat.format(result.getFicaTax()), result.getFicaTax()));
        if (result.getSelfEmploymentTax() > 0)
            pieChart.getData().add(new PieChart.Data("SE Tax " + currencyFormat.format(result.getSelfEmploymentTax()), result.getSelfEmploymentTax()));
        if (result.getCapitalGainsTax() > 0)
            pieChart.getData().add(new PieChart.Data("Cap Gains " + currencyFormat.format(result.getCapitalGainsTax()), result.getCapitalGainsTax()));
        if (result.getStateTax() > 0)
            pieChart.getData().add(new PieChart.Data("State Tax " + currencyFormat.format(result.getStateTax()), result.getStateTax()));
        if (result.getTakeHomePay() > 0)
            pieChart.getData().add(new PieChart.Data("Take-Home " + currencyFormat.format(result.getTakeHomePay()), result.getTakeHomePay()));
    }

    // ── Reset ───────────────────────────────────────────────────────

    private void resetAll() {
        incomeField.clear();
        selfEmploymentField.clear();
        capitalGainsField.clear();
        itemizedField.clear();
        childrenSpinner.getValueFactory().setValue(0);
        filingStatusCombo.setValue(FilingStatus.SINGLE);
        yearCombo.setValue(2025);
        stateCombo.setValue("NONE - No State Tax");
        standardRadio.setSelected(true);

        clearErrors();

        taxableIncomeLabel.setText("\u2014");
        totalTaxLabel.setText("\u2014");
        effectiveRateLabel.setText("\u2014");
        marginalRateLabel.setText("\u2014");
        creditsLabel.setText("\u2014");
        ficaLabel.setText("\u2014");
        seTaxLabel.setText("\u2014");
        capGainsTaxLabel.setText("\u2014");
        stateTaxLabel.setText("\u2014");
        takeHomeAnnualLabel.setText("\u2014");
        takeHomeMonthlyLabel.setText("\u2014");
        takeHomeBiweeklyLabel.setText("\u2014");

        bracketTable.getItems().clear();
        pieChart.getData().clear();

        lastResult = null;
        lastInput = null;
    }

    // ── Save / Load ─────────────────────────────────────────────────

    private void saveInputs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Tax Inputs");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        chooser.setInitialFileName("tax_inputs.json");
        File file = chooser.showSaveDialog(primaryStage);
        if (file == null) return;

        try {
            double grossIncome = parseFieldSilent(incomeField);
            double seIncome = parseFieldSilent(selfEmploymentField);
            double ltcg = parseFieldSilent(capitalGainsField);
            double itemizedAmount = parseFieldSilent(itemizedField);
            TaxInput input = buildTaxInput(grossIncome, seIncome, ltcg, itemizedAmount);
            TaxFileManager.save(input, file);
        } catch (IOException e) {
            showAlert("Save Error", "Could not save: " + e.getMessage());
        }
    }

    private void loadInputs() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Tax Inputs");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = chooser.showOpenDialog(primaryStage);
        if (file == null) return;

        try {
            TaxInput input = TaxFileManager.load(file);
            populateFromInput(input);
        } catch (IOException e) {
            showAlert("Load Error", "Could not load: " + e.getMessage());
        }
    }

    private void populateFromInput(TaxInput input) {
        incomeField.setText(formatForField(input.getGrossIncome()));
        selfEmploymentField.setText(formatForField(input.getSelfEmploymentIncome()));
        capitalGainsField.setText(formatForField(input.getLongTermCapitalGains()));
        childrenSpinner.getValueFactory().setValue(input.getNumberOfChildren());
        filingStatusCombo.setValue(input.getFilingStatus());
        yearCombo.setValue(input.getTaxYear());

        for (String item : stateCombo.getItems()) {
            if (item.startsWith(input.getStateCode() + " - ")) {
                stateCombo.setValue(item);
                break;
            }
        }

        if (input.getDeductionType() == DeductionType.ITEMIZED) {
            itemizedRadio.setSelected(true);
            itemizedField.setText(formatForField(input.getItemizedAmount()));
        } else {
            standardRadio.setSelected(true);
        }
    }

    private String formatForField(double value) {
        return (value == 0) ? "" : String.format("%.0f", value);
    }

    // ── Report ──────────────────────────────────────────────────────

    private void generateReport() {
        // Calculate first if needed
        if (lastResult == null) {
            calculate();
            if (lastResult == null) return;
        }

        try {
            File reportFile = ReportGenerator.generate(lastInput, lastResult);
            getHostServices().showDocument(reportFile.toURI().toString());
        } catch (IOException e) {
            showAlert("Report Error", "Could not generate report: " + e.getMessage());
        }
    }

    // ── Theme ───────────────────────────────────────────────────────

    private void applyTheme() {
        if (mainScene == null) return;
        mainScene.getStylesheets().clear();
        String css = isDarkMode ? "dark.css" : "light.css";
        var resource = getClass().getResource("styles/" + css);
        if (resource != null) {
            mainScene.getStylesheets().add(resource.toExternalForm());
        }
    }

    private void loadPreferences() {
        try {
            Path prefsPath = Path.of(System.getProperty("user.home"), ".taxcalculator", "prefs.properties");
            if (Files.exists(prefsPath)) {
                Properties props = new Properties();
                try (var reader = Files.newBufferedReader(prefsPath)) { props.load(reader); }
                isDarkMode = "true".equals(props.getProperty("darkMode"));
            }
        } catch (Exception ignored) {}
    }

    private void savePreferences() {
        try {
            Path prefsDir = Path.of(System.getProperty("user.home"), ".taxcalculator");
            Files.createDirectories(prefsDir);
            Properties props = new Properties();
            props.setProperty("darkMode", String.valueOf(isDarkMode));
            try (var writer = Files.newBufferedWriter(prefsDir.resolve("prefs.properties"))) {
                props.store(writer, "Tax Calculator Pro Preferences");
            }
        } catch (Exception ignored) {}
    }

    // ── Utility ─────────────────────────────────────────────────────

    private double parseFieldSilent(TextField field) {
        String text = field.getText().replaceAll("[,$\\s]", "");
        if (text.isEmpty()) return 0;
        try { return Math.max(0, Double.parseDouble(text)); }
        catch (NumberFormatException e) { return 0; }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
