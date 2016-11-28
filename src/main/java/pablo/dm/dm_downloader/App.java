package pablo.dm.dm_downloader;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import java.io.*;

import pablo.dm.analyzers.MethodTimingAnalyzer;
import pablo.dm.dm_downloader.Controller.*;
import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.objects.*;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.RangeSpecifierBeforeNow;
import pablo.dm.objects.SnapshotSearchCriteria;
import java.time.*;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
	public static BTSnapshot getSnap() throws ConfigurationException, ClassNotFoundException, SnapshotNotFound, IOException{

    	MongoSnapshotSource f = new MongoSnapshotSource();
    	return f.RetrieveSnapshot("51ffff4d-536d-4f21-a47e-cf26f62731ec");
    	
	}
	
    public static void main( String[] args ) throws Exception
    {
    	final Logger log = Logger.getLogger(App.class.getName());
    	Configurations configs = new Configurations();
    	Configuration config = configs.properties(new File("./Resources/config.properties"));
    	
    	ControllerInfo c = new ControllerInfo();
    	c.setAccount(config.getString("controller.account"));
    	c.setUser(config.getString("controller.user"));
    	c.setPass(config.getString("controller.password"));
    	c.setUrl(config.getString("controller.url"));
    	final String path=config.getString("snapshots.path");
    	ControllerClient cC=new ControllerClient(c);
    	try
    	{
    	cC.Authenticate();
    	}
    	catch (Exception e)
    	{}
//
//    	BTSnapshot bt = cC.GetBTSnapshot("ef1770a7-0d0a-44cb-9292-cc489d88df32", true);
//
//    	bt.SerializeToFile(path + bt.guid + ".snapshot" );

//    	SnapshotSearchCriteria c1 = new SnapshotSearchCriteria();
//    	c1.firstInChain=true;
//    	c1.maxRows=50;
//    	c1.applicationIds=new int[]{6};
//    	//c1.rangeSpecifier=rs;
//    	c1.rangeSpecifier=new RangeSpecifierBeforeNow(60);
//    	c1.businessTransactionIds= new int[]{38};
//    	
//    	SnapshotSearchCriteria c2 = new SnapshotSearchCriteria();
//    	c2.firstInChain=true;
//    	c2.maxRows=50;
//    	c2.applicationIds=new int[]{6};
//    	//c1.rangeSpecifier=rs;
//    	ZonedDateTime start =  ZonedDateTime.now().minusMinutes(60);
//    	ZonedDateTime end = ZonedDateTime.now().minusMinutes(10);
//    	c2.rangeSpecifier=new RangeSpecifierBetweenTimes(start, end );
//    	c2.businessTransactionIds= new int[]{38};

    	ControllerSnapshotStore s = new ControllerSnapshotStore(c);
    	MongoSnapshotSource f = new MongoSnapshotSource();

    	//ArrayList<BTSnapshot> snaps = f.SearchFullSnapshots(c2);
    	
//    	BTSnapshot r = s.RetrieveSnapshot("ef1770a7-0d0a-44cb-9292-cc489d88df32");
//    	r = s.RetrieveSnapshot("ef1770a7-0d0a-44cb-9292-cc489d88df3");
    	SnapshotManager sm = new SnapshotManager(s,f);
    	
    	BTSnapshot snap = sm.RetrieveSnapshot("51ffff4d-536d-4f21-a47e-cf26f62731ec");
    	snap.originatingSegment.callGraph.PostProcess();
    	
    	MethodTimingAnalyzer mta = new MethodTimingAnalyzer();
    	mta.AddToAnalysis(snap);
    	mta.analyze();
    	
    	
    	sm.SaveSnapshot(snap);
    	BTSnapshot s2 = sm.RetrieveSnapshot("51ffff4d-536d-4f21-a47e-cf26f62731ec");
    	
    	BTSnapshot bt = cC.GetBTSnapshot("00d61112-32a4-4a3e-b86d-078f74abbca1", true);

    	
    	//sm.DownloadSnapshotsFromRemoteSource(c1);

     }
}
