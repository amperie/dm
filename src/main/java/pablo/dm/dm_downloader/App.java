package pablo.dm.dm_downloader;

import org.apache.log4j.Logger;
import java.io.*;

import pablo.dm.dm_downloader.Controller.*;
import pablo.dm.objects.*;
import pablo.dm.objects.BTSegmentList;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.CallGraph;
import pablo.dm.objects.CallGraphNode;
import pablo.dm.objects.RangeSpecifierBeforeNow;
import pablo.dm.objects.SnapshotSearchCriteria;
import pablo.dm.dm_downloader.utils.*;
import java.time.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	final Logger log = Logger.getLogger(App.class.getName());
    	final String path=".\\Snapshots\\";

    	ZonedDateTime t = ZonedDateTime.now().minusDays(1);
    	ZonedDateTime t2=t.minusDays(1);
    	RangeSpecifierBetweenTimes rs = new RangeSpecifierBetweenTimes(t2,t);
    	String j = rs.toJSON();
    	
    	SnapshotSearchCriteria c1 = new SnapshotSearchCriteria();
    	c1.firstInChain=true;
    	c1.maxRows=10;
    	c1.applicationIds=new int[]{6};
    	c1.rangeSpecifier=rs;
    	//c1.rangeSpecifier=new RangeSpecifierBeforeNow(1440);
    	System.out.println(c1.toJSON());
    	
    	
    	ControllerInfo c = new ControllerInfo();
    	c.setAccount("customer1");
    	c.setUser("pablo");
    	c.setPass("Teto4958");
    	c.setUrl("http://dev.demo.appdynamics.com");
    	ControllerClient cC=new ControllerClient(c);
    	try
    	{
    	cC.Authenticate();
    	}
    	catch (Exception e)
    	{}
    	
    	BTSegmentList list = cC.GetBTSnapshotList(c1);
    	
    	BTSnapshot bt = cC.GetBTSnapshot("ef1770a7-0d0a-44cb-9292-cc489d88df32", true);

    	bt.SerializeToFile(path + bt.guid + ".ser" );
    	
    	BTSnapshot bt2 = BTSnapshot.FactoryDeserializeFromFile(path + bt.guid + ".ser");
    	bt2.SerializeToFile(path + "bt2");
        
    	
    	String URL = "https://paypalnodejs1.saas.appdynamics.com/controller/restui/snapshot/snapshotListDataWithFilterHandle";
    	//String Data = c1.toJSON();
    	//String res=cC.Post(URL, Data,"application/json;charset=UTF-8");
    	
    	//System.out.println(Data);
    	//System.out.println(res);
    	
    	//System.out.println( cC.Get("https://paypalnodejs1.saas.appdynamics.com/controller/restui/home/getAllAppPerformanceViewData?time-range=last_15_minutes.BEFORE_NOW.-1.-1.15&filter=incidentSummary"));
     }
}
