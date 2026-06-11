package com.libreria.controladores;

import com.libreria.dao.ClienteDAO;
import com.libreria.excepciones.ClienteDuplicadoException;
import com.libreria.modelos.Cliente;
import com.libreria.vistas.PanelClientes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class ClienteControlador implements ActionListener {

    private ClienteDAO dao;
    private PanelClientes vista;
    private int idClienteSeleccionado = -1;

    public ClienteControlador(PanelClientes vista, ClienteDAO dao) {
        this.vista = vista;
        this.dao = dao;

        // "Enchufamos" los botones de la vista a este controlador
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnModificar().addActionListener(this);
        this.vista.getBtnEliminar().addActionListener(this);
        this.vista.getBtnLimpiar().addActionListener(this);

        // "Enchufamos" la tabla para detectar clics
        this.vista.getTablaClientes().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCliente();
            }
        });

        // Cargamos la tabla al iniciar
        cargarClientes();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarCliente();
        } else if (e.getSource() == vista.getBtnModificar()) {
            modificarCliente();
        } else if (e.getSource() == vista.getBtnEliminar()) {
            eliminarCliente();
        } else if (e.getSource() == vista.getBtnLimpiar()) {
            limpiarCampos();
        }
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = dao.consultarTodos();
            DefaultTableModel modelo = vista.getModelo();
            modelo.setRowCount(0); // Limpia la tabla visual

            for (Cliente c : clientes) {
                modelo.addRow(new Object[]{
                        c.getIdCliente(), c.getNombre(), c.getApellido(), c.getDni(), c.getEmail()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar la lista de clientes:\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarCliente() {
        if (!validarCampos()) return;

        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(vista.getTxtNombre().getText());
            cliente.setApellido(vista.getTxtApellido().getText());
            cliente.setDni(vista.getTxtDni().getText());
            cliente.setEmail(vista.getTxtEmail().getText());

            dao.insertar(cliente);

            JOptionPane.showMessageDialog(vista, "Cliente guardado correctamente");
            cargarClientes();
            limpiarCampos();

        } catch (ClienteDuplicadoException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Dato Duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error crítico al guardar en la BD:\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarCliente() {
        int fila = vista.getTablaClientes().getSelectedRow();
        if (fila == -1) return;

        idClienteSeleccionado = (int) vista.getTablaClientes().getValueAt(fila, 0);
        vista.getTxtNombre().setText(vista.getTablaClientes().getValueAt(fila, 1).toString());
        vista.getTxtApellido().setText(vista.getTablaClientes().getValueAt(fila, 2).toString());
        vista.getTxtDni().setText(vista.getTablaClientes().getValueAt(fila, 3).toString());
        vista.getTxtEmail().setText(vista.getTablaClientes().getValueAt(fila, 4).toString());
    }

    private void modificarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla");
            return;
        }
        if (!validarCampos()) return;

        try {
            Cliente cliente = new Cliente();
            cliente.setIdCliente(idClienteSeleccionado);
            cliente.setNombre(vista.getTxtNombre().getText());
            cliente.setApellido(vista.getTxtApellido().getText());
            cliente.setDni(vista.getTxtDni().getText());
            cliente.setEmail(vista.getTxtEmail().getText());

            dao.modificar(cliente);

            JOptionPane.showMessageDialog(vista, "Cliente modificado correctamente");
            cargarClientes();
            limpiarCampos();

        } catch (ClienteDuplicadoException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Dato Duplicado", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error crítico al modificar:\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarCliente() {
        if (idClienteSeleccionado == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente de la tabla");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista, "¿Seguro que desea eliminar este cliente?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try {
            dao.eliminar(idClienteSeleccionado);
            JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente");
            cargarClientes();
            limpiarCampos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(vista, "Error al eliminar en la BD:\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        vista.getTxtNombre().setText("");
        vista.getTxtApellido().setText("");
        vista.getTxtDni().setText("");
        vista.getTxtEmail().setText("");
        idClienteSeleccionado = -1;
        vista.getTablaClientes().clearSelection();
    }

    private boolean validarCampos() {
        if (vista.getTxtNombre().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El nombre es obligatorio");
            return false;
        }
        if (vista.getTxtApellido().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "El apellido es obligatorio");
            return false;
        }
        if (!vista.getTxtDni().getText().matches("\\d{7,8}")) {
            JOptionPane.showMessageDialog(vista, "El DNI debe tener 7 u 8 números");
            return false;
        }
        return true;
    }
}