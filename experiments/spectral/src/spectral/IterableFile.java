package spectral;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class IterableFile implements Iterable<String> {
	
	private File file;

	public IterableFile(String filename) throws IOException {
		this(new File(filename));
	}
	
	public IterableFile(File file) throws IOException {
		this.file = file;
	}
	
	public Iterator<String> iterator() {
		ArrayList<String> lines = new ArrayList<String>();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException io) {
			//XXX
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					//XXX
				}
			}
		}
		return lines.iterator();
	}
	
	public String toString() {
		return this.file.toString();
	}

}
