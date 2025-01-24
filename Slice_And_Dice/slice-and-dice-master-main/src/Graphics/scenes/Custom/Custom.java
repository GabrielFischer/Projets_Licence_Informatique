package Graphics.scenes.Custom;

import main.Main;
import Graphics.scenes.SceneMethodes;
import Controller.ui.Custom.CustomHelp;

import java.awt.*;

public class Custom implements SceneMethodes {
    private Main main;
    private CustomHomePage homePage;
    private CustomHero hero;
    private CustomDicePage dicePage;
    private CustomNamePage namePage;
    private  CustomFinalPage finalPage;
    private CustomHelp customHelp;
    private CustomEntrance customEntrance;
    private CustomChoose customChoose;
    private String state;  //depending on the page of Custom that we are currently at - Home, Dice, Name, Final

    public Custom(Main main){
        this.main = main;
        this.state = "Entrance";
        this.customHelp = new CustomHelp(this);
        this.homePage = new CustomHomePage(this);
        this.dicePage = new CustomDicePage(this);
        this.namePage = new CustomNamePage(this);
        this.finalPage = new CustomFinalPage(this);
        this.customEntrance = new CustomEntrance(this);
        this.customChoose = new CustomChoose(this);
        //this.hero = new CustomHero(this);
    }

    @Override
    public void render(Graphics g) {
        switch (state){
            case "Entrance":
                customEntrance.render(g);
                break;
            case "Choose":
                customChoose.render(g);
                break;
            case "Home":
                homePage.render(g);
                break;
            case "Dice":
                dicePage.render(g);
                break;
            case "Name":
                namePage.render(g);
                namePage.renderPanel();
                break;
            case "Final":
                finalPage.render(g);
        }
    }

    @Override
    public void mouseClicked(int x, int y) {
        switch (state){
            case "Entrance":
                customEntrance.mouseClicked(x, y);
                break;
            case "Choose":
                customChoose.mouseClicked(x, y);
                break;
            case "Home":
                homePage.mouseClicked(x,y);
                break;
            case "Dice":
                dicePage.mouseClicked(x,y);
                break;
            case "Name":
                namePage.mouseClicked(x,y);
                break;
            case "Final":
                finalPage.mouseClicked(x,y);
                break;
        }
    }

    @Override
    public void mouseMoved(int x, int y) {
        switch (state){
            case "Entrance":
                customEntrance.mouseMoved(x, y);
                break;
            case "Choose":
                customChoose.mouseMoved(x, y);
                break;
            case "Home":
                homePage.mouseMoved(x,y);
                break;
            case "Dice":
                dicePage.mouseMoved(x,y);
                break;
            case "Name":
                namePage.mouseMoved(x,y);
                break;
            case "Final":
                finalPage.mouseMoved(x,y);
                break;
        }
    }

    @Override
    public void mousePressed(int x, int y) {
        switch (state){
            case "Entrance":
                customEntrance.mousePressed(x, y);
                break;
            case "Choose":
                customChoose.mousePressed(x, y);
                break;
            case "Home":
                homePage.mousePressed(x,y);
                break;
            case "Dice":
                dicePage.mousePressed(x,y);
                break;
            case "Name":
                namePage.mousePressed(x,y);
                break;
            case "Final":
                finalPage.mousePressed(x,y);
                break;
        }
    }

    @Override
    public void mouseReleased(int x, int y) {
        switch (state){
            case "Entrance":
                customEntrance.mouseReleased(x, y);
                break;
            case "Choose":
                customChoose.mouseReleased(x, y);
                break;
            case "Home":
                homePage.mouseReleased(x,y);
                break;
            case "Dice":
                dicePage.mouseReleased(x,y);
                break;
            case "Name":
                namePage.mouseReleased(x,y);
                break;
            case "Final":
                finalPage.mouseReleased(x,y);
                break;
        }
    }

    @Override
    public void mouseDragged(int x, int y) {

    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState(){
        return state;
    }

    public Main getMain(){
        return this.main;
    }

    public CustomHomePage getHomePage(){
        return this.homePage;
    }

    public CustomHelp getCustomHelp() {
        return customHelp;
    }

    public CustomDicePage getCustomDice(){
        return dicePage;
    }

    public CustomHero getCustomHero(){
        return hero;
    }

    public CustomNamePage getNamePage(){
        return namePage;
    }

    public CustomFinalPage getFinalPage(){
        return finalPage;
    }
}
