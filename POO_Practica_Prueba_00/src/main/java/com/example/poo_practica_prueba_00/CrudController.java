package com.example.poo_practica_prueba_00;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CrudController {
    @FXML
    private TextField txtCedula, txtNombre;
    @FXML
    private ComboBox<String> cmbCarrera;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private TableView<Estudiante>  tablaEstudiantes;
    @FXML
    private TableColumn<Estudiante,String> colNombre, colCedula, colCarrera;
    @FXML
    private TableColumn<Estudiante,Integer> colId;
    @FXML
    private TableColumn<Estudiante, Boolean> colActivo;

    private ObservableList<Estudiante> listaEstudiante;
    private Estudiante estudianteSeleccionado;

    @FXML
    public void initialize(){
        cmbCarrera.setItems(FXCollections.observableArrayList("Desarrollo de Software", "Redes", "Electromecánica"));

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        cargarDatosTabla();
    }

    @FXML
    private void cargarDatosTabla() {
        listaEstudiante = FXCollections.observableArrayList();
        String query = "SELECT * FROM estudiantes";

        try (Connection con = Conexion.conectar();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                listaEstudiante.add(new Estudiante(
                        rs.getInt("id"),
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("carrera"),
                        rs.getBoolean("activo")
                ));
            }
            tablaEstudiantes.setItems(listaEstudiante);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void alSeleccionarFila(){
        estudianteSeleccionado = tablaEstudiantes.getSelectionModel().getSelectedItem();

        if (estudianteSeleccionado != null) {
            txtCedula.setText(estudianteSeleccionado.getCedula());
            txtNombre.setText(estudianteSeleccionado.getNombre());
            cmbCarrera.setValue(estudianteSeleccionado.getCarrera());
            chkActivo.setSelected(estudianteSeleccionado.isActivo());
        }
    }

    @FXML
    public void onGuardar(){
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String carrera = cmbCarrera.getValue();
        boolean activo = chkActivo.isSelected();

        if(cedula.isEmpty() || nombre.isEmpty() || carrera == null){
            mostrarAlerta("Validacion", "Llene todos los campos.", Alert.AlertType.WARNING);
            return;
        }
        if(cedula.length() != 10 || !cedula.matches("\\d+")){
            mostrarAlerta("Validacion", "La cedula no es valida.", Alert.AlertType.ERROR);
            return;
        }

        String query = "insert into estudiantes (cedula, nombre, carrera, activo) values (?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement state = con.prepareStatement(query)){

            state.setString(1, cedula);
            state.setString(2, nombre);
            state.setString(3, carrera);
            state.setBoolean(4, activo);

            state.executeUpdate();
            mostrarAlerta("Exito", "Estudiante guardado en la base de datos.", Alert.AlertType.INFORMATION);

            cargarDatosTabla();
            onLimpiar();

        }catch (SQLException e){
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

        if (cedula.isEmpty() || nombre.isEmpty() || carrera == null || cedula.length() != 10) {
            mostrarAlerta("Validación", "Verifica que todos los campos estén llenos y la cédula tenga 10 dígitos.", Alert.AlertType.WARNING);
            return;
        }

        String query = "UPDATE estudiantes SET cedula = ?, nombre = ?, carrera = ?, activo = ? WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, carrera);
            pstmt.setBoolean(4, activo);
            pstmt.setInt(5, estudianteSeleccionado.getId());

            pstmt.executeUpdate();
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
            mostrarAlerta("Atención", "Primero debes seleccionar un estudiante de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        String query = "DELETE FROM estudiantes WHERE id = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, estudianteSeleccionado.getId());
            pstmt.executeUpdate();

            mostrarAlerta("Éxito", "Estudiante eliminado.", Alert.AlertType.INFORMATION);

            cargarDatosTabla();
            onLimpiar();
            estudianteSeleccionado = null;

        } catch (SQLException e) {
            mostrarAlerta("Error BD", "Error al eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void onLimpiar(){
        txtCedula.clear();
        txtNombre.clear();
        cmbCarrera.setValue(null);
        chkActivo.setSelected(false);
    }

    @FXML
    void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}