package com.libreria.vistas;

import java.awt.*;
import javax.swing.*;

public class PanelLogin extends javax.swing.JPanel {

    public PanelLogin() {
        // Usamos un método manual para bypassear las restricciones visuales de NetBeans
        initComponentsManual();
    }

    private void initComponentsManual() {
        // 1. Inicializamos los componentes
        jLabel1 = new JLabel("Usuario:");
        jLabel3 = new JLabel("Contraseña:");
        jLabel2 = new JLabel(); // Acá va a ir el logo
        txtUsuario = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnIngresar = new JButton("Ingresar");

        // 2. Layout principal de este Panel (Centra el contenido automáticamente)
        this.setLayout(new GridBagLayout());

        // 3. Creamos la "Tarjeta" del Login
        JPanel panelTarjeta = new JPanel(new GridBagLayout());
        // Le damos un borde gris sutil y mucho "aire" (padding) adentro
        panelTarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        panelTarjeta.setBackground(Color.WHITE); // Fondo blanco para que resalte

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

       // Fila 0: Logo y Título
    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.CENTER;
    
    JPanel panelLogo = new JPanel(new BorderLayout(5, 5));
    panelLogo.setBackground(Color.WHITE);
    
    // Intentamos cargar el logo
    JLabel lblLogo = new JLabel();
    try {
        java.net.URL urlLogo = getClass().getResource("/com/libreria/recursos/logo.png");
        if (urlLogo != null) {
            ImageIcon icono = new ImageIcon(urlLogo);
            lblLogo.setIcon(new ImageIcon(icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
        }
    } catch (Exception e) { /* Si no hay logo, no hace nada */ }
    
    JLabel lblTitulo = new JLabel("SISTEMA LIBRERÍA", SwingConstants.CENTER);
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    
    panelLogo.add(lblLogo, BorderLayout.NORTH);
    panelLogo.add(lblTitulo, BorderLayout.SOUTH);
    
    panelTarjeta.add(panelLogo, gbc);
        // Fila 1: Usuario
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        jLabel1.setFont(new Font("Arial", Font.BOLD, 12));
        panelTarjeta.add(jLabel1, gbc);
        
        gbc.gridx = 1;
        panelTarjeta.add(txtUsuario, gbc);

        // Fila 2: Contraseña
        gbc.gridy = 2; gbc.gridx = 0;
        jLabel3.setFont(new Font("Arial", Font.BOLD, 12));
        panelTarjeta.add(jLabel3, gbc);
        
        gbc.gridx = 1;
        panelTarjeta.add(txtPassword, gbc);

        // Fila 3: Botón Ingresar
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Para que el botón no se estire a los costados
        gbc.insets = new Insets(30, 10, 10, 10); // Más separación arriba del botón
        
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(0, 102, 204)); // Azul corporativo
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelTarjeta.add(btnIngresar, gbc);

        // 4. Agregamos los eventos
        btnIngresar.addActionListener(this::btnIngresarActionPerformed);
        txtPassword.addActionListener(this::txtPasswordActionPerformed);

        // 5. Agregamos la tarjeta terminada al centro del panel principal
        this.add(panelTarjeta);
    }

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {                                            
        // TODO add your handling code here:
    }                                           

    private void btnIngresarActionPerformed(java.awt.event.ActionEvent evt) {                                            
        
        //boton ingresar
        
        // 1. Capturamos lo que escribió el usuario
        String usuario = txtUsuario.getText();

        // Para el JPasswordField no se usa getText() por seguridad, se usa getPassword() que devuelve un arreglo de caracteres, así que lo convertimos a String:
        String password = new String(txtPassword.getPassword());

        // 2. Llamamos a tu Controlador (DAO)
        com.libreria.controladores.UsuarioDAO dao = new com.libreria.controladores.UsuarioDAO();
        com.libreria.modelos.Usuario userLogueado = dao.validarLogin(usuario, password);

        // 3. Verificamos el resultado
        if (userLogueado != null) {
            
            // Si entró acá, las credenciales son correctas
            javax.swing.JOptionPane.showMessageDialog(this, "¡Bienvenido " + userLogueado.getUsername() + "!");
            
            // TODO: Acá le vamos a avisar a la Ventana Principal que cargue las pestañas
            // Obtenemos la ventana principal que contiene a este panel
            VentanaPrincipal ventana = (VentanaPrincipal) javax.swing.SwingUtilities.getWindowAncestor(this);
            // Ejecutamos el método pasándole el rol del usuario que acaba de entrar
            ventana.habilitarModulos(userLogueado.getRol());
            
        } else {
            // Si dio null, se equivocó de usuario o clave
            javax.swing.JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de Acceso", javax.swing.JOptionPane.ERROR_MESSAGE);
            
            // Limpiamos la caja de contraseña para que vuelva a intentar
            txtPassword.setText("");
        }
    }                                           

    // Variables de la interfaz
    private javax.swing.JButton btnIngresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2; // Nuestro contenedor para el logo/título
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
}