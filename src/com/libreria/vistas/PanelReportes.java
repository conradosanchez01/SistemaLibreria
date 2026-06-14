package com.libreria.vistas;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class PanelReportes extends javax.swing.JPanel {

    public PanelReportes() {
        initComponentsManual();
    }

    private void initComponentsManual() {
        // 1. Añadimos márgenes generosos alrededor de todo el panel (20px de aire)
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setLayout(new BorderLayout(0, 15)); // Espaciado vertical de 15px entre componentes

        // 2. Panel Superior para los controles (Filtros y Botones)
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBackground(this.getBackground());

        jComboBox1 = new JComboBox<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" });
        jButton1 = new JButton("Ventas Del Mes");
        jButton2 = new JButton("Ganancias Totales");
        jButton3 = new JButton("Top 3 Libros");
        
        // BOToN DE EXPORTACION A PDF
        btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.setBackground(new Color(180, 40, 40)); // Rojo 
        btnExportarPDF.setForeground(Color.WHITE);
        btnExportarPDF.setFont(new Font("Arial", Font.BOLD, 12));
        btnExportarPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
       
        lblGanancias = new JLabel("Total: $ 0,00");
        lblGanancias.setFont(new Font("Arial", Font.BOLD, 14));
        lblGanancias.setForeground(new Color(0, 102, 204)); // Azul destacado
        lblGanancias.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0)); // Separación interna

        panelControles.add(jComboBox1);
        panelControles.add(jButton1);
        panelControles.add(jButton2);
        panelControles.add(jButton3);
        panelControles.add(lblGanancias);
        panelControles.add(btnExportarPDF);

        // 3. Tabla de Datos Configurada para expandirse
        jTable1 = new JTable();
        jTable1.setRowHeight(25); // Filas más altas para que respiren los textos
        jTable1.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // ¡REQUISITO DE USUARIO! Desactivamos el auto-resize estricto para que respete 
        // nuestros tamaños calculados por contenido, pero habilitamos el rediseño manual.
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setResizingAllowed(true); // El usuario puede arrastrar columnas
        jTable1.getTableHeader().setReorderingAllowed(false); // Evita que desordenen las columnas arrastrándolas

        jScrollPane1 = new JScrollPane(jTable1);
        // Hacemos que el scrollbar horizontal aparezca solo si los textos son muy largos
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // 4. Armamos la distribución: Controles arriba, Tabla en el centro ocupando TODO el espacio restante
        this.add(panelControles, BorderLayout.NORTH);
        this.add(jScrollPane1, BorderLayout.CENTER);
    }

    // =========================================================================
    // ALGORITMO DE AJUSTE AUTOMÁTICO DE COLUMNAS POR CONTENIDO
    // =========================================================================
    public void ajustarColumnasPorContenido() {
        // Recorremos cada columna de la tabla
        for (int column = 0; column < jTable1.getColumnCount(); column++) {
            int width = 80; // Ancho mínimo por defecto
            
            // 1. Medimos el ancho del texto de la cabecera (Header)
            Object headerValue = jTable1.getColumnModel().getColumn(column).getHeaderValue();
            if (headerValue != null) {
                Component comp = jTable1.getTableHeader().getDefaultRenderer()
                    .getTableCellRendererComponent(jTable1, headerValue, false, false, -1, column);
                width = Math.max(width, comp.getPreferredSize().width + 25); // +25px de margen interno
            }
            
            // 2. Medimos el texto de cada celda de esa columna para encontrar el más largo
            for (int row = 0; row < jTable1.getRowCount(); row++) {
                Component comp = jTable1.getCellRenderer(row, column)
                    .getTableCellRendererComponent(jTable1, jTable1.getValueAt(row, column), false, false, row, column);
                width = Math.max(width, comp.getPreferredSize().width + 20); // +20px de margen interno
            }
            
            // 3. Le asignamos el ancho óptimo calculado (el usuario igual podrá estirarla manualmente)
            jTable1.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }

    // =========================================================
    // GETTERS SEMÁNTICOS PARA EL CONTROLADOR
    // =========================================================
    public JComboBox<String> getCbMeses() { return jComboBox1; }
    public JButton getBtnVentasMes() { return jButton1; }
    public JButton getBtnGananciasTotales() { return jButton2; }
    public JButton getBtnTopLibros() { return jButton3; }
    public JLabel getLblGanancias() { return lblGanancias; }
    public JTable getTablaReportes() { return jTable1; }
    public JButton getBtnExportarPDF() { return btnExportarPDF; }
    
    
    // Variables privadas del diseño
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblGanancias;
    private javax.swing.JButton btnExportarPDF;
}