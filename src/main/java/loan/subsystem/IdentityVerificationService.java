package loan.subsystem;

import loan.model.Applicant;

import java.util.Set;

public class IdentityVerificationService {

    private static final Set<String> BLACKLIST = Set.of("999999999999");

    public void verify(Applicant applicant) throws IdentityVerificationException {
        if (applicant.fullName() == null || applicant.fullName().isBlank()) {
            throw new IdentityVerificationException("Full name is missing");
        }
        if (applicant.nationalId() == null || !applicant.nationalId().matches("\\d{12}")) {
            throw new IdentityVerificationException("National ID must contain exactly 12 digits");
        }
        if (BLACKLIST.contains(applicant.nationalId())) {
            throw new IdentityVerificationException("Applicant is on the bank blacklist");
        }
    }
}
