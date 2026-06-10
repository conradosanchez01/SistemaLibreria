package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import com.libreria.modelos.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // 1. Método para INSERTAR un libro nuevo
    public boolean insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, precio, stock, id_categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setDouble(3, libro.getPrecio());
            ps.setInt(4, libro.getStock());
            ps.setInt(5, libro.getIdCategoria());
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar libro: " + e.getMessage());
            return false;
        }
    }

    // 2. Método para LEER 
    public List<Libro> obtenerTodos() {
        List<Libro> listaLibros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setPrecio(rs.getDouble("precio"));
                libro.setStock(rs.getInt("stock"));
                libro.setIdCategoria(rs.getInt("id_categoria"));
                
                listaLibros.add(libro);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar libros: " + e.getMessage());
        }
        return listaLibros;
    }

    // 3. Método para MODIFICAR un libro existente
    public boolean modificar(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, precio=?, stock=?, id_categoria=? WHERE id_libro=?";
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setDouble(3, libro.getPrecio());
            ps.setInt(4, libro.getStock());
            ps.setInt(5, libro.getIdCategoria());
            ps.setInt(6, libro.getIdLibro());
            
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al modificar libro: " + e.getMessage());
            return false;
        }
    }

    // 4. Método para ELIMINAR un libro
    public boolean eliminar(int idLibro) {
        String sql = "DELETE FROM libros WHERE id_libro=?";
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idLibro);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }
}