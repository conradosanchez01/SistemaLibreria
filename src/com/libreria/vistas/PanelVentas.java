package com.libreria.vistas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelVentas extends javax.swing.JPanel {


    private JComboBox<String> cbClientes;
    private JComboBox<String> cbLibros;
    private java.util.List<String> datosTecnicosClientes;
    private java.util.List<String> datosTecnicosLibros;
    private JTextField txtCantidad;
    private JButton btnAgregar;
    private JTable tablaCarrito;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JButton btnFacturar;
    
    private double totalVenta = 0.0;

    public PanelVentas() {
        initComponentsManual();
        cargarCombosDinamicos(); 
    }

    /**
     * Construye toda la interfaz gráfica de forma robusta por código.
     */
    private void initComponentsManual() {
        // Layout principal con márgenes limpios
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. PANEL SUPERIOR: Formulario de Carga
        // ==========================================
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Registrar Ítem de Venta"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fila 0: Selección de Cliente
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        // Se inicializa vacío porque lo llenamos desde la BD
        cbClientes = new JComboBox<>(); 
        panelSuperior.add(cbClientes, gbc);

        // Fila 1: Selección de Libro
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        panelSuperior.add(new JLabel("Libro / Producto:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        // Se inicializa vacío porque lo llenamos desde la BD
        cbLibros = new JComboBox<>(); 
        panelSuperior.add(cbLibros, gbc);

        // Fila 2: Cantidad e Inserción al carrito
        JPanel panelAccionesProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelAccionesProducto.add(new JLabel("Cantidad:"));
        
        txtCantidad = new JTextField("1", 6);
        // Validación Solo deja tipear números.
        txtCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (!Character.isDigit(evt.getKeyChar())) {
                    evt.consume(); // Cancela la tecla si es una letra
                }
            }
        });
        panelAccionesProducto.add(txtCantidad);

        btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setBackground(new Color(34, 139, 34));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 12));
        panelAccionesProducto.add(btnAgregar);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        panelSuperior.add(panelAccionesProducto, gbc);

        this.add(panelSuperior, BorderLayout.NORTH);

        // ==========================================
        // 2. PANEL CENTRAL: Tabla del Carrito (JTable)
        // ==========================================
        String[] columnas = {"ID Libro", "Título del Libro", "Cantidad", "Precio Unitario", "Subtotal"};
        
        // Bloqueamos la edición directa sobre las celdas de la tabla por seguridad
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

        // ==========================================
        // 3. PANEL INFERIOR: Totales y Confirmación
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        lblTotal = new JLabel("TOTAL A PAGAR: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setForeground(new Color(40, 40, 40));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        btnFacturar = new JButton("Confirmar y Facturar Venta");
        btnFacturar.setFont(new Font("Arial", Font.BOLD, 14));
        btnFacturar.setBackground(new Color(0, 102, 204)); // Azul operativo
        btnFacturar.setForeground(Color.WHITE);
        panelInferior.add(btnFacturar, BorderLayout.EAST);

        this.add(panelInferior, BorderLayout.SOUTH);

        // ==========================================
        // 4. CONTROLADORES DE EVENTOS (Acciones)
        // ==========================================
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarAgregarProducto();
            }
        });

        btnFacturar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarFacturacion();
            }
        });
    }

    /**
     Consulta a MySQL y llena los ComboBox 
     */
   private void cargarCombosDinamicos() {
        // --- CLIENTES ---
        cbClientes.removeAllItems();
        com.libreria.dao.ClienteDAO cDao = new com.libreria.dao.ClienteDAO();
        datosTecnicosClientes = cDao.listarClientesCombo();
        
        for (String c : datosTecnicosClientes) {
            String[] partes = c.split("::");
            if (partes.length > 1) {
                cbClientes.addItem(partes[1]); // Muestra: Juan Pérez
            } else {
                cbClientes.addItem(partes[0]); // Muestra: --- Seleccione ---
            }
        }

        // --- LIBROS ---
        cbLibros.removeAllItems();
        com.libreria.dao.LibroDAO lDao = new com.libreria.dao.LibroDAO();
        datosTecnicosLibros = lDao.listarLibrosCombo();
        
        for (String l : datosTecnicosLibros) {
            String[] partes = l.split("::");
            if (partes[0].equals("0")) {
                cbLibros.addItem(partes[1]); // Muestra: --- Seleccione un Libro ---
            } else {
                // Muestra: Don Quijote de la Mancha ($1500.0)
                cbLibros.addItem(partes[1] + " ($" + partes[2] + ")");
            }
        }
    }

    /**
     * Captura los datos de la interfaz e introduce la línea al carrito temporal.
     */
    private void ejecutarAgregarProducto() {
        // Obtenemos el índice seleccionado por el usuario en la interfaz
        int indexCliente = cbClientes.getSelectedIndex();
        int indexLibro = cbLibros.getSelectedIndex();

        // 1. Validaciones de selección
        if (indexCliente <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (indexLibro <= 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un libro para agregar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validamos que la cantidad sea un número válido
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            // 2. BUSCAMOS LOS DATOS EN LA LISTA OCULTA
            String libroSelect = datosTecnicosLibros.get(indexLibro);
            String[] partesLibro = libroSelect.split("::");
            
            int idLibro = Integer.parseInt(partesLibro[0]);
            String titulo = partesLibro[1];
            double precio = Double.parseDouble(partesLibro[2]);
            int stockDisponible = Integer.parseInt(partesLibro[3]);

            // 3. Validación de Stock
            if (cantidad > stockDisponible) {
                JOptionPane.showMessageDialog(this, 
                    "Stock insuficiente. Solo quedan " + stockDisponible + " unidades de '" + titulo + "'.", 
                    "Sin Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double subtotal = precio * cantidad;

            // 4. Agregamos al modelo de la tabla
            modeloTabla.addRow(new Object[]{idLibro, titulo, cantidad, "$" + precio, "$" + subtotal});

            // 5. Actualizamos el total
            totalVenta += subtotal;
            lblTotal.setText("TOTAL A PAGAR: $" + String.format("%.2f", totalVenta));
            
            txtCantidad.setText("1");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero mayor a cero.", "Error de Dato", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * CONEXIÓN CON LA BASE DE DATOS:
     * Toma los datos del JTable, arma la estructura de modelos e invoca al DAO.
     */
    private void ejecutarFacturacion() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El carrito de compras se encuentra vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtenemos el ID real usando la lista oculta técnica
        int indexCliente = cbClientes.getSelectedIndex();
        if (indexCliente <= 0) { 
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtenemos el String y extraemos el ID
        String clienteTecnico = datosTecnicosClientes.get(indexCliente);
        int idCliente = Integer.parseInt(clienteTecnico.split("::")[0]);
        String nombreCliente = clienteTecnico.split("::")[1]; // Ya tenemos el nombre para el PDF

        // Preguntar confirmación al usuario
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Confirmar la venta por un total de $" + String.format("%.2f", totalVenta) + "?", 
                "Confirmar Operación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return; 
        }

        try {
            com.libreria.model.Venta nuevaVenta = new com.libreria.model.Venta(idCliente, new java.util.Date(), totalVenta);

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idLibro = Integer.parseInt(modeloTabla.getValueAt(i, 0).toString());
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 2).toString());
                
                String precioCelda = modeloTabla.getValueAt(i, 3).toString().replace("$", "").trim();
                double precioUnitario = Double.parseDouble(precioCelda);

                com.libreria.model.DetalleVenta detalle = new com.libreria.model.DetalleVenta(idLibro, cantidad, precioUnitario);
                nuevaVenta.agregarDetalle(detalle);
            }

            com.libreria.dao.VentaDAO ventaDAO = new com.libreria.dao.VentaDAO();
            boolean exito = ventaDAO.registrarVentaCompleta(nuevaVenta);

            if (exito) {
                JOptionPane.showMessageDialog(this, 
                        "¡Venta registrada y facturada con éxito en MySQL!\nEl stock ha sido actualizado.", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                // Generamos el PDF usando el nombre que sacamos de la lista técnica
                generarFacturaPDF(nombreCliente, totalVenta);
                
                // Limpiar por completo la interfaz
                modeloTabla.setRowCount(0);
                totalVenta = 0.0;
                lblTotal.setText("TOTAL A PAGAR: $0.00");
                txtCantidad.setText("1");
                cargarCombosDinamicos(); // Refresca todo
            }

        } catch (com.libreria.model.StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error crítico en la Base de Datos:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            // Esto captura cualquier error inesperado, como el NumberFormatException anterior
            JOptionPane.showMessageDialog(this, "Ocurrió un error al procesar la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
/**
     * Genera el comprobante en PDF leyendo los datos de la tabla visual
     */
    private void generarFacturaPDF(String nombreCliente, double totalFacturado) {
        try {
            
            String nombreArchivo = "Factura_" + System.currentTimeMillis() + ".pdf";
            
            com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(nombreArchivo));
            
            documento.open();
            
            // 1. Título y Encabezado
            documento.add(new com.itextpdf.text.Paragraph("SISTEMA LIBRERIA - COMPROBANTE DE VENTA\n\n"));
            documento.add(new com.itextpdf.text.Paragraph("Cliente: " + nombreCliente));
            documento.add(new com.itextpdf.text.Paragraph("Fecha: " + new java.util.Date().toString() + "\n\n"));
            
            // 2. Tabla de Detalles (4 columnas)
            com.itextpdf.text.pdf.PdfPTable tablaPDF = new com.itextpdf.text.pdf.PdfPTable(4);
            tablaPDF.addCell("ID Libro");
            tablaPDF.addCell("Título");
            tablaPDF.addCell("Cantidad");
            tablaPDF.addCell("Subtotal");
            
            // Recorremos la tabla visual (modeloTabla) para escribir los libros comprados en el PDF
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                tablaPDF.addCell(modeloTabla.getValueAt(i, 0).toString()); // ID
                tablaPDF.addCell(modeloTabla.getValueAt(i, 1).toString()); // Título
                tablaPDF.addCell(modeloTabla.getValueAt(i, 2).toString()); // Cantidad
                tablaPDF.addCell(modeloTabla.getValueAt(i, 4).toString()); // Subtotal
            }
            
            documento.add(tablaPDF);
            
            // 3. Total Final
            documento.add(new com.itextpdf.text.Paragraph("\nTOTAL ABONADO: $" + String.format("%.2f", totalFacturado)));
            
            documento.close();
            
            // 4. Intentamos abrir el PDF automáticamente en la pantalla del usuario
            java.awt.Desktop.getDesktop().open(new java.io.File(nombreArchivo));
            
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error al generar el PDF: " + e.getMessage(), 
                "Error PDF", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }    
}