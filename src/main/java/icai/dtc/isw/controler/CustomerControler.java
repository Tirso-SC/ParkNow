package icai.dtc.isw.controler;

import java.util.ArrayList;

import icai.dtc.isw.dao.CustomerDAO;
import icai.dtc.isw.domain.Customer;

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

    public boolean aparcar(int idUsuario, double latitud, double longitud) {
        return customerDAO.aparcarCoche(idUsuario, latitud, longitud);
    }

    public boolean desaparcar(int idUsuario) {
        return customerDAO.desaparcarCoche(idUsuario);
    }
}
