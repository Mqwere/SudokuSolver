package main.core;

import java.util.ArrayList;

public class ProgressionMap {
	public ProgressionNode currentNode;
	
	SudokuTable master;
	
	public ProgressionMap(SudokuTable master)
	{
		this.master = master;
		this.currentNode = new ProgressionNode(null, 0);
	}
	
	public ArrayList<Integer> getPossibleChildrenValues()
	{
		return this.currentNode.possibleValues;
	}
	
	public void moveUp() 
	{
		if(currentNode.parent!=null)
			this.currentNode = currentNode.parent;
		else
			throw new ProgressionMapException("End of the map reached.");
	}
	
	public void moveDown(int possibleValueChosen)
	{
		this.currentNode = new ProgressionNode(currentNode, possibleValueChosen);
	}
	
	public void setTarget(int row, int col, ArrayList<Integer> possibleValues)
	{
		if(currentNode.targetIsSet()) return;
		this.currentNode.setTarget(row, col);
		this.currentNode.addPossibleValues(possibleValues);
	}
	
	public void addPossibleValues(ArrayList<Integer> possibleValues)
	{
		this.currentNode.addPossibleValues(possibleValues);
	}
	
	public SudokuFieldLite getFieldChangedByCurrentNode()
	{
		return currentNode.getFieldChangedByThisNode();
	}
}

class ProgressionMapException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public ProgressionMapException(String message, Object...args) { super(String.format(message, args)); }
}