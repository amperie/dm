package pablo.dm.objects;

import java.util.HashMap;

public class BTSegmentDescriptor extends BaseObject {
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
	public HashMap<String,ExitCalls> CallGraphExitIDs;
	public ExitCalls[] exitCalls;
	public BTSnapshot parentSnapshot;
	
	public void PostProcess(){
		if (isCallGraphLoaded()) {
			CallGraphExitIDs=callGraph.CallGraphExitIDs;
			callGraph.parentSegment=this;
		}
	}
	
	public boolean isCallGraphLoaded(){
		return (callGraph != null);
	}
}
