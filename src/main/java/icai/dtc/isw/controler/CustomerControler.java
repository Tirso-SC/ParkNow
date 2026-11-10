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

    // Valida los datos y registra al usuario.
    // Devuelve null si todo ha ido bien, o un texto explicando el error
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

        // Guardamos el email en minúsculas para que "Ana@Mail.com" y "ana@mail.com" sean el mismo
        String emailLimpio = email.trim().toLowerCase();

        if (customerDAO.existeEmail(emailLimpio)) {
            return "Ya existe una cuenta con ese correo.";
        }
        if (customerDAO.existeNombre(nombre.trim())) {
            return "Ese nombre de usuario ya está en uso.";
        }

        // Nunca guardamos la contraseña tal cual, solo su hash
        String hash = PasswordUtil.hashear(password);
        boolean ok = customerDAO.registrarCliente(nombre.trim(), emailLimpio, hash);
        if (!ok) {
            return "No se pudo guardar el usuario en la base de datos.";
        }
        return null;
    }

    // Cifra la contraseña y busca un usuario con ese email y ese hash
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

    // ---------- Plazas SER por barrio (datos abiertos del Ayuntamiento) ----------

    // Si la plaza SER más cercana está más lejos que esto, consideramos que la zona no tiene SER
    public static final double DISTANCIA_MAXIMA_SER_METROS = 300;

    PlazasSERDAO plazasSERDAO = new PlazasSERDAO();

    // Devuelve la información del barrio donde está el punto (lat, lon):
    // barrio, distrito, plazas SER (total, verdes, azules, otras), coches de ParkNow
    // aparcados ahora y una estimación de plazas libres.
    // Si no hay plazas SER cerca, devuelve solo la clave "error".
    public HashMap<String, Object> infoBarrio(double lat, double lon) {
        HashMap<String, Object> info = new HashMap<>();
        ArrayList<PlazaSER> plazas = plazasSERDAO.getPlazas();

        // 1) Buscamos la plaza SER más cercana al punto
        PlazaSER masCercana = buscarPlazaMasCercana(plazas, lat, lon);
        if (masCercana == null || Distancia.distanciaMetros(lat, lon,
                masCercana.getLatitud(), masCercana.getLongitud()) > DISTANCIA_MAXIMA_SER_METROS) {
            info.put("error", "Esta zona no tiene plazas reguladas (SER) registradas.");
            return info;
        }
        String barrio = masCercana.getBarrio();

        // 2) Sumamos todas las plazas de ese barrio, separadas por color
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

        // 3) Contamos los coches de ParkNow aparcados ahora en ese barrio.
        //    Para saber en qué barrio está cada coche, miramos su plaza SER más cercana
        int coches = 0;
        for (double[] coche : customerDAO.getCochesAparcadosAhora()) {
            PlazaSER plazaCoche = buscarPlazaMasCercana(plazas, coche[0], coche[1]);
            if (plazaCoche != null && plazaCoche.getBarrio().equals(barrio)) {
                coches++;
            }
        }

        // 4) Estimación de plazas libres (nunca puede ser negativa)
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

    // Recorre todas las plazas y devuelve la que está más cerca del punto (o null si la lista está vacía)
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
