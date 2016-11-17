package pablo.dm.objects;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;

public class BTSnapshot extends BaseObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2015833290541387822L;
	public BTSegment[] segments;
	public String guid;
	public BTSegment originatingSegment;
	public boolean CallGraphLoaded=false;
	public HashMap<String,ExitCall> SnapshotCallGraphExitIDs = new HashMap<String,ExitCall> ();;
	public HashMap<String,SegmentTriggerCall> triggerCalls = new HashMap<String,SegmentTriggerCall>();
	public HashMap<String,BTSegment> segmentMap = new HashMap<String,BTSegment> ();
	public ArrayList<CallGraphNodePair> SnapshotGraph= new ArrayList<CallGraphNodePair>();
	
	public void PostProcess(){
		
		if (segments != null){
			for (BTSegment seg: segments){
				seg.parentSnapshot=this;
				
				//Build call graph exit call map for whole snapshot
				SnapshotCallGraphExitIDs.putAll(seg.CallGraphExitIDs);
				if (seg.triggerCall != null){
					triggerCalls.put(seg.triggerCall.snapshotSequenceCounter,seg.triggerCall);
				}
				
				segmentMap.put(Long.toString(seg.id), seg);
			}
		}
		FindOriginatingSegment();
		CalculateSnapshotGraph();
	}
	
	private void FindOriginatingSegment(){
		for (BTSegment seg:segments){
			if (seg.firstInChain){
				originatingSegment=seg;
				break;
			}
		}
	}
	
	public void CalculateSnapshotGraph(){
		Integer x = 0;
		CallGraphNodePair tmpPair;
		CallGraphNodePair originatingNode = new CallGraphNodePair();
		originatingNode.fromNode=null;
		originatingNode.toNode=this.originatingSegment.callGraph.roots[0];
		originatingNode.PairID=x;
		originatingNode.SequenceCounter="0";
		SnapshotGraph.add(originatingNode);
		
		for (ExitCall ec:SnapshotCallGraphExitIDs.values()){
			if (ec != null){
				x+=1;
				//Find the snapshotSequence that matches the triggerCall to its exitCall
				tmpPair=new CallGraphNodePair();
				tmpPair.fromNode=ec.parentNode;
				if (triggerCalls.containsKey(ec.snapshotSequenceCounter)){
					tmpPair.toNode=triggerCalls.get(ec.snapshotSequenceCounter).parentSegment.callGraph.roots[0];
				} else {
					tmpPair.toNode=null;
				}
				tmpPair.PairID=x;
				tmpPair.SequenceCounter=ec.snapshotSequenceCounter;
				SnapshotGraph.add(tmpPair);
			}
		}
		
//		for (SegmentTriggerCall trigger:triggerCalls.values()){
//			if (trigger != null){
//				x+=1;
//				//Find the snapshotSequence that matches the triggerCall to the right exit call
//				tmpPair=new CallGraphNodePair();
//				tmpPair.toNode=trigger.parentSegment.callGraph.roots[0];
//				tmpPair.fromNode=SnapshotCallGraphExitIDs.get(trigger.snapshotSequenceCounter).parentNode;
//				tmpPair.PairID=x;
//				tmpPair.SequenceCounter=trigger.snapshotSequenceCounter;
//				SnapshotGraph.add(tmpPair);
//			}
//		}
		Collections.sort(SnapshotGraph);
	}
	
	public void SerializeToFile(String path) throws IOException{
		
    	FileOutputStream fout = new FileOutputStream(path);
    	ObjectOutputStream oos = new ObjectOutputStream(fout);
    	oos.writeObject(this);
    	oos.close();
	}
	
	public static BTSnapshot FactoryDeserializeFromFile(String path) throws IOException, ClassNotFoundException, SnapshotNotFound{
    	
        FileInputStream streamIn = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(streamIn);
        BTSnapshot retVal = (BTSnapshot) ois.readObject();
    	ois.close();
    	if (retVal == null) throw new SnapshotNotFound("Path: " + path);
    	return retVal;
	}
}
