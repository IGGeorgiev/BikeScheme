package bikescheme;

public class BankServer implements BankingServerInterface{
    
    private Hub hub;
    
    public BankServer(){
        hub.setBankingServerInterface(this);
    }
    
    public void applyCharges(int price, String personalDetails, String cardDetails){
        //Dummy Implementation
    }

}
