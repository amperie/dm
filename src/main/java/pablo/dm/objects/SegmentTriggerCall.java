package pablo.dm.objects;

public class SegmentTriggerCall extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6058285218868332624L;
	public String type;
	public String detailString;
	public String toComponentId;
	public int timeTakenInMillis;
	public int count;
	public int errorCount;
	public String snapshotSequenceCounter;
	public String callingMethod;
	public String exitPointName;
	public String propertiesAsString;
	public BTSegment parentSegment;
}
