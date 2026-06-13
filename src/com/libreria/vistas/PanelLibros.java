package com.libreria.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelLibros extends JPanel {

    private JTextField txtIsbn; // Campo nuevo
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JComboBox<String> cbCategoria; // Reemplaza al '1' hardcodeado
    private JTextField txtBuscar;
    
    
    
    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnNuevaCategoria;
    
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    public PanelLibros() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. PANEL DE FORMULARIO (Arriba) - Cambiado filas para meter ISBN y Categoría
      //Usando GridBagLayout para alinear
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Libro"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Márgenes internos
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: ISBN
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtIsbn = new JTextField();
        panelFormulario.add(txtIsbn, gbc);

        // Fila 1: Título
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Título:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtTitulo = new JTextField();
        panelFormulario.add(txtTitulo, gbc);

        // Fila 2: Autor
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Autor:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        txtAutor = new JTextField();
        panelFormulario.add(txtAutor, gbc);

        // Fila 3: Precio
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Precio:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio, gbc);

        // Fila 4: Stock
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Stock:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0;
        txtStock = new JTextField();
        panelFormulario.add(txtStock, gbc);

        // Fila 5: Categoría (con su panel interno para el botón +)
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0;
        panelFormulario.add(new JLabel("Categoría:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0;
        JPanel panelCat = new JPanel(new BorderLayout(5, 0));
        cbCategoria = new JComboBox<>();
        btnNuevaCategoria = new JButton("+");
        btnNuevaCategoria.setToolTipText("Agregar nueva categoría"); 
        panelCat.add(cbCategoria, BorderLayout.CENTER);
        panelCat.add(btnNuevaCategoria, BorderLayout.EAST);
        panelFormulario.add(panelCat, gbc);

        this.add(panelFormulario, BorderLayout.NORTH);

// 2. PANEL DE TABLA Y BÚSQUEDA (Centro)
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        panelBusqueda.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        panelBusqueda.add(new JLabel("Buscar Libro (ISBN/ Título/ Autor): "), BorderLayout.WEST);
        txtBuscar = new JTextField();
        panelBusqueda.add(txtBuscar, BorderLayout.CENTER);

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelBusqueda, BorderLayout.NORTH); // Arriba va el buscador
        
        modeloTabla = new DefaultTableModel(new String[]{"ID", "ISBN", "Título", "Autor", "Precio", "Stock", "ID Cat"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { 
                return false; 
            }
        };
        tablaLibros = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaLibros);
        
        panelCentro.add(scrollTabla, BorderLayout.CENTER); // Al medio va la tabla
        
        this.add(panelCentro, BorderLayout.CENTER); // Agregamos todo el paquete a la pantalla
        
        

        // 3. PANEL DE BOTONES (Abajo)
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        this.add(panelBotones, BorderLayout.SOUTH);
    }

   
    // GETTERS PARA CONTROLADORES
   
    public JTextField getTxtIsbn() { return txtIsbn; }
    public JTextField getTxtTitulo() { return txtTitulo; }
    public JTextField getTxtAutor() { return txtAutor; }
    public JTextField getTxtPrecio() { return txtPrecio; }
    public JTextField getTxtStock() { return txtStock; }
    public JComboBox<String> getCbCategoria() { return cbCategoria; }
    
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnModificar() { return btnModificar; }
    public JButton getBtnEliminar() { return btnEliminar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    
    public JTable getTablaLibros() { return tablaLibros; }
    public DefaultTableModel getModeloTabla() { return modeloTabla; }
    public JButton getBtnNuevaCategoria() { return btnNuevaCategoria; }
    public JTextField getTxtBuscar() { return txtBuscar; }
    
    
    
}