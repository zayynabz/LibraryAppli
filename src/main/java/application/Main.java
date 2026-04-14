package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("1. Lancement de l'application...");
            
            // On vérifie si Java trouve bien ton fichier FXML
            URL fxmlLocation = getClass().getResource("/interface_graphique/Login.fxml");
            
            if (fxmlLocation == null) {
                System.err.println("ERREUR FATALE : Java ne trouve pas le fichier Login.fxml !");
                System.err.println("Le fichier n'est pas dans le bon dossier ou le dossier resources n'est pas configuré.");
                return; // On arrête le programme
            }
            
            System.out.println("2. Fichier FXML trouvé ! Chargement...");
            Parent root = FXMLLoader.load(fxmlLocation);
            
            System.out.println("3. Affichage de la fenêtre...");
            stage.setTitle("Authentification");
            stage.setWidth(400);
            stage.setHeight(300);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite lors du chargement :");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}