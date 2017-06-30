package org.mcal.mcpe_dumper.nativeapi;

public class MCPEDumper
{
	public static native void load(String path);
	public static native String getNameAt(long pos);
	public static native String getDemangledNameAt(int pos);
	public static native short getTypeAt(int pos);
	public static native short getBindAt(int pos);
	public static native int getSize();
	public static native long getSizeAt(int pos);
	public static native String demangle(String name);
	public static native String demangleOnly(String name);
}
