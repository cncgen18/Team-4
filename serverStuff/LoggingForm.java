package serverStuff;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class LoggingForm 
{
	
	private static Map<FSPair, Integer> cache = new HashMap<FSPair, Integer>();
	private static ArrayList<FSPair> memory = new ArrayList<FSPair>();
	private static int cacheLimit; 
	private static ServerSocket  server;
	private static Socket socket;
	private static DataInputStream  in;
	private static DataOutputStream out;
	File f = new File("logForm.txt");
	
	
	public static void main(String[] args )
	{
		fileGenerator();
		System.out.println("How many files will be in the cache?");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		sc.close();
		setCacheLimit(i);
		logRequest();
		try 
		{
			server = new ServerSocket(1234);
			while(true)
			{
				socket = server.accept();
				RequestResponder rr = new RequestResponder(socket);
				new Thread(rr).start();
			}
		}
		catch (IOException e) {}	
	}
	public static void setCacheLimit(int i )
	{
		cacheLimit = i;
	}
	public static void reOrder(FSPair newEntry)
	{
		if (cache.containsKey(newEntry))
		{	
			int border = cache.get(newEntry);
			
			for (FSPair i : cache.keySet())
			{
				if (cache.get(i) < border)
				{
					cache.replace(i, cache.get(i), cache.get(i)+1);
					
				}
				else if (cache.get(i) == border)
				{ 
					cache.replace(i, cache.get(i), 1);
				}
			}
		}
		else
		{
			if (cache.size() >= cacheLimit)
			{
				for (FSPair i : cache.keySet())
				{
					if (cache.get(i) == cacheLimit)
						cache.remove(i);
				}
			}
			for ( FSPair i : cache.keySet())
				cache.replace(i, cache.get(i), cache.get(i)+1);
			cache.put(newEntry, 1);	
		}
	}
	
	public static FSPair memLookup(String fileName)
	{
		FSPair possible = new FSPair("error");
		for (FSPair i : memory)
		{
			if (fileName.equalsIgnoreCase(i.getName()))
			{
				possible.setName(fileName);
				possible.setfile(i.getFile());
			}
		}
		return possible;
	}
	
	public static FSPair cacheLookup(String fileName)
	{
		Set<FSPair> set = cache.keySet();
		Set<String> nameSet = new HashSet<String>();
		for (FSPair i : set)
		{
			nameSet.add(i.getName());
		}
		if (!nameSet.contains(fileName))
		{
			FSPair errorPair = new FSPair("error");
			return errorPair;
		}
		else
		{
			FSPair x = null;
			for (FSPair i : set)
			{
				if (i.getName().equalsIgnoreCase(fileName))
				{
					x = i;
				}
			}
			return x;
		}
	}
	
	public static void fileGenerator()
	{		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader("fileGeneration.txt"));
			
			String str = br.readLine();
			String alphabet = "abcdefghijklmnopqrstuvwxyz";
			while (str!=null)
			{
				long rand1 = System.currentTimeMillis();  //Random file size
				long rand2 = System.currentTimeMillis();
				int size = (int)((rand1 * rand2) % 10000);
				while (size >100000 || size <1000)
				{
					if (size < 1000) size*=1000;
					if (size > 100000) size = size % 100000;
				}
				File f = new File(str + ".txt");
				PrintWriter writer = new PrintWriter(f);
				int x = 0;
				while (x < size)
				{
					 long rand3 = (long)(Math.random() * System.currentTimeMillis());
					 writer.print(alphabet.charAt((int)(rand3%26)));
					x++;
				}
				memory.add(new FSPair(str, f));
				writer.close();
				str = br.readLine();
			}
			br.close();
		}
		catch (IOException e)
		{
			System.out.println("Something went wrong with the file.");
		}
	}

	public static  void logRequest()
	{
		
		try 
		{
			
			PrintWriter logger;
			logger = new PrintWriter(f);
			
		int start = 0, end = 10, size = 12400;
		
		String url = "http://google.com";
		logger.print(url);
		Boolean cacheHit = true;
		Boolean find = true;
		String output = "";
		output+=url + "\t";
		output+=Integer.toString(start) + "\t";
		output+=Integer.toString(end) + "\t";
		output+=Integer.toString(size) + "\t";
		if (cacheHit) output+= "hit\t";
		else output+="miss\t";
		if (find) output+="200";
		else output +="400";
		logger.write(output+ "\n");
		logger.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
	
					



