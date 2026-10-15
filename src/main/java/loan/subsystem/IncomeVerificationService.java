package loan.subsystem;

import loan.model.Applicant;

public class IncomeVerificationService {

    public double debtToIncomeRatio(Applicant applicant, long newMonthlyPayment) {
        if (applicant.monthlyIncome() <= 0) {
            return Double.POSITIVE_INFINITY;
        }
        long totalDebtPayments = applicant.existingMonthlyDebt() + newMonthlyPayment;
        return (double) totalDebtPayments / applicant.monthlyIncome();
    }

    public long maxAffordablePayment(Applicant applicant, double dtiLimit) {
        long room = (long) (applicant.monthlyIncome() * dtiLimit) - applicant.existingMonthlyDebt();
        return Math.max(0, room);
    }
}
