# Assignment #4 — Facade Pattern: Mortgage Application

**Topic:** processing a mortgage application in a bank.
A client should not have to know that a mortgage decision needs identity verification,
credit scoring, property appraisal, payment calculation, underwriting rules, archiving
and e-mail notification — and in which order.

## Structure

```
src/main/java/loan
├── model        Applicant, PropertyInfo, LoanRequest, LoanDecision, PreApproval, DecisionStatus
├── subsystem    IdentityVerificationService, CreditScoringService, IncomeVerificationService,
│                PropertyAppraisalService, LoanCalculator, UnderwritingService,
│                ApplicationArchive, NotificationService
├── facade       LoanApplicationFacade
└── client       Main
```

## Pattern roles

| Role | Class(es) |
| --- | --- |
| Complex subsystem (8 classes) | everything in `loan.subsystem` |
| Facade | `LoanApplicationFacade` — `applyForMortgage(...)` and `checkPreApproval(...)` |
| Client | `loan.client.Main` — imports only the facade and plain data records |

Subsystem classes have **no reference to the facade** and can be used directly by an advanced
client, e.g. `new LoanCalculator().monthlyPayment(10_000_000, 12.0, 120)`.

`applyForMortgage` order of work: identity → credit score → appraisal → rate and payment →
debt-to-income → underwriting verdict → archive → e-mail.
`checkPreApproval` uses only a subset (identity, score, income), so the client does not need
to know which services are relevant to which question.

## How to run

```bash
mkdir out
javac -d out $(find src -name '*.java')
java -cp out loan.client.Main
```
Requires JDK 17+ (records are used).

## Clean Code principles used

1. **Meaningful names.** `applyForMortgage`, `checkPreApproval`, `debtToIncomeRatio`,
   `maxAffordablePayment` explain themselves; the client reads like the business process.
2. **Single Responsibility.** Each subsystem class has one reason to change: the calculator
   changes only if the maths changes, `UnderwritingService` only if lending rules change,
   and so on. The facade only coordinates — it contains no formulas and no lending rules.
3. **No leaking of subsystem details.** The public API of the facade accepts and returns only
   model records. The client never sees `CreditScoringService`, scores' internal tables,
   `Verdict`, or the archive.
4. **No magic numbers.** Limits are named constants (`MAX_DTI`, `MAX_LOAN_TO_VALUE`,
   `MIN_CREDIT_SCORE`, `PENALTY_PER_LATE_PAYMENT`, ...).
5. **Proper error handling.** `IdentityVerificationService` throws a specific checked
   `IdentityVerificationException`; the facade catches it at the one place where it can
   decide what to do and turns it into a rejected decision with a clear reason. Business
   rejections are return values, not exceptions.
6. **Small, focused methods.** Every method does one thing and fits on a screen
   (`reject`, `describe` extracted from the main flow).
7. **Dependency injection / testability.** The facade has a second constructor that accepts
   all subsystem objects, so they can be replaced by test doubles. The default constructor
   keeps client code trivial.
8. **Immutable data.** Java records are used for all data passed between layers.

## Defense notes

- **Why Facade and not Adapter/Mediator?** Facade only simplifies access to an existing
  subsystem; it does not convert interfaces (Adapter) and the subsystem classes do not talk
  to each other through it (Mediator).
- **Does the facade add functionality?** No. It adds convenience: one entry point and a
  fixed order of calls. All behaviour is still in the subsystem.
- **Can the subsystem be used without the facade?** Yes, by design — nothing in the
  subsystem depends on the facade.
- **Risk:** a facade can turn into a "god object". Here it is kept small by delegating every
  calculation and rule, and by splitting into several facades if an unrelated feature
  (e.g. loan repayment) is needed.
- **What changes if a rule changes?** Only `UnderwritingService`; the facade and client are
  untouched.
