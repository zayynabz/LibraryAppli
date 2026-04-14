package acces_donnees;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import modele.Emprunt;

public class DBEmprunt {

    public ObservableList<Emprunt> listerEmprunts() {
        ObservableList<Emprunt> liste = FXCollections.observableArrayList();

        String sql = "SELECT e.id_emprunt, e.id_livre, e.id_adherent, e.date_emprunt, e.date_retour_prevue, e.date_retour, e.statut, "
                   + "l.titre, a.nom, a.prenom "
                   + "FROM (emprunt e "
                   + "INNER JOIN livre l ON e.id_livre = l.id_livre) "
                   + "INNER JOIN adherent a ON e.id_adherent = a.id_adherent "
                   + "ORDER BY e.id_emprunt DESC";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return liste;
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    Emprunt emprunt = new Emprunt(
                        rs.getInt("id_emprunt"),
                        rs.getInt("id_livre"),
                        rs.getInt("id_adherent"),
                        rs.getDate("date_emprunt"),
                        rs.getDate("date_retour_prevue"),
                        rs.getDate("date_retour"),
                        rs.getString("statut")
                    );
                    //infos supplementaires pour affichage
                    emprunt.setTitreLivre(rs.getString("titre"));
                    emprunt.setNomAdherent(rs.getString("nom") + " " + rs.getString("prenom"));

                    liste.add(emprunt);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur liste emprunts : " + e.getMessage());
        }
        return liste;
    }

    public ObservableList<Emprunt> listerEmpruntsEnCours() {
        ObservableList<Emprunt> liste = FXCollections.observableArrayList();

        String sql = "SELECT e.id_emprunt, e.id_livre, e.id_adherent, e.date_emprunt, e.date_retour_prevue, e.date_retour, e.statut, "
                   + "l.titre, a.nom, a.prenom "
                   + "FROM (emprunt e INNER JOIN livre l ON e.id_livre = l.id_livre) "
                   + "INNER JOIN adherent a ON e.id_adherent = a.id_adherent "
                   + "WHERE e.statut = 'En cours' "
                   + "ORDER BY e.date_retour_prevue ASC";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return liste;
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    Emprunt emprunt = new Emprunt(
                        rs.getInt("id_emprunt"),
                        rs.getInt("id_livre"),
                        rs.getInt("id_adherent"),
                        rs.getDate("date_emprunt"),
                        rs.getDate("date_retour_prevue"),
                        rs.getDate("date_retour"),
                        rs.getString("statut")
                    );

                    emprunt.setTitreLivre(rs.getString("titre"));
                    emprunt.setNomAdherent(rs.getString("nom") + " " + rs.getString("prenom"));

                    liste.add(emprunt);
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur liste retours : " + e.getMessage());
        }
        return liste;
    }

    public boolean ajouterEmprunt(int idLivre, int idAdherent, Date dateRetour) {
        String sqlEmprunt = "INSERT INTO emprunt (id_livre, id_adherent, date_emprunt, date_retour_prevue, statut) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE livre SET quantite = quantite - 1 WHERE id_livre = ?"; //diminuer stock du livre qd on l'emprunte

        Connection con = DBConnection.MaCon();
        if (con == null) {
            return false;
        }

        try {
            if (verifierStock(idLivre) <= 0) {
                return false;
            }

            con.setAutoCommit(false); //pour s'assurer que les 2 operations (inserer emprunt + diminuer stock) soient traitées ensemble

            try (PreparedStatement pstEmprunt = con.prepareStatement(sqlEmprunt);
                 PreparedStatement pstStock = con.prepareStatement(sqlStock)) {

                pstEmprunt.setInt(1, idLivre);
                pstEmprunt.setInt(2, idAdherent);
                pstEmprunt.setDate(3, new Date(System.currentTimeMillis())); //date emprunt = date actuelle
                pstEmprunt.setDate(4, dateRetour);
                pstEmprunt.setString(5, "En cours");
                pstEmprunt.executeUpdate();

                pstStock.setInt(1, idLivre);
                pstStock.executeUpdate();
            }
            con.commit(); //si tout s'est bien passé, on valide les changements
            return true;
        } catch (Exception e) {
            try {
                con.rollback(); //en cas d'erreur, on annule les changements pour éviter d'avoir un emprunt sans mise à jour du stock ou inversement
            } catch (Exception ex) {
                System.out.println("Erreur rollback emprunt : " + ex.getMessage());
            }

            System.out.println("Erreur ajout emprunt : " + e.getMessage());
            return false;

        } finally {
            try {
                con.setAutoCommit(true); //remettre autoCommit à true pour les autres opérations
            } catch (Exception e) {
                System.out.println("Erreur remise autoCommit : " + e.getMessage());
            }
        }
    }

    public boolean rendreLivre(int idEmprunt) {
        String sqlTrouverLivre = "SELECT id_livre FROM emprunt WHERE id_emprunt = ? AND statut = 'En cours'";
        String sqlEmprunt = "UPDATE emprunt SET statut = 'Rendu', date_retour = ? WHERE id_emprunt = ?";
        String sqlStock = "UPDATE livre SET quantite = quantite + 1 WHERE id_livre = ?";

        Connection con = DBConnection.MaCon();
        if (con == null) {
            return false;
        }

        try {
            con.setAutoCommit(false);

            int idLivre = -1; //on connait le livre par l'emprunt pas par son id, donc on doit d'abord le trouver pour pouvoir mettre à jour son stock

            try (PreparedStatement pstTrouver = con.prepareStatement(sqlTrouverLivre)) {
                pstTrouver.setInt(1, idEmprunt);

                try (ResultSet rs = pstTrouver.executeQuery()) {
                    if (rs.next()) {
                        idLivre = rs.getInt("id_livre");
                    }
                }
            }

            if (idLivre == -1) {
                con.rollback();
                return false;
            }

            try (PreparedStatement pstEmprunt = con.prepareStatement(sqlEmprunt);
                 PreparedStatement pstStock = con.prepareStatement(sqlStock)) {

                pstEmprunt.setDate(1, new Date(System.currentTimeMillis()));
                pstEmprunt.setInt(2, idEmprunt);
                pstEmprunt.executeUpdate();

                pstStock.setInt(1, idLivre);
                pstStock.executeUpdate();
            }

            con.commit();
            return true;

        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
                System.out.println("Erreur rollback retour : " + ex.getMessage());
            }

            System.out.println("Erreur retour livre : " + e.getMessage());
            return false;

        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Erreur remise autoCommit : " + e.getMessage());
            }
        }
    }

    public int verifierStock(int idLivre) {
        String sql = "SELECT quantite FROM livre WHERE id_livre = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return 0;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, idLivre);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("quantite");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Erreur verification stock : " + e.getMessage());
        }
        return 0;
    }
}