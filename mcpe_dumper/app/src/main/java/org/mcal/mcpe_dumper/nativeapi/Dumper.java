package org.mcal.mcpe_dumper.nativeapi;
import java.util.*;
import org.mcal.mcpe_dumper.vtable.*;

public class Dumper
{
	public static ArrayList<MCPESymbol> symbols=new ArrayList<>();
	public static ArrayList<MCPEVtable> exploed=new ArrayList<>();
	public static ArrayList<MCPEClass> classes=new ArrayList<>();

	public static void readData(String path)
	{
		symbols.clear();
		exploed.clear();
		classes.clear();
		
		for (int i=0;i < MCPEDumper.getSize();++i)
		{
			String demangledName=MCPEDumper.getDemangledNameAt(i);
			if (demangledName == null || demangledName.isEmpty() || demangledName == "" || demangledName == " ")
				demangledName = MCPEDumper.getNameAt(i);
			MCPESymbol newSymbol=new MCPESymbol(MCPEDumper.getNameAt(i), demangledName, MCPEDumper.getTypeAt(i), MCPEDumper.getBindAt(i), MCPEDumper.getSizeAt(i));
			symbols.add(newSymbol);
		}
	}
}
