package pablo.dm.dm_downloader;

public abstract class SnapshotSourceBase implements ISnapshotSource, Comparable<SnapshotSourceBase> {

	public int priority=1;
	
	public int compareTo(SnapshotSourceBase source1){
		return this.priority - source1.priority;
	}
}
