package icai.dtc.isw.ui;

import icai.dtc.isw.client.Client;
import icai.dtc.isw.domain.Customer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

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
    private MadridMapPanel mapaMadrid;

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
        JScrollPane scrollAparcar = new JScrollPane(crearPanelAparcar());
        scrollAparcar.setBorder(null);
        scrollAparcar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        pnlContenedorPrincipal.add(scrollAparcar, "PANTALLA_APARCAR");

        this.setContentPane(pnlContenedorPrincipal);
        this.setSize(1120, 760);
        this.setMinimumSize(new Dimension(980, 680));
        this.setResizable(true);
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
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Introduce un correo válido.");
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
                    cargarAparcamientosEnMapa();
                    cargarMiAparcamiento();
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
        Color fondo = new Color(246, 247, 249);
        Color azul = new Color(30, 96, 220);
        JPanel pnl = new JPanel(new BorderLayout(18, 18));
        pnl.setBackground(fondo);
        pnl.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel marca = new JLabel("ParkNow");
        marca.setFont(new Font("Arial", Font.BOLD, 25));
        marca.setForeground(new Color(24, 28, 38));
        lblSaludo = new JLabel("Hola");
        lblSaludo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSaludo.setForeground(new Color(105, 110, 122));
        textos.add(marca);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblSaludo);
        cabecera.add(textos, BorderLayout.WEST);
        JLabel ciudad = new JLabel("MADRID  ·  MAPA EN TIEMPO REAL");
        ciudad.setFont(new Font("Arial", Font.BOLD, 11));
        ciudad.setForeground(new Color(105, 110, 122));
        cabecera.add(ciudad, BorderLayout.EAST);
        pnl.add(cabecera, BorderLayout.NORTH);

        mapaMadrid = new MadridMapPanel();
        JPanel mapaConControles = new JPanel(new BorderLayout());
        mapaConControles.setBackground(Color.WHITE);
        mapaConControles.setBorder(BorderFactory.createLineBorder(new Color(224, 227, 233)));
        mapaConControles.add(mapaMadrid, BorderLayout.CENTER);
        JPanel controlesMapa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controlesMapa.setBackground(Color.WHITE);
        JButton btnMenosZoom = crearBoton("-", false);
        JButton btnMasZoom = crearBoton("+", false);
        JButton btnCentrarMapa = crearBoton("Centrar Madrid", false);
        btnMenosZoom.addActionListener(e -> mapaMadrid.cambiarZoom(-1));
        btnMasZoom.addActionListener(e -> mapaMadrid.cambiarZoom(1));
        btnCentrarMapa.addActionListener(e -> mapaMadrid.centrar());
        controlesMapa.add(btnMenosZoom);
        controlesMapa.add(btnMasZoom);
        controlesMapa.add(btnCentrarMapa);
        JLabel ayuda = new JLabel("Pulsa en el mapa para elegir tu plaza");
        ayuda.setFont(new Font("Arial", Font.PLAIN, 12));
        ayuda.setForeground(new Color(115, 120, 130));
        controlesMapa.add(Box.createHorizontalStrut(8));
        controlesMapa.add(ayuda);
        mapaConControles.add(controlesMapa, BorderLayout.SOUTH);
        pnl.add(mapaConControles, BorderLayout.CENTER);

        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(Color.WHITE);
        lateral.setBorder(new EmptyBorder(20, 18, 20, 18));
        lateral.setPreferredSize(new Dimension(285, 0));

        JLabel titulo = new JLabel("Tu aparcamiento");
        titulo.setFont(new Font("Arial", Font.BOLD, 19));
        titulo.setForeground(new Color(24, 28, 38));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(titulo);
        JLabel descripcion = new JLabel("<html>Selecciona una posición y<br>confirma dónde has dejado el coche.</html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 13));
        descripcion.setForeground(new Color(105, 110, 122));
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(7));
        lateral.add(descripcion);
        lateral.add(Box.createVerticalStrut(22));

        JPanel ubicacion = crearTarjeta("Ubicación seleccionada");
        txtLatitud = crearCampo("40.41680");
        txtLongitud = crearCampo("-3.70380");
        JPanel coordenadas = new JPanel(new GridLayout(2, 1, 0, 8));
        coordenadas.setOpaque(false);
        coordenadas.add(crearCampoConEtiqueta("Latitud", txtLatitud));
        coordenadas.add(crearCampoConEtiqueta("Longitud", txtLongitud));
        ubicacion.add(coordenadas, BorderLayout.CENTER);
        lateral.add(ubicacion);

        btnAparcar = crearBoton("He aparcado aquí", true);
        btnAparcar.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(14));
        lateral.add(btnAparcar);
        btnAparcar.addActionListener(e -> aparcarEnUbicacionSeleccionada());

        // Botón para consultar las plazas SER del barrio del punto seleccionado en el mapa
        JButton btnVerPlazas = crearBoton("🔍 Ver plazas de esta zona", false);
        btnVerPlazas.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(btnVerPlazas);
        btnVerPlazas.addActionListener(e -> verPlazasDeLaZona());

        JPanel estado = crearTarjeta("Estado actual");
        lblEstadoTexto = new JLabel("No hay ningún vehículo aparcado.");
        lblEstadoTexto.setFont(new Font("Arial", Font.PLAIN, 13));
        estado.add(lblEstadoTexto, BorderLayout.CENTER);
        lateral.add(Box.createVerticalStrut(18));
        lateral.add(estado);

        btnDesaparcar = crearBoton("Ya me he marchado", false);
        btnDesaparcar.setEnabled(false);
        estado.add(btnDesaparcar, BorderLayout.SOUTH);
        btnDesaparcar.addActionListener(e -> desaparcar());
        pnl.add(lateral, BorderLayout.EAST);

        mapaMadrid.setClickListener((lat, lon) -> {
            txtLatitud.setText(String.format(Locale.US, "%.5f", lat));
            txtLongitud.setText(String.format(Locale.US, "%.5f", lon));
        });
        return pnl;
    }

    private JPanel crearTarjeta(String titulo) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 10));
        tarjeta.setBackground(new Color(247, 248, 250));
        tarjeta.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 234, 239)),
                new EmptyBorder(13, 12, 13, 12)));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font("Arial", Font.BOLD, 12));
        etiqueta.setForeground(new Color(45, 50, 60));
        tarjeta.add(etiqueta, BorderLayout.NORTH);
        return tarjeta;
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, JTextField campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 3));
        grupo.setOpaque(false);
        JLabel titulo = new JLabel(etiqueta);
        titulo.setFont(new Font("Arial", Font.PLAIN, 11));
        titulo.setForeground(new Color(105, 110, 122));
        grupo.add(titulo, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);
        return grupo;
    }

    private JTextField crearCampo(String valor) {
        JTextField campo = new JTextField(valor);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 223, 229)),
                new EmptyBorder(7, 9, 7, 9)));
        return campo;
    }

    private JButton crearBoton(String texto, boolean principal) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBackground(principal ? new Color(30, 96, 220) : Color.WHITE);
        boton.setForeground(principal ? Color.WHITE : new Color(45, 50, 60));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(principal ? new Color(30, 96, 220) : new Color(220, 223, 229)),
                new EmptyBorder(8, 13, 8, 13)));
        return boton;
    }

    private void aparcarEnUbicacionSeleccionada() {
        try {
            double lat = Double.parseDouble(txtLatitud.getText().trim());
            double lon = Double.parseDouble(txtLongitud.getText().trim());
            int idUser = Integer.parseInt(usuarioActual.getId());
            HashMap<String, Object> session = new HashMap<>();
            session.put("idUsuario", idUser);
            session.put("latitud", lat);
            session.put("longitud", lon);
            session = new Client().sentMessage("/aparcar", session);
            if ((boolean) session.get("exito")) {
                horaAparcado = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                estaAparcado = true;
                mapaMadrid.setMyCar(lat, lon);
                actualizarEstadoVisual();
                cargarAparcamientosEnMapa();
            } else {
                JOptionPane.showMessageDialog(this, "No se ha podido registrar el aparcamiento.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce coordenadas numéricas válidas.");
        }
    }

    // Pide al servidor la información SER del barrio de las coordenadas seleccionadas
    // (las que se rellenan al pulsar en el mapa) y la muestra en una ventana
    private void verPlazasDeLaZona() {
        try {
            double lat = Double.parseDouble(txtLatitud.getText().trim());
            double lon = Double.parseDouble(txtLongitud.getText().trim());
            HashMap<String, Object> session = new HashMap<>();
            session.put("latitud", lat);
            session.put("longitud", lon);
            session = new Client().sentMessage("/infoBarrio", session);

            // Si el servidor devuelve "error" es que no hay plazas SER cerca
            if (session.get("error") != null) {
                JOptionPane.showMessageDialog(this, "Esta zona no tiene plazas reguladas (SER) registradas.");
                return;
            }

            String mensaje = "Barrio: " + session.get("barrio") + " (" + session.get("distrito") + ")\n"
                    + "Plazas SER: " + session.get("totalPlazas")
                    + " (" + session.get("plazasVerdes") + " verdes, "
                    + session.get("plazasAzules") + " azules)\n"
                    + "Coches de ParkNow aparcados ahora: " + session.get("cochesAparcados") + "\n"
                    + "Plazas libres estimadas: " + session.get("plazasLibres") + "\n\n"
                    + "(Estimación basada en datos del Ayuntamiento y en los usuarios de ParkNow)";
            JOptionPane.showMessageDialog(this, mensaje, "Plazas de esta zona", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Introduce coordenadas numéricas válidas.");
        } catch (Exception ex) {
            // Si el servidor no responde, avisamos pero la aplicación sigue abierta
            JOptionPane.showMessageDialog(this, "Error de conexión con el servidor");
        }
    }

    private void desaparcar() {
        HashMap<String, Object> session = new HashMap<>();
        session.put("idUsuario", Integer.parseInt(usuarioActual.getId()));
        session = new Client().sentMessage("/desaparcar", session);
        if ((boolean) session.get("exito")) {
            estaAparcado = false;
            horaAparcado = "";
            mapaMadrid.setMyCar(null, null);
            actualizarEstadoVisual();
            cargarAparcamientosEnMapa();
        } else {
            JOptionPane.showMessageDialog(this, "No se ha podido liberar la plaza.");
        }
    }

    private void cargarMiAparcamiento() {
        HashMap<String, Object> peticion = new HashMap<>();
        peticion.put("idUsuario", Integer.parseInt(usuarioActual.getId()));
        HashMap<String, Object> respuesta = new Client().sentMessage("/getAparcamientoActivo", peticion);
        if (!respuesta.containsKey("latitud")) {
            estaAparcado = false;
            actualizarEstadoVisual();
            return;
        }

        double lat = ((Number) respuesta.get("latitud")).doubleValue();
        double lon = ((Number) respuesta.get("longitud")).doubleValue();
        Object horaInicio = respuesta.get("horaInicio");
        horaAparcado = horaInicio == null ? "" : horaInicio.toString();
        if (horaAparcado.length() >= 16) {
            horaAparcado = horaAparcado.substring(11, 16);
        }
        estaAparcado = true;
        txtLatitud.setText(String.format(Locale.US, "%.5f", lat));
        txtLongitud.setText(String.format(Locale.US, "%.5f", lon));
        mapaMadrid.setMyCar(lat, lon);
        actualizarEstadoVisual();
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

    private String cargarAparcamientosEnMapa() {
        Client cliente = new Client();
        HashMap<String, Object> session = cliente.sentMessage("/getAparcamientos", new HashMap<>());
        String aparcamientos = (String) session.get("aparcamientos");
        if (mapaMadrid != null) {
            mapaMadrid.setMarkers(leerCochesActivos(aparcamientos));
        }
        return aparcamientos;
    }

    private List<ParkingMarker> leerCochesActivos(String texto) {
        List<ParkingMarker> coches = new ArrayList<>();
        Pattern patron = Pattern.compile(
                "(Coche aparcado|Aparcamiento libre) en \\((-?\\d+(?:\\.\\d+)?),\\s*(-?\\d+(?:\\.\\d+)?)\\)");
        Matcher matcher = patron.matcher(texto == null ? "" : texto);
        while (matcher.find()) {
            coches.add(new ParkingMarker(
                    Double.parseDouble(matcher.group(2)),
                    Double.parseDouble(matcher.group(3)),
                    matcher.group(1).startsWith("Aparcamiento")));
        }
        return coches;
    }

    private static class ParkingMarker {
        private final double latitud;
        private final double longitud;
        private final boolean libre;

        private ParkingMarker(double latitud, double longitud, boolean libre) {
            this.latitud = latitud;
            this.longitud = longitud;
            this.libre = libre;
        }
    }

    private static class MadridMapPanel extends JPanel {
        private static final double LAT_MIN = 40.30;
        private static final double LAT_MAX = 40.50;
        private static final double LON_MIN = -3.85;
        private static final double LON_MAX = -3.55;

        private List<ParkingMarker> markers = new ArrayList<>();
        private MapClickListener clickListener;
        private Double myLatitude;
        private Double myLongitude;
        private double zoom = 1;
        private double centroLatitud = 40.4168;
        private double centroLongitud = -3.7038;
        private int ultimoX;
        private int ultimoY;
        private final Map<String, BufferedImage> teselas = new HashMap<>();
        private final Map<String, Boolean> teselasCargando = new HashMap<>();
        private final BufferedImage iconoLibre;
        private final BufferedImage iconoOcupada;
        private final BufferedImage iconoMio;

        private MadridMapPanel() {
            iconoLibre = cargarIcono("/markers/parking_libre.png");
            iconoOcupada = cargarIcono("/markers/parking_ocupado.png");
            iconoMio = cargarIcono("/markers/mi_parking.png");
            setPreferredSize(new Dimension(760, 560));
            setMinimumSize(new Dimension(620, 420));
            setBorder(BorderFactory.createLineBorder(new Color(150, 180, 210)));
            setToolTipText("Pulsa en el mapa para elegir una coordenada");
            setBackground(new Color(238, 241, 238));
            java.awt.event.MouseAdapter raton = new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    ultimoX = e.getX();
                    ultimoY = e.getY();
                }

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {
                    double centroX = longitudAMundo(centroLongitud)
                            - (e.getX() - ultimoX);
                    double centroY = latitudAMundo(centroLatitud)
                            - (e.getY() - ultimoY);
                    centroLongitud = mundoALongitud(centroX);
                    centroLatitud = mundoALatitud(centroY);
                    ultimoX = e.getX();
                    ultimoY = e.getY();
                    repaint();
                }

                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 1 && clickListener != null) {
                        double lon = longitudDesdeX(e.getX());
                        double lat = latitudDesdeY(e.getY());
                        setMyCar(lat, lon);
                        clickListener.clicked(lat, lon);
                    }
                }
            };
            addMouseListener(raton);
            addMouseMotionListener(raton);
            addMouseWheelListener(e -> cambiarZoom(e.getWheelRotation() < 0 ? 1 : -1));
        }

        private void setClickListener(MapClickListener clickListener) {
            this.clickListener = clickListener;
        }

        private void setMarkers(List<ParkingMarker> markers) {
            this.markers = markers;
            repaint();
        }

        private void setMyCar(Double latitude, Double longitude) {
            myLatitude = latitude;
            myLongitude = longitude;
            repaint();
        }

        private BufferedImage cargarIcono(String ruta) {
            try (InputStream entrada = JVentana.class.getResourceAsStream(ruta)) {
                if (entrada == null) {
                    throw new IllegalStateException("No se encuentra el icono del mapa: " + ruta);
                }
                BufferedImage imagen = ImageIO.read(entrada);
                if (imagen == null) {
                    throw new IllegalStateException("El icono del mapa no es una imagen válida: " + ruta);
                }
                return imagen;
            } catch (IOException ex) {
                throw new IllegalStateException("No se puede cargar el icono del mapa: " + ruta, ex);
            }
        }

        private void cambiarZoom(int cambio) {
            zoom = Math.max(1, Math.min(5, zoom + cambio));
            repaint();
        }

        private void centrar() {
            zoom = 1;
            centroLatitud = 40.4168;
            centroLongitud = -3.7038;
            repaint();
        }

        private double rangoLatitud() {
            return (LAT_MAX - LAT_MIN) / zoom;
        }

        private double rangoLongitud() {
            return (LON_MAX - LON_MIN) / zoom;
        }

        private double longitudDesdeX(int x) {
            return mundoALongitud(xDesdeMundo(x));
        }

        private double latitudDesdeY(int y) {
            return mundoALatitud(yDesdeMundo(y));
        }

        private int xDesdeLongitud(double longitud) {
            return (int) (longitudAMundo(longitud) - longitudAMundo(centroLongitud) + getWidth() / 2.0);
        }

        private int yDesdeLatitud(double latitud) {
            return (int) (latitudAMundo(latitud) - latitudAMundo(centroLatitud) + getHeight() / 2.0);
        }

        private int xDesdeMundo(int x) {
            return (int) (longitudAMundo(centroLongitud) + x - getWidth() / 2.0);
        }

        private int yDesdeMundo(int y) {
            return (int) (latitudAMundo(centroLatitud) + y - getHeight() / 2.0);
        }

        private int nivelTeselas() {
            return 15 + (int) zoom - 1;
        }

        private double longitudAMundo(double longitud) {
            double tamano = 256 * Math.pow(2, nivelTeselas());
            return (longitud + 180) / 360 * tamano;
        }

        private double latitudAMundo(double latitud) {
            double seno = Math.sin(Math.toRadians(latitud));
            double tamano = 256 * Math.pow(2, nivelTeselas());
            return (0.5 - Math.log((1 + seno) / (1 - seno)) / (4 * Math.PI)) * tamano;
        }

        private double mundoALongitud(double mundo) {
            double tamano = 256 * Math.pow(2, nivelTeselas());
            return mundo / tamano * 360 - 180;
        }

        private double mundoALatitud(double mundo) {
            double tamano = 256 * Math.pow(2, nivelTeselas());
            double n = Math.PI - 2 * Math.PI * mundo / tamano;
            return Math.toDegrees(Math.atan(Math.sinh(n)));
        }

        private void dibujarTeselas(Graphics2D g) {
            int nivel = nivelTeselas();
            double centroX = longitudAMundo(centroLongitud);
            double centroY = latitudAMundo(centroLatitud);
            int inicioX = (int) Math.floor((centroX - getWidth() / 2.0) / 256);
            int finX = (int) Math.floor((centroX + getWidth() / 2.0) / 256);
            int inicioY = (int) Math.floor((centroY - getHeight() / 2.0) / 256);
            int finY = (int) Math.floor((centroY + getHeight() / 2.0) / 256);

            for (int tileX = inicioX; tileX <= finX; tileX++) {
                for (int tileY = inicioY; tileY <= finY; tileY++) {
                    int xNormalizado = Math.floorMod(tileX, 1 << nivel);
                    if (tileY < 0 || tileY >= (1 << nivel)) {
                        continue;
                    }
                    int x = (int) (tileX * 256 - centroX + getWidth() / 2.0);
                    int y = (int) (tileY * 256 - centroY + getHeight() / 2.0);
                    String clave = nivel + "/" + xNormalizado + "/" + tileY;
                    BufferedImage tesela = teselas.get(clave);
                    if (tesela != null) {
                        g.drawImage(tesela, x, y, null);
                    } else {
                        g.setColor(new Color(225, 232, 225));
                        g.fillRect(x, y, 256, 256);
                        cargarTesela(clave, nivel, xNormalizado, tileY);
                    }
                }
            }
        }

        private void cargarTesela(String clave, int nivel, int x, int y) {
            if (teselasCargando.putIfAbsent(clave, Boolean.TRUE) != null) {
                return;
            }
            Thread hilo = new Thread(() -> {
                try {
                    URLConnection conexion = new URL(
                            "https://tile.openstreetmap.org/" + nivel + "/" + x + "/" + y + ".png")
                            .openConnection();
                    conexion.setRequestProperty("User-Agent", "ParkNow/1.0");
                    BufferedImage imagen = ImageIO.read(conexion.getInputStream());
                    if (imagen != null) {
                        teselas.put(clave, imagen);
                    }
                } catch (IOException ex) {
                    System.err.println("No se pudo cargar el mapa: " + ex.getMessage());
                } finally {
                    teselasCargando.remove(clave);
                    SwingUtilities.invokeLater(this::repaint);
                }
            }, "cargar-mapa");
            hilo.setDaemon(true);
            hilo.start();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dibujarTeselas(g);

            g.setColor(new Color(80, 80, 80));
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRoundRect(8, 8, 145, 26, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString("Madrid · nivel " + nivelTeselas(), 16, 26);
            g.setColor(new Color(50, 50, 50));
            g.drawString("© OpenStreetMap contributors", 8, getHeight() - 8);
            for (ParkingMarker marker : markers) {
                int x = xDesdeLongitud(marker.longitud);
                int y = yDesdeLatitud(marker.latitud);
                if (x < -20 || x > getWidth() + 20 || y < -25 || y > getHeight() + 20) {
                    continue;
                }
                if (marker.libre) {
                    dibujarPlazaLibre(g, x, y);
                    continue;
                }
                boolean isMyCar = myLatitude != null && myLongitude != null
                        && Math.abs(myLatitude - marker.latitud) < 0.00001
                        && Math.abs(myLongitude - marker.longitud) < 0.00001;
                if (!isMyCar) {
                    dibujarCoche(g, x, y, false);
                }
            }
            if (myLatitude != null && myLongitude != null) {
                int x = xDesdeLongitud(myLongitude);
                int y = yDesdeLatitud(myLatitude);
                dibujarCoche(g, x, y, true);
            }
            dibujarLeyenda(g);
        }

        private void dibujarCoche(Graphics2D g, int x, int y, boolean esMio) {
            dibujarIcono(g, x, y, esMio ? iconoMio : iconoOcupada);
        }

        private void dibujarPlazaLibre(Graphics2D g, int x, int y) {
            dibujarIcono(g, x, y, iconoLibre);
        }

        private void dibujarIcono(Graphics2D g, int x, int y, BufferedImage icono) {
            int tamano = 46;
            g.drawImage(icono, x - tamano / 2, y - tamano, tamano, tamano, null);
        }

        private void dibujarLeyenda(Graphics2D g) {
            int x = getWidth() - 172;
            int y = 8;
            g.setColor(new Color(255, 255, 255, 225));
            g.fillRoundRect(x, y, 164, 67, 10, 10);
            g.setColor(new Color(70, 70, 70));
            g.drawRoundRect(x, y, 164, 67, 10, 10);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 11f));
            g.drawString("Estado de las plazas", x + 10, y + 15);
            g.setFont(g.getFont().deriveFont(Font.PLAIN, 11f));
            g.setColor(new Color(220, 55, 55));
            g.fillOval(x + 11, y + 23, 12, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Ocupada", x + 30, y + 33);
            g.setColor(new Color(35, 100, 220));
            g.fillOval(x + 85, y + 23, 12, 12);
            g.setColor(Color.DARK_GRAY);
            g.drawString("La mía", x + 103, y + 33);
            g.setColor(new Color(35, 165, 85));
            g.fillRoundRect(x + 11, y + 43, 12, 12, 3, 3);
            g.setColor(Color.DARK_GRAY);
            g.drawString("Libre", x + 30, y + 53);
        }
    }

    @FunctionalInterface
    private interface MapClickListener {
        void clicked(double latitud, double longitud);
    }
}
