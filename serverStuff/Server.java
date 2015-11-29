package serverStuff;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class Server
{

	private static Map<FSPair, Integer> cache = new HashMap<FSPair, Integer>();
	private static ArrayList<FSPair> memory = new ArrayList<FSPair>();
	private static int cacheLimit;
	private static ServerSocket  server;
	private static File f = new File("logForm.txt");
	private static String outputString = "";



	public static void main(String[] args)
	{
		fileGenerator();
		System.out.println("How many files will be in the cache?");
		Scanner sc = new Scanner(System.in);
		int i = sc.nextInt();
		sc.close();
		setCacheLimit(i);

		try
		{
			server = new ServerSocket(8241);
			while(true)
			{
				Socket client = server.accept();
				RequestResponder rr = new RequestResponder(client);
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
		if (cache.containsKey(newEntry))  //if reOrder was called and the file is already in the cache...
		{
			int border = cache.get(newEntry);

			for (FSPair i : cache.keySet())  //increase the ints of everything but said file by one
			{
				if (cache.get(i) ==cacheLimit)
				{

				}
				else if (cache.get(i) < border)
				{
					cache.replace(i, cache.get(i), cache.get(i)+1);

				}
				else if (cache.get(i) == border)  //and reset the file to the value one.
				{
					cache.replace(i, cache.get(i), 1);
				}
			}
		}
		else  //if the file isn't in the cache...
		{
			if (cache.size() >= cacheLimit)
			{
				for (FSPair i : cache.keySet())
				{
					if (cache.get(i) == cacheLimit)  //Find the item in the cache that has gone the longes without
						cache.remove(i);  			//being called up and delete it from the cache.
				}
			}
			for ( FSPair i : cache.keySet())
				cache.replace(i, cache.get(i), cache.get(i)+1);  //Everything remaining goes up by one
			cache.put(newEntry, 1);	//add the new guy
		}
	}

	public static FSPair memLookup(String fileName)
	{//This will look through the memory array list for the fileName.
		FSPair possible = new FSPair("error");
		for (FSPair i : memory)
		{
			if (fileName.equalsIgnoreCase(i.getName()))  //when found, set the name and file to the object
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
		Set<String> nameSet = new HashSet<String>();  //make a set for the names in the cache
		for (FSPair i : set)
		{
			nameSet.add(i.getName());  //fill up the nameSet.
		}
		if (!nameSet.contains(fileName))  //if the file isn't in the nameSet
		{
			FSPair errorPair = new FSPair("error");
			return errorPair;  //return error
		}
		else  //if it's in there
		{
			FSPair x = null;
			for (FSPair i : set)  //find it and return it.
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
				long rand1 = System.currentTimeMillis();  //Use the current time to get a random file size
				long rand2 = System.currentTimeMillis();
				int size = (int)((rand1 * rand2) % 10000);  //mod by 100000 to keep within range.
				while (size >100000 || size <1000) //if it's out of range
				{
					if (size < 1000) size*=1000; //multiply by 1K if it's too small
					if (size > 100000) size = size % 100000; //divide by 100K if too big.
				}
				File newInput = new File(str + ".txt");  //make file for memory.
				PrintWriter writer = new PrintWriter(newInput);
				int x = 0;
				int y = 0;
				while (x < size)  //get size number of random letters and populate file with them.
				{
					 long rand3 = (long)(Math.random() * System.currentTimeMillis());
					 writer.print(alphabet.charAt((int)(rand3%26)));
					 x++;
					 y++;
					 if (y==100)
					 {
					 	writer.print('\n');
					 	y=0;
					 }
				}
				writer.close();
				memory.add(new FSPair(str, newInput));  //When the file's populated, add it to memory
				str = br.readLine();
			}
			br.close();
		}
		catch (IOException e)
		{
			System.out.println("Something went wrong with the file.");
		}
	}

	public static  void logRequest(String name, Date start, Date end, int size, boolean hitMiss, String status)
	{//This will print the above information to the logFile.
		try
		{
			PrintWriter pw = new PrintWriter(f);
			pw.print(outputString);
			String output= "";
			output+=name;
			output+="\t";
			output+=start.toString().substring(11, 19);
			output+="\t";
			output+=end.toString().substring(11, 19);
			output+="\t";
			output+=size;
			output+="\t";
			if (hitMiss)output+="hit";
			else output+="miss";
			output+="\t";
			output+=status;
			pw.write(output+ "\n");
			outputString +=output + "\n";
			pw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}

class RequestResponder implements Runnable
{
	private Socket client;
	private DataInputStream in;
	private PrintWriter out;

	private static Semaphore cacheSemaphore = new Semaphore(1, true);
	private static Semaphore logSemaphore = new Semaphore(1, true);



	private FileInputStream fis;
	private BufferedInputStream bis;
	private OutputStream os;
	private Date startTime;
	private Date endTime;
	private boolean cacheHit;
	private String message = "200";

	public RequestResponder(Socket socket)
	{
		this.client = socket;
	}
	public void run()
	{
		startTime = new Date();

		try
		{
			DataInputStream in = new DataInputStream(client.getInputStream());
        	DataOutputStream out = new DataOutputStream(client.getOutputStream());
			String pageName = in.readUTF();  //gets name of file that user wants
			FSPair possible = null;  //This is what's passed back.
			cacheSemaphore.acquire();
			possible = Server.cacheLookup(pageName);  //This is what's returned from the cacheLookup
			cacheSemaphore.release();


			if(possible.getName().equalsIgnoreCase("error"))  //cache Lookup failure
			{
				possible = Server.memLookup(pageName);  //look in memory
				if(possible.getName().equalsIgnoreCase("error"))  //If that's an error too
				{
					message = "404";
					//possible.setName("404 Error");  //Make possible have a 404 error.

				}
				else  //If it was found in memory
				{
					cacheSemaphore.acquire();
					Server.reOrder(possible);  //reorder cache
					cacheSemaphore.release();
				}
			}  //if it was in the cache...
			else
			{
				cacheHit = true;
				cacheSemaphore.acquire();
				Server.reOrder(possible);  //reorder cache
				cacheSemaphore.release();
			}
			//output it through the socket

			if (message.equalsIgnoreCase("200"))
			{
				File f = possible.getFile();  //print it through the socket byte by byte
				BufferedReader fRead = new BufferedReader(new FileReader(f));
				String line = fRead.readLine();
				while(line !=null)
				{
					out.writeUTF(line);
					line = fRead.readLine();
				}
			}
			else
			{
				out.writeUTF("Error 404 -  File Not Found.");
			}
	        in.close();
	        out.close();
	        endTime = new Date();
			logSemaphore.acquire();
			Server.logRequest(possible.getName(), startTime, endTime, (int)possible.getFile().length(),
								cacheHit, message);  //logs request for file
			logSemaphore.release();

		}
		catch (Exception e)
		{

		}


	}
}




