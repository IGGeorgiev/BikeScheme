package bikescheme;

public class FaultButton extends AbstractInputDevice {
    public FaultButton(String instance) { 
        super(instance);
    }

    private FaultButtonObserver observer;
    public void setFaultButtonObserver(FaultButtonObserver o){
        this.observer = o;
    }
}
