package loan.facade;

import loan.model.Applicant;
import loan.model.DecisionStatus;
import loan.model.LoanDecision;
import loan.model.LoanRequest;
import loan.model.PreApproval;
import loan.model.PropertyInfo;
import loan.subsystem.ApplicationArchive;
import loan.subsystem.CreditScoringService;
import loan.subsystem.IdentityVerificationException;
import loan.subsystem.IdentityVerificationService;
import loan.subsystem.IncomeVerificationService;
import loan.subsystem.LoanCalculator;
import loan.subsystem.NotificationService;
import loan.subsystem.PropertyAppraisalService;
import loan.subsystem.UnderwritingService;
import loan.subsystem.UnderwritingService.Verdict;


public class LoanApplicationFacade {

    private static final String NO_RATE_REASON_PREFIX = "Identity check failed: ";

    private final IdentityVerificationService identityService;
    private final CreditScoringService creditService;
    private final IncomeVerificationService incomeService;
    private final PropertyAppraisalService appraisalService;
    private final LoanCalculator calculator;
    private final UnderwritingService underwriting;
    private final ApplicationArchive archive;
    private final NotificationService notifications;

    public LoanApplicationFacade() {
        this(new IdentityVerificationService(), new CreditScoringService(),
                new IncomeVerificationService(), new PropertyAppraisalService(),
                new LoanCalculator(), new UnderwritingService(),
                new ApplicationArchive(), new NotificationService());
    }

    public LoanApplicationFacade(IdentityVerificationService identityService,
                                 CreditScoringService creditService,
                                 IncomeVerificationService incomeService,
                                 PropertyAppraisalService appraisalService,
                                 LoanCalculator calculator,
                                 UnderwritingService underwriting,
                                 ApplicationArchive archive,
                                 NotificationService notifications) {
        this.identityService = identityService;
        this.creditService = creditService;
        this.incomeService = incomeService;
        this.appraisalService = appraisalService;
        this.calculator = calculator;
        this.underwriting = underwriting;
        this.archive = archive;
        this.notifications = notifications;
    }

    public LoanDecision applyForMortgage(Applicant applicant, PropertyInfo property, LoanRequest request) {
        try {
            identityService.verify(applicant);
        } catch (IdentityVerificationException e) {
            return reject(applicant, NO_RATE_REASON_PREFIX + e.getMessage());
        }

        int creditScore = creditService.scoreFor(applicant);
        long propertyValue = appraisalService.appraise(property);
        double rate = calculator.annualRatePercentFor(creditScore);
        long payment = calculator.monthlyPayment(request.amount(), rate, request.termMonths());
        double dti = incomeService.debtToIncomeRatio(applicant, payment);
        double loanToValue = (double) request.amount() / propertyValue;

        Verdict verdict = underwriting.evaluate(creditScore, dti, loanToValue);
        DecisionStatus status = verdict.approved() ? DecisionStatus.APPROVED : DecisionStatus.REJECTED;

        String referenceId = archive.save(applicant.nationalId() + " " + status + " " + verdict.reason());
        LoanDecision decision = new LoanDecision(status, verdict.reason(), rate, payment, referenceId);
        notifications.sendEmail(applicant.email(), describe(decision));
        return decision;
    }

    public PreApproval checkPreApproval(Applicant applicant) {
        try {
            identityService.verify(applicant);
        } catch (IdentityVerificationException e) {
            return new PreApproval(false, 0, 0, NO_RATE_REASON_PREFIX + e.getMessage());
        }

        int creditScore = creditService.scoreFor(applicant);
        long maxPayment = incomeService.maxAffordablePayment(applicant, UnderwritingService.MAX_DTI);
        boolean eligible = creditScore >= UnderwritingService.MIN_CREDIT_SCORE && maxPayment > 0;
        String reason = eligible ? "Pre-approved" : "Not eligible for a mortgage at the moment";
        return new PreApproval(eligible, creditScore, maxPayment, reason);
    }

    private LoanDecision reject(Applicant applicant, String reason) {
        String referenceId = archive.save(applicant.nationalId() + " REJECTED " + reason);
        LoanDecision decision = new LoanDecision(DecisionStatus.REJECTED, reason, 0, 0, referenceId);
        notifications.sendEmail(applicant.email(), describe(decision));
        return decision;
    }

    private String describe(LoanDecision decision) {
        if (decision.isApproved()) {
            return String.format("Application %s approved: %.1f%% per year, %,d KZT per month.",
                    decision.referenceId(), decision.annualRatePercent(), decision.monthlyPayment());
        }
        return "Application " + decision.referenceId() + " rejected. " + decision.reason();
    }
}
