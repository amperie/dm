package pablo.dm.analyzers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import pablo.dm.analyzers.objects.MethodSummaryStats;
import pablo.dm.objects.BTSegment;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.CallGraph;
import pablo.dm.objects.CallGraphNode;
import pablo.dm.objects.ExitCall;

public class MethodTimingAnalyzer extends BaseAnalyzer implements IAnalyzer {

	private ConcurrentHashMap<String, ConcurrentHashMap<String,MethodSummaryStats>> TierMap;
	private ConcurrentHashMap<String, MethodSummaryStats> OverallMethodMap;
	private ConcurrentLinkedQueue<BTSnapshot> snapshotsToAnalyze;
	
	public MethodTimingAnalyzer(){
		TierMap = new ConcurrentHashMap<String, ConcurrentHashMap<String,MethodSummaryStats>>();
		OverallMethodMap = new ConcurrentHashMap<String, MethodSummaryStats>() ;
		snapshotsToAnalyze = new ConcurrentLinkedQueue<BTSnapshot>();
	}
	
	@Override
	public void analyze() {
		// TODO Make this multithreaded
		ConcurrentHashMap<String, MethodSummaryStats> temp = new  ConcurrentHashMap<String, MethodSummaryStats> ();
		BTSnapshot snap = snapshotsToAnalyze.poll();
		
		while (snap != null){
		//for (BTSnapshot snap:snapshotsToAnalyze){
			for (BTSegment seg:snap.segments){
				//Analyze the current segment
				temp=AnalyzeCallGraph(seg.callGraph);
				String tier = seg.applicationComponentName;
				//This is for updating the Tier Map
				if (TierMap.containsKey(tier)){
					TierMap.put(tier,
							CombineSummaryMaps(TierMap.get(tier), temp));
				} else {
					TierMap.put(tier, temp);
				}
				//This is for updating the overall map
				OverallMethodMap = CombineSummaryMaps(OverallMethodMap,temp);
			}
			snap = snapshotsToAnalyze.poll();
		}
		
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void AddToAnalysis(Object objToAdd) {
		// TODO Auto-generated method stub
		BTSnapshot snap = (BTSnapshot)objToAdd;
		snapshotsToAnalyze.add(snap);
	}
	
	private ConcurrentHashMap<String, MethodSummaryStats> AnalyzeCallGraph(CallGraph cg){
		
		return RecurseAllNodes(cg.roots[0]);
		
	}
	
	private ConcurrentHashMap<String, MethodSummaryStats> RecurseAllNodes(CallGraphNode node){

		ConcurrentHashMap<String, MethodSummaryStats> retVal = new ConcurrentHashMap<String, MethodSummaryStats>();
		
		MethodSummaryStats thisNodeStats=MethodSummaryStats.GenerateFromCallGraphNode(node);
		PutSummaryInMap(retVal,thisNodeStats);

		if (node.children != null){
			for (CallGraphNode child : node.children){
				ConcurrentHashMap<String, MethodSummaryStats> childrenMap =
						RecurseAllNodes(child);
				retVal = CombineSummaryMaps(retVal,childrenMap);
				}
		}
		return retVal;
	}
	
	private ConcurrentHashMap<String, MethodSummaryStats> CombineSummaryMaps (
			ConcurrentHashMap<String, MethodSummaryStats> left,
			ConcurrentHashMap<String, MethodSummaryStats> right){
		ConcurrentHashMap<String, MethodSummaryStats> retVal = new ConcurrentHashMap<String, MethodSummaryStats> ();
		retVal.putAll(left);
		for (String key:right.keySet()){
			PutSummaryInMap(retVal,right.get(key));
		}
		return retVal;
	}
	
	private void PutSummaryInMap (ConcurrentHashMap<String, MethodSummaryStats> mapIn,
					MethodSummaryStats stats){
		if (mapIn.containsKey(stats.name)){
			mapIn.get(stats.name).combine(stats);
		} else {
			mapIn.put(stats.name, stats);
		}
	}

}
