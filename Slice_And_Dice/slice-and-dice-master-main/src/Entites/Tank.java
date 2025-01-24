package Entites;


import De.Capacite.Bouclier;
import De.Capacite.CoupEpee;
import De.Capacite.Mana;
import De.Capacite.Soin;
import De.De;

import java.awt.*;

public class Tank extends Heros{

    public Tank(){
        super(15, importeDeTank(), new Color(156,156,156), "Tank", "Colosse", "Brute", "Titan", "Golem", initDeAmelioration());

    }

    protected static De importeDeTank(){ //sert à créer le dé du tank, donc mettre les attaques dans le new De(...) ; utiliser cette fonctiondans la classe Joueur lorsque l'on ajoute les héros à la listeHerosDisponibles
        return new De(new Bouclier(2), new Bouclier(1), new Bouclier(1),new CoupEpee(1),  new Bouclier(2), null);
    }

    protected static De[] initDeAmelioration(){/*La fonction suivante permet de stocker les dés correspondants aux différents stades d'amélioration du héros*/
        De[] res = new De[4];
        res[0] = new De(new Bouclier(3), new Bouclier(2), new Bouclier(2), new Soin(1), new Bouclier(3), null);
        res[1] = new De(new Bouclier(1), new Bouclier(2), new Bouclier(2), new CoupEpee(1), new CoupEpee(1), null);
        res[2] = new De(new Bouclier(3), new Bouclier(3), new Bouclier(2), new Soin(3), new Bouclier(1), new Bouclier(1));
        res[3] = new De(new Bouclier(3), new Bouclier(3), new Bouclier(2), new Bouclier(2), new CoupEpee(3), new Bouclier(3)) ;

        return res;
    }

    //Fonctions d'amélioration

    /*La fonction suivante est celle qui est appelée depuis la classe Combat lorsque le héros doit être amélioré au niveau 2*/
    public void ameliorationAuNiveau2(String ameliorationChoisie)
    {
        if(ameliorationChoisie.equals("Brute"))
        {
            this.ameliorationNiv2Brute();
        }
        else if(ameliorationChoisie.equals("Colosse"))
        {
            this.ameliorationNiv2Colosse();
        }
    }

    //Premiere proposition si le héros est au niveau 1
    public void ameliorationNiv2Brute()
    {
        this.setPdvMax(this.getPdvMax()+4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnDeux());
        this.setNomEntite("Brute");
        this.incrementationNiveauActuel();
    }
    //2ème proposition si le tank est au niveau 1
    public void ameliorationNiv2Colosse()
    {
        this.setPdvMax(this.getPdvMax() + 4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnUn());
        this.setNomEntite("Colosse");
        this.incrementationNiveauActuel();
    }


    public void ameliorationAuNiveau3()
    {
        if(this.getNomEntite().equals("Brute"))
        {
            this.ameliorationNiv3Golem();
        }
        else if(this.getNomEntite().equals("Colosse"))
        {
            this.ameliorationNiv3Titan();
        }
    }
    //Proposée si le tank  avait été amélioré en Brute
    public void ameliorationNiv3Golem()
    {
        this.setPdvMax(this.getPdvMax()+4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxDeux());
        this.setNomEntite("Golem");
        this.incrementationNiveauActuel();
    }
    //Proposée si le tank avait été amélioré en Colosse
    public void ameliorationNiv3Titan()
    {
        this.setPdvMax(this.getPdvMax()+4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxUn());
        this.setNomEntite("Titan");
        this.incrementationNiveauActuel();
    }

}
