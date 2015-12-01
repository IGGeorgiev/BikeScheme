/**
 * 
 */
package bikescheme;

// import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
// import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class SystemTest {
    private static final String LS = System.getProperty("line.separator");
    private static Logger logger;
    
    private EventDistributor distributor;
    private EventCollector collector;
    
    private List<Event> expectedOutputEvents;
    
    /*
     * 
     * INSERT SYSTEM TESTS HERE
     * 
     * 
     * 
     */
    @Test
    public void viewActivity(){
        logger.info("Start viewActivity test:");
        setupDemoSystemConfig();
        
        setupABikeConfig("B.2","bike-1");
        setupABikeConfig("A.1","bike-2");
        setupABikeConfig("A.2","bike-3");
        setupABikeConfig("A.3","bike-4");
        
        setupAUserConfig("Alice","A",1);
        setupAUserConfig("Tim","B",1);
        
        input("2 23:30, KeyReader, B.2.kr, insertKey, B.ki-1");
        expect("2 23:30, BikeLock, B.2.bl, unlocked");
        expect("2 23:30, OKLight, B.2.ok, flashed");
        
        input("2 23:55, BikeSensor, A.4.bs, dockBike, bike-1");
        expect("2 23:55, BikeLock, A.4.bl, locked");
        expect("2 23:55, OKLight, A.4.ok, flashed");
        
        input ("2 23:58, DSTouchScreen, A.ts, viewActivity");
        expect("2 23:58, DSTouchScreen, A.ts, viewPrompt  , Please insert key.");
        input ("2 23:59, KeyReader    , A.kr, keyInsertion, B.ki-1");
        expect("2 23:59, DSTouchScreen, A.ts, viewUserActivity, ordered-tuples, 4 ," 
                + "HireTime, HireDS, ReturnDS, Duration (min),"
                + "23:30   ,   B   ,    A    ,        25     ");
        input("3 00:00, Clock, clk, tick");
        //CLEARING OCCURS HERE
        expect("3 00:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
             + "DSName, East, North, Status, #Occupied, #DPoints,"
             + "     B,  400,   300,    LOW,         0,        3,"
             + "     A,    0,     0,     OK,         4,        5" 
             );

        expect("3 00:00, BankServer, BankServer, applyCharges, Charged!");
        input ("3 00:01, DSTouchScreen, A.ts, viewActivity");
        expect("3 00:01, DSTouchScreen, A.ts, viewPrompt  , Please insert key.");
        input ("3 00:01, KeyReader    , A.kr, keyInsertion, B.ki-1");
        expect("3 00:01, DSTouchScreen, A.ts, viewUserActivity, ordered-tuples, 4 ," 
                + "HireTime, HireDS, ReturnDS, Duration (min),"
                );
        input("3 23:30, KeyReader, A.2.kr, insertKey, B.ki-1");
        expect("3 23:30, BikeLock, A.2.bl, unlocked");
        expect("3 23:30, OKLight, A.2.ok, flashed");
        
        input("3 23:55, BikeSensor, A.2.bs, dockBike, bike-1");
        expect("3 23:55, BikeLock, A.2.bl, locked");
        expect("3 23:55, OKLight, A.2.ok, flashed");
        
        input("4 00:00, Clock, clk, tick");
        
        expect("4 00:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
                + "DSName, East, North, Status, #Occupied, #DPoints,"
                + "     B,  400,   300,    LOW,         0,        3,"
                + "     A,    0,     0,     OK,         4,        5" 
                );
        
        input ("4 00:01, DSTouchScreen, A.ts, viewActivity");
        expect("4 00:01, DSTouchScreen, A.ts, viewPrompt  , Please insert key.");
        input ("4 00:01, KeyReader    , A.kr, keyInsertion, B.ki-1");
        expect("4 00:01, DSTouchScreen, A.ts, viewUserActivity, ordered-tuples, 4 ," 
                + "HireTime, HireDS, ReturnDS, Duration (min),"
                );
        
        input ("3 00:10, DSTouchScreen, A.ts, viewActivity");
        expect("3 00:10, DSTouchScreen, A.ts, viewPrompt  , Please insert key.");
        input ("3 00:10, KeyReader    , A.kr, keyInsertion, B.ki-1");
        expect("3 00:10, DSTouchScreen, A.ts, viewUserActivity, ordered-tuples, 4 ," 
                + "HireTime, HireDS, ReturnDS, Duration (min)");

       // setupBikeConfig
    }
    
    @Test
    public void testFaultFlashForRefusedUserKey(){
        logger.info("Starting test: testFaultFlashForRefusedUserKey");
        setupDemoSystemConfig();
        input ("2 09:00, BikeSensor,B.2.bs, dockBike, bike-2");
        expect("2 09:00, BikeLock,  B.2.bl, locked");
        expect("2 09:00, OKLight,   B.2.ok, flashed");
        input ("2 09:30, KeyReader, B.2.kr, insertKey, key-2");
        expect("2 09:30, FaultLight,   B.2.fl, flashed");

    }
    @Test
    public void testHireBike(){
        logger.info("Starting test: testHireBike");
        setupDemoSystemConfig();
        setupAUserConfig("Gosho","A",1);//key is A.ki-1
        setupABikeConfig("B.2","bike-1");//bike at station 2, bikeId = bike-2
        setupAUserConfig("Mariika","A",2);
        setupABikeConfig("B.2","bike-1");//bike at station 2, bikeId = bike-2
        input ("2 09:35, KeyReader, B.2.kr, insertKey, A.ki-1");
        expect("2 09:35, BikeLock,  B.2.bl, unlocked");
        expect("2 09:35, OKLight,   B.2.ok, flashed");
        
    }
    @Test
    public void testNoBikeKeyInserted(){
        logger.info("Starting test: testNoBikeKeyInserted");
        setupDemoSystemConfig();
        setupAUserConfig("Gosho","A",1);//key is A.ki-1
        input ("2 09:35, KeyReader, B.2.kr, insertKey, A.ki-1");
        expect("2 09:35, OKLight,   B.2.ok, flashed");
    }
    @Test
    public void testFaultButtonAndFaultLight(){
        logger.info("Starting test: testFaultButtonAndFaultLight");
        setupDemoSystemConfig();
        
        setupABikeConfig("B.2","bike-1");
        setupABikeConfig("A.1","bike-2");
        setupABikeConfig("A.2","bike-3");
        setupABikeConfig("A.3","bike-4");
        
        setupAUserConfig("Tim","B",1);
        
        input ("2 23:30, KeyReader, B.2.kr, insertKey, B.ki-1");
        expect("2 23:30, BikeLock, B.2.bl, unlocked");
        expect("2 23:30, OKLight, B.2.ok, flashed");
        
        input ("2 23:55, BikeSensor, A.4.bs, dockBike, bike-1");
        expect("2 23:55, BikeLock, A.4.bl, locked");
        expect("2 23:55, OKLight, A.4.ok, flashed");
        
        input ("2 23:56, FaultButton, A.4.fb, pressed");
        expect("2 23:56, FaultLight,  A.4.fl, turnedOn");
    }
    @Test
    public void testViewFreeStations(){
        logger.info("Starting test: testFaultButtonAndFaultLight");
        setupDemoSystemConfig();
        
        input ("2 10:00, DSTouchScreen, A.ts, showFreeDockingStations");
        expect("2 10:00, DSTouchScreen, A.ts, viewFreeDockingStations, ordered-tuples, 3," +
        		"DSName, EastPos, NorthPos," +
                "A, 0, 0,"+
        		"B, 400, 300");
    }
    
    @Test
    public void testViewFaultyStations(){
        logger.info("Starting test: testViewFaultyStations");
        setupDemoSystemConfig();
        
        setupABikeConfig("B.2","bike-1");
        setupABikeConfig("A.1","bike-2");
        setupABikeConfig("A.2","bike-3");
        setupABikeConfig("A.3","bike-4");
        
        setupAUserConfig("Tim","B",1);
        
        input ("2 23:30, KeyReader, B.2.kr, insertKey, B.ki-1");
        expect("2 23:30, BikeLock, B.2.bl, unlocked");
        expect("2 23:30, OKLight, B.2.ok, flashed");
        
        input ("2 23:55, BikeSensor, A.4.bs, dockBike, bike-1");
        expect("2 23:55, BikeLock, A.4.bl, locked");
        expect("2 23:55, OKLight, A.4.ok, flashed");
        
        input ("2 23:56, FaultButton, A.4.fb, pressed");
        expect("2 23:56, FaultLight,  A.4.fl, turnedOn");
        
        input("2 23:57, HubTerminal, ht, showFaulty");
        expect("2 23:57, HubTerminal, ht, showFaulty, unordered-tuples, 3,"+ 
               "DStation, East, North,"+
               "       A,    0,     0");
        input("2 23:57, HubTerminal, ht, showStats");
        expect("2 23:57, HubTerminal, ht, showStats, unordered-tuples, 4, "+
               "#Trips, #Users, Total Distance Travelled (m), Average Journey Time (min),"+ 
               "     1,      1,                       500.00,                      25.00");

    }
    @Test
    public void testAddAndRemoveBikeAndAddStaffKey(){
        logger.info("Starting test: testAddAndRemoveBike");
        setupDemoSystemConfig();
        
        setupABikeConfig("A.2", "bike-1");
        
        input ("2 09:06, HubTerminal, ht, issueKey");
        expect("2 09:06, KeyIssuer, CyclOps.Hub.ki, keyIssued, CyclOps.Hub.ki-1");
        
        input ("2 09:07, KeyReader, A.2.kr, insertKey, CyclOps.Hub.ki-1");
        expect("2 09:07, BikeLock, A.2.bl, unlocked");
        expect("2 09:07, OKLight, A.2.ok, flashed");
    }
    /**
     * 
     * Setup demonstration system configuration:
     * 
     * Clock clk ----------------->
     * HubTerminal ht <-----------> Hub  -------->   HubDisplay d
     *                              |   
     *                              |   
     *                              |   
     *                              v
     * DSTouchScreen x.ts <---->  
     * CardReader x.cr <------->  DStation x   -------> KeyIssuer x.ki
     *                          |  x in {A,B}
     *                          |
     *                          |
     *                          v
     * KeyReader x.k.kr ---> DPoint x.k    ------> OKLight x.k.ok
     *                       for x.k in {A.1 ... A.5,
     *                                   B.1 ... B.3}
     *  
     *  This configuration is used in all the demonstration tests.
     *  
     *  It is inserted explicitly into each @Test block rather than the 
     *  @Before block so that alternate configurations can also be set up
     *  in this same test class.
     *   
     */
    public void setupDemoSystemConfig() {
        logger.info("Starting demo configuration...");
        input("1 07:00, HubTerminal, ht, addDStation, A,   0,   0, 5");
        input("1 07:00, HubTerminal, ht, addDStation, B, 400, 300, 3");
    }
    public void setupAUserConfig(String username,String DStation, int keyNum){
        logger.info("Setting up user: " + username);
        input ("2 09:05, DSTouchScreen," + DStation  + ".ts, startReg," + username);
        expect("2 09:05, CardReader, " + DStation  + ".cr, enterCardAndPin");

        input ("2 09:06, CardReader, " + DStation  + ".cr, checkCard," + username+"-card-auth");
        expect("2 09:06, KeyIssuer, " + DStation  + ".ki, keyIssued, " + DStation + ".ki-" + keyNum);

    }
    public void setupABikeConfig(String DPointId, String bikeId){
        input ("2 09:00, BikeSensor," + DPointId + ".bs, dockBike,"+ bikeId);
        expect("2 09:00, BikeLock," + DPointId + ".bl, locked");
        expect("2 09:00, OKLight, "+DPointId  +".ok, flashed");
    }
    
    public void setupATrip(String startDStation,String endDStation){
        
    }
    
    /**
     *  Run the "Register User" use case.
     * 
     */
    @Test
    public void registerUser() {
        logger.info("Starting test: registerUser");

        setupDemoSystemConfig();
        
        // Set up input and expected output.
        // Interleave input and expected output events so that sequence 
        // matches that when describing the use case main success scenario.
        logger.info("registerUser");
        
        input ("2 08:00, DSTouchScreen, A.ts, startReg, Alice");
        expect("2 08:00, CardReader, A.cr, enterCardAndPin");
        input ("2 08:01, CardReader, A.cr, checkCard, Alice-card-auth");
        expect("2 08:01, KeyIssuer, A.ki, keyIssued, A.ki-1");
        
    }
    /**
     *  Run a show high/low occupancy test.
     *  
     *  Display event is scheduled to run only when minutes is multiple of 5,
     *  so only one of the input events should trigger the display. 
     * 
     */
        
    @Test 
    public void showHighLowOccupancy() {
        logger.info("Starting test: showHighLowOccupancy");
        
        setupDemoSystemConfig();

        input ("2 08:00, Clock, clk, tick");
        input ("2 08:01, Clock, clk, tick");
        input ("2 08:02, Clock, clk, tick");
        expect("2 08:00, HubDisplay, hd, viewOccupancy, unordered-tuples, 6,"
             + "DSName, East, North, Status, #Occupied, #DPoints,"
             + "     A,    0,     0,    LOW,         0,        5," 
             + "     B,  400,   300,    LOW,         0,        3");
    }
        
    /**
     * Run a test to demonstrate basic docking point interface
     * functionality.
     * 
     */ 
    @Test
    public void testKeyReaderAndOKLight() {
        logger.info("Starting test: testKeyReaderAndOKLight");
        
        setupDemoSystemConfig();
        
        input ("2 09:30, KeyReader, B.2.kr, insertKey, key-2");
        expect("2 09:30, OKLight,   B.2.ok, flashed");

    }
    
    
    /*
     * 
     * SUPPORT CODE FOR RUNNING TESTS
     * 
     * NOTHING HERE SHOULD NEED TOUCHING
     * 
     * 
     */
     
    /**
     * Utility method for specifying an input event to drive in.
     * 
     * For use in test methods in this class.
     * 
     * @param inputEventString
     */
    private void input(String inputEventString) {
        distributor.enqueue(new Event(inputEventString));
    }
    
    /**
     * Utility method for specifying an expected output event.
     * 
     * For use in test methods in this class.
     * 
     * Relies on test object field expectedOutputEvents for passing
     * argument output event to checking method. 
     * 
     * @param outputEventString
     */
    private void expect(String outputEventString) {
        expectedOutputEvents.add(new Event(outputEventString));
    }
    
    
    /**
     * Queue up input events at event distributor.
     * 
     * Intended for calling from other classes, when input events are
     * read from a file, for example.
     * 
     * @param es input events
     */
    public void enqueueInputEvents(List<Event> es) {
        for (Event e : es) {
            distributor.enqueue(e);
        }
    }
    
    
    /**
     * Set expected output events.  These are compared with actual 
     * output events after a test is run.
     * 
     * Intended for calling from other classes, when input events are
     * read from a file, for example.
     *
     * @param es expected output events
     */
    public void setExpectedOutputEvents(List<Event> es) {
        expectedOutputEvents = es;
    }
    
    
    /**
     * Initialise logging framework so all log records FINER and above
     * are reported.
     * 
     */
    @BeforeClass
    public static void setupLogger() {
         
        // Enable log record filtering at FINER level.
        logger = Logger.getLogger("bikescheme"); 
        logger.setLevel(Level.FINER);
        
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(Level.FINER);
    }
    
    /**
     * Setup test environment and starting system configuration.
     * 
     * Starting system configuration consists of a Hub object and
     * no Docking Station objects.
     * 
     * Suitable for calling directly as well as from JUnit.
     */
    @Before
    public void setupTestEnvAndSystem() {
       
        // Initialise core event framework objects
        
        distributor = new EventDistributor();
        collector = new EventCollector(); 
        
        // Create a hub object with interface devices.
        
                Hub hub = new Hub();
                
        // Connect up hub interface devices to event framework
                
        hub.setDistributor(distributor);
        hub.setCollector(collector);
         
        // Initialise expected output
        
        expectedOutputEvents = new ArrayList<Event>();
    }
    
   
     /**
     * Run test and check results. 
     * 
     * Run this after input events have been loaded into event queue in 
     * event distributor and expected output events have been loaded into
     * expectedOutputEvents field of object this.
     * 
     * If called directly, not via JUnit runner, the AssertionError, thrown
     * when some assertion fails, should be caught.
     */ 
    @After
    public void runAndCheck() {
        List<Event> actualOutputEvents = runTestAndReturnResults();
        checkTestResults(expectedOutputEvents, actualOutputEvents);
    } 
    
    
    /**
     * Inject input events in distributor queue into system and return the
     * resulting output events.
     * 
     * This method can called directly as an alternative to runAndCheck
     * if results want to be seen, but not checked.
     * 
     * @return Output events from test run
     */
    public List<Event> runTestAndReturnResults() {

        distributor.sendEvents();
        List<Event> actualOutputEvents = collector.fetchEvents();
        return actualOutputEvents;
    }
    
    /**
     * Compare expected and actual output events.  
     * 
     * Uses Event.listEqual() to do the comparison.  This not the same as
     * the normal list equality. 
     * 
     * @see Event
     * 
     * @param expectedEvents
     * @param actualEvents
     */
    public void checkTestResults(
            List<Event> expectedEvents,  // Avoid field name expectedOutputEvents
            List<Event> actualEvents) {
            
        // Log output event sequences for easy comparison when different.

        
        StringBuilder sb = new StringBuilder();
        sb.append(LS);
        sb.append("Expected output events:");
        sb.append(LS);
        for (Event e : expectedEvents) {
            sb.append(e);
            sb.append(LS);
        }
        sb.append("Actual output events:");
        sb.append(LS);
        for (Event e : actualEvents) {
            sb.append(e);
            sb.append(LS);
        }
        logger.info(sb.toString());
        
        assertTrue("Expected and actual output events differ",
                Event.listEqual(expectedEvents, actualEvents));
               
    }
}
