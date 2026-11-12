package icai.dtc.isw.dao;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import icai.dtc.isw.domain.PlazaSER;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PlazasSERDAOTest {

    
    @Test
    void testCSVSeCarga() {
        ArrayList<PlazaSER> plazas = new PlazasSERDAO().getPlazas();
        assertFalse(plazas.isEmpty());
    }

    
    @Test
    void testEmbajadoresTienePlazas() {
        ArrayList<PlazaSER> plazas = new PlazasSERDAO().getPlazas();
        int totalEmbajadores = 0;
        for (PlazaSER plaza : plazas) {
            if (plaza.getBarrio().equals("Embajadores")) {
                totalEmbajadores += plaza.getNumeroPlazas();
            }
        }
        assertTrue(totalEmbajadores > 0);
    }

    
    
    @Test
    void testTildesSeLeenBien() {
        boolean encontrado = false;
        for (PlazaSER plaza : new PlazasSERDAO().getPlazas()) {
            if (plaza.getBarrio().equals("Argüelles")) {
                encontrado = true;
                break;
            }
        }
        assertTrue(encontrado);
    }
}
