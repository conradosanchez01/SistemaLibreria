package com.libreria.controladores;

import com.libreria.dao.VentaDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.modelos.Venta;
import com.libreria.vistas.PanelReportes;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ReportesControlador {

    private final PanelReportes vista;
    private final VentaDAO ventaDAO;
    private final LibroDAO libroDAO;

    public ReportesControlador(PanelReportes vista, VentaDAO ventaDAO, LibroDAO libroDAO) {
        this.vista = vista;
        this.ventaDAO = ventaDAO;
        this.libroDAO = libroDAO;

        this.vista.getBtnVentasMes().addActionListener(e -> filtrarVentasPorMes());
        this.vista.getBtnGananciasTotales().addActionListener(e -> calcularGananciasTotales());
        this.vista.getBtnTopLibros().addActionListener(e -> calcularTop3Libros());
    }

    private void filtrarVentasPorMes() {
        int numeroMes = vista.getCbMeses().getSelectedIndex() + 1; 
        
        // Ahora el DAO nos devuelve directamente las filas armadas con los títulos
        List<Object[]> ventasFiltradas = ventaDAO.obtenerVentasPorMesDetalladas(numeroMes);
        
        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaReportes().getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"ID Venta", "Libros Vendidos", "Fecha", "Total ($)"});

        for (Object[] fila : ventasFiltradas) {
            modelo.addRow(fila); // Inyectamos la fila directa a la tabla
        }
    }

    private void calcularGananciasTotales() {
        // El DAO suma todo directamente en MySQL
        double total = ventaDAO.obtenerGananciasTotales();
        vista.getLblGanancias().setText(String.format("Total: $ %.2f", total));
    }

    private void calcularTop3Libros() {
        // El DAO cruza las tablas y nos devuelve las filas listas
        List<Object[]> top3 = libroDAO.obtenerTop3LibrosVendidos();
        
        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaReportes().getModel();
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(new Object[]{"ID Libro", "Titulo", "Detalle", "Cant. Vendida"});
        
        for (Object[] fila : top3) {
            modelo.addRow(fila);
        }
    }
}