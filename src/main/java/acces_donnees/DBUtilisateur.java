package acces_donnees;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBUtilisateur {

    public boolean authentifier(String login, String motDePasse) {
        String sql = "SELECT COUNT(*) AS total FROM utilisateur WHERE login = ? AND mot_de_passe = ?";

        try {
            Connection con = DBConnection.MaCon();
            if (con == null) {
                return false;
            }

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, login);
                pst.setString(2, motDePasse);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total") > 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur authentification : " + e.getMessage());
        }
        return false;
    }
}
