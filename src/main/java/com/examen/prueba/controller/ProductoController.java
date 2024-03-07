package com.examen.prueba.controller;

import com.examen.prueba.Conexion.Conexion;
import com.examen.prueba.DTO.productoDTO;
import com.examen.prueba.modelo.ErrorResponse;
import com.examen.prueba.modelo.Producto;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("api/v1/productos/")
public class ProductoController {
    static Logger logger = LogManager.getLogger(ProductoController.class);
    @PostMapping("nuevo")
    public ResponseEntity insertarProducto(@RequestBody productoDTO producto)
    {
        Connection cn = null;
        CallableStatement pstmt = null;
        ResultSet rs = null;

        Calendar calendar = Calendar.getInstance();
        Date fechaActual = new Date(calendar.getTime().getTime());

        String codigoRespuesta = null;
        String mensaje ="";
        List<Producto> listaProductos= new ArrayList<Producto>();
        Producto nuevoProducto = null;
        try {

            cn = Conexion.getConexion();
            pstmt = cn.prepareCall("{call SP_PRODUCTO_INSERT(?,?,?,?,?)}");
            pstmt.setString(1,producto.getNombre());
            pstmt.setDate(2,fechaActual);
            pstmt.registerOutParameter(3, OracleTypes.CURSOR);
            pstmt.registerOutParameter(4, OracleTypes.VARCHAR);
            pstmt.registerOutParameter(5, OracleTypes.VARCHAR);
            pstmt.execute();
            rs = (ResultSet) pstmt.getObject(3);
            codigoRespuesta = pstmt.getString(4);
            mensaje = pstmt.getString(5);



            while (rs.next()) {
                nuevoProducto = new Producto();
                nuevoProducto.setId(rs.getLong("id"));
                nuevoProducto.setNombre(rs.getString("nombre"));
                nuevoProducto.setFechaRegistro(rs.getDate("fecha_registro"));

                listaProductos.add(nuevoProducto);
            }

            rs.close();
            cn.close();
            cn = null;
            logger.info("listaProductos.size(): "+ listaProductos.size());
            if (codigoRespuesta.equals("000"))
            {
                logger.info("Result OK: "+mensaje);
                return ResponseEntity.ok(listaProductos);
            }
            else {
                logger.info("Result BAD: "+mensaje);
                ErrorResponse error = new ErrorResponse();
                error.setCodigoRespuesta(codigoRespuesta.toString());
                error.setMensaje(mensaje);

                return ResponseEntity.badRequest().body(error);
            }

        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.setCodigoRespuesta("002");
            error.setMensaje(e.getMessage());

            return ResponseEntity.internalServerError().body(error);
        }finally {
            try {
                if (rs != null)
                    rs.close();
                if (cn != null)
                    cn.close();
            } catch (SQLException e) {
                ErrorResponse error = new ErrorResponse();
                error.setCodigoRespuesta("003");
                error.setMensaje(e.getMessage());

                return ResponseEntity.internalServerError().body(error);
            }
        }
    }
}
