package br.unicamp.ic.mc322.view;

import br.unicamp.ic.mc322.model.House;
import br.unicamp.ic.mc322.persistence.HousePersistence;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class StartController {

    @FXML
    private Button btnCriarNovaCasa;

    @FXML
    private Button btnCarregarSave;

    @FXML
    private Button btnSair;

    @FXML
    private void onBtnCreateNewHouse(ActionEvent event) {
        System.out.println("Abrir tela de criar nova casa...");
    }

    @FXML
    private void onBtnLoadHouseFromFile(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carregar save do Batuta");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos XML (*.xml)", "*.xml")
        );

        File arquivoSelecionado = fileChooser.showOpenDialog(stage);
        if (arquivoSelecionado == null) {
            return;
        }

        try {
            HousePersistence persistence = new HousePersistence();
            House casaCarregada = persistence.load();
            System.out.println("Casa carregada: " + casaCarregada.getName());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao carregar save");
            alert.setHeaderText("Não foi possível carregar o arquivo selecionado.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onBtnSairClick(ActionEvent event) {
        Platform.exit();
    }
}