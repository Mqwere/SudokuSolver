package main.core;

public class UnableToSolveException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public UnableToSolveException(String message, Object...args) { super(String.format(message, args)); }
}
