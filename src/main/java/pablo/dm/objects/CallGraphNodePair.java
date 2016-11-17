package pablo.dm.objects;

public class CallGraphNodePair extends BaseObject implements Comparable<CallGraphNodePair>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7857469793063652562L;
	public CallGraphNode fromNode;
	public CallGraphNode toNode;
	public int PairID;
	public String SequenceCounter;

	public int compareTo(CallGraphNodePair Node1){
		float retVal = Float.parseFloat(this.SequenceCounter.replace("|", ".")) - Float.parseFloat(Node1.SequenceCounter.replace("|", "."));
		retVal = retVal*10000;
		return Math.round(retVal);
	}
}
