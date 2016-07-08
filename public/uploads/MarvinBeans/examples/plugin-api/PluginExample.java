/*
 * Copyright (c) 1998-2015 ChemAxon Ltd. All Rights Reserved.
 */

import java.io.IOException;

import chemaxon.marvin.calculations.MajorMicrospeciesPlugin;
import chemaxon.marvin.calculations.TPSAPlugin;
import chemaxon.marvin.calculations.logDPlugin;
import chemaxon.marvin.calculations.IUPACNamingPlugin;
import chemaxon.marvin.plugin.PluginException;
import chemaxon.struc.Molecule;
import chemaxon.formats.MolExporter;
import chemaxon.formats.MolImporter;

/**
 * In this example MajorMicrospeciesPlugin is used to generate the major
 * microspecies at pH 7.4,TPSAPlugin to calculate the polar surface areas,
 * loDPlugin to to calculate the logD values, and IUPACNamingPlugin to 
 * generate the names of molecules read from a molfile. The results of the 
 * calculations are written to an SDfile, the molecules in the file are the 
 * major microspecies of the input molecules, IUPAC names, surface area 
 * values and logD values are saved in SDF property fields.
 * 
 * <p>Usage:
 * <pre>   java PluginExample [molFile]</pre>
 * 
 * For the detailed description of this example, please visit
 * http://www.chemaxon.com/marvin/examples/plugin/index.html#example1
 * 
 * @version 5.0.4 04/22/2008
 * @since Marvin 5.0.4
 * @author Zsolt Mohacsi
 */
public class PluginExample {

    public static void main(String[] args) {
	try {
	    // instantiate the plugin objects
	    MajorMicrospeciesPlugin mmsPlugin = new MajorMicrospeciesPlugin();
	    TPSAPlugin tpsaPlugin = new TPSAPlugin();
	    logDPlugin logDPlugin = new logDPlugin(); 
	    IUPACNamingPlugin iupacNamingPlugin = new IUPACNamingPlugin();

	    // set the parameters for the calculations
	    // MajorMicrospeciesPlugin parameters
	    mmsPlugin.setpH(7.4); // major microspecies generation at pH = 7.4
	    // TPSAPlugin parameters
	    tpsaPlugin.setpH(7.4); // surface area calculation at pH = 7.4
	    // logDPlugin parameters
	    // set the Cl- and Na+/K+ concentration
	    logDPlugin.setCloridIonConcentration(0.15);
	    logDPlugin.setNaKIonConcentration(0.15);
	    // set the pH range and pH step size
	    logDPlugin.setpHLower(5.4);
	    logDPlugin.setpHUpper(9.4);
	    logDPlugin.setpHStep(2.0);
	    // (there are no parameters to set for IUPACNamingPlugin) 
	    
	    // create a MolExporter for writing the result molecules
	    MolExporter exporter = new MolExporter(System.out, "sdf");
	    
	    // read the input molecules and perform the calculations
	    MolImporter importer = new MolImporter(args[0]);
	    Molecule mol;
	    while ((mol = importer.read()) != null) {

	        // Perform major microspecies, polar surface area and logD 
	        // calulations
	        
	        // set the input molecule for MajorMicrospeciesPlugin,
	        // TPSAPlugin and logDPlugin
	        mmsPlugin.setMolecule(mol);
	        tpsaPlugin.setMolecule(mol);
	        logDPlugin.setMolecule(mol);
	        
	        // run the major microspecies, TPSA and logD calculations
	        mmsPlugin.run();
	        tpsaPlugin.run();
	        logDPlugin.run();

	        // get the results of the calculations
	        // get the result of the major microspecies calculation
	        Molecule majorms = mmsPlugin.getMajorMicrospecies();
	        
	        // get the result of the polar surface area calculation
	        double surfaceArea = tpsaPlugin.getSurfaceArea();

	        // get the results of the logD calculation
	        // get the pH values 
	        double[] pHs = logDPlugin.getpHs();
	        // get the logD values
	        double[] logDs = logDPlugin.getlogDs();
	        
	        // Generate the IUPAC name of the major microspecies
	        // generated by the MajorMicrospeciesPlugin.

	        // set majorms as input molecule for IUPACNamingPlugin 
	        iupacNamingPlugin.setMolecule(majorms);

	        // run the IUPACNamingPlugin 
	        iupacNamingPlugin.run();

	        // get the preferred IUPAC name
	        String name = iupacNamingPlugin.getPreferredIUPACName();
	        
	        // Format and display the results

	        // store the pH - logD pairs in a string in format
	        // "[pH1]:logD1 [pH2]:logD2 ..."  
	        String logDresult = "";
	        for (int i = 0; i < pHs.length; i++) {
	    	double pH = pHs[i];
	    	double logD = logDs[i];
	    	logD = Math.rint(logD * 100)/100; // round
	    	logDresult += ("[" + pH + "]:" + logD + " ");
	        }

	        // write the result molecules (the major microspecies at  
	        // pH 7.4) with other results in SDF fields
	        majorms.setProperty("NAME", name);
	        majorms.setProperty("TPSA at pH 7.4", 
	    	    new Double(surfaceArea).toString());
	        majorms.setProperty("logD ([pH]:value)", logDresult);
	        exporter.write(majorms);
	    }
	    importer.close();
	    exporter.close();
	} catch (IOException e) {
	    System.err.println("I/O error has occurred.");
	    e.printStackTrace();
	} catch (PluginException e) {
	    System.err.println("Plugin processing or calculation error.");
	    e.printStackTrace();
	}
    }
}
