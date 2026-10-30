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
    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    // Componentes Registro
    private JTextField txtRegNombre;
    private JTextField txtRegCorreo;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegPassword2;

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
        pnlContenedorPrincipal.add(crearPanelRegistro(), "PANTALLA_REGISTRO");
        pnlContenedorPrincipal.add(crearPanelAparcar(), "PANTALLA_APARCAR");

        this.setContentPane(pnlContenedorPrincipal);
        this.setSize(480, 580);
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

        txtCorreo = new JTextField();
        anadirCampo(pnl, "Correo", txtCorreo);

        txtPassword = new JPasswordField();
        anadirCampo(pnl, "Contraseña", txtPassword);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.setMaximumSize(new Dimension(130, 36));
        btnEntrar.setPreferredSize(new Dimension(130, 36));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnEntrar.addActionListener(e -> {
            String email = txtCorreo.getText().trim();
            String password = new String(txtPassword.getPassword());
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, escribe tu correo y tu contraseña.");
                return;
            }

            try {
                Client cliente = new Client();
                HashMap<String, Object> session = new HashMap<>();
                session.put("email", email);
                session.put("password", password);
                session = cliente.sentMessage("/login", session);

                usuarioActual = (Customer) session.get("Customer");

                if (usuarioActual != null) {
                    txtPassword.setText("");
                    lblSaludo.setText("Hola, " + usuarioActual.getName());
                    cardLayout.show(pnlContenedorPrincipal, "PANTALLA_APARCAR");
                } else {
                    JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.");
            }
        });

        pnl.add(btnEntrar);
        pnl.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón para ir a la pantalla de registro
        JButton btnIrRegistro = new JButton("¿No tienes cuenta? Regístrate");
        btnIrRegistro.setFont(new Font("Arial", Font.PLAIN, 13));
        btnIrRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnIrRegistro.addActionListener(e -> cardLayout.show(pnlContenedorPrincipal, "PANTALLA_REGISTRO"));
        pnl.add(btnIrRegistro);

        return pnl;
    }

    private JPanel crearPanelRegistro() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBorder(new EmptyBorder(25, 60, 25, 60));

        JLabel lblTitulo = new JLabel("ParkNow");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lblTitulo);
        pnl.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblSub = new JLabel("Crear cuenta");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lblSub);
        pnl.add(Box.createRigidArea(new Dimension(0, 10)));

        txtRegNombre = new JTextField();
        anadirCampo(pnl, "Nombre", txtRegNombre);

        txtRegCorreo = new JTextField();
        anadirCampo(pnl, "Correo", txtRegCorreo);

        txtRegPassword = new JPasswordField();
        anadirCampo(pnl, "Contraseña (mínimo 8 caracteres)", txtRegPassword);

        txtRegPassword2 = new JPasswordField();
        anadirCampo(pnl, "Repetir contraseña", txtRegPassword2);
        pnl.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton btnRegistrarme = new JButton("Registrarme");
        btnRegistrarme.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegistrarme.setMaximumSize(new Dimension(150, 36));
        btnRegistrarme.setPreferredSize(new Dimension(150, 36));
        btnRegistrarme.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnRegistrarme.addActionListener(e -> {
            String nombre = txtRegNombre.getText().trim();
            String email = txtRegCorreo.getText().trim();
            String password = new String(txtRegPassword.getPassword());
            String password2 = new String(txtRegPassword2.getPassword());

            // Esta comprobación se hace aquí porque el servidor solo recibe una contraseña
            if (!password.equals(password2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
                return;
            }

            try {
                Client cliente = new Client();
                HashMap<String, Object> session = new HashMap<>();
                session.put("nombre", nombre);
                session.put("email", email);
                session.put("password", password);
                session = cliente.sentMessage("/registro", session);

                boolean exito = (boolean) session.get("exito");
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Cuenta creada correctamente. Ya puedes iniciar sesión.");
                    txtCorreo.setText(email);
                    limpiarRegistro();
                    cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
                } else {
                    JOptionPane.showMessageDialog(this, (String) session.get("error"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.");
            }
        });

        pnl.add(btnRegistrarme);
        pnl.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnVolver = new JButton("Volver al login");
        btnVolver.setFont(new Font("Arial", Font.PLAIN, 13));
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.addActionListener(e -> {
            limpiarRegistro();
            cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
        });
        pnl.add(btnVolver);

        return pnl;
    }

    // Añade al panel una etiqueta y debajo su campo de texto, con el mismo estilo que el login
    private void anadirCampo(JPanel pnl, String texto, JTextField campo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(lbl);
        pnl.add(Box.createRigidArea(new Dimension(0, 4)));

        campo.setFont(new Font("Arial", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(300, 35));
        campo.setPreferredSize(new Dimension(300, 35));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnl.add(campo);
        pnl.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    // Vacía los campos de la pantalla de registro
    private void limpiarRegistro() {
        txtRegNombre.setText("");
        txtRegCorreo.setText("");
        txtRegPassword.setText("");
        txtRegPassword2.setText("");
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

        JButton btnFlecha = new JButton("⬇");
        btnFlecha.setPreferredSize(new Dimension(50, 30));
        JTextArea txtAparcamientos = new JTextArea(4, 30);
        txtAparcamientos.setEditable(false);
        btnFlecha.addActionListener(e -> {
            Client cliente = new Client();
            HashMap<String, Object> session = cliente.sentMessage("/getAparcamientos", new HashMap<>());
            txtAparcamientos.setText((String) session.get("aparcamientos"));
        });

        pnlConsultar.add(new JScrollPane(txtAparcamientos), BorderLayout.CENTER);
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
