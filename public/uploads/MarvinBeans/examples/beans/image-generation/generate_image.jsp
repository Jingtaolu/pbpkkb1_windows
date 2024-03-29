<%--

    generate_image.jsp

    ChemAxon Ltd., 1999-2002

    Generates an image and displays it in the web browser.
    
    Parameters:
    
    format	    image format 
		    details: http://www.chemaxon.com/marvin/help/formats/images-doc.html
		    
    mol		    input structure
		    details: http://www.chemaxon.com/marvin/help/formats/formats.html

    filename	    name of file containing an input structure

   
    Exactly 2 parameters need to be set
    1) format
    2) mol or filename

--%>

<%@page import="java.io.*,java.net.URLDecoder,chemaxon.formats.MolImporter,chemaxon.struc.Molecule,chemaxon.formats.MolExporter"%>
<%
try {
    // Retrieving GET/POST parameters
    String format = request.getParameter("format");
    if(format == null) {
    	throw new Exception("Format needs to be specified.");
    }
    String filename = null;
    String molstring = null;
    if((filename = request.getParameter("file")) == null) {
    	if((molstring = request.getParameter("mol")) == null) {
	    throw new Exception("Structure needs to be specified.");
	}
    }

    // Setting content type
    String type = format.indexOf(":") == -1 ? 
	    format : format.substring(0, format.indexOf(":"));
    response.setContentType("image/" + type);

    // Creating the molecule
    Molecule mol = null;
    if(molstring == null) {
        // Reading the file
        File f = new File(filename);
        MolImporter mi = new MolImporter(f,"");
        mol = mi.read();
    } else {
        // Reading the posted SMILES string
	mol = MolImporter.importMol(molstring);
    }
    
    // Calculate the coordinates if needed (for example
    // if the input is SMILES)
    if(mol.getDim()==0) {
        mol.clean(2, "O1");
    }
    
    // Generating the image
    byte[] b = MolExporter.exportToBinFormat(mol, format);
    ServletOutputStream outs = response.getOutputStream();
    outs.flush();
    outs.write(b,0,b.length);
    outs.close();
} catch(Throwable t) {
    t.printStackTrace();
}
%>
    
