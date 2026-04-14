package acces_donnees;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class DBConnection {

    private static final String DB_FILE_NAME = "bibliotheque.accdb";
    private static final String DB_ENV_VAR = "LIBRARY_DB_PATH";

    private static Connection con = null;

    public static Connection MaCon() {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

            if (con == null || con.isClosed()) {
                Path dbPath = trouverBaseDeDonnees();
                String url = "jdbc:ucanaccess://" + dbPath.toAbsolutePath().normalize();
                con = DriverManager.getConnection(url);
                System.out.println("Connexion a la base etablie : " + dbPath.toAbsolutePath().normalize());
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
        return con;
    }

    private static Path trouverBaseDeDonnees() {
        String cheminEnv = System.getenv(DB_ENV_VAR);
        if (cheminEnv != null && !cheminEnv.isBlank()) {
            Path cheminPersonnalise = Paths.get(cheminEnv).toAbsolutePath().normalize();
            if (Files.exists(cheminPersonnalise)) {
                return cheminPersonnalise;
            }
        }

        List<Path> cheminsPossibles = List.of(
            Paths.get(DB_FILE_NAME),
            Paths.get(".", DB_FILE_NAME),
            Paths.get("..", DB_FILE_NAME),
            Paths.get("LibraryApp2", DB_FILE_NAME),
            Paths.get("LibraryApp2_github", DB_FILE_NAME)
        );

        for (Path chemin : cheminsPossibles) {
            Path absolu = chemin.toAbsolutePath().normalize();
            if (Files.exists(absolu)) {
                return absolu;
            }
        }

        throw new IllegalStateException(
            "Base de donnees introuvable. Placez '" + DB_FILE_NAME + "' a la racine du projet "
            + "ou definissez la variable d'environnement " + DB_ENV_VAR + "."
        );
    }

    public static void fermerConnexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Connexion fermee.");
            }
        } catch (Exception e) {
            System.out.println("Erreur fermeture connexion : " + e.getMessage());
        }
    }
}
