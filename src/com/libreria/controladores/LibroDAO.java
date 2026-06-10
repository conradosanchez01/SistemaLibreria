package com.libreria.controladores;

import com.libreria.conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;

public class LibroDAO {
    public ArrayList<String> listarLibrosCombo() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione un Libro ---::0.0::0"); 
        
        // Solo traemos los libros que tengan más de 0 en stock
        String sql = "SELECT id_libro, titulo, precio, stock FROM libros WHERE stock > 0";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                // Formato: ID::Titulo::Precio::Stock
                lista.add(rs.getInt("id_libro") + "::" + rs.getString("titulo") + "::" + 
                          rs.getDouble("precio") + "::" + rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar libros: " + e.getMessage());
        }
        return lista;
    }
}