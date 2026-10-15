package loan.subsystem;

import loan.model.Applicant;

import java.util.Map;

public class CreditScoringService {

    private static final int BASE_SCORE = 650;
    private static final int PENALTY_PER_LATE_PAYMENT = 40;
    private static final int BONUS_PER_CLOSED_LOAN = 15;
    private static final int MIN_SCORE = 300;
    private static final int MAX_SCORE = 850;

    private record CreditHistory(int latePayments, int closedLoans) { }

    private final Map<String, CreditHistory> bureau = Map.of(
            "111111111111", new CreditHistory(0, 4),
            "222222222222", new CreditHistory(1, 1),
            "333333333333", new CreditHistory(6, 0));

    public int scoreFor(Applicant applicant) {
        CreditHistory history = bureau.getOrDefault(applicant.nationalId(), new CreditHistory(0, 0));
        int score = BASE_SCORE
                - history.latePayments() * PENALTY_PER_LATE_PAYMENT
                + history.closedLoans() * BONUS_PER_CLOSED_LOAN;
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
