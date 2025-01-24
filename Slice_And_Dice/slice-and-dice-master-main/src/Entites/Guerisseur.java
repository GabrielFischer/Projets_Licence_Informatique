package Entites;

import De.Capacite.CoupEpee;
import De.Capacite.Mana;
import De.Capacite.Poison;
import De.Capacite.Soin;
import De.De;

import java.awt.*;

public class Guerisseur extends Heros {
    public Guerisseur(){
        super(8, importeDeGuerisseur(), new Color(255,74,74), "Guerisseur", "Pretre", "Apothicaire", "Starseer", "Miracleur", initDeAmelioration());
    }

    protected static De importeDeGuerisseur(){ //sert à créer le dé du guérisseur, donc mettre les attaques dasn le new De(...) ; utiliser cette fonction dans la classe Joueur lorsque l'on ajoute les héros à la listeHerosDisponibles
        return new De(new Soin(1), new Soin(2), new Soin(1),new Soin(1), null, null);
    }

    protected static De[] initDeAmelioration(){/*La fonction suivante permet de stocker les dés correspondants aux différents stades d'amélioration du héros*/
        De[] res = new De[4];
        res[0] = new De(new Soin(1), new Soin(2), new Soin(2), new Soin(2), new Mana(1), null);
        res[1] = new De(new Soin(2), new Poison(2), new Soin(2), null, null, null);
        res[2] = new De(new Soin(3), new Soin(3), new Mana(2), new Mana(2), new Soin(2), new Soin(3));
        res[3] = new De(new Soin(3), new Soin(3), new Poison(3), new Poison(1), new Soin(2), new Soin(2));

        return res;
    }

    //Fonctions d'amélioration
    /*La fonction suivante est celle qui est appelée depuis la classe Combat lorsque le héros doit être amélioré au niveau 2*/

    public void ameliorationAuNiveau2(String ameliorationChoisie)
    {
        if(ameliorationChoisie.equals("Pretre"))
        {
            this.ameliorationNiv2Pretre();
        }
        else if(ameliorationChoisie.equals("Apothicaire"))
        {
            this.ameliorationNiv2Apothicaire();
        }
    }
    //Premiere proposition si le héros est au niveau 1
    public void ameliorationNiv2Pretre()
    {
        this.setPdvMax(this.getPdvMax()+3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnUn());
        this.setNomEntite("Pretre");
        this.incrementationNiveauActuel();
    }
    //2ème proposition si le guérisseur est au niveau 1
    public void ameliorationNiv2Apothicaire()
    {
        this.setPdvMax(this.getPdvMax() + 2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnDeux());
        this.setNomEntite("Apothicaire");
        this.incrementationNiveauActuel();
    }


    public void ameliorationAuNiveau3()
    {
        if(this.getNomEntite().equals("Pretre"))
        {
            this.ameliorationNiv3Starseer();
        }
        else if(this.getNomEntite().equals("Apothicaire"))
        {
            this.ameliorationNiv3Miracleur();
        }
    }
    //Proposée si le guérisseur avait été amélioré en Prêtre
    public void ameliorationNiv3Starseer()
    {
        this.setPdvMax(this.getPdvMax()+3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxUn());
        this.setNomEntite("Starseer");
        this.incrementationNiveauActuel();
    }
    //Proposée si le guérisseur avait été amélioré en Apothicaire
    public void ameliorationNiv3Miracleur()
    {
        this.setPdvMax(this.getPdvMax()+3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxDeux());
        this.setNomEntite("Miracleur");
        this.incrementationNiveauActuel();
    }

}
