package pablo.dm.dm_downloader;

import java.io.IOException;
import java.util.ArrayList;

import pablo.dm.dm_downloader.Exceptions.ControllerException;
import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.dm_downloader.Exceptions.UnsupportedOperation;
import pablo.dm.objects.*;

public interface ISnapshotStore {
	public boolean SnapshotExists(String guid) throws ControllerException, IOException;
	public BTSnapshot RetrieveSnapshot(String guid)  throws ClassNotFoundException, IOException, SnapshotNotFound, ControllerException;
	public String SaveSnapshot(BTSnapshot snapshot) throws UnsupportedOperation, IOException;
	public BTSegmentList SearchSnapshotSegments(SnapshotSearchCriteria criteria) throws ControllerException, IOException, UnsupportedOperation;
	public ArrayList<BTSnapshot> SearchFullSnapshots(SnapshotSearchCriteria criteria) throws ControllerException, IOException, UnsupportedOperation, ClassNotFoundException, SnapshotNotFound;
}
