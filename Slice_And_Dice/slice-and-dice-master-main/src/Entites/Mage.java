package Entites;

import De.Capacite.*;
import De.De;

import java.awt.*;

public class Mage extends Heros {
    public Mage() {
        super(8, importeDeMage(), new Color(127, 212, 147), "Mage", "Sorcier", "Arcaniste", "Chaman", "Demoniste", initDeAmelioration());
    }

    protected static De importeDeMage() { // sert à créer le dé du mage, donc mettre les attaques dasn le new De(...) ;
                                          // utiliser cette fonctiondans la classe Joueur lorsque l'on ajoute les héros
                                          // à la listeHerosDisponibles
        De nouvDe = new De(new Poison(1), new Mana(2), new Mana(2), new Mana(1), null, null);
        return nouvDe;
    }

    protected static De[] initDeAmelioration(){/*La fonction suivante permet de stocker les dés correspondants aux différents stades d'amélioration du héros*/
        De[] res = new De[4];
        res[0] = new De(new Mana(2), new Mana(2), new Mana(2), new Mana(2), new Mana(1), new Mana(1));
        res[1] = new De(new Mana(2), new Mana(3), new Mana(2), new Poison(1), null, null);
        res[2] = new De(new Mana(3), new Mana(3), new Mana(3), new Mana(3), new Soin(2), new CoupEpee(2));
        res[3] = new De(new Mana(3), new Mana(3), new Poison(2), new Poison(1), new Mana(3), new Mana(2));

        return res;
    }

    // Fonctions d'amélioration

    /*La fonction suivante est celle qui est appelée depuis la classe Combat lorsque le héros doit être amélioré au niveau 2*/
    public void ameliorationAuNiveau2(String ameliorationChoisie)
    {
        if(ameliorationChoisie.equals("Sorcier"))
        {
            this.ameliorationNiv2Sorcier();
        }
        else if(ameliorationChoisie.equals("Arcaniste"))
        {
            this.ameliorationNiv2Arcaniste();
        }
    }

    // Premiere proposition si le héros est au niveau 1
    public void ameliorationNiv2Sorcier() {
        this.setPdvMax(this.getPdvMax() + 2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnUn());
        this.setNomEntite("Sorcier");
        this.incrementationNiveauActuel();
    }

    // 2ème proposition si le mage est au niveau 1
    public void ameliorationNiv2Arcaniste() {
        this.setPdvMax(this.getPdvMax() + 2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnDeux());
        this.setNomEntite("Arcaniste");
        this.incrementationNiveauActuel();
    }

    public void ameliorationAuNiveau3() {
        if (this.getNomEntite().equals("Sorcier")) {
            this.ameliorationNiv3Chaman();
        } else if (this.getNomEntite().equals("Arcaniste")) {
            this.ameliorationNiv3Demoniste();
        }
    }



    // Proposée si le mage avait été amélioré en Sorcier
    public void ameliorationNiv3Chaman() {
        this.setPdvMax(this.getPdvMax() + 3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxUn());
        this.setNomEntite("Chaman");
        this.incrementationNiveauActuel();
    }

    // Proposée si le mage avait été amélioré en Arcaniste
    public void ameliorationNiv3Demoniste() {
        this.setPdvMax(this.getPdvMax() + 3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxDeux());
        this.setNomEntite("Demoniste");
        this.incrementationNiveauActuel();
    }


}
