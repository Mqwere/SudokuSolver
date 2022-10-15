package test.core;

import java.util.Arrays;

import main.core.SquareSudokuMatrix;
import main.core.SudokuTable;

public class SudokuTableTest
{
	//003000200060980043490031006907000860040098000005407109600003905508100072209056038
	
	private SquareSudokuMatrix getTestMatrix()
	{
		return new SquareSudokuMatrix(3,"003000200060980043490031006907000860040098000005407109600003905508100072209056038");
	}
	
	private SudokuTable getTestTable()
	{
		SquareSudokuMatrix subOutput = getTestMatrix();
		
		subOutput.get(1, 1).addPossibleValues(Arrays.asList(1, 7, 8));
		subOutput.get(1, 2).addPossibleValues(Arrays.asList(1, 5, 7, 8));
		subOutput.get(1, 4).addPossibleValues(Arrays.asList(5, 6, 7));
		subOutput.get(1, 5).addPossibleValues(Arrays.asList(4, 6, 7));
		subOutput.get(1, 6).addPossibleValues(Arrays.asList(4, 5));
		subOutput.get(1, 8).addPossibleValues(Arrays.asList(1, 5, 8, 9));
		subOutput.get(1, 9).addPossibleValues(Arrays.asList(1, 7, 9));

		subOutput.get(2, 1).addPossibleValues(Arrays.asList(1, 7));
		subOutput.get(2, 3).addPossibleValues(Arrays.asList(1, 2));
		subOutput.get(2, 6).addPossibleValues(Arrays.asList(2, 5));
		subOutput.get(2, 7).addPossibleValues(Arrays.asList(5, 7));
		
		subOutput.get(3, 3).addPossibleValues(Arrays.asList(2));
		subOutput.get(3, 4).addPossibleValues(Arrays.asList(2, 5, 7));
		subOutput.get(3, 7).addPossibleValues(Arrays.asList(5, 7));
		subOutput.get(3, 8).addPossibleValues(Arrays.asList(2, 5, 8));

		subOutput.get(4, 2).addPossibleValues(Arrays.asList(1, 2, 3));
		subOutput.get(4, 4).addPossibleValues(Arrays.asList(2, 3, 5));
		subOutput.get(4, 5).addPossibleValues(Arrays.asList(1, 2));
		subOutput.get(4, 6).addPossibleValues(Arrays.asList(2, 5));
		subOutput.get(4, 9).addPossibleValues(Arrays.asList(4));

		subOutput.get(5, 1).addPossibleValues(Arrays.asList());
		
	}

}
