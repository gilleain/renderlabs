package spectral;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SpectrumPanel extends JPanel {
	
	private SpectrumChart spectrumChart;
	
	public SpectrumPanel(int w, int h) {
		this.spectrumChart = new SpectrumChart();
		this.setBackground(Color.WHITE);
		this.setPreferredSize(new Dimension(w, h));
	}
	
	public SpectrumPanel(Spectrum spectrum) {
		this.spectrumChart = new SpectrumChart(spectrum);
	}

	public void setSpectrum(Spectrum spectrum) {
		this.spectrumChart.setSpectrum(spectrum);
	}
	
	public void setSpectrum(String spectrum) {
		this.spectrumChart.setSpectrum(new Spectrum(spectrum, 1));
	}
	
	public void paint(Graphics g) {
	    super.paint(g);
		int h = this.getHeight();
		int w = this.getWidth();
		this.spectrumChart.paint(g, w, h);
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
