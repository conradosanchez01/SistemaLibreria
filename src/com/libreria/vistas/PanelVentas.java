package com.libreria.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelVentas extends javax.swing.JPanel {

    private JTextField txtBuscarCliente;
    private JButton btnBuscarCliente;
    private JLabel lblClienteSeleccionado;

    private JTextField txtBuscarLibro;
    private JButton btnBuscarLibro;
    private JLabel lblLibroSeleccionado;

    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JButton btnFacturar;
    private JButton btnVaciarCarrito;
    
    
    public PanelVentas() {
        initComponentsManual();
    }

    private void initComponentsManual() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. PANEL SUPERIOR: Formulario de Búsqueda
        // ==========================================
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Búsqueda y Selección de Ítems"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Buscador de Clientes
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Buscar Cliente (DNI/Apellido/Nombre):"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtBuscarCliente = new JTextField();
        panelSuperior.add(txtBuscarCliente, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.0;
        btnBuscarCliente = new JButton("Buscar");
        panelSuperior.add(btnBuscarCliente, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        lblClienteSeleccionado = new JLabel("Cliente: --- Ninguno Seleccionado ---");
        lblClienteSeleccionado.setFont(new Font("Arial", Font.BOLD, 12));
        lblClienteSeleccionado.setForeground(new Color(150, 0, 0));
        panelSuperior.add(lblClienteSeleccionado, gbc);

        // Fila 1: Buscador de Libros
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Buscar Libro (ISBN/Título/Autor):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtBuscarLibro = new JTextField();
        panelSuperior.add(txtBuscarLibro, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.0;
        btnBuscarLibro = new JButton("Buscar");
        panelSuperior.add(btnBuscarLibro, gbc);

        gbc.gridx = 3; gbc.gridy = 1; gbc.weightx = 1.0;
        lblLibroSeleccionado = new JLabel("Libro: --- Ninguno Seleccionado ---");
        lblLibroSeleccionado.setFont(new Font("Arial", Font.BOLD, 12));
        lblLibroSeleccionado.setForeground(new Color(150, 0, 0));
        panelSuperior.add(lblLibroSeleccionado, gbc);

        // Fila 2: Cantidad e Inserción
        JPanel panelAccionesProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAccionesProducto.add(new JLabel("Cantidad:"));
        
        txtCantidad = new JTextField("1", 6);
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) {
                    evt.consume(); // Filtro nativo de teclado para números
                }
            }
        });
        panelAccionesProducto.add(txtCantidad);

        btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setBackground(new Color(34, 139, 34));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        panelAccionesProducto.add(btnAgregar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panelSuperior.add(panelAccionesProducto, gbc);

        this.add(panelSuperior, BorderLayout.NORTH);

        // ==========================================
        // 2. PANEL CENTRAL: Tabla del Carrito
        // ==========================================
        String[] columnas = {"ID Libro","ISBN", "Título del Libro", "Cantidad", "Precio Unitario", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaCarrito = new JTable(modeloTabla);
        tablaCarrito.setFillsViewportHeight(true);
        
        // TRUCO Ocultamos la columna 0 (ID) de la vista del cajero, pero sigue existiendo para el código
        tablaCarrito.getColumnModel().getColumn(0).setMinWidth(0);
        tablaCarrito.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaCarrito.getColumnModel().getColumn(0).setWidth(0);
        JScrollPane scrollTabla = new JScrollPane(tablaCarrito);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Artículos en el Carrito Actual"));
        this.add(scrollTabla, BorderLayout.CENTER);

       // ==========================================
        // 3. PANEL INFERIOR: Totales y Confirmación
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        lblTotal = new JLabel("TOTAL A PAGAR: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(40, 40, 40));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        // Agrupamos los dos botones a la derecha
        JPanel panelBotonesInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        btnVaciarCarrito = new JButton("Vaciar Carrito");
        btnVaciarCarrito.setFont(new Font("Arial", Font.BOLD, 14));
        btnVaciarCarrito.setBackground(new Color(204, 51, 51)); // Rojo oscuro
        btnVaciarCarrito.setForeground(Color.WHITE);
        panelBotonesInferior.add(btnVaciarCarrito);

        btnFacturar = new JButton("Confirmar y Facturar Venta");
        btnFacturar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFacturar.setBackground(new Color(0, 102, 204)); // Azul
        btnFacturar.setForeground(Color.WHITE);
        panelBotonesInferior.add(btnFacturar);

        panelInferior.add(panelBotonesInferior, BorderLayout.EAST);

        this.add(panelInferior, BorderLayout.SOUTH);
    }

    // Getters para que el controlador tome el control de la pantalla
    public JTextField getTxtBuscarCliente() { return txtBuscarCliente; }
    public JButton getBtnBuscarCliente() { return btnBuscarCliente; }
    public JLabel getLblClienteSeleccionado() { return lblClienteSeleccionado; }

    public JTextField getTxtBuscarLibro() { return txtBuscarLibro; }
    public JButton getBtnBuscarLibro() { return btnBuscarLibro; }
    public JLabel getLblLibroSeleccionado() { return lblLibroSeleccionado; }

    public JTextField getTxtCantidad() { return txtCantidad; }
    public JButton getBtnAgregar() { return btnAgregar; }
    public JTable getTablaCarrito() { return tablaCarrito; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JLabel getLblTotal() { return lblTotal; }
    public JButton getBtnFacturar() { return btnFacturar; }
    
    public JButton getBtnVaciarCarrito() { return btnVaciarCarrito; }
    
    
    
    
}