package proyecto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author bl250
 */
public class conexion {
    Connection conectar = null;

    public Connection conectar(){
        
        try {
            // Cargar el driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Conexión a base de datos local
            String url = "jdbc:mysql://localhost:3306/utacuarto?useSSL=false&serverTimezone=UTC";
            conectar = DriverManager.getConnection(url, "root", "");
            
            //JOptionPane.showMessageDialog(null, "Conectado a la base de datos");
            
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, 
                "Driver MySQL no encontrado.\nAsegúrate de que el driver esté en las dependencias.", 
                "Error de Driver", 
                JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Error de conexión a la base de datos:\n" + ex.getMessage() + 
                "\n\nAsegúrate de que:\n- MySQL/XAMPP esté ejecutándose\n- La base de datos 'utacuarto' exista\n- El usuario 'root' no tenga contraseña", 
                "Error de Conexión", 
                JOptionPane.ERROR_MESSAGE);
        }
        return conectar;
    }

    public PreparedStatement prepareStatement(String SqlInsert) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
