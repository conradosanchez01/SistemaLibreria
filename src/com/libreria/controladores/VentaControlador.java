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
                    cantidadPrevia = Integer.parseInt(modeloTabla.getValueAt(i, 2).toString());
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
                modeloTabla.setValueAt(cantidadTotalDeseada, filaExistente, 2);
                modeloTabla.setValueAt("$" + nuevoSubtotal, filaExistente, 4);
            } else {
                double subtotal = libroActivo.getPrecio() * cantidad;
                modeloTabla.addRow(new Object[]{libroActivo.getIdLibro(), libroActivo.getTitulo(), cantidad, "$" + libroActivo.getPrecio(), "$" + subtotal});
            }

            recalcularTotal();
            vista.getTxtCantidad().setText("1");
            
            // Medida de seguridad POS: No dejar cambiar de cliente a mitad de la carga
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
            String subtotalCelda = modelo.getValueAt(i, 4).toString().replace("$", "").trim();
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
            Venta nuevaVenta = new Venta(clienteActivo.getIdCliente(), new Date(), totalVenta);

            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                int idLibro = Integer.parseInt(modeloTabla.getValueAt(i, 0).toString());
                int cantidad = Integer.parseInt(modeloTabla.getValueAt(i, 2).toString());
                String precioCelda = modeloTabla.getValueAt(i, 3).toString().replace("$", "").trim();
                double precioUnitario = Double.parseDouble(precioCelda);

                DetalleVenta detalle = new DetalleVenta(idLibro, cantidad, precioUnitario);
                nuevaVenta.agregarDetalle(detalle);
            }

            boolean exito = ventaDao.registrarVentaCompleta(nuevaVenta);

            if (exito) {
                JOptionPane.showMessageDialog(vista, "¡Venta procesada con éxito en la Base de Datos!\nEl stock se actualizó correctamente.", "Éxito transaccional", JOptionPane.INFORMATION_MESSAGE);

                generarFacturaPDF(clienteActivo.getApellido() + " " + clienteActivo.getNombre(), totalVenta);
                
                resetearPOS(); // Llamamos al metodo que creamos para limpiar todo y reutilizar codigo
            }

        } catch (com.libreria.excepciones.StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error crítico en el motor relacional:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error general del sistema de facturación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarFacturaPDF(String nombreCliente, double totalFacturado) {
        try {
            String nombreArchivo = "Factura_" + System.currentTimeMillis() + ".pdf";
            com.itextpdf.text.Document documento = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(documento, new java.io.FileOutputStream(nombreArchivo));
            
            documento.open();
            documento.add(new com.itextpdf.text.Paragraph("SISTEMA LIBRERIA - COMPROBANTE DE VENTA\n\n"));
            documento.add(new com.itextpdf.text.Paragraph("Cliente: " + nombreCliente));
            documento.add(new com.itextpdf.text.Paragraph("Fecha: " + new Date().toString() + "\n\n"));
            
            com.itextpdf.text.pdf.PdfPTable tablaPDF = new com.itextpdf.text.pdf.PdfPTable(4);
            tablaPDF.addCell("ID Libro");
            tablaPDF.addCell("Título");
            tablaPDF.addCell("Cantidad");
            tablaPDF.addCell("Subtotal");
            
            DefaultTableModel modeloTabla = vista.getModeloTabla();
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                tablaPDF.addCell(modeloTabla.getValueAt(i, 0).toString());
                tablaPDF.addCell(modeloTabla.getValueAt(i, 1).toString());
                tablaPDF.addCell(modeloTabla.getValueAt(i, 2).toString());
                tablaPDF.addCell(modeloTabla.getValueAt(i, 4).toString());
            }
            
            documento.add(tablaPDF);
            documento.add(new com.itextpdf.text.Paragraph("\nTOTAL ABONADO: $" + String.format("%.2f", totalFacturado)));
            documento.close();
            
            java.awt.Desktop.getDesktop().open(new java.io.File(nombreArchivo));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al escribir archivo de impresión: " + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
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