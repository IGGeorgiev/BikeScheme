package bikescheme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    
    private String personalDetails;
    private String cardDetails;
    private String keyId;
    private Date startDate;
    private String startPoint;  //Starting point of Trip
    private List<Trip> trips;
    
    
    
    public User(String keyId,String personalDetails,String cardDetails){
        this.setKeyId(keyId);
        this.setCardDetails(cardDetails);
        this.setPersonalDetails(personalDetails);
        this.startDate = null;
        this.startPoint = null;
        this.trips = new ArrayList<Trip>();
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public void setCardDetails(String cardDetails) {
        this.cardDetails = cardDetails;
    }

    public String getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(String personalDetails) {
        this.personalDetails = personalDetails;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    
    public void startUsage(Date date, String startPoint){
        this.startDate = date;
        this.startPoint = startPoint;
    }
    
    public void endUsage(Date date, String endPoint){
        Trip tr = new Trip(startDate, date);
        trips.add(tr);
        tr.setStartStation(startPoint);
        tr.setEndStation(endPoint);
    }
    
    public List<Trip> getTrips(){
        return trips;
    }
    
    public void clearTrips(){
        trips.clear();
    }
}
