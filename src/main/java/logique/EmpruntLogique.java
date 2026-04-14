package logique;

import java.sql.Date;
import java.time.LocalDate;

import acces_donnees.DBAdherent;
import acces_donnees.DBEmprunt;
import acces_donnees.DBLivre;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import modele.Adherent;
import modele.Emprunt;
import modele.Livre;

public class EmpruntLogique {

    public static Integer idLivreTransfert = null;//permet de transferer l'id du livre selectionne dans la page livre vers la page emprunt

    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colLivre;
    @FXML private TableColumn<Emprunt, String> colAdherent;
    @FXML private TableColumn<Emprunt, Date> colDateEmprunt;
    @FXML private TableColumn<Emprunt, Date> colDateRetour;
    @FXML private TableColumn<Emprunt, String> colStatut;

    @FXML private ComboBox<Livre> comboLivre;
    @FXML private ComboBox<Adherent> comboAdherent;
    @FXML private DatePicker datePrevue;
    @FXML private Label lblMessage;
    @FXML private Button btnRetour;

    private final DBEmprunt dbEmprunt = new DBEmprunt();
    private final DBLivre dbLivre = new DBLivre();
    private final DBAdherent dbAdherent = new DBAdherent();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idEmprunt"));
        colLivre.setCellValueFactory(new PropertyValueFactory<>("titreLivre"));
        colAdherent.setCellValueFactory(new PropertyValueFactory<>("nomAdherent"));
        colDateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        colDateRetour.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        configurerListes();
        actualiserPage();

        if (idLivreTransfert != null) {
            for (Livre livre : comboLivre.getItems()) {//parcourir la liste des livres pour trouver celui qui correspond à l'id transféré
                if (livre.getIdLivre() == idLivreTransfert) {
                    comboLivre.setValue(livre);
                    break;
                }
            }
            idLivreTransfert = null;
        }
        btnRetour.setVisible(false);

        tableEmprunts.getSelectionModel().selectedItemProperty().addListener((observation, ancienneSelection, nouvelleSelection) -> {
            boolean afficher = nouvelleSelection != null && !"Rendu".equals(nouvelleSelection.getStatut());
            btnRetour.setVisible(afficher);
        });

        tableEmprunts.setRowFactory(table -> new TableRow<Emprunt>() {
            @Override
            protected void updateItem(Emprunt emprunt, boolean empty) {
                super.updateItem(emprunt, empty);

                if (empty || emprunt == null) {
                    setStyle("");
                    return;
                }

                if ("Rendu".equals(emprunt.getStatut())) {
                    setStyle("-fx-background-color: #e8f8f5;");
                } else if (emprunt.getDateRetourPrevue() != null
                        && emprunt.getDateRetourPrevue().toLocalDate().isBefore(LocalDate.now())) {
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void configurerListes() {
        comboLivre.setConverter(new StringConverter<Livre>() {
            @Override
            public String toString(Livre livre) {
                if (livre == null) {
                    return "";
                }
                return livre.getTitre() + " (stock : " + livre.getQuantite() + ")";
            }

            @Override
            public Livre fromString(String texte) {
                return null; //affiche le titre du livre et sa quantité disponible dans le combo box, mais ne convertit pas de texte en objet Livre
            }
        });

        comboAdherent.setConverter(new StringConverter<Adherent>() {
            @Override
            public String toString(Adherent adherent) {
                if (adherent == null) {
                    return "";
                }
                return adherent.getNom() + " " + adherent.getPrenom();
            }

            @Override
            public Adherent fromString(String texte) {
                return null;
            }
        });
    }

    private void actualiserPage() {
        ObservableList<Emprunt> listeEmprunts = dbEmprunt.listerEmprunts();
        tableEmprunts.setItems(listeEmprunts);
        comboLivre.setItems(dbLivre.listerLivres());
        comboAdherent.setItems(dbAdherent.listerAdherents());
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
    public void handleAjouter() {
        Livre livreChoisi = comboLivre.getValue();
        Adherent adherentChoisi = comboAdherent.getValue();
        LocalDate dateChoisie = datePrevue.getValue();

        if (livreChoisi == null || adherentChoisi == null || dateChoisie == null) {
            afficherMessage("Selectionnez un livre, un adherent et une date.", false);
            return;
        }

        if (dateChoisie.isBefore(LocalDate.now())) {
            afficherMessage("La date de retour ne doit pas etre dans le passe.", false);
            return;
        }

        int stock = dbEmprunt.verifierStock(livreChoisi.getIdLivre());
        if (stock <= 0) {
            afficherMessage("Ce livre n'est plus disponible.", false);
            return;
        }

        boolean ajoute = dbEmprunt.ajouterEmprunt(
            livreChoisi.getIdLivre(),
            adherentChoisi.getIdAdherent(),
            Date.valueOf(dateChoisie)//convertit la date choisie en objet Date pour l'enregistrer dans la base de données
        );

        if (ajoute) {
            comboLivre.setValue(null);
            comboAdherent.setValue(null);
            datePrevue.setValue(null);
            actualiserPage();
            afficherMessage("Emprunt enregistre avec succes.", true);
        } else {
            afficherMessage("Impossible d'enregistrer l'emprunt.", false);
        }
    }

    @FXML
    public void handleRetour() {
        Emprunt empruntSelectionne = tableEmprunts.getSelectionModel().getSelectedItem();

        if (empruntSelectionne == null) {
            afficherMessage("Selectionnez d'abord un emprunt.", false);
            return;
        }

        if ("Rendu".equals(empruntSelectionne.getStatut())) {
            afficherMessage("Ce livre est deja rendu.", false);
            return;
        }

        boolean rendu = dbEmprunt.rendreLivre(empruntSelectionne.getIdEmprunt());

        if (rendu) {
            tableEmprunts.getSelectionModel().clearSelection();
            actualiserPage();
            btnRetour.setVisible(false);
            afficherMessage("Retour enregistre avec succes.", true);
        } else {
            afficherMessage("Impossible d'enregistrer le retour.", false);
        }
    }
}
