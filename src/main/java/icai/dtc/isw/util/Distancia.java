package icai.dtc.isw.util;

public class Distancia {
    private static final double RADIO_TIERRA_METROS = 6371000.0;

    public static double distanciaMetros(double lat1, double lon1, double lat2, double lon2) {
        double difLat = Math.toRadians(lat2 - lat1);
        double difLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(difLat / 2) * Math.sin(difLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(difLon / 2) * Math.sin(difLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIO_TIERRA_METROS * c;
    }
}
