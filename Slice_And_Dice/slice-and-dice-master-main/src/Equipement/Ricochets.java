package Equipement;

public class Ricochets extends Equipement{
    
    public Ricochets(){
        super("Ricochets","Toutes les attaques du porteurs inflingent 1 point de dégat aux ennemis adjacents",15,3);
    }

    public void effet(){
        if (perso!=null){
            perso.setRicochets(true);
        }
    }

    public void annuleEffet(){
        if (perso!=null){
            Equipement[] invent = perso.getInventaire();
            //Vérifie que le perso n'ai pas 2 Ricochets (inutile mais possible)
            if(invent[0]!=this && !invent[0].nom.equals("Ricochets")){
                perso.setRicochets(false);
            }else{
                if(invent[1]!=this && !invent[1].nom.equals("Ricochets")){
                    perso.setRicochets(false);
                }
            }
        }
    }
}
