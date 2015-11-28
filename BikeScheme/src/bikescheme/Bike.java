package bikescheme;

public class Bike {
    private String bikeID;
    
    public Bike(String bikeID){
        this.bikeID = bikeID;
    }
    /**
     * returns a string that is the unique bike ID
     * @return String bikeId
     */
    public String getId(){
        return bikeID;
    }
}
