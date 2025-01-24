package Jeu;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



public class KeyHandler implements KeyListener{
    GamePanel gp;
    public boolean spacePressed, enterPressed;

    private static final int MAX_X = 12;
    private static final int MAX_Y = 11;

    public int x;
    public int y;

    KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_SPACE){
            spacePressed = true;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {


        int code = e.getKeyCode();


        //MENU

        if(gp.gameState == gp.fileSelectState){
            if(!gp.choixNiveau){
                if (code == KeyEvent.VK_Z){ 
                    gp.ui.commandNum --;
                    if(gp.ui.commandNum < 0){
                        gp.ui.commandNum = 1;
                    }
                }

                if (code == KeyEvent.VK_S){ 
                    gp.ui.commandNum ++;
                    if(gp.ui.commandNum > 1){
                        gp.ui.commandNum = 0 ;
                    }
                }
                if(code == KeyEvent.VK_ENTER){
                    if(gp.ui.commandNum == 0){
                        gp.choixNiveau = true;
                    }
                    else if(gp.ui.commandNum == 1){
                        gp.choixNiveau = true;
                        gp.niveau.marathon = true;
                    }
                }
            }

            else{
                
                if (code == KeyEvent.VK_Z){ 
                    gp.ui.commandNum --;
                    if(gp.ui.commandNum < 0){
                        gp.ui.commandNum = gp.niveau.nbNiveaux-1;
                    }
                }

                if (code == KeyEvent.VK_S){ 
                    
                    gp.ui.commandNum ++;
                    if(gp.ui.commandNum >= gp.niveau.nbNiveaux){
                        gp.ui.commandNum = 0;
                    }
                }
                if(code == KeyEvent.VK_ENTER){
                
                    gp.niveau.choixNiveauGraphique(gp.ui.commandNum);
                    gp.gameState = gp.playState;
                    gp.stopMusic();
                    gp.playLoop(2);
                    //gp.niveau.run();
                    
                }
            }
            
            
        }


        if (gp.gameState == gp.playState) {
            if (gp.niveau.acheterTours) {
                
                handleCursorMovementInToursMode(code);
                
            }
            else if(gp.niveau.caseSelectionnee){
                handleCursorAmelioration(code);
            }
            else
            if (!gp.niveau.next && !gp.niveau.caseSelectionnee && !gp.niveau.acheterTours) {
                handleCursorMovement(code);
                
                if (code == KeyEvent.VK_ENTER) {
                    x = gp.ui.xCase;
                    y = gp.ui.yCase;

                    if(!(x == MAX_X && y == 3) && gp.niveau.p.getTab()[y][x].type == 3 && gp.niveau.p.getTab()[y][x].caseVide()){
                        gp.niveau.acheterTours = true;
                        gp.ui.yCase = 0;
                        gp.ui.xCase = 0;
                    }
                    else if(x == MAX_X && y == 3){
                        gp.niveau.next = true;
                        gp.niveau.run();
                    }
                    else if(!(x == MAX_X && y == 3)&&gp.niveau.p.getTab()[y][x].t!=null){
                        gp.niveau.caseSelectionnee = true;
                    }
                    
                    
                    
                    // gp.niveau.caseSelectionnee = true;
                    // gp.niveau.acheterTours = true;
                }
            }
            
        
            // ...
        }
         
        

        if (code == KeyEvent.VK_SPACE){ //Pour rajouter des touches copier coller le if et remplacer VK_SPACE par VK_Touche desiree
            spacePressed=true;//il faut également creer un nouvel attribut
        }
        if (code == KeyEvent.VK_ENTER){
            enterPressed=true;
        }
    }
    private void handleCursorAmelioration(int code){
        if (code == KeyEvent.VK_Z) {
            System.out.println("Z pressed :" +gp.ui.yCase);
            gp.ui.yCase--;
            System.out.println("after Z pressed :" +gp.ui.yCase);
            if (gp.ui.yCase < 0) {
                gp.ui.yCase = 2;
            }
        } else if (code == KeyEvent.VK_S) {
            gp.ui.yCase++;
            if (gp.ui.yCase > 2) {
                gp.ui.yCase = 0;
            }
        }else if(code == KeyEvent.VK_ENTER){
            System.out.println("yCase :"+gp.ui.yCase);
            if(gp.ui.yCase == 0){
                gp.niveau.ameliorer(gp.ui.xCase, gp.ui.yCase);
                gp.niveau.caseSelectionnee =false;
            }else if(gp.ui.yCase == 1){
                gp.niveau.supprimerTour(gp.ui.xCase, gp.ui.yCase);
                gp.niveau.caseSelectionnee =false;
            }
        }
    }
    private void handleCursorMovementInToursMode(int code) {
        
        if (code == KeyEvent.VK_Z) {
            System.out.println("Z pressed :" +gp.ui.yCase);
            gp.ui.yCase--;
            System.out.println("after Z pressed :" +gp.ui.yCase);
            if (gp.ui.yCase < 0) {
                gp.ui.yCase = 2;
            }
        } else if (code == KeyEvent.VK_S) {
            gp.ui.yCase++;
            if (gp.ui.yCase > 2) {
                gp.ui.yCase = 0;
            }
        }
        else if (code == KeyEvent.VK_ENTER){
            System.out.println("x :" +x);
            System.out.println("y :" +y);
            System.out.println("typeTour :" +gp.ui.yCase);
            gp.niveau.setTours(x, y, gp.ui.yCase);
            gp.niveau.caseSelectionnee = false;
            gp.niveau.acheterTours = false;
        }
    }

    private void handleCursorMovement(int code) {
        if (code == KeyEvent.VK_Z) {
            gp.ui.yCase--;
            if(gp.ui.xCase == MAX_X){
                gp.ui.yCase = 3;
            }
            if (gp.ui.yCase < 0) {
                gp.ui.yCase = MAX_Y;
            }
        } else if (code == KeyEvent.VK_S) {
            gp.ui.yCase++;
            if(gp.ui.xCase == MAX_X){
                gp.ui.yCase = 3;
            }
            if (gp.ui.yCase > MAX_Y) {
                gp.ui.yCase = 0;
            }
        }
    
        if (code == KeyEvent.VK_Q) {
            gp.ui.xCase--;
            if(gp.ui.xCase == MAX_X){
                gp.ui.yCase = 3;
            }
            if (gp.ui.xCase < 0) {
                gp.ui.xCase = MAX_X;
            }
        } else if (code == KeyEvent.VK_D) {
            gp.ui.xCase++;
            if(gp.ui.xCase == MAX_X){
                gp.ui.yCase = 3;
            }
            if (gp.ui.xCase > MAX_X) {
                gp.ui.xCase = 0;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        int code = e.getKeyCode();
        if (code == KeyEvent.VK_SPACE){ 
            spacePressed=false;
        }
        if (code == KeyEvent.VK_ENTER){
            enterPressed=false;
        }
    }

}