package pablo.dm.objects;

public class ExitCall extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2899981384942399297L;
	public String type;
	public int customExitPointId;
	public String detailString;
	public ExitCallProperty[] properties;
	public String toComponentId;
	public int timeTakenInMillis;
	public int count;
	public int errorCount;
	public String errorDetails;
	public String snapshotSequenceCounter;
	public String callingMethod;
	public String exitPointName;
	public int timestamp;
	public boolean customActivity;
	public CallGraphNode parentNode;
	public CallGraphNode triggeredNode;
}
