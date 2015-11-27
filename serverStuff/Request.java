package serverStuff;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Request implements Runnable
{

	private String pageName;
	private File page;
	private DataInputStream  in;
	private DataOutputStream out;
	private Socket socket;
	public Request(Socket socket)
	{
		this.socket = socket;
	}
	@Override
	public void run() {
		System.out.println("Please request a page.");
		Scanner sc = new Scanner(System.in);
		pageName = sc.next();
		sc.close();
		try
		{
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			
			out.writeUTF(pageName);
			
			page = new File("output.txt");
			PrintWriter logger = new PrintWriter(page);
			int bytesRead;
			int current = 0;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			byte [] mybytearray  = new byte [100000];
			fos = new FileOutputStream(page);
			bos = new BufferedOutputStream(fos);
			InputStream is = socket.getInputStream();
			String result = in.readUTF();
			if(result.equalsIgnoreCase("error"))
			{
				logger.write("404 Error - File Not Found");
			}
			else 
			{
				bytesRead = is.read(mybytearray, 0, mybytearray.length);
				current = bytesRead;
				do
				{
					bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
					if (bytesRead >=0) current+=bytesRead;
				}
				while (bytesRead > -1);
				bos.write(mybytearray, 0, current);
				bos.flush();
			}
			//print file to user someway
			logger.close();
			bos.close();
			
		}
		catch(IOException e){}
		
		
		
		
				// TODO Auto-generated method stub
		
	}
	
}