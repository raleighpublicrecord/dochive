//---------------------------------------------------------
// Author: Edward Brian Duncan
// DocHive Description: This is DocHive's point of entry.
// The main method controls the DocHive template-based
// conversion process.
//---------------------------------------------------------
package org.rpr.dh;

import java.io.File;

public class DocHive{

	//-----------------------------------------------------
    // arg 0: sourceFile including path
    // arg 1: destinationDirectory including path
    // arg 2: autoalign pages
    // arg 3: template location
    // arg 4: template set
    // arg 5: additional identifier
    //-----------------------------------------------------
    // Description: This application will convert a multipage
    // document into it's csv content. This is the entry point.
    //-----------------------------------------------------
    public static void main(String args[]){

		String sourceFile;
		String absolutePath;
		String filePath;
		String destinationDirectory;
		String optionalTemplateSet;
		Boolean bOptionalTemplateSet = false;
		Boolean bAutoAlign = true;
		String optionalIdentifier = "";
		Boolean bOptionalIdentifier = false;
		String templateDirectory = "templates";

		//-------------------------------------------------
	    // Handle the command line parameters and set
	    // default values if required.
    	//-------------------------------------------------

		// minimum requiremnets
		// sourceand destination parameters
		if(args.length < 2) {
			System.out.println(args.length + " arguments have been passed into DocHive+... Exiting");
			return;
		}

		// source file
		sourceFile = args[0];
		File temp = new File(sourceFile);
		if(temp.exists()) {
			absolutePath = temp.getAbsolutePath();
			filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
			System.out.println("File path: " + filePath);
		}
		else {
			// exit
			System.out.println("sourceFile argument does not exist");
			System.out.println("value: "+sourceFile+"... Exiting");
			return;
		}

		// destination location (directory)
		destinationDirectory = args[1];
		File dest = new File(destinationDirectory);
		if(!dest.exists()) {
			// exit
			System.out.println("destinationDirectory argument does not exist");
			System.out.println("value: "+destinationDirectory+"... Exiting");
			return;
		}

		// (optional parameter) autoalignment
		if(args.length>2) {
			if(args[2].equals("true")) {
				bAutoAlign = true;
			}
			else {
				bAutoAlign = false;
			}
		}

		// (optional parameter) custom template directory location
		if(args.length>3) {

			// '*' to set default
			if(!args[3].equals("*")) {
				templateDirectory = args[3];
			}
		}

		// (optional parameter) custom template set
		if(args.length>4) {

			// '*' to set default
			if(!args[4].equals("*")) {
				optionalTemplateSet = args[4];
				bOptionalTemplateSet = true;
			}
		}

		// (optional parameter) custom identifier
		if(args.length>5) {

			// '*' to set default
			if(!args[5].equals("*")) {
				optionalIdentifier = args[5];
				bOptionalIdentifier = true;
			}
		}

		//-------------------------------------------------
	    // now we have the parameters, lets separate the
	    // source file into separate PNG pages
    	//-------------------------------------------------

	  	// initialize toolbox (rotation, alignment)
	  	DocHiveToolbox tools = new DocHiveToolbox();

	  	// pages in current document
	  	int index = temp.getName().lastIndexOf('.');
		String fileName = temp.getName();
		String woext = temp.getName().substring(0, index);

		// record separation start time
		long sepStartTime = System.currentTimeMillis();
	  	int pageCount = tools.separateDocumentPages(sourceFile, fileName, woext, destinationDirectory);

		// record separation end time
		long sepEndTime = System.currentTimeMillis();
		System.out.println("Converted in " + (sepEndTime - sepStartTime)/1000 + " seconds");

		//-------------------------------------------------
	    // we have separated all the pages into PNG files.
	    // now we can align them (if asked for), find the
	    // point of reference and template for each file.
	    // finally, convert the PNG file into one or more
	    // CSV lines
    	//-------------------------------------------------

	  	double rotate;
	  	String files;
	  	File folder = new File(destinationDirectory + File.separator + woext);
	  	File[] listOfFiles = folder.listFiles();
	  	boolean templateAvailable;

		System.out.println("Page Count: " + pageCount);

	    // only continue if pages exist
	  	if(pageCount>0) {

			// record start time
			long startTime = System.currentTimeMillis();

		  	// loop through all the files in destinationDirectory
			for (int i = 0; i < listOfFiles.length; i++) {

				// ensure it is not a file
		  		if (listOfFiles[i].isFile()) {

		     		String afile = listOfFiles[i].getName();
		          	if (afile.toUpperCase().endsWith(".PNG"))	{

						// create the output directory corresponding to the file
						boolean status;
						status = new File(destinationDirectory + File.separator + woext).mkdir();
						status = new File(destinationDirectory + File.separator + woext + File.separator + afile.substring(0, afile.lastIndexOf("."))).mkdir();

						String curLocation = destinationDirectory + File.separator + woext + File.separator + afile;
						String newLocation = destinationDirectory + File.separator + woext + File.separator + afile.substring(0, afile.lastIndexOf(".")) + File.separator + afile;

						File currentFile = new File(curLocation);
						currentFile.renameTo(new File(newLocation));

						// instanciate template class
 						DocHiveTemplate dhTemplate = new DocHiveTemplate(templateDirectory);

						// auto rotate file
					    if(bAutoAlign) rotate = dhTemplate.autoAlignRotate(afile, destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

					    // trim/normalize form
					    dhTemplate.normalizeByTrimming(afile, destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));
					    // dhTemplate.determinePagePointOfAlignment(afile, destinationDirectory);

					    // determine if a template exists for the file
					    templateAvailable = false;
					    templateAvailable = dhTemplate.templateExistFor(afile, destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));


						System.out.println("-------------------------------------------------------------------");
						System.out.println(templateAvailable ? "success" : "unexpected opportunity");

					    // use template to extract information
					    if(templateAvailable) {

							// yo, mark page as having a template ************************************************************

						    // get current template name
 						    String templateName = dhTemplate.getTemplateName();
							System.out.println("Template ["+templateName+"] selected for ["+afile+"]");

						    // use the template to extract pieces
							dhTemplate.extractWithTemplate(templateName, afile, destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

						    // convert the pieces into a csv file
							dhTemplate.transformWithTemplate(templateName, afile, bOptionalIdentifier, optionalIdentifier, destinationDirectory + File.separator + fileName.substring(0, fileName.lastIndexOf(".")));

					    } // end [if(templateAvailable)]
					    else {

							// mark page as not having a template + new method needed ****************************************
						}

		            } // end [if (files.toUpperCase().endsWith(".PNG"))]

		   		} // end [if (listOfFiles[i].isFile())]

			} // end [for (int i = 0; i < listOfFiles.length; i++)]

			// record end time
			long endTime = System.currentTimeMillis();
			System.out.println("Converted in " + (endTime - startTime)/1000 + " seconds");

	  	} // end [if(pageCount>0)]

    } // end [main(String args[])]

} // end [class DocHive]