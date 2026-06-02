package com.libreria.main;

import com.libreria.vistas.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {
        
        // Esto hace que la ventana se abra con el estilo visual del sistema operativo
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar el diseño visual.");
        }

        // Crear y mostrar la ventana principal
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal();
                // Hace que la ventana aparezca en el centro de la pantalla
                ventana.setLocationRelativeTo(null); 
                ventana.setVisible(true);
            }
        });
    }
}