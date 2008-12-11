package spectral;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.controller.AddAtomModule;
import org.openscience.cdk.controller.AddBondModule;
import org.openscience.cdk.controller.AddRingModule;
import org.openscience.cdk.controller.CycleSymbolModule;
import org.openscience.cdk.controller.IControllerModule;
import org.openscience.cdk.controller.IViewEventRelay;
import org.openscience.cdk.controller.RemoveModule;
import org.openscience.cdk.controller.SelectModule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

public class Spectral extends JFrame implements ActionListener, IViewEventRelay {
    
    private MoleculeEditPanel molPanel;
    
    private SpectrumPanel specPanel;
    
    private JPanel buttonPanel;
    
    private HashMap<String, Class<? extends IControllerModule>> moduleMap;
    
    private HashMap<String, Integer> ringSizeMap;
    
    private ButtonGroup buttonGroup;
    
    private PredictionTool tool;

    private CDKHydrogenAdder hydrogenAdder =
        CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
    
    private CDKAtomTypeMatcher matcher =
      CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());

    
    public Spectral() {
        super("Spectral");
        this.setLayout(new BorderLayout());
        
        this.molPanel = new MoleculeEditPanel(new ChemModel(), this);
        this.add(new JScrollPane(molPanel), BorderLayout.WEST);
        
        this.specPanel = new SpectrumPanel(400, 400);
        this.add(this.specPanel, BorderLayout.EAST);
        
        int rows = 2;
        int cols = 5;
        this.buttonPanel = new JPanel(new GridLayout(rows, cols));
        this.add(this.buttonPanel, BorderLayout.NORTH);
        
        buttonGroup = new ButtonGroup();
        
        moduleMap = new HashMap<String, Class<? extends IControllerModule>>();
        makeModuleButton("ATOM",   AddAtomModule.class);
        makeModuleButton("CYCLE",  CycleSymbolModule.class);
        makeModuleButton("BOND",   AddBondModule.class);
        makeModuleButton("5RING",  AddRingModule.class);
        makeModuleButton("6RING",  AddRingModule.class);
        makeModuleButton("7RING",  AddRingModule.class);
        makeModuleButton("8RING",  AddRingModule.class);
        makeModuleButton("DELETE", RemoveModule.class);
        makeModuleButton("SELECT", SelectModule.class);
        makeModuleButton("CLEAR",  ClearModule.class);
        
        this.ringSizeMap = new HashMap<String, Integer>();
        this.ringSizeMap.put("5RING", 5);
        this.ringSizeMap.put("6RING", 6);
        this.ringSizeMap.put("7RING", 7);
        this.ringSizeMap.put("8RING", 8);
        
        this.setSize(800, 400);
        this.setLocation(300, 100);
        try {
            this.tool = new PredictionTool("nmrshiftdb.csv");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        this.setVisible(true);

    }
    
    private void makeModuleButton(String action,
            Class<? extends IControllerModule> moduleClass) {
        JButton button = new JButton(action);
        button.setActionCommand(action);
        button.addActionListener(this);
        buttonGroup.add(button);
        this.buttonPanel.add(button);
        moduleMap.put(action, moduleClass);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        Class<? extends IControllerModule> moduleClass =
            this.moduleMap.get(command);
        if (this.ringSizeMap.containsKey(command)) {
            int i = ringSizeMap.get(command);
            this.molPanel.setControllerModule(moduleClass, i);
        } else {
            this.molPanel.setControllerModule(moduleClass);
        }
    }
    
    public static void main(String[] args) {
        new Spectral();
    }
    
    private Spectrum makeSpectrum(IChemModel chemModel) {
        IMoleculeSet molSet = chemModel.getMoleculeSet();
        if (molSet == null) return new Spectrum(1);
        Spectrum spectrum = new Spectrum(1);
        for (IAtomContainer ac : molSet.molecules()) {
            IMolecule mol = ac.getBuilder().newMolecule(ac);
            mol = this.fixMolecule(mol);
            spectrum = tool.predict(mol, "C");
            System.err.println(spectrum);
            break;
        }
        return spectrum;
    }

    private IMolecule fixMolecule(IMolecule mol) {
        try {
            for (IAtom atom : mol.atoms()) {
                if (atom instanceof PseudoAtom) continue;
                IAtomType atomType = matcher.findMatchingAtomType(mol, atom);
                if (atomType == null) {
                    System.err.println("atom type null for " + atom.getSymbol());
                    continue;
                }
                AtomTypeManipulator.configure(atom, atomType);
            }
            this.hydrogenAdder.addImplicitHydrogens(mol);
        } catch (CDKException c) {
            c.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mol;
    }

    public void updateView() {
        this.molPanel.updateView();
        Spectrum spectrum = this.makeSpectrum(this.molPanel.getChemModel());
        this.specPanel.setSpectrum(spectrum);
        this.specPanel.repaint();
    }


}
