package com.libreria.vistas;

import com.libreria.dao.ClienteDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.controladores.ClienteControlador;
import com.libreria.controladores.VentaControlador;
import com.libreria.controladores.LibroControlador;

public class VentanaPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    public VentanaPrincipal() {
        initComponents();
        
        // Configuraciones dinámicas de la ventana (de main)
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new java.awt.Dimension(1000, 700));

        // 1. Al arrancar, SOLO cargamos la pestaña de Login (de feature-login)
        PanelLogin panelLog = new PanelLogin();
        jTabbedPane1.addTab("Iniciar Sesión", panelLog);
    }

    // Método invocado desde el PanelLogin al acertar la contraseña
    public void habilitarModulos(String rol) {
        // 1. Borramos la pestaña de Login
        jTabbedPane1.removeAll();

        // 2. Módulos base para TODOS (Cargamos las vistas y controladores reales)
        PanelVentas vistaVentas = new PanelVentas();
        VentaDAO daoVentas = new VentaDAO();
        new VentaControlador(vistaVentas, daoVentas);
        jTabbedPane1.addTab("Punto de Venta", vistaVentas);

        PanelClientes vistaClientes = new PanelClientes();
        ClienteDAO daoClientes = new ClienteDAO();
        new ClienteControlador(vistaClientes, daoClientes);
        jTabbedPane1.addTab("Gestión de Clientes", vistaClientes);

        // 3. Control de acceso estricto: Solo DUEÑO ve el inventario
        if (rol.equals("DUEÑO")) {
            PanelLibros vistaLibros = new PanelLibros();
            LibroDAO daoLibros = new LibroDAO();
            new LibroControlador(vistaLibros, daoLibros);
            jTabbedPane1.addTab("Inventario de Libros", vistaLibros);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    // Variables declaration - do not modify                     
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration                   
}