package icai.dtc.isw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import icai.dtc.isw.domain.Customer;

public class CustomerDAO {

    public void getClientes(ArrayList<Customer> lista) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        try (PreparedStatement pst = con.prepareStatement("SELECT id, nombre FROM public.usuarios");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                lista.add(new Customer(rs.getString("id"), rs.getString("nombre")));
            }

        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudieron recuperar los usuarios de la base de datos", ex);
        }
    }

    public Customer getCliente(int id) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        Customer cu = null;
        String consulta = "SELECT id, nombre FROM public.usuarios WHERE id = ?";

        try (PreparedStatement pst = con.prepareStatement(consulta)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    cu = new Customer(rs.getString("id"), rs.getString("nombre"));
                }
            }

        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo recuperar el usuario con id " + id, ex);
        }
        return cu;
    }

    // Busca el usuario por nombre y, si no existe en la base de datos, lo crea automáticamente
    public Customer getClientePorNombre(String nombre) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        Customer cu = null;
        String consulta = "SELECT id, nombre FROM public.usuarios WHERE nombre = ?";

        try (PreparedStatement pst = con.prepareStatement(consulta)) {
            pst.setString(1, nombre);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    cu = new Customer(rs.getString("id"), rs.getString("nombre"));
                } else {
                    String sqlInsert = "INSERT INTO public.usuarios (nombre) VALUES (?) RETURNING id, nombre";
                    try (PreparedStatement pstIns = con.prepareStatement(sqlInsert)) {
                        pstIns.setString(1, nombre);
                        try (ResultSet rsIns = pstIns.executeQuery()) {
                            if (rsIns.next()) {
                                cu = new Customer(rsIns.getString("id"), rsIns.getString("nombre"));
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo recuperar o crear el usuario con nombre " + nombre, ex);
        }
        return cu;
    }

    // Insertar en la tabla aparcamientos
    public boolean aparcarCoche(int idUsuario, double latitud, double longitud) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String sql = "INSERT INTO public.aparcamientos (id_usuario, latitud, longitud) VALUES (?, ?, ?)";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            pst.setDouble(2, latitud);
            pst.setDouble(3, longitud);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Eliminar el aparcamiento al marcharse para dejar la plaza libre
    public boolean desaparcarCoche(int idUsuario) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String sql = "DELETE FROM public.aparcamientos WHERE id_usuario = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        CustomerDAO customerDAO = new CustomerDAO();
        ArrayList<Customer> lista = new ArrayList<>();
        customerDAO.getClientes(lista);

        for (Customer customer : lista) {
            System.out.println("He leído el id: " + customer.getId() + " con nombre: " + customer.getName());
        }
    }
}
