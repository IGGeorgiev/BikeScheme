package bikescheme;
/**
 * Interface observer for when a bike needs
 * to be removed from the user it is
 * associated to.
 * @author ivo
 *
 */
public interface ActionsForBikeAndUserObserver {
    public void returnOrAddBike(String bikeId, String endPoint);
    public boolean hireOrRemoveBike(String keyId, String bikeId, String startPoint);
    public void reportBikeFaulty(String bikeId);
}
