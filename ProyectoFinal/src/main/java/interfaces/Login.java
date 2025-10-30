/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import proyecto.conexion;

/**
 *
 * @author bl250
 */
public class Login extends javax.swing.JFrame {

    /**
     * Creates new form Login
     */
    public Login() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    private static final String SALT_FIJO = "un_salt_secreto_y_largo_para_cuartouta";

    // ===============================================
    //           MÉTODOS DE HASHING SHA-256
    // ===============================================
    /**
     * Genera un hash SHA-256 de la contraseña combinada con el salt.
     */
    private static String hashClaveSHA256(String password) {
        String passwordConSalt = password + SALT_FIJO;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(passwordConSalt.getBytes());
            // Convierte el hash de bytes a una cadena Base64 para almacenamiento
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al usar el algoritmo de hashing SHA-256", e);
        }
    }

    /**
     * Verifica la contraseña plana comparando su hash con el hash almacenado.
     */
    private static boolean verificarClave(String plainPassword, String hashedPassword) {
        String nuevoHash = hashClaveSHA256(plainPassword);
        return nuevoHash.equals(hashedPassword);
    }

    // ===============================================
    //           LÓGICA DE AUTENTICACIÓN
    // ===============================================
    private void autenticarUsuario() {
        String usuario = jtxtUsuario.getText();
        String password = new String(jtxtContraseña.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese usuario y contraseña.", "Error de Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        conexion db = new conexion();
        Connection conn = null;

        // La consulta SQL es la misma: buscamos el hash almacenado y el rol.
        String sql = "SELECT clave_hash, rol FROM usuarios WHERE usuario = ?";

        try {
            conn = (Connection) db.conectar();
            if (conn == null) {
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, usuario);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        String claveHashAlmacenado = rs.getString("clave_hash");
                        String rol = rs.getString("rol");

                        // **CLAVE:** Llamada al método SHA-256 interno para verificación
                        if (verificarClave(password, claveHashAlmacenado)) {
                            // Autenticación exitosa
                            /*JOptionPane.showMessageDialog(this,
                                    "¡Bienvenido, " + usuario + "! Rol: " + rol,
                                    "Login Exitoso",
                                    JOptionPane.INFORMATION_MESSAGE
                            );*/
                            // 1. Creamos la ventana Principal usando el nuevo constructor
                            Principal ventanaPrincipal = new Principal(usuario, rol);

                            // 2. La hacemos visible
                            ventanaPrincipal.setVisible(true);

                            // 3. Cerramos la ventana de Login
                            this.dispose();

                        } else {
                            // Contraseña incorrecta
                            JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        // Usuario no encontrado
                        JOptionPane.showMessageDialog(this, "Usuario no encontrado.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la consulta a la base de datos: " + e.getMessage(), "Error de SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.err.println("Error al cerrar la conexión: " + ex.getMessage());
            }
        }
    }
    
   public static void configurarAlertas() {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }

    // 🎨 Paleta de colores
    Color blanco = Color.WHITE;
    Color azulOscuro = new Color(4, 34, 255);   // azul marino oscuro

    // 🧱 Fondo general
    UIManager.put("OptionPane.background", blanco);
    UIManager.put("Panel.background", blanco);
    UIManager.put("RootPane.background", blanco);

    // 🔠 Texto y fuente
  
    UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 15));
    UIManager.put("OptionPane.minimumSize", new Dimension(400, 160));

    // 🔘 Botones azul oscuro con letras blancas
    UIManager.put("Button.background", azulOscuro);
   
    UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
    UIManager.put("Button.focus", azulOscuro);
    UIManager.put("Button.select", azulOscuro);

    // 🔧 Evita el fondo gris de Look and Feel
    UIManager.put("OptionPaneUI", "javax.swing.plaf.basic.BasicOptionPaneUI");
}



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jtxtUsuario = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jbtnEntrar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtxtContraseña = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setToolTipText("Iniciar Sesión");
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jtxtUsuario.setBorder(null);
        jtxtUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtUsuarioActionPerformed(evt);
            }
        });
        jPanel1.add(jtxtUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 160, 223, -1));

        jLabel1.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        jLabel1.setText("Contraseña:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 220, -1, -1));

        jbtnEntrar.setBackground(new java.awt.Color(4, 4, 34));
        jbtnEntrar.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jbtnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnEntrar.setText("ENTRAR");
        jbtnEntrar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEntrarActionPerformed(evt);
            }
        });
        jPanel1.add(jbtnEntrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 320, 105, 40));

        jLabel3.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Usuario: ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 130, 66, -1));

        jLabel2.setFont(new java.awt.Font("Calibri", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 51));
        jLabel2.setText("INICIAR SESION");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, 159, -1));

        jtxtContraseña.setBorder(null);
        jPanel1.add(jtxtContraseña, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 250, 219, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/usuario.png"))); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 160, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/bloquear.png"))); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 250, -1, -1));

        jPanel2.setBackground(new java.awt.Color(0, 0, 51));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/acceso.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(100, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 390));
        jPanel1.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 180, 223, 10));
        jPanel1.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 270, 219, 10));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEntrarActionPerformed
        autenticarUsuario();// TODO add your handling code here:
    }//GEN-LAST:event_jbtnEntrarActionPerformed

    private void jtxtUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtUsuarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jtxtUsuarioActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /*String adminPass = "admin123";
        String secretPass = "secre123";

        System.out.println("Hash Admin ('" + adminPass + "'): " + hashClaveSHA256(adminPass));
        System.out.println("Hash Secretaria ('" + secretPass + "'): " + hashClaveSHA256(secretPass));

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton jbtnEntrar;
    private javax.swing.JPasswordField jtxtContraseña;
    private javax.swing.JTextField jtxtUsuario;
    // End of variables declaration//GEN-END:variables

}
