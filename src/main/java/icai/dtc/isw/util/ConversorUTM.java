package icai.dtc.isw.util;
















public class ConversorUTM {

    
    
    private static final double RADIO_ECUADOR = 6378137.0;       
    private static final double APLANAMIENTO = 1 / 298.257222101; 

    
    private static final double K0 = 0.9996;

    
    private static final double LONGITUD_CENTRAL_HUSO_30 = -3.0;

    
    public static double[] utmALatLon(double x, double y) {
        
        double e2 = APLANAMIENTO * (2 - APLANAMIENTO);
        double ePrima2 = e2 / (1 - e2);

        
        double xReal = x - 500000.0;

        
        
        double m = y / K0; 
        double mu = m / (RADIO_ECUADOR * (1 - e2 / 4 - 3 * e2 * e2 / 64 - 5 * e2 * e2 * e2 / 256));

        double e1 = (1 - Math.sqrt(1 - e2)) / (1 + Math.sqrt(1 - e2));
        double latPie = mu
                + (3 * e1 / 2 - 27 * Math.pow(e1, 3) / 32) * Math.sin(2 * mu)
                + (21 * e1 * e1 / 16 - 55 * Math.pow(e1, 4) / 32) * Math.sin(4 * mu)
                + (151 * Math.pow(e1, 3) / 96) * Math.sin(6 * mu)
                + (1097 * Math.pow(e1, 4) / 512) * Math.sin(8 * mu);

        
        double senoPie = Math.sin(latPie);
        double cosenoPie = Math.cos(latPie);
        double tangentePie = Math.tan(latPie);

        double c1 = ePrima2 * cosenoPie * cosenoPie;
        double t1 = tangentePie * tangentePie;
        double n1 = RADIO_ECUADOR / Math.sqrt(1 - e2 * senoPie * senoPie);                    
        double r1 = RADIO_ECUADOR * (1 - e2) / Math.pow(1 - e2 * senoPie * senoPie, 1.5);      
        double d = xReal / (n1 * K0); 

        
        double latitud = latPie - (n1 * tangentePie / r1) * (
                d * d / 2
                - (5 + 3 * t1 + 10 * c1 - 4 * c1 * c1 - 9 * ePrima2) * Math.pow(d, 4) / 24
                + (61 + 90 * t1 + 298 * c1 + 45 * t1 * t1 - 252 * ePrima2 - 3 * c1 * c1) * Math.pow(d, 6) / 720);

        
        double desplazamiento = (d
                - (1 + 2 * t1 + c1) * Math.pow(d, 3) / 6
                + (5 - 2 * c1 + 28 * t1 - 3 * c1 * c1 + 8 * ePrima2 + 24 * t1 * t1) * Math.pow(d, 5) / 120)
                / cosenoPie;

        
        double latitudGrados = Math.toDegrees(latitud);
        double longitudGrados = LONGITUD_CENTRAL_HUSO_30 + Math.toDegrees(desplazamiento);

        return new double[]{latitudGrados, longitudGrados};
    }
}
