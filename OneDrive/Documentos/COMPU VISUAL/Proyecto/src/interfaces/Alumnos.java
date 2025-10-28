/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import proyecto.conexion;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bl250
 */
public class Alumnos extends javax.swing.JInternalFrame {

    /**
     * Creates new form Alumnos
     */
    public Alumnos() {
        initComponents();
        configurarGeneroComboBox();
        mostrarDatos();
        cargarCampos();
        botonesInicio();
        textosInicio();
        ValidarEntrada();

    }

    private void configurarGeneroComboBox() {
        // Asegura que el JComboBox tenga el valor inicial 'Seleccione' y que esté deshabilitado.
        // El diseñador lo inicializó con "Hombre", "Mujer", lo modificamos aquí para incluir "Seleccione".
        jcboxGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione", "Hombre", "Mujer" }));
    }
    
    private void ValidarEntrada() {
        // --- Validación Cédula (Solo 10 dígitos) ---
        jtxtCedula.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = jtxtCedula.getText();
                int currentLength = currentText.length();
                // 1. Verificar longitud máxima (excepto para teclas de edición)
                if (currentLength >= 10 && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) {
                    e.consume(); // Ignorar la tecla
                    // Mostrar mensaje de advertencia sobre la longitud
                    JOptionPane.showMessageDialog(jtxtCedula,
                            "El campo Cédula no puede exceder los 10 dígitos.",
                            "Límite alcanzado",
                            JOptionPane.WARNING_MESSAGE);
                    return; // Salir del método para no hacer la validación de dígito
                }

                // 2. Verificar que sea un dígito (permitiendo teclas de edición)
                if (!Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) {
                    e.consume(); // Ignorar caracteres que no sean dígitos
                    // Mostrar mensaje de advertencia sobre el tipo de caracter
                    JOptionPane.showMessageDialog(jtxtCedula,
                            "Solo se permiten números en el campo Cédula.",
                            "Entrada no válida",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // --- Validación Teléfono (Solo 10 dígitos) ---
        jtxtTelefono.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = jtxtTelefono.getText();
                int currentLength = currentText.length();

                // 1. Verificar longitud máxima (excepto para teclas de edición)
                if (currentLength >= 10 && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) {
                    e.consume(); // Ignorar la tecla
                    // Mostrar mensaje de advertencia sobre la longitud
                    JOptionPane.showMessageDialog(jtxtTelefono,
                            "El campo Teléfono no puede exceder los 10 dígitos.",
                            "Límite alcanzado",
                            JOptionPane.WARNING_MESSAGE);
                    return; // Salir del método
                }

                // 2. Verificar que sea un dígito (permitiendo teclas de edición)
                if (!Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) {
                    e.consume(); // Ignorar caracteres que no sean dígitos
                    // Mostrar mensaje de advertencia sobre el tipo de caracter
                    JOptionPane.showMessageDialog(jtxtTelefono,
                            "Solo se permiten números en el campo Teléfono.",
                            "Entrada no válida",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // --- Validación Nombre (Impedir números) ---
        jtxtNombre.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Permitir teclas de edición como Backspace y Delete
                if (Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) { // Si es un dígito (y no es tecla de edición)
                    e.consume(); // Ignorar el carácter
                    // Mostrar mensaje de advertencia
                    JOptionPane.showMessageDialog(jtxtNombre,
                            "No se permiten números en el campo Nombre.",
                            "Entrada no válida",
                            JOptionPane.WARNING_MESSAGE);
                }

            }
        });

        // --- Validación Apellido (Impedir números) ---
        jtxtApellido.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Permitir teclas de edición como Backspace y Delete
                if (Character.isDigit(c) && (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE)) { // Si es un dígito (y no es tecla de edición)
                    e.consume(); // Ignorar el carácter
                    // Mostrar mensaje de advertencia
                    JOptionPane.showMessageDialog(jtxtApellido,
                            "No se permiten números en el campo Apellido.",
                            "Entrada no válida",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        jtxtBuscarCedula.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Llamamos al método de búsqueda cada vez que se suelta una tecla
                buscarDatos(jtxtBuscarCedula.getText().trim());
            }

            // Opcionalmente, puedes restringir la entrada solo a números si lo deseas
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });

    }

