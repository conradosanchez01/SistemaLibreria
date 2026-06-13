package com.libreria.vistas;

import com.libreria.dao.ClienteDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.dao.LibroDAO;
import com.libreria.controladores.ClienteControlador;
import com.libreria.controladores.VentaControlador;
import com.libreria.controladores.LibroControlador;
import javax.swing.JOptionPane;

public class VentanaPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaPrincipal.class.getName());

    public VentanaPrincipal() {
        initComponents();
        
       // Configuraciones dinámicas de la ventana (de main)
       this.setTitle("Sistema de Gestión - Librería");
        this.setSize(1000, 700); // 1. Primero le damos el tamaño real
        this.setMinimumSize(new java.awt.Dimension(1000, 700)); // 2. Bloqueamos para que no la achiquen
        this.setLocationRelativeTo(null); // 3. AHORA SÍ, centramos la caja gigante

        // 1. Al arrancar, llamamos al método que construye el Login
        cargarPantallaLogin();
    }

    // Extraje esta lógica a un método para reutilizarla al cerrar sesión
    private void cargarPantallaLogin() {
        // Limpiamos todo rastro de la sesión anterior
        jTabbedPane1.removeAll();
        this.setJMenuBar(null); // Ocultamos la barra superior si estaba visible
        
        // Refrescamos la ventana para que aplique los cambios
        this.revalidate();
        this.repaint();

        // Creamos y acoplamos el módulo de Login MVC
        PanelLogin panelLog = new PanelLogin();
        com.libreria.dao.UsuarioDAO daoUsuario = new com.libreria.dao.UsuarioDAO();
        new com.libreria.controladores.UsuarioControlador(panelLog, daoUsuario);
        
        jTabbedPane1.addTab("Iniciar Sesión", panelLog);
    }

    // Método invocado desde el Controlador al acertar la contraseña
    public void habilitarModulos(String rol) {
        // 1. Borramos la pestaña de Login
        jTabbedPane1.removeAll();

        // --- 2. CREACIÓN DEL MENÚ SUPERIOR PARA CERRAR SESIÓN ---
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuSistema = new javax.swing.JMenu("Sistema");
        javax.swing.JMenuItem itemCerrarSesion = new javax.swing.JMenuItem("Cerrar Sesión");
        
        // Le asignamos la acción al botón
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        
        menuSistema.add(itemCerrarSesion);
        menuBar.add(menuSistema);
        this.setJMenuBar(menuBar); // Hacemos visible la barra
        // ---------------------------------------------------------

        // 3. Módulos base para TODOS (Empleados y Dueños)
        
        // Módulo Ventas
        PanelVentas vistaVentas = new PanelVentas();
        VentaDAO daoVentas = new VentaDAO();
        new VentaControlador(vistaVentas, daoVentas); 
        /* falso positivo, el controlador se queda agarrado a los botones 
        de la pantalla gracias al addactionlistener(this)  */
        jTabbedPane1.addTab("Punto de Venta", vistaVentas);

        // Módulo Clientes
        PanelClientes vistaClientes = new PanelClientes();
        ClienteDAO daoClientes = new ClienteDAO();
        new ClienteControlador(vistaClientes, daoClientes);
        /* falso positivo, el controlador se queda agarrado a los botones 
        de la pantalla gracias al addactionlistener(this)  */
        jTabbedPane1.addTab("Gestión de Clientes", vistaClientes);

        // Módulo Libros
        PanelLibros vistaLibros = new PanelLibros();
        LibroDAO daoLibros = new LibroDAO();
        new LibroControlador(vistaLibros, daoLibros);
        /* falso positivo, el controlador se queda agarrado a los botones 
        de la pantalla gracias al addactionlistener(this)  */
        jTabbedPane1.addTab("Inventario de Libros", vistaLibros);

        // 4. Control de acceso estricto
        if (rol.equals("DUEÑO")) {
            // Futuros módulos
        }
    }

    // Lógica para interceptar el botón de Cerrar Sesión
    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro que desea cerrar la sesión actual?", 
                "Cerrar Sesión", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE);
        
        // Si dice que sí, destruimos la sesión devolviéndolo a la pantalla de origen
        if (confirmacion == JOptionPane.YES_OPTION) {
            cargarPantallaLogin();
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