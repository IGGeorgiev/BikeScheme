package bikescheme;
/**
 * Interface observer for when a bike needs
 * to be removed from the user it is
 * associated to.
 * @author ivo
 *
 */
public interface ActionsForBikeAndUserObserver {
    public void returnBike(String bikeId, String endPoint);
    /**
     * 
     * @param keyId
     * @param bikeId
     * @param startPoint
     * @return 
     */
    public boolean addBike(String keyId, String bikeId, String startPoint);
}
