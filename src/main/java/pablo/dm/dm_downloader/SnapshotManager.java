package pablo.dm.dm_downloader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pablo.dm.objects.*;

public class SnapshotManager {

	private ArrayList<SnapshotSourceBase> snapshotSourceChain = new ArrayList<SnapshotSourceBase>();
	private HashMap<String,BTSnapshot> snapshotMap = new HashMap<String,BTSnapshot>();

	public ArrayList<SnapshotSourceBase> getSnapshotSourceChain() {
		return snapshotSourceChain;
	}

	public void AddSnapshotSource(SnapshotSourceBase source){
		snapshotSourceChain.add(source);
	}
	
	public void DownloadSnapshots(SnapshotSearchCriteria criteria){
		
	}
	
	public boolean SnapshotExists(String guid) {
		for (SnapshotSourceBase source:snapshotSourceChain){
			if (source.SnapshotExists(guid)) return true; 
		}
		return false;
	}

	public BTSnapshot RetrieveSnapshot(String guid) throws ClassNotFoundException, IOException {
		for (SnapshotSourceBase source:snapshotSourceChain){
			if (source.SnapshotExists(guid)) return source.RetrieveSnapshot(guid); 
		}
		return null;
	}

	public boolean SaveSnapshot(String path, BTSnapshot snapshot) throws IOException {
		for (SnapshotSourceBase source:snapshotSourceChain){
			if (!source.SnapshotExists(snapshot.guid)) {
				source.SaveSnapshot(snapshot.guid, snapshot);
				return true;
			}; 
		}
		return false;
	}
	
}
