package icai.dtc.isw.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import icai.dtc.isw.configuration.PropertiesISW;
import icai.dtc.isw.controler.CustomerControler;
import icai.dtc.isw.domain.Customer;
import icai.dtc.isw.message.Message;

public class SocketServer extends Thread {
    public static int port = Integer.parseInt(PropertiesISW.getInstance().getProperty("port"));

    protected Socket socket;

    private SocketServer(Socket socket) {
        this.socket = socket;
        // Configure connections
        System.out.println("New client connected from " + socket.getInetAddress().getHostAddress());
        start();
    }

    public void run() {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            
            // First read the object that has been sent
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            Message mensajeIn = (Message) objectInputStream.readObject();
            
            // Object to return information 
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
            Message mensajeOut = new Message();
            HashMap<String, Object> session = mensajeIn.getSession();
            CustomerControler customerControler;

            switch (mensajeIn.getContext()) {
                case "/getCustomers":
                    customerControler = new CustomerControler();
                    ArrayList<Customer> lista = new ArrayList<Customer>();
                    customerControler.getCustomers(lista);
                    mensajeOut.setContext("/getCustomersResponse");
                    session.put("Customer", lista);
                    mensajeOut.setSession(session);
                    objectOutputStream.writeObject(mensajeOut);                 
                    break;

                case "/getCustomer":
                    int id = (int) session.get("id");
                    customerControler = new CustomerControler();
                    Customer cu = customerControler.getCustomer(id);
                    if (cu != null) {
                        System.out.println("id:" + cu.getId());
                    } else {
                        System.out.println("No encontrado en la base de datos");
                    }

                    mensajeOut.setContext("/getCustomerResponse");
                    session.put("Customer", cu);
                    mensajeOut.setSession(session);
                    objectOutputStream.writeObject(mensajeOut);
                    break;

                case "/login":
                    // Nuevo caso para iniciar sesión por nombre
                    String nombre = (String) session.get("nombre");
                    customerControler = new CustomerControler();
                    Customer cuLogin = customerControler.getCustomerByName(nombre);
                    
                    if (cuLogin != null) {
                        System.out.println("Login correcto - Usuario: " + cuLogin.getName());
                    } else {
                        System.out.println("Login fallido - Usuario no encontrado: " + nombre);
                    }

                    mensajeOut.setContext("/loginResponse");
                    session.put("Customer", cuLogin);
                    mensajeOut.setSession(session);
                    objectOutputStream.writeObject(mensajeOut);
                    break;
                
                default:
                    System.out.println("\nParámetro no encontrado");
                    break;
            }

        } catch (IOException ex) {
            System.out.println("Unable to get streams from client");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("SocketServer Example - Listening port " + port);
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            while (true) {
                new SocketServer(server.accept());
            }
        } catch (IOException ex) {
            System.out.println("Unable to start server.");
        } finally {
            try {
                if (server != null)
                    server.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}