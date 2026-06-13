package com.libreria.controladores;

import com.libreria.dao.UsuarioDAO;
import com.libreria.modelos.Usuario;
import com.libreria.vistas.PanelLogin;
import com.libreria.vistas.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UsuarioControlador {

    private final PanelLogin vista;
    private final UsuarioDAO dao;

    // El constructor une la vista tonta con el DAO de la base de datos
    public UsuarioControlador(PanelLogin vista, UsuarioDAO dao) {
        this.vista = vista;
        this.dao = dao;

        // Le asignamos la escucha de eventos a los componentes de la vista
        this.vista.getBtnIngresar().addActionListener(e -> ejecutarLogin());
        this.vista.getTxtPassword().addActionListener(e -> ejecutarLogin());
    }

    private void ejecutarLogin() {
        // 1. Capturamos lo que escribió el usuario
        String usuario = vista.getTxtUsuario().getText();

        // Para el JPasswordField no se usa getText() por seguridad, se usa getPassword() que devuelve un arreglo de caracteres, así que lo convertimos a String:
        String password = new String(vista.getTxtPassword().getPassword());

        // 2. Llamamos a tu Controlador (DAO)
        Usuario userLogueado = dao.validarLogin(usuario, password);

        // 3. Verificamos el resultado
        if (userLogueado != null) {
            // Si entró acá, las credenciales son correctas
            JOptionPane.showMessageDialog(vista, "¡Bienvenido " + userLogueado.getUsername() + "!");

            // Obtenemos la ventana principal que contiene a este panel
            VentanaPrincipal ventana = (VentanaPrincipal) SwingUtilities.getWindowAncestor(vista);
            
            // Ejecutamos el método pasándole el rol del usuario que acaba de entrar
            ventana.habilitarModulos(userLogueado.getRol());
            
        } else {
            // Si dio null, se equivocó de usuario o clave
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos", "Error de Acceso", JOptionPane.ERROR_MESSAGE);

            // Limpiamos la caja de contraseña para que vuelva a intentar
            vista.getTxtPassword().setText("");
        }
    }
}