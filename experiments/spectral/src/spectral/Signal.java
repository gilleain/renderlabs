package spectral;

import java.util.ArrayList;

public class Signal {
	
	private float shift;
	private float intensity;
	private String multiplicity;
	private ArrayList<Integer> atomIDs;
	
	
	public Signal() {
		this.shift = 0.0f;
		this.intensity = 0.0f;
		this.multiplicity = "";
		this.atomIDs = new ArrayList<Integer>();
	}
	
	public Signal(float shift) {
		this();
		this.shift = shift;
	}
	
	/**
	 * This is rather odd, since the atoms are ints...
	 * 
	 * @param atomValue
	 */
	public void addAtom(String atomValue) {
		this.atomIDs.add(new Integer(atomValue));
	}
	
	public void setShift(float shift) {
		this.shift = shift;
	}
	
	public void setShift(String shift) {
		this.shift = new Float(shift.trim());
	}
	
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
	public void setIntensity(String intensity) {
		this.intensity = new Float(intensity.trim());
	}
	
	public void addToMultiplicity(char c) {
		this.multiplicity += c;
	}
	
	public float getShift() {
		return this.shift;
	}
	
	public float getIntensity() {
		return this.intensity;
	}
	
	public boolean multiplicitiesEqual(Signal other) {
		return this.multiplicity.equals(other.multiplicity);
	}
	
	public boolean shiftsEqual(Signal other) {
		return this.shift == other.shift;
	}
	
	public float shiftDifference(Signal other) {
		return this.shift - other.shift;
	}
	
	public String toString() {
		return String.format("%s;%s;%s", this.shift, this.intensity, this.multiplicity);
	}

}
