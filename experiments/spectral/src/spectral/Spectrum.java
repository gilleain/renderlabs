package spectral;

import java.util.ArrayList;

public class Spectrum {
	
	private int moleculeID;
	private ArrayList<Signal> signals;
	
	public Spectrum(int moleculeID) {
		this.moleculeID = moleculeID;
		this.signals = new ArrayList<Signal>(); 
	}
	
	public Spectrum(String specFile, int moleculeID) throws NumberFormatException {
		this(moleculeID);
		
		boolean addingShift = true;
		boolean addingIntensity = true;
		String shift = "";
		String intensity = "";
		String atom = "";
		
		for (int i = 0; i < specFile.length(); i++) {
			char x = specFile.charAt(i);
			if (addingShift && x == ';') {
				intensity = "";
				addingShift = false;
				addingIntensity = true;
			} else {
				if (addingIntensity && x == ';') {
					atom = "";
					addingIntensity = false;
				} else {
					if (x == '|') {
						Signal currentSignal = new Signal();
						if (!shift.trim().equals("")) {
							try {
								currentSignal.setShift(shift);
								if (intensity.trim().equals("")) {
									// do nothing, as it is 0 by default
								} else {
									String intensityNew = "";
									for (int j = 0; j < intensity.length(); j++) {
										char cj = intensity.charAt(j);
										if (Character.isLetter(cj)) {
											currentSignal.addToMultiplicity(cj);
										} else {
											intensityNew += cj;
										}
									}
									if (intensityNew.trim().equals("")) {
										currentSignal.setIntensity(intensityNew);
									}
								}
								
								// FIXME : there are problems here...
								for (Signal signal : this.signals) {
									if (signal.shiftsEqual(currentSignal) 
											&& signal.multiplicitiesEqual(currentSignal)) {
										currentSignal.addAtom(atom);
										break;
									}
								}
								
								if (!atom.trim().equals("")) {
									currentSignal.addAtom(atom);
								}
								this.signals.add(currentSignal);
									
							} catch (NumberFormatException nfe) {
								throw nfe;
							}
							shift = "";
							addingShift = true;
						}
					} else {
						if (addingShift) {
							shift += x;
						} else {
							if (addingIntensity) {
								intensity += x;
							} else {
								atom += x;
							}
						}
					}
				}
			}
		}
	}
	
	public void addSignal(float shift) {
		this.signals.add(new Signal(shift));
	}
	
	public ArrayList<Signal> getSignals() {
		return this.signals;
	}
	
	public float similarity(Spectrum other, boolean isSubSpectrum) {
		float similarityPerSignal;
		float similarity = 0;
		
		ArrayList<Signal> smallList;
		ArrayList<Signal> largeList;
		
		int smallSize;
		int largeSize;
		
		if (isSubSpectrum) {
			smallSize = this.signals.size();
			smallList = this.signals;
			
			largeSize = other.signals.size();
			largeList = other.signals;
			
			similarityPerSignal = 100f / smallSize;
		} else {
			if (this.signals.size() > other.signals.size()) {
				smallList = other.signals;
				largeList = this.signals;
			} else {
				smallList = this.signals;
				largeList = other.signals;
			}
			largeSize = largeList.size();
			smallSize = smallList.size();
			
			similarityPerSignal = 100f / largeSize;
		}
		
		
		boolean[] signalsUsed = new boolean[largeSize];
		for (Signal a : smallList) {
			
			int i = 0;
			int nearestSignal = 0;
			double currentMinimum = Double.MAX_VALUE;
			
			for (Signal b : largeList) {
				float shiftDifference = a.shiftDifference(b);
				
				if (!signalsUsed[i] && Math.abs((double) shiftDifference) < currentMinimum) {
					currentMinimum = Math.abs((double) shiftDifference);
					nearestSignal = i;
				}
				i++;
			}
			if (currentMinimum < similarityPerSignal) {
				similarity += similarityPerSignal - currentMinimum;
			}
			signalsUsed[nearestSignal] = true;
		}
		
		return similarity;
	}
	
	public int getMoleculeID() {
		return this.moleculeID;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Signal signal : this.signals) {
			s.append(signal.toString());
			s.append("|");
		}
		return s.toString();
	}
	
	public static void main(String[] args) {
//		String specFile = 
//			"128.5;0.0;0|128.5;0.0;1|128.5;0.0;2|128.5;0.0;3|128.5;0.0;4|128.5;0.0;5|";
		String specFile = "119;0|";
		Spectrum a = new Spectrum(specFile, 1);
		Spectrum b = new Spectrum(specFile, 2);
		float similarity = a.similarity(b, false);
		if (similarity > 99f) {
			System.out.print(a + "\nmatches\n" + b + " with similarity " + similarity);
		}
		
	}

}
