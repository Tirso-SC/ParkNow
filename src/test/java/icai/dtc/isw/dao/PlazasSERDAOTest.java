package icai.dtc.isw.dao;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import icai.dtc.isw.domain.PlazaSER;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Estos tests solo leen el archivo CSV, no usan la base de datos
class PlazasSERDAOTest {

    // Comprueba que el CSV se carga y tiene filas
    @Test
    void testCSVSeCarga() {
        ArrayList<PlazaSER> plazas = new PlazasSERDAO().getPlazas();
        assertFalse(plazas.isEmpty());
    }

    // Comprueba que existe el barrio de Embajadores (Lavapiés) y que tiene más de 0 plazas
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

    // Comprueba que las tildes se leen bien (el CSV está en ISO-8859-1):
    // el barrio "Argüelles" tiene que aparecer escrito correctamente
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
