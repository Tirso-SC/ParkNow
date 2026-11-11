package icai.dtc.isw.util;

/**
 * Calcula la distancia en metros entre dos puntos dados por latitud y longitud.
 *
 * <p>Usamos la fórmula de Haversine: considera que la Tierra es una esfera y calcula
 * la distancia "en línea recta sobre la superficie" (como volaría un pájaro).
 * Para distancias dentro de Madrid el error es de unos pocos metros, más que suficiente.</p>
 */
public class Distancia {

    // Radio medio de la Tierra en metros
    private static final double RADIO_TIERRA_METROS = 6371000.0;

    /**
     * Calcula la distancia entre dos puntos.
     *
     * @param lat1 latitud del primer punto, en grados
     * @param lon1 longitud del primer punto, en grados
     * @param lat2 latitud del segundo punto, en grados
     * @param lon2 longitud del segundo punto, en grados
     * @return distancia entre los dos puntos, en metros
     */
    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        // 1) Diferencias de latitud y longitud, pasadas a radianes
        double difLat = Math.toRadians(lat2 - lat1);
        double difLon = Math.toRadians(lon2 - lon1);

        // 2) Parte central de la fórmula. "a" va de 0 (mismo punto) a 1 (puntos opuestos del planeta)
        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(difLon / 2) * Math.sin(difLon / 2);

        // 3) "c" es el ángulo (en radianes) entre los dos puntos visto desde el centro de la Tierra
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 4) Ángulo por radio = longitud del arco, es decir, la distancia
        return RADIO_TIERRA_METROS * c;
    }
}
