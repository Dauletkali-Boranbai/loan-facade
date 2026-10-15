package loan.subsystem;

public class NotificationService {

    public void sendEmail(String address, String message) {
        System.out.println("  [email -> " + address + "] " + message);
    }
}
