package modele;

public class Livre {
    private int id_livre;
    private String titre;
    private String auteur;
    private String categorie;
    private int annee_publication;
    private int quantite;

    public Livre(int id_livre, String titre, String auteur, String categorie, int annee_publication, int quantite) {
        this.id_livre = id_livre;
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.annee_publication = annee_publication;
        this.quantite = quantite;
    }

    public int getIdLivre() {return id_livre;}
    public void setIdLivre(int id_livre) {this.id_livre = id_livre;}

    public String getTitre() {return titre;}
    public void setTitre(String titre) {this.titre = titre;}

    public String getAuteur() {return auteur;}
    public void setAuteur(String auteur) {this.auteur = auteur;}

    public String getCategorie() {return categorie;}
    public void setCategorie(String categorie) {this.categorie = categorie;}

    public int getAnneePublication() {return annee_publication;}
    public void setAnneePublication(int annee_publication) {this.annee_publication = annee_publication;}

    public int getQuantite() {return quantite;}
    public void setQuantite(int quantite) {this.quantite = quantite;}

    @Override
    public String toString() {
        return titre + " - " + auteur;
    }
}