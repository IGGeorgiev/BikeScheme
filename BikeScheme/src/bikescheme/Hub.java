/**
 * 
 */
package bikescheme;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.ArrayList;


/**
 * 
 * Hub system.
 * 
 * 
 * @author pbj
 * 
 */

public class Hub implements AddDStationObserver,
					        ActionsForBikeAndUserObserver,
					        UserActivitiesObserver,
					        HubTerminalStatReqObserver {
    
	public static final Logger logger = Logger.getLogger("bikescheme");
	public static final String HUBNAME = "CyclOps.Hub";
	//String is the unique key in users
	private List<Bike> bikes ;
	private List<User> users ;
	private Map<Bike,User> inUse ; 
	private HubTerminal terminal;
	private HubDisplay display;
	private Map<String, DStation> dockingStationMap;
	private KeyIssuer keyIssuer;
	private List<String>staffIds;

	/**
	 * 
	 * Construct a hub system with an operator terminal, a wall display and
	 * connections to a number of docking stations (initially 0).
	 * 
	 * Schedule update of the hub wall display every 5 minutes with docking
	 * station occupancy data.
	 * 
	 * @param instanceName
	 */
	
	public Hub() {
		// Construct and make connections with interface devices
		terminal = new HubTerminal("ht");
		terminal.setObserver(this);
		terminal.setStatObserver(this);
		terminal.setFaultObserver(this);
		display = new HubDisplay("hd");
		dockingStationMap = new HashMap<String, DStation>();
		bikes = new ArrayList<Bike>();
	    users = new ArrayList<User>();
	    inUse = new HashMap<Bike,User>();
	    keyIssuer = new KeyIssuer(HUBNAME);
	    staffIds = new ArrayList<String>();
		// Schedule timed notification for generating updates of
		// hub display.

		// The idiom of an anonymous class is used here, to make it easy
		// for hub code to process multiple timed notification, if needed.

		
        Clock.createInstance();
		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {

					/**
					 * Generate display of station occupancy data.
					 */
					@Override
					public void processTimedNotification() {
						logger.fine("");
						/*String[] occupancyArray =
						// "DSName","East","North","Status","#Occupied","#DPoints"
						{ "A", "100", "200", "HIGH", "19", "20", "B", "300",
								"-500", "LOW", "1", "50" };
								*/
						
						List<String> occupancyData = populateFullOccupancyList();
						display.showOccupancy(occupancyData);
					}

				}, Clock.getStartDate(), 0, 5);
		
		/**
		 * Notification to clear the User's recent trips and possibly
		 * apply charges to their bank account based on time hired.
		 * THE LATTER IS NOT IMPLEMENTED
		 * 
		 * @author iggeorgiev
		 */
		Clock.getInstance().scheduleNotification(
		        new TimedNotificationObserver() {
		            
		            public void processTimedNotification(){
		                logger.fine("");
		                
		                for (User u : users){
		                    // User billing should be done here
		                    int charge = 0;
		                    for(Trip tr : u.getTrips()){
		                        
		                        //Calculates each user's applied charges
		                        charge += tr.getPrice();
		                    }
		                    if(charge != 0){
		                        applyCharges(charge, u.getPersonalDetails(), u.getCardDetails());
		                    } 
		                    u.clearTrips();
		                }
		            }
		            
		        }, Clock.getStartDate(), 24, 0);
	}

	public void setDistributor(EventDistributor d) {

		// The clock device is connected to the EventDistributor here, even
		// though the clock object is not constructed here,
		// as no distributor is available to the Clock constructor.
		Clock.getInstance().addDistributorLinks(d);
		terminal.addDistributorLinks(d);
	}
	
	public void setCollector(EventCollector c) {
		display.setCollector(c);
		terminal.setCollector(c);
	}

	/**
     * 
     */
	@Override
	public void addDStation(String instanceName, int eastPos, int northPos, int numPoints) {
		logger.fine("");

		DStation newDStation = new DStation(instanceName, eastPos, northPos,numPoints);
		dockingStationMap.put(instanceName, newDStation);
		newDStation.setRemoveBikeObserver(this);
		newDStation.setAddBikeObserver(this);
		newDStation.setAddUserObserver(this);
		newDStation.setUserActivitiesObserver(this);
		newDStation.setRequestFreeStationsObserver(this);

		// Now connect up DStation to event distributor and collector.

		EventDistributor d = terminal.getDistributor();
		EventCollector c = display.getCollector();

		newDStation.setDistributor(d);
		newDStation.setCollector(c);
	}

	public DStation getDStation(String instanceName) {
		return dockingStationMap.get(instanceName);
	}
	
	//====================ADDS USER TO USER LIST==============================
	
    public boolean addUser(String keyId, String personalDetails, String cardDetails){
        logger.fine("Recording user : "+personalDetails+" with key : "+keyId+" in " + HUBNAME);
        User user = new User(keyId,personalDetails,cardDetails);
        users.add(user);
        return true;
    }
    //=========CODE FOR HANDLING RETURN BIKE AND ADD BIKE USE-CASE=========    
    
    @Override
    public void returnBike(String bikeId, String endPoint) {
        Bike bike = findBikeById(bikeId);
        if(inUse.get(bike)==null){
            logger.fine(HUBNAME+"~ "+bikeId+" added by Staff.");
            Bike newBike = new Bike(bikeId);
            bikes.add(newBike);
        }else{
            logger.fine(HUBNAME+"~ bike returned by Customer.");
            User user = findUserByBikeId(bikeId);
            user.endUsage(Clock.getInstance().getDateAndTime(), endPoint);
            inUse.remove(bike);
        }
        
    }
    //=========CODE FOR HANDLING HIRE BIKE AND REMOVE BIKE USE-CASES=========   
    @Override
    public boolean addBike(String keyId, String bikeId, String startPoint) {        
        logger.fine(HUBNAME);
        Bike bike = findBikeById(bikeId);
        User user = findUserByKeyId(keyId);
        boolean isStaff = isStaffKey(keyId);
        boolean isUser  = user != null;
        boolean shouldContinue = isStaff || isUser;

        if(isStaff && bike != null){
            bikes.remove(bike);
            logger.fine(HUBNAME+"~ bike is removed");

        }
        if(shouldContinue && bike != null){
            inUse.put(bike,user);
            logger.fine(HUBNAME+"~ bike is hired");
            user.startUsage(Clock.getInstance().getDateAndTime(), startPoint);
        }
        return shouldContinue;
    }
    //=========CODE FOR HANDLING VIEW ACTIVITY USE-CASE=========   
    /**
     * 
     * Returns a list of Strings depicting user activity
     * all the way back to the DSTouchScreen through
     * DStation.
     * 
     * @return List<String>
     * 
     */
    @Override
    public List<String> viewActivityReceived(String keyId) {
        logger.fine("Currently at " + HUBNAME);
        List<String> viewActivity = new ArrayList<String>();
        User usr = findUserByKeyId(keyId);
        if (usr != null){
            logger.fine(HUBNAME+"~ Fetching Trip Information...");
            for(Trip tr : usr.getTrips()){
                viewActivity.add(tr.getStartTime());
                viewActivity.add(tr.getStartStation());
                viewActivity.add(tr.getEndStation());
                viewActivity.add(tr.getDuration());
            }
        }
        return viewActivity;
    }
    //=========CODE FOR ISSUING STAFF MEMBER KEYS(USED FOR REMOVING BIKES)=========
    public void issueStaffKey(){
        logger.fine(HUBNAME+ "~ issuing staff key");
        staffIds.add(this.keyIssuer.issueKey());
    }
    //=========CODE FOR HANDLING FAULTY BIKE USE-CASE=========
    @Override
    public void reportBikeFaulty(String bikeId) {
        logger.fine(HUBNAME+"~ reporting bike fault");
        Bike bike = findBikeById(bikeId);
        if(bike != null){
            bike.setFaulty(true);
        }
    }
    //=========CODE FOR HANDLING VIEW FREE STATIONS USE-CASE=========
    @Override
    public List<String> sendFreeStations() {
        // TODO Auto-generated method stub
        List<String> freePoints = new ArrayList<String>();
        for(String s : dockingStationMap.keySet()){
            if(dockingStationMap.get(s).getFreePoints() > 0){
                freePoints.add(s);
                freePoints.add(String.format("%d", dockingStationMap.get(s).getEastPos()));
                freePoints.add(String.format("%d", dockingStationMap.get(s).getNorthPos()));
            }
        }
        return freePoints;
    }
    //=======================HELPER FUNCTIONS ==============================
    /**
     * Given a unique bike ID, returns the bike it's
     * associated with or null if bike with such ID
     * doesn't exist
     *   
     * @return Bike
     */
    public Bike findBikeById(String bikeId){
        for(Bike bike : bikes){
            if(bike.getId().equals(bikeId))return bike;
        }
        return null;
    }
    /**
     * Given a unique user ID, returns the user it's
     * associated with or null if user with such ID
     * doesn't exist
     *  
     * @return User
     */
    public User findUserByKeyId(String keyId){
        for(User usr : users){
            if(usr.getKeyId().equals(keyId))return usr;
        }
        return null;
    }
    /**
     * Given a unique bike ID, returns the user it's
     * associated with or null if bike with such ID
     * doesn't exist or is not associated with the user
     *  
     * @return User
     */
    public User findUserByBikeId(String bikeId){
        Bike bike = findBikeById(bikeId);
        if(bike == null)return null;
        User user = inUse.get(bike);    
        return user;
    }
    /**
     * Given a unique key ID, returns true
     * if the key is a staff key.
     *  
     * @return boolean
     */
    public boolean isStaffKey(String keyId){
        for(String staffId : staffIds){
            if (staffId.equals(keyId))return true;
        }
        return false;
    }
    
    /**
     * Populates a List of all DStations in the database in the form:
     * "DSName","East","North","Status","#Occupied","#DPoints"
     * 
     * @return List<String>
     */
    
    public List<String> populateFullOccupancyList(){
      //FIRST PRINT HIGH AND LOW OCCUPIED DSTATIONS
        List<String> occupancyList = new ArrayList<String>();
        for(String s : dockingStationMap.keySet()){
            DStation dStation = dockingStationMap.get(s);
            double occupancyRatio =(double) dStation.getFreePoints()/
                                            dStation.getNumberOfPoints();
            if(occupancyRatio >= 0.85 || occupancyRatio <= 0.15){
                occupancyList.add(dStation.getInstanceName());
                occupancyList.add(String.format("%d", dStation.getEastPos()));
                occupancyList.add(String.format("%d", dStation.getNorthPos()));
                occupancyList.add(dStation.getStatus());
                occupancyList.add(String.format("%d", dStation.getNumberOfPoints()-
                                                        dStation.getFreePoints()));
                occupancyList.add(String.format("%d", dStation.getNumberOfPoints()));
            }
        }
        //NOW PRINT THE OTHER DSTATIONS
        for(String s : dockingStationMap.keySet()){
            DStation dStation = dockingStationMap.get(s);
            double occupancyRatio =(double) dStation.getFreePoints()/
                                            dStation.getNumberOfPoints();
            if(occupancyRatio < 0.85 && occupancyRatio > 0.15){
                occupancyList.add(dStation.getInstanceName());
                occupancyList.add(String.format("%d", dStation.getEastPos()));
                occupancyList.add(String.format("%d", dStation.getNorthPos()));
                occupancyList.add(dStation.getStatus());
                occupancyList.add(String.format("%d", dStation.getNumberOfPoints()-
                                                        dStation.getFreePoints()));
                occupancyList.add(String.format("%d", dStation.getNumberOfPoints()));
            }
        }
        //FINALLY RETURN THE POPULATED ARRAY
        return occupancyList;
    }
    /**
     * 
     * Populates a list of faulty DStations of the form
     * #DName,#EastPos,#NorthPos
     *
     */
    public List<String> populateFaultyDStationList(){
        List<String> faultyStations = new ArrayList<String>();
        for(String s : dockingStationMap.keySet()){
            if(dockingStationMap.get(s).hasFaultyBikes()){
               faultyStations.add(s);
               faultyStations.add(String.format("%d", dockingStationMap.get(s).getEastPos()));
               faultyStations.add(String.format("%d", dockingStationMap.get(s).getNorthPos()));
            }
        }
        return faultyStations;
    }
    /**
     * 
     * Populates a list of stats of the form
     * #numberOfTrips,#numberOfUsers,#distanceTraveled,#averageTripTime
     *
     */
    public List<String> populateStatsList(){
        List<String> statsList = new ArrayList<String>();
        int numberOfTrips = 0;
        int numberOfUsers = this.users.size();
        double distanceTraveled = 0;
        int totalTripTimeMins = 0;
        for(User usr : users){
            for(Trip trip: usr.getTrips()){
                numberOfTrips++;
                DStation start = dockingStationMap.get(trip.getStartStation());
                DStation end   = dockingStationMap.get(trip.getEndStation());
                distanceTraveled+=distance(start.getEastPos(),start.getNorthPos(),
                                           end.getEastPos()  ,end.getNorthPos());
                totalTripTimeMins+=trip.getDurationInt();
            }
        }
        double averageTripTime = (double)totalTripTimeMins/numberOfTrips;
        statsList.add(String.format("%d", numberOfTrips));
        statsList.add(String.format("%d", numberOfUsers));
        statsList.add(String.format("%.2f", distanceTraveled));
        statsList.add(String.format("%.2f", averageTripTime));

       
        return statsList;
    }
    public double distance(int x,int y,int x1,int y1){
        return Math.sqrt((double)(x-x1)*(x-x1)+(y-y1)*(y-y1));
    }
    //======================POSSIBLE CONNECTION TO BANKING SERVER======================
    
    private BankingServerInterface bsi;
    
    public void setBankingServerInterface(BankingServerInterface o){
        bsi = o;
    }
    
    public void applyCharges(int charge, String personalDetails, String cardAuthenticationNumber){
      //Applies charges to the given card authentication number relative
      //to the user's personal details
        bsi.applyCharges(charge, personalDetails, cardAuthenticationNumber);
    }

    

    

    
}

