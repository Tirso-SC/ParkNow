package icai.dtc.isw.domain;



public class PlazaSER {

    private String barrio;
    private String distrito;
    private String color;       
    private int numeroPlazas;   
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
