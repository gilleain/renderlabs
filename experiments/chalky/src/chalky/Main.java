package chalky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.generators.BasicGenerator;
import org.openscience.cdk.smiles.SmilesParser;

public class Main extends JPanel {
    
    public class BlackboardMonitor implements IRenderingVisitor {
        
        public static final int STROKE_WIDTH = 3;
        public Graphics2D g;
        private final double s;
        private final double dx;
        private final double dy;
        private final double cx;
        private final double cy;
        
        private final Random r = new Random();
        
        public BlackboardMonitor(Graphics2D g, double s, Point2d displayC, Point2d modelC) {
            this.g = g;
            this.s = s;
            this.dx = displayC.x;
            this.dy = displayC.y;
            this.cx = modelC.x;
            this.cy = modelC.y;
            
            Stroke stroke = new BasicStroke(STROKE_WIDTH);
            this.g.setStroke(stroke);
            
            this.g.setPaint(this.createPaint());
            this.g.setFont(new Font("ARIAL", Font.PLAIN, 20));
        }
        
        private Paint createPaint() {
            int w = STROKE_WIDTH;
            int h = 50;
            BufferedImage bi = this.makeImage(w, h);
            return new TexturePaint(bi, new Rectangle(0, 0, w, h));
        }

        private BufferedImage makeImage(int w, int h) {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = bi.createGraphics();

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    boolean a = r.nextBoolean();
                    boolean b = r.nextBoolean();
                    if (a && b) {
                        g.setColor(Color.WHITE);
                    } else if (a) {
                        g.setColor(Color.LIGHT_GRAY);
                    } else if (b) {
                        g.setColor(Color.GRAY);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.fillRect(i, j, 1, 1);
                }
            }
            return bi;
        }
        
        private int tX(double x) {
//            return (int) ((this.s * (x - this.cx)) + this.dx);
            return (int) ((this.s * x) + this.dx);
        }
        
        private int tY(double y) {
//            return (int) ((this.s * (y - this.cy)) + this.dy);
            return (int) ((this.s * y) + this.dy);
        }

        public void visitElementGroup(ElementGroup group) {
            group.visitChildren(this);
        }

        public void visitLine(LineElement line) {
            g.drawLine(tX(line.x1), tY(line.y1), tX(line.x2), tY(line.y2));
        }

        public void visitOval(OvalElement arg0) {
            // TODO Auto-generated method stub
            
        }
        
        private Rectangle2D getTextBounds(TextElement textElement, Graphics2D g) {
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D bounds = fm.getStringBounds(textElement.text, g);

            double widthPad = 3;
            double heightPad = 1;

            double w = bounds.getWidth() + widthPad;
            double h = bounds.getHeight() + heightPad;
            return new Rectangle2D.Double(
                    tX(textElement.x) - w / 2,
                    tY(textElement.y) - h / 2,
                    w,
                    h);
        }

        private Point getTextBasePoint(TextElement textElement, Graphics2D g) {
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D stringBounds = fm.getStringBounds(textElement.text, g);
            int baseX = (int) (tX(textElement.x) - (stringBounds.getWidth() / 2));

            // correct the baseline by the ascent
            int baseY = (int) (tY(textElement.y) +
                    (fm.getAscent() - stringBounds.getHeight() / 2));
            return new Point(baseX, baseY);
        }

        public void visitText(TextElement text) {
            Point p = this.getTextBasePoint(text, g);
            Rectangle2D textBounds = this.getTextBounds(text, g);
            Paint paint = g.getPaint();
            this.g.setColor(Color.BLACK);
            this.g.fill(textBounds);
            this.g.setPaint(paint);
            this.g.drawString(text.text, p.x, p.y);
        }

        public void visitWedge(WedgeLineElement arg0) {
            // TODO Auto-generated method stub
            
        }
        
    }

    public String smiles = "C1(C(N)C)C=C(O)C(C)C=C1";
    private BasicGenerator generator;
    private IAtomContainer ac;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    
    public Main() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        Renderer2DModel model = new Renderer2DModel();
        generator = new BasicGenerator(model);
        try {
           SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
           ac = parser.parseSmiles(smiles);
           StructureDiagramGenerator sdg = new StructureDiagramGenerator();
           sdg.setMolecule((IMolecule)ac);
           sdg.generateCoordinates();
           ac = (IAtomContainer)sdg.getMolecule();
        } catch (Exception e) {
            e.printStackTrace();
            ac = null;
        }
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        if (ac != null) {
            Point2d c = GeometryTools.get2DCenter(ac);
            IRenderingElement diagram = generator.generate(ac);
            Point2d d = new Point2d((double)WIDTH/2, (double)HEIGHT/2);
            diagram.accept(new BlackboardMonitor((Graphics2D) g, 50.0d, d, c));
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        Main m = new Main();
        f.add(m);
        f.pack();
        f.setVisible(true);
    }

}
