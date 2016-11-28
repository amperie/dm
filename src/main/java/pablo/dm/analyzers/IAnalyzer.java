package pablo.dm.analyzers;

public interface IAnalyzer {

	public void analyze();
	public void AddToAnalysis(Object objToAdd);
	public void reset();
	
}
