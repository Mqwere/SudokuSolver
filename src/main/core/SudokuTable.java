package main.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import main.threads.SudokuThread;

public class SudokuTable implements Comparable<Object>{
	public int size;
	public Date startingTime;
	
	private SquareSudokuMatrix content;
	private ProgressionMap progressMap;
	private final static String
		CELL_CEILING_PART = "---", CELL_CEILING_CORNER = "+",
		CELL_WALL_PART = "|",
		CELL_INSIDE_PADDING = " ";
	
	public SudokuTable() { this(9); }
	public SudokuTable(int size)
	{
		this(new SquareSudokuMatrix(size));
	}
		
	public SudokuTable(SquareSudokuMatrix matrix)
	{
		this.size = matrix.getSize();
		this.progressMap = new ProgressionMap(this);
		content = matrix;
	}
	
	public boolean isValidMove(SudokuFieldLite coords, int val)
	{
		//SudokuSolver.verbosePrintln("[%d, %d] -?-> %d", coords.row, coords.col, val);
		return isValidMove(coords.row, coords.col, val);
	}
	
	public boolean isValidMove(int row, int col, int val)
	{
		content.validate(row, col, val);
		
		return getAffectedFieldsCoords(row, col)
			.stream()
			.allMatch( (sf) -> { return val != get(sf).value; } );
	}
	
	public void threadlessContainedAnalysis()
	{
		List<Integer> allPossibleValues = Arrays.asList(1,2,3,4,5,6,7,8,9);
		
		content
			.stream()
			.forEach
			(
				(field) ->
				{
					field.addPossibleValues(allPossibleValues);
				}
			);
		
		for (int y = 0; y < size; y++)
		{
			for(int x = 0; x < size; y++)
			{
				int value;
				if((value = get(y,x).value) == 0) continue;
				reflectSetInPossibleValues(y, x, value);
			}
		}
	}
	
	public void commenceAnalysis() { commenceAnalysis(true); }
	public void commenceAnalysis(boolean isOriginalTable)
	{
		if(!validate()) return;
		SudokuSolver.println("Initiating analysis for the table below:\n%s\n", this.toTableString());
		this.startingTime = new Date();
		SudokuThread
			.getTableAnalyzer(this)
			.start();
	}
	
	private void analyzeSelf()
	{
		if(thereIsAtLeastOneUndefinedValue())
		{
			notifyAboutAnalysisCompletion();
//			SudokuThread
//				.getTableAnalyzer(this)
//				.start();
		}
		else 
		{
			printFinishedAnalysisMessage();
		}
	}
	
	public void notifyAboutAnalysisCompletion()
	{
		try 
		{
			//printPossibleValuesForDebugPurposes();
			
			if(!foundAndAppliedObviousMoves())
			{
				if(!foundNonobviousMovesAndDecidedOnBestOne()) {
					//SudokuSolver.println("Failed analysis yielded the table below:\n%s", this.toTableString());
					throw new AnalyzerException("Failed to yield a conclusive answer.");
				}
			}
			//content.clearPossibleValues();
			if(SudokuSolver.referenceWindow) SudokuSolver.window.notifyAboutAnalysisCompletion();
			if(SudokuSolver.sleep) Thread.sleep(10);
			analyzeSelf();
		}
		catch(UnableToSolveException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			SudokuSolver.println("There was an exception, printing pre-error table state:");
			printFinishedAnalysisMessage();
			e.printStackTrace();
		}
	}
	public SudokuField get(SudokuFieldLite field)
	{
		return content.get(field.row, field.col);
	}
	
	public SudokuField get(int row, int col)
	{
		return content.get(row, col);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(content, progressMap, size, startingTime);
	}
	
