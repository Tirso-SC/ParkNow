package icai.dtc.isw.ui;

import icai.dtc.isw.client.Client;
import icai.dtc.isw.domain.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class JVentana extends JFrame {

    private CardLayout cardLayout;
    private JPanel pnlContenedorPrincipal;

    // Componentes Login
    private JTextField txtUsuario;

    // Componentes Aparcar
    private JLabel lblSaludo;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JButton btnAparcar;
    private JLabel lblEstadoTexto;
    private JButton btnDesaparcar;

    // Usuario activo en la sesión
    private Customer usuarioActual;
    private boolean estaAparcado = false;
    private String horaAparcado = "";

    public static void main(String[] args) {
        new JVentana();
    }

    public JVentana() {
        super("ParkNow");

        cardLayout = new CardLayout();
        pnlContenedorPrincipal = new JPanel(cardLayout);

        pnlContenedorPrincipal.add(crearPanelLogin(), "PANTALLA_LOGIN");
        pnlContenedorPrincipal.add(crearPanelAparcar(), "PANTALLA_APARCAR");

        this.setContentPane(pnlContenedorPrincipal);
        this.setSize(480, 480);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
    }

    private JPanel crearPanelLogin() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel lblTitulo = new JLabel("ParkNow");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lblTitulo);
        pnl.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel lblSub = new JLabel("Iniciar sesión");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lblSub);
        pnl.add(Box.createRigidArea(new Dimension(0, 10)));

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setMaximumSize(new Dimension(300, 35));
        txtUsuario.setPreferredSize(new Dimension(300, 35));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(txtUsuario);
        pnl.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.setMaximumSize(new Dimension(130, 36));
        btnEntrar.setPreferredSize(new Dimension(130, 36));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnEntrar.addActionListener(e -> {
            String nombre = txtUsuario.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, escribe un nombre de usuario.");
                return;
            }

            try {
                Client cliente = new Client();
                HashMap<String, Object> session = new HashMap<>();
                session.put("nombre", nombre);
                session = cliente.sentMessage("/login", session);

                usuarioActual = (Customer) session.get("Customer");

                if (usuarioActual != null) {
                    lblSaludo.setText("Hola, " + usuarioActual.getName());
                    cardLayout.show(pnlContenedorPrincipal, "PANTALLA_APARCAR");
                } else {
                    JOptionPane.showMessageDialog(this, "Usuario no encontrado en la base de datos.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.");
            }
        });

        pnl.add(btnEntrar);
        return pnl;
    }

    private JPanel crearPanelAparcar() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Saludo
        JPanel pnlSaludo = new JPanel(new BorderLayout());
        lblSaludo = new JLabel("Hola");
        lblSaludo.setFont(new Font("Arial", Font.BOLD, 18));
        pnlSaludo.add(lblSaludo, BorderLayout.WEST);
        pnl.add(pnlSaludo);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));

        // 2. Registrar aparcamiento
        JPanel pnlRegistrar = new JPanel(new BorderLayout(10, 10));
        pnlRegistrar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "📍 Registrar aparcamiento",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13)));

        JPanel pnlCoords = new JPanel(new GridLayout(2, 2, 10, 5));
        pnlCoords.add(new JLabel("Latitud:"));
        pnlCoords.add(new JLabel("Longitud:"));
        txtLatitud = new JTextField("40.4168");
        txtLongitud = new JTextField("-3.7038");
        pnlCoords.add(txtLatitud);
        pnlCoords.add(txtLongitud);

        btnAparcar = new JButton("🅿 He aparcado aquí");
        btnAparcar.setFont(new Font("Arial", Font.BOLD, 13));

        btnAparcar.addActionListener(e -> {
            try {
                double lat = Double.parseDouble(txtLatitud.getText().trim());
                double lon = Double.parseDouble(txtLongitud.getText().trim());
                int idUser = Integer.parseInt(usuarioActual.getId());

                Client cliente = new Client();
                HashMap<String, Object> session = new HashMap<>();
                session.put("idUsuario", idUser);
                session.put("latitud", lat);
                session.put("longitud", lon);

                session = cliente.sentMessage("/aparcar", session);
                boolean exito = (boolean) session.get("exito");

                if (exito) {
                    horaAparcado = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                    estaAparcado = true;
                    actualizarEstadoVisual();
                    JOptionPane.showMessageDialog(this, "Aparcado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar aparcamiento en la base de datos.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce coordenadas numéricas válidas.");
            }
        });

        pnlRegistrar.add(pnlCoords, BorderLayout.CENTER);
        pnlRegistrar.add(btnAparcar, BorderLayout.SOUTH);
        pnl.add(pnlRegistrar);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3. Estado del aparcamiento
        JPanel pnlEstado = new JPanel(new BorderLayout(10, 10));
        pnlEstado.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Estado del aparcamiento",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13)));

        lblEstadoTexto = new JLabel("No hay ningún vehículo aparcado.");
        lblEstadoTexto.setFont(new Font("Arial", Font.PLAIN, 13));

        btnDesaparcar = new JButton("🚗 Ya me he marchado");
        btnDesaparcar.setFont(new Font("Arial", Font.BOLD, 13));
        btnDesaparcar.setEnabled(false);

        btnDesaparcar.addActionListener(e -> {
            int idUser = Integer.parseInt(usuarioActual.getId());

            Client cliente = new Client();
            HashMap<String, Object> session = new HashMap<>();
            session.put("idUsuario", idUser);

            session = cliente.sentMessage("/desaparcar", session);
            boolean exito = (boolean) session.get("exito");

            if (exito) {
                estaAparcado = false;
                horaAparcado = "";
                actualizarEstadoVisual();
                JOptionPane.showMessageDialog(this, "Has retirado el coche. Plaza liberada.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el registro.");
            }
        });

        pnlEstado.add(lblEstadoTexto, BorderLayout.CENTER);
        pnlEstado.add(btnDesaparcar, BorderLayout.SOUTH);
        pnl.add(pnlEstado);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. Consultar aparcamientos
        JPanel pnlConsultar = new JPanel(new BorderLayout(5, 5));
        pnlConsultar.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Consultar aparcamientos",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 13)));

        JLabel lblSubConsulta = new JLabel("Lista de registros recientes y su estado.");
        lblSubConsulta.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSubConsulta.setForeground(Color.DARK_GRAY);

        JButton btnFlecha = new JButton("⬇");
        btnFlecha.setPreferredSize(new Dimension(50, 30));
        btnFlecha.addActionListener(e -> JOptionPane.showMessageDialog(this, "Listado en desarrollo."));

        pnlConsultar.add(lblSubConsulta, BorderLayout.CENTER);
        pnlConsultar.add(btnFlecha, BorderLayout.EAST);
        pnl.add(pnlConsultar);

        return pnl;
    }

    private void actualizarEstadoVisual() {
        if (estaAparcado) {
            lblEstadoTexto.setText("<html>Aparcado desde las <b>" + horaAparcado + "</b> &nbsp;&nbsp;<font color='green'>[Activo]</font></html>");
            btnAparcar.setEnabled(false);
            btnDesaparcar.setEnabled(true);
            txtLatitud.setEnabled(false);
            txtLongitud.setEnabled(false);
        } else {
            lblEstadoTexto.setText("No hay ningún vehículo aparcado.");
            btnAparcar.setEnabled(true);
            btnDesaparcar.setEnabled(false);
            txtLatitud.setEnabled(true);
            txtLongitud.setEnabled(true);
        }
    }
}
