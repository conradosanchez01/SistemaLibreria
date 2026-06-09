package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import java.sql.*;
import java.util.ArrayList;

public class ClienteDAO {
    public ArrayList<String> listarClientesCombo() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione un Cliente ---"); // Opción por defecto
        
        String sql = "SELECT id_cliente, nombre, apellido FROM clientes";
        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                // Formato: ID::Nombre Apellido
                lista.add(rs.getInt("id_cliente") + "::" + rs.getString("nombre") + " " + rs.getString("apellido"));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
        }
        return lista;
    }
}