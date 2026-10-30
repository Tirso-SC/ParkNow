package icai.dtc.isw.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    // Comprueba que verificar acepta la contraseña correcta
    @Test
    void testVerificarContrasenaCorrecta() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertTrue(PasswordUtil.verificar("miPassword123", hash));
    }

    // Comprueba que verificar rechaza una contraseña distinta
    @Test
    void testVerificarContrasenaIncorrecta() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertFalse(PasswordUtil.verificar("otraPassword", hash));
    }

    // Comprueba que el hash SHA-256 en hexadecimal tiene 64 caracteres
    @Test
    void testHashTiene64Caracteres() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertEquals(64, hash.length());
    }

    // Comprueba que la misma contraseña da siempre el mismo hash
    @Test
    void testMismaContrasenaMismoHash() {
        String hash1 = PasswordUtil.hashear("miPassword123");
        String hash2 = PasswordUtil.hashear("miPassword123");
        assertEquals(hash1, hash2);
    }

    // Comprueba que dos contraseñas distintas dan hashes distintos
    @Test
    void testContrasenasDistintasHashesDistintos() {
        String hash1 = PasswordUtil.hashear("miPassword123");
        String hash2 = PasswordUtil.hashear("otraPassword");
        assertNotEquals(hash1, hash2);
    }

    // Comprueba que verificar devuelve false si el hash guardado es null
    @Test
    void testVerificarHashNull() {
        assertFalse(PasswordUtil.verificar("miPassword123", null));
    }
}
