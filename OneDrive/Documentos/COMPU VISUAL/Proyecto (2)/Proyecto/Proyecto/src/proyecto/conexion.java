/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

/**
 *
 * @author bl250
 */
public class conexion {
    Connection conectar = null;

    public Connection conectar(){
        
        try {
            //servidor, usuario.contraseñaUsuario
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection("jdbc:mysql://localhost/utacuarto", "root", "");
            //JOptionPane.showMessageDialog(null, "Te conectaste");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
        return conectar;
    }

    public PreparedStatement prepareStatement(String SqlInsert) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
