package claseseguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class Contrasenas {

    private static final SecureRandom ALEATORIO = new SecureRandom();

    private Contrasenas() {
    }

    public static String hashear(String password) {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + calcular(sal, password);
    }

    public static boolean verificar(String password, String almacenado) {
        if (password == null || almacenado == null) {
            return false;
        }
        String[] partes = almacenado.split(":", 2);
        if (partes.length != 2) {
            return false;
        }
        byte[] sal;
        try {
            sal = Base64.getDecoder().decode(partes[0]);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return MessageDigest.isEqual(
                calcular(sal, password).getBytes(StandardCharsets.UTF_8),
                partes[1].getBytes(StandardCharsets.UTF_8));
    }

    public static boolean esValida(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean letra = false;
        boolean numero = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                letra = true;
            } else if (Character.isDigit(c)) {
                numero = true;
            }
        }
        return letra && numero;
    }

    private static String calcular(byte[] sal, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(sal);
            return Base64.getEncoder().encodeToString(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
