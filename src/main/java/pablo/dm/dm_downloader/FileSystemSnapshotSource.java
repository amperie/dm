package pablo.dm.dm_downloader;

import java.io.IOException;
import java.io.File;

import pablo.dm.objects.BTSnapshot;

public class FileSystemSnapshotSource extends SnapshotSourceBase implements ISnapshotSource {

	public String rootPath=".\\Snapshots\\";
	private final String fileExtension=".snapshot";
	
	public FileSystemSnapshotSource(String path){
		if (path.substring(Math.max(path.length() - 2, 0)) != "\\") path+="\\";
		rootPath=path;
	}
	
	@Override
	public boolean SnapshotExists(String guid) {
		File tmp = new File(rootPath+guid+fileExtension);
		return tmp.exists();
	}

	@Override
	public BTSnapshot RetrieveSnapshot(String guid) throws ClassNotFoundException, IOException {
		return BTSnapshot.FactoryDeserializeFromFile(rootPath+guid+fileExtension);
	}

	@Override
	public void SaveSnapshot(String path, BTSnapshot snapshot) throws IOException {
		snapshot.SerializeToFile(rootPath+snapshot.guid+fileExtension);
	}

}
