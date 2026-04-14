package logique;

import acces_donnees.DBEmprunt;
import modele.Emprunt;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Date;
import java.time.LocalDate;

public class RetourLogique {

    @FXML private TableView<Emprunt> tableRetours;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colAdherent;
    @FXML private TableColumn<Emprunt, Date> colDateEmprunt;
    @FXML private TableColumn<Emprunt, Date> colDateRetour;
    @FXML private TableColumn<Emprunt, String> colStatut;
    @FXML private Button btnValiderRetour;
    @FXML private Button btnActualiser;
    @FXML private Label lblMessage;

    private DBEmprunt dbEmprunt = new DBEmprunt();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmprunt"));
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colAdherent.setCellValueFactory(new PropertyValueFactory<>("nomAdherent"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        chargerDonnees();

        btnValiderRetour.setVisible(false);

        tableRetours.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnValiderRetour.setVisible(newValue != null);
        });

        tableRetours.setRowFactory(table -> new TableRow<Emprunt>() {
            @Override
            protected void updateItem(Emprunt emprunt, boolean empty) {
                super.updateItem(emprunt, empty);

                if (empty || emprunt == null) {
                    setStyle("");
                } else if (emprunt.getDateRetourPrevue() != null
                        && emprunt.getDateRetourPrevue().toLocalDate().isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffe5e5;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void chargerDonnees() {
        tableRetours.setItems(dbEmprunt.listerEmpruntsEnCours());
    }

    private void afficherMessage(String message, boolean succes) {
        if (succes) {
            lblMessage.setStyle("-fx-text-fill: green;");
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
        }
        lblMessage.setText(message);
    }

    @FXML
    public void handleRetour() {
        Emprunt selection = tableRetours.getSelectionModel().getSelectedItem();

        if (selection == null) {
            afficherMessage("Veuillez sélectionner un emprunt.", false);
            return;
        }

        boolean retour = dbEmprunt.rendreLivre(selection.getIdEmprunt());

        if (retour) {
            chargerDonnees();
            tableRetours.getSelectionModel().clearSelection();
            btnValiderRetour.setVisible(false);
            afficherMessage("Retour enregistré avec succès.", true);
        } else {
            afficherMessage("Erreur lors du retour.", false);
        }
    }

    @FXML
    public void handleActualiser() {
        chargerDonnees();
        tableRetours.getSelectionModel().clearSelection();
        btnValiderRetour.setVisible(false);
        lblMessage.setText("");
    }
}