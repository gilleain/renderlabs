package spectral;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.Constructor;

import javax.swing.JPanel;

import org.openscience.cdk.controller.ControllerHub;
import org.openscience.cdk.controller.ControllerModel;
import org.openscience.cdk.controller.IChemModelRelay;
import org.openscience.cdk.controller.IControllerModule;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.SwingMouseEventRelay;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.renderer.IntermediateRenderer;

public class MoleculeEditPanel extends JPanel {
    
    private ControllerHub hub;
    
    private IntermediateRenderer renderer;
    
    private ControllerModel controllerModel;
    
    private SwingMouseEventRelay mouseEventRelay;
    
    private boolean isNew = true;
    
    private boolean shouldPaintFromCache = false;
    
    public MoleculeEditPanel(IChemModel chemModel, IViewEventRelay eventRelay) {
        this.renderer = new IntermediateRenderer();
        this.renderer.setModelCenter(0, 0);
        this.controllerModel = new ControllerModel();
        this.hub 
            = new ControllerHub(controllerModel, renderer, chemModel, eventRelay);
        this.mouseEventRelay = new SwingMouseEventRelay(this.hub);
        this.addMouseListener(mouseEventRelay);
        this.addMouseMotionListener(mouseEventRelay);
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(400, 400));
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        IChemModel chemModel = hub.getIChemModel();
        if (chemModel != null && chemModel.getMoleculeSet() != null) {
            if (this.shouldPaintFromCache) {
                this.renderer.repaint(g2);
            } else {
                this.renderer.paintChemModel(
                        chemModel, g2, getBounds(), this.isNew);
            }
            isNew = false;
            shouldPaintFromCache = true;
        }
    }
    
    public void updateView() {
        this.shouldPaintFromCache = false;
        this.repaint();
    }
    
    public void setControllerModule(Class<? extends IControllerModule> moduleClass) {
        try {
            // only works for constructor that takes a hub
            Constructor<? extends IControllerModule> constructor =
                moduleClass.getConstructor(IChemModelRelay.class);
            IControllerModule module = 
                (IControllerModule) constructor.newInstance(this.hub); 
            this.hub.setActiveDrawModule(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setControllerModule(Class<? extends IControllerModule> moduleClass, int i) {
        try {
            // only works for constructor that takes a hub and an int
            Constructor<? extends IControllerModule> constructor =
                moduleClass.getConstructor(IChemModelRelay.class, int.class, boolean.class);
            IControllerModule module = 
                (IControllerModule) constructor.newInstance(this.hub, i, false); 
            this.hub.setActiveDrawModule(module);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public IChemModel getChemModel() {
        return this.hub.getIChemModel();
    }
}
