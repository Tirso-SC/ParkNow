package icai.dtc.isw.domain;

// Un tramo de calle con plazas SER (zona azul, verde...), según los datos abiertos
// del Ayuntamiento de Madrid. Cada fila del CSV se convierte en un objeto PlazaSER.
public class PlazaSER {

    private String barrio;
    private String distrito;
    private String color;       // "verde", "azul", "naranja", "alta rotación" o "rojo"
    private int numeroPlazas;   // plazas que hay en este tramo de calle
    private double latitud;
    private double longitud;

    public PlazaSER(String barrio, String distrito, String color, int numeroPlazas,
                    double latitud, double longitud) {
        this.barrio = barrio;
        this.distrito = distrito;
        this.color = color;
        this.numeroPlazas = numeroPlazas;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getBarrio() {
        return barrio;
    }

    public String getDistrito() {
        return distrito;
    }

    public String getColor() {
        return color;
    }

    public int getNumeroPlazas() {
        return numeroPlazas;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
