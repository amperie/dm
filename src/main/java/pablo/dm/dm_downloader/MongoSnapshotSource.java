package pablo.dm.dm_downloader;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.bson.Document;

import pablo.dm.dm_downloader.Exceptions.ControllerException;
import pablo.dm.dm_downloader.Exceptions.SnapshotNotFound;
import pablo.dm.dm_downloader.Exceptions.UnsupportedOperation;
import pablo.dm.objects.BTSegmentList;
import pablo.dm.objects.BTSnapshot;
import pablo.dm.objects.RangeSpecifierBetweenTimes;
import pablo.dm.objects.SnapshotSearchCriteria;
import pablo.dm.objects.mongo.mongoSnapshotMetadata;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoSnapshotSource extends SnapshotSourceBase implements ISnapshotSource {

	private MongoClient mng;
	private String mngHost;
	private int mngPort;
	private String mngSnapshotDB;
	private String mngSnapshotCollection;
	private MongoDatabase mngDB;
	private MongoCollection mngColl;
	private FileSystemSnapshotSource fs;
	
	public MongoSnapshotSource() throws ConfigurationException{
		super();
		SetConfiguration();
		mng=new MongoClient(mngHost,mngPort);
		mngDB=mng.getDatabase(mngSnapshotDB);
		mngColl=mngDB.getCollection(mngSnapshotCollection);
	}
	
	private void SetConfiguration() throws ConfigurationException{
    	Configurations configs = new Configurations();
    	Configuration config = configs.properties(new File("./Resources/config.properties"));
    	mngHost=config.getString("snapshots.mongoHost");
    	mngSnapshotDB=config.getString("snapshots.mongoSnapshotDB");
    	mngSnapshotCollection=config.getString("snapshots.mongoSnapshotCollection");
    	mngPort=Integer.parseInt(config.getString("snapshots.mongoPort"));
		fs=new FileSystemSnapshotSource(config.getString("snapshots.path"));
	}
	
	@Override
	public boolean SnapshotExists(String guid) throws ControllerException, IOException {
		Document mngDoc = RetrieveMngDocumentByGUID(guid);
		if (mngDoc == null){
			return false;
		} else {
			return true;
		}
	}

	@Override
	public BTSnapshot RetrieveSnapshot(String guid) throws SnapshotNotFound, ClassNotFoundException, IOException {
		Document tmp=RetrieveMngDocumentByGUID(guid);
		if (tmp==null) throw new SnapshotNotFound("Snapshot not in Mongo: " + guid);
		BTSnapshot retVal = BTSnapshot.FactoryDeserializeFromFile(tmp.getString("fileSystemPath"));
		return retVal;
	}

	private Document RetrieveMngDocument(BasicDBObject criteria){
		return (Document) mngColl.find(criteria).first();
	}
	
	private Document RetrieveMngDocumentByGUID(String guid){
		BasicDBObject fields = new BasicDBObject("guid", guid);
		return (Document) mngColl.find(fields).first();
	}

	@Override
	public String SaveSnapshot(BTSnapshot snapshot) throws IOException {
		
		mongoSnapshotMetadata doc=new mongoSnapshotMetadata();
		doc.guid=snapshot.guid;
		doc.CallGraphLoaded=snapshot.CallGraphLoaded;
		doc.userExperience=snapshot.originatingSegment.userExperience;
		doc.errorOcurred=snapshot.originatingSegment.errorOcurred;
		doc.serverStartTime=snapshot.originatingSegment.serverStartTime;
		doc.businessTransactionId=snapshot.originatingSegment.businessTransactionId;
		doc.applicationId=snapshot.originatingSegment.applicationId;
		doc.applicationComponentId=snapshot.originatingSegment.applicationComponentId;
		doc.applicationComponentNodeId=snapshot.originatingSegment.applicationComponentNodeId;
		doc.businessTransactionName=snapshot.originatingSegment.businessTransactionName;
		
		doc.fileSystemPath=fs.SaveSnapshot(snapshot);
		
		Document mngDoc = Document.parse(doc.toJSON());
		mngColl.insertOne(mngDoc);
		return doc.fileSystemPath;
	}

	@Override
	public BTSegmentList SearchSnapshotSegments(SnapshotSearchCriteria criteria) throws UnsupportedOperation {
		throw new UnsupportedOperation("Segment Search not implemented for MongoDB Source");
	}

	@Override
	public ArrayList<BTSnapshot> SearchFullSnapshots(SnapshotSearchCriteria criteria) throws ClassNotFoundException, IOException, SnapshotNotFound {

		ArrayList<BTSnapshot> retVal = new ArrayList<BTSnapshot>();

		BasicDBObject mngCriteria = new BasicDBObject();
		//Add all the stuff from "criteria" to mngCriteria
		if (criteria.guids != null)	mngCriteria.append("guid",criteria.guids[0]);
		if (criteria.userExperience != null) mngCriteria.append("userExperience", criteria.userExperience);
		if (criteria.errorOccured != null)	mngCriteria.append("errorOcurred", criteria.errorOccured);
		if (criteria.businessTransactionIds != null) mngCriteria.append("businessTransactionId", criteria.businessTransactionIds[0]);
		if (criteria.applicationIds != null) mngCriteria.append("applicationId", criteria.applicationIds[0]);
		if (criteria.applicationComponentIds != null) mngCriteria.append("applicationComponentId", criteria.applicationComponentIds[0]);
		if (criteria.applicationComponentNodeIds != null) mngCriteria.append("applicationComponentNodeId", criteria.applicationComponentNodeIds[0]);
		if (criteria.rangeSpecifier != null){
			if (criteria.rangeSpecifier.type=="BEFORE_NOW"){
				ZonedDateTime timeNow = ZonedDateTime.now(ZoneId.of("UTC"));
				mngCriteria.append("serverStartTime",new BasicDBObject("$gt", 
						(timeNow.toInstant().toEpochMilli()-
						criteria.rangeSpecifier.durationInMinutes*60*1000)));
			} else {
				RangeSpecifierBetweenTimes rs = (RangeSpecifierBetweenTimes)criteria.rangeSpecifier;
				ZonedDateTime start = ZonedDateTime.parse(rs.startTime);
				ZonedDateTime end = ZonedDateTime.parse(rs.endTime);

				mngCriteria.append("serverStartTime",new BasicDBObject("$gt", 
						(start.toInstant().toEpochMilli())).append("$lt", 
						(end.toInstant().toEpochMilli())));
				//mngCriteria.append("serverStartTime",new BasicDBObject("$lt", 
				//		(end.toInstant().toEpochMilli())));	
			}
		}
		
		
		FindIterable<Document> curs = mngColl.find(mngCriteria);
		MongoCursor<Document> iter = curs.iterator();
		while (iter.hasNext()){
			retVal.add(BTSnapshot.FactoryDeserializeFromFile(iter.next().getString("fileSystemPath")));
		}

		return retVal;
	}
}
