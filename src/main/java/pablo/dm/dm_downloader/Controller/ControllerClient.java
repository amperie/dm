package pablo.dm.dm_downloader.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import pablo.dm.dm_downloader.App;
import pablo.dm.dm_downloader.Controller.Exceptions.ControllerAuthException;
import pablo.dm.dm_downloader.Controller.Exceptions.ControllerException;
import pablo.dm.dm_downloader.utils.Utils;
import pablo.dm.objects.*;

public class ControllerClient {

	private ControllerInfo cInfo;
	private HttpClient client;
	private String AuthHeader;
	private String JSESSIONID;
	private Header SessionIDHeader;
	private final String AuthURI="/controller/auth?action=login";
	private final String BTSnapListURI="/controller/restui/snapshot/snapshotListDataWithFilterHandle";
	private final String SegmentDetailsURI="/controller/restui/snapshot/getRequestSegmentById";
	private final String SegmentCallGraphURI="/controller/restui/snapshot/getCallGraphRoot";
	private final String AllSegmentsForGuidURI="/controller/restui/snapshot/getFilteredRSDListData";
	private final Logger log = Logger.getLogger(ControllerClient.class.getName());
	
	public ControllerClient(ControllerInfo ControllerIn)
	{
		cInfo=ControllerIn;
		AuthHeader="Basic " + Utils.EncodeStringBase64(cInfo.getUser() + "@" + cInfo.getAccount() + ":" + cInfo.getPass() );
		client = HttpClientBuilder.create().build();
	}
	
	public void Authenticate () throws IOException, ControllerException
	{
		ControllerResponse resp = Call(cInfo.getUrl() + AuthURI,null,"GET","");
		if (resp.returnCode==200) return;
		SessionIDHeader=resp.SessionIDHeader;
		JSESSIONID=SessionIDHeader.getValue();
		resp = Call(cInfo.getUrl() + AuthURI,null,"GET","");
		if (resp.returnCode!=200) 
			{throw new ControllerAuthException("Return code on Authentication: " + resp.returnCode );}
	}

	public String Get(String URL, String ContentType) throws IOException, ControllerException
	{
		return Call(URL,null,"GET",ContentType).body;
	}
	
	public String Post(String URL, String Data, String ContentType) throws IOException, ControllerException
	{
		return Call(URL,Data,"POST",ContentType).body;
	}

	private ControllerResponse Call(String URL, String Data, String HttpMethod, String ContentType) throws IOException, ControllerException
	{
		ControllerResponse retVal = new ControllerResponse();
		HttpResponse resp;
		HttpRequestBase req;
		switch (HttpMethod){
		case "GET":
			req=new HttpGet(URL);
			break;
		case "POST":
			HttpPost reqp=new HttpPost(URL);
			StringEntity ent = new StringEntity(Data);
			reqp.setEntity(ent);
			req=reqp;
			break;
		default:
			throw new ControllerException("Unsupported Method: " + HttpMethod);
		} 
		
		req.addHeader("Authorization",AuthHeader);
		req.addHeader("Accept","application/json, text/plain, */*");
		req.addHeader("Content-Type", ContentType);
		if (SessionIDHeader != null){
			req.addHeader("Cookie", JSESSIONID);
		}
		resp=client.execute(req);
		if (resp.getStatusLine().getStatusCode()==401 && !URL.contains(AuthURI)) 
		{
			Authenticate();
			resp=client.execute(req);
		}
		retVal.body=ResponseToString(resp);
		retVal.returnCode=resp.getStatusLine().getStatusCode();
		retVal.returnMessage=resp.getStatusLine().getReasonPhrase();
		retVal.Headers=resp.getAllHeaders();
		retVal.SessionIDHeader=resp.getFirstHeader("Set-Cookie");
		req.releaseConnection();
		return retVal;
	}
	