    public void guardarDatos() {
        if (jtxtCedula.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar la cedula");
            jtxtCedula.requestFocus();
        } else if (jtxtNombre.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar el nombre");
            jtxtNombre.requestFocus();
        } else if (jtxtApellido.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar el apellido");
            jtxtApellido.requestFocus();
        } else if (jtxtDireccion.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar la direccion");
            jtxtDireccion.requestFocus();
        } else if (jcboxGenero.getSelectedIndex() == 0) { // Índice 0 es "Seleccione"
            JOptionPane.showMessageDialog(null, "Debe seleccionar el género.");
            jcboxGenero.requestFocus();
        } else {
            try {

                if (jtxtCedula.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar la Cédula.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                    jtxtCedula.requestFocus(); // Poner foco en el campo faltante
                    return; // Salir del método
                }
                if (jtxtCedula.getText().length() != 10) {
                    JOptionPane.showMessageDialog(this, "La Cédula debe tener exactamente 10 dígitos.", "Validación Cédula", JOptionPane.WARNING_MESSAGE);
                    jtxtCedula.requestFocus();
                    return;
                }

                conexion cn = new conexion();
                Connection cc = (Connection) cn.conectar();
                String Sql = "insert into estudiantes value(?,?,?,?,?,?)";
                PreparedStatement psd = cc.prepareStatement(Sql);
                psd.setString(1, jtxtCedula.getText().trim());
                psd.setString(2, jtxtNombre.getText().trim());
                psd.setString(3, jtxtApellido.getText().trim());
                psd.setString(4, jtxtDireccion.getText().trim());
                if (!jtxtTelefono.getText().isEmpty()) {
                    if (jtxtTelefono.getText().length() != 10) {
                        JOptionPane.showMessageDialog(this, "El Teléfono debe tener exactamente 10 dígitos.", "Validación Teléfono", JOptionPane.WARNING_MESSAGE);
                        jtxtTelefono.requestFocus();
                        return;
                    }
                    if (!jtxtTelefono.getText().startsWith("09")) {
                        JOptionPane.showMessageDialog(this, "El número de Teléfono debe comenzar con '09'.", "Validación Teléfono", JOptionPane.WARNING_MESSAGE);
                        jtxtTelefono.requestFocus();
                        return;
                    }
                }
                if (jtxtTelefono.getText().isEmpty()) {
                    psd.setString(5, "0900000000"); // Asigna ceros si está vacío
                } else {
                    psd.setString(5, jtxtTelefono.getText());     // Asigna el valor validado
                }
                String genero = jcboxGenero.getSelectedItem().toString();
                psd.setString(6, genero);
                int opc = psd.executeUpdate();
                if (opc > 0) {
                    JOptionPane.showMessageDialog(null, "Se inserto los datos");
                    mostrarDatos();
                    limpiarCampos();
                    botonesInicio();
                    textosInicio();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex);

            }
        }
    }

