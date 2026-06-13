package com.libreria.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelClientes extends JPanel {

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtEmail;
    private JTextField txtBuscar;
    

    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tablaClientes;
    private DefaultTableModel modelo;

    public PanelClientes() {// Le damos 10 píxeles de aire entre la parte de arriba, el centro y los botones
        setLayout(new BorderLayout(10, 10));
        
        // MARGEN EXTERNO: Evita que todo el panel se pegue a las paredes de la pestaña
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PANEL SUPERIOR (Formulario) ---
        //Al texto dale solo el espacio que necesite, y el resto del ancho dejáselo al cuadro de texto
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        
        // EL FAMOSO MARQUITO: Esto crea la caja con el título "Datos del Cliente"
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Márgenes internos (arriba, izquierda, abajo, derecha)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        
      // Fila 0: Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0; // El Label ocupa solo lo necesario
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; // El JTextField se estira para llenar el resto
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre, gbc);

      // Fila 1: Apellido
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Apellido:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtApellido = new JTextField();
        panelFormulario.add(txtApellido, gbc);

        // Fila 2: DNI
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("DNI:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        txtDni = new JTextField();
        panelFormulario.add(txtDni, gbc);

        // Fila 3: Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail, gbc);

        add(panelFormulario, BorderLayout.NORTH);

        // --- PANEL CENTRAL (Buscador + Tabla) ---
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panelBusqueda.add(new JLabel("Buscar Cliente por (DNI/ Nombre/ Apellido): "), BorderLayout.WEST);
        txtBuscar = new JTextField();
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelBusqueda, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Email"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaClientes);
        
        panelCentro.add(scroll, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Botones) ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    // =========================================================
    // GETTERS PARA QUE EL CONTROLADOR PUEDA MANEJAR LA VISTA
    // =========================================================
    public JTextField getTxtNombre() { return txtNombre; }
    public JTextField getTxtApellido() { return txtApellido; }
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtEmail() { return txtEmail; }
    
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnModificar() { return btnModificar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    
    public JTable getTablaClientes() { return tablaClientes; }
    public DefaultTableModel getModelo() { return modelo; }
    
    public JTextField getTxtBuscar() { return txtBuscar; }
    
    
    
    
}