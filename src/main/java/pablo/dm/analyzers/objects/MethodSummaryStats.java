package pablo.dm.analyzers.objects;

import pablo.dm.objects.CallGraphNode;

public class MethodSummaryStats implements Comparable<MethodSummaryStats> {
	public String type;
	public String name;
	public String className;
	public String methodName;
	public int lineNumber;
	public boolean loop;
	public int loopCount;
	public int selfTime;
	public int selfTimeWithExitCalls;
	public int childrenTime;
	public int totalTime;
	public int timeSpentInMilliSec;
	public int blockTime;
	public int waitTime;
	public int cpuTime;
	public String tierName;
	public String nodeName;
	
	@Override
	public int compareTo(MethodSummaryStats o) {
		return this.totalTime - o.totalTime;
	}
	
	public void combine(MethodSummaryStats inVal){
		if (name != inVal.name) return;
		this.loopCount=inVal.loopCount;
		this.selfTime=inVal.selfTime;
		this.selfTimeWithExitCalls=inVal.selfTimeWithExitCalls;
		this.childrenTime=inVal.childrenTime;
		this.totalTime=inVal.totalTime;
		this.timeSpentInMilliSec=inVal.timeSpentInMilliSec;
		this.blockTime=inVal.blockTime;
		this.waitTime=inVal.waitTime;
		this.cpuTime=inVal.cpuTime;
	}
	
	public static MethodSummaryStats GenerateFromCallGraphNode(CallGraphNode inVal){
		MethodSummaryStats retVal = new MethodSummaryStats();
		retVal.name=inVal.name;
		retVal.type=inVal.type;
		retVal.className=inVal.className;
		retVal.methodName=inVal.methodName;
		retVal.lineNumber=inVal.lineNumber;
		retVal.loop=inVal.loop;
		retVal.loopCount=inVal.loopCount;
		retVal.selfTime=inVal.selfTime;
		retVal.selfTimeWithExitCalls=inVal.selfTimeWithExitCalls;
		retVal.childrenTime=inVal.childrenTime;
		retVal.totalTime=inVal.totalTime;
		retVal.timeSpentInMilliSec=inVal.timeSpentInMilliSec;
		retVal.blockTime=inVal.blockTime;
		retVal.waitTime=inVal.waitTime;
		retVal.cpuTime=inVal.cpuTime;
		retVal.tierName=inVal.parentCallGraph.parentSegment.applicationComponentName;
		retVal.nodeName=inVal.parentCallGraph.parentSegment.applicationComponentNodeName;
		return retVal;
	}
}
