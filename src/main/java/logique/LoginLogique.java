package logique;

import acces_donnees.DBUtilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginLogique {

    @FXML private TextField txtUtilisateur;
    @FXML private PasswordField txtMotDePasse;
    @FXML private Label lblMessage;

    private final DBUtilisateur dbUtilisateur = new DBUtilisateur();

    @FXML
    public void handleLogin(ActionEvent event) {
        String login = txtUtilisateur.getText().trim();
        String motDePasse = txtMotDePasse.getText().trim();

        if (login.isEmpty() || motDePasse.isEmpty()) {
            lblMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        boolean autorise = dbUtilisateur.authentifier(login, motDePasse);

        if (!autorise) {
            lblMessage.setText("Identifiants incorrects.");
            return;
        }

        try {
            DashboardLogique.utilisateurConnecte = login;

            Stage ancienneFenetre = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ancienneFenetre.close();

            Parent root = FXMLLoader.load(getClass().getResource("/interface_graphique/Dashboard.fxml"));
            Stage nouvelleFenetre = new Stage();
            nouvelleFenetre.setTitle("Tableau de bord - Bibliotheque");
            nouvelleFenetre.setScene(new Scene(root));
            nouvelleFenetre.setMaximized(true);
            nouvelleFenetre.show();
        } catch (Exception e) {
            lblMessage.setText("Erreur pendant l'ouverture du tableau de bord.");
            e.printStackTrace();
        }
    }
}
