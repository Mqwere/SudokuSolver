package main.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SudokuField {
	public int value, numberOfPossibleValues = 0;
	private ArrayList<Integer> possibleValues = new ArrayList<>();
	
	public SudokuField(int value)
	{
		this.value = value;
	}
	
	public void addPossibleValue(int value) 
	{ 
		this.possibleValues.add(value); 
		numberOfPossibleValues++; 
	}
	
	public void addPossibleValues(Collection<Integer> values) 
	{ 
		this.possibleValues.addAll(values);
		numberOfPossibleValues += values.size(); 
	}
	
	public void removePossibleValue(int value)
	{
		if(possibleValues.contains(value))
		{
			possibleValues = new ArrayList<>
			(
				possibleValues
					.stream()
					.filter
					(
						(Integer num) -> 
							{
								return num != value;
							}
					)
					.collect(Collectors.toList())
			);
			
			numberOfPossibleValues--; 
		}
	}
	
	public ArrayList<Integer> getPossibleValues() { return this.possibleValues; }
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(o instanceof SudokuField)
		{
			SudokuField sf = (SudokuField)o;
			if( sf.value == this.value && 
				sf.possibleValues.equals(this.possibleValues) ) 
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() 
	{
		String posVals = " ";
		for(int val:possibleValues) posVals += val+" ";
		return  String.format("[%d] PV(%s)", value, posVals);
	}
}
