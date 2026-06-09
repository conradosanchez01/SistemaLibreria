package com.libreria.vistas;

import com.libreria.dao.ClienteDAO;
import com.libreria.modelo.Cliente;
import com.libreria.excepciones.ClienteDuplicadoException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelClientes extends JPanel {

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtEmail;

    private JButton btnGuardar;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tablaClientes;

    private int idClienteSeleccionado = -1;

    public PanelClientes() {

        setLayout(new BorderLayout());

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));

        panelFormulario.add(new JLabel("Nombre"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Apellido"));
        txtApellido = new JTextField();
        panelFormulario.add(txtApellido);

        panelFormulario.add(new JLabel("DNI"));
        txtDni = new JTextField();
        panelFormulario.add(txtDni);

        panelFormulario.add(new JLabel("Email"));
        txtEmail = new JTextField();
        panelFormulario.add(txtEmail);

        add(panelFormulario, BorderLayout.NORTH);

        String[] columnas = {
                "ID",
                "Nombre",
                "Apellido",
                "DNI",
                "Email"
        };

        DefaultTableModel modelo =
                new DefaultTableModel(columnas, 0);

        tablaClientes = new JTable(modelo);

        JScrollPane scroll =
                new JScrollPane(tablaClientes);

        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();

        btnGuardar = new JButton("Guardar");
        btnModificar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnModificar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        cargarClientes();

        btnGuardar.addActionListener(e -> guardarCliente());
        btnModificar.addActionListener(e -> modificarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarCliente();
            }
        });
    }

    private void cargarClientes() {

        ClienteDAO dao = new ClienteDAO();

        List<Cliente> clientes = dao.consultarTodos();

        DefaultTableModel modelo =
                (DefaultTableModel) tablaClientes.getModel();

        modelo.setRowCount(0);

        for (Cliente c : clientes) {

            modelo.addRow(new Object[]{
                    c.getIdCliente(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getDni(),
                    c.getEmail()
            });
        }
    }

    private void guardarCliente() {

    if(txtNombre.getText().trim().isEmpty()){

        JOptionPane.showMessageDialog(
                this,
                "El nombre es obligatorio"
        );

        return;
    }

    if(txtApellido.getText().trim().isEmpty()){

        JOptionPane.showMessageDialog(
                this,
                "El apellido es obligatorio"
        );

        return;
    }

    if(!txtDni.getText().matches("\\d{7,8}")){

        JOptionPane.showMessageDialog(
                this,
                "El DNI debe tener 7 u 8 numeros"
        );

        return;
    }

    try {

        Cliente cliente = new Cliente();

        cliente.setNombre(txtNombre.getText());
        cliente.setApellido(txtApellido.getText());
        cliente.setDni(txtDni.getText());
        cliente.setEmail(txtEmail.getText());

        ClienteDAO dao = new ClienteDAO();

        dao.insertar(cliente);

        JOptionPane.showMessageDialog(
                this,
                "Cliente guardado correctamente"
        );

        cargarClientes();
        limpiarCampos();

    } catch (ClienteDuplicadoException e) {

        JOptionPane.showMessageDialog(
                this,
                e.getMessage()
        );
    }
}

    private void seleccionarCliente() {

        int fila = tablaClientes.getSelectedRow();

        if (fila == -1) {
            return;
        }

        idClienteSeleccionado =
                (int) tablaClientes.getValueAt(fila, 0);

        txtNombre.setText(
                tablaClientes.getValueAt(fila, 1).toString()
        );

        txtApellido.setText(
                tablaClientes.getValueAt(fila, 2).toString()
        );

        txtDni.setText(
                tablaClientes.getValueAt(fila, 3).toString()
        );

        txtEmail.setText(
                tablaClientes.getValueAt(fila, 4).toString()
        );
    }

    private void modificarCliente() {

        if (idClienteSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente"
            );

            return;
        }

        Cliente cliente = new Cliente();

        cliente.setIdCliente(idClienteSeleccionado);
        cliente.setNombre(txtNombre.getText());
        cliente.setApellido(txtApellido.getText());
        cliente.setDni(txtDni.getText());
        cliente.setEmail(txtEmail.getText());

        ClienteDAO dao = new ClienteDAO();

        dao.modificar(cliente);

        JOptionPane.showMessageDialog(
                this,
                "Cliente modificado correctamente"
        );

        cargarClientes();
        limpiarCampos();
    }

    private void eliminarCliente() {

        if (idClienteSeleccionado == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione un cliente"
            );

            return;
        }

        ClienteDAO dao = new ClienteDAO();

        dao.eliminar(idClienteSeleccionado);

        JOptionPane.showMessageDialog(
                this,
                "Cliente eliminado correctamente"
        );

        cargarClientes();
        limpiarCampos();
    }

    private void limpiarCampos() {

        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtEmail.setText("");

        idClienteSeleccionado = -1;
    }
}