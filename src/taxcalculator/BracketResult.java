package taxcalculator;

public class BracketResult {
    private final double rate;
    private final double lowerBound;
    private final double upperBound;
    private final double taxableInBracket;
    private final double taxInBracket;

    public BracketResult(double rate, double lowerBound, double upperBound,
                         double taxableInBracket, double taxInBracket) {
        this.rate = rate;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.taxableInBracket = taxableInBracket;
        this.taxInBracket = taxInBracket;
    }

    public double getRate() { return rate; }
    public double getLowerBound() { return lowerBound; }
    public double getUpperBound() { return upperBound; }
    public double getTaxableInBracket() { return taxableInBracket; }
    public double getTaxInBracket() { return taxInBracket; }
}
