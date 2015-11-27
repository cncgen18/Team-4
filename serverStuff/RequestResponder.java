package serverStuff;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

class RequestResponder implements Runnable
{
	private Socket socket;
	private DataInputStream  in;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private OutputStream os;
	private static int logLimit = 1;
	private static int cacheLimit = 1;
	private Date startTime;
	private Date endTime;
	private boolean cacheHit;
	private String message = "202";
	
	public RequestResponder(Socket socket)
	{
		this.socket = socket;
	}
	public void run()
	{
		startTime = new Date();
		try
		{
			in = new DataInputStream(socket.getInputStream());  //gets input and output for socket
			String pageName = in.readUTF();  //gets name of file that user wants
			FSPair possible = null;  //This is what's passed back.
			while (cacheLimit < 1)  //semaphore for cache access
			{
				
			}
			if (cacheLimit >=1)
			{
				cacheLimit--;
				possible = LoggingForm.cacheLookup(pageName);  //This is what's returned from the cacheLookup
				cacheLimit++;
			}
			
			if(possible.getName().equalsIgnoreCase("error"))  //cache Lookup failure
			{
				possible = LoggingForm.memLookup(pageName);  //look in memory
				if(possible.getName().equalsIgnoreCase("error"))  //If that's an error too
				{
					message = "404";
					//possible.setName("404 Error");  //Make possible have a 404 error.
					
				}
				else  //If it was found in memory
				{
					while (cacheLimit < 1)  //semaphore for cache access
					{
						
					}
					if (cacheLimit >=1)
					{
						cacheLimit--;
						LoggingForm.reOrder(possible);  //reorder cache
						cacheLimit++;
					}
					
					
				}
			}  //if it was in the cache...
			else
			{
				cacheHit = true;
				while (cacheLimit < 1)  //semaphore for cache access
				{
					
				}
				if (cacheLimit >=1)
				{
					cacheLimit--;
					LoggingForm.reOrder(possible);  //reorder cache
					cacheLimit++;
				}
			}
			//output it through the socket
			File f = possible.getFile();  //print it through the socket byte by byte
			byte [] mybytearray  = new byte [(int)f.length()];
	        fis = new FileInputStream(f);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length);
	        os = socket.getOutputStream();
	        os.write(mybytearray,0,mybytearray.length);
	        os.flush();		
	        endTime = new Date();
			while (logLimit < 1)  //sempahore for logFile access
			{
					
			}
			if (logLimit >=1)
			{
				logLimit--;
				LoggingForm.logRequest(possible.getName(), startTime, endTime, (int)possible.getFile().length(),
						cacheHit, message);  //logs request for file
				logLimit++;
			}
		}
		catch (IOException e)
		{
			
		}
		
		
	}
}