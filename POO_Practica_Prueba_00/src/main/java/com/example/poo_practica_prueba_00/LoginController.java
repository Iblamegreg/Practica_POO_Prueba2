package com.example.poo_practica_prueba_00;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;


public class LoginController {
    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPassword;

    @FXML
    void onIngresar(){
        String user = txtUser.getText().trim();
        String pass = txtPassword.getText().trim();
        if(user.isEmpty() || pass.isEmpty()){
            mostrarAlerta("Campos Vacios", "Por favor llena todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        if (user.equals("admin") && pass.equals("1234")){
            mostrarAlerta("Exito", "Bienvenido al sistema!", Alert.AlertType.INFORMATION);
            abrirCRUD();
        }else{
            mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }

    }

    @FXML
    void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    void abrirCRUD(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("CrudVista.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Gestion de Estudiantes - CRUD");
            stage.setScene(scene);
            stage.show();

            Stage stageLogin = (Stage) txtUser.getScene().getWindow();
            stageLogin.close();
        }catch (IOException e){
            System.out.println("Error al abrir el registro: " + e.getMessage());
        }
    }
}
