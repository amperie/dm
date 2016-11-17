package pablo.dm.dm_downloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import pablo.dm.dm_downloader.Exceptions.ControllerException;
import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.dm_downloader.Exceptions.UnsupportedOperation;
import pablo.dm.objects.*;

public class SnapshotManager implements Runnable {

	final Logger log = Logger.getLogger(App.class.getName());
	private final int THREADPOOL_SIZE=5;
	private ArrayList<SnapshotSourceBase> snapshotLocalSourceChain = new ArrayList<SnapshotSourceBase>();
	private ConcurrentHashMap<String,BTSnapshot> snapshotMap = new ConcurrentHashMap<String,BTSnapshot>();
	private SnapshotSourceBase remoteReadSource=null;
	private SnapshotSourceBase primaryWriteSource=null;
	public int NumberOfSnapshotsToKeepInMemory=-1; //-1 means keep all
	ExecutorService executor = Executors.newFixedThreadPool(THREADPOOL_SIZE);
	private ConcurrentLinkedQueue<BTSegment> retrievalQueue = new ConcurrentLinkedQueue<BTSegment>();


	public SnapshotManager (SnapshotSourceBase primaryReadSource, SnapshotSourceBase primaryWriteSource){
		this.remoteReadSource=primaryReadSource;
		this.primaryWriteSource=primaryWriteSource;
	}
	
	public ArrayList<SnapshotSourceBase> getSnapshotSourceChain() {
		return snapshotLocalSourceChain;
	}

	public void AddSnapshotSource(SnapshotSourceBase source){
		snapshotLocalSourceChain.add(source);
		Collections.sort(snapshotLocalSourceChain);
	}
	
	private void AddSnapshotToDictionary(String guid, BTSnapshot snap){
		if (!snapshotMap.containsKey(snap.guid) && 
				(this.NumberOfSnapshotsToKeepInMemory == -1 || snapshotMap.mappingCount() < NumberOfSnapshotsToKeepInMemory)){
			snapshotMap.put(snap.guid, snap);
		}
	}
	
	public void ClearSnapshots() {
		snapshotMap = new ConcurrentHashMap<String,BTSnapshot>();
		retrievalQueue = new ConcurrentLinkedQueue<BTSegment>();
	}
	
	public void DownloadSnapshotsFromRemoteSource(SnapshotSearchCriteria criteria) throws UnsupportedOperation, IOException, ControllerException, ClassNotFoundException, SnapshotNotFound, InterruptedException{
		if (remoteReadSource==null) throw new UnsupportedOperation ("Snapshot Manager doesn't have a remote read source to download from");
		
		BTSegmentList searchRes = remoteReadSource.SearchSnapshotSegments(criteria);
		for (BTSegment seg:searchRes.requestSegmentDataListItems){
			//Check if the snapshot is already in memory or stored locally
			if (!snapshotMap.containsKey(seg.requestGUID) && !SnapshotExistsLocally(seg.requestGUID)){
				retrievalQueue.add(seg);
			}
		}
        for (int i = 0; i < THREADPOOL_SIZE; i++) {
            Runnable worker = this;
            executor.execute(worker);
          }
        executor.shutdown();
        
        while (!executor.isTerminated()) {
        	Thread.sleep(100);
        }
	}
	
	public boolean SnapshotExistsLocally(String guid) throws ControllerException, IOException {
		//First check the Source chain
		for (SnapshotSourceBase source:snapshotLocalSourceChain){
			if (source.SnapshotExists(guid)) return true; 
		}
		//Then check the primary write source
		if (primaryWriteSource.SnapshotExists(guid)) return true;
		
		return false;
	}
	
	public boolean SnapshotExistsRemotely(String guid) throws ControllerException, IOException {

		if (remoteReadSource.SnapshotExists(guid)) return true;
		
		return false;
	}

	public BTSnapshot RetrieveSnapshot(String guid) throws ClassNotFoundException, IOException, SnapshotNotFound, ControllerException {
		BTSnapshot retVal = null;
		//check the Local write source
		if (primaryWriteSource.SnapshotExists(guid)) {
			retVal=primaryWriteSource.RetrieveSnapshot(guid);
			if (!snapshotMap.containsKey(guid)) AddSnapshotToDictionary(guid, retVal);
			return retVal;
		}
		//check the Source chain
		for (SnapshotSourceBase source:snapshotLocalSourceChain){
			if (source.SnapshotExists(guid)) {
				try {
					retVal = source.RetrieveSnapshot(guid);
					if (!snapshotMap.containsKey(guid)) AddSnapshotToDictionary(guid, retVal);
					break;
				} catch (SnapshotNotFound e) {
					retVal = null;
				}
			}
		}
		//Then check the remote read source
		if (remoteReadSource.SnapshotExists(guid)) {
			retVal=remoteReadSource.RetrieveSnapshot(guid);
			if (!snapshotMap.containsKey(guid)) AddSnapshotToDictionary(guid, retVal);
			return retVal;
		}
		return retVal;
	}

	public void SaveSnapshot(BTSnapshot snapshot) throws IOException, ControllerException, UnsupportedOperation {
		primaryWriteSource.SaveSnapshot(snapshot);
	}

	@Override
	public void run() {

		BTSnapshot tmp;
		BTSegment desc=this.retrievalQueue.poll();
		while (desc != null){
			try {
				tmp = remoteReadSource.RetrieveSnapshot(desc.requestGUID);
				if (!snapshotMap.containsKey(desc.requestGUID))
					AddSnapshotToDictionary(desc.requestGUID, tmp);
				primaryWriteSource.SaveSnapshot(tmp);
				desc = this.retrievalQueue.poll();
			} catch (Exception e) {
				log.error("Error downloading snapshot: " + desc.requestGUID + ":::::" + e.getStackTrace().toString());
			}
		}
	}
	
}
