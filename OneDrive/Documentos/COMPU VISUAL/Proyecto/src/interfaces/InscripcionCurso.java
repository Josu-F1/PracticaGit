/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import proyecto.conexion;
import java.sql.Connection;
import java.util.Vector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

/**
 *
 * @author bl250
 */
public class InscripcionCurso extends javax.swing.JInternalFrame {

    // LÍNEA CORRECTA
    /**
     * Creates new form IncripcionCurso
     */
    public InscripcionCurso() {
        initComponents();
        cargarEstudiantesComboBox();
    }

    private class EstudianteComboBox {
        String cedula;
        String nombreCompleto;

        public EstudianteComboBox(String cedula, String nombre, String apellido) {
            this.cedula = cedula;
            // Formato de visualización: CEDULA - Nombre Apellido
            this.nombreCompleto = cedula + " - " + nombre + " " + apellido; 
        }

        @Override
        public String toString() {
            return nombreCompleto;
        }
    }
    
    // ===============================================
    //           LÓGICA DE CARGA DE COMBOBOX
    // ===============================================
    private void cargarEstudiantesComboBox() {
        // El tipo del Vector debe coincidir con el tipo del JComboBox corregido (EstudianteComboBox)
        Vector<EstudianteComboBox> items = new Vector<>();
        conexion db = new conexion();
        Connection conn = null;
        
        // CORRECCIÓN CRÍTICA: La consulta SELECT ahora usa los nombres correctos de la DB.
        String sql = "SELECT estcedula, estnombre, estapellido FROM estudiantes ORDER BY estnombre"; 
        
        try {
            conn = db.conectar();
            if (conn == null) return;
            
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    // La lectura ya era correcta, ahora coincide con el SELECT
                    String cedula = rs.getString("estcedula"); 
                    String nombre = rs.getString("estnombre");
                    String apellido = rs.getString("estapellido");
                    
                    items.add(new EstudianteComboBox(cedula, nombre, apellido));
                }
                
                // Asignar el vector al modelo del ComboBox
                jComboBoxEstudiantes.setModel(new DefaultComboBoxModel<>(items));
                
            }
        } catch (Exception e) { 
             JOptionPane.showMessageDialog(this, "Error de SQL al cargar estudiantes: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
    
    // ===============================================
    //           LÓGICA DE INSCRIPCIÓN
    // ===============================================
    private void realizarInscripcion() {
        String nombreCurso = jtxtNombreCurso.getText().trim();
        
        if (jComboBoxEstudiantes.getItemCount() == 0 || jComboBoxEstudiantes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un estudiante de la lista.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Se obtiene el objeto EstudianteComboBox seleccionado
        EstudianteComboBox estudianteSeleccionado = (EstudianteComboBox) jComboBoxEstudiantes.getSelectedItem();
        
        if (nombreCurso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del curso.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String cedulaEstudiante = estudianteSeleccionado.cedula;
        
        conexion db = new conexion();
        Connection conn = null;
        
        // La sentencia SQL es correcta para dos columnas (nombre_curso, estcedula)
        String sql = "INSERT INTO curso (nombre_curso, estcedula) VALUES (?, ?)";
        
        try {
            conn = db.conectar();
            if (conn == null) return;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombreCurso);
                ps.setString(2, cedulaEstudiante);
                
                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Inscripción al curso exitosa!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    jtxtNombreCurso.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo completar la inscripción.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Error de SQL al guardar la inscripción: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (Exception ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBoxEstudiantes = new javax.swing.JComboBox<>();
        jbtnInscribir = new javax.swing.JButton();
        jtxtNombreCurso = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("INSCRIPCION AL CURSO");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Nombre del curso:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Cedula - Nombre del alumno: ");

        jComboBoxEstudiantes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jbtnInscribir.setBackground(new java.awt.Color(4, 4, 34));
        jbtnInscribir.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnInscribir.setForeground(new java.awt.Color(255, 255, 255));
        jbtnInscribir.setText("INSCRIBIR");
        jbtnInscribir.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbtnInscribir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnInscribirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jbtnInscribir)))
                        .addGap(59, 59, 59))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtxtNombreCurso)
                            .addComponent(jComboBoxEstudiantes, 0, 226, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(245, 245, 245)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 249, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jComboBoxEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(54, 54, 54)
                .addComponent(jbtnInscribir)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnInscribirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnInscribirActionPerformed
        realizarInscripcion();// TODO add your handling code here:
    }//GEN-LAST:event_jbtnInscribirActionPerformed

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
            java.util.logging.Logger.getLogger(InscripcionCurso.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InscripcionCurso.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InscripcionCurso.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InscripcionCurso.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InscripcionCurso().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<EstudianteComboBox> jComboBoxEstudiantes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbtnInscribir;
    private javax.swing.JTextField jtxtNombreCurso;
    // End of variables declaration//GEN-END:variables
}
