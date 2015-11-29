package serverStuff;

import java.io.*;
import java.net.*;
import java.util.Scanner;





public class Client{
	private static ServerSocket  server;

	public static void main(String[] args)
	{
		System.out.println("How many requests do you have?");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		sc.close();
		try
		{

			BufferedReader br = new BufferedReader(new FileReader("input.txt"));

			//server = new ServerSocket(8241);
			for (int x = 0; x < i; x++)
			{

				Socket socket = new Socket("localhost", 8241);

				String line = br.readLine();
				Request r = new Request(socket, line);
				new Thread(r).start();

			}
			br.close();
		}
		catch (IOException e)
		{

		}

			//String page = sc.next();
			//add HTML stuff



		/*
		System.out.println("GET / HTTP/1.1");
		System.out.println("User-Agent: test/1.0");
		System.out.println("Host: localhost");

		*/
	}
}

class Request implements Runnable
{

	private String pageName;
	private File page;
	private Socket socket;

	public Request(Socket socket, String name)
	{
		this.socket = socket;
		this.pageName = name;
	}

	@Override
	public void run() {
		try(
		DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		)
		{
			System.out.println(pageName);
			out.writeUTF(pageName);
			page = new File("output " + pageName + ".txt");
			PrintWriter fileOut = new PrintWriter(page);

			String line = in.readUTF();
			while(line !=null)
			{
				fileOut.print(line + "\n");
				line = in.readUTF();
			}
			out.close();
			in.close();
			fileOut.close();

			//print file to user someway


		}
		catch(IOException e){}




				// TODO Auto-generated method stub

	}

}
