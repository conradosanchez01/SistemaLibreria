package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
    
    public List<String> listarCategoriasCombo() throws SQLException {
        List<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione Categoría ---");
        String sql = "SELECT id_categoria, nombre FROM categorias ORDER BY nombre";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(rs.getInt("id_categoria") + "::" + rs.getString("nombre"));
            }
        }
        return lista;
    }
    
    // Método para insertar la categoría desde el botón [+]
    public boolean insertarCategoria(String nombre) throws SQLException {
        String sql = "INSERT INTO categorias (nombre) VALUES (?)";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.executeUpdate();
            return true;
        }
    }
}