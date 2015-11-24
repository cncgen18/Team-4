package serverStuff;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;




public class Client {
	private static Socket clientSocket;
	private static ServerSocket  server;
	
	public static void main(String[] args)
	{
		System.out.println("How many requests do you have?");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		try
		{
			server = new ServerSocket(1234);
			for (int x = 0; x < i; x++)
			{
				clientSocket = server.accept();
				Request r = new Request(clientSocket);
				new Thread(r).start();
			}
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
