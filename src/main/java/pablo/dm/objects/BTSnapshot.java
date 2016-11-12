package pablo.dm.objects;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class BTSnapshot extends BaseObject {
	public BTSegmentDescriptor[] segments;
	public String guid;
	public BTSegmentDescriptor originatingSegment;
	public boolean CallGraphLoaded=false;
	public HashMap<String,ExitCalls> SnapshotCallGraphExitIDs;
	
	public void PostProcess(){
		
		SnapshotCallGraphExitIDs = new HashMap<String,ExitCalls> ();
		
		if (segments != null){
			for (BTSegmentDescriptor seg: segments){
				seg.parentSnapshot=this;
				
				//Build call graph exit call map for whole snapshot
				SnapshotCallGraphExitIDs.putAll(seg.CallGraphExitIDs);
			}
		}
		FindOriginatingSegment();
	}
	
	private void FindOriginatingSegment(){
		for (BTSegmentDescriptor seg:segments){
			if (seg.firstInChain){
				originatingSegment=seg;
				break;
			}
		}
	}
	
	public void SerializeToFile(String path) throws IOException{
		
    	FileOutputStream fout = new FileOutputStream(path);
    	ObjectOutputStream oos = new ObjectOutputStream(fout);
    	oos.writeObject(this);
    	oos.close();
	}
	
	public static BTSnapshot FactoryDeserializeFromFile(String path) throws IOException, ClassNotFoundException{
    	
        FileInputStream streamIn = new FileInputStream(path);
        ObjectInputStream ois = new ObjectInputStream(streamIn);
        BTSnapshot retVal = (BTSnapshot) ois.readObject();
    	ois.close();
    	return retVal;
	}
}
