package com.libreria.conexion;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException; 

public class ConexionDB { 
    
    // Configuración de la URL de conexión, usuario y contraseña 
    private static final String URL = "jdbc:mysql://localhost:3306/libreria_db"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; // Sustituye por tu clave si usas una en tu MySQL local 

    public static Connection conectar() { 
        Connection conexion = null; 
        try { 
            // Estableciendo la conexión utilizando DriverManager 
            conexion = DriverManager.getConnection(URL, USER, PASSWORD); 
            System.out.println("Conexion a la base de datos establecida con exito!"); 
        } catch (SQLException e) { 
            System.err.println("Error al conectar con la base de datos: " + e.getMessage()); 
        }
        return conexion; // 
    }
}