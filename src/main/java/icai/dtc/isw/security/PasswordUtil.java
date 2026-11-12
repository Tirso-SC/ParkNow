package icai.dtc.isw.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Utilidades para no guardar nunca las contraseñas en texto plano.
// En la base de datos se guarda el "hash": una huella de la contraseña
// de la que no se puede recuperar la contraseña original.
public class PasswordUtil {

    // Devuelve el hash SHA-256 de la contraseña escrito en hexadecimal (64 caracteres)
    public static String hashear(String password) {
        try {
            // 1. Pedimos a Java el algoritmo SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // 2. Convertimos la contraseña a bytes y calculamos su hash
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 3. Pasamos cada byte a dos caracteres hexadecimales (por ejemplo 10 -> "0a")
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException ex) {
            // No debería pasar nunca: todas las versiones de Java incluyen SHA-256
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }

    // Comprueba una contraseña: la hasheamos y la comparamos con el hash guardado.
    // Si la contraseña es la misma, el hash sale siempre igual.
    public static boolean verificar(String password, String hashGuardado) {
        if (password == null || hashGuardado == null) {
            return false;
        }
        return hashear(password).equals(hashGuardado);
    }
}