	private String ResponseToString (HttpResponse resp) throws IOException
	{
		BufferedReader rd = new BufferedReader(
				new InputStreamReader(resp.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		return result.toString();
	}
	
//Methods That Pull JSON

	public BTSegmentList GetBTSnapshotList(SnapshotSearchCriteria criteria) throws ControllerException, IOException
	{
		String url = this.cInfo.getUrl()+this.BTSnapListURI;
		String json = Post(url,criteria.toJSON(),"application/json;charset=UTF-8");
		log.trace("Segment List for URL: " + url + "::::::" + json);
		BTSegmentList retVal = (BTSegmentList)BTSegmentList.FactoryFromJSON(json, BTSegmentList.class);
		return retVal;
	}

	public BTSegmentDescriptor[] GetDetailedSegmentList(SnapshotSearchCriteria criteria, boolean GetCallGraph) throws ControllerException, IOException
	{
		String url = this.cInfo.getUrl()+this.AllSegmentsForGuidURI;
		String json = Post(url,criteria.toJSON(),"application/json;charset=UTF-8");
		log.trace("Detailed Segment List for URL: " + url + "::::::" + json);
		BTSegmentDescriptor[] retVal = (BTSegmentDescriptor[])BTSegmentList.FactoryFromJSON(json, BTSegmentDescriptor[].class);
		if (retVal != null){
			for (BTSegmentDescriptor seg:retVal){
				if (GetCallGraph){
					seg.callGraph=GetSegmentCallGraph(seg.id,seg.serverStartTime);
				}
				seg.PostProcess();
			}
		}
		return retVal;
	}
	
	public BTSegmentDescriptor GetSegment(long rsdId, long timestamp, boolean GetCallGraph) throws IOException, ControllerException{
		String url = this.cInfo.getUrl()+ SegmentDetailsURI;
		url += "?rsdId=" + rsdId + "&timeRange=Custom_Time_Range.BETWEEN_TIMES."
				+ (timestamp) + "." + (timestamp-3600000) + ".60";
		String json = Get(url,"application/json;charset=UTF-8");
		log.trace("Segment Details for URL: " + url + "::::::" + json);
		BTSegmentDescriptor retVal = (BTSegmentDescriptor)BTSegmentDescriptor.FactoryFromJSON(json, BTSegmentDescriptor.class);
		if (GetCallGraph){
			retVal.callGraph = GetSegmentCallGraph(rsdId,timestamp);
		}
		retVal.PostProcess();
		return retVal;
	}
	
	public CallGraph GetSegmentCallGraph(long rsdId, long timestamp) throws ControllerException, IOException
	{
		String url = this.cInfo.getUrl()+this.SegmentCallGraphURI;
		url += "?rsdId=" + rsdId + "&timeRange=Custom_Time_Range.BETWEEN_TIMES."
				+ (timestamp) + "." + (timestamp-3600000) + ".60";
		String json = Get(url,"application/json;charset=UTF-8");
		log.trace("Segment Call Graph for URL: " + url + "::::::" + json);
		
		CallGraph retVal = new CallGraph();
		retVal.roots = (CallGraphNode[])Utils.JSONtoObject(json, CallGraphNode[].class);
		retVal.PostProcess();
		
		return retVal;
	}
	
	public BTSnapshot GetBTSnapshot(String guid, boolean LoadCallGraphs) throws ControllerException, IOException{
		log.trace("GettingBTSnapshot for " + guid);
		BTSnapshot retVal = new BTSnapshot();
		retVal.guid=guid;
		SnapshotSearchCriteria criteria = new SnapshotSearchCriteria();
		criteria.firstInChain=false;
		criteria.guids=new String[]{guid};
		criteria.needExitCalls=true;
		retVal.segments=GetDetailedSegmentList(criteria,LoadCallGraphs);
		retVal.CallGraphLoaded=LoadCallGraphs;
		retVal.PostProcess();
		return retVal;
	}
	
	
}
