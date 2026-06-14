package com.libreria.controladores;

import com.libreria.dao.LibroDAO;
import com.libreria.dao.CategoriaDAO;
import com.libreria.modelos.Libro;
import com.libreria.vistas.PanelLibros;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.List;

public class LibroControlador implements ActionListener {

    private PanelLibros vista;
    private LibroDAO libroDao;
    private CategoriaDAO categoriaDao;
    
    private List<String> datosTecnicosCategorias;
    private int idLibroSeleccionado = -1;

    public LibroControlador(PanelLibros vista, LibroDAO libroDao) {
        this.vista = vista;
        this.libroDao = libroDao;
        this.categoriaDao = new CategoriaDAO();

        // Enchufamos los botones y la tabla a sus respectivos escuchadores de acciones
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnModificar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);
        this.vista.getBtnNuevaCategoria().addActionListener(this);
        
        // Escuchador para detectar cuando el usuario hace clic en una fila de la tabla
        this.vista.getTablaLibros().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarLibro();
            }
        });
        
        // Escuchador en tiempo real para la barra de búsqueda (KeyReleased)
        this.vista.getTxtBuscar().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarLibros();
            }
        });

        // =====================================================================
        // REFRESCO AUTOMÁTICO DE ESTADO 
        // Agregamos un vigilante al Panel. Cuando el JTabbedPane muestra esta pestaña,
        // se dispara el evento 'componentShown' y forzamos una recarga silenciosa de la BD.
        this.vista.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                cargarTablaLibros();
            }
        });

        // Carga inicial al momento de construir el controlador
        cargarCategoriasCombo();
        cargarTablaLibros();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarLibro();
        } else if (e.getSource() == vista.getBtnModificar()) {
            modificarLibro();
        } else if (e.getSource() == vista.getBtnEliminar()) {
            eliminarLibro();
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            limpiarCampos();
        } else if (e.getSource() == vista.getBtnNuevaCategoria()) {
            agregarNuevaCategoria();
        }
    }

    // Método que trae las categorías de la BD y las inyecta en el ComboBox
    private void cargarCategoriasCombo() {
        try {
            vista.getCbCategoria().removeAllItems();
            datosTecnicosCategorias = categoriaDao.listarCategoriasCombo();
            for (String cat : datosTecnicosCategorias) {
                vista.getCbCategoria().addItem(cat.split("::")[1]);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar categorías: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método central que lee el stock de MySQL y dibuja la tabla
    private void cargarTablaLibros() {
        try {
            DefaultTableModel modelo = vista.getModeloTabla();
            modelo.setRowCount(0); // Borra la "foto" vieja
            List<Libro> lista = libroDao.obtenerTodos(); // Saca la "foto" nueva
            for (Libro l : lista) {
                modelo.addRow(new Object[]{l.getIdLibro(), l.getIsbn(), l.getTitulo(), l.getAutor(), l.getPrecio(), l.getStock(), l.getIdCategoria()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar libros: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarLibro() {
        if (!validarCampos()) return; // Si algún campo está mal, abortamos la operación
        int indexCat = vista.getCbCategoria().getSelectedIndex();

        try {
            int idCategoria = Integer.parseInt(datosTecnicosCategorias.get(indexCat).split("::")[0]);
            
            Libro nuevo = new Libro(
                vista.getTxtIsbn().getText().trim(),
                vista.getTxtTitulo().getText().trim(),
                vista.getTxtAutor().getText().trim(),
                Double.parseDouble(vista.getTxtPrecio().getText().trim()),
                Integer.parseInt(vista.getTxtStock().getText().trim()),
                idCategoria
            );

            libroDao.insertar(nuevo);
            JOptionPane.showMessageDialog(vista, "Libro guardado con éxito.");
            limpiarCampos();
            cargarTablaLibros(); // Recarga visual post-guardado
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Volca los datos de la fila seleccionada a los campos de texto
    private void seleccionarLibro() {
        int fila = vista.getTablaLibros().getSelectedRow();
        if (fila == -1) return;

        idLibroSeleccionado = Integer.parseInt(vista.getTablaLibros().getValueAt(fila, 0).toString());
        vista.getTxtIsbn().setText(vista.getTablaLibros().getValueAt(fila, 1).toString());
        vista.getTxtTitulo().setText(vista.getTablaLibros().getValueAt(fila, 2).toString());
        vista.getTxtAutor().setText(vista.getTablaLibros().getValueAt(fila, 3).toString());
        vista.getTxtPrecio().setText(vista.getTablaLibros().getValueAt(fila, 4).toString());
        vista.getTxtStock().setText(vista.getTablaLibros().getValueAt(fila, 5).toString());
        
        // Empareja el ID de categoría oculto con el índice visual del ComboBox
        int idCatBusqueda = Integer.parseInt(vista.getTablaLibros().getValueAt(fila, 6).toString());
        for (int i = 0; i < datosTecnicosCategorias.size(); i++) {            
            int idActual = Integer.parseInt(datosTecnicosCategorias.get(i).split("::")[0]);
            if (idActual == idCatBusqueda) {
                vista.getCbCategoria().setSelectedIndex(i);
                break;
            }
        }
    }

    private void modificarLibro() {
        if (idLibroSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro de la tabla para modificar.");
            return;
        }
        if (!validarCampos()) return;
        int indexCat = vista.getCbCategoria().getSelectedIndex();

        try {
            int idCategoria = Integer.parseInt(datosTecnicosCategorias.get(indexCat).split("::")[0]);
            
            Libro editado = new Libro(
                idLibroSeleccionado,
                vista.getTxtIsbn().getText().trim(),
                vista.getTxtTitulo().getText().trim(),
                vista.getTxtAutor().getText().trim(),
                Double.parseDouble(vista.getTxtPrecio().getText().trim()),
                Integer.parseInt(vista.getTxtStock().getText().trim()),
                idCategoria
            );

            libroDao.modificar(editado);
            JOptionPane.showMessageDialog(vista, "Libro modificado con éxito.");
            limpiarCampos();
            cargarTablaLibros();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLibro() {
        if (idLibroSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro de la tabla para eliminar.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar este libro?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            libroDao.eliminar(idLibroSeleccionado);
            JOptionPane.showMessageDialog(vista, "Libro eliminado.");
            limpiarCampos();
            cargarTablaLibros();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error al eliminar en BD:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Devuelve el formulario a su estado original vacío
    private void limpiarCampos() {
        vista.getTxtIsbn().setText("");
        vista.getTxtTitulo().setText("");
        vista.getTxtAutor().setText("");
        vista.getTxtPrecio().setText("");
        vista.getTxtStock().setText("");
        vista.getCbCategoria().setSelectedIndex(0);
        idLibroSeleccionado = -1;
        vista.getTablaLibros().clearSelection();
    }

    // Barrera de seguridad para evitar datos corruptos
    private boolean validarCampos() {
        if (vista.getTxtIsbn().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El ISBN no puede estar vacío.");
            return false;
        }
        if (vista.getTxtTitulo().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El título no puede estar vacío.");
            return false;
        }
        if (vista.getTxtAutor().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El autor no puede estar vacío.");
            return false;
        }
        try {
            double precio = Double.parseDouble(vista.getTxtPrecio().getText().trim());
            if (precio <= 0) {
                JOptionPane.showMessageDialog(vista, "El precio debe ser mayor a 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El precio debe ser un número válido.");
            return false;
        }
        try {
            int stock = Integer.parseInt(vista.getTxtStock().getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(vista, "El stock no puede ser negativo.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "El stock debe ser un número entero.");
            return false;
        }
        if (vista.getCbCategoria().getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(vista, "Debe seleccionar una categoría válida.");
            return false;
        }
        return true;
    }
    
    private void agregarNuevaCategoria() {
        // 1. Le pedimos al usuario que escriba la categoría
        String nuevaCat = JOptionPane.showInputDialog(vista, "Ingrese el nombre de la nueva categoría:", "Nueva Categoría", JOptionPane.PLAIN_MESSAGE);

        // 2. Si no canceló y escribió algo, procedemos
        if (nuevaCat != null && !nuevaCat.trim().isEmpty()) {
            try {
                // 3. La guardamos en MySQL
                categoriaDao.insertarCategoria(nuevaCat.trim());

                // 4. Recargamos el ComboBox mágicamente para que aparezca
                cargarCategoriasCombo();

                // 5. Autoseleccionamos la categoría que acaba de crear para ahorrarle un clic
                vista.getCbCategoria().setSelectedItem(nuevaCat.trim());

                JOptionPane.showMessageDialog(vista, "Categoría agregada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(vista, "Error al guardar la categoría:\n" + ex.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filtrarLibros() {
        String texto = vista.getTxtBuscar().getText().trim();

        // Si la barra está vacía, volvemos a cargar todos los libros normales
        if (texto.isEmpty()) {
            try {
                DefaultTableModel modelo = vista.getModeloTabla();
                modelo.setRowCount(0);
                List<Libro> lista = libroDao.obtenerTodos(); 
                for (Libro l : lista) {
                    modelo.addRow(new Object[]{l.getIdLibro(), l.getIsbn(), l.getTitulo(), l.getAutor(), l.getPrecio(), l.getStock(), l.getIdCategoria()});
                }
            } catch (SQLException e) {
                // Failsafe silencioso para tipeo dinámico
            }
            return;
        }

        // Si hay texto, llamamos al método universal de búsqueda del DAO
        try {
            List<Libro> filtrados = libroDao.buscarLibros(texto);
            DefaultTableModel modelo = vista.getModeloTabla();
            modelo.setRowCount(0); // Limpiamos la tabla

            for (Libro l : filtrados) {
                modelo.addRow(new Object[]{
                    l.getIdLibro(), l.getIsbn(), l.getTitulo(), l.getAutor(), l.getPrecio(), l.getStock(), l.getIdCategoria()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(vista, "Error dinámico de búsqueda:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}