package icai.dtc.isw.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistanciaTest {

    // Coordenadas de dos puntos conocidos del centro de Madrid
    private static final double SOL_LAT = 40.416775;
    private static final double SOL_LON = -3.703790;
    private static final double PLAZA_MAYOR_LAT = 40.415511;
    private static final double PLAZA_MAYOR_LON = -3.707401;

    // Comprueba que la distancia de un punto a sí mismo es 0
    @Test
    void testMismoPuntoDistanciaCero() {
        double d = Distancia.distanciaMetros(SOL_LAT, SOL_LON, SOL_LAT, SOL_LON);
        assertEquals(0.0, d, 0.000001);
    }

    // Comprueba que ir de A a B mide lo mismo que ir de B a A
    @Test
    void testDistanciaSimetrica() {
        double ida = Distancia.distanciaMetros(SOL_LAT, SOL_LON, PLAZA_MAYOR_LAT, PLAZA_MAYOR_LON);
        double vuelta = Distancia.distanciaMetros(PLAZA_MAYOR_LAT, PLAZA_MAYOR_LON, SOL_LAT, SOL_LON);
        assertEquals(ida, vuelta, 0.000001);
    }

    // Comprueba que de la Puerta del Sol a la Plaza Mayor hay entre 300 y 600 metros
    @Test
    void testSolPlazaMayor() {
        double d = Distancia.distanciaMetros(SOL_LAT, SOL_LON, PLAZA_MAYOR_LAT, PLAZA_MAYOR_LON);
        assertTrue(d > 300 && d < 600, "La distancia calculada ha sido " + d + " metros");
    }
}
