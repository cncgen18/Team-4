package serverStuff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class RequestResponder implements Runnable
{
	private Socket socket;
	private String pageName;
	private File page;
	private DataInputStream  in;
	private DataOutputStream out;
	private static int logLimit = 1;
	private static int cacheLimit = 1;
	public RequestResponder(Socket socket)
	{
		this.socket = socket;
	}
	public void run()
	{
		try
		{
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			String pageName = in.readUTF();
			FSPair possible = null;
			while (cacheLimit < 1)
			{
				
			}
			if (cacheLimit >=1)
			{
				cacheLimit--;
				possible = LoggingForm.cacheLookup(pageName);
				cacheLimit++;
			}
			
			if(possible.getName().equalsIgnoreCase("error"))  //cache Lookup failure
			{
				possible = LoggingForm.memLookup(pageName);
				if(possible.getName().equalsIgnoreCase("error"))
				{
					possible.setName("404 Error");
					
				}
				else
				{
					while (cacheLimit < 1)
					{
						
					}
					if (cacheLimit >=1)
					{
						cacheLimit--;
						LoggingForm.reOrder(possible);
						cacheLimit++;
					}
					File f = possible.getFile();
					byte [] mybytearray  = new byte [(int)f.length()];
			        FileInputStream fis = new FileInputStream(f);
			        BufferedInputStream bis = new BufferedInputStream(fis);
			        bis.read(mybytearray,0,mybytearray.length);
			        OutputStream os = socket.getOutputStream();
			        os.write(mybytearray,0,mybytearray.length);
			        os.flush();			
				}
			}
			else
			{
				//pass back to request
				while (logLimit < 1)
				{
					
				}
				if (logLimit >=1)
				{
					logLimit--;
					LoggingForm.logRequest();
					logLimit++;
				}
			}
		}
		catch (IOException e)
		{
			
		}
		
		
	}
}