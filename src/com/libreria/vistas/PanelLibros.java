package com.libreria.vistas;

import com.libreria.dao.LibroDAO;
import com.libreria.modelos.Libro;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class PanelLibros extends JPanel {

    // Componentes visuales
    private JTextField txtTitulo, txtAutor, txtPrecio, txtStock;
    private JButton btnGuardar, btnModificar, btnEliminar, btnLimpiar;
    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;
    private int idLibroSeleccionado = -1; // Para saber qué libro editar/borrar

    private LibroDAO dao = new LibroDAO();

    public PanelLibros() {
        // Configurar el diseño principal del Panel
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. PANEL DE FORMULARIO (Arriba)
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Libro"));

        panelFormulario.add(new JLabel("Título:"));
        txtTitulo = new JTextField();
        panelFormulario.add(txtTitulo);

        panelFormulario.add(new JLabel("Autor:"));
        txtAutor = new JTextField();
        panelFormulario.add(txtAutor);

        panelFormulario.add(new JLabel("Precio:"));
        txtPrecio = new JTextField();
        panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel("Stock:"));
        txtStock = new JTextField();
        panelFormulario.add(txtStock);

        this.add(panelFormulario, BorderLayout.NORTH);

        // 2. PANEL DE TABLA (Centro)
       modeloTabla = new DefaultTableModel(new String[]{"ID", "Título", "Autor", "Precio", "Stock"}, 0) {
         @Override
         public boolean isCellEditable(int row, int column) {
             return false; // Esto bloquea la edición directa en la tabla
         }
     };
        tablaLibros = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaLibros);
        this.add(scrollTabla, BorderLayout.CENTER);

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

        // 4. EVENTOS DE LOS BOTONES Y TABLA
        eventosBotones();
        cargarTabla(); // Cargar los datos al iniciar
    }

    // --- REQUISITO: VALIDACIÓN FAIL-FAST ---
    private void validarCampos() throws IllegalArgumentException {
        if (txtTitulo.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (txtAutor.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido.");
        }
        try {
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo.");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El stock debe ser un número entero.");
        }
    }

    // --- LÓGICA DE LOS BOTONES ---
    private void eventosBotones() {
        // BOTÓN GUARDAR
        btnGuardar.addActionListener(e -> {
            try {
                validarCampos(); // Llama al Fail-fast
                Libro nuevoLibro = new Libro(txtTitulo.getText(), txtAutor.getText(), 
                        Double.parseDouble(txtPrecio.getText()), Integer.parseInt(txtStock.getText()), 1);
                
                if (dao.insertar(nuevoLibro)) {
                    JOptionPane.showMessageDialog(this, "Libro guardado con éxito.");
                    limpiarCampos();
                    cargarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar en BD.");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
            }
        });

        // BOTÓN MODIFICAR
        btnModificar.addActionListener(e -> {
            if (idLibroSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para modificar.");
                return;
            }
            try {
                validarCampos();
                Libro libroEditado = new Libro(idLibroSeleccionado, txtTitulo.getText(), txtAutor.getText(), 
                        Double.parseDouble(txtPrecio.getText()), Integer.parseInt(txtStock.getText()), 1);
                
                if (dao.modificar(libroEditado)) {
                    JOptionPane.showMessageDialog(this, "Libro modificado con éxito.");
                    limpiarCampos();
                    cargarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al modificar en BD.");
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
            }
        });

        // BOTÓN ELIMINAR
        btnEliminar.addActionListener(e -> {
            if (idLibroSeleccionado == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para eliminar.");
                return;
            }
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este libro?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (dao.eliminar(idLibroSeleccionado)) {
                    JOptionPane.showMessageDialog(this, "Libro eliminado.");
                    limpiarCampos();
                    cargarTabla();
                }
            }
        });

        // BOTÓN LIMPIAR
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // CLIC EN LA TABLA (Para pasar datos a los campos de texto)
        tablaLibros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaLibros.getSelectedRow();
                if (fila != -1) {
                    idLibroSeleccionado = Integer.parseInt(tablaLibros.getValueAt(fila, 0).toString());
                    txtTitulo.setText(tablaLibros.getValueAt(fila, 1).toString());
                    txtAutor.setText(tablaLibros.getValueAt(fila, 2).toString());
                    txtPrecio.setText(tablaLibros.getValueAt(fila, 3).toString());
                    txtStock.setText(tablaLibros.getValueAt(fila, 4).toString());
                }
            }
        });
    }

    // --- MÉTODOS AUXILIARES ---
    private void cargarTabla() {
        modeloTabla.setRowCount(0); // Vaciar tabla
        List<Libro> lista = dao.obtenerTodos();
        for (Libro l : lista) {
            modeloTabla.addRow(new Object[]{l.getIdLibro(), l.getTitulo(), l.getAutor(), l.getPrecio(), l.getStock()});
        }
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        idLibroSeleccionado = -1;
        tablaLibros.clearSelection();
    }
}