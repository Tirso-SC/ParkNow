package icai.dtc.isw.dao;

import icai.dtc.isw.domain.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerDAOTest {

    private static CustomerDAO customerDAO;

    @BeforeAll
    static void setUp() {
        customerDAO = new CustomerDAO();
    }

    @Test
    void testGetClientes() {
        ArrayList<Customer> lista = new ArrayList<>();
        customerDAO.getClientes(lista);
        assertNotNull(lista);
    }

    @Test
    void testGetClientePorNombre() {
        Customer c = customerDAO.getClientePorNombre("Jude Bellingham");
        assertNotNull(c);
        assertNotNull(c.getId());
    }

    @Test
    void testAparcarYDesaparcarCoche() {
        Customer c = customerDAO.getClientePorNombre("Jude Bellingham");
        int id = Integer.parseInt(c.getId());

        boolean aparcado = customerDAO.aparcarCoche(id, 40.4168, -3.7038);
        assertTrue(aparcado);

        boolean desaparcado = customerDAO.desaparcarCoche(id);
        assertTrue(desaparcado);
    }

    @Test
    void testGetAparcamientos() {
        String aparcamientos = customerDAO.getAparcamientos();
        assertNotNull(aparcamientos);
    }

    @Test
    void testGetAparcamientoActivo() {
        Customer c = customerDAO.getClientePorNombre("Jude Bellingham");
        int id = Integer.parseInt(c.getId());

        customerDAO.desaparcarCoche(id);
        customerDAO.aparcarCoche(id, 40.4168, -3.7038);

        try {
            Map<String, Object> aparcamiento = customerDAO.getAparcamientoActivo(id);

            assertNotNull(aparcamiento);
            assertEquals(40.4168, (double) aparcamiento.get("latitud"));
            assertEquals(-3.7038, (double) aparcamiento.get("longitud"));
            assertNotNull(aparcamiento.get("horaInicio"));
        } finally {
            customerDAO.desaparcarCoche(id);
        }
    }
}