package com.example.poo_practica_prueba_00;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;

public class CrudController {
    @FXML private TextField txtCedula, txtNombre;
    @FXML private ComboBox<String> cmbCarrera;
    @FXML private CheckBox chkActivo;

    // --- CONTROLES NUEVOS DEL DOCUMENTO ---
    @FXML private RadioButton rdMas;
    @FXML private RadioButton rdFem;
    @FXML private ToggleGroup grupoGenero; // Maneja la exclusión mutua

    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, Integer> colId;
    @FXML private TableColumn<Estudiante, String> colNombre, colCedula, colCarrera, colGenero; // Añadida colGenero
    @FXML private TableColumn<Estudiante, Boolean> colActivo;

    private ObservableList<Estudiante> listaEstudiante;
    private Estudiante estudianteSeleccionado;

    @FXML
    public void initialize() {
        cmbCarrera.setItems(FXCollections.observableArrayList("Desarrollo de Software", "Redes", "Electromecánica"));

        // Vincular columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera"));
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero")); // Mapeo de la nueva columna
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // --- NUEVO: LISTENER DE SELECCIÓN AUTOMÁTICA (Reemplaza el On Mouse Clicked) ---
        tablaEstudiantes.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        estudianteSeleccionado = newSelection;
                        txtCedula.setText(newSelection.getCedula());
                        txtNombre.setText(newSelection.getNombre());
                        cmbCarrera.setValue(newSelection.getCarrera());
                        chkActivo.setSelected(newSelection.isActivo());

                        // Seleccionar el RadioButton correcto según el dato de la fila
                        if (newSelection.getGenero().equals("Masculino")) {
                            rdMas.setSelected(true);
                        } else if (newSelection.getGenero().equals("Femenino")) {
                            rdFem.setSelected(true);
                        }
                    }
                }
        );

        cargarDatosTabla();
    }

    @FXML
    private void cargarDatosTabla() {
        listaEstudiante = FXCollections.observableArrayList();
        String query = "SELECT * FROM estudiantes"; // Uso de Statement básico para lecturas globales

        try (Connection con = Conexion.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) { // executeQuery devuelve un ResultSet

            while (rs.next()) {
                listaEstudiante.add(new Estudiante(
                        rs.getInt("id"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("carrera"),
                        rs.getString("genero"), // Extraemos la nueva columna texto
                        rs.getBoolean("activo")
                ));
            }
            tablaEstudiantes.setItems(listaEstudiante);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onGuardar() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String carrera = cmbCarrera.getValue();
        boolean activo = chkActivo.isSelected();

        // Capturar cuál RadioButton está seleccionado en el ToggleGroup
        String genero = "";
        if (rdMas.isSelected()) {
            genero = "Masculino";
        } else if (rdFem.isSelected()) {
            genero = "Femenino";
        }

        // Validación incluyendo el nuevo campo
        if (cedula.isEmpty() || nombre.isEmpty() || carrera == null || genero.isEmpty()) {
            mostrarAlerta("Validacion", "Llene todos los campos, incluido el género.", Alert.AlertType.WARNING);
            return;
        }
        if (cedula.length() != 10 || !cedula.matches("\\d+")) {
            mostrarAlerta("Validacion", "La cedula no es valida.", Alert.AlertType.ERROR);
            return;
        }

        String query = "INSERT INTO estudiantes (cedula, nombre, carrera, genero, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement state = con.prepareStatement(query)) { // Uso seguro de PreparedStatement

            state.setString(1, cedula);
            state.setString(2, nombre);
            state.setString(3, carrera);
            state.setString(4, genero);
            state.setBoolean(5, activo);

            state.executeUpdate(); // executeUpdate para operaciones de escritura (INSERT)
            mostrarAlerta("Exito", "Estudiante guardado en la base de datos.", Alert.AlertType.INFORMATION);

            cargarDatosTabla();
            onLimpiar();

        } catch (SQLException e) {
            mostrarAlerta("Error BD", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onModificar() {
        if (estudianteSeleccionado == null) {
            mostrarAlerta("Atención", "Primero debes seleccionar un estudiante de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String carrera = cmbCarrera.getValue();
        boolean activo = chkActivo.isSelected();

        String genero = "";
        if (rdMas.isSelected()) genero = "Masculino";
        if (rdFem.isSelected()) genero = "Femenino";

        if (cedula.isEmpty() || nombre.isEmpty() || carrera == null || genero.isEmpty() || cedula.length() != 10) {
            mostrarAlerta("Validación", "Verifique todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        String query = "UPDATE estudiantes SET cedula = ?, nombre = ?, carrera = ?, genero = ?, activo = ? WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, carrera);
            pstmt.setString(4, genero);
            pstmt.setBoolean(5, activo);
            pstmt.setInt(6, estudianteSeleccionado.getId());

            pstmt.executeUpdate(); // executeUpdate para modificaciones
            mostrarAlerta("Éxito", "Estudiante modificado correctamente.", Alert.AlertType.INFORMATION);

            cargarDatosTabla();
            onLimpiar();
            estudianteSeleccionado = null;

        } catch (SQLException e) {
            mostrarAlerta("Error BD", "Error al modificar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar() {
        if (estudianteSeleccionado == null) {
            mostrarAlerta("Atención", "Primero debes seleccionar un estudiante.", Alert.AlertType.WARNING);
            return;
        }

        String query = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, estudianteSeleccionado.getId());
            pstmt.executeUpdate(); // executeUpdate para eliminaciones

            mostrarAlerta("Éxito", "Estudiante eliminado.", Alert.AlertType.INFORMATION);

            cargarDatosTabla();
            onLimpiar();
            estudianteSeleccionado = null;

        } catch (SQLException e) {
            mostrarAlerta("Error BD", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onLimpiar() {
        txtCedula.clear();
        txtNombre.clear();
        cmbCarrera.setValue(null);
        chkActivo.setSelected(false);
        if (grupoGenero.getSelectedToggle() != null) {
            grupoGenero.getSelectedToggle().setSelected(false); // Desmarcar los RadioButtons
        }
    }

    void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}