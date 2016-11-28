package pablo.dm.dm_downloader;

public abstract class SnapshotStoreBase implements ISnapshotStore, Comparable<SnapshotStoreBase> {

	public int priority=1;
	
	public int compareTo(SnapshotStoreBase source1){
		return this.priority - source1.priority;
	}
}
