package bikescheme;

import java.util.List;

public interface UserActivitiesObserver {
    public boolean addUser(String keyId, String personalDetails, String cardDetails);
    public List<String> viewActivityReceived(String keyId);
    public List<String> sendFreeStations();
}
