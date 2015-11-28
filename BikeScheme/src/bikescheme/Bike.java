package bikescheme;

public class Bike {
    private String bikeID;
    
    public Bike(String bikeID){
        this.bikeID = bikeID;
    }
    
    public String getId(){
        return bikeID;
    }
}
