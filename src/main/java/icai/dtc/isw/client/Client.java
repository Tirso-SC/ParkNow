package icai.dtc.isw.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import icai.dtc.isw.configuration.PropertiesISW;
import icai.dtc.isw.domain.Customer;
import icai.dtc.isw.message.Message;

public class Client {
	private String host;
	private int port;

	public Client(String host, int port) {
		this.host=host;
		this.port=port;
	}
	public Client() {
		this.host = PropertiesISW.getInstance().getProperty("host");
		this.port = Integer.parseInt(PropertiesISW.getInstance().getProperty("port"));
	}
	public HashMap<String, Object> sentMessage(String Context, HashMap<String, Object> session) {
		System.out.println("Host: "+host+" port"+port);
		
		
		Message mensajeEnvio=new Message();
		Message mensajeVuelta=new Message();
		mensajeEnvio.setContext(Context);
		mensajeEnvio.setSession(session);
		this.sent(mensajeEnvio,mensajeVuelta);

		if (mensajeVuelta.getContext().isEmpty()) {
			throw new IllegalStateException("No se ha podido conectar con el servidor. ¿Está arrancado?");
		}
		if (mensajeVuelta.getContext().equals("/errorServidor")) {
			throw new IllegalStateException((String) mensajeVuelta.getSession().get("errorServidor"));
		}

		
		switch (mensajeVuelta.getContext()) {
			case "/getCustomersResponse":
				ArrayList<Customer> customerList=(ArrayList<Customer>)(mensajeVuelta.getSession().get("Customer"));
				 for (Customer customer : customerList) {			
						System.out.println("He leído el id: "+customer.getId()+" con nombre: "+customer.getName());
					} 
				break;
			case "/getCustomerResponse":
				session=mensajeVuelta.getSession();
				Customer customer =(Customer) (session.get("Customer"));
				if (customer!=null) {
					System.out.println("He leído el id: " + customer.getId() + " con nombre: " + customer.getName());
				}else {
					System.out.println("No se ha recuperado nada de la base de datos");
				}
				break;
			case "/loginResponse":
			case "/registroResponse":
				session=mensajeVuelta.getSession();
				break;
			case "/aparcarResponse":
			case "/desaparcarResponse":
				session=mensajeVuelta.getSession();
				break;
			case "/getAparcamientosResponse":
			case "/getAparcamientoActivoResponse":
				session=mensajeVuelta.getSession();
				break;
			case "/infoBarrioResponse":
				session=mensajeVuelta.getSession();
				break;
			default:

				System.out.println("\nError a la vuelta");
				break;
		}
		return session;
	}
	

	public void sent(Message messageOut, Message messageIn) {
		try {
			System.out.println("Connecting to host " + host + " on port " + port + ".");

			Socket echoSocket = null;
			OutputStream out = null;
			InputStream in = null;

			try {
				echoSocket = new Socket(host, port);
				in = echoSocket.getInputStream();
				out = echoSocket.getOutputStream();
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
				
				objectOutputStream.writeObject(messageOut);
				
		        ObjectInputStream objectInputStream = new ObjectInputStream(in);
		        Message msg=(Message)objectInputStream.readObject();
		        messageIn.setContext(msg.getContext());
		        messageIn.setSession(msg.getSession());
			} catch (UnknownHostException e) {
				System.err.println("Unknown host: " + host);
				return;
			} catch (IOException e) {
				System.err.println("Unable to get streams from server");
				if (echoSocket != null) echoSocket.close();
				return;
			}

			out.close();
			in.close();			
			echoSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}