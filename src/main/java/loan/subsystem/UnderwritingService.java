package loan.subsystem;

public class UnderwritingService {

    public static final double MAX_DTI = 0.50;
    public static final double MAX_LOAN_TO_VALUE = 0.80;
    public static final int MIN_CREDIT_SCORE = 550;

    public record Verdict(boolean approved, String reason) { }

    public Verdict evaluate(int creditScore, double debtToIncome, double loanToValue) {
        if (creditScore < MIN_CREDIT_SCORE) {
            return new Verdict(false, "Credit score " + creditScore + " is below " + MIN_CREDIT_SCORE);
        }
        if (debtToIncome > MAX_DTI) {
            return new Verdict(false, String.format("Debt-to-income %.0f%% exceeds %.0f%%",
                    debtToIncome * 100, MAX_DTI * 100));
        }
        if (loanToValue > MAX_LOAN_TO_VALUE) {
            return new Verdict(false, String.format("Loan-to-value %.0f%% exceeds %.0f%%",
                    loanToValue * 100, MAX_LOAN_TO_VALUE * 100));
        }
        return new Verdict(true, "All lending criteria are met");
    }
}
