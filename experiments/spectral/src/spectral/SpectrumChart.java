package spectral;

import java.awt.FontMetrics;
import java.awt.Graphics;

public class SpectrumChart {
	
	private Spectrum spectrum;
	
	public SpectrumChart() {
		this.spectrum = null;
	}
	
	public SpectrumChart(Spectrum spectrum) {
		this.spectrum = spectrum;
	}
	
	public void setSpectrum(Spectrum spectrum) {
		this.spectrum = spectrum;
	}
	
	public void paint(Graphics g, int w, int h) {
		if (this.spectrum == null) return;
		
		int signalHeight = h / 2;
		int layerHeight = 10;
		int layerIndex = 0;
		int previousX = 0;
		
		FontMetrics fm = g.getFontMetrics();
		double maxSignal = 0;
		for (Signal signal : this.spectrum.getSignals()) {
			if (signal.getShift() > maxSignal) maxSignal = signal.getShift();
		}
		maxSignal += 10;	// fudge factor to get text boxes in FIXME
		
		double scale = w / maxSignal;
		for (Signal signal : this.spectrum.getSignals()) {
			int signalPos = w - (int) (signal.getShift() * scale);
			g.drawLine(signalPos, signalHeight, signalPos, h);
			String signalLabel = String.valueOf((int)signal.getShift());
			
			// TODO : center label
			double stringWidth = fm.getStringBounds(signalLabel, g).getWidth();
			int halfWidth = (int)(stringWidth / 2);
			int currentX = signalPos - halfWidth;
			if (previousX >= currentX) {
				layerIndex++;
			} else {
				layerIndex = 0;
			}
			int labelHeight = signalHeight - (layerHeight * layerIndex);
			g.drawString(signalLabel, signalPos - halfWidth, labelHeight);
			previousX = signalPos + halfWidth;
		}
	}
	
	public static void main(String[] args) {
		// test
		String spectrum = "30;0|31;0|32;0|51;0|52;0|";
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SpectrumPanel panel = new SpectrumPanel(new Spectrum(spectrum, 1));
		frame.add(panel);
		frame.setSize(new java.awt.Dimension(400, 400));
		frame.setVisible(true);
	}

}
