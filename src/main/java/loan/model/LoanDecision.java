package loan.model;

public record LoanDecision(DecisionStatus status, String reason, double annualRatePercent,
                           long monthlyPayment, String referenceId) {

    public boolean isApproved() {
        return status == DecisionStatus.APPROVED;
    }
}
