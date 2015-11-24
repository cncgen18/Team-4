package serverStuff;

import java.io.File;

public class FSPair
{
	String fileName;
	File file;
	
	public FSPair(String name, File f)
	{
		this.file = f;
		this.fileName = name;
	}
	public FSPair(String name)
	{
		this.fileName = name;
		this.file = null;
	}
	public String getName()
	{
		return fileName;
	}
	public File getFile()
	{
		return file;
	}
	public void setName(String name)
	{
		this.fileName = name;
	}
	public void setfile(File f)
	{
		this.file = f;
	}
	
}