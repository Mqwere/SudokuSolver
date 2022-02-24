package core;

import java.util.ArrayList;
import java.util.Date;

import threads.SudokuThread;

public class SudokuTable implements Comparable<Object>{
	private SquareSudokuMatrix content;
	private ProgressionMap progressMap;
	public int size;
	public Date startingTime;
	
	public SudokuTable() { this(9); }
	public SudokuTable(int size)
	{
		this.size = size;
		this.progressMap = new ProgressionMap(this);
		this.content = new SquareSudokuMatrix(size);
	}
	
	public SudokuTable(SquareSudokuMatrix matrix)
	{
		this.size = matrix.getSize();
		this.progressMap = new ProgressionMap(this);
		content = matrix;
	}
	
	public boolean isValidMove(int row, int col, int val)
	{
		content.validate(row, col, val);
		
		for(int x=0; x<content.getSize(); x++)
			if(val == content.get(row, x+1).value) return false;
		
		for(int y=0; y<content.getSize(); y++)
			if(val == content.get(y+1, col).value) return false;

		int 
			initX = (((col-1)/3) * 3) + 1, 
			initY = (((row-1)/3) * 3) + 1,
			
			value;
		
		for(int y = 0; y<3; y++)
		{
			for(int x = 0; x<3; x++)
			{
				value = content.get(initY+y, initX+x).value;
				///Program.print("isValidMove for move (%d,%d,%d) - checking field [%d-%d] (%d)\n", row, col, val, initY+y, initX+x, value);
				if(val == value)
					return false;
			}
		}
			
		return true;
	}
	
	public void commenceAnalysis()
	{
		Program.print("Initiating analysis for the table below:\n%s\n", this.toTableString());
		this.startingTime = new Date();
		analyzeSelf();
	}
	
	private void analyzeSelf()
	{
		if(thereIsAtLeastOneUndefinedValue())
			SudokuThread
				.getTableAnalyzer(this)
				.start();
		else {
			Program.print("Completed analysis yielded the table below:\n%s", this.toTableString());
			Program.print("Analysis took %.3f seconds in total.", (float)(new Date().getTime() - startingTime.getTime())/1000f);

			if(Program.referenceWindow) Program.window.notifyAboutFinalization();
		}
	}
	
	public void notifyAboutAnalysisCompletion()
	{
		if(!foundAndAppliedObviousMoves())
		{
			if(!foundNonobviousMovesAndDecidedOnBestOne()) {
				Program.print("Failed analysis yielded the table below:\n%s", this.toTableString());
				throw new AnalyzerException("Failed to yield a conclusive answer.");
			}
		}
		content.clearPossibleValues();
		if(Program.referenceWindow) Program.window.notifyAboutAnalysisCompletion();
		analyzeSelf();
	}
	
	private boolean thereIsAtLeastOneUndefinedValue()
	{
		for(int y = 1; y <= size; y++)
			for(int x = 1; x <= size; x++)
				if(get(y,x).value == 0)
					return true;
		
		return false;
	}
	
	private boolean foundAndAppliedObviousMoves()
	{	
		int counter = 0;
		
		for(int y = 1; y <= size; y++)
		{
			for(int x = 1; x <= size; x++)
			{
				SudokuField field;
				if((field = get(y,x)).numberOfPossibleValues == 1)
				{
					ArrayList<Integer> possibleValues = field.getPossibleValues();
					int chosenValue = possibleValues.get(0);
					progressMap.setTarget(y, x, possibleValues);
					set(y, x, chosenValue);
					progressMap.moveDown(chosenValue);
					counter++;
				}
			}
		}
		
		if(Program.verbose && counter>0) Program.print("%d obvious moves.",counter);
		
		return counter!=0;
	}
	
