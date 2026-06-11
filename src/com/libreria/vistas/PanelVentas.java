package com.libreria.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelVentas extends javax.swing.JPanel {

    private JComboBox<String> cbClientes;
    private JComboBox<String> cbLibros;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JButton btnFacturar;

    public PanelVentas() {
        initComponentsManual();
    }

    private void initComponentsManual() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Formulario de Carga
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Registrar Ítem de Venta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        cbClientes = new JComboBox<>(); 
        panelSuperior.add(cbClientes, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Libro / Producto:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        cbLibros = new JComboBox<>(); 
        panelSuperior.add(cbLibros, gbc);

        JPanel panelAccionesProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAccionesProducto.add(new JLabel("Cantidad:"));
        
        txtCantidad = new JTextField("1", 6);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) {
                    evt.consume();
                }
            }
        });
        panelAccionesProducto.add(txtCantidad);

        btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setBackground(new Color(34, 139, 34));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        panelAccionesProducto.add(btnAgregar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelSuperior.add(panelAccionesProducto, gbc);

        this.add(panelSuperior, BorderLayout.NORTH);

        // Tabla del Carrito
        String[] columnas = {"ID Libro", "Título del Libro", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tablaCarrito);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Artículos en el Carrito Actual"));
        
        this.add(scrollTabla, BorderLayout.CENTER);

        // Totales y Confirmación
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        lblTotal = new JLabel("TOTAL A PAGAR: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(40, 40, 40));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        btnFacturar = new JButton("Confirmar y Facturar Venta");
        btnFacturar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFacturar.setBackground(new Color(0, 102, 204));
        btnFacturar.setForeground(Color.WHITE);
        panelInferior.add(btnFacturar, BorderLayout.EAST);

        this.add(panelInferior, BorderLayout.SOUTH);
    }

    // =========================================================
    // GETTERS PARA CONTROLADORES
    // =========================================================
    public JComboBox<String> getCbClientes() { return cbClientes; }
    public JComboBox<String> getCbLibros() { return cbLibros; }
    public JTextField getTxtCantidad() { return txtCantidad; }
    public JButton getBtnAgregar() { return btnAgregar; }
    public JTable getTablaCarrito() { return tablaCarrito; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JLabel getLblTotal() { return lblTotal; }
    public JButton getBtnFacturar() { return btnFacturar; }
}