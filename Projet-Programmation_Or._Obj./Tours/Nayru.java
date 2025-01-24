package Tours;

public class Nayru extends Tours{
    
    public Nayru(){
        super("Nayru",60,10,0,3,2,1,3,false,20);
    }

    public boolean augmenterNv(){
        if(super.augmenterNv()){
            this.augmenterDegatsP((10*this.getNv()));
            this.augmenterVitesseAttaque((2*this.getNv()));
            return true;
        }
        return false;
    }
}