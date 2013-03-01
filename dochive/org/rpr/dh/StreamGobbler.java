package org.rpr.dh;

import java.io.*;

//---------------------------------------------------------
// This class reads an input stream in an extra thread.
// This is used for running external programms out of java
// so that stdout and stderr are not blocking the process
// from work. If the stream is closed by Runtime the
// gobbler will also die. Poor dead gobbler.
//---------------------------------------------------------
class StreamGobbler extends Thread
{
  InputStream is;
  String type;

  //-------------------------------------------------------
  // Ititialize
  //-------------------------------------------------------
  StreamGobbler(InputStream is, String type)
  {
	this.is = is;
	this.type = type;
  }

  //-------------------------------------------------------
  // While there is output continue reading.
  //-------------------------------------------------------
  public void run()
  {
	try
    {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line=null;
      while ( (line = br.readLine()) != null)
		System.out.println(type + ">" + line);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
