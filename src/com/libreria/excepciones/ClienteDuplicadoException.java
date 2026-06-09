package com.libreria.excepciones;

public class ClienteDuplicadoException extends Exception {

    public ClienteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}