package pablo.dm.objects;

public class LockArray extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4998345081450140588L;
	public String threadOwner;
	public String lockName;
	public long startTime;
	public String className;
	public String methodName;
	public int lineNumber;
	public long endTime;
}