    public void mostrarDatos() {
        try {
            DefaultTableModel tabla = new DefaultTableModel();
            String titulos[] = {"Cedula", "Nombre", "Apellido", "Direccion", "Telefono", "Genero"};
            String registros[] = new String[6];
            tabla = new DefaultTableModel(null, titulos);

            conexion cn = new conexion();
            Connection cc = cn.conectar();
            String SqlSelect = "select * from estudiantes";
            Statement psd = cc.createStatement();
            ResultSet rs = psd.executeQuery(SqlSelect);
            while (rs.next()) {
                registros[0] = rs.getString("estcedula");  //estcedula
                registros[1] = rs.getString("estnombre");  //estnombre
                registros[2] = rs.getString("estapellido");  //estapellido
                registros[3] = rs.getString("estdireccion");  //estdireccion
                registros[4] = rs.getString("esttelefono");  //esttelefono
                registros[5] = rs.getString("estgenero");
                tabla.addRow(registros);
                jtblAlumnos.setModel(tabla);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Comunicate con el administrador");
        }
    }

    public void eliminarDatos() {
        try {
            if ((JOptionPane.showConfirmDialog(null, "Estas seguro de eliminar?", "Borrar estudiante", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)) {
                conexion cn = new conexion();
                Connection cc = cn.conectar();
                String SqlDelete = "delete from estudiantes where estcedula='" + jtxtCedula.getText() + "'";
                PreparedStatement psd = cc.prepareStatement(SqlDelete);
                int n = psd.executeUpdate();
                if (n > 0) {
                    JOptionPane.showMessageDialog(null, "Se elimino correctamente");
                    mostrarDatos();
                    limpiarCampos();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Comunicate con el administrador");
        }
    }

    public void editarDatos() {
        // 1. Obtener el valor del teléfono
        String telefono = jtxtTelefono.getText().trim();
        String telefonoParaSQL;

        // --- Validación y Asignación de Teléfono ---
        if (telefono.isEmpty()) {
            // Si está vacío, asigna el valor por defecto para la base de datos
            telefonoParaSQL = "0000000000";
        } else {
            // Si tiene datos, realiza las validaciones de formato
            if (telefono.length() != 10) {
                JOptionPane.showMessageDialog(this, "El Teléfono debe tener exactamente 10 dígitos.", "Validación Teléfono", JOptionPane.WARNING_MESSAGE);
                jtxtTelefono.requestFocus();
                return;
            }
            if (!telefono.startsWith("09")) {
                JOptionPane.showMessageDialog(this, "El número de Teléfono debe comenzar con '09'.", "Validación Teléfono", JOptionPane.WARNING_MESSAGE);
                jtxtTelefono.requestFocus();
                return;
            }
            // Si pasa la validación, usa el valor ingresado
            telefonoParaSQL = telefono;
            
        }
        if (jcboxGenero.getSelectedIndex() == 0) { 
            JOptionPane.showMessageDialog(this, "Debe seleccionar el Género.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            jcboxGenero.requestFocus();
            return;
        }
        String generoSeleccionado = jcboxGenero.getSelectedItem().toString();
        try {
            conexion cn = new conexion();
            Connection cc = (Connection) cn.conectar();

            // 🚨 CÓDIGO ORIGINAL (Mantiene la vulnerabilidad a SQL Injection)
            String Sql = "update estudiantes set estnombre='" + jtxtNombre.getText() + "'"
                    + ", estapellido='" + jtxtApellido.getText()
                    + "', estdireccion='" + jtxtDireccion.getText()
                    + "', esttelefono='" + telefonoParaSQL + "'" // Se agregó el campo teléfono
                    + ", estgenero='" + generoSeleccionado + "'" + " where estcedula='" + jtxtCedula.getText() + "'";

            PreparedStatement psd = cc.prepareStatement(Sql);

            if (jtxtNombre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el Nombre.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                jtxtNombre.requestFocus();
                return;
            }
            // 4. Validar Apellido vacío
            if (jtxtApellido.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el Apellido.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                jtxtApellido.requestFocus();
                return;
            }
            // 5. Validar Dirección vacía
            if (jtxtDireccion.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar la Dirección.", "Campo Requerido", JOptionPane.WARNING_MESSAGE);
                jtxtDireccion.requestFocus();
                return;
            }

            int opc = psd.executeUpdate();
            if (opc > 0) {
                JOptionPane.showMessageDialog(null, "Se modifico el estudiante");
                mostrarDatos();
                limpiarCampos();
                botonesInicio(); // Añadido para volver al estado inicial después de editar
                textosInicio();
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el estudiante o no se realizaron cambios.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void cargarCampos() {
        jtblAlumnos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (jtblAlumnos.getSelectedRow() != -1) {
                    int fila = jtblAlumnos.getSelectedRow();
                    jtxtCedula.setText(jtblAlumnos.getValueAt(fila, 0).toString().trim());
                    jtxtNombre.setText(jtblAlumnos.getValueAt(fila, 1).toString().trim());
                    jtxtApellido.setText(jtblAlumnos.getValueAt(fila, 2).toString().trim());
                    jtxtDireccion.setText(jtblAlumnos.getValueAt(fila, 3).toString().trim());
                    jtxtTelefono.setText(jtblAlumnos.getValueAt(fila, 4).toString().trim());
                    if (jtblAlumnos.getColumnCount() > 5) {
                         String genero = jtblAlumnos.getValueAt(fila, 5).toString().trim();
                         jcboxGenero.setSelectedItem(genero);
                    }
                    botonesEliminar();
                    botonesEditar();
                    textosEditar();
                }
            }
        });

    }

    public void buscarDatos(String cedula) {
        String filtro = cedula.trim();

        try {
            DefaultTableModel tabla = new DefaultTableModel();
            String titulos[] = {"Cedula", "Nombre", "Apellido", "Direccion", "Telefono", "Genero"};
            String registros[] = new String[6];
            tabla = new DefaultTableModel(null, titulos);

            conexion cn = new conexion();
            Connection cc = cn.conectar();

            // Query SQL con LIKE: 'estcedula LIKE ?'
            // Esto buscará todas las cédulas que empiecen con el valor proporcionado (filtro + "%")
            String SqlSelect = "SELECT * FROM estudiantes WHERE estcedula LIKE ? OR estnombre LIKE ?";

            PreparedStatement psd = cc.prepareStatement(SqlSelect);

            // Usamos el placeholder '?'
            psd.setString(1, filtro + "%");
            psd.setString(2, filtro + "%"); // Opcionalmente, también podemos buscar por nombre

            ResultSet rs = psd.executeQuery();

            while (rs.next()) {
                registros[0] = rs.getString("estcedula");
                registros[1] = rs.getString("estnombre");
                registros[2] = rs.getString("estapellido");
                registros[3] = rs.getString("estdireccion");
                registros[4] = rs.getString("esttelefono");
                registros[5] = rs.getString("estgenero");
                tabla.addRow(registros);
            }

            jtblAlumnos.setModel(tabla);
            rs.close();
            psd.close();
            cc.close();

        } catch (Exception ex) {
            // En un entorno de producción, es mejor imprimir el error para depurar
            // ex.printStackTrace(); 
            JOptionPane.showMessageDialog(null, "Error al buscar datos: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        jtxtCedula.setText("");
        jtxtNombre.setText("");
        jtxtApellido.setText("");
        jtxtDireccion.setText("");
        jtxtTelefono.setText("");
        jcboxGenero.setSelectedIndex(0);
    }

    public void botonesInicio() {
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
    }

    public void textosInicio() {
        jtxtCedula.setEnabled(false);
        jtxtNombre.setEnabled(false);
        jtxtApellido.setEnabled(false);
        jtxtDireccion.setEnabled(false);
        jtxtTelefono.setEnabled(false);
        jcboxGenero.setEnabled(false);
    }

    public void botonesNuevo() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
    }

    public void textosNuevo() {
        jtxtCedula.setEnabled(true);
        jtxtNombre.setEnabled(true);
        jtxtApellido.setEnabled(true);
        jtxtDireccion.setEnabled(true);
        jtxtTelefono.setEnabled(true);
        jcboxGenero.setEnabled(true);
    }

    public void textosEditar() {
        jtxtCedula.setEnabled(false);
        jtxtNombre.setEnabled(true);
        jtxtApellido.setEnabled(true);
        jtxtDireccion.setEnabled(true);
        jtxtTelefono.setEnabled(true);
        jcboxGenero.setEnabled(true);
    }

    public void botonesEditar() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnEliminar.setEnabled(true);
        jbtnEditar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    public void botonesEliminar() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblAlumnos = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jtxtBuscarCedula = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jtxtCedula = new javax.swing.JTextField();
        jtxtNombre = new javax.swing.JTextField();
        jtxtApellido = new javax.swing.JTextField();
        jtxtTelefono = new javax.swing.JTextField();
        jtxtDireccion = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jcboxGenero = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jbtnNuevo = new javax.swing.JButton();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jbtnCancelar = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(250, 250, 250));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jtblAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jtblAlumnos);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 102));
        jLabel7.setText("Buscar por cedula: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jtxtBuscarCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jtxtBuscarCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(250, 250, 250));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Cedula:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(4, 4, 34));
        jLabel2.setText("Nombre:");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(4, 4, 34));
        jLabel3.setText("Apellido:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(4, 4, 34));
        jLabel4.setText("Telefono:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(4, 4, 34));
        jLabel5.setText("Direccion:");

        jtxtCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtCedulaActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Genero:");

        jcboxGenero.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        jcboxGenero.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jcboxGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 63, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtxtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jtxtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jtxtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jcboxGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(60, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(250, 250, 250));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jbtnNuevo.setBackground(new java.awt.Color(4, 4, 34));
        jbtnNuevo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        jbtnNuevo.setText("Nuevo");
        jbtnNuevo.setBorderPainted(false);
        jbtnNuevo.setFocusPainted(false);
        jbtnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnNuevoActionPerformed(evt);
            }
        });

        jbtnGuardar.setBackground(new java.awt.Color(4, 4, 34));
        jbtnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnGuardar.setText("Guardar");
        jbtnGuardar.setBorderPainted(false);
        jbtnGuardar.setFocusPainted(false);
        jbtnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGuardarActionPerformed(evt);
            }
        });

        jbtnEditar.setBackground(new java.awt.Color(4, 4, 34));
        jbtnEditar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtnEditar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnEditar.setText("Editar");
        jbtnEditar.setBorderPainted(false);
        jbtnEditar.setFocusPainted(false);
        jbtnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEditarActionPerformed(evt);
            }
        });

        jbtnEliminar.setBackground(new java.awt.Color(4, 4, 34));
        jbtnEliminar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jbtnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnEliminar.setText("Eliminar");
        jbtnEliminar.setBorderPainted(false);
        jbtnEliminar.setFocusPainted(false);
        jbtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnEliminarActionPerformed(evt);
            }
        });

        jbtnCancelar.setBackground(new java.awt.Color(4, 4, 34));
        jbtnCancelar.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        jbtnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        jbtnCancelar.setText("Cancelar");
        jbtnCancelar.setBorderPainted(false);
        jbtnCancelar.setFocusPainted(false);
        jbtnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnCancelarActionPerformed(evt);
            }
        });

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registro.png"))); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/guardar.png"))); // NOI18N

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cancelar.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jbtnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jbtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbtnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel9)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel6.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(4, 4, 34));
        jLabel6.setText("ESTUDIANTES");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(4, 4, 34));
        jLabel13.setText("Datos: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(430, 430, 430)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 1008, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(51, 51, 51)
                                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(198, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jtxtCedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtCedulaActionPerformed
    }//GEN-LAST:event_jtxtCedulaActionPerformed

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminarDatos();
    }//GEN-LAST:event_jbtnEliminarActionPerformed

    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardarDatos();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        editarDatos();
        botonesEditar();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        botonesNuevo();
        textosNuevo();
        limpiarCampos();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        textosInicio();
        botonesInicio();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    /**
     * @param args the cjsndommand line arguments
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
            java.util.logging.Logger.getLogger(Alumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Alumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Alumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Alumnos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Alumnos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JComboBox<String> jcboxGenero;
    private javax.swing.JTable jtblAlumnos;
    private javax.swing.JTextField jtxtApellido;
    private javax.swing.JTextField jtxtBuscarCedula;
    private javax.swing.JTextField jtxtCedula;
    private javax.swing.JTextField jtxtDireccion;
    private javax.swing.JTextField jtxtNombre;
    private javax.swing.JTextField jtxtTelefono;
    // End of variables declaration//GEN-END:variables

}
