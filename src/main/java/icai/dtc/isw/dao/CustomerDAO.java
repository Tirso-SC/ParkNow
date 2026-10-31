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

    // Devuelve true si ya hay un usuario registrado con ese email
    public boolean existeEmail(String email) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String consulta = "SELECT id FROM public.usuarios WHERE email = ?";

        try (PreparedStatement pst = con.prepareStatement(consulta)) {
            pst.setString(1, email);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo comprobar el email " + email, ex);
        }
    }

    // Devuelve true si ya hay un usuario registrado con ese nombre
    public boolean existeNombre(String nombre) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String consulta = "SELECT id FROM public.usuarios WHERE nombre = ?";

        try (PreparedStatement pst = con.prepareStatement(consulta)) {
            pst.setString(1, nombre);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo comprobar el nombre " + nombre, ex);
        }
    }

    // Inserta un usuario nuevo. Recibe la contraseña ya cifrada (hash), nunca la original
    public boolean registrarCliente(String nombre, String email, String passwordHash) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String sql = "INSERT INTO public.usuarios (nombre, email, contrasena) VALUES (?, ?, ?)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nombre);
            pst.setString(2, email);
            pst.setString(3, passwordHash);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Busca un usuario con ese email y ese hash de contraseña. Si no hay ninguno devuelve null
    public Customer login(String email, String passwordHash) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        Customer cu = null;
        String consulta = "SELECT id, nombre, email FROM public.usuarios WHERE email = ? AND contrasena = ?";

        try (PreparedStatement pst = con.prepareStatement(consulta)) {
            pst.setString(1, email);
            pst.setString(2, passwordHash);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    cu = new Customer(rs.getString("id"), rs.getString("nombre"), rs.getString("email"));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("No se pudo hacer login con el email " + email, ex);
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

    // Poner la hora de salida al marcharse
    public boolean desaparcarCoche(int idUsuario) {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String sql = "UPDATE public.aparcamientos SET hora_fin = CURRENT_TIMESTAMP " +
                "WHERE id_usuario = ? AND hora_fin IS NULL";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            return pst.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getAparcamientos() {
        Connection con = ConnectionDAO.getInstance().getConnection();
        String sql = "SELECT latitud, longitud, hora_inicio, hora_fin " +
                "FROM public.aparcamientos ORDER BY hora_inicio DESC";
        String texto = "";

        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                if (rs.getTimestamp("hora_fin") == null) {
                    texto += "Coche aparcado en (" + rs.getDouble("latitud") + ", "
                            + rs.getDouble("longitud") + ") desde "
                            + rs.getTimestamp("hora_inicio") + "\n";
                } else {
                    texto += "Aparcamiento libre en (" + rs.getDouble("latitud") + ", "
                            + rs.getDouble("longitud") + ") desde "
                            + rs.getTimestamp("hora_fin") + "\n";
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return texto;
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
