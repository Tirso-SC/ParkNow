package icai.dtc.isw.ui;

import icai.dtc.isw.client.Client;
import icai.dtc.isw.domain.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;

public class JVentana extends JFrame {

    private JTextField txtUsuario;

    public static void main(String[] args) {
        new JVentana();
    }

    public JVentana() {
        super("Park Now");

        // Panel principal contenedor con margen alrededor (arriba, izquierda, abajo, derecha)
        JPanel pnlContenedor = new JPanel(new BorderLayout(15, 15));
        pnlContenedor.setBorder(new EmptyBorder(25, 30, 25, 30));
        this.setContentPane(pnlContenedor);

        // 1. Panel Norte: Título de la aplicación
        JPanel pnlNorte = new JPanel();
        JLabel lblTitulo = new JLabel("ParkNow", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        pnlNorte.add(lblTitulo);
        pnlContenedor.add(pnlNorte, BorderLayout.NORTH);

        // 2. Panel Centro: Etiqueta y campo de texto con margen y altura cómoda
        JPanel pnlCentro = new JPanel();
        pnlCentro.setLayout(new GridLayout(2, 1, 8, 8));

        JLabel lblUsuario = new JLabel("Iniciar sesión");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 15));

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(300, 35));

        pnlCentro.add(lblUsuario);
        pnlCentro.add(txtUsuario);
        pnlContenedor.add(pnlCentro, BorderLayout.CENTER);

        // 3. Panel Sur: Para Botón Entrar
        JPanel pnlSur = new JPanel();
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.setPreferredSize(new Dimension(130, 36));

        btnEntrar.addActionListener(e -> {
            String nombre = txtUsuario.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, escribe un nombre de usuario.");
                return;
            }

            Customer user = login(nombre);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "¡Bienvenido, " + user.getName() + "!");
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado.");
            }
        });

        pnlSur.add(btnEntrar);
        pnlContenedor.add(pnlSur, BorderLayout.SOUTH);

        // Ajustes de dimensiones y centrado
        this.setSize(440, 250);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public Customer login(String nombre) {
        Client cliente = new Client();
        HashMap<String, Object> session = new HashMap<>();
        session.put("nombre", nombre);

        session = cliente.sentMessage("/login", session);
        return (Customer) session.get("Customer");
    }
}