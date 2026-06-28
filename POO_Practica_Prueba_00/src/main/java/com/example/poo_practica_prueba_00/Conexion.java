package com.example.poo_practica_prueba_00;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String url = "jdbc:postgresql://localhost:5432/practica_00";
    private static final String user = "postgres";
    private static final String password = "Iblame2007*";

    public static Connection conectar(){
        try {
            return DriverManager.getConnection(url, user, password);
        }catch (SQLException e){
            System.out.println("Error al conectar: " + e.getMessage());
            return null;
        }
    }
}
