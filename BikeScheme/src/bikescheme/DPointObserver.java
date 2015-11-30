package bikescheme;
/**
 * Interface observer for a bike being returned 
 * to a DPoint
 * @author ivo
 *
 */
public interface DPointObserver {
    public void disassociateBikeFromUser(String bikeId);
    /**
     * 
     * @param keyId
     * @param bikeId
     */
    public boolean associateBikeToUser(String keyId, String bikeId);
    public void reportBikeFaulty(String bikeId);
}
