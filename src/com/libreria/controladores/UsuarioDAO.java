package com.libreria.controladores;

import com.libreria.conexion.ConexionDB;
import com.libreria.modelos.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Metodo que recibe lo que el usuario escribio y verifica si existe en la BD
    public Usuario validarLogin(String username, String password) {
        Usuario usu = null;
        //placeholders ? ? (marcadores de posición) texto plano
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        
        // Usamos try-with-resources (requisito avanzado) para que se cierre sola la conexión y evitar fugas de memoria
        try (Connection conn = ConexionDB.conectar(); // Se invoca la funcion conectar de la clase ConexionDB
             PreparedStatement ps = conn.prepareStatement(sql)) { //MySQL compila la consulta, crea un plan de ejecución óptimo en su memoria y deja dos "casilleros vacíos" para los parámetros
            
            // Reemplazamos los signos de interrogación por los datos reales (Evita inyección SQL) los numeros indican el orden de las referencias 
            ps.setString(1, username); // ?posicion 1, Juan
            ps.setString(2, password); // ?posicion 2, 123
            
            try (ResultSet rs = ps.executeQuery()) {  //try-with-resources cuando tiene ()
            //ps.execute Envía la consulta SQL (ya con los datos inyectados) a la base de datos
            // ResultSet rs es una tabla virtual que guarda las filas devueltas por la base de datos.                
           
                if (rs.next()) //Cuando el ResultSet se crea, el cursor apunta "antes de la primera fila". Al ejecutar .next(), el cursor salta a la primera fila de resultados.
                // Si rs.next() es true, significa que encontró un registro que coincide
                {
                    usu = new Usuario();
                    usu.setIdUsuario(rs.getInt("id_usuario"));
                    usu.setUsername(rs.getString("username"));
                    usu.setPassword(rs.getString("password"));
                    usu.setRol(rs.getString("rol"));
                } //obtiene los datos de la tabla generada y los asigna al nuevo objeto
            }
        } catch (SQLException e) { //Si no encuentra nada (credenciales incorrectas)
            System.err.println("Error al validar usuario: " + e.getMessage());
        }
        
        // Retorna el usuario con sus datos, o null si la contraseña era incorrecta
        return usu;
    }
}