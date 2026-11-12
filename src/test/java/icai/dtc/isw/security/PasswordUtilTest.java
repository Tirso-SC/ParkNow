package icai.dtc.isw.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {
    @Test
    void testVerificarContrasenaCorrecta() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertTrue(PasswordUtil.verificar("miPassword123", hash));
    }

    @Test
    void testVerificarContrasenaIncorrecta() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertFalse(PasswordUtil.verificar("otraPassword", hash));
    }

    @Test
    void testHashTiene64Caracteres() {
        String hash = PasswordUtil.hashear("miPassword123");
        assertEquals(64, hash.length());
    }

    @Test
    void testMismaContrasenaMismoHash() {
        String hash1 = PasswordUtil.hashear("miPassword123");
        String hash2 = PasswordUtil.hashear("miPassword123");
        assertEquals(hash1, hash2);
    }

    @Test
    void testContrasenasDistintasHashesDistintos() {
        String hash1 = PasswordUtil.hashear("miPassword123");
        String hash2 = PasswordUtil.hashear("otraPassword");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testVerificarHashNull() {
        assertFalse(PasswordUtil.verificar("miPassword123", null));
    }
}
