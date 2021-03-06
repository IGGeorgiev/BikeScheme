/**
 * 
 */
package bikescheme;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *  
 * Docking Station.
 * 
 * @author pbj
 *
 */
public class DStation implements StartRegObserver,  
                                 DPointObserver,
                                 ViewActivityObserver,
                                 ViewFreeDockingStationsObserver
                                 /*,KeyInsertionObserver*/ {
    public static final Logger logger = Logger.getLogger("bikescheme");

    private String instanceName;
    private int eastPos;
    private int northPos;
    private int freePoints;
    
    private DSTouchScreen touchScreen;
    private CardReader cardReader;
    private KeyReader keyReader;
    private KeyIssuer keyIssuer;
    private List<DPoint> dockingPoints;
         
 
    /**
     * 
     * Construct a Docking Station object with touch screen, card reader
     * and key issuer interface devices and a connection to a number of
     * docking points.
     * 
     * If the instance name is <foo>, then the Docking Points are named
     * <foo>.1 ... <foo>.<numPoints> . 
     * 
     * @param instanceName
     */
    public DStation(
            String instanceName,
            int eastPos,
            int northPos,
            int numPoints) {
        
     // Construct and make connections with interface devices
        
        this.instanceName = instanceName;
        this.eastPos = eastPos;
        this.northPos = northPos;
        
        touchScreen = new DSTouchScreen(instanceName + ".ts");
        touchScreen.setStartRegObserver(this);
        touchScreen.setViewActivityObserver(this);
        touchScreen.setViewFreeDockingStationsObserver(this);
        
        cardReader = new CardReader(instanceName + ".cr");
        
        keyIssuer = new KeyIssuer(instanceName + ".ki");
        
        keyReader = new KeyReader(instanceName+ ".kr");
        //keyReader.setObserver(this);
        
        freePoints = numPoints;
        
        dockingPoints = new ArrayList<DPoint>();
        
        for (int i = 1; i <= numPoints; i++) {
            DPoint dp = new DPoint(instanceName + "." + i, i - 1);
            dockingPoints.add(dp);
            dp.setDPointObserver(this); 
            dp.setKeyInsertedObserver(this);
        }
    }
       
    void setDistributor(EventDistributor d) {
        touchScreen.addDistributorLinks(d); 
        cardReader.addDistributorLinks(d);
        keyReader.addDistributorLinks(d);
        for (DPoint dp : dockingPoints) {
            dp.setDistributor(d);
        }
    }
    
    void setCollector(EventCollector c) {
        touchScreen.setCollector(c);
        cardReader.setCollector(c);
        keyIssuer.setCollector(c);
        for (DPoint dp : dockingPoints) {
            dp.setCollector(c);
        }
    }
    
    /** 
     * 
     * Method called on docking station receiving a "start registration"
     * triggering input event at the touch screen.
     * 
     * @param personalInfo
     */
    public void startRegReceived(String personalInfo) {
        logger.fine("Starting on instance " + getInstanceName());
        
        cardReader.requestCard();    
        logger.fine("Card read at " + getInstanceName());
        
        String cardDetails = cardReader.checkCard();
        //cardReader.checkCard();
        
        String keyId = keyIssuer.issueKey(); // Generate output event
        //keyIssuer.issueKey();
        logger.fine("Key " + keyId + " issued");
        
        addUserObserver.addUser(keyId, personalInfo, cardDetails);
    }
    
    public String getInstanceName() {
        return instanceName;
    }
    
    public int getEastPos() {
        return eastPos;
    }
    
    public int getNorthPos() {
        return northPos;
    }
    
    public int getNumberOfPoints(){
        return dockingPoints.size();
    }
    
    public int getFreePoints(){
        return freePoints;
    }
    
    public String getStatus(){
        double occupancyRatio = (double)(getNumberOfPoints()-getFreePoints())/getNumberOfPoints();
        if     (occupancyRatio>=0.85){return "HIGH";}
        else if(occupancyRatio<=0.15){return "LOW";}
        else{return "OK";}
        
    }
    public boolean hasFaultyBikes(){
        for(DPoint dPoint : dockingPoints){
            if(dPoint.hasFaultyBike())return true;
        }
        return false;
    }
    //======================HANDLES ADD USER REQUESTS=========================
    
    private UserActivitiesObserver addUserObserver;
    
    public void setAddUserObserver(UserActivitiesObserver o){
        addUserObserver = o;
    }
    //=========CODE FOR HANDLING RETURN BIKE AND ADD BIKE USE-CASE=========
    private ActionsForBikeAndUserObserver removeBikeObserver ;

    public void setRemoveBikeObserver(ActionsForBikeAndUserObserver o){
        removeBikeObserver = o;
    }
    
    @Override
    public void disassociateBikeFromUser(String bikeId) {
        logger.fine(getInstanceName());
        removeBikeObserver.returnOrAddBike(bikeId, this.getInstanceName());
        freePoints--;
    }
    
    //=========CODE FOR HANDLING HIRE BIKE USE-CASE=========
    private ActionsForBikeAndUserObserver bikeActionObserver;
    public void setAddBikeObserver(ActionsForBikeAndUserObserver o){
        bikeActionObserver = o;
    }
    
    @Override
    public boolean associateBikeToUser(String keyId, String bikeId) {
        logger.fine(getInstanceName());
        boolean shouldContinue = bikeActionObserver.hireOrRemoveBike(keyId, bikeId, this.getInstanceName());
        if(shouldContinue){
            freePoints++;
        }
        return shouldContinue;
        
    }
    //=========CODE FOR HANDLING FAULTY BIKE USE-CASE=========
    @Override
    public void reportBikeFaulty(String bikeId) {
        logger.fine(getInstanceName());
        bikeActionObserver.reportBikeFaulty(bikeId);
    }
    //==========CODE FOR HANDLING VIEW ACTIVITY USE CASE=========
   
    private UserActivitiesObserver viewActivityObserver;
    
    public void setUserActivitiesObserver(UserActivitiesObserver o){
        viewActivityObserver = o;
    }
    
    @Override
    public void viewActivityReceived() {
        logger.fine(getInstanceName());
        this.touchScreen.showPrompt("Please insert key.");
        logger.fine(getInstanceName());
        String keyId = this.keyReader.waitForKeyInsertion();
        logger.fine(getInstanceName());
        this.touchScreen.showUserActivity(viewActivityObserver.viewActivityReceived(keyId));
    }
    //==========CODE FOR HANDLING VIEW FREE STATIONS USE CASE=========
    private UserActivitiesObserver requestFreeStations;
    public void setRequestFreeStationsObserver(UserActivitiesObserver o){
        this.requestFreeStations = o;
    }
    @Override
    public List<String> requestFreeStations() {
        logger.fine(getInstanceName());

        return requestFreeStations.sendFreeStations();
    }

    
   
 

}
