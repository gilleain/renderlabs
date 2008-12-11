package spectral;

import javax.vecmath.Point2d;

import org.openscience.cdk.controller.ControllerModuleAdapter;
import org.openscience.cdk.controller.IChemModelRelay;

public class ClearModule extends ControllerModuleAdapter {

    public ClearModule(IChemModelRelay chemModelRelay) {
        super(chemModelRelay);
    }
    
    public void mouseClickedDown(Point2d p) {
        System.err.println("zapping");
        this.chemModelRelay.zap();
        this.chemModelRelay.updateView();
    }

    public String getDrawModeString() {
        return "CLEAR";
    }

}
