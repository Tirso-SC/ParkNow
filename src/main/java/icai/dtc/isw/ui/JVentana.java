package icai.dtc.isw.ui;

import icai.dtc.isw.client.Client;
import icai.dtc.isw.domain.Customer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class JVentana extends JFrame {

    
    private static final Color AZUL = new Color(0, 113, 227);
    private static final Color AZUL_OSCURO = new Color(0, 94, 190);
    private static final Color FONDO = new Color(245, 245, 247);
    private static final Color GRIS_BOTON = new Color(232, 232, 237);
    private static final Color GRIS_BOTON_OSCURO = new Color(218, 218, 224);
    private static final Color TEXTO = new Color(29, 29, 31);
    private static final Color TEXTO_SUAVE = new Color(134, 134, 139);
    private static final Color DESACTIVADO = new Color(238, 238, 240);
    private static final Color VERDE = new Color(52, 199, 89);
    private static final Color NARANJA = new Color(255, 149, 0);
    private static final Color ROJO = new Color(255, 59, 48);

    
    private static final String FUENTE = elegirFuente();

    private static String elegirFuente() {
        List<String> instaladas = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        String[] preferidas = {"SF Pro Text", "Helvetica Neue", "Segoe UI", "Arial"};
        for (String fuente : preferidas) {
            if (instaladas.contains(fuente)) {
                return fuente;
            }
        }
        return "SansSerif";
    }

    
    private enum TipoAviso { ERROR, AVISO, EXITO }

    private CardLayout cardLayout;
    private JPanel pnlContenedorPrincipal;

    
    private JTextField txtCorreo;
    private JPasswordField txtPassword;

    
    private JTextField txtRegNombre;
    private JTextField txtRegCorreo;
    private JPasswordField txtRegPassword;
    private JPasswordField txtRegPassword2;

    
    private JLabel lblSaludo;
    private JTextField txtLatitud;
    private JTextField txtLongitud;
    private JButton btnAparcar;
    private JLabel lblEstadoTexto;
    private JButton btnDesaparcar;
    private JLabel lblInfoZona;
    private MadridMapPanel mapaMadrid;

    
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
        JPanel tarjeta = crearTarjetaFormulario("Bienvenido de nuevo", "Inicia sesión para encontrar aparcamiento en Madrid.");

        txtCorreo = new JTextField();
        anadirCampo(tarjeta, "Correo electrónico", txtCorreo);

        txtPassword = new JPasswordField();
        anadirCampo(tarjeta, "Contraseña", txtPassword);
        tarjeta.add(Box.createVerticalStrut(6));

        JButton btnEntrar = crearBoton("Entrar", true);
        anadirBotonAncho(tarjeta, btnEntrar);
        btnEntrar.addActionListener(e -> iniciarSesion());

        
        txtCorreo.addActionListener(e -> btnEntrar.doClick());
        txtPassword.addActionListener(e -> btnEntrar.doClick());

        tarjeta.add(Box.createVerticalStrut(14));
        JButton btnIrRegistro = crearBotonEnlace("¿No tienes cuenta? Regístrate");
        btnIrRegistro.addActionListener(e -> cardLayout.show(pnlContenedorPrincipal, "PANTALLA_REGISTRO"));
        tarjeta.add(btnIrRegistro);

        return centrarEnPantalla(tarjeta);
    }

    private void iniciarSesion() {
        String email = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            mostrarAviso(TipoAviso.AVISO, "Faltan datos", "Escribe tu correo y tu contraseña.");
            return;
        }
        if (!email.contains("@") || !email.contains(".")) {
            mostrarAviso(TipoAviso.AVISO, "Correo no válido", "Revisa que el correo esté bien escrito.");
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
                mostrarAviso(TipoAviso.ERROR, "No se ha podido iniciar sesión", "Correo o contraseña incorrectos.");
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    
    
    

    private JPanel crearPanelRegistro() {
        JPanel tarjeta = crearTarjetaFormulario("Crear cuenta", "Regístrate en unos segundos y empieza a aparcar.");

        txtRegNombre = new JTextField();
        anadirCampo(tarjeta, "Nombre", txtRegNombre);

        txtRegCorreo = new JTextField();
        anadirCampo(tarjeta, "Correo electrónico", txtRegCorreo);

        txtRegPassword = new JPasswordField();
        anadirCampo(tarjeta, "Contraseña (mínimo 8 caracteres)", txtRegPassword);

        txtRegPassword2 = new JPasswordField();
        anadirCampo(tarjeta, "Repetir contraseña", txtRegPassword2);
        tarjeta.add(Box.createVerticalStrut(6));

        JButton btnRegistrarme = crearBoton("Registrarme", true);
        anadirBotonAncho(tarjeta, btnRegistrarme);
        btnRegistrarme.addActionListener(e -> registrarse());
        txtRegPassword2.addActionListener(e -> btnRegistrarme.doClick());

        tarjeta.add(Box.createVerticalStrut(14));
        JButton btnVolver = crearBotonEnlace("← Ya tengo cuenta, volver al login");
        btnVolver.addActionListener(e -> {
            limpiarRegistro();
            cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
        });
        tarjeta.add(btnVolver);

        return centrarEnPantalla(tarjeta);
    }

    private void registrarse() {
        String nombre = txtRegNombre.getText().trim();
        String email = txtRegCorreo.getText().trim();
        String password = new String(txtRegPassword.getPassword());
        String password2 = new String(txtRegPassword2.getPassword());

        
        if (!password.equals(password2)) {
            mostrarAviso(TipoAviso.AVISO, "Las contraseñas no coinciden", "Escribe la misma contraseña en los dos campos.");
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
                mostrarAviso(TipoAviso.EXITO, "Cuenta creada", "Ya puedes iniciar sesión con tu correo.");
                txtCorreo.setText(email);
                limpiarRegistro();
                cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
            } else {
                mostrarAviso(TipoAviso.ERROR, "No se ha podido crear la cuenta", (String) session.get("error"));
            }
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    
    private void limpiarRegistro() {
        txtRegNombre.setText("");
        txtRegCorreo.setText("");
        txtRegPassword.setText("");
        txtRegPassword2.setText("");
    }

    
    
    

    
    private JPanel crearTarjetaFormulario(String titulo, String subtitulo) {
        PanelRedondeado tarjeta = new PanelRedondeado(Color.WHITE, 24);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(36, 40, 32, 40));

        tarjeta.add(crearLogo());
        tarjeta.add(Box.createVerticalStrut(28));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(6));

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 14));
        lblSub.setForeground(TEXTO_SUAVE);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.add(lblSub);
        tarjeta.add(Box.createVerticalStrut(24));
        return tarjeta;
    }

    
    private JPanel crearLogo() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelRedondeado icono = new PanelRedondeado(AZUL, 12);
        icono.setLayout(new GridBagLayout());
        icono.setPreferredSize(new Dimension(38, 38));
        JLabel letra = new JLabel("P");
        letra.setFont(new Font(FUENTE, Font.BOLD, 21));
        letra.setForeground(Color.WHITE);
        icono.add(letra);

        JLabel nombre = new JLabel("ParkNow");
        nombre.setFont(new Font(FUENTE, Font.BOLD, 22));
        nombre.setForeground(TEXTO);

        fila.add(icono);
        fila.add(Box.createHorizontalStrut(10));
        fila.add(nombre);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return fila;
    }

    
    private JPanel centrarEnPantalla(JPanel tarjeta) {
        JPanel fondo = new JPanel(new GridBagLayout()); 
        fondo.setBackground(FONDO);
        fondo.add(tarjeta);
        return fondo;
    }

    
    private void anadirCampo(JPanel pnl, String texto, JTextField campo) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lbl);
        pnl.add(Box.createVerticalStrut(6));

        JPanel caja = crearCajaCampo(campo, FONDO);
        caja.setPreferredSize(new Dimension(320, 42));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(caja);
        pnl.add(Box.createVerticalStrut(14));
    }

    
    private void anadirBotonAncho(JPanel pnl, JButton boton) {
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        pnl.add(boton);
    }

    
    private JButton crearBotonEnlace(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(FUENTE, Font.PLAIN, 13));
        boton.setForeground(AZUL);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(0, 0, 0, 0));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        return boton;
    }

    
    
    

    private JPanel crearPanelAparcar() {
        JPanel pnl = new JPanel(new BorderLayout(18, 18));
        pnl.setBackground(FONDO);
        pnl.setBorder(new EmptyBorder(20, 28, 24, 28));

        
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(crearLogo());
        textos.add(Box.createVerticalStrut(6));
        lblSaludo = new JLabel("Hola");
        lblSaludo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        lblSaludo.setForeground(TEXTO_SUAVE);
        lblSaludo.setAlignmentX(Component.LEFT_ALIGNMENT);
        textos.add(lblSaludo);
        cabecera.add(textos, BorderLayout.WEST);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        derecha.setOpaque(false);
        JLabel ciudad = new JLabel("Madrid · Mapa en tiempo real");
        ciudad.setFont(new Font(FUENTE, Font.PLAIN, 13));
        ciudad.setForeground(TEXTO_SUAVE);
        JButton btnCerrarSesion = crearBoton("Cerrar sesión", false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        derecha.add(ciudad);
        derecha.add(btnCerrarSesion);
        cabecera.add(derecha, BorderLayout.EAST);
        pnl.add(cabecera, BorderLayout.NORTH);

        
        mapaMadrid = new MadridMapPanel();
        PanelRedondeado mapaConControles = new PanelRedondeado(Color.WHITE, 24);
        mapaConControles.setLayout(new BorderLayout());
        mapaConControles.setBorder(new EmptyBorder(8, 8, 0, 8));
        mapaConControles.add(mapaMadrid, BorderLayout.CENTER);
        JPanel controlesMapa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controlesMapa.setOpaque(false);
        JButton btnMenosZoom = crearBoton("−", false);
        JButton btnMasZoom = crearBoton("+", false);
        JButton btnCentrarMapa = crearBoton("Centrar Madrid", false);
        btnMenosZoom.setToolTipText("Alejar");
        btnMasZoom.setToolTipText("Acercar");
        btnMenosZoom.addActionListener(e -> mapaMadrid.cambiarZoom(-1));
        btnMasZoom.addActionListener(e -> mapaMadrid.cambiarZoom(1));
        btnCentrarMapa.addActionListener(e -> mapaMadrid.centrar());
        controlesMapa.add(btnMenosZoom);
        controlesMapa.add(btnMasZoom);
        controlesMapa.add(btnCentrarMapa);
        JLabel ayuda = new JLabel("Haz clic para elegir un punto · Arrastra para moverte · Rueda para hacer zoom");
        ayuda.setFont(new Font(FUENTE, Font.PLAIN, 12));
        ayuda.setForeground(TEXTO_SUAVE);
        controlesMapa.add(Box.createHorizontalStrut(8));
        controlesMapa.add(ayuda);
        mapaConControles.add(controlesMapa, BorderLayout.SOUTH);
        pnl.add(mapaConControles, BorderLayout.CENTER);

        
        PanelRedondeado lateral = new PanelRedondeado(Color.WHITE, 24);
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(new EmptyBorder(22, 18, 20, 18));
        lateral.setPreferredSize(new Dimension(300, 0));

        JLabel titulo = new JLabel("Tu aparcamiento");
        titulo.setFont(new Font(FUENTE, Font.BOLD, 21));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(titulo);
        JLabel descripcion = new JLabel("<html>Selecciona una posición en el mapa y<br>confirma dónde has dejado el coche.</html>");
        descripcion.setFont(new Font(FUENTE, Font.PLAIN, 13));
        descripcion.setForeground(TEXTO_SUAVE);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(6));
        lateral.add(descripcion);
        lateral.add(Box.createVerticalStrut(18));

        
        JPanel estado = crearTarjeta("Estado actual");
        lblEstadoTexto = new JLabel();
        lblEstadoTexto.setFont(new Font(FUENTE, Font.PLAIN, 13));
        estado.add(lblEstadoTexto, BorderLayout.CENTER);
        
        btnDesaparcar = new BotonRedondeado("Ya me he marchado", Color.WHITE, new Color(255, 236, 235), ROJO);
        estado.add(btnDesaparcar, BorderLayout.SOUTH);
        btnDesaparcar.addActionListener(e -> desaparcar());
        lateral.add(estado);
        lateral.add(Box.createVerticalStrut(12));

        
        JPanel ubicacion = crearTarjeta("Ubicación seleccionada");
        txtLatitud = new JTextField("40.41680");
        txtLongitud = new JTextField("-3.70380");
        JPanel coordenadas = new JPanel(new GridLayout(1, 2, 8, 0));
        coordenadas.setOpaque(false);
        coordenadas.add(crearCampoConEtiqueta("Latitud", txtLatitud));
        coordenadas.add(crearCampoConEtiqueta("Longitud", txtLongitud));
        ubicacion.add(coordenadas, BorderLayout.CENTER);
        lateral.add(ubicacion);

        btnAparcar = crearBoton("He aparcado aquí", true);
        btnAparcar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAparcar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(btnAparcar);
        btnAparcar.addActionListener(e -> aparcarEnUbicacionSeleccionada());

        
        JButton btnVerPlazas = crearBoton("Ver plazas de esta zona", false);
        btnVerPlazas.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVerPlazas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(btnVerPlazas);
        btnVerPlazas.addActionListener(e -> verPlazasDeLaZona());
        lateral.add(Box.createVerticalStrut(12));

        
        JPanel zona = crearTarjeta("Información de la zona");
        zona.setMaximumSize(new Dimension(Integer.MAX_VALUE, 240));
        lblInfoZona = new JLabel();
        lblInfoZona.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblInfoZona.setVerticalAlignment(SwingConstants.TOP);
        zona.add(lblInfoZona, BorderLayout.CENTER);
        lateral.add(zona);
        lateral.add(Box.createVerticalGlue());
        reiniciarInfoZona();

        pnl.add(lateral, BorderLayout.EAST);

        mapaMadrid.setClickListener((lat, lon) -> {
            
            if (estaAparcado) {
                return;
            }
            mapaMadrid.setMyCar(lat, lon);
            txtLatitud.setText(String.format(Locale.US, "%.5f", lat));
            txtLongitud.setText(String.format(Locale.US, "%.5f", lon));
        });
        actualizarEstadoVisual();
        return pnl;
    }

    
    private JPanel crearTarjeta(String titulo) {
        PanelRedondeado tarjeta = new PanelRedondeado(FONDO, 16);
        tarjeta.setLayout(new BorderLayout(0, 10));
        tarjeta.setBorder(new EmptyBorder(13, 14, 14, 14));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel etiqueta = new JLabel(titulo.toUpperCase());
        etiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        etiqueta.setForeground(TEXTO_SUAVE);
        tarjeta.add(etiqueta, BorderLayout.NORTH);
        return tarjeta;
    }

    private JPanel crearCampoConEtiqueta(String etiqueta, JTextField campo) {
        JPanel grupo = new JPanel(new BorderLayout(0, 4));
        grupo.setOpaque(false);
        JLabel titulo = new JLabel(etiqueta);
        titulo.setFont(new Font(FUENTE, Font.PLAIN, 11));
        titulo.setForeground(TEXTO_SUAVE);
        grupo.add(titulo, BorderLayout.NORTH);
        grupo.add(crearCajaCampo(campo, Color.WHITE), BorderLayout.CENTER);
        return grupo;
    }

    
    private JPanel crearCajaCampo(JTextField campo, Color fondo) {
        PanelRedondeado caja = new PanelRedondeado(fondo, 12);
        caja.setLayout(new BorderLayout());
        caja.setBorder(new EmptyBorder(0, 10, 0, 10));

        campo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        campo.setForeground(TEXTO);
        campo.setCaretColor(AZUL);
        campo.setOpaque(false); 
        campo.setBorder(new EmptyBorder(8, 0, 8, 0));
        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                caja.setColorBorde(AZUL);
            }

            @Override
            public void focusLost(FocusEvent e) {
                caja.setColorBorde(null);
            }
        });
        caja.add(campo, BorderLayout.CENTER);
        return caja;
    }

    
    private JButton crearBoton(String texto, boolean principal) {
        if (principal) {
            return new BotonRedondeado(texto, AZUL, AZUL_OSCURO, Color.WHITE);
        }
        return new BotonRedondeado(texto, GRIS_BOTON, GRIS_BOTON_OSCURO, AZUL);
    }

    
    private void cerrarSesion() {
        usuarioActual = null;
        estaAparcado = false;
        horaAparcado = "";
        mapaMadrid.setMyCar(null, null);
        reiniciarInfoZona();
        actualizarEstadoVisual();
        txtPassword.setText("");
        cardLayout.show(pnlContenedorPrincipal, "PANTALLA_LOGIN");
    }

    private void reiniciarInfoZona() {
        lblInfoZona.setText("<html><div style='width:175px'><font color='#86868b'>"
                + "Elige un punto en el mapa y pulsa <b>Ver plazas de esta zona</b> "
                + "para saber cuántas plazas libres hay cerca.</font></div></html>");
    }

    
    
    

    
    private void mostrarAviso(TipoAviso tipo, String titulo, String mensaje) {
        Color color;
        String simbolo;
        if (tipo == TipoAviso.ERROR) {
            color = ROJO;
            simbolo = "!";
        } else if (tipo == TipoAviso.AVISO) {
            color = NARANJA;
            simbolo = "!";
        } else {
            color = VERDE;
            simbolo = "✓";
        }

        JDialog dialogo = new JDialog(this, true);
        dialogo.setUndecorated(true);
        dialogo.setBackground(new Color(0, 0, 0, 0)); 

        PanelRedondeado caja = new PanelRedondeado(Color.WHITE, 26);
        caja.setColorBorde(new Color(0, 0, 0, 45));
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(24, 28, 20, 28));

        
        PanelRedondeado icono = new PanelRedondeado(color, 46);
        icono.setLayout(new GridBagLayout());
        icono.setPreferredSize(new Dimension(46, 46));
        icono.setMaximumSize(new Dimension(46, 46));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel lblSimbolo = new JLabel(simbolo);
        lblSimbolo.setFont(new Font(Font.DIALOG, Font.BOLD, 24)); 
        lblSimbolo.setForeground(Color.WHITE);
        icono.add(lblSimbolo);
        caja.add(icono);
        caja.add(Box.createVerticalStrut(14));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 15));
        lblTitulo.setForeground(TEXTO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(lblTitulo);
        caja.add(Box.createVerticalStrut(6));

        
        String texto = mensaje == null ? "" : mensaje.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        JLabel lblMensaje = new JLabel("<html><div style='width:220px; text-align:center'>" + texto + "</div></html>");
        lblMensaje.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblMensaje.setForeground(TEXTO_SUAVE);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(lblMensaje);
        caja.add(Box.createVerticalStrut(20));

        JButton btnAceptar = crearBoton("Aceptar", true);
        btnAceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAceptar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAceptar.addActionListener(e -> dialogo.dispose());
        caja.add(btnAceptar);

        
        dialogo.getRootPane().setDefaultButton(btnAceptar);
        dialogo.getRootPane().registerKeyboardAction(e -> dialogo.dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialogo.setContentPane(caja);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true); 
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
            } else if (session.get("error") != null) {
                
                mostrarAviso(TipoAviso.AVISO, "No puedes aparcar aquí", (String) session.get("error"));
            } else {
                mostrarAviso(TipoAviso.ERROR, "Algo ha ido mal", "No se ha podido registrar el aparcamiento.");
            }
        } catch (NumberFormatException ex) {
            mostrarAviso(TipoAviso.AVISO, "Coordenadas no válidas", "Introduce coordenadas numéricas válidas.");
        } catch (Exception ex) {
            mostrarError(ex);
        }
    }

    
    private void mostrarError(Exception ex) {
        String motivo = ex.getMessage() != null ? ex.getMessage() : "Error desconocido.";
        mostrarAviso(TipoAviso.ERROR, "Algo ha ido mal", motivo);
    }

    
    
    private void verPlazasDeLaZona() {
        try {
            double lat = Double.parseDouble(txtLatitud.getText().trim());
            double lon = Double.parseDouble(txtLongitud.getText().trim());
            HashMap<String, Object> session = new HashMap<>();
            session.put("latitud", lat);
            session.put("longitud", lon);
            session = new Client().sentMessage("/infoBarrio", session);

            
            if (session.get("error") != null) {
                lblInfoZona.setText("<html><div style='width:175px'>"
                        + "Esta zona no tiene plazas reguladas (SER) registradas.</div></html>");
                return;
            }

            
            lblInfoZona.setText("<html><div style='width:175px'>"
                    + "<b>" + session.get("barrio") + "</b> <font color='#86868b'>(" + session.get("distrito") + ")</font>"
                    + "<br><br><font size='+2' color='#34c759'><b>" + session.get("plazasLibres") + "</b></font>"
                    + " plazas libres estimadas"
                    + "<br><br>Plazas SER: <b>" + session.get("totalPlazas") + "</b>"
                    + "<br><font color='#34c759'>●</font> " + session.get("plazasVerdes") + " verdes"
                    + " &nbsp; <font color='#0071e3'>●</font> " + session.get("plazasAzules") + " azules"
                    + " &nbsp; <font color='#86868b'>●</font> " + session.get("plazasOtras") + " otras"
                    + "<br>Coches de ParkNow ahora: <b>" + session.get("cochesAparcados") + "</b>"
                    + "<br><br><font color='#86868b' size='-1'>Estimación basada en datos del Ayuntamiento "
                    + "y en los usuarios de ParkNow.</font></div></html>");
        } catch (NumberFormatException ex) {
            mostrarAviso(TipoAviso.AVISO, "Coordenadas no válidas", "Introduce coordenadas numéricas válidas.");
        } catch (Exception ex) {
            
            mostrarError(ex);
        }
    }

    private void desaparcar() {
        try {
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
                mostrarAviso(TipoAviso.ERROR, "Algo ha ido mal", "No se ha podido liberar la plaza.");
            }
        } catch (Exception ex) {
            mostrarError(ex);
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
            lblEstadoTexto.setText("<html><font color='#34c759'>●</font> <b>Aparcado</b> desde las <b>"
                    + horaAparcado + "</b><br><font color='#86868b'>Pulsa el botón cuando te vayas.</font></html>");
            btnAparcar.setEnabled(false);
            btnDesaparcar.setEnabled(true);
            txtLatitud.setEnabled(false);
            txtLongitud.setEnabled(false);
        } else {
            lblEstadoTexto.setText("<html><font color='#c7c7cc'>●</font> No hay ningún vehículo aparcado."
                    + "<br><font color='#86868b'>Elige un punto en el mapa.</font></html>");
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
            setPreferredSize(new Dimension(640, 470));
            setMinimumSize(new Dimension(620, 420));
            setOpaque(false); 
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
            Graphics2D g = (Graphics2D) graphics.create();
            
            g.clip(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dibujarTeselas(g);

            g.setColor(new Color(0, 0, 0, 170));
            g.fillRoundRect(8, 8, 110, 26, 8, 8);
            g.setColor(Color.WHITE);
            g.drawString("Zoom " + (int) zoom + " de 5", 16, 26);
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
            g.dispose();
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
            g.fillRoundRect(x, y, 164, 67, 16, 16);
            g.setColor(new Color(0, 0, 0, 30));
            g.drawRoundRect(x, y, 164, 67, 16, 16);
            g.setColor(TEXTO);
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

    
    private static class PanelRedondeado extends JPanel {
        private final int radio;
        private Color colorBorde; 

        private PanelRedondeado(Color fondo, int radio) {
            this.radio = radio;
            setBackground(fondo);
            setOpaque(false); 
        }

        private void setColorBorde(Color colorBorde) {
            this.colorBorde = colorBorde;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            if (colorBorde != null) {
                g2.setColor(colorBorde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    
    private static class BotonRedondeado extends JButton {
        private final Color colorNormal;
        private final Color colorEncima;
        private boolean ratonEncima = false;

        private BotonRedondeado(String texto, Color colorNormal, Color colorEncima, Color colorTexto) {
            super(texto);
            this.colorNormal = colorNormal;
            this.colorEncima = colorEncima;
            setForeground(colorTexto);
            setFont(new Font(FUENTE, Font.BOLD, 13));
            setContentAreaFilled(false); 
            setBorderPainted(false);
            setFocusPainted(false);
            setBorder(new EmptyBorder(10, 16, 10, 16));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ratonEncima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ratonEncima = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (!isEnabled()) {
                g2.setColor(DESACTIVADO);
            } else if (getModel().isPressed()) {
                g2.setColor(colorEncima.darker());
            } else if (ratonEncima) {
                g2.setColor(colorEncima);
            } else {
                g2.setColor(colorNormal);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            g2.dispose();
            super.paintComponent(g); 
        }
    }

    @FunctionalInterface
    private interface MapClickListener {
        void clicked(double latitud, double longitud);
    }
}
