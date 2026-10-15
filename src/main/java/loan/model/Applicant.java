package loan.model;

public record Applicant(String fullName, String nationalId, long monthlyIncome,
                        long existingMonthlyDebt, String email) {
}
