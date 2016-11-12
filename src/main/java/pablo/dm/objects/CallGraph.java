package pablo.dm.objects;

import java.util.HashMap;

public class CallGraph extends BaseObject {
	public CallGraphNode[] roots;
	public HashMap<String,ExitCalls> CallGraphExitIDs;
	public BTSegmentDescriptor parentSegment;
	
	private void RecurseAllNodes(CallGraphNode node){
		HashMap<String,ExitCalls> tmpMap;
		
		//Put stuff to do for each node here
		node.CalculateTimings();
		node.parentCallGraph=this;
		tmpMap = ExtractExitSequence(node);
		CallGraphExitIDs.putAll(tmpMap);
		if (node.exitCalls != null){
			for (ExitCalls ec:node.exitCalls){
				ec.parentNode=node;
			}
		}
		//Finish
		if (node.children != null){
			for (CallGraphNode child : node.children){
					RecurseAllNodes(child);
				}
		}
	}
	
	public void PostProcess(){
		CallGraphExitIDs = new HashMap<String,ExitCalls>();
		for (CallGraphNode rootNode : roots){
			RecurseAllNodes(rootNode);
		}
	}
	
	private HashMap<String,ExitCalls> ExtractExitSequence(CallGraphNode node){
		
		HashMap<String,ExitCalls> retVal = new HashMap<String,ExitCalls>();
		if (node.exitCalls != null){
			for (ExitCalls ec : node.exitCalls){
				retVal.put(ec.snapshotSequenceCounter, ec);
			}
		}
		return retVal;
	}
	
}
