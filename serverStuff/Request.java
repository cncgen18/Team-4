package serverStuff;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
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
		String name = sc.next();
		//add HTML
		String htmlPage;
		try
		{
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(htmlPage);
			in.readUTF(arg0);
		}
		catch(IOException e){}
		
		
		
		
				// TODO Auto-generated method stub
		
	}
	
}