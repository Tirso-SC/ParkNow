package icai.dtc.isw.domain;

/**
 * Un tramo de calle con plazas SER (zona azul, verde...), según los datos abiertos
 * del Ayuntamiento de Madrid. Cada fila del CSV se convierte en un objeto PlazaSER.
 */
public class PlazaSER {

    private String barrio;
    private String distrito;
    private String color;       // "verde", "azul", "naranja", "alta rotación" o "rojo"
    private int numeroPlazas;   // plazas que hay en este tramo de calle
    private double latitud;
    private double longitud;

    /**
     * Crea un tramo de calle con plazas SER.
     *
     * @param barrio nombre del barrio
     * @param distrito nombre del distrito
     * @param color color de las plazas: "verde", "azul", "naranja", "alta rotación" o "rojo"
     * @param numeroPlazas número de plazas que hay en este tramo
     * @param latitud latitud del tramo, en grados
     * @param longitud longitud del tramo, en grados
     */
    public PlazaSER(String barrio, String distrito, String color, int numeroPlazas,
                    double latitud, double longitud) {
        this.barrio = barrio;
        this.distrito = distrito;
        this.color = color;
        this.numeroPlazas = numeroPlazas;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    /**
     * Devuelve el barrio del tramo.
     *
     * @return nombre del barrio
     */
    public String getBarrio() {
        return barrio;
    }

    /**
     * Devuelve el distrito del tramo.
     *
     * @return nombre del distrito
     */
    public String getDistrito() {
        return distrito;
    }

    /**
     * Devuelve el color de las plazas del tramo.
     *
     * @return "verde", "azul", "naranja", "alta rotación" o "rojo"
     */
    public String getColor() {
        return color;
    }

    /**
     * Devuelve cuántas plazas hay en el tramo.
     *
     * @return número de plazas
     */
    public int getNumeroPlazas() {
        return numeroPlazas;
    }

    /**
     * Devuelve la latitud del tramo.
     *
     * @return latitud en grados
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * Devuelve la longitud del tramo.
     *
     * @return longitud en grados
     */
    public double getLongitud() {
        return longitud;
    }
}
