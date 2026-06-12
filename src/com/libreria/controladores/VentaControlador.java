package com.libreria.controladores;

import com.libreria.dao.VentaDAO;
import com.libreria.dao.ClienteDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.modelos.Venta;
import com.libreria.modelos.DetalleVenta;
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
    
    private List<String> datosTecnicosClientes;
    private List<String> datosTecnicosLibros;
    private double totalVenta = 0.0;

    public VentaControlador(PanelVentas vista, VentaDAO ventaDao) {
        this.vista = vista;
        this.ventaDao = ventaDao;

        // Enlace de los escuchadores de eventos
        this.vista.getBtnAgregar().addActionListener(this);
        this.vista.getBtnFacturar().addActionListener(this);

        cargarCombosDinamicos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnAgregar()) {
            ejecutarAgregarProducto();
        } else if (e.getSource() == vista.getBtnFacturar()) {
            ejecutarFacturacion();
        }
    }

    private void cargarCombosDinamicos() {
        try {
            vista.getCbClientes().removeAllItems();
            ClienteDAO cDao = new ClienteDAO();
            datosTecnicosClientes = cDao.listarClientesCombo();
            
            for (String c : datosTecnicosClientes) {
                String[] partes = c.split("::");
                if (partes.length > 1) {
                    vista.getCbClientes().addItem(partes[1]);
                } else {
                    vista.getCbClientes().addItem(partes[0]);
                }
            }

            vista.getCbLibros().removeAllItems();
            LibroDAO lDao = new LibroDAO();
            datosTecnicosLibros = lDao.listarLibrosCombo();
            
            for (String l : datosTecnicosLibros) {
                String[] partes = l.split("::");
                if (partes[0].equals("0")) {
                    vista.getCbLibros().addItem(partes[1]);
                } else {
                    vista.getCbLibros().addItem(partes[1] + " ($" + partes[2] + ")");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, 
                "Error al cargar las listas desplegables desde la base de datos:\n" + ex.getMessage(), 
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarAgregarProducto() {
        int indexCliente = vista.getCbClientes().getSelectedIndex();
        int indexLibro = vista.getCbLibros().getSelectedIndex();

        if (indexCliente <= 0) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un cliente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (indexLibro <= 0) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un libro para agregar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int cantidad = Integer.parseInt(vista.getTxtCantidad().getText().trim());
            if (cantidad <= 0) {
                throw new NumberFormatException();
            }

            String libroSelect = datosTecnicosLibros.get(indexLibro);
            String[] partesLibro = libroSelect.split("::");
            
            int idLibro = Integer.parseInt(partesLibro[0]);
            String titulo = partesLibro[1];
            double precio = Double.parseDouble(partesLibro[2]);
            int stockDisponible = Integer.parseInt(partesLibro[3]);

            int cantidadPrevia = 0;
            int filaExistente = -1;
            DefaultTableModel modeloTabla = vista.getModeloTabla();
            
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (Integer.parseInt(modeloTabla.getValueAt(i, 0).toString()) == idLibro) {
                    cantidadPrevia = Integer.parseInt(modeloTabla.getValueAt(i, 2).toString());
                    filaExistente = i;
                    break;
                }
            }

            int cantidadTotalDeseada = cantidadPrevia + cantidad;

            if (cantidadTotalDeseada > stockDisponible) {
                JOptionPane.showMessageDialog(vista, 
                    "Stock insuficiente. Ya tenés " + cantidadPrevia + " en el carrito y el stock máximo es de " + stockDisponible + " unidades.", 
                    "Sin Stock", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (filaExistente != -1) {
                double nuevoSubtotal = precio * cantidadTotalDeseada;
                modeloTabla.setValueAt(cantidadTotalDeseada, filaExistente, 2);
                modeloTabla.setValueAt("$" + nuevoSubtotal, filaExistente, 4);
            } else {
                double subtotal = precio * cantidad;
                modeloTabla.addRow(new Object[]{idLibro, titulo, cantidad, "$" + precio, "$" + subtotal});
            }

            totalVenta = 0.0;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String subtotalCelda = modeloTabla.getValueAt(i, 4).toString().replace("$", "").trim();
                totalVenta += Double.parseDouble(subtotalCelda);
            }
            
            vista.getLblTotal().setText("TOTAL A PAGAR: $" + String.format("%.2f", totalVenta));
            vista.getTxtCantidad().setText("1");
            vista.getCbClientes().setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número entero mayor a cero.", "Error de Dato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ejecutarFacturacion() {
        DefaultTableModel modeloTabla = vista.getModeloTabla();
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "El carrito de compras se encuentra vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int indexCliente = vista.getCbClientes().getSelectedIndex();
        if (indexCliente <= 0) { 
            JOptionPane.showMessageDialog(vista, "Debe seleccionar un cliente válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String clienteTecnico = datosTecnicosClientes.get(indexCliente);
        int idCliente = Integer.parseInt(clienteTecnico.split("::")[0]);
        String nombreCliente = clienteTecnico.split("::")[1];

        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                "¿Confirmar la venta por un total de $" + String.format("%.2f", totalVenta) + "?", 
                "Confirmar Operación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return; 
        }

        try {
            Venta nuevaVenta = new Venta(idCliente, new Date(), totalVenta);

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
                JOptionPane.showMessageDialog(vista, 
                        "¡Venta registrada y facturada con éxito en MySQL!\nEl stock ha sido actualizado.", 
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);

                generarFacturaPDF(nombreCliente, totalVenta);
                
                modeloTabla.setRowCount(0);
                totalVenta = 0.0;
                vista.getLblTotal().setText("TOTAL A PAGAR: $0.00");
                vista.getTxtCantidad().setText("1");
                
                vista.getCbClientes().setEnabled(true);
                cargarCombosDinamicos(); 
            }

        } catch (com.libreria.excepciones.StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Stock Insuficiente", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error crítico en la Base de Datos:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error al procesar la factura: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(vista, "Error al generar el PDF: " + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
        }
    }
}