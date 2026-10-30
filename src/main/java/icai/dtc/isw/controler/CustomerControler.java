package icai.dtc.isw.controler;

import java.util.ArrayList;

import icai.dtc.isw.dao.CustomerDAO;
import icai.dtc.isw.domain.Customer;
import icai.dtc.isw.security.PasswordUtil;

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

    public String getAparcamientos() {
        return customerDAO.getAparcamientos();
    }
}
