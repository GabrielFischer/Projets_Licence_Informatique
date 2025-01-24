package De.Capacite;

import Entites.*;
public class Bind extends CapaciteSpe{
/*Sort disponible si le Démoniste est sur le terrain*/
    public Bind()
    {
        super(0, 3,"Bind");
        this.setNom("Bind");
        this.setDist(true);
        this.setNiveau(8);//on dit que le "niveau" est égal à 4 car ce sort inflige toujours 4 dégâts
        this.setDescription("8 dégâts à tous les ennemis et 5 dégâts au héros qui possède le plus de vie");
        this.setType(12);
        this.cible=0;
    }

    //Besoin de faire les deux car si on fait avec Entite en parametre, appelle la fonction avec ennemi/hero de capacite
    @Override
    public void action(Ennemis e){
        this.attaque(e);
        /*Cette fonction ne prend en compte que les dégâts infligés à un ennemi, le fait que les dégâts doievnt être infligés à tous les ennemis
        * ainsi que le fait que le héros possèdant le plus de points de vie doive subir des dégâts devront être gérés dans la classe qui gère
        * le combat*/
    }
    @Override
    public void action(Heros e){
        this.attaque(e);
        /*Cette fonction ne prend en compte que les dégâts infligés à un héros, le fait que les dégâts doievnt être infligés à tous les ennemis
        * ainsi que le fait que le héros possèdant le plus de points de vie doive subir des dégâts devront être gérés dans la classe qui gère
        * le combat*/
    }
}
