package Tours;

import java.io.IOException;

import javax.imageio.ImageIO;

import Jeu.GamePanel;
import Jeu.Niveau;

public class Bomber extends Tours{
    
    public Bomber(Niveau niveau){
        super("Bomber",15,3,3,3,3,1,3,false,3);
        this.niveau = niveau;
        this.plateau = niveau.p;
        importImage();
    }
    public Bomber(Niveau niveau,GamePanel gp){
        super("Bomber",15,3,3,3,3,1,3,false,3);
        this.niveau = niveau;
        this.plateau = niveau.p;
        this.gp = gp;
        importImage();   
    }

    public void importImage(){
        try {
            image = ImageIO.read(getClass().getResourceAsStream("../Images/Tours/Bomber/Bomber1.png"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean augmenterNv(){
        if(super.augmenterNv()){
            this.augmenterDegatsP((3));
            this.augmenterDegatsM((3));
            this.augmenterVitesseAttaque(1);
            return true;
        }
        return false;
    }
}