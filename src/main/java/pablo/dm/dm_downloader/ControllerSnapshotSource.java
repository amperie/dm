package pablo.dm.dm_downloader;

import java.io.IOException;
import java.util.ArrayList;

import pablo.dm.dm_downloader.Controller.ControllerClient;
import pablo.dm.dm_downloader.Controller.ControllerInfo;
import pablo.dm.dm_downloader.Exceptions.ControllerException;
import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.dm_downloader.Exceptions.UnsupportedOperation;
import pablo.dm.objects.BTSegmentList;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.RangeSpecifierBeforeNow;
import pablo.dm.objects.SnapshotSearchCriteria;

public class ControllerSnapshotSource extends SnapshotSourceBase implements ISnapshotSource {

	private ControllerClient cClient;
	
	public ControllerSnapshotSource(ControllerInfo controllerInf){
    	cClient=new ControllerClient(controllerInf);
    	try
    	{
    	cClient.Authenticate();
    	}
    	catch (Exception e)
    	{}
	}
	@Override
	public boolean SnapshotExists(String guid) throws ControllerException, IOException {
		SnapshotSearchCriteria criteria = new SnapshotSearchCriteria();
		criteria.firstInChain=true;
    	criteria.maxRows=1;
    	criteria.applicationIds=new int[]{};
    	criteria.guids=new String[]{guid};
    	criteria.rangeSpecifier=new RangeSpecifierBeforeNow(20160);
    	
		BTSegmentList tmp = cClient.GetBTSnapshotList(criteria);
		if (tmp.requestSegmentDataListItems.length==0){
			return false;
		}
		return true;
	}

	@Override
	public BTSnapshot RetrieveSnapshot(String guid) throws ClassNotFoundException, IOException, SnapshotNotFound, ControllerException {		
		BTSnapshot retVal = cClient.GetBTSnapshot(guid, true);
		if (retVal.originatingSegment == null) throw new SnapshotNotFound("Snapshot for " + guid + " not found on Controller: " + cClient.toString());
		return retVal;
	}

	@Override
	public String SaveSnapshot(BTSnapshot snapshot) throws UnsupportedOperation, IOException {
		throw new UnsupportedOperation("Saving snapshots not supported in ControllerSnapshotSource");
	}
	
	public BTSnapshot GetBTSnapshot(String guid, boolean LoadCallGraphs) throws ControllerException, IOException{
		return cClient.GetBTSnapshot(guid, LoadCallGraphs);
	}

	public BTSegmentList SearchSnapshotSegments(SnapshotSearchCriteria criteria) throws ControllerException, IOException {
		return cClient.GetBTSnapshotList(criteria);
	}
	@Override
	public ArrayList<BTSnapshot> SearchFullSnapshots(SnapshotSearchCriteria criteria) throws UnsupportedOperation{
		throw new UnsupportedOperation("Searching full snapshots not supported in ControllerSnapshotSource");
	}

}
