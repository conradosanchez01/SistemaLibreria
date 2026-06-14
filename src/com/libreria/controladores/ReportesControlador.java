package com.libreria.controladores;

import com.libreria.dao.VentaDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.vistas.PanelReportes;

// IMPORTACIONES DE ITEXT PARA LA GENERACIÓN DE PDF
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ReportesControlador {

    private final PanelReportes vista;
    private final VentaDAO ventaDAO;
    private final LibroDAO libroDAO;

    // =========================================================================
    // CONSTRUCTOR DEL CONTROLADOR
    // =========================================================================
    // El constructor vincula la vista del módulo con los DAOs (Base de Datos) del sistema
    public ReportesControlador(PanelReportes vista, VentaDAO ventaDAO, LibroDAO libroDAO) {
        this.vista = vista;
        this.ventaDAO = ventaDAO;
        this.libroDAO = libroDAO;

        // Enlazamos las acciones de los botones de la interfaz a nuestros métodos lógicos (Event Listeners)
        this.vista.getBtnVentasMes().addActionListener(e -> filtrarVentasPorMes());
        this.vista.getBtnGananciasTotales().addActionListener(e -> calcularGananciasTotales());
        this.vista.getBtnTopLibros().addActionListener(e -> calcularTop3Libros());
        
        // Enlazamos el botón de exportación para generar el PDF de la vista actual
        this.vista.getBtnExportarPDF().addActionListener(e -> exportarAPDF());
    }

    // =========================================================================
    // LÓGICA DE BOTONES Y FILTROS
    // =========================================================================

    // Acción para el Botón: "Ventas Del Mes"
    private void filtrarVentasPorMes() {
        // Los ComboBox de NetBeans son arreglos que empiezan en el índice 0 (0=Enero, 1=Febrero). 
        // Le sumamos 1 porque la base de datos MySQL cuenta los meses desde el 1 al 12.
        int numeroMes = vista.getCbMeses().getSelectedIndex() + 1; 
        
        // El DAO hace la consulta JOIN pesada en SQL y nos devuelve los tickets de ese mes específico
        java.util.List<Object[]> ventasFiltradas = ventaDAO.obtenerVentasPorMesDetalladas(numeroMes);
        
        // Obtenemos el modelo de la tabla para poder manipular sus filas
        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaReportes().getModel();
        modelo.setRowCount(0); // Reiniciamos/limpiamos la tabla borrando todo el contenido previo
        
        // Configuramos las cabeceras exactas para este tipo de reporte
        modelo.setColumnIdentifiers(new Object[]{"ID Venta", "Libros Vendidos", "Fecha", "Total Abonado"});

        double totalDelMes = 0.0; // Acumulador para saber cuánto ganamos en este mes

        // Recorremos la lista que nos trajo la base de datos
        for (Object[] fila : ventasFiltradas) {
            // Extraemos el total crudo (Double) de la columna 3 antes de modificarla
            double totalTicket = (Double) fila[3]; 
            
            // Sumamos el dinero crudo al acumulador del mes
            totalDelMes += totalTicket; 
            
            // ¡NUEVO! Reemplazamos el Double crudo de la fila por nuestro String formateado (Ej: $ 1.500,00)
            // Hacemos esto ANTES de meterlo a la tabla para que se vea lindo tanto en pantalla como en el PDF
            fila[3] = formatearDinero(totalTicket);
            
            // Inyectamos la fila procesada a la tabla visual
            modelo.addRow(fila); 
        }
        
        // Actualizamos la etiqueta de texto usando nuestro formateador estético
        vista.getLblGanancias().setText("Total del Mes: " + formatearDinero(totalDelMes));
        
        // Llamamos a la función matemática de la Vista para que estire las columnas según el largo del texto
        vista.ajustarColumnasPorContenido();
    }

    // Acción para el Botón: "Ganancias Totales"
    private void calcularGananciasTotales() {
        // Le pedimos al DAO que haga un SELECT SUM() gigante en MySQL
        double total = ventaDAO.obtenerGananciasTotales();
        
        // Actualizamos directamente la etiqueta con el total de toda la vida de la librería
        vista.getLblGanancias().setText("Total Histórico: " + formatearDinero(total));
    }

    // Acción para el Botón: "Top 3 Libros"
    private void calcularTop3Libros() {
        // El DAO cruza la tabla de libros con los detalles de venta, suma cantidades y las ordena (LIMIT 3)
        java.util.List<Object[]> top3 = libroDAO.obtenerTop3LibrosVendidos();
        
        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaReportes().getModel();
        modelo.setRowCount(0); // Vaciamos la tabla
        
        // Re-etiquetamos las cabeceras porque los datos que vamos a mostrar ahora son distintos
        modelo.setColumnIdentifiers(new Object[]{"ID Libro", "Titulo", "Detalle", "Cant. Vendida"});
        
        for (Object[] fila : top3) {
            modelo.addRow(fila); // Las cantidades son números enteros simples, no necesitan formato de dinero
        }
        
        // Al estar viendo un historial general (Top 3), sincronizamos la etiqueta al dinero histórico
        double totalHistorico = ventaDAO.obtenerGananciasTotales();
        vista.getLblGanancias().setText("Total Histórico: " + formatearDinero(totalHistorico));
        
        // Auto-ajustamos el ancho de las columnas
        vista.ajustarColumnasPorContenido();
    }

    // =========================================================================
    // HERRAMIENTAS INTERNAS DEL CONTROLADOR
    // =========================================================================

    /**
     * MÉTOD AUXILIAR PARA FORMATEO DE DINERO
     * Toma un número crudo (Ej: 1530200.5) y lo transforma en un texto contable (Ej: $ 1.530.200,50).
     * Obligamos al sistema a usar puntos para los miles y comas para los decimales.
     */
    private String formatearDinero(double monto) {
        // 1. Instanciamos la configuración de símbolos
        java.text.DecimalFormatSymbols simbolos = new java.text.DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.'); // Marcamos el punto como separador de miles/millones
        simbolos.setDecimalSeparator(',');  // Marcamos la coma para separar los centavos
        
        // 2. Creamos la máscara del formato: $ (Moneda), #,##0 (Agrupación), .00 (Forzamos 2 decimales siempre)
        java.text.DecimalFormat formato = new java.text.DecimalFormat("$ #,##0.00", simbolos);
        
        // 3. Devolvemos el texto listo para usar en la interfaz
        return formato.format(monto);
    }


    // =========================================================================
    // LÓGICA EXCLUSIVA DE ITEXT PARA EXPORTAR A PDF
    // =========================================================================
    private void exportarAPDF() {
        DefaultTableModel modelo = (DefaultTableModel) vista.getTablaReportes().getModel();
        
        // REGLA DE SEGURIDAD: Evitamos generar un documento en blanco si el usuario no hizo ninguna búsqueda
        if (modelo.getRowCount() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay datos en la tabla para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // LÓGICA INTELIGENTE: Verificamos el nombre de la primera columna para deducir qué reporte estamos viendo
        boolean esReporteMensual = modelo.getColumnName(0).equals("ID Venta");
        String mesSeleccionado = vista.getCbMeses().getSelectedItem().toString();

        // 1. Abrimos el JFileChooser (Ventana nativa de Windows) para elegir dónde guardar el archivo
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle("Guardar Reporte Estadístico");
        
        // 2. Asignamos un nombre de archivo dinámico sugerido según el reporte en pantalla
        String nombreArchivoSugerido = esReporteMensual ? "Reporte_Mensual_" + mesSeleccionado + ".pdf" : "Reporte_Top3_Historico.pdf";
        selectorArchivo.setSelectedFile(new File(nombreArchivoSugerido));

        // Pausamos la ejecución hasta que el usuario elija la ruta y presione "Guardar"
        int seleccion = selectorArchivo.showSaveDialog(vista);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivoDestino = selectorArchivo.getSelectedFile();
            Document documento = new Document(); // Instanciamos el lienzo en blanco del PDF

            try {
                // 3. Vinculamos el motor de escritura de iText al archivo físico en el disco duro
                PdfWriter.getInstance(documento, new FileOutputStream(archivoDestino));
                documento.open(); // Empezamos a editar el documento

                // 4. Diseñamos el Encabezado Institucional del PDF (Centrado y en Negrita)
                Paragraph titulo = new Paragraph("LIBRERÍA EL ROBLE", FontFactory.getFont("Arial", 18, com.itextpdf.text.Font.BOLD));
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);

                // 5. Lógica condicional del Subtítulo: Imprimimos textos diferentes según el tipo de reporte
                if (esReporteMensual) {
                    Paragraph subtitulo = new Paragraph("Reporte Estadístico Gerencial - Historial Mensual\n\n", FontFactory.getFont("Arial", 12, com.itextpdf.text.Font.ITALIC));
                    subtitulo.setAlignment(Element.ALIGN_CENTER);
                    documento.add(subtitulo);
                    // Aclaramos el mes filtrado usando Font.NORMAL para que no de error la librería
                    documento.add(new Paragraph("Filtro Aplicado o Mes de Consulta: " + mesSeleccionado, FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.NORMAL)));
                } else {
                    Paragraph subtitulo = new Paragraph("Reporte Estadístico Gerencial - Top 3 Libros\n\n", FontFactory.getFont("Arial", 12, com.itextpdf.text.Font.ITALIC));
                    subtitulo.setAlignment(Element.ALIGN_CENTER);
                    documento.add(subtitulo);
                    documento.add(new Paragraph("Filtro Aplicado: Histórico Completo", FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.NORMAL)));
                }

                // Inyectamos el label monetario exactamente como se ve en pantalla (ya tiene el formateo lindo)
                documento.add(new Paragraph(vista.getLblGanancias().getText() + "\n\n", FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.BOLD)));

                // 6. Construimos la Tabla virtual en iText calcando la estructura de la Vista
                int columnasTotales = modelo.getColumnCount();
                PdfPTable tablaPdf = new PdfPTable(columnasTotales);
                tablaPdf.setWidthPercentage(100); // Forzamos a que la tabla ocupe el 100% del ancho de la hoja A4

                // 7. Renderizamos las Cabeceras de las columnas con fondo gris corporativo
                for (int i = 0; i < columnasTotales; i++) {
                    PdfPCell celdaCabecera = new PdfPCell(new Phrase(modelo.getColumnName(i), FontFactory.getFont("Arial", 10, com.itextpdf.text.Font.BOLD)));
                    celdaCabecera.setHorizontalAlignment(Element.ALIGN_CENTER);
                    celdaCabecera.setPadding(6);
                    celdaCabecera.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                    tablaPdf.addCell(celdaCabecera);
                }

                // 8. Doble Bucle (Filas y Columnas) para volcar los datos reales al PDF
                for (int row = 0; row < modelo.getRowCount(); row++) {
                    for (int col = 0; col < columnasTotales; col++) {
                        Object valorCelda = modelo.getValueAt(row, col);
                        // Evitamos errores de puntero nulo convirtiendo todo a String seguro
                        String textoCelda = (valorCelda != null) ? valorCelda.toString() : "";
                        
                        PdfPCell celdaDato = new PdfPCell(new Phrase(textoCelda, FontFactory.getFont("Arial", 9, com.itextpdf.text.Font.NORMAL)));
                        celdaDato.setPadding(5);
                        
                        // Si es la columna de cantidades/totales (índice 3) o ID (índice 0), la alineamos al centro
                        if (col == 0 || col == 3) {
                            celdaDato.setHorizontalAlignment(Element.ALIGN_CENTER);
                        } else {
                            celdaDato.setHorizontalAlignment(Element.ALIGN_LEFT); // Textos largos a la izquierda
                        }
                        
                        tablaPdf.addCell(celdaDato);
                    }
                }

                // 9. Acoplamos la tabla ya construida al documento principal y cerramos el flujo de datos
                documento.add(tablaPdf);
                documento.close();

                // 10. Operación finalizada: Mensaje de éxito y apertura automática del archivo con la app nativa del usuario
                JOptionPane.showMessageDialog(vista, "¡Reporte PDF generado con éxito!", "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                java.awt.Desktop.getDesktop().open(archivoDestino);

            } catch (Exception ex) {
                // Bloque de captura para evitar que el programa crashee si el PDF está abierto o no hay permisos
                JOptionPane.showMessageDialog(vista, "Error crítico al compilar el PDF: " + ex.getMessage(), "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}