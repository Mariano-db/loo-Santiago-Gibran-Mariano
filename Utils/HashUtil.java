package Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class HashUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private HashUtil() {
    }

    public static String generarSal() {
        byte[] sal = new byte[16];
        RANDOM.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal);
    }

    public static String hashear(String password, String sal) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(sal));
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no esta disponible", e);
        }
    }

    public static boolean coinciden(String hashA, String hashB) {
        return MessageDigest.isEqual(
                hashA.getBytes(StandardCharsets.UTF_8),
                hashB.getBytes(StandardCharsets.UTF_8));
    }
}
