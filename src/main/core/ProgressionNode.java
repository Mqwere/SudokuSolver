package main.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgressionNode {
	public int
		row = 0, col = 0, parentPossibleValueChosen;
	
	public ProgressionNode parent;
	
	public HashMap<Integer, ProgressionNode> children;
	
	public ArrayList<Integer> possibleValues;
	
	public ProgressionNode
	(
		ProgressionNode parent, 
		int parentPossibleValueChosen
	)
	{
		this.parent = parent;
		this.parentPossibleValueChosen = parentPossibleValueChosen;
		this.children = new HashMap<>();
		this.possibleValues = new ArrayList<>();
		
		if(this.parent != null)
			this.parent.addChild(parentPossibleValueChosen, this);
	}
	
	public void setTarget(int row, int col)
	{
		this.row = row;
		this.col = col;
	}
	
	public boolean targetIsSet() 
	{
		return this.row!=0 && this.col!=0 && possibleValues.size()>0;
	}
		
	public void addPossibleValues(ArrayList<Integer> possibleValues)
	{
		this.possibleValues.addAll(possibleValues);
	}
	
	public void removePossibleValue(int value)
	{
		ArrayList<Integer> values = new ArrayList<>();
		for(int val: this.possibleValues)
			if(val != value) values.add(val);
		
		this.possibleValues = values;
	}
	
	public boolean hasChild(int value)
	{
		return this.children.containsKey(value);
	}
	
	public void addChild(int value, ProgressionNode child)
	{
		if(children.containsKey(value))		throw new ProgressionNodeException("This Progression Node already contains a child with the given value (%d).", value);
		if(!possibleValues.contains(value)) throw new ProgressionNodeException("The given value (%d) is not contained within the possible values array.", value);
		
		this.children.put(value, child);
		this.removePossibleValue(value);
	}
	
	public SudokuFieldLite getFieldChangedByThisNode()
	{
		if(parent == null) throw new UnableToSolveException("SudokuSolver could not solve this table.");
		return new SudokuFieldLite(parent.row, parent.col, this.parentPossibleValueChosen);
	}
	
	@Override
	public String toString() 
	{
		return String.format("(%2d,%2d,%2d)", row, col, this.parentPossibleValueChosen);
	}

}

class ProgressionNodeException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public ProgressionNodeException(String message, Object...args) { super(String.format(message, args)); }
}
