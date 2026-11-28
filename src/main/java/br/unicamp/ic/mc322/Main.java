package br.unicamp.ic.mc322;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        carregarFontes();

        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("/br/unicamp/ic/mc322/view/start_view.fxml"))
        );

        Scene scene = new Scene(root, 600, 560);
        primaryStage.setTitle("Batuta");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void carregarFontes() {
        String basePath = "/br/unicamp/ic/mc322/view/fonts/";
        String[] fontes = {
                "BryndanWrite-Regular.ttf",
                "Nunito-Regular.ttf",
                "Nunito-Light.ttf",
                "Nunito-Medium.ttf",
                "Nunito-Bold.ttf"
        };
        for (String fonte : fontes) {
            var stream = getClass().getResourceAsStream(basePath + fonte);
            if (stream != null) {
                Font.loadFont(stream, 14);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}