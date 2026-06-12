package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import com.libreria.modelos.Libro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public void insertar(Libro libro) throws SQLException {
        String sqlVerificar = "SELECT COUNT(*) FROM libros WHERE isbn = ?";
        String sqlInsert = "INSERT INTO libros (isbn, titulo, autor, precio, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar)) {
            
            psVerificar.setString(1, libro.getIsbn());
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Ya existe un libro registrado con el ISBN: " + libro.getIsbn());
                }
            }

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setString(1, libro.getIsbn());
                psInsert.setString(2, libro.getTitulo());
                psInsert.setString(3, libro.getAutor());
                psInsert.setDouble(4, libro.getPrecio());
                psInsert.setInt(5, libro.getStock());
                psInsert.setInt(6, libro.getIdCategoria());
                psInsert.executeUpdate();
            }
        }
    }

    public List<Libro> obtenerTodos() throws SQLException {
        List<Libro> listaLibros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setPrecio(rs.getDouble("precio"));
                libro.setStock(rs.getInt("stock"));
                libro.setIdCategoria(rs.getInt("id_categoria"));
                listaLibros.add(libro);
            }
        }
        return listaLibros;
    }

    public void modificar(Libro libro) throws SQLException {
        String sqlVerificar = "SELECT COUNT(*) FROM libros WHERE isbn = ? AND id_libro != ?";
        String sqlUpdate = "UPDATE libros SET isbn=?, titulo=?, autor=?, precio=?, stock=?, id_categoria=? WHERE id_libro=?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar)) {
            
            psVerificar.setString(1, libro.getIsbn());
            psVerificar.setInt(2, libro.getIdLibro());
            
            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("El ISBN '" + libro.getIsbn() + "' ya está asignado a otro libro.");
                }
            }

            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, libro.getIsbn());
                psUpdate.setString(2, libro.getTitulo());
                psUpdate.setString(3, libro.getAutor());
                psUpdate.setDouble(4, libro.getPrecio());
                psUpdate.setInt(5, libro.getStock());
                psUpdate.setInt(6, libro.getIdCategoria());
                psUpdate.setInt(7, libro.getIdLibro());
                psUpdate.executeUpdate();
            }
        }
    }

    public void eliminar(int idLibro) throws SQLException {
        String sql = "DELETE FROM libros WHERE id_libro=?";
        try (Connection con = ConexionDB.conectar(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idLibro);
            ps.executeUpdate();
        }
    }

    
    // (Mantiene la compatibilidad) pendiente de eliminar
    
    public List<String> listarLibrosCombo() throws SQLException {
        List<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione un Libro ---");
        String sql = "SELECT id_libro, titulo, precio, stock FROM libros WHERE stock >= 0 ORDER BY titulo";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_libro");
                String tit = rs.getString("titulo");
                double pre = rs.getDouble("precio");
                int stk = rs.getInt("stock");
                lista.add(id + "::" + tit + "::" + pre + "::" + stk);
            }
        }
        return lista;
    }
    
    // metodo para buscar libros (eliminar combobox)
    public List<Libro> buscarLibros(String criterio) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        // Buscamos coincidencia parcial en isbn, titulo o autor
        String sql = "SELECT * FROM libros WHERE isbn LIKE ? OR titulo LIKE ? OR autor LIKE ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            ps.setString(3, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Libro libro = new Libro();
                    libro.setIdLibro(rs.getInt("id_libro"));
                    libro.setIsbn(rs.getString("isbn"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setPrecio(rs.getDouble("precio"));
                    libro.setStock(rs.getInt("stock"));
                    libro.setIdCategoria(rs.getInt("id_categoria"));
                    lista.add(libro);
                }
            }
        }
        return lista;
    }
    
}