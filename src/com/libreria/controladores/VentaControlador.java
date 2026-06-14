package com.libreria.controladores;

import com.libreria.dao.VentaDAO;
import com.libreria.dao.ClienteDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.modelos.Venta;
import com.libreria.modelos.DetalleVenta;
import com.libreria.modelos.Cliente;
import com.libreria.modelos.Libro;
import com.libreria.vistas.PanelVentas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class VentaControlador implements ActionListener {

    private PanelVentas vista;
    private VentaDAO ventaDao;
    private ClienteDAO clienteDao;
    private LibroDAO libroDao;

    // Estado interno del punto de venta (POS)
    private Cliente clienteActivo = null;
    private Libro libroActivo = null;
    private double totalVenta = 0.0;

    public VentaControlador(PanelVentas vista, VentaDAO ventaDao) {
        this.vista = vista;
        this.ventaDao = ventaDao;
        this.clienteDao = new ClienteDAO();
        this.libroDao = new LibroDAO();

        // Enchufamos los botones operativos
        this.vista.getBtnBuscarCliente().addActionListener(this);
        this.vista.getBtnBuscarLibro().addActionListener(this);
        this.vista.getBtnAgregar().addActionListener(this);
        this.vista.getBtnFacturar().addActionListener(this);
        this.vista.getBtnVaciarCarrito().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnBuscarCliente()) {
            buscarClienteDinamico();
        } else if (e.getSource() == vista.getBtnBuscarLibro()) {
            buscarLibroDinamico();
        } else if (e.getSource() == vista.getBtnAgregar()) {
            ejecutarAgregarProducto();
        } else if (e.getSource() == vista.getBtnFacturar()) {
            ejecutarFacturacion();
        } else if (e.getSource() == vista.getBtnVaciarCarrito()) {
            ejecutarVaciarCarrito();
        }
    }

    private void buscarClienteDinamico() {
        String filtro = vista.getTxtBuscarCliente().getText().trim();
        if (filtro.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un DNI o Apellido para buscar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Cliente> encontrados = clienteDao.buscarClientes(filtro);
            if (encontrados.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún cliente.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (encontrados.size() == 1) {
                seleccionarCliente(encontrados.get(0));
            } else {
                // Pop-up inteligente si hay múltiples coincidencias en la BD
                String[] opciones = new String[encontrados.size()];
                for (int i = 0; i < encontrados.size(); i++) {
                    opciones[i] = encontrados.get(i).getDni() + " - " + encontrados.get(i).getApellido() + " " + encontrados.get(i).getNombre();
                }
                String seleccion = (String) JOptionPane.showInputDialog(vista, "Múltiples clientes encontrados. Seleccione uno:", 
                        "Coincidencias Encontradas", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
                
                if (seleccion != null) {
                    int index = -1;
                    for (int i = 0; i < opciones.length; i++) {
                        if (opciones[i].equals(seleccion)) { index = i; break; }
                    }
                    seleccionarCliente(encontrados.get(index));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error en la consulta de clientes:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarCliente(Cliente c) {
        this.clienteActivo = c;
        vista.getLblClienteSeleccionado().setText("Cliente: " + c.getApellido() + " " + c.getNombre() + " (DNI: " + c.getDni() + ")");
        vista.getLblClienteSeleccionado().setForeground(new java.awt.Color(0, 120, 0)); // Pone el label en verde
    }

    private void buscarLibroDinamico() {
        String filtro = vista.getTxtBuscarLibro().getText().trim();
        if (filtro.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el ISBN o Título del libro.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Libro> encontrados = libroDao.buscarLibros(filtro);
            if (encontrados.isEmpty()) {
                JOptionPane.showMessageDialog(vista, "No se encontró ningún libro con ese criterio.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (encontrados.size() == 1) {
                seleccionarLibro(encontrados.get(0));
            } else {
                // Pop-up inteligente si hay títulos parecidos
                String[] opciones = new String[encontrados.size()];
                for (int i = 0; i < encontrados.size(); i++) {
                    opciones[i] = encontrados.get(i).getIsbn() + " - " + encontrados.get(i).getTitulo() + " ($" + encontrados.get(i).getPrecio() + ")";
                }
                String seleccion = (String) JOptionPane.showInputDialog(vista, "Múltiples libros encontrados. Seleccione uno:", 
                        "Catálogo Coincidente", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
                
                if (seleccion != null) {
                    int index = -1;
                    for (int i = 0; i < opciones.length; i++) {
                        if (opciones[i].equals(seleccion)) { index = i; break; }
                    }
                    seleccionarLibro(encontrados.get(index));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error en la consulta de libros:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarLibro(Libro l) {
        this.libroActivo = l;
        vista.getLblLibroSeleccionado().setText("Libro: " + l.getTitulo() + " (Stock: " + l.getStock() + ")");
        vista.getLblLibroSeleccionado().setForeground(new java.awt.Color(0, 120, 0)); // Pone el label en verde
    }

    private void ejecutarAgregarProducto() {
       if (clienteActivo == null) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un cliente antes de armar el carrito.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (libroActivo == null) {
            JOptionPane.showMessageDialog(vista, "Debe buscar y seleccionar un libro.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();

            int cantidadPrevia = 0;
            int filaExistente = -1;
            DefaultTableModel modeloTabla = vista.getModeloTabla();
            
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (Integer.parseInt(modeloTabla.getValueAt(i, 0).toString()) == libroActivo.getIdLibro()) {
                    cantidadPrevia = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString()); // Columna 3 es Cantidad
                    filaExistente = i;
                    break;
                }
            }

            int cantidadTotalDeseada = cantidadPrevia + cantidad;

            if (cantidadTotalDeseada > libroActivo.getStock()) {
                JOptionPane.showMessageDialog(vista, "Stock insuficiente en base de datos. Máximo disponible: " + libroActivo.getStock() + " unidades.", "Falta Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (filaExistente != -1) {
                double nuevoSubtotal = libroActivo.getPrecio() * cantidadTotalDeseada;
                modeloTabla.setValueAt(cantidadTotalDeseada, filaExistente, 3);
                modeloTabla.setValueAt("$" + nuevoSubtotal, filaExistente, 5); // Columna 5 es Subtotal
            } else {
                double subtotal = libroActivo.getPrecio() * cantidad;
                // Inyectamos el ID (oculto) y el ISBN (visible)
                modeloTabla.addRow(new Object[]{libroActivo.getIdLibro(), libroActivo.getIsbn(), libroActivo.getTitulo(), cantidad, "$" + libroActivo.getPrecio(), "$" + subtotal});
            }

            recalcularTotal();
            vista.getTxtCantidad().setText("1");
            vista.getTxtBuscarCliente().setEnabled(false);
            vista.getBtnBuscarCliente().setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número entero mayor a cero.", "Dato Incorrecto", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recalcularTotal() {
        totalVenta = 0.0;
        DefaultTableModel modelo = vista.getModeloTabla();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            String subtotalCelda = modelo.getValueAt(i, 5).toString().replace("$", "").trim(); // Columna 5
            totalVenta += Double.parseDouble(subtotalCelda);
        }
        vista.getLblTotal().setText("TOTAL A PAGAR: $" + String.format("%.2f", totalVenta)); 
    }

    private void ejecutarFacturacion() {
        DefaultTableModel modeloTabla = vista.getModeloTabla();
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "El carrito de compras está vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Confirmar la venta total por $" + String.format("%.2f", totalVenta) + "?", 
                "Confirmar Operación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            Venta nuevaVenta = new Venta(clienteActivo.getIdCliente(), new java.util.Date(), totalVenta);

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idLibro = Integer.parseInt(modeloTabla.getValueAt(i, 0).toString()); // Columna 0 (Oculta)
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 3).toString()); // Columna 3
                String precioCelda = modeloTabla.getValueAt(i, 4).toString().replace("$", "").trim(); // Columna 4
                double precioUnitario = Double.parseDouble(precioCelda);

                DetalleVenta detalle = new DetalleVenta(idLibro, cantidad, precioUnitario);
                nuevaVenta.agregarDetalle(detalle);
            }

            boolean exito = ventaDao.registrarVentaCompleta(nuevaVenta);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Venta procesada con éxito en la Base de Datos!\nEl stock se actualizó correctamente.", "Éxito transaccional", JOptionPane.INFORMATION_MESSAGE);
                generarFacturaPDF(clienteActivo.getApellido() + " " + clienteActivo.getNombre(), totalVenta);
                resetearPOS();
            }

        } catch (com.libreria.excepciones.StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error crítico en el motor relacional:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error general del sistema de facturación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // LÓGICA EXCLUSIVA DE ITEXT PARA EXPORTAR LA FACTURA ACTUAL A PDF
    // =========================================================================
    private void generarFacturaPDF(String nombreCliente, double totalFacturado) {
       try {
            // 1. Abrimos el JFileChooser de Windows para elegir la ruta de guardado
            JFileChooser selectorArchivo = new JFileChooser();
            selectorArchivo.setDialogTitle("Guardar Comprobante de Venta");
            
            // Limpiamos el nombre del cliente para sugerir un nombre de archivo válido
            String nombreArchivoSugerido = "Factura_" + nombreCliente.replaceAll("[^a-zA-Z0-9_-]", "") + "_" + System.currentTimeMillis() + ".pdf";
            selectorArchivo.setSelectedFile(new java.io.File(nombreArchivoSugerido));

            int seleccion = selectorArchivo.showSaveDialog(vista);

            if (seleccion == JFileChooser.APPROVE_OPTION) {
                java.io.File archivoDestino = selectorArchivo.getSelectedFile();
                com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
                
                // 2. Vinculamos el escritor de iText al archivo físico elegido
                com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(archivoDestino));
                documento.open();
                
                // 3. Damos formato local a la fecha (DD/MM/AAAA HH:mm)
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                String fechaLocal = sdf.format(new java.util.Date());
                
                // 4. Diseñamos el Encabezado Institucional corporativo
                com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph("LIBRERÍA EL ROBLE", com.itextpdf.text.FontFactory.getFont("Arial", 18, com.itextpdf.text.Font.BOLD));
                titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                documento.add(titulo);

                com.itextpdf.text.Paragraph subtitulo = new com.itextpdf.text.Paragraph("Comprobante Oficial de Venta\n\n", com.itextpdf.text.FontFactory.getFont("Arial", 12, com.itextpdf.text.Font.ITALIC));
                subtitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                documento.add(subtitulo);

                // Añadimos información del cliente y fecha (Usando NORMAL para compatibilidad de iText)
                documento.add(new com.itextpdf.text.Paragraph("Cliente: " + nombreCliente, com.itextpdf.text.FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.BOLD)));
                documento.add(new com.itextpdf.text.Paragraph("Fecha de Emisión: " + fechaLocal + "\n\n", com.itextpdf.text.FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.NORMAL)));
                
                // 5. Ajustamos el ancho de las columnas (el ISBN necesita más espacio que la cantidad)
                com.itextpdf.text.pdf.PdfPTable tablaPDF = new com.itextpdf.text.pdf.PdfPTable(4);
                tablaPDF.setWidthPercentage(100); // Forzamos a que ocupe todo el ancho
                tablaPDF.setWidths(new float[]{2.5f, 4f, 1f, 2f}); // Proporciones de las columnas
                
                // 6. Inyectamos las Cabeceras de las columnas con fondo gris sutil
                String[] cabeceras = {"ISBN", "Título", "Cant.", "Subtotal"};
                for (String textoCabecera : cabeceras) {
                    com.itextpdf.text.pdf.PdfPCell celdaCabecera = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(textoCabecera, com.itextpdf.text.FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.BOLD)));
                    celdaCabecera.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    celdaCabecera.setPadding(6);
                    celdaCabecera.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablaPDF.addCell(celdaCabecera);
                }
                
                // 7. Recorremos el carrito (JTable) e inyectamos los datos en el PDF
                DefaultTableModel modeloTabla = vista.getModeloTabla();
                for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                    String[] valoresCelda = {
                        modeloTabla.getValueAt(i, 1).toString(), // ISBN
                        modeloTabla.getValueAt(i, 2).toString(), // Título
                        modeloTabla.getValueAt(i, 3).toString(), // Cantidad
                        modeloTabla.getValueAt(i, 5).toString()  // Subtotal
                    };
                    
                    for (int col = 0; col < valoresCelda.length; col++) {
                        com.itextpdf.text.pdf.PdfPCell celdaDato = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(valoresCelda[col], com.itextpdf.text.FontFactory.getFont("Arial", 9, com.itextpdf.text.Font.NORMAL)));
                        celdaDato.setPadding(5);
                        
                        // Alineación: Cantidad y Subtotal al centro. Textos a la izquierda.
                        if (col == 2 || col == 3) {
                            celdaDato.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        } else {
                            celdaDato.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                        }
                        tablaPDF.addCell(celdaDato);
                    }
                }
                
                // Acoplamos la tabla al documento principal
                documento.add(tablaPDF);
                
                // 8. Agregamos el Total Abonado destacado al final alineado a la derecha
                com.itextpdf.text.Paragraph parrafoTotal = new com.itextpdf.text.Paragraph("\nTOTAL ABONADO: $" + String.format("%.2f", totalFacturado), com.itextpdf.text.FontFactory.getFont("Arial", 14, com.itextpdf.text.Font.BOLD));
                parrafoTotal.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                documento.add(parrafoTotal);
                
                documento.close();
                
                // 9. Automáticamente abrimos el PDF generado en pantalla usando la herramienta nativa de Windows
                java.awt.Desktop.getDesktop().open(archivoDestino);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al generar el PDF: " + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void ejecutarVaciarCarrito() {
        if (vista.getModeloTabla().getRowCount() == 0 && clienteActivo == null) {
            return; // No hace falta vaciar si ya está vacío
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Estás seguro de que querés vaciar el carrito y cancelar la venta actual?", 
                "Cancelar Venta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            resetearPOS();
        }
    }

    private void resetearPOS() {
        // Vaciamos la tabla y las variables lógicas
        vista.getModeloTabla().setRowCount(0);
        totalVenta = 0.0;
        clienteActivo = null;
        libroActivo = null;
        
        // Reseteamos las etiquetas de texto a rojo
        vista.getLblTotal().setText("TOTAL A PAGAR: $0.00");
        vista.getLblClienteSeleccionado().setText("Cliente: --- Ninguno Seleccionado ---");
        vista.getLblClienteSeleccionado().setForeground(new java.awt.Color(150, 0, 0));
        vista.getLblLibroSeleccionado().setText("Libro: --- Ninguno Seleccionado ---");
        vista.getLblLibroSeleccionado().setForeground(new java.awt.Color(150, 0, 0));
        
        // Limpiamos las barras de búsqueda y reactivamos al cliente
        vista.getTxtBuscarCliente().setText("");
        vista.getTxtBuscarLibro().setText("");
        vista.getTxtCantidad().setText("1");
        
        vista.getTxtBuscarCliente().setEnabled(true);
        vista.getBtnBuscarCliente().setEnabled(true);
    }
}