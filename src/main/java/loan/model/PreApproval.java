package loan.model;

public record PreApproval(boolean eligible, int creditScore, long maxMonthlyPayment, String reason) {
}
