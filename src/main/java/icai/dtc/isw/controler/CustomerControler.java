package icai.dtc.isw.controler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import icai.dtc.isw.dao.CustomerDAO;
import icai.dtc.isw.dao.PlazasSERDAO;
import icai.dtc.isw.domain.Customer;
import icai.dtc.isw.domain.PlazaSER;
import icai.dtc.isw.security.PasswordUtil;
import icai.dtc.isw.util.Distancia;

public class CustomerControler {
    CustomerDAO customerDAO = new CustomerDAO();

    public void getCustomers(ArrayList<Customer> lista) {
        customerDAO.getClientes(lista);
    }

    public Customer getCustomer(int id) {
        return customerDAO.getCliente(id);
    }

    public Customer getCustomerByName(String name) {
        return customerDAO.getClientePorNombre(name);
    }

    
    
    public String registrar(String nombre, String email, String password) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre no puede estar vacío.";
        }
        if (nombre.trim().length() > 50) {
            return "El nombre no puede tener más de 50 caracteres.";
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return "El correo no es válido.";
        }
        if (password == null || password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres.";
        }

        
        String emailLimpio = email.trim().toLowerCase();

        if (customerDAO.existeEmail(emailLimpio)) {
            return "Ya existe una cuenta con ese correo.";
        }
        if (customerDAO.existeNombre(nombre.trim())) {
            return "Ese nombre de usuario ya está en uso.";
        }

        
        String hash = PasswordUtil.hashear(password);
        boolean ok = customerDAO.registrarCliente(nombre.trim(), emailLimpio, hash);
        if (!ok) {
            return "No se pudo guardar el usuario en la base de datos.";
        }
        return null;
    }

    
    public Customer login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        String hash = PasswordUtil.hashear(password);
        return customerDAO.login(email.trim().toLowerCase(), hash);
    }

    public boolean aparcar(int idUsuario, double latitud, double longitud) {
        return customerDAO.aparcarCoche(idUsuario, latitud, longitud);
    }

    public boolean desaparcar(int idUsuario) {
        return customerDAO.desaparcarCoche(idUsuario);
    }

    public Map<String, Object> getAparcamientoActivo(int idUsuario) {
        return customerDAO.getAparcamientoActivo(idUsuario);
    }

    public String getAparcamientos() {
        return customerDAO.getAparcamientos();
    }

    

    
    public static final double DISTANCIA_MAXIMA_SER_METROS = 300;

    PlazasSERDAO plazasSERDAO = new PlazasSERDAO();

    
    
    
    public static final double DISTANCIA_MAXIMA_CALLE_METROS = 40;

    
    
    
    
    public String validarUbicacionAparcar(double lat, double lon) {
        PlazaSER masCercana = buscarPlazaMasCercana(plazasSERDAO.getPlazas(), lat, lon);
        if (masCercana != null && Distancia.distanciaMetros(lat, lon,
                masCercana.getLatitud(), masCercana.getLongitud()) < DISTANCIA_MAXIMA_CALLE_METROS) {
            return null;
        }
        return "Ahí no se puede aparcar. Elige un punto sobre una calle con plazas de aparcamiento (zona SER).";
    }

    
    
    
    
    public HashMap<String, Object> infoBarrio(double lat, double lon) {
        HashMap<String, Object> info = new HashMap<>();
        ArrayList<PlazaSER> plazas = plazasSERDAO.getPlazas();

        
        PlazaSER masCercana = buscarPlazaMasCercana(plazas, lat, lon);
        if (masCercana == null || Distancia.distanciaMetros(lat, lon,
                masCercana.getLatitud(), masCercana.getLongitud()) > DISTANCIA_MAXIMA_SER_METROS) {
            info.put("error", "Esta zona no tiene plazas reguladas (SER) registradas.");
            return info;
        }
        String barrio = masCercana.getBarrio();

        
        int total = 0;
        int verdes = 0;
        int azules = 0;
        int otras = 0;
        for (PlazaSER plaza : plazas) {
            if (plaza.getBarrio().equals(barrio)) {
                total += plaza.getNumeroPlazas();
                if (plaza.getColor().equals("verde")) {
                    verdes += plaza.getNumeroPlazas();
                } else if (plaza.getColor().equals("azul")) {
                    azules += plaza.getNumeroPlazas();
                } else {
                    otras += plaza.getNumeroPlazas();
                }
            }
        }

        
        
        int coches = 0;
        for (double[] coche : customerDAO.getCochesAparcadosAhora()) {
            PlazaSER plazaCoche = buscarPlazaMasCercana(plazas, coche[0], coche[1]);
            if (plazaCoche != null && plazaCoche.getBarrio().equals(barrio)) {
                coches++;
            }
        }

        
        int libres = Math.max(0, total - coches);

        info.put("barrio", barrio);
        info.put("distrito", masCercana.getDistrito());
        info.put("totalPlazas", total);
        info.put("plazasVerdes", verdes);
        info.put("plazasAzules", azules);
        info.put("plazasOtras", otras);
        info.put("cochesAparcados", coches);
        info.put("plazasLibres", libres);
        return info;
    }

    
    private PlazaSER buscarPlazaMasCercana(ArrayList<PlazaSER> plazas, double lat, double lon) {
        PlazaSER masCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        for (PlazaSER plaza : plazas) {
            double distancia = Distancia.distanciaMetros(lat, lon, plaza.getLatitud(), plaza.getLongitud());
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                masCercana = plaza;
            }
        }
        return masCercana;
    }
}
