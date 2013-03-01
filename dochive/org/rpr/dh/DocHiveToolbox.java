//---------------------------------------------------------
// Author: Edward Brian Duncan
// DocHiveToolbox Description: General uility functions for
// DocHive.
//---------------------------------------------------------
package org.rpr.dh;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class DocHiveToolbox {

	//-----------------------------------------------------
  	// Constructor- Initialize the class.
  	//-----------------------------------------------------
  	DocHiveToolbox() {
  	} // end [DocHiveToolbox()]


	//-----------------------------------------------------
  	// Description: Report success or failure based on
  	// boolean state.
  	//-----------------------------------------------------
	void report(boolean b) {
	    System.out.println(b ? "success" : "unexpected opportunity");
    }


	//-----------------------------------------------------
  	// Description: Separate the file specified by fileName
  	// parameter into png files stored in the directoryLocation
  	// parameter. If a directoryLocation does not exist then
  	// it will be created. Return the number of pages.
  	//-----------------------------------------------------
  	int separateDocumentPages(String sourceFile, String fileName, String woext, String directoryLocation){

		// create the output directory corresponding to the file
		boolean status;
		int pageCount = 0;
		status = new File(directoryLocation + "//" + woext).mkdir();
        report(status);

        if(status) {
			// execute separation
			runtimeExecuteAndWait("convert.bat -monochrome -density 300 " + sourceFile + " " + directoryLocation + "\\" + woext + "\\" + woext + "_%02d.png");
		}
		// return the number of files in the [input\fileName] directory
	  	pageCount = new File(directoryLocation + "\\" + woext).listFiles().length;
	  	return pageCount;
  	} // end [separateDocumentPages(String fileName, String directoryLocation)]


  	//-----------------------------------------------------
  	// Description: via StreamGobbler, execute an external
  	// command and wait for it to finish executing.
  	//-----------------------------------------------------
    void runtimeExecuteAndWait(String cmd){
    	try {
			Runtime rt = Runtime.getRuntime();
			System.out.println("Executing: " + cmd);
			Process proc = rt.exec(cmd);

			// any error message?
			StreamGobbler errorGobbler = new
			StreamGobbler(proc.getErrorStream(), "ERROR");

			// any output?
			StreamGobbler outputGobbler = new
			StreamGobbler(proc.getInputStream(), "OUTPUT");

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);

      	} catch (Throwable t){
			t.printStackTrace();
	  	}
    } // end [runtimeExecuteAndWait(String cmd)]

} // end [class DocHiveToolbox]