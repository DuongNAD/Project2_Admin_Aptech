import org.mindrot.jbcrypt.BCrypt;
public class TestBcrypt {
    public static void main(String[] args) {
        String pass = "admin123";
        String hash = "$2a$10$EIFzOM8E0S0QkR1S2z1l1eB1yOOT005P7.i8E0bC.00A.7E0T/";
        System.out.println("Matches? " + BCrypt.checkpw(pass, hash));
        System.out.println("New hash: " + BCrypt.hashpw(pass, BCrypt.gensalt()));
    }
}
