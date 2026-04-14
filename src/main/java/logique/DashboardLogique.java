package logique;

import acces_donnees.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardLogique {

    public static String utilisateurConnecte;

    @FXML private BorderPane mainPane;
    @FXML private Label lblBienvenue;
    @FXML private Label lblStatLivres;
    @FXML private Label lblStatAdherents;
    @FXML private Label lblStatEmprunts;
    
    @FXML
    public void initialize() {
        if (utilisateurConnecte != null) {
            lblBienvenue.setText("Bienvenue, " + utilisateurConnecte);
        }
        chargerStatistiques();
    }

    private void chargerStatistiques() {
        try {
            Connection con = DBConnection.MaCon();
            Statement st = con.createStatement();

            ResultSet rsLivres = st.executeQuery("SELECT SUM(quantite) FROM livre");
            if (rsLivres.next()) {
                lblStatLivres.setText(String.valueOf(rsLivres.getInt(1)));
            }

            ResultSet rsAdherents = st.executeQuery("SELECT COUNT(*) FROM adherent");
            if (rsAdherents.next()) {
                lblStatAdherents.setText(String.valueOf(rsAdherents.getInt(1)));
            }

            ResultSet rsEmprunts = st.executeQuery("SELECT COUNT(*) FROM emprunt WHERE statut = 'En cours'");
            if (rsEmprunts.next()) {
                lblStatEmprunts.setText(String.valueOf(rsEmprunts.getInt(1)));
            }

        } catch (Exception e) {
            System.out.println("Erreur chargement statistiques : " + e.getMessage());
        }
    }

    @FXML
    public void afficherLivres(ActionEvent event) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource("/interface_graphique/LivreView.fxml"));
            mainPane.setCenter(vue);
        } catch (Exception e) {
            System.out.println("Erreur ouverture livres : " + e.getMessage());
        }
    }

    @FXML
    public void afficherAdherents(ActionEvent event) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource("/interface_graphique/AdherentView.fxml"));
            mainPane.setCenter(vue);
        } catch (Exception e) {
            System.out.println("Erreur ouverture adherents : " + e.getMessage());
        }
    }

    @FXML
    public void afficherEmprunts(ActionEvent event) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource("/interface_graphique/EmpruntView.fxml"));
            mainPane.setCenter(vue);
        } catch (Exception e) {
            System.out.println("Erreur ouverture emprunts : " + e.getMessage());
        }
    }

    @FXML
    public void afficherRetours(ActionEvent event) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource("/interface_graphique/RetourView.fxml"));
            mainPane.setCenter(vue);
        } catch (Exception e) {
            System.out.println("Erreur ouverture retour : " + e.getMessage());
        }
    }

    @FXML
    public void handleDeconnexion(ActionEvent event) {
        try {
            utilisateurConnecte = null;

            Stage stageActuel = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageActuel.close();

            Parent root = FXMLLoader.load(getClass().getResource("/interface_graphique/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("Authentification");
            loginStage.setScene(new Scene(root));
            loginStage.show();

        } catch (Exception e) {
            System.out.println("Erreur deconnexion : " + e.getMessage());
        }
    }
}