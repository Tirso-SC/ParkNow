package icai.dtc.isw.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import icai.dtc.isw.dao.PlazasSERDAO;
import icai.dtc.isw.domain.PlazaSER;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomerControlerTest {
    private CustomerControler controler;

    @BeforeEach
    void setUp() {
        controler = new CustomerControler();
    }

    @Test
    void testRegistrarNombreVacio() {
        String error = controler.registrar("", "ana@gmail.com", "Password123");
        assertNotNull(error);
    }

    @Test
    void testRegistrarNombreDemasiadoLargo() {
        String nombreLargo = "a".repeat(51);
        String error = controler.registrar(nombreLargo, "ana@gmail.com", "Password123");
        assertNotNull(error);
    }

    @Test
    void testRegistrarEmailSinArroba() {
        String error = controler.registrar("Ana", "anagmail.com", "Password123");
        assertNotNull(error);
    }

    @Test
    void testRegistrarEmailSinPunto() {
        String error = controler.registrar("Ana", "ana@gmailcom", "Password123");
        assertNotNull(error);
    }

    @Test
    void testRegistrarPasswordCorta() {
        String error = controler.registrar("Ana", "ana@gmail.com", "abc123");
        assertNotNull(error);
    }

    @Test
    void testAparcarEnPlazaSER() {
        PlazaSER plaza = new PlazasSERDAO().getPlazas().get(0);
        String error = controler.validarUbicacionAparcar(plaza.getLatitud(), plaza.getLongitud());
        assertNull(error);
    }

    @Test
    void testAparcarEnRetiroNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.4150, -3.6840);
        assertNotNull(error);
    }

    @Test
    void testAparcarEnRetiroInteriorNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.41702, -3.68222);
        assertNotNull(error);
    }

    @Test
    void testAparcarEnFuenteDeSolNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.41692, -3.70348);
        assertNotNull(error);
    }

    @Test
    void testAparcarEnCasaDeCampoNoPermitido() {
        String error = controler.validarUbicacionAparcar(40.4180, -3.7550);
        assertNotNull(error);
    }
}
