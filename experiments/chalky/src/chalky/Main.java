package chalky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.generators.BasicGenerator;
import org.openscience.cdk.renderer.visitor.AbstractAWTRenderingVisitor;
import org.openscience.cdk.smiles.SmilesParser;

@SuppressWarnings("serial")
public class Main extends JPanel {
    
    // these are all related, so must be changed together
    public static final int STROKE_WIDTH = 5;
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int FONT_SIZE = 25;
    public static final double SCALE = 50.0;

    public class BlackboardMonitor extends AbstractAWTRenderingVisitor {
        
        private final Random r = new Random();
        private final Graphics2D g;
        
        public BlackboardMonitor(Graphics2D g, double s, Point2d c) {
            super(s, c.x, c.y);
            this.g = g;
            
            Stroke stroke = new BasicStroke(STROKE_WIDTH);
            this.g.setStroke(stroke);
            
            this.g.setPaint(this.createPaint());
            this.g.setFont(new Font("ARIAL", Font.PLAIN, FONT_SIZE));
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
        
        public void visitElementGroup(ElementGroup group) {
            group.visitChildren(this);
        }

        public void visitLine(LineElement line) {
            g.drawLine(tX(line.x1), tY(line.y1), tX(line.x2), tY(line.y2));
        }

        public void visitOval(OvalElement arg0) {
            // TODO Auto-generated method stub
            
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

    public String smiles = "C1(C2C=CC=C2)C(C)=C(F)C(C2C=CC=C2)=C(C)C1(F)";
    private BasicGenerator generator;
    private IAtomContainer ac;
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
            IRenderingElement diagram = generator.generate(ac);
            Point2d d = new Point2d((double)WIDTH/2, (double)HEIGHT/2);
            diagram.accept(new BlackboardMonitor((Graphics2D) g, SCALE, d));
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
