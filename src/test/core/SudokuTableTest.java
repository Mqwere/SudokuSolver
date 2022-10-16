package test.core;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import main.core.SquareSudokuMatrix;
import main.core.SudokuTable;

public class SudokuTableTest
{
	private SquareSudokuMatrix getTestMatrix(int size, String values)
	{
		return new SquareSudokuMatrix(size, values);
	}
	
	@Test
	public void tableAnalysisReturnsValidOutputForNearCompleteSudoku()
	{
		int matrixSize = 9;
		String matrixValues = "534678912672195348198342567859761423426000791713924856961537284287419635345286179";
		
		SquareSudokuMatrix expectedMatrix = getTestMatrix(matrixSize, matrixValues);
		expectedMatrix.get(5, 4).addPossibleValue(8);
		expectedMatrix.get(5, 5).addPossibleValue(5);
		expectedMatrix.get(5, 6).addPossibleValue(3);
		
		SudokuTable expectedTable =  new SudokuTable(expectedMatrix);
		SudokuTable actualTable = new SudokuTable(getTestMatrix(matrixSize, matrixValues));
		actualTable.threadlessContainedAnalysis();
		
		for(int y = 1; y <= expectedTable.size; y++)
		{
			for(int x = 1; x <= expectedTable.size; x++)
			{
				ArrayList<Integer> expectedPossibleValues = expectedTable.get(y, x).getPossibleValues();
				ArrayList<Integer> actualPossibleValues	= actualTable	.get(y, x).getPossibleValues();
				Assertions.assertEquals(expectedPossibleValues.size(), actualPossibleValues.size(), String.format("[%d, %d]", y, x));
				for(int possibleValue : expectedPossibleValues)
				{
					if(actualPossibleValues.contains(possibleValue)) continue;
					Assertions.fail("There is at least one possible value not present in the actual values ("+possibleValue+").");
				}
				
			}
		}
		
	}
	
	/*
	 Matrix used below:
	 	0 4 6	3 0 0 	0 7 9
	 	7 0 3 	0 0 6 	4 0 0
	 	0 0 5 	0 0 0 	0 3 6
	 
	 	0 5 0  	0 0 1   6 0 3
	 	3 7 0  	5 6 4   9 0 0
	 	0 6 9  	8 2 3   0 0 7
	 
	 	0 0 0	0 6 3   7 0 0
	 	0 0 0 	0 0 7 	3 6 8
	 	6 3 7 	0 1 8 	0 9 0 
	 */
	
