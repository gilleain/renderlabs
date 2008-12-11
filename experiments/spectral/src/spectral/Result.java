package spectral;

import org.openscience.cdk.interfaces.IMolecule;

public class Result implements Comparable<Result> {
	
	private int id;
	
	private float spectralSimilarity;
	
	private IMolecule molecule;
	
	public Result(int id, float spectralSimilarity) {
		this.id = id;
		this.spectralSimilarity = spectralSimilarity;
		this.molecule = null;
	}
	
	public void setMolecule(IMolecule molecule) {
		this.molecule = molecule;
	}
	
	public IMolecule getMolecule() {
		return this.molecule;
	}
	
	public int getID() {
		return this.id;
	}
	
	public float getSpectralSimilarity() {
		return this.spectralSimilarity;
	}
	
	public int compareTo(Result other) {
	    if (this.spectralSimilarity > other.spectralSimilarity) {
	        return -1;
	    } else if (this.spectralSimilarity < other.spectralSimilarity) {
	        return 1;
	    } else {
	        return 0;
	    }

	}
	
	public String toString() {
		return String.format("%s [%s]", this.id, this.spectralSimilarity);
	}

}
