package loan.client;

import loan.facade.LoanApplicationFacade;
import loan.model.Applicant;
import loan.model.LoanDecision;
import loan.model.LoanRequest;
import loan.model.PreApproval;
import loan.model.PropertyInfo;

public class Main {

    public static void main(String[] args) {
        LoanApplicationFacade bank = new LoanApplicationFacade();

        System.out.println("=== 1. Reliable client, normal request ===");
        print(bank.applyForMortgage(
                new Applicant("Aigerim Nurlanova", "111111111111", 900_000, 100_000, "aigerim@mail.kz"),
                new PropertyInfo("Astana, Kabanbay Batyr 12", 40_000_000),
                new LoanRequest(25_000_000, 240)));

        System.out.println("\n=== 2. Bad credit history ===");
        print(bank.applyForMortgage(
                new Applicant("Ruslan Omarov", "333333333333", 800_000, 50_000, "ruslan@mail.kz"),
                new PropertyInfo("Almaty, Abay 5", 30_000_000),
                new LoanRequest(15_000_000, 180)));

        System.out.println("\n=== 3. Loan is too big for the property ===");
        print(bank.applyForMortgage(
                new Applicant("Dana Sarsen", "222222222222", 1_500_000, 0, "dana@mail.kz"),
                new PropertyInfo("Shymkent, Tauke Khan 7", 20_000_000),
                new LoanRequest(19_000_000, 240)));

        System.out.println("\n=== 4. Invalid national ID ===");
        print(bank.applyForMortgage(
                new Applicant("Unknown Person", "12345", 500_000, 0, "unknown@mail.kz"),
                new PropertyInfo("Astana, Mangilik El 1", 25_000_000),
                new LoanRequest(10_000_000, 120)));

        System.out.println("\n=== 5. Pre-approval check ===");
        PreApproval preApproval = bank.checkPreApproval(
                new Applicant("Aigerim Nurlanova", "111111111111", 900_000, 100_000, "aigerim@mail.kz"));
        System.out.printf("  %s | score %d | max payment %,d KZT%n",
                preApproval.reason(), preApproval.creditScore(), preApproval.maxMonthlyPayment());
    }

    private static void print(LoanDecision decision) {
        System.out.printf("  -> %s (%s) rate=%.1f%% payment=%,d KZT ref=%s%n",
                decision.status(), decision.reason(), decision.annualRatePercent(),
                decision.monthlyPayment(), decision.referenceId());
    }
}
