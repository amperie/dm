package pablo.dm.dm_downloader.Controller;

import org.apache.http.Header;

public class ControllerResponse {
	public String body;
	public int returnCode;
	public String returnMessage;
	public Header[] Headers;
	public Header SessionIDHeader;
}
