import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

public class ShortestPicturePath {
	private AcyclicSP sp;
	private Picture picture;
	private double totalenergy;
	private EdgeWeightedDigraph G;
	public ShortestPicturePath(EdgeWeightedDigraph G, Picture picture) {
		this.G = G;
		this.picture = picture;
	}
	public void ShortestVertical() {
		double temp;
		for (int i = 1; i < picture.width(); i += 2) {
			if (i == 1) {
				sp = new AcyclicSP(G, i);
				totalenergy = sp.distTo((picture.height() * picture.width() - 1));
			}
			else {
				sp = new AcyclicSP(G, i);
				temp = sp.distTo((picture.height() * picture.width() - 1));
				if (temp < totalenergy) {
					totalenergy = temp;
				}
			}
			
		}		
	}
}