	@Test
	public void tableAnalysisReturnsValidOutputForSomewhatLessCompleteSudoku()
	{
		int matrixSize = 9;
		String matrixValues = "046300079703006400005000036050001603370564900069823007000063700000007368637018090";
		
		SquareSudokuMatrix expectedMatrix = getTestMatrix(matrixSize, matrixValues);
		expectedMatrix.get(1,1).addPossibleValues(Arrays.asList(1,2,8));
		expectedMatrix.get(1,5).addPossibleValues(Arrays.asList(5,8));
		expectedMatrix.get(1,6).addPossibleValues(Arrays.asList(2,5));
		expectedMatrix.get(1,7).addPossibleValues(Arrays.asList(1,2,5,8));
		
		expectedMatrix.get(2,2).addPossibleValues(Arrays.asList(1,2,8,9));
		expectedMatrix.get(2,4).addPossibleValues(Arrays.asList(1,2,9));
		expectedMatrix.get(2,5).addPossibleValues(Arrays.asList(5,8,9));
		expectedMatrix.get(2,8).addPossibleValues(Arrays.asList(1,2,5,8));
		expectedMatrix.get(2,9).addPossibleValues(Arrays.asList(1,2,5));
		
		expectedMatrix.get(3,1).addPossibleValues(Arrays.asList(1,2,8,9));
		expectedMatrix.get(3,2).addPossibleValues(Arrays.asList(1,2,8,9));
		expectedMatrix.get(3,4).addPossibleValues(Arrays.asList(1,2,4,7,9));
		expectedMatrix.get(3,5).addPossibleValues(Arrays.asList(4,7,8,9));
		expectedMatrix.get(3,6).addPossibleValues(Arrays.asList(2,9));
		expectedMatrix.get(3,7).addPossibleValues(Arrays.asList(1,2,8));

		expectedMatrix.get(4,1).addPossibleValues(Arrays.asList(2,4,8));
		expectedMatrix.get(4,3).addPossibleValues(Arrays.asList(2,4,8));
		expectedMatrix.get(4,4).addPossibleValues(Arrays.asList(7,9));
		expectedMatrix.get(4,5).addPossibleValues(Arrays.asList(7,9));
		expectedMatrix.get(4,8).addPossibleValues(Arrays.asList(2,4,8));
		
		expectedMatrix.get(5,3).addPossibleValues(Arrays.asList(1,2,8));
		expectedMatrix.get(5,8).addPossibleValues(Arrays.asList(1,2,8));
		expectedMatrix.get(5,9).addPossibleValues(Arrays.asList(1,2));

		expectedMatrix.get(6,1).addPossibleValues(Arrays.asList(1,4));
		expectedMatrix.get(6,7).addPossibleValues(Arrays.asList(1,5));
		expectedMatrix.get(6,8).addPossibleValues(Arrays.asList(1,4,5));

		expectedMatrix.get(7,1).addPossibleValues(Arrays.asList(1,2,4,5,8,9));
		expectedMatrix.get(7,2).addPossibleValues(Arrays.asList(1,2,8,9));
		expectedMatrix.get(7,3).addPossibleValues(Arrays.asList(1,2,4,8));
		expectedMatrix.get(7,4).addPossibleValues(Arrays.asList(2,4,9));
		expectedMatrix.get(7,8).addPossibleValues(Arrays.asList(1,2,4,5));
		expectedMatrix.get(7,9).addPossibleValues(Arrays.asList(1,2,4,5));
		
		expectedMatrix.get(8,1).addPossibleValues(Arrays.asList(1,2,4,5,9));
		expectedMatrix.get(8,2).addPossibleValues(Arrays.asList(1,2,9));
		expectedMatrix.get(8,3).addPossibleValues(Arrays.asList(1,2,4));
		expectedMatrix.get(8,4).addPossibleValues(Arrays.asList(2,4,9));
		expectedMatrix.get(8,5).addPossibleValues(Arrays.asList(4,5,9));

		expectedMatrix.get(9,4).addPossibleValues(Arrays.asList(2,4));
		expectedMatrix.get(9,7).addPossibleValues(Arrays.asList(2,5));
		expectedMatrix.get(9,9).addPossibleValues(Arrays.asList(2,4,5));
		
		SudokuTable expectedTable =  new SudokuTable(expectedMatrix);
		SudokuTable actualTable = new SudokuTable(getTestMatrix(matrixSize, matrixValues));
		actualTable.threadlessContainedAnalysis();
		
		for(int y = 1; y <= expectedTable.size; y++)
		{
			for(int x = 1; x <= expectedTable.size; x++)
			{
				ArrayList<Integer> expectedPossibleValues = expectedTable.get(y, x).getPossibleValues();
				ArrayList<Integer> actualPossibleValues	= actualTable	.get(y, x).getPossibleValues();
				if(expectedPossibleValues.size() != actualPossibleValues.size()) {
					String additionalOutput = "expected values: {";
					for(int pv: expectedPossibleValues) additionalOutput+= " "+pv;
					additionalOutput += " }\nactual values: {";
					for(int pv: actualPossibleValues) additionalOutput+= " "+pv;
					additionalOutput += " }";
					
					Assertions.fail( 
						String.format("[%d, %d] -> expected: <%d>, actual: <%d>\n%s", 
											y, x, 
											expectedPossibleValues.size(), 
											actualPossibleValues.size(), 
											additionalOutput
						) 
					);
				}
				for(int possibleValue : expectedPossibleValues)
				{
					if(actualPossibleValues.contains(possibleValue)) continue;
					Assertions.fail("There is at least one possible value not present in the actual values ("+possibleValue+").");
				}
				
			}
		}
		
	}

}
