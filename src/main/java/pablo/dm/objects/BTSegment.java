package pablo.dm.objects;

import java.util.HashMap;

public class BTSegment extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7008644935573834106L;
	public boolean hasNetworkSnapshot;
	public long id;
	public String userExperience;
	public String endToEndUserExperience;
	public boolean errorOcurred;
	public boolean fullCallgraph;
	public boolean hotSpotCallGraph;
	public boolean delayedCallGraph;
	public long timeTakenInMilliSecs;
	public long endToEndLatency;
	public String url;
	public long serverStartTime;
	public boolean archived;
	public String requestGUID;
	public int businessTransactionId;
	public int applicationId;
	public int applicationComponentId;
	public int applicationComponentNodeId;
	public String businessTransactionName;
	public String applicationComponentName;
	public String applicationComponentNodeName;
	public boolean firstInChain;
	public String diagnosticSessionGUID;
	public String summary;
	public SegmentTriggerCall triggerCall;
	public CallGraph callGraph;
	public HashMap<String,ExitCall> CallGraphExitIDs;
	public ExitCall[] exitCalls;
	public BTSnapshot parentSnapshot;
	
	public void PostProcess(){
		if (isCallGraphLoaded()) {
			CallGraphExitIDs=callGraph.CallGraphExitIDs;
			callGraph.parentSegment=this;
		}
		if (triggerCall!=null)	triggerCall.parentSegment=this;
	}
	
	public boolean isCallGraphLoaded(){
		return (callGraph != null);
	}
}
