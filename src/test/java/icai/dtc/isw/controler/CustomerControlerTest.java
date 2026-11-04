package icai.dtc.isw.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import icai.dtc.isw.dao.PlazasSERDAO;
import icai.dtc.isw.domain.PlazaSER;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

// Solo probamos los casos en los que registrar devuelve un error
// ANTES de llamar a la base de datos, así estos tests no la usan.
// Los tests de validarUbicacionAparcar tampoco la usan: solo leen el CSV de plazas SER.
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

    // Comprueba que se puede aparcar justo encima de una plaza SER del CSV
    @Test
    void testAparcarEnPlazaSER() {
        PlazaSER plaza = new PlazasSERDAO().getPlazas().get(0);
        String error = controler.validarUbicacionAparcar(plaza.getLatitud(), plaza.getLongitud());
        assertNull(error);
    }

    // Comprueba que no se puede aparcar en medio del parque del Retiro
    @Test
    void testAparcarEnRetiroNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.4150, -3.6840);
        assertNotNull(error);
    }

    // Punto real donde la app dejó aparcar con un servidor antiguo: dentro del Retiro
    // (la plaza SER más cercana está a unos 309 m, en la avenida de Menéndez Pelayo)
    @Test
    void testAparcarEnRetiroInteriorNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.41702, -3.68222);
        assertNotNull(error);
    }

    // Puerta del Sol, junto a la fuente: es peatonal
    // (la plaza SER más cercana está a unos 157 m, en la calle Esparteros)
    @Test
    void testAparcarEnFuenteDeSolNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.41692, -3.70348);
        assertNotNull(error);
    }

    // Comprueba que no se puede aparcar en medio de la Casa de Campo
    @Test
    void testAparcarEnCasaDeCampoNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.4180, -3.7550);
        assertNotNull(error);
    }
}
