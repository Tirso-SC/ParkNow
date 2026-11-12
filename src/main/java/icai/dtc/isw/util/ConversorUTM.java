package icai.dtc.isw.util;

/**
 * Convierte coordenadas UTM (las que usa el Ayuntamiento, en metros)
 * a latitud y longitud en grados (las que usa nuestro mapa).
 *
 * <p>¿Qué es UTM? La Tierra se divide en 60 "husos" verticales de 6 grados.
 * Dentro de cada huso, cada punto se indica con dos distancias en metros:</p>
 * <ul>
 *   <li>x (Este): metros desde el meridiano central del huso, al que se le suman
 *       500.000 m para que nunca salgan números negativos.</li>
 *   <li>y (Norte): metros desde el ecuador.</li>
 * </ul>
 * <p>Madrid está en el huso 30 norte, cuyo meridiano central es la longitud -3º.</p>
 *
 * <p>ETRS89 (Europa) y WGS84 (GPS) se diferencian en menos de un metro,
 * así que los consideramos iguales.</p>
 *
 * <p>La fórmula es la "inversa de Transversa de Mercator" del libro de Snyder
 * (USGS, "Map Projections - A Working Manual"). Da precisión de centímetros.</p>
 */
public class ConversorUTM {

    // Datos del elipsoide GRS80 (el de ETRS89): la Tierra no es una esfera perfecta,
    // está un poco "aplastada" por los polos
    private static final double RADIO_ECUADOR = 6378137.0;       // a, en metros
    private static final double APLANAMIENTO = 1 / 298.257222101; // f

    // Factor de escala de UTM en el meridiano central
    private static final double K0 = 0.9996;

    // Huso 30: el meridiano central está en -3 grados
    private static final double LONGITUD_CENTRAL_HUSO_30 = -3.0;

    /**
     * Pasa un punto en UTM (huso 30 norte) a latitud y longitud.
     *
     * @param x coordenada Este en metros (UTM huso 30 norte)
     * @param y coordenada Norte en metros (UTM huso 30 norte)
     * @return array {latitud, longitud} en grados
     */
    public static double[] utmALatLon(double x, double y) {
        // Excentricidad al cuadrado: mide cuánto se aleja el elipsoide de una esfera
        double e2 = APLANAMIENTO * (2 - APLANAMIENTO);
        double ePrima2 = e2 / (1 - e2);

        // 1) Quitamos los 500.000 m que se añadieron a la x
        double xReal = x - 500000.0;

        // 2) Calculamos la "latitud del pie": la latitud que tendría el punto
        //    si estuviera justo en el meridiano central (x = 0)
        double m = y / K0; // distancia sobre el meridiano desde el ecuador
        double mu = m / (RADIO_ECUADOR * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));

        double e1 = (1 - Math.sqrt(1 - e2)) / (1 + Math.sqrt(1 - e2));
        double latPie = mu
                + (3 * e1 / 2 - 27 * Math.pow(e1, 3) / 32) * Math.sin(2 * mu)
                + (21 * e1 * e1 / 16 - 55 * Math.pow(e1, 4) / 32) * Math.sin(4 * mu)
                + (151 * Math.pow(e1, 3) / 96) * Math.sin(6 * mu)
                + (1097 * Math.pow(e1, 4) / 512) * Math.sin(8 * mu);

        // 3) Valores auxiliares en esa latitud del pie
        double senoPie = Math.sin(latPie);
        double cosenoPie = Math.cos(latPie);
        double tangentePie = Math.tan(latPie);

        double c1 = ePrima2 * cosenoPie * cosenoPie;
        double t1 = tangentePie * tangentePie;
        double n1 = RADIO_ECUADOR / Math.sqrt(1 - e2 * senoPie * senoPie);                    // radio de curvatura Este-Oeste
        double r1 = RADIO_ECUADOR * (1 - e2) / Math.pow(1 - e2 * senoPie * senoPie, 1.5);      // radio de curvatura Norte-Sur
        double d = xReal / (n1 * K0); // cuánto se separa el punto del meridiano central

        // 4) Corregimos la latitud del pie según lo lejos que esté el punto del meridiano central
        double latitud = latPie - (n1 * tangentePie / r1) * (
                d * d / 2
                - (5 + 3 * t1 + 10 * c1 - 4 * c1 * c1 - 9 * ePrima2) * Math.pow(d, 4) / 24
                + (61 + 90 * t1 + 298 * c1 + 45 * t1 * t1 - 252 * ePrima2 - 3 * c1 * c1) * Math.pow(d, 6) / 720);

        // 5) La longitud es la del meridiano central más el desplazamiento hacia el Este o el Oeste
        double desplazamiento = (d
                - (1 + 2 * t1 + c1) * Math.pow(d, 3) / 6
                + (5 - 2 * c1 + 28 * t1 - 3 * c1 * c1 + 8 * ePrima2 + 24 * t1 * t1) * Math.pow(d, 5) / 120)
                / cosenoPie;

        // Las fórmulas trabajan en radianes: pasamos el resultado a grados
        double latitudGrados = Math.toDegrees(latitud);
        double longitudGrados = LONGITUD_CENTRAL_HUSO_30 + Math.toDegrees(desplazamiento);

        return new double[]{latitudGrados, longitudGrados};
    }
}
