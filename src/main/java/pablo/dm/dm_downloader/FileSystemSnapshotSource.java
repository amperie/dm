package pablo.dm.dm_downloader;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.dm_downloader.Exceptions.UnsupportedOperation;
import pablo.dm.objects.BTSegmentList;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.SnapshotSearchCriteria;

public class FileSystemSnapshotSource extends SnapshotSourceBase implements ISnapshotSource {

	public String rootPath="./Snapshots/";
	private final String fileExtension=".snapshot";
	
	public FileSystemSnapshotSource() throws ConfigurationException{
		super();
		SetConfiguration();
	}
	
	public FileSystemSnapshotSource(String dataDirectory){
		super();
		if (dataDirectory.substring(Math.max(dataDirectory.length() - 1, 0)) != "/") dataDirectory+="/";
		rootPath=dataDirectory;
	}
	
	private void SetConfiguration() throws ConfigurationException{
    	Configurations configs = new Configurations();
    	Configuration config = configs.properties(new File("./Resources/config.properties"));
    	rootPath=config.getString("snapshots.path");
	}
	
	@Override
	public boolean SnapshotExists(String guid) {
		File tmp = new File(rootPath+guid+fileExtension);
		return tmp.exists();
	}

	@Override
	public BTSnapshot RetrieveSnapshot(String guid) throws ClassNotFoundException, IOException, SnapshotNotFound {
		if (SnapshotExists(guid)) return BTSnapshot.FactoryDeserializeFromFile(rootPath+guid+fileExtension);
		throw new SnapshotNotFound("Snapshot for " + guid + " not found in path: " + rootPath);
	}

	@Override
	public String SaveSnapshot(BTSnapshot snapshot) throws IOException {
		snapshot.SerializeToFile(rootPath+snapshot.guid+fileExtension);
		return rootPath+snapshot.guid+fileExtension;
	}

	public BTSegmentList SearchSnapshotSegments(SnapshotSearchCriteria criteria) throws UnsupportedOperation {
		throw new UnsupportedOperation("Snapshot Search not implemented for File System Source");
	}

	@Override
	public ArrayList<BTSnapshot> SearchFullSnapshots(SnapshotSearchCriteria criteria) throws UnsupportedOperation{
		throw new UnsupportedOperation("Snapshot Search not implemented for File System Source");
	}

}