	private boolean foundNonobviousMovesAndDecidedOnBestOne()
	{
		boolean foundOne = false;
		int 
			y, x,
			minPosNr = 0, 
			minPosY = 0, 
			minPosX = 0;
		SudokuField field;
		
		for(y = 1; y <= size; y++)
		{
			for(x = 1; x <= size; x++)
			{
				if((field = get(y,x)).numberOfPossibleValues !=0 && (field.numberOfPossibleValues < minPosNr || minPosNr == 0))
				{
					minPosNr 	= field.numberOfPossibleValues;
					minPosY		= y;
					minPosX		= x;
					foundOne	= true;
				}
			}
		}
		
		y = minPosY;
		x = minPosX;
		
		if(foundOne)
		{
			field = get(y, x);
			ArrayList<Integer> possibleValues = field.getPossibleValues();
			if(Program.verbose) Program.print("Unobvious move, out of %d possible.", possibleValues.size());
			int chosenValue = possibleValues.get(0);
			progressMap.setTarget(y, x, possibleValues);
			set(y, x, chosenValue);
			progressMap.moveDown(chosenValue);
		}
		else
		{
			try {
				ArrayList<Integer> possibleChildren;
				int counter = 0;
				do {
					revertChangeCreatedByProgressionNode(progressMap.getFieldChangedByCurrentNode());
					progressMap.moveUp();
					counter++;
					possibleChildren = progressMap.getPossibleChildrenValues();
				} while(possibleChildren.size() <= 0);
				
				int chosenValue = possibleChildren.get(0);
				if(Program.verbose) Program.print("Had to revert %d time(s) 1/%d possible values.", counter, possibleChildren.size());
				set(progressMap.currentNode.row, progressMap.currentNode.col, chosenValue);
				progressMap.moveDown(chosenValue);
				foundOne = true;
			}
			catch(Exception e) {
				Program.print("Exception catched in unobvious block:");
				e.printStackTrace();
			}
		}
		
		return foundOne;
	}
	
	private void revertChangeCreatedByProgressionNode(SudokuFieldLite change) 
	{
		this.get(change.row, change.col).value = 0;
	}
	
	public SudokuField get(int row, int col)
	{
		return content.get(row, col);
	}
	
	private void set(int row, int col, int val)
	{
		content.set(row, col, val);
	}
	
	public SudokuTable clone()
	{
		return new SudokuTable(content.clone());
	}
	
	final static String
		CELL_CEILING_PART = "---", CELL_CEILING_CORNER = "+",
		CELL_WALL_PART = "|",
		CELL_INSIDE_PADDING = " ";
	
	private String buildTableCeiling()
	{
		String output = SudokuTable.CELL_CEILING_CORNER;
		for (int i = 0; i < size; i++)
			output += SudokuTable.CELL_CEILING_PART + SudokuTable.CELL_CEILING_CORNER;
		return output;
	}
	
	public String toTableString()
	{
		String 
			ceiling = this.buildTableCeiling(),
			output = ceiling,
			inside;
		
		int value;
		
		for(int y = 1; y <= size; y++)
		{
			output += "\n";
			for(int x = 1; x <= size; x++)
			{
				inside = (value = get(y, x).value) == 0 ? " " : "" + value;
				output += SudokuTable.CELL_WALL_PART + SudokuTable.CELL_INSIDE_PADDING + inside + SudokuTable.CELL_INSIDE_PADDING;
			}
			output += SudokuTable.CELL_WALL_PART + "\n" + ceiling;
		}
		
		return output;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(o instanceof SudokuTable)
		{
			SudokuTable st = (SudokuTable)o;
			if(st.content.equals(this.content)) return true;
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		String output = "========================================\nTable:";
		for(int y = 1; y <= size; y++)
		{
			for(int x = 1; x <= size; x++)
			{
				SudokuField field = get(y,x);
				if(field.numberOfPossibleValues!=0)output += String.format("\n[%d-%d]: %s", y, x, field.toString());
			}
		}
		output+= "\n========================================";
		
		return output;
	}
	@Override
	public int compareTo(Object o) {
		if(this.equals(o))
			return 0;
		else {
			return o.hashCode() - this.hashCode();
		}
	}
	
}

class AnalyzerException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public AnalyzerException(String message, Object...args) { super(String.format(message, args)); }
}
