package icai.dtc.isw.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversorUTMTest {

    
    private static final double MARGEN = 0.0001;

    
    
    
    @Test
    void testPuertaDelSol() {
        double[] latLon = ConversorUTM.utmALatLon(440291.28, 4474254.60);
        assertEquals(40.416775, latLon[0], MARGEN);
        assertEquals(-3.703790, latLon[1], MARGEN);
    }

    
    
    @Test
    void testMeridianoCentral() {
        double[] latLon = ConversorUTM.utmALatLon(500000, 4474254.60);
        assertEquals(-3.0, latLon[1], 0.0000001);
    }
}
