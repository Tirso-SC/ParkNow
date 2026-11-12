package icai.dtc.isw.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import icai.dtc.isw.domain.PlazaSER;
import icai.dtc.isw.util.ConversorUTM;

public class PlazasSERDAO {
    private static final String RUTA_CSV = "datos/calles_ser.csv";

    private static ArrayList<PlazaSER> plazas = null;

    public synchronized ArrayList<PlazaSER> getPlazas() {
        if (plazas == null) {
            plazas = leerCSV();
        }
        return plazas;
    }

    private ArrayList<PlazaSER> leerCSV() {
        ArrayList<PlazaSER> lista = new ArrayList<>();

        InputStream entrada = getClass().getClassLoader().getResourceAsStream(RUTA_CSV);
        if (entrada == null) {
            System.out.println("No se encuentra el archivo " + RUTA_CSV);
            return lista;
        }

        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.ISO_8859_1))) {
            lector.readLine();

            String linea;
            while ((linea = lector.readLine()) != null) {
                PlazaSER plaza = leerFila(linea);
                if (plaza != null) {
                    lista.add(plaza);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    private PlazaSER leerFila(String linea) {
        try {
            String[] columnas = linea.split(";");
            if (columnas.length < 12) {
                return null;
            }

            double x = Double.parseDouble(columnas[0].trim());
            double y = Double.parseDouble(columnas[1].trim());
            String distrito = columnas[3].trim();
            String barrio = columnas[6].trim();
            String color = traducirColor(columnas[9]);
            int numeroPlazas = Integer.parseInt(columnas[11].trim());

            double[] latLon = ConversorUTM.utmALatLon(x, y);

            return new PlazaSER(barrio, distrito, color, numeroPlazas, latLon[0], latLon[1]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String traducirColor(String colorCSV) {
        String texto = colorCSV.toLowerCase();
        if (texto.contains("verde")) {
            return "verde";
        } else if (texto.contains("azul")) {
            return "azul";
        } else if (texto.contains("naranja")) {
            return "naranja";
        } else if (texto.contains("alta")) {
            return "alta rotación";
        } else if (texto.contains("rojo")) {
            return "rojo";
        }
        return texto.trim();
    }
}
