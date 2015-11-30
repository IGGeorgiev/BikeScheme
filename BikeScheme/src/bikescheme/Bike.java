package bikescheme;

public class Bike {
    private String bikeID;
    private boolean isFaulty;
    public Bike(String bikeID){
        this.bikeID = bikeID;
        this.isFaulty = false;
    }
    /**
     * returns a string that is the unique bike ID
     * @return String bikeId
     */
    public String getId(){
        return bikeID;
    }
    public boolean isFaulty(){
        return this.isFaulty;
    }
    public void setFaulty(boolean fault){
        this.isFaulty = fault;
    }
}
