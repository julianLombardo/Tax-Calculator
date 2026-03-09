# Tax Calculator Pro

A comprehensive desktop application for calculating US federal and state income taxes, built with Java and JavaFX.

## Features

- **Federal Income Tax** — Progressive bracket calculation (10%–37%) with detailed bracket breakdown
- **FICA Tax** — Social Security & Medicare with wage base limits and additional Medicare tax
- **Self-Employment Tax** — Full SE tax calculation with W-2 wage coordination
- **Capital Gains Tax** — Long-term capital gains rates (0%/15%/20%) plus Net Investment Income Tax (NIIT)
- **State Income Tax** — All 50 states + DC with accurate 2023–2025 brackets
- **Tax Credits** — Child Tax Credit and Earned Income Tax Credit with phase-outs
- **AI Deduction Assistant** — Describe deductions in plain English and let Claude AI parse them automatically
- **Report Generation** — Export detailed HTML tax reports viewable in any browser
- **Save/Load** — Save and reload tax scenarios as JSON files
- **Dark & Light Themes** — Toggle between themes with persistent preferences

## Supported Tax Years

2023, 2024, 2025

## Filing Statuses

- Single
- Married Filing Jointly
- Married Filing Separately
- Head of Household

## Requirements

- Java 11 or higher
- macOS (the run script auto-downloads JavaFX SDK)

## Getting Started

### Run from Source

```bash
chmod +x run.sh
./run.sh
```

The script will automatically download the JavaFX SDK if not present, compile the source, and launch the application.

### Install from DMG

A pre-built macOS installer is available in the `dist/` folder:

```
dist/Tax Calculator Pro-1.0.0.dmg
```

## Project Structure

```
├── src/taxcalculator/
│   ├── Main.java                 # Application entry point
│   ├── TaxCalculatorApp.java     # JavaFX UI and application logic
│   ├── TaxCalculator.java        # Core federal tax calculation engine
│   ├── TaxInput.java             # Input data model
│   ├── TaxResult.java            # Result data model
│   ├── TaxBracket.java           # Tax bracket definitions (2023–2025)
│   ├── BracketResult.java        # Per-bracket calculation results
│   ├── FilingStatus.java         # Filing status enum
│   ├── DeductionType.java        # Deduction type enum
│   ├── FICATax.java              # FICA tax calculations
│   ├── SelfEmploymentTax.java    # Self-employment tax calculations
│   ├── StateTax.java             # State tax rates and calculations
│   ├── CapitalGainsTax.java      # Capital gains tax calculations
│   ├── TaxCredits.java           # Tax credit calculations
│   ├── AIDeductionParser.java    # Claude AI integration for deductions
│   ├── ReportGenerator.java      # HTML report generation
│   ├── TaxFileManager.java       # JSON save/load functionality
│   ├── CurrencyTextFormatter.java# Currency input formatting
│   └── styles/
│       ├── dark.css              # Dark theme stylesheet
│       └light.css               # Light theme stylesheet
├── dist/                         # Pre-built distribution
├── run.sh                        # Build and run script
└── README.md
```

## Usage

1. Enter your gross annual income (W-2 wages)
2. Optionally add self-employment income and/or capital gains
3. Select your filing status, tax year, and state
4. Choose standard or itemized deductions
5. Click **Calculate** to see your full tax breakdown
6. View bracket details, pie chart visualization, and take-home pay
7. Generate an HTML report or save your inputs for later

## License

This project is for educational and informational purposes. Tax calculations are estimates and should not be used as professional tax advice.
