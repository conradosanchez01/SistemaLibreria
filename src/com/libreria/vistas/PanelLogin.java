package com.libreria.vistas;

import java.awt.*;
import javax.swing.*;

public class PanelLogin extends javax.swing.JPanel {

    private Image imagenFondo; // Variable para guardar el fondo en memoria

    public PanelLogin() {
        // Intentamos cargar la imagen de fondo una sola vez al iniciar
        try {
            java.net.URL urlFondo = getClass().getResource("/com/libreria/recursos/fondo.jpg");
            if (urlFondo != null) {
                imagenFondo = new ImageIcon(urlFondo).getImage();
            }
        } catch (Exception e) {
            System.err.println("No se encontró la imagen de fondo.");
        }

        initComponentsManual();
    }

    // MAGIA VISUAL: Este método de Java pinta el fondo antes de poner los botones
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            // Dibuja la imagen estirándola al tamaño de la ventana
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Si no hay foto, pinta un gris oscuro elegante
            g.setColor(new Color(40, 45, 50));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void initComponentsManual() {
        jLabel1 = new JLabel("Usuario:");
        jLabel3 = new JLabel("Contraseña:");
        jLabel2 = new JLabel(); 
        txtUsuario = new JTextField(15);
        txtPassword = new JPasswordField(15);
        btnIngresar = new JButton("Ingresar");

        this.setLayout(new GridBagLayout()); // Centra la tarjeta en la inmensidad del fondo

        // Creamos la "Tarjeta" del Login
        JPanel panelTarjeta = new JPanel(new GridBagLayout());
        panelTarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        panelTarjeta.setBackground(Color.WHITE); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Logo y Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel panelLogo = new JPanel(new BorderLayout(5, 15)); // 15px de separación entre logo y texto
        panelLogo.setBackground(Color.WHITE);
        
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER); // Fuerzo el centrado horizontal
        
        try {
            java.net.URL urlLogo = getClass().getResource("/com/libreria/recursos/logo.png");
            if (urlLogo != null) {
                ImageIcon icono = new ImageIcon(urlLogo);
                // Aumentamos el tamaño a 180x180 para que se lea perfecto
                lblLogo.setIcon(new ImageIcon(icono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            }
        } catch (Exception e) { }
        
        JLabel lblTitulo = new JLabel("SISTEMA LIBRERÍA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        panelLogo.add(lblLogo, BorderLayout.CENTER);
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
        gbc.fill = GridBagConstraints.NONE; 
        gbc.insets = new Insets(30, 10, 10, 10); 
        
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(0, 102, 204)); 
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelTarjeta.add(btnIngresar, gbc);

        this.add(panelTarjeta);
    }

    // GETTERS
    public JTextField getTxtUsuario() { return txtUsuario; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnIngresar() { return btnIngresar; }

    // Variables de la interfaz
    private javax.swing.JButton btnIngresar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2; 
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsuario;
}