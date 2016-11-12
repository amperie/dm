package pablo.dm.objects;

public class SnapshotSearchCriteria extends BaseObject {
	public boolean firstInChain;
	public int maxRows=600;
	public int[] applicationIds;
	public int[] businessTransactionIds;
	public int[] applicationComponentIds;
	public int[] applicationComponentNodeIds;
	public String archived;
	public String badRequest;
	public String[] deepDivePolicy;
	public String diagnosticSnapshot;
	public int[] errorIDs;
	public String errorOccured;
	public int endToEndLatency;
	public int executionTimeInMilis;
	public String[] guids;
	public String sessionId;
	public String url;
	public String userExperience[];
	public String userPrincipalId;
	public boolean needExitCalls;
	public boolean needProps=false;
	public RangeSpecifierBase rangeSpecifier;
}
