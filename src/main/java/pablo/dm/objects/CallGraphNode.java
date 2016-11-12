package pablo.dm.objects;

public class CallGraphNode extends BaseObject {
	public String type;
	public String name;
	public String[] names;
	public String className;
	public String methodName;
	public int lineNumber;
	public boolean loop;
	public int loopCount;
	public int selfTime;
	public int childrenTime;
	public int totalTime;
	public int timeSpentInMilliSec;
	public String properties;
	public CallGraphNode[] children;
	public ExitCalls[] exitCalls;
	public int blockTime;
	public int waitTime;
	public int[] serviceEndPointIds;
	public LockArray[] lockArray;
	public int cpuTime;
	public CallGraph parentCallGraph;
	
	public void CalculateTimings(){
		childrenTime=0;
		totalTime=timeSpentInMilliSec;
		if (children != null){
			for (CallGraphNode child : children){
				childrenTime += child.timeSpentInMilliSec;
			}
		}
		selfTime = totalTime - childrenTime;
	}
	
}
