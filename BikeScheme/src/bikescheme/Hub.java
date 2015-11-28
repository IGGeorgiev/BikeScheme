/**
 * 
 */
package bikescheme;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
					AddUserObserver,
					ViewActivityDStationObserver {
	public static final Logger logger = Logger.getLogger("bikescheme");
	public static final String HUBNAME = "CyclOps.Hub";
	//String is the unique key in users
	private List<Bike> bikes;
	private List<User> users;
	private Map<User,Bike> inUse; 
	private HubTerminal terminal;
	private HubDisplay display;
	private Map<String, DStation> dockingStationMap;

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
		display = new HubDisplay("hd");
		dockingStationMap = new HashMap<String, DStation>();
		Clock.createInstance();
		// Schedule timed notification for generating updates of
		// hub display.

		// The idiom of an anonymous class is used here, to make it easy
		// for hub code to process multiple timed notification, if needed.

		Clock.getInstance().scheduleNotification(
				new TimedNotificationObserver() {

					/**
					 * Generate dummy display of station occupancy data.
					 */
					@Override
					public void processTimedNotification() {
						logger.fine("");

						String[] occupancyArray =
						// "DSName","East","North","Status","#Occupied","#DPoints"
						{ "A", "100", "200", "HIGH", "19", "20", "B", "300",
								"-500", "LOW", "1", "50" };

						List<String> occupancyData = Arrays.asList(occupancyArray);
						display.showOccupancy(occupancyData);
					}

				}, Clock.getStartDate(), 0, 5);

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
		newDStation.setAddUserObserver(this);

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
	
    public void addUser(String keyId, String personalDetails, String cardDetails){
        logger.fine("Recording user in " + HUBNAME);
        User user = new User(keyId,personalDetails,cardDetails);
        users.add(user);
    }
    
    private Bike findBikebyId(String bikeId){
        for(Bike bike : bikes){
            if(bike.getId().equals(bikeId))return bike;
        }
        return null;
    }
    
    @Override
    public void returnBike(String bikeId) {
        Bike bike = findBikebyId(bikeId);
        if(inUse.get(bike)==null){
            logger.fine(HUBNAME+": bike added by Staff.");
        }else{
            logger.fine(HUBNAME+": bike returned by Customer.");
            inUse.remove(bike);
        }
    }
	
}
