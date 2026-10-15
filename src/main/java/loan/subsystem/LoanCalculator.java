package loan.subsystem;

public class LoanCalculator {

    public double annualRatePercentFor(int creditScore) {
        if (creditScore >= 700) {
            return 12.0;
        }
        if (creditScore >= 620) {
            return 15.0;
        }
        return 19.0;
    }

    public long monthlyPayment(long principal, double annualRatePercent, int termMonths) {
        double monthlyRate = annualRatePercent / 100 / 12;
        double payment = principal * monthlyRate / (1 - Math.pow(1 + monthlyRate, -termMonths));
        return Math.round(payment);
    }
}
