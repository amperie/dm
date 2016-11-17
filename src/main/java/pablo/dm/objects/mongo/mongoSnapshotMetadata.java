package pablo.dm.objects.mongo;

import pablo.dm.objects.BaseObject;

public class mongoSnapshotMetadata extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8899845298549773814L;
	public String guid;
	public boolean CallGraphLoaded;
	public String userExperience;
	public boolean errorOcurred;
	public long serverStartTime;
	public int businessTransactionId;
	public int applicationId;
	public int applicationComponentId;
	public int applicationComponentNodeId;
	public String businessTransactionName;
	public String fileSystemPath;
}
