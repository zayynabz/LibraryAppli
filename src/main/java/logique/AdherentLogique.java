package logique;

import java.util.Optional;

import acces_donnees.DBAdherent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import modele.Adherent;

public class AdherentLogique {

    @FXML private TableView<Adherent> tableAdherents;
    @FXML private TableColumn<Adherent, Integer> colId;
    @FXML private TableColumn<Adherent, String> colNom;
    @FXML private TableColumn<Adherent, String> colPrenom;
    @FXML private TableColumn<Adherent, String> colEmail;
    @FXML private TableColumn<Adherent, String> colTel;
    @FXML private TableColumn<Adherent, String> colAdresse;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTel;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtRecherche;

    @FXML private Label lblMessage;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnAnnuler;

    private final DBAdherent dbAdherent = new DBAdherent();
    private final ObservableList<Adherent> listeAdherents = FXCollections.observableArrayList();
    private Adherent adherentSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAdherent")); //getIdAdherent() dans Adherent
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        FilteredList<Adherent> listeFiltree = new FilteredList<>(listeAdherents, adherent -> true); //affiche tous les adherents au depart
        SortedList<Adherent> listeTriee = new SortedList<>(listeFiltree); 
        listeTriee.comparatorProperty().bind(tableAdherents.comparatorProperty());
        tableAdherents.setItems(listeTriee);

        txtRecherche.textProperty().addListener((observation, ancienneValeur, nouvelleValeur) -> {
            String motCle = nouvelleValeur == null ? "" : nouvelleValeur.toLowerCase().trim();

            listeFiltree.setPredicate(adherent -> {
                if (motCle.isEmpty()) { //si le champ de recherche est vide, afficher tous les adherents
                    return true;
                }
                return adherent.getNom().toLowerCase().contains(motCle)
                    || adherent.getPrenom().toLowerCase().contains(motCle)
                    || adherent.getEmail().toLowerCase().contains(motCle)
                    || adherent.getTelephone().toLowerCase().contains(motCle);
            });
        });
        //qd on selectionne un adherent dans le tableau, afficher ses details dans les champs de saisie
        tableAdherents.getSelectionModel().selectedItemProperty().addListener((observation, ancienneSelection, nouvelleSelection) -> {
            adherentSelectionne = nouvelleSelection;

            if (nouvelleSelection == null) {
                viderChamps();
                gererBoutons(false);
            } else {
                txtNom.setText(nouvelleSelection.getNom());
                txtPrenom.setText(nouvelleSelection.getPrenom());
                txtEmail.setText(nouvelleSelection.getEmail());
                txtTel.setText(nouvelleSelection.getTelephone());
                txtAdresse.setText(nouvelleSelection.getAdresse());
                gererBoutons(true);
            }
        });

        gererBoutons(false);
        actualiserTableau();
    }
    //rafraichit le tableau apres chaque operation d'ajout, modification ou suppression
    private void actualiserTableau() {
        listeAdherents.setAll(dbAdherent.listerAdherents());
    }

    private void gererBoutons(boolean actif) {
        btnModifier.setVisible(actif);
        btnSupprimer.setVisible(actif);
        btnAnnuler.setVisible(actif);
    }

    private void viderChamps() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTel.clear();
        txtAdresse.clear();
    }

    private void afficherMessage(String message, boolean succes) {
        if (succes) {
            lblMessage.setStyle("-fx-text-fill: green;");
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
        }
        lblMessage.setText(message);
    }

    private boolean champsValides() {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
            afficherMessage("Le nom et le prenom sont obligatoires.", false);
            return false;
        }
        return true;
    }

    @FXML
    public void handleAjouter() {
        if (!champsValides()) {
            return;
        }
        boolean ajoute = dbAdherent.ajouterAdherent(
            txtNom.getText().trim(),
            txtPrenom.getText().trim(),
            txtEmail.getText().trim(),
            txtTel.getText().trim(),
            txtAdresse.getText().trim()
        );

        if (ajoute) {
            tableAdherents.getSelectionModel().clearSelection();
            viderChamps();
            actualiserTableau();
            afficherMessage("Adherent ajoute avec succes.", true);
        } else {
            afficherMessage("Impossible d'ajouter l'adherent.", false);
        }
    }

    @FXML
    public void handleModifier() {
        if (adherentSelectionne == null) {
            afficherMessage("Selectionnez d'abord un adherent.", false);
            return;
        }

        if (!champsValides()) {
            return;
        }

        boolean modifie = dbAdherent.modifierAdherent(
            adherentSelectionne.getIdAdherent(),
            txtNom.getText().trim(),
            txtPrenom.getText().trim(),
            txtEmail.getText().trim(),
            txtTel.getText().trim(),
            txtAdresse.getText().trim()
        );

        if (modifie) {
            tableAdherents.getSelectionModel().clearSelection();
            viderChamps();
            actualiserTableau();
            afficherMessage("Adherent modifie avec succes.", true);
        } else {
            afficherMessage("Impossible de modifier l'adherent.", false);
        }
    }

    @FXML
    public void handleSupprimer() {
        if (adherentSelectionne == null) {
            afficherMessage("Selectionnez d'abord un adherent.", false);
            return;
        }

        if (dbAdherent.adherentAEmpruntEnCours(adherentSelectionne.getIdAdherent())) {
            afficherMessage("Impossible de supprimer cet adherent : un emprunt est en cours.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer cet adherent ?");
        alert.setContentText(adherentSelectionne.getPrenom() + " " + adherentSelectionne.getNom());

        Optional<ButtonType> resultat = alert.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            boolean supprime = dbAdherent.supprimerAdherent(adherentSelectionne.getIdAdherent());

            if (supprime) {
                tableAdherents.getSelectionModel().clearSelection();
                viderChamps();
                actualiserTableau();
                afficherMessage("Adherent supprime avec succes.", true);
            } else {
                afficherMessage("Impossible de supprimer l'adherent.", false);
            }
        }
    }

    @FXML
    public void handleAnnuler() {
        tableAdherents.getSelectionModel().clearSelection();
        adherentSelectionne = null;
        viderChamps();
        gererBoutons(false);
        afficherMessage("Formulaire reinitialise.", true);
    }
}