	public SudokuTable clone()
	{
		return new SudokuTable(content.clone());
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
	
	public boolean validate()
	{
		for(int row = 1; row<size; row++) {
			for(int col = 1; col<size; col++) {
				int val = get(row,col).value;
				if(val == 0) continue;
				for(int x=0; x<content.getSize(); x++) {
					if(x+1!=col && val == content.get(row, x+1).value) {
						SudokuSolver.println("Validation failed for row falidation of cell %d,%d - conflict with cell %d,%d.", row, col, row, x+1);
						return false;
					}
				}
				
				for(int y=0; y<content.getSize(); y++) {
					if(y+1!=row && val == content.get(y+1, col).value) {
						SudokuSolver.println("Validation failed for column falidation of cell %d,%d - conflict with cell %d,%d.", row, col, y+1, col);
						return false;
					}
				}
				
				int 
					initX = (((col-1)/3) * 3) + 1, 
					initY = (((row-1)/3) * 3) + 1;
				
				for(int y = 0; y<3; y++)
				{
					for(int x = 0; x<3; x++)
					{
						if(initY+y == row && initX+x == col) continue;
						if(val == content.get(initY+y, initX+x).value) {
							SudokuSolver.println("Validation failed for square of cell %d,%d - conflict with cell %d,%d.", row, col, initY+y, initX+x);
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void set(int row, int col, int val)
	{
		content.set(row, col, val);
		SudokuSolver.verbosePrintln("[%d, %d] -> %d", row, col, val);
		reflectSetInPossibleValues(row, col, val);
	}
	
	private ArrayList<SudokuFieldLite> getAffectedFieldsCoords(int row, int col)
	{
		ArrayList<SudokuFieldLite> output = new ArrayList<>();
		
		for(int x=0; x<content.getSize(); x++)
			output.add(new SudokuFieldLite(row, x+1, 0));
		
		for(int y=0; y<content.getSize(); y++)
			if(y+1!=row)
				output.add(new SudokuFieldLite(y+1, col, 0));

		int 
			initX = (((col-1)/3) * 3) + 1, 
			initY = (((row-1)/3) * 3) + 1;
		
		for(int y = 0; y<3; y++)
		{
			for(int x = 0; x<3; x++)
			{
				if(initY+y != row && initX+x != col)
					output.add(new SudokuFieldLite(initY+y, initX+x, 0));
//				else
//				{
//					if(SudokuSolver.verbose)
//						SudokuSolver.print("%s, %s, disengaging", initY+y != row, initX+x != col);
//				}
			}
		}
		
		if(SudokuSolver.verbose && output.size() != 21)
			SudokuSolver.println("There are %d/21 affected fields in the output.", output.size());
		
		return output;
	}

	private void revertChangeCreatedByProgressionNode(SudokuFieldLite change) 
	{
		this.get(change.row, change.col).value = 0;
		SudokuSolver.verbosePrintln("[%d, %d] <> %d -> 0", change.row, change.col, change.val);
		reflectRevertInPossibleValues(change.row, change.col, change.val);
	}
	
	private void reflectSetInPossibleValues(int row, int col, int val)
	{
		getAffectedFieldsCoords(row, col)
			.stream()
			.filter( (sf) -> { return get(sf).value == 0; } )
			.forEach( (sf) -> { get(sf).removePossibleValue(val); } );
	}

	private void reflectRevertInPossibleValues(int row, int col, int val)
	{
		getAffectedFieldsCoords(row, col)
			.stream()
			.filter( (sf) -> { return get(sf).value == 0 && isValidMove(sf, val); } )
			.forEach( (sf) -> { get(sf).addPossibleValue(val); } );
	}

	private boolean foundAndAppliedObviousMoves()
	{	
		//boolean foundOne = false;
		for(int y = 1; y <= size; y++)
		{
			for(int x = 1; x <= size; x++)
			{
				SudokuField field;
				if((field = get(y,x)).numberOfPossibleValues == 1)
				{
					ArrayList<Integer> possibleValues = field.getPossibleValues();
					int chosenValue = possibleValues.get(0);
					SudokuSolver.verbosePrintln("Lucky, [%d,%d] has 1 possibility!.", y, x);
					progressMap.setTarget(y, x, possibleValues);
					set(y, x, chosenValue);
					progressMap.moveDown(chosenValue);
					//foundOne = true;
					return true;
				}
			}
		}
		
		SudokuSolver.verbosePrintln("No luck, no obvious moves.");
		
		//return foundOne;
		return false;
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
			int chosenValue = possibleValues.get(0);
			progressMap.setTarget(y, x, possibleValues);
			set(y, x, chosenValue);
			if(SudokuSolver.verbose) 
				SudokuSolver.println("Unobvious move, out of %d possible.", possibleValues.size());
			progressMap.moveDown(chosenValue);
		}
		else
		{
				ArrayList<Integer> possibleChildren;
				int counter = 0;
				do {
					revertChangeCreatedByProgressionNode(progressMap.getFieldChangedByCurrentNode());
					progressMap.moveUp();
					counter++;
					possibleChildren = progressMap.getPossibleChildrenValues();
				} while(possibleChildren.size() <= 0);
				
				int chosenValue = possibleChildren.get(0);
				if(SudokuSolver.verbose) 
					SudokuSolver.println("Had to revert %d time(s) 1/%d possible values.", counter, possibleChildren.size());
				set(progressMap.currentNode.row, progressMap.currentNode.col, chosenValue);
				progressMap.moveDown(chosenValue);
				foundOne = true;
		}
		
		return foundOne;
	}
	
	private void foreachElement(Consumer<? super SudokuField> action)
	{
		this.content.forEachElement(action);
	}

	private void printFinishedAnalysisMessage()
	{
		SudokuSolver.println("Completed analysis yielded the table below:\n%s", this.toTableString());
		SudokuSolver.println("Analysis took %.3f seconds in total.", (float)(new Date().getTime() - startingTime.getTime())/1000f);

		if(SudokuSolver.referenceWindow) SudokuSolver.window.notifyAboutFinalization();
		SudokuSolver.println("The result table has %s the validation.", validate() ? "passed":"failed");
	}
	
	private void printPossibleValuesForDebugPurposes()
	{
		int y, x;
		SudokuField field;
		
		String output = "";
		
		for(y = 1; y <= size; y++)
		{
			for(x = 1; x <= size; x++)
			{
				if((field = get(y,x)).numberOfPossibleValues !=0)
				{
					output += "["+y+", "+x+"] -> {";
					for(int value: field.getPossibleValues())
						output += " " + value;
					output += " }\n";
				}
			}
		}
		SudokuSolver.print(output);
	}
	
	private boolean thereIsAtLeastOneUndefinedValue()
	{
		return !content
				.stream()
				.allMatch(
					(cell) -> 
					{
						return cell.value != 0;
					}
				);
	}
	
	private String buildTableCeiling()
	{
		String output = SudokuTable.CELL_CEILING_CORNER;
		for (int i = 0; i < size; i++)
			output += SudokuTable.CELL_CEILING_PART + SudokuTable.CELL_CEILING_CORNER;
		return output;
	}
	
}

class AnalyzerException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	public AnalyzerException(String message, Object...args) { super(String.format(message, args)); }
}
