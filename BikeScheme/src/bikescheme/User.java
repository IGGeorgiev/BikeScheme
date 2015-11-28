package bikescheme;

public class User {
    
    private String personalDetails;
    private String cardDetails;
    private String keyId;
    
    public User(String keyId,String personalDetails,String cardDetails){
        this.setKeyId(keyId);
        this.setCardDetails(cardDetails);
        this.setPersonalDetails(personalDetails);
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
    
}
