package pablo.dm.objects;

import java.util.HashMap;

public class CallGraph extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2653986127860103677L;
	public CallGraphNode[] roots;
	public HashMap<String,ExitCall> CallGraphExitIDs;
	public BTSegment parentSegment;
	
	private void RecurseAllNodes(CallGraphNode node){
		HashMap<String,ExitCall> tmpMap;
		
		//Put stuff to do for each node here
		node.CalculateTimings();
		node.parentCallGraph=this;
		tmpMap = ExtractExitCalls(node);
		CallGraphExitIDs.putAll(tmpMap);
		if (node.exitCalls != null){
			for (ExitCall ec:node.exitCalls){
				ec.parentNode=node;
			}
		}
		//Finish stuff to do for each node
		if (node.children != null){
			for (CallGraphNode child : node.children){
					RecurseAllNodes(child);
				}
		}
	}
	
	public void PostProcess(){
		CallGraphExitIDs = new HashMap<String,ExitCall>();
		for (CallGraphNode rootNode : roots){
			RecurseAllNodes(rootNode);
		}
	}
	
	private HashMap<String,ExitCall> ExtractExitCalls(CallGraphNode node){
		
		HashMap<String,ExitCall> retVal = new HashMap<String,ExitCall>();
		if (node.exitCalls != null){
			for (ExitCall ec : node.exitCalls){
				retVal.put(ec.snapshotSequenceCounter, ec);
			}
		}
		return retVal;
	}
	
}
