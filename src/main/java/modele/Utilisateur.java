package modele;

public class Utilisateur {
    private int id_user;
    private String login;
    private String mot_de_passe;

    public Utilisateur(int id_user, String login, String mot_de_passe) {
        this.id_user = id_user;
        this.login = login;
        this.mot_de_passe = mot_de_passe;
    }

    public int getIdUser() {return id_user;}
    public void setIdUser(int id_user) {this.id_user = id_user;}

    public String getLogin() {return login;}
    public void setLogin(String login) {this.login = login;}

    public String getMotDePasse() {return mot_de_passe;}
    public void setMotDePasse(String mot_de_passe) {this.mot_de_passe = mot_de_passe;}
}