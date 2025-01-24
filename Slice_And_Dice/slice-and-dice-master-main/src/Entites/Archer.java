package Entites;

import De.Capacite.Arc;
import De.Capacite.Bouclier;
import De.Capacite.Mana;
import De.Capacite.Poison;
import De.De;

import java.awt.*;

public class Archer extends Heros {

    public Archer() {
        super(10, importeDeArcher(), new Color(245, 138, 66), "Archer", "Ranger", "Chasseur", "Elfe", "Beastmaster", initDeAmelioration());
    }

    protected static De importeDeArcher() { // sert à créer le dé de l'archer, donc mettre les attaques dasn le new
                                            // De(...) ; utiliser cette fonctiondans la classe Joueur lorsque l'on
                                            // ajoute les héros à la listeHerosDisponibles
        De nouvDe = new De(new Arc(1), new Arc(1), new Arc(2), new Arc(2), new Arc(2), null);
        return nouvDe;
    }
    /*La fonction suivante permet de stocker les dés correspondants aux différents stades d'amélioration de l'Archer*/
    protected static De[] initDeAmelioration(){
        De[] res = new De[4];
        res[0] = new De(new Arc(3), new Arc(3), /*new Arc(2)*/ new Poison(2), new Arc(2), new Arc(2), new Arc(3));
        res[1] = new De(new Arc(3), new Arc(3), new Bouclier(1), new Arc(2), new Arc(3), new Arc(2));
        res[2] = new De(new Arc(4), new Arc(3), /*new Arc(3)*/ new Poison(3), new Arc(4), new Mana(2), new Arc(2));
        res[3] = new De(new Arc(3), new Bouclier(2), new Arc(4), new Arc(5), new Bouclier(2), new Arc(4));
        return res;
    }

    // FONCTIONS D'AMELIORATION
    /*La fonction suivante est celle qui est appelée depuis la classe Combat lorsque le héros doit être amélioré au niveau 2*/
    public void ameliorationAuNiveau2(String ameliorationChoisie)
    {
        if(ameliorationChoisie.equals("Ranger"))
        {
            this.ameliorationNiv2Ranger();
        }
        else if(ameliorationChoisie.equals("Chasseur"))
        {
            this.ameliorationNiv2Chasseur();
        }
    }
    // Premiere proposition si le héros est au niveau 1
    public void ameliorationNiv2Ranger() {
        this.setPdvMax(this.getPdvMax() + 2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnUn());
        this.setNomEntite("Ranger");
        this.incrementationNiveauActuel();
    }

    // 2ème proposition si l'archer est au niveau 1
    public void ameliorationNiv2Chasseur() {
        this.setPdvMax(this.getPdvMax() + 4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnDeux());
        this.setNomEntite("Chasseur");
        this.incrementationNiveauActuel();
    }

    /*Fonction appelée lorsque le héros doit être amélioré au niveau 3, puisque le joueur n'a pas le choix de
    l'amélioration pour le niveau 3, il n'y a pas besoin de prendre un Sring ameliorationChoisie en argument*/
    public void ameliorationAuNiveau3() {
        if (this.getNomEntite().equals("Ranger")) {
            this.ameliorationNiv3Elfe();
        } else if (this.getNomEntite().equals("Chasseur")) {
            this.ameliorationNiv3Beastmaster();
        }
    }

    // Proposée si l'archer avait été amélioré en Ranger
    public void ameliorationNiv3Elfe() {
        this.setPdvMax(this.getPdvMax() + 2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxUn());
        this.setNomEntite("Elfe");
        this.incrementationNiveauActuel();
    }

    // Proposée si l'archer avait été amélioré en Chasseur
    public void ameliorationNiv3Beastmaster() {
        this.setPdvMax(this.getPdvMax() + 4);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxDeux());
        this.setNomEntite("Beastmaster");
        this.incrementationNiveauActuel();
    }
}
