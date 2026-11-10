package icai.dtc.isw.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversorUTMTest {

    // Margen de error en grados: 0.0001 grados son unos 10 metros
    private static final double MARGEN = 0.0001;

    // Comprueba que la Puerta del Sol (kilómetro cero) se convierte bien.
    // En UTM huso 30 está en x = 440291.28, y = 4474254.60,
    // que corresponde a latitud 40.416775 y longitud -3.703790
    @Test
    void testPuertaDelSol() {
        double[] latLon = ConversorUTM.utmALatLon(440291.28, 4474254.60);
        assertEquals(40.416775, latLon[0], MARGEN);
        assertEquals(-3.703790, latLon[1], MARGEN);
    }

    // Comprueba que un punto en el meridiano central del huso (x = 500000)
    // tiene exactamente la longitud central del huso 30, que es -3 grados
    @Test
    void testMeridianoCentral() {
        double[] latLon = ConversorUTM.utmALatLon(500000, 4474254.60);
        assertEquals(-3.0, latLon[1], 0.0000001);
    }
}
