package org.mcal.mcpe_dumper.nativeapi;

public class MCPESymbol
{
	private String name;
	private String demangledName;
	private int type;
	private int bind;
	private long size;
	
	public MCPESymbol(String name,String demangledName,int type,int bind,long size)
	{
		this.type=type;
		this.demangledName=demangledName;
		this.name=name;
		this.bind=bind;
		this.size=size;
	}

	public void setDemangledName(String demangledName)
	{
		this.demangledName = demangledName;
	}

	public String getDemangledName()
	{
		return demangledName;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
	
	public void setBind(int type)
	{
		this.type = type;
	}

	public int getBind()
	{
		return type;
	}
	
	public void setBind(long size)
	{
		this.size = size;
	}

	public long getSize()
	{
		return size;
	}
}
