package pablo.dm.objects;

public class RangeSpecifierBeforeNow extends RangeSpecifierBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5785060216026202835L;
	public RangeSpecifierBeforeNow()
	{
		type="BEFORE_NOW";
		durationInMinutes=15;
	}
	public RangeSpecifierBeforeNow(int MinutesBack)
	{
		type="BEFORE_NOW";
		durationInMinutes=MinutesBack;
	}
}
