package com.examen.prueba.Conexion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

@Component
public class Conexion {

    private static String url;

    private static String user;
    private static String password;

    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        Conexion.url = url;
    }

    @Value("${spring.datasource.username}")
    public void setUser(String user) {
        Conexion.user = user;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        Conexion.password = password;
    }

    public static synchronized Connection getConexion() {
        Connection cn = null;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            cn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            cn = null;
            System.out.println("Fallo Conexion...");
            System.out.println(ex.getStackTrace());
            System.out.println(ex.getMessage());
        } finally {
            return cn;
        }
    }
    public static synchronized void cerrarCall(CallableStatement cl) {
        try{cl.close();}catch(Exception e){}
    }
    public static synchronized void cerrarConexion(ResultSet rs) {
        try{rs.close();} catch (Exception e) {}
    }
    public static synchronized void cerrarConexion(Connection cn) {
        try{cn.close();} catch (Exception e) {}
    }
    public static synchronized void deshacerCambios(Connection cn) {
        try{cn.rollback();}catch (Exception e){}
    }
}
