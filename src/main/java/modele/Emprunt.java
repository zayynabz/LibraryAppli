package modele;

import java.sql.Date;

public class Emprunt {
    private int id_emprunt;
    private int id_livre;
    private int id_adherent;
    private Date date_emprunt;
    private Date date_retour_prevue;
    private Date date_retour;
    private String statut;

    //on n'affiche pas les id dans les tableaux, on affiche les titres et les noms à la place
    private String titreLivre;
    private String nomAdherent;
    
    //constructeur de base
    public Emprunt(int id_emprunt, int id_livre, int id_adherent, Date date_emprunt,
                   Date date_retour_prevue, Date date_retour, String statut) {
        this.id_emprunt = id_emprunt;
        this.id_livre = id_livre;
        this.id_adherent = id_adherent;
        this.date_emprunt = date_emprunt;
        this.date_retour_prevue = date_retour_prevue;
        this.date_retour = date_retour;
        this.statut = statut;
    }

    //constructeur pour afficher les emprunts dans les tableaux mais sans les id, avec les titres et les noms à la place
    public Emprunt(int id_emprunt, String titreLivre, String nomAdherent, Date date_emprunt,
                   Date date_retour_prevue, String statut) {
        this.id_emprunt = id_emprunt;
        this.titreLivre = titreLivre;
        this.nomAdherent = nomAdherent;
        this.date_emprunt = date_emprunt;
        this.date_retour_prevue = date_retour_prevue;
        this.statut = statut;
    }

    public int getIdEmprunt() {return id_emprunt;}
    public void setIdEmprunt(int id_emprunt) {this.id_emprunt = id_emprunt;}

    public int getIdLivre() {return id_livre;}
    public void setIdLivre(int id_livre) {this.id_livre = id_livre;}

    public int getIdAdherent() {return id_adherent;}
    public void setIdAdherent(int id_adherent) {this.id_adherent = id_adherent;}

    public Date getDateEmprunt() {return date_emprunt;}
    public void setDateEmprunt(Date date_emprunt) {this.date_emprunt = date_emprunt;}

    public Date getDateRetourPrevue() {return date_retour_prevue;}
    public void setDateRetourPrevue(Date date_retour_prevue) {this.date_retour_prevue = date_retour_prevue;}

    public Date getDateRetour() {return date_retour;}
    public void setDateRetour(Date date_retour) {this.date_retour = date_retour;}

    public String getStatut() {return statut;}
    public void setStatut(String statut) {this.statut = statut;}

    public String getTitreLivre() {return titreLivre;}
    public void setTitreLivre(String titreLivre) {this.titreLivre = titreLivre;}

    public String getNomAdherent() {return nomAdherent;}
    public void setNomAdherent(String nomAdherent) {this.nomAdherent = nomAdherent;}
}