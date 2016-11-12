package pablo.dm.objects;

public class ExitCalls extends BaseObject {
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
}
