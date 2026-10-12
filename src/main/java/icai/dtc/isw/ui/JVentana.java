package icai.dtc.isw.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JVentana extends JFrame {

    private CardLayout cardLayout;
    private JPanel pnlContenedorPrincipal;

    // Panel Login
    private JTextField txtUsuario;

    // Panel Aparcamiento
    private JLabel lblSaludo;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JButton btnAparcar;
    private JLabel lblEstadoTexto;
    private JButton btnDesaparcar;

    // Variables de estado local (sin BD por ahora)
    private String nombreUsuarioActual;
    private boolean estaAparcado = false;
    private String horaAparcado = "";

    public static void main(String[] args) {
        new JVentana();
    }

    public JVentana() {
        super("ParkNow");

        cardLayout = new CardLayout();
        pnlContenedorPrincipal = new JPanel(cardLayout);

        // Añadimos los dos paneles
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
        JPanel pnl = new JPanel(new BorderLayout(15, 15));
        pnl.setBorder(new EmptyBorder(60, 40, 60, 40));

        JLabel lblTitulo = new JLabel("ParkNow", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        pnl.add(lblTitulo, BorderLayout.NORTH);

        JPanel pnlCentro = new JPanel(new GridLayout(2, 1, 8, 8));
        JLabel lblSub = new JLabel("Iniciar sesión");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 15));
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        pnlCentro.add(lblSub);
        pnlCentro.add(txtUsuario);
        pnl.add(pnlCentro, BorderLayout.CENTER);

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

            nombreUsuarioActual = nombre;
            lblSaludo.setText("Hola, " + nombreUsuarioActual);
            cardLayout.show(pnlContenedorPrincipal, "PANTALLA_APARCAR");
        });

        pnlSur.add(btnEntrar);
        pnl.add(pnlSur, BorderLayout.SOUTH);

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
            horaAparcado = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            estaAparcado = true;
            actualizarEstadoVisual();
            JOptionPane.showMessageDialog(this, "¡Coche registrado como aparcado!");
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
            estaAparcado = false;
            horaAparcado = "";
            actualizarEstadoVisual();
            JOptionPane.showMessageDialog(this, "Has retirado el coche.");
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