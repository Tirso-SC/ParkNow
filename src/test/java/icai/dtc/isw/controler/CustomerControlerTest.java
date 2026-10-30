package icai.dtc.isw.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Solo probamos los casos en los que registrar devuelve un error
// ANTES de llamar a la base de datos, así estos tests no la usan.
class CustomerControlerTest {

    private CustomerControler controler;

    @BeforeEach
    void setUp() {
        controler = new CustomerControler();
    }

    // Comprueba que un nombre vacío da error
    @Test
    void testRegistrarNombreVacio() {
        String error = controler.registrar("", "ana@gmail.com", "Password123");
        assertNotNull(error);
    }

    // Comprueba que un nombre de más de 50 caracteres da error
    @Test
    void testRegistrarNombreDemasiadoLargo() {
        String nombreLargo = "a".repeat(51);
        String error = controler.registrar(nombreLargo, "ana@gmail.com", "Password123");
        assertNotNull(error);
    }

    // Comprueba que un correo sin "@" da error
    @Test
    void testRegistrarEmailSinArroba() {
        String error = controler.registrar("Ana", "anagmail.com", "Password123");
        assertNotNull(error);
    }

    // Comprueba que un correo sin "." da error
    @Test
    void testRegistrarEmailSinPunto() {
        String error = controler.registrar("Ana", "ana@gmailcom", "Password123");
        assertNotNull(error);
    }

    // Comprueba que una contraseña de menos de 8 caracteres da error
    @Test
    void testRegistrarPasswordCorta() {
        String error = controler.registrar("Ana", "ana@gmail.com", "abc123");
        assertNotNull(error);
    }
}
