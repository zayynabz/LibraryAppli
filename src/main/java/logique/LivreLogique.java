package logique;

import java.util.Optional;

import acces_donnees.DBLivre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import modele.Livre;

public class LivreLogique {

    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, Integer> colId;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, Integer> colQuantite;

    @FXML private TextField txtTitre;
    @FXML private TextField txtAuteur;
    @FXML private TextField txtCategorie;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtQuantite;
    @FXML private TextField txtRecherche;

    @FXML private Label lblMessage;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnEmprunter;
    @FXML private Button btnAnnuler;

    private final DBLivre dbLivre = new DBLivre();
    private final ObservableList<Livre> listeLivres = FXCollections.observableArrayList();
    private Livre livreSelectionne;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idLivre"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colAuteur.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));

        FilteredList<Livre> listeFiltree = new FilteredList<>(listeLivres, livre -> true);
        SortedList<Livre> listeTriee = new SortedList<>(listeFiltree);
        listeTriee.comparatorProperty().bind(tableLivres.comparatorProperty());
        tableLivres.setItems(listeTriee);

        txtRecherche.textProperty().addListener((observation, ancienneValeur, nouvelleValeur) -> {
            String motCle = nouvelleValeur == null ? "" : nouvelleValeur.toLowerCase().trim();

            listeFiltree.setPredicate(livre -> {
                if (motCle.isEmpty()) {
                    return true;
                }
                return livre.getTitre().toLowerCase().contains(motCle)
                    || livre.getAuteur().toLowerCase().contains(motCle)
                    || livre.getCategorie().toLowerCase().contains(motCle);
            });
        });

        tableLivres.getSelectionModel().selectedItemProperty().addListener((observation, ancienneSelection, nouvelleSelection) -> {
            livreSelectionne = nouvelleSelection;

            if (nouvelleSelection == null) {
                viderChamps();
                gererBoutons(false);
            } else {
                txtTitre.setText(nouvelleSelection.getTitre());
                txtAuteur.setText(nouvelleSelection.getAuteur());
                txtCategorie.setText(nouvelleSelection.getCategorie());
                txtAnnee.setText(String.valueOf(nouvelleSelection.getAnneePublication()));
                txtQuantite.setText(String.valueOf(nouvelleSelection.getQuantite()));
                gererBoutons(true);
            }
        });

        gererBoutons(false);
        actualiserTableau();
    }

    private void actualiserTableau() {
        listeLivres.setAll(dbLivre.listerLivres());
    }

    private void gererBoutons(boolean actif) {
        btnModifier.setVisible(actif);
        btnSupprimer.setVisible(actif);
        btnEmprunter.setVisible(actif);
        btnAnnuler.setVisible(actif);
    }

    private void viderChamps() {
        txtTitre.clear();
        txtAuteur.clear();
        txtCategorie.clear();
        txtAnnee.clear();
        txtQuantite.clear();
    }

    private void afficherMessage(String message, boolean succes) {
        if (succes) {
            lblMessage.setStyle("-fx-text-fill: green;");
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
        }
        lblMessage.setText(message);
    }

    private int lireNombre(String texte) {
        return Integer.parseInt(texte.trim());
    }

    private boolean champsLivreValides() {
        String titre = txtTitre.getText().trim();
        String auteur = txtAuteur.getText().trim();

        if (titre.isEmpty() || auteur.isEmpty()) {
            afficherMessage("Le titre et l'auteur sont obligatoires.", false);
            return false;
        }

        try {
            int annee = lireNombre(txtAnnee.getText());
            int quantite = lireNombre(txtQuantite.getText());

            if (annee <= 0) {
                afficherMessage("L'annee doit etre positive.", false);
                return false;
            }

            if (quantite < 0) {
                afficherMessage("La quantite ne peut pas etre negative.", false);
                return false;
            }
        } catch (Exception e) {
            afficherMessage("L'annee et la quantite doivent etre des nombres.", false);
            return false;
        }
        return true;
    }

    @FXML
    public void handleAjouter() {
        if (!champsLivreValides()) {
            return;
        }

        boolean ajoute = dbLivre.ajouterLivre(
            txtTitre.getText().trim(),
            txtAuteur.getText().trim(),
            txtCategorie.getText().trim(),
            lireNombre(txtAnnee.getText()),
            lireNombre(txtQuantite.getText())
        );

        if (ajoute) {
            tableLivres.getSelectionModel().clearSelection();
            viderChamps();
            actualiserTableau();
            afficherMessage("Livre ajoute avec succes.", true);
        } else {
            afficherMessage("Impossible d'ajouter le livre.", false);
        }
    }

    @FXML
    public void handleModifier() {
        if (livreSelectionne == null) {
            afficherMessage("Selectionnez d'abord un livre.", false);
            return;
        }
        if (!champsLivreValides()) {
            return;
        }

        boolean modifie = dbLivre.modifierLivre(
            livreSelectionne.getIdLivre(),
            txtTitre.getText().trim(),
            txtAuteur.getText().trim(),
            txtCategorie.getText().trim(),
            lireNombre(txtAnnee.getText()),
            lireNombre(txtQuantite.getText())
        );

        if (modifie) {
            tableLivres.getSelectionModel().clearSelection();
            viderChamps();
            actualiserTableau();
            afficherMessage("Livre modifie avec succes.", true);
        } else {
            afficherMessage("Impossible de modifier le livre.", false);
        }
    }

    @FXML
    public void handleSupprimer() {
        if (livreSelectionne == null) {
            afficherMessage("Selectionnez d'abord un livre.", false);
            return;
        }

        if (dbLivre.livreAEmpruntEnCours(livreSelectionne.getIdLivre())) {
            afficherMessage("Impossible de supprimer ce livre : un emprunt est en cours.", false);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer ce livre ?");
        alert.setContentText(livreSelectionne.getTitre() + " - " + livreSelectionne.getAuteur());

        Optional<ButtonType> resultat = alert.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            boolean supprime = dbLivre.supprimerLivre(livreSelectionne.getIdLivre());

            if (supprime) {
                tableLivres.getSelectionModel().clearSelection();
                viderChamps();
                actualiserTableau();
                afficherMessage("Livre supprime avec succes.", true);
            } else {
                afficherMessage("Impossible de supprimer le livre.", false);
            }
        }
    }

    @FXML
    public void handleAnnuler() {
        tableLivres.getSelectionModel().clearSelection();
        livreSelectionne = null;
        viderChamps();
        gererBoutons(false);
        afficherMessage("Formulaire reinitialise.", true);
    }

    @FXML
    public void handleVersEmprunt(ActionEvent event) {
        if (livreSelectionne == null) {
            afficherMessage("Selectionnez d'abord un livre.", false);
            return;
        }

        EmpruntLogique.idLivreTransfert = livreSelectionne.getIdLivre();

        try {
            BorderPane root = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
            Parent vueEmprunts = FXMLLoader.load(getClass().getResource("/interface_graphique/EmpruntView.fxml"));
            root.setCenter(vueEmprunts);
        } catch (Exception e) {
            afficherMessage("Impossible d'ouvrir la page emprunts.", false);
        }
    }
}
