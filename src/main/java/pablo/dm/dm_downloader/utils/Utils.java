package pablo.dm.dm_downloader.utils;

import java.lang.reflect.Type;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	public static String EncodeStringBase64 (String StrIn)
	{
		byte[] bytesEncoded = Base64.encodeBase64(StrIn.getBytes());
		return new String(bytesEncoded);
	}
	public static String DecodeStringBase64 (String StrIn)
	{
		byte[] bytesEncoded = Base64.decodeBase64(StrIn.getBytes());
		return new String(bytesEncoded);
	}
	public static String ObjectToJSON(Object obj)
	{
		Gson gson = new GsonBuilder().create();
		return gson.toJson(obj);
	}
	public static Object JSONtoObject(String json, Type type)
	{
		Gson gson = new GsonBuilder().create();
		Object retVal = gson.fromJson(json, type);
		return retVal;
	}
}
