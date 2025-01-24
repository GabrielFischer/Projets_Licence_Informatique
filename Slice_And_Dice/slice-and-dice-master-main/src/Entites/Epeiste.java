package Entites;

import De.Capacite.Arc;
import De.Capacite.Bouclier;
import De.Capacite.CoupEpee;
import De.Capacite.Soin;
import De.De;
import java.awt.*;

public class Epeiste extends Heros{

    public Epeiste(){
        super(12, importeDeEpeiste(), new Color(100, 211, 239), "Epeiste", "Barbare", "Chevalier", "Mercenaire", "Warmaster", initDeAmelioration());

    }

    protected static De importeDeEpeiste(){ //sert à créer le dé de l'a, donc mettre les attaques dasn le new De(...) ; utiliser cette fonctiondans la classe Joueur lorsque l'on ajoute les héros à la listeHerosDisponibles
        return new De(new CoupEpee(1), new CoupEpee(2), new CoupEpee(2),new CoupEpee(1), new CoupEpee(1), new CoupEpee(3));
        
    }

    protected static De[] initDeAmelioration(){/*La fonction suivante permet de stocker les dés correspondants aux différents stades d'amélioration du héros*/
        De[] res = new De[4];
        res[0] = new De(new CoupEpee(2), new CoupEpee(2), new Bouclier(2), new Bouclier(3), null, null);
        res[1] = new De(new CoupEpee(2), new CoupEpee(2), new CoupEpee(2), new CoupEpee(3), new Bouclier(2), null);
        res[2] = new De(new CoupEpee(2), new CoupEpee(3), new Arc(2), new Arc(3), new CoupEpee(2), new CoupEpee(3));
        res[3] = new De(new CoupEpee(3), new CoupEpee(3), new Bouclier(3), new Bouclier(3), new Arc(3), null);

        return res;
    }
    //FONCTIONS D'AMELIORATIONS
    /*La fonction suivante est celle qui est appelée depuis la classe Combat lorsque le héros doit être amélioré au niveau 2*/
    public void ameliorationAuNiveau2(String ameliorationChoisie)
    {
        if(ameliorationChoisie.equals("Barbare"))
        {
            this.ameliorationNiv2Barbare();
        }
        else if(ameliorationChoisie.equals("Chevalier"))
        {
            this.ameliorationNiv2Chevalier();
        }
    }
    //Premiere proposition si le héros est au niveau 1
    public void ameliorationNiv2Chevalier()
    {
        this.setPdvMax(this.getPdvMax()+2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnDeux());
        this.setNomEntite("Chevalier");
        this.incrementationNiveauActuel();
    }
    //2ème proposition si l'épéiste est au niveau 1
    public void ameliorationNiv2Barbare()
    {
        this.setPdvMax(this.getPdvMax() + 3);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationUnUn());
        this.setNomEntite("Barbare");
        this.incrementationNiveauActuel();
    }



    public void ameliorationAuNiveau3()
    {
        if(this.getNomEntite().equals("Barbare"))
        {
            this.ameliorationNiv3Mercenaire();
        }
        else if(this.getNomEntite().equals("Chevalier"))
        {
            this.ameliorationNiv3Warmaster();
        }
    }
    //Proposée si l'épéiste avait été amélioré en Chevalier
    public void ameliorationNiv3Warmaster()
    {
        this.setPdvMax(this.getPdvMax()+2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxDeux());
        this.setNomEntite("Warmaster");
        this.incrementationNiveauActuel();
    }
    //Proposée si l'épéiste avait été amélioré en Barbare
    public void ameliorationNiv3Mercenaire()
    {
        this.setPdvMax(this.getPdvMax()+2);
        this.setPdv(this.getPdvMax());
        this.setDe(this.getDeAmeliorationDeuxUn());
        this.setNomEntite("Mercenaire");
        this.incrementationNiveauActuel();
    }
}
