package pablo.dm.objects;

import java.lang.reflect.Type;
import java.io.Serializable;

import pablo.dm.dm_downloader.utils.Utils;

public class BaseObject implements Serializable {
	
	public String toJSON()
	{
		return Utils.ObjectToJSON(this);
	}
	public static Object FactoryFromJSON(String json, Type ObjectType){
		Object retVal = Utils.JSONtoObject(json, ObjectType);
		return retVal;
	}
}
