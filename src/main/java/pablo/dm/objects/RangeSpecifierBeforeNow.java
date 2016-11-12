package pablo.dm.objects;

public class RangeSpecifierBeforeNow extends RangeSpecifierBase {
	public int durationInMinutes;
	public final String type="BEFORE_NOW";
	
	public RangeSpecifierBeforeNow()
	{
		durationInMinutes=15;
	}
	public RangeSpecifierBeforeNow(int MinutesBack)
	{
		durationInMinutes=MinutesBack;
	}
}
