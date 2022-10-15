package main.core;

public class Converter {
	
	public static String stringify(Object input) 
	{
		return (input instanceof String) ? (String)input : input.toString();
	}
	
	public static String format(String message, Object... args) 
	{
		return (args.length > 0) ? String.format(message, args) : message;
	}
	
}
