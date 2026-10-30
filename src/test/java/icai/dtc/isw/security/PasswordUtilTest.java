package icai.dtc.isw.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
