

package com.libreria.main;

import com.libreria.dao.UsuarioDAO;
import com.libreria.vistas.VentanaPrincipal;
import com.libreria.modelos.Usuario;



public class Main {
    public static void main(String[] args) {
        
        // 1. Inicializamos FlatLaf (El diseño premium) ANTES de abrir nada

        try {
            //com.formdev.flatlaf.FlatDarkLaf.setup();
            // Si en algún momento preferís el tema claro, comentá la línea de arriba y usá esta:
             com.formdev.flatlaf.FlatLightLaf.setup();
             
        } catch (Exception ex) {
            System.err.println("Error al cargar el diseño visual FlatLaf.");
        }

        // 2. Crear y mostrar la ventana principal
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               
                new VentanaPrincipal().setVisible(true);
            }
        });
    }
}