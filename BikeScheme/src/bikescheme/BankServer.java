package bikescheme;

import java.util.ArrayList;
import java.util.List;

public class BankServer extends AbstractOutputDevice{
    
    public BankServer(String instanceName){
        super(instanceName);
    }
    
    public void applyCharges(int price, String personalDetails, String cardDetails){
        logger.fine(getInstanceName());
        
        String deviceClass = "BankServer";
        String deviceInstance = getInstanceName();
        String messageName = "applyCharges";
        
        List<String> messageArgs = new ArrayList<String>();
        messageArgs.add("Charged!");
        
        super.sendEvent(
            new Event(
                Clock.getInstance().getDateAndTime(), 
                deviceClass,
                deviceInstance,
                messageName,
                messageArgs));    }

}
