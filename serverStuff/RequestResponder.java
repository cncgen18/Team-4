package serverStuff;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

class RequestResponder implements Runnable
{
	private Socket socket;
	private String pageName;
	private File page;
	private DataInputStream  in;
	private DataOutputStream out;
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
			String htmlRequest = in.readUTF();
			//parse to get fileName
			String pageName = "";
			FSPair possible = LoggingForm.cacheLookup(pageName);
			if(possible.getName().equalsIgnoreCase("error"))
			{
				
			}
			else
			{
				
			}
		}
		catch (IOException e)
		{
			
		}
		
		
	}
}