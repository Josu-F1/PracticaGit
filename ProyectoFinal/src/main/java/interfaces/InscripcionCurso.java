/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import proyecto.conexion;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bl250
 */
public class InscripcionCurso extends javax.swing.JInternalFrame {

    private int idCursoSeleccionado = -1;

    public InscripcionCurso() {
        initComponents();
        cargarEstudiantesComboBox();
        cargarCursosComboBox();
        mostrarDatos();
        mostrarMatriculas();
        cargarCampos();
        botonesInicio();
        textosInicio();
        ValidarEntrada();
    }

    private void ValidarEntrada() {
        // --- Validación: solo letras y espacios, máximo 20 caracteres ---
        jtxtNombreCurso.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = jtxtNombreCurso.getText();
                int currentLength = currentText.length();

                // 🔒 Verificar longitud máxima
                if (currentLength >= 20 && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    JOptionPane.showMessageDialog(jtxtNombreCurso,
                            "El nombre del curso no puede exceder los 20 caracteres.",
                            "Límite alcanzado",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // 🔠 Solo letras y espacios permitidos
                if (!Character.isLetter(c) && c != ' '
                        && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                    JOptionPane.showMessageDialog(jtxtNombreCurso,
                            "Solo se permiten letras y espacios.",
                            "Entrada no válida",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    public void guardarDatos() {

        String nombreCurso = jtxtNombreCurso.getText().trim();

        // Validar vacío
        if (nombreCurso.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Debe ingresar el nombre del curso");
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validar solo letras y espacios
        if (!nombreCurso.matches("[\\p{L} ]+")) {
            JOptionPane.showMessageDialog(null, "Solo se permiten letras y espacios en el nombre del curso.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validar longitud máx 20
        if (nombreCurso.length() > 20) {
            JOptionPane.showMessageDialog(null, "El nombre del curso no puede exceder 20 caracteres.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        try {
            conexion cn = new conexion();
            Connection cc = cn.conectar();

            // ✅ Verificar si ya existe un curso con el mismo nombre (sin distinguir mayúsculas/minúsculas)
            String SqlVerificar = "SELECT COUNT(*) FROM cursos WHERE LOWER(nombre_curso) = LOWER(?)";
            PreparedStatement psVerificar = cc.prepareStatement(SqlVerificar);
            psVerificar.setString(1, nombreCurso);
            ResultSet rs = psVerificar.executeQuery();
            rs.next();

            int existe = rs.getInt(1);
            rs.close();
            psVerificar.close();

            if (existe > 0) {
                JOptionPane.showMessageDialog(null,
                        "⚠ Ya existe un curso con el nombre \"" + nombreCurso + "\".\nPor favor, elija otro nombre.",
                        "Curso duplicado", JOptionPane.WARNING_MESSAGE);
                jtxtNombreCurso.requestFocus();
                return; // ❌ No continúa si el curso ya existe
            }

            // ✅ Si no existe, insertar el nuevo curso
            String SqlInsert = "INSERT INTO cursos (nombre_curso) VALUES (?)";
            PreparedStatement psInsert = cc.prepareStatement(SqlInsert);
            psInsert.setString(1, nombreCurso);

            int opc = psInsert.executeUpdate();

            if (opc > 0) {
                JOptionPane.showMessageDialog(null, "✅ Curso guardado exitosamente");
                mostrarDatos();
                limpiarCampos();
                botonesInicio();
                textosInicio();
                cargarCursosComboBox();
                cargarEstudiantesComboBox();
            }

            psInsert.close();
            cc.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al guardar: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    public void mostrarDatos() {
        try {

            String titulos[] = {"ID Curso", "Nombre del Curso"};

            DefaultTableModel tabla = new DefaultTableModel(null, titulos) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 🔒 Evita editar directamente desde la tabla
                }
            };
            String registros[] = new String[2];

            conexion cn = new conexion();
            Connection cc = cn.conectar();
            String SqlSelect = "SELECT * FROM cursos ORDER BY id_curso";
            Statement psd = cc.createStatement();
            ResultSet rs = psd.executeQuery(SqlSelect);

            while (rs.next()) {
                registros[0] = rs.getString("id_curso");
                registros[1] = rs.getString("nombre_curso");
                tabla.addRow(registros);
            }

            jtblCursos.setModel(tabla);

            rs.close();
            psd.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al mostrar datos: " + ex.getMessage());
        }
    }

    public void eliminarDatos() {
        // Verificar selección
        if (idCursoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso de la tabla para eliminar");
            return;
        }

        conexion cn = new conexion();
        try (Connection cc = cn.conectar()) {
            if (cc == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos");
                return;
            }

            // ¿Cuántas matrículas apuntan a este curso?
            int totalMatriculas = contarMatriculasPorCurso(cc, idCursoSeleccionado);

            String nombreCurso = jtxtNombreCurso.getText().trim();
            String mensaje;

            if (totalMatriculas > 0) {
                mensaje = "El curso \"" + nombreCurso + "\" tiene " + totalMatriculas + " matrícula(s) asociada(s).\n\n"
                        + "Si continúa, se eliminarán primero esas matrículas y luego el curso.\n\n"
                        + "¿Desea continuar?";
            } else {
                mensaje = "¿Está seguro de eliminar el curso: " + nombreCurso + "?";
            }

            int confirmacion = JOptionPane.showConfirmDialog(
                    this, mensaje, "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
            );

            if (confirmacion != JOptionPane.YES_OPTION) {
                return;
            }

            // Transacción: borrar matriculas (si hay) y luego el curso
            boolean oldAutoCommit = cc.getAutoCommit();
            cc.setAutoCommit(false);
            try {
                if (totalMatriculas > 0) {
                    try (PreparedStatement psDelM = cc.prepareStatement(
                            "DELETE FROM matriculas WHERE id_cur_per = ?")) {
                        psDelM.setInt(1, idCursoSeleccionado);
                        psDelM.executeUpdate();
                    }
                }

                int filas;
                try (PreparedStatement psDelC = cc.prepareStatement(
                        "DELETE FROM cursos WHERE id_curso = ?")) {
                    psDelC.setInt(1, idCursoSeleccionado);
                    filas = psDelC.executeUpdate();
                }

                if (filas > 0) {
                    cc.commit();
                    JOptionPane.showMessageDialog(this, "Curso eliminado correctamente");
                    mostrarDatos();
                    mostrarMatriculas();
                    limpiarCampos();
                    botonesInicio();
                    textosInicio();
                    cargarCursosComboBox();
                    cargarEstudiantesComboBox();
                } else {
                    cc.rollback();
                    JOptionPane.showMessageDialog(this, "No se encontró el curso a eliminar");
                }
            } catch (SQLException exTx) {
                cc.rollback();
                JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + exTx.getMessage(),
                        "Error SQL", JOptionPane.ERROR_MESSAGE);
            } finally {
                cc.setAutoCommit(oldAutoCommit);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private int contarMatriculasPorCurso(Connection cc, int idCurso) throws SQLException {
        try (PreparedStatement ps = cc.prepareStatement(
                "SELECT COUNT(*) FROM matriculas WHERE id_cur_per = ?")) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public void editarDatos() {
        if (idCursoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un curso de la tabla para editar.",
                    "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nuevoNombre = jtxtNombreCurso.getText().trim();

        // Validar vacío
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el nombre del curso.",
                    "Campo Requerido", JOptionPane.WARNING_MESSAGE);
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validar solo letras y espacios
        if (!nuevoNombre.matches("[\\p{L} ]+")) {
            JOptionPane.showMessageDialog(this, "Solo se permiten letras y espacios en el nombre del curso.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validar longitud máx 20
        if (nuevoNombre.length() > 20) {
            JOptionPane.showMessageDialog(this, "El nombre del curso no puede exceder 20 caracteres.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validación opcional: máximo 20 (coincide con varchar(20))
        if (nuevoNombre.length() > 20) {
            JOptionPane.showMessageDialog(this, "El nombre no puede exceder 20 caracteres.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        // Validación opcional: solo letras, números y espacios (igual que en el KeyListener)
        if (!nuevoNombre.matches("[\\p{L}\\p{N} ]+")) {
            JOptionPane.showMessageDialog(this, "Solo se permiten letras, números y espacios.");
            jtxtNombreCurso.requestFocus();
            return;
        }

        try {
            conexion cn = new conexion();
            try (Connection cc = cn.conectar()) {
                if (cc == null) {
                    JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos");
                    return;
                }

                // Verificar duplicados (ignora mayúsculas/minúsculas y excluye el propio id)
                String sqlVerificar = "SELECT COUNT(*) FROM cursos "
                        + "WHERE LOWER(nombre_curso) = LOWER(?) AND id_curso <> ?";
                try (PreparedStatement psVer = cc.prepareStatement(sqlVerificar)) {
                    psVer.setString(1, nuevoNombre);
                    psVer.setInt(2, idCursoSeleccionado);
                    try (ResultSet rs = psVer.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(this,
                                    "⚠ Ya existe un curso con el nombre \"" + nuevoNombre + "\".\n"
                                    + "Por favor, elija otro nombre.",
                                    "Curso duplicado", JOptionPane.WARNING_MESSAGE);
                            jtxtNombreCurso.requestFocus();
                            return;
                        }
                    }
                }

                // Actualizar
                String Sql = "UPDATE cursos SET nombre_curso = ? WHERE id_curso = ?";
                try (PreparedStatement psd = cc.prepareStatement(Sql)) {
                    psd.setString(1, nuevoNombre);
                    psd.setInt(2, idCursoSeleccionado);

                    int opc = psd.executeUpdate();
                    if (opc > 0) {
                        JOptionPane.showMessageDialog(this, "Curso modificado exitosamente");
                        mostrarDatos();
                        limpiarCampos();
                        botonesInicio();
                        textosInicio();
                        cargarCursosComboBox();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No se encontró el curso o no se realizaron cambios.");
                    }
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + ex.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + ex.getMessage());
        }
    }

    public void cargarCampos() {
        jtblCursos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && jtblCursos.getSelectedRow() != -1) {
                    int fila = jtblCursos.getSelectedRow();

                    // Guardar el ID del curso seleccionado
                    idCursoSeleccionado = Integer.parseInt(jtblCursos.getValueAt(fila, 0).toString().trim());

                    // Cargar el nombre del curso en el campo editable
                    jtxtNombreCurso.setText(jtblCursos.getValueAt(fila, 1).toString().trim());

                    // Habilitar botones de Editar y Eliminar
                    botonesEliminarEditar();
                    textosEditar();
                }
            }
        });
    }

    public void buscarDatos(String busqueda) {
        String filtro = busqueda.trim();

        try {
            DefaultTableModel tabla = new DefaultTableModel();
            String titulos[] = {"ID Curso", "Nombre del Curso"};
            String registros[] = new String[2];
            tabla = new DefaultTableModel(null, titulos);

            conexion cn = new conexion();
            Connection cc = cn.conectar();

            // Búsqueda por ID o nombre
            String SqlSelect = "SELECT * FROM cursos WHERE id_curso LIKE ? OR nombre_curso LIKE ? ORDER BY id_curso";
            PreparedStatement psd = cc.prepareStatement(SqlSelect);

            psd.setString(1, filtro + "%");
            psd.setString(2, "%" + filtro + "%");

            ResultSet rs = psd.executeQuery();

            while (rs.next()) {
                registros[0] = rs.getString("id_curso");
                registros[1] = rs.getString("nombre_curso");
                tabla.addRow(registros);
            }

            jtblCursos.setModel(tabla);

            rs.close();
            psd.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al buscar: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        jtxtNombreCurso.setText("");
        jtblCursos.clearSelection();
        idCursoSeleccionado = -1; // Resetear el ID seleccionado
    }

    // === MÉTODOS PARA CONTROLAR BOTONES ===
    public void botonesInicio() {
        jbtnNuevo.setEnabled(true);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
    }

    public void textosInicio() {
        jtxtNombreCurso.setEnabled(false);
    }

    public void botonesNuevo() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(true);
        jbtnEditar.setEnabled(false);
        jbtnEliminar.setEnabled(false);
        jbtnCancelar.setEnabled(true);
    }

    public void textosNuevo() {
        jtxtNombreCurso.setEnabled(true);
    }

    public void textosEditar() {
        jtxtNombreCurso.setEnabled(true);
    }

    public void botonesEditar() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
    }

    public void botonesEliminarEditar() {
        jbtnNuevo.setEnabled(false);
        jbtnGuardar.setEnabled(false);
        jbtnEditar.setEnabled(true);
        jbtnEliminar.setEnabled(true);
        jbtnCancelar.setEnabled(true);
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

        Vector<EstudianteComboBox> items = new Vector<>();

        conexion db = new conexion();
        Connection conn = null;

        String sql = "SELECT estcedula, estnombre, estapellido FROM estudiantes ORDER BY estnombre";

        try {
            conn = db.conectar();
            if (conn == null) {
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String cedula = rs.getString("estcedula");
                    String nombre = rs.getString("estnombre");
                    String apellido = rs.getString("estapellido");

                    items.add(new EstudianteComboBox(cedula, nombre, apellido));
                }

                // ✅ AHORA SÍ los agregamos al combo
                jComboBoxEstudiantes.setModel(new DefaultComboBoxModel<>(items));

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de SQL al cargar estudiantes: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }

    private class CursoComboBox {

        int idCurso;
        String nombreCurso;

        public CursoComboBox(int id, String nombre) {
            this.idCurso = id;
            this.nombreCurso = nombre;
        }

        @Override
        public String toString() {
            return nombreCurso;  // ✅ SOLO NOMBRE VISIBLE
        }
    }

    private void cargarCursosComboBox() {
        Vector<CursoComboBox> items = new Vector<>();
        conexion db = new conexion();
        Connection conn = null;

        String sql = "SELECT id_curso, nombre_curso FROM cursos ORDER BY nombre_curso";

        try {
            conn = db.conectar();
            if (conn == null) {
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id_curso");
                    String nombre = rs.getString("nombre_curso");
                    items.add(new CursoComboBox(id, nombre));
                }

                jComboBoxCursos.setModel(new DefaultComboBoxModel<>(items));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de SQL al cargar cursos: " + e.getMessage(),
                    "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }

    public void mostrarMatriculas() {
        try {

            String titulos[] = {"ID Matrícula", "Nombre del Curso", "Cédula Estudiante"};
            DefaultTableModel tabla = new DefaultTableModel(null, titulos) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // 🔒 Bloquea edición de las celdas
                }
            };
            String registros[] = new String[3];

            conexion cn = new conexion();
            Connection cc = cn.conectar();

            String sql
                    = "SELECT m.id_mat, c.nombre_curso, m.estcedula_per "
                    + "FROM matriculas m "
                    + "INNER JOIN cursos c ON m.id_cur_per = c.id_curso "
                    + "ORDER BY m.id_mat";

            Statement psd = cc.createStatement();
            ResultSet rs = psd.executeQuery(sql);

            while (rs.next()) {
                registros[0] = rs.getString("id_mat");
                registros[1] = rs.getString("nombre_curso");
                registros[2] = rs.getString("estcedula_per");
                tabla.addRow(registros);
            }

            jtableMatriculas.setModel(tabla);

            rs.close();
            psd.close();
            cc.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al mostrar matrículas: " + ex.getMessage());
        }
    }

    public void registrarMatricula() {
        try {
            // Obtener estudiante seleccionado
            EstudianteComboBox est = (EstudianteComboBox) jComboBoxEstudiantes.getSelectedItem();
            // Obtener curso seleccionado
            CursoComboBox curso = (CursoComboBox) jComboBoxCursos.getSelectedItem();

            // Validar selección
            if (est == null || curso == null) {
                JOptionPane.showMessageDialog(this,
                        "Debe seleccionar un estudiante y un curso antes de matricular.",
                        "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cedula = est.cedula;
            int idCurso = curso.idCurso;

            // Conexión
            conexion cn = new conexion();
            Connection cc = cn.conectar();

            // 🔎 Verificar si ya existe una matrícula del estudiante en ese curso
            String sqlVerificar = "SELECT COUNT(*) FROM matriculas WHERE id_cur_per = ? AND estcedula_per = ?";
            PreparedStatement psVerificar = cc.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, idCurso);
            psVerificar.setString(2, cedula);
            ResultSet rs = psVerificar.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "⚠ El estudiante ya está matriculado en este curso.",
                        "Matrícula duplicada", JOptionPane.WARNING_MESSAGE);
                rs.close();
                psVerificar.close();
                cc.close();
                return; // 🚫 Evita insertar duplicado
            }

            rs.close();
            psVerificar.close();

            // ✅ Si no existe, registrar normalmente
            String sqlInsert = "INSERT INTO matriculas (id_cur_per, estcedula_per) VALUES (?, ?)";
            PreparedStatement psInsert = cc.prepareStatement(sqlInsert);
            psInsert.setInt(1, idCurso);
            psInsert.setString(2, cedula);

            int res = psInsert.executeUpdate();

            if (res > 0) {
                JOptionPane.showMessageDialog(this, "✅ Matrícula registrada correctamente");
                mostrarMatriculas(); // Actualiza la tabla
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo registrar la matrícula.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            psInsert.close();
            cc.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar matrícula: " + e.getMessage(),
                    "Error SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtxtNombreCurso = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtblCursos = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jbtnNuevo = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jbtnGuardar = new javax.swing.JButton();
        jbtnEditar = new javax.swing.JButton();
        jbtnEliminar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jbtnCancelar = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel7 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jComboBoxEstudiantes = new javax.swing.JComboBox<>();
        jComboBoxCursos = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtableMatriculas = new javax.swing.JTable();
        jbtnMatricular = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 51));
        jLabel1.setText("CURSOS");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(4, 4, 34));
        jLabel2.setText("Nombre del curso:");

        jtblCursos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jtblCursos);

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(4, 4, 34));
        jLabel13.setText("CREACION:");

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

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

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/registro.png"))); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/guardar.png"))); // NOI18N

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

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/eliminar.png"))); // NOI18N

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

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cancelar.png"))); // NOI18N

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/editar-archivo.png"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jbtnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jbtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbtnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8)
                            .addComponent(jbtnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addComponent(jLabel9))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jbtnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbtnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel11))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 51));
        jLabel7.setText("Cursos existentes:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jtxtNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1)
                            .addComponent(jLabel7)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtxtNombreCurso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jComboBoxEstudiantes.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(4, 4, 34));
        jLabel3.setText("Cursos:");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(4, 4, 34));
        jLabel4.setText("Estudiantes:");

        jtableMatriculas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jtableMatriculas);

        jbtnMatricular.setBackground(new java.awt.Color(0, 0, 51));
        jbtnMatricular.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbtnMatricular.setForeground(new java.awt.Color(255, 255, 255));
        jbtnMatricular.setText("Inscribir");
        jbtnMatricular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnMatricularActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 51));
        jLabel6.setText("INSCRIPCION:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jbtnMatricular, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel6)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(22, 22, 22)
                            .addComponent(jComboBoxCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(27, 27, 27)
                            .addComponent(jComboBoxEstudiantes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxCursos, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBoxEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbtnMatricular, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(467, 467, 467))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator2)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEliminarActionPerformed
        eliminarDatos();
    }//GEN-LAST:event_jbtnEliminarActionPerformed


    private void jbtnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGuardarActionPerformed
        guardarDatos();
    }//GEN-LAST:event_jbtnGuardarActionPerformed

    private void jbtnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnEditarActionPerformed
        editarDatos();
    }//GEN-LAST:event_jbtnEditarActionPerformed

    private void jbtnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnNuevoActionPerformed
        limpiarCampos();
        botonesNuevo();
        textosNuevo();
        jtxtNombreCurso.requestFocus();
    }//GEN-LAST:event_jbtnNuevoActionPerformed

    private void jbtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnCancelarActionPerformed
        limpiarCampos();
        botonesInicio();
        textosInicio();
        mostrarDatos();
    }//GEN-LAST:event_jbtnCancelarActionPerformed

    private void jbtnMatricularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnMatricularActionPerformed
        // TODO add your handling code here:
        registrarMatricula();
    }//GEN-LAST:event_jbtnMatricularActionPerformed

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
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
    private javax.swing.JComboBox<CursoComboBox> jComboBoxCursos;
    private javax.swing.JComboBox<EstudianteComboBox> jComboBoxEstudiantes;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton jbtnCancelar;
    private javax.swing.JButton jbtnEditar;
    private javax.swing.JButton jbtnEliminar;
    private javax.swing.JButton jbtnGuardar;
    private javax.swing.JButton jbtnMatricular;
    private javax.swing.JButton jbtnNuevo;
    private javax.swing.JTable jtableMatriculas;
    private javax.swing.JTable jtblCursos;
    private javax.swing.JTextField jtxtNombreCurso;
    // End of variables declaration//GEN-END:variables

}
