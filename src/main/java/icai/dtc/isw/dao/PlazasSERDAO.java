package icai.dtc.isw.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import icai.dtc.isw.domain.PlazaSER;
import icai.dtc.isw.util.ConversorUTM;

/**
 * Lee las plazas SER del archivo CSV del Ayuntamiento (NO de la base de datos).
 * El archivo tiene unas 34.500 filas, así que solo lo leemos la primera vez que
 * alguien lo pide y después guardamos la lista en memoria.
 */
public class PlazasSERDAO {

    private static final String RUTA_CSV = "datos/calles_ser.csv";

    // "static" para que la lista se comparta entre todos los PlazasSERDAO que se creen.
    // Así el CSV se lee una sola vez aunque el servidor cree un controlador por petición.
    private static ArrayList<PlazaSER> plazas = null;

    /**
     * Devuelve todas las plazas SER. La primera vez lee el CSV.
     * "synchronized" evita que dos peticiones a la vez lean el archivo dos veces.
     *
     * @return lista con todas las plazas SER (vacía si no se encuentra el CSV)
     */
    public synchronized ArrayList<PlazaSER> getPlazas() {
        if (plazas == null) {
            plazas = leerCSV();
        }
        return plazas;
    }

    private ArrayList<PlazaSER> leerCSV() {
        ArrayList<PlazaSER> lista = new ArrayList<>();

        // Igual que PropertiesISW: buscamos el archivo dentro de src/main/resources
        InputStream entrada = getClass().getClassLoader().getResourceAsStream(RUTA_CSV);
        if (entrada == null) {
            System.out.println("No se encuentra el archivo " + RUTA_CSV);
            return lista;
        }

        // El CSV del Ayuntamiento viene en ISO-8859-1 (no en UTF-8).
        // Si no lo indicamos, las tildes saldrían mal (por ejemplo "Línea" o "Argüelles")
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.ISO_8859_1))) {

            lector.readLine(); // la primera línea son los nombres de las columnas: la saltamos

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

    // Convierte una línea del CSV en una PlazaSER. Si la línea está mal, devuelve null.
    // Columnas (separadas por ";"):
    // 0 gis_x | 1 gis_y | 2 cod_distrito | 3 distrito | 4 cod_barrio | 5 num_barrio |
    // 6 barrio | 7 calle | 8 numero_finca | 9 color | 10 bateria_linea | 11 numero_plazas
    private PlazaSER leerFila(String linea) {
        try {
            String[] columnas = linea.split(";");
            if (columnas.length < 12) {
                return null; // faltan columnas
            }

            // Las coordenadas usan punto decimal, así que parseDouble las entiende directamente
            double x = Double.parseDouble(columnas[0].trim());
            double y = Double.parseDouble(columnas[1].trim());
            String distrito = columnas[3].trim();
            String barrio = columnas[6].trim();
            String color = traducirColor(columnas[9]);
            int numeroPlazas = Integer.parseInt(columnas[11].trim());

            // Pasamos de UTM (metros) a latitud y longitud (grados)
            double[] latLon = ConversorUTM.utmALatLon(x, y);

            return new PlazaSER(barrio, distrito, color, numeroPlazas, latLon[0], latLon[1]);
        } catch (NumberFormatException ex) {
            return null; // algún número no se puede leer: saltamos la fila
        }
    }

    // En el CSV el color viene como un código RGB seguido del nombre, por ejemplo
    // "077214010 Verde" o "043000255 Azul". Lo pasamos a un texto sencillo.
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
        return texto.trim(); // un color que no conocemos: lo dejamos tal cual
    }
}
