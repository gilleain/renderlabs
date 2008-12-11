package spectral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


public class PredictionTool {
	
	private static HashMap<String, HashMap<String, Double>> mapsmap 
	        = new HashMap<String, HashMap<String, Double>>();

	/**
	 *Constructor for the PredictionTool object
	 * 
	 * @exception IOException
	 *                Problems reading the HOSE code file.
	 */
	public PredictionTool() throws IOException {
		String filename = "nmrshiftdb.csv";
		InputStream ins = this.getClass().getClassLoader()
							.getResourceAsStream(filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
		this.readCSVFile(reader);
	}
	
	public PredictionTool(String filename) throws IOException {
		this.readCSVFile(new BufferedReader(new FileReader(filename)));
	}
	
	private void readCSVFile(BufferedReader reader) throws IOException {
		String input;
		while ((input = reader.readLine()) != null) {
			StringTokenizer st2 = new StringTokenizer(input, "|");
			String symbol = st2.nextToken();
			String code = st2.nextToken();
			st2.nextToken();	// throw away minimum
			Double av = new Double(st2.nextToken());
			st2.nextToken();	// throw away maximum
			HashMap<String, Double> map = mapsmap.get(symbol);
			if (map == null) {
				map = new HashMap<String, Double>();
				mapsmap.put(symbol, map);
			}
			map.put(code, av);
		}
	}
	
	public Spectrum predict(IMolecule mol, String elementSymbol) {
	    Spectrum spectrum = new Spectrum(1);
	    for (IAtom atom : mol.atoms()) {
	        if (atom.getSymbol().equals(elementSymbol)) {
	            spectrum.addSignal(this.predict(mol, atom));
	        }
	    }
	    return spectrum;
	}

	/**
	 * This method does a prediction.
	 * 
	 * @param comment
	 *            Contains additional text after processing predictRange().
	 * @param mol
	 *            The molecule the atoms comes from.
	 * @param a
	 *            The atom the shift of which to be predicted.
	 * @param maxSpheresToUse
	 *            Restrict number of spheres to use, to use max spheres set -1.
	 * @return Average
	 */
	public float predict(IMolecule mol, IAtom a) {
		
		int maxSpheresToUse = 6;
		
		HOSECodeGenerator hcg = new HOSECodeGenerator();
		int spheres;
		for (spheres = maxSpheresToUse; spheres > 0; spheres--) {
			StringBuffer hoseCodeBuffer = new StringBuffer();
			StringTokenizer st; 
			try {
				st = new StringTokenizer(
							hcg.getHOSECode(mol, a, maxSpheresToUse), "()/");
			} catch (CDKException c) {
				c.printStackTrace();
				return -1;
			}
			for (int k = 0; k < spheres; k++) {
				if (st.hasMoreTokens()) {
					String partcode = st.nextToken();
					hoseCodeBuffer.append(partcode);
				}
				if (k == 0) {
					hoseCodeBuffer.append("(");
				} else if (k == 3) {
					hoseCodeBuffer.append(")");
				} else {
					hoseCodeBuffer.append("/");
				}
			}
			String hoseCode = hoseCodeBuffer.toString();
			String symbol = a.getSymbol();
			HashMap<String, Double> map = mapsmap.get(symbol);
			Double d = ((Double) map.get(hoseCode));
			
			if (d != null) {
				return d.floatValue();
			}
		}
		return -1;
	}

	/**
	 * The main program for the PredictionTool class.
	 * 
	 * @param args
	 *            The file name of the test mdl file.
	 * @exception Exception
	 *                Description of Exception.
	 */
	public static void main(String[] args) throws Exception {
		MDLV2000Reader mdlreader = new MDLV2000Reader(new FileReader(args[0]));
		IMolecule mol = (IMolecule) mdlreader.read(new Molecule());
		mol = (IMolecule) AtomContainerManipulator.removeHydrogens(mol);
	
		CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance())
				.addImplicitHydrogens(mol);
		for (int i = 0; i < mol.getAtomCount(); i++) {
			if (mol.getAtom(i).getHydrogenCount() < 0)
				mol.getAtom(i).setHydrogenCount(0);
		}
		CDKHueckelAromaticityDetector.detectAromaticity(mol);
		PredictionTool tool = new PredictionTool();
		for (IAtom atom : mol.atoms()) {
			float result = tool.predict(mol, atom);
			System.out.println(result);
		}
	}
}
