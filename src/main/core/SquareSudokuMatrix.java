package main.core;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SquareSudokuMatrix 
{
	private ArrayList<ArrayList<SudokuField>> content = new ArrayList<>();
	private int size;
	
	public SquareSudokuMatrix(int size)
	{
		this.size = size;
		for(int y=0; y<size; y++)
		{
			ArrayList<SudokuField> row = new ArrayList<>();
			for(int x=0; x<size; x++)
				row.add(new SudokuField(0));
			content.add(row);
		}
	}
	
	public SquareSudokuMatrix(int size, String input)
	{
		this.size = size;
		int s = 0;
		if(input.length()<81)
			for(int i=input.length(); i<=81; i++)
				input+=0;
		
		for(int y=0; y<size; y++)
		{
			ArrayList<SudokuField> row = new ArrayList<>();
			for(int x=0; x<size; x++) {
				row.add(new SudokuField(Integer.parseInt(input.substring(s, ++s))));
			}
			content.add(row);
		}
	}
	
	private SquareSudokuMatrix(ArrayList<ArrayList<SudokuField>> data)
	{
		this.size = data.size();
		for(int y=0; y<size; y++)
		{
			ArrayList<SudokuField> row = new ArrayList<>();
			for(int x=0; x<size; x++)
				row.add(new SudokuField(data.get(y).get(x).value));
			content.add(row);
		}
	}
	
	private void validate(int row, int col)
	{
		validate(row, col, 1);
	}
	
	public void validate(int row, int col, int val)
	{
		if(row<=0 || row>size) throw new MatrixException("Invalid row number (%d).", row);
		if(col<=0 || col>size) throw new MatrixException("Invalid column number (%d).", col);
		if(val<=0 || val>size) throw new MatrixException("Invalid value (%d).", val);
	}
	
	public void set(int row, int col, int val)
	{
		validate(row, col, val);
		content.get(row-1).set(col-1, new SudokuField(val));
	}
	
	public SudokuField get(int row, int col)
	{
		validate(row, col);
		return content.get(row-1).get(col-1);
	}
	
	public int getSize()
	{
		return size;
	}
	
	public void clearPossibleValues()
	{
		for(int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; x++)
				content.get(y).set(x, new SudokuField(content.get(y).get(x).value));
		}
	}
	
	public SquareSudokuMatrix clone()
	{
		return new SquareSudokuMatrix(content);
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(o instanceof SquareSudokuMatrix)
		{
			SquareSudokuMatrix ssm = (SquareSudokuMatrix)o;
			if(ssm.content.equals(this.content)) return true;
		}
		return false;
	}

	public void forEachElement(Consumer<? super SudokuField> action)
	{
		this.content
			.stream()
			.forEachOrdered
			(
				(subcontent) ->
				{
					subcontent.stream()
					.forEachOrdered(action);
				}
			);
	}
	
	public Stream<SudokuField> stream()
	{
		return content.stream().flatMap(ArrayList::stream);
	}
}

class MatrixException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public MatrixException(String message, Object...args) { super(String.format(message, args)); }
}
