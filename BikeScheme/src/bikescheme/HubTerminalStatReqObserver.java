package bikescheme;

import java.util.List;

public interface HubTerminalStatReqObserver {
    public List<String> populateFaultyDStationList();
    public List<String> populateStatsList();
}
