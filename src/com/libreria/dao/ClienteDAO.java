package com.libreria.dao;

import com.libreria.conexion.ConexionDB;
import com.libreria.excepciones.ClienteDuplicadoException;
import com.libreria.modelos.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void insertar(Cliente cliente) throws ClienteDuplicadoException, SQLException {
        String sqlVerificar = "SELECT COUNT(*) FROM clientes WHERE dni = ? OR email = ?";
        String sqlInsert = "INSERT INTO clientes(nombre, apellido, dni, email) VALUES(?, ?, ?, ?)";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar)) {

            psVerificar.setString(1, cliente.getDni());
            psVerificar.setString(2, cliente.getEmail());

            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new ClienteDuplicadoException("Ya existe un cliente con ese DNI o Email");
                }
            }

            try (PreparedStatement psInsert = con.prepareStatement(sqlInsert)) {
                psInsert.setString(1, cliente.getNombre());
                psInsert.setString(2, cliente.getApellido());
                psInsert.setString(3, cliente.getDni());
                psInsert.setString(4, cliente.getEmail());
                psInsert.executeUpdate();
            }
        }
    }

   public List<Cliente> consultarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cliente c = new Cliente();
                c.setIdCliente(rs.getInt("id_cliente"));
                c.setNombre(rs.getString("nombre"));
                c.setApellido(rs.getString("apellido"));
                c.setDni(rs.getString("dni"));
                c.setEmail(rs.getString("email"));
                lista.add(c);
            }
        }
        return lista;
    }

    public void modificar(Cliente cliente) throws ClienteDuplicadoException, SQLException {
        String sqlVerificar = "SELECT COUNT(*) FROM clientes WHERE (dni = ? OR email = ?) AND id_cliente != ?";
        String sqlUpdate = "UPDATE clientes SET nombre = ?, apellido = ?, dni = ?, email = ? WHERE id_cliente = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement psVerificar = con.prepareStatement(sqlVerificar)) {

            psVerificar.setString(1, cliente.getDni());
            psVerificar.setString(2, cliente.getEmail());
            psVerificar.setInt(3, cliente.getIdCliente());

            try (ResultSet rs = psVerificar.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new ClienteDuplicadoException("Ya existe otro cliente con ese DNI o Email");
                }
            }

            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {
                psUpdate.setString(1, cliente.getNombre());
                psUpdate.setString(2, cliente.getApellido());
                psUpdate.setString(3, cliente.getDni());
                psUpdate.setString(4, cliente.getEmail());
                psUpdate.setInt(5, cliente.getIdCliente());
                psUpdate.executeUpdate();
            }
        }
    }

    public void eliminar(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCliente);
            ps.executeUpdate();
        }
    }

    // =========================================================================
    // MÉTODO OPTIMIZADO Y ORDENADO PARA EL MÓDULO DE VENTAS
    // =========================================================================
    public List<String> listarClientesCombo() throws SQLException {
        List<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione ---");
        
        // Se agrega ORDER BY para mejorar la experiencia de usuario al vender
        String sql = "SELECT id_cliente, nombre, apellido FROM clientes ORDER BY apellido, nombre";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_cliente");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                lista.add(id + "::" + nombre + " " + apellido);
            }
        }
        return lista;
    }
    
    
    public List<String> buscarClientesCombo(String criterio) throws SQLException {
        List<String> lista = new ArrayList<>();
        lista.add("0::--- Seleccione Cliente ---");
        
        String sql = "SELECT id_cliente, nombre, apellido FROM clientes WHERE dni LIKE ? OR apellido LIKE ? ORDER BY apellido, nombre";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, "%" + criterio + "%");
            ps.setString(2, "%" + criterio + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id_cliente") + "::" + rs.getString("nombre") + " " + rs.getString("apellido"));
                }
            }
        }
        return lista;
    }
    
    
    
    
    // Método para el buscador en tiempo real
    public List<Cliente> buscarClientes(String criterio) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE dni LIKE ? OR nombre LIKE ? OR apellido LIKE ?";

        try (Connection con = ConexionDB.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String parametro = "%" + criterio + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            ps.setString(3, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente c = new Cliente();
                    c.setIdCliente(rs.getInt("id_cliente"));
                    c.setNombre(rs.getString("nombre"));
                    c.setApellido(rs.getString("apellido"));
                    c.setDni(rs.getString("dni"));
                    c.setEmail(rs.getString("email"));
                    lista.add(c);
                }
            }
        }
        return lista;
    }
    
}