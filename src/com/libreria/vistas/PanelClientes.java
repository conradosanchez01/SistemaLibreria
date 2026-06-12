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

    public PanelClientes() {
        setLayout(new BorderLayout());

        // --- PANEL SUPERIOR (Formulario) ---
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormulario.add(new JLabel("Nombre"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellido"));
        txtApellido = new JTextField();
        panelFormulario.add(txtApellido);

        panelFormulario.add(new JLabel("DNI"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Email"));
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail);

        add(panelFormulario, BorderLayout.NORTH);

// --- PANEL CENTRAL (Buscador + Tabla) ---
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panelBusqueda.add(new JLabel("Buscar Cliente (DNI, Nombre o Apellido): "), BorderLayout.WEST);
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
        JPanel panelBotones = new JPanel();
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