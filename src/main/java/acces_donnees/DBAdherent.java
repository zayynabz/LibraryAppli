package acces_donnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modele.Adherent;

public class DBAdherent {

    public ObservableList<Adherent> listerAdherents() {
        ObservableList<Adherent> liste = FXCollections.observableArrayList();
        String sql = "SELECT * FROM adherent ORDER BY id_adherent";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return liste;
            }

            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    Adherent adherent = new Adherent(
                        rs.getInt("id_adherent"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("telephone"),
                        rs.getString("adresse")
                    );
                    liste.add(adherent);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur liste adherents : " + e.getMessage());
        }
        return liste;
    }

    public boolean ajouterAdherent(String nom, String prenom, String email, String telephone, String adresse) {
        String sql = "INSERT INTO adherent (nom, prenom, email, telephone, adresse) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, email);
                pst.setString(4, telephone);
                pst.setString(5, adresse);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur ajout adherent : " + e.getMessage());
            return false;
        }
    }

    public boolean modifierAdherent(int idAdherent, String nom, String prenom, String email, String telephone, String adresse) {
        String sql = "UPDATE adherent SET nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ? WHERE id_adherent = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, nom);
                pst.setString(2, prenom);
                pst.setString(3, email);
                pst.setString(4, telephone);
                pst.setString(5, adresse);
                pst.setInt(6, idAdherent);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur modification adherent : " + e.getMessage());
            return false;
        }
    }

    public boolean supprimerAdherent(int idAdherent) {
        String sql = "DELETE FROM adherent WHERE id_adherent = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, idAdherent);
                return pst.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Erreur suppression adherent : " + e.getMessage());
            return false;
        }
    }
    //empeche la suppression d'un adherent qui a encore au moins un emprunt en cours
    public boolean adherentAEmpruntEnCours(int idAdherent) {
        String sql = "SELECT COUNT(*) AS total FROM emprunt WHERE id_adherent = ? AND statut = 'En cours'";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, idAdherent);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur verification emprunt adherent : " + e.getMessage());
        }
        return false;
    }
}
