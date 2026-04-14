package acces_donnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modele.Livre;

public class DBLivre {

    public ObservableList<Livre> listerLivres() {
        ObservableList<Livre> liste = FXCollections.observableArrayList();
        String sql = "SELECT * FROM livre ORDER BY id_livre";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return liste;
            }

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Livre livre = new Livre(
                        rs.getInt("id_livre"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getInt("annee_publication"),
                        rs.getInt("quantite")
                    );
                    liste.add(livre);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur liste livres : " + e.getMessage());
        }

        return liste;
    }

    public boolean ajouterLivre(String titre, String auteur, String categorie, int annee, int quantite) {
        String sql = "INSERT INTO livre (titre, auteur, categorie, annee_publication, quantite) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, titre);
                pst.setString(2, auteur);
                pst.setString(3, categorie);
                pst.setInt(4, annee);
                pst.setInt(5, quantite);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur ajout livre : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierLivre(int idLivre, String titre, String auteur, String categorie, int annee, int quantite) {
        String sql = "UPDATE livre SET titre = ?, auteur = ?, categorie = ?, annee_publication = ?, quantite = ? WHERE id_livre = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, titre);
                pst.setString(2, auteur);
                pst.setString(3, categorie);
                pst.setInt(4, annee);
                pst.setInt(5, quantite);
                pst.setInt(6, idLivre);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur modification livre : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerLivre(int idLivre) {
        String sql = "DELETE FROM livre WHERE id_livre = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, idLivre);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur suppression livre : " + e.getMessage());
            return false;
        }
    }

    public boolean livreAEmpruntEnCours(int idLivre) {
        String sql = "SELECT COUNT(*) AS total FROM emprunt WHERE id_livre = ? AND statut = 'En cours'";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, idLivre);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur verification emprunt livre : " + e.getMessage());
        }
        return false;
    }
}
