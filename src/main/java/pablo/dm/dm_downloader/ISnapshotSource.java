package pablo.dm.dm_downloader;

import java.io.IOException;

import pablo.dm.objects.*;

public interface ISnapshotSource {
	public boolean SnapshotExists(String guid);
	public BTSnapshot RetrieveSnapshot(String guid)  throws ClassNotFoundException, IOException;
	public void SaveSnapshot(String path, BTSnapshot snapshot) throws IOException;
}
