package icai.dtc.isw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import icai.dtc.isw.domain.Customer;

public class CustomerDAO {

	public void getClientes(ArrayList<Customer> lista) {
		Connection con=ConnectionDAO.getInstance().getConnection();
		try (PreparedStatement pst = con.prepareStatement("SELECT id, nombre FROM public.usuarios");
                ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
            	lista.add(new Customer(rs.getString("id"),rs.getString("nombre")));
            }

        } catch (SQLException ex) {

            throw new IllegalStateException("No se pudieron recuperar los usuarios de la base de datos", ex);
        }

	}
	public Customer getCliente(int id) {
		Connection con=ConnectionDAO.getInstance().getConnection();
		Customer cu=null;
		String consulta = "SELECT id, nombre FROM public.usuarios WHERE id = ?";

		try (PreparedStatement pst = con.prepareStatement(consulta)) {
			// Asignar el valor del parámetro
			pst.setInt(1, id);  // El primer parámetro "?" se reemplaza por el valor de 'id'

			try (ResultSet rs = pst.executeQuery()) {
				// Procesar el resultado
				if (rs.next()) {
					cu = new Customer(rs.getString("id"), rs.getString("nombre"));
				}
			}

		} catch (SQLException ex) {
			throw new IllegalStateException("No se pudo recuperar el usuario con id " + id, ex);
		}
		return cu;
		//return new Customer("1","Atilano");
	}
	
	public static void main(String[] args) {
		
		CustomerDAO customerDAO=new CustomerDAO();
		ArrayList<Customer> lista= new ArrayList<>();
		customerDAO.getClientes(lista);
		
		
		 for (Customer customer : lista) {			
			System.out.println("He leído el id: "+customer.getId()+" con nombre: "+customer.getName());
		}
		
	
	}

}
