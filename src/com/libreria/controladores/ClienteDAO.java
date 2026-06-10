package com.libreria.controladores;

import com.libreria.conexion.ConexionDB;
import com.libreria.excepciones.ClienteDuplicadoException;
import com.libreria.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void insertar(Cliente cliente)
            throws ClienteDuplicadoException {

        Connection con = ConexionDB.conectar();

        try {

            String verificar =
                    "SELECT * FROM clientes WHERE dni=? OR email=?";

            PreparedStatement psVerificar =
                    con.prepareStatement(verificar);

            psVerificar.setString(1, cliente.getDni());
            psVerificar.setString(2, cliente.getEmail());

            ResultSet rs = psVerificar.executeQuery();

            if(rs.next()){
                throw new ClienteDuplicadoException(
                        "Ya existe un cliente con ese DNI o Email"
                );
            }

            String sql =
                    "INSERT INTO clientes(nombre,apellido,dni,email) VALUES(?,?,?,?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getEmail());

            ps.executeUpdate();

            ps.close();
            psVerificar.close();
            con.close();

        } catch(SQLException e) {

            System.err.println(
                    "Error al insertar cliente: "
                            + e.getMessage()
            );
        }
    }

    public List<Cliente> consultarTodos() {

        List<Cliente> lista = new ArrayList<>();

        Connection con = ConexionDB.conectar();

        try {

            String sql = "SELECT * FROM clientes";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                Cliente c = new Cliente();

                c.setIdCliente(
                        rs.getInt("id_cliente")
                );

                c.setNombre(
                        rs.getString("nombre")
                );

                c.setApellido(
                        rs.getString("apellido")
                );

                c.setDni(
                        rs.getString("dni")
                );

                c.setEmail(
                        rs.getString("email")
                );

                lista.add(c);
            }

            rs.close();
            ps.close();
            con.close();

        } catch(SQLException e) {

            System.err.println(
                    "Error al consultar clientes: "
                            + e.getMessage()
            );
        }

        return lista;
    }

    public void modificar(Cliente cliente) {

        Connection con = ConexionDB.conectar();

        try {

            String sql =
                    "UPDATE clientes SET nombre=?, apellido=?, dni=?, email=? WHERE id_cliente=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getEmail());
            ps.setInt(5, cliente.getIdCliente());

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch(SQLException e) {

            System.err.println(
                    "Error al modificar cliente: "
                            + e.getMessage()
            );
        }
    }

    public void eliminar(int idCliente) {

        Connection con = ConexionDB.conectar();

        try {

            String sql =
                    "DELETE FROM clientes WHERE id_cliente=?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setInt(1, idCliente);

            ps.executeUpdate();

            ps.close();
            con.close();

        } catch(SQLException e) {

            System.err.println(
                    "Error al eliminar cliente: "
                            + e.getMessage()
            );
        }
    }
}