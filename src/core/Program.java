package core;

import frontend.SudokuWindow;

//530070000600195000098000060800060003400803001700020006060000280000419005000080079
//120406089
public class Program {
	public static boolean 
		verbose = false,
		sleep = true,
		referenceWindow = true;
	
	public static SudokuWindow window;

	public static void main(String[] args) 
	{
		/**/
		String matrixString = 
				//"906001040701290060402806300000020980600000002094080000003708409040013706060900108"; // Easy https://www.sudokuoftheday.com/free/easy Puzzle 2 /// 0.107 s
				//"300007010040520000709400000102000000007906800000000307000009206000074030060200004"; // Medium https://www.sudokuoftheday.com/free/medium Puzzle 2 /// 2.296 s
				//"090040007020500090006907000800700000700080004000003001000809500060005030500010020"; // Tricky https://www.sudokuoftheday.com/free/tricky Puzzle 2 /// 1.109 s
				//"030002009509000000000008001304090085070803010890060407200300000000000104100400020"; // Fiendish https://www.sudokuoftheday.com/free/fiendish Puzzle 2 /// 0.343 s
				//"590004000016080000080100005300000140065402780048000009900003050000040230000800097"; // Diabolical https://www.sudokuoftheday.com/free/diabolical Puzzle 2 /// 0.226 s
				//"800000000003600000070090200050007000000045700000100030001000068008500010090000400"; // Alleged "Hardest Sudoku Ever" https://abcnews.go.com/blogs/headlines/2012/06/can-you-solve-the-hardest-ever-sudoku /// 2.406 s
		
				"056080000208030004900500000000000640090843020034000000000001007300060402000020910"; // tato-wyzwanie
		
		SudokuTable table = new SudokuTable(new SquareSudokuMatrix(9, matrixString));
		/**/
		verbose = false; sleep = false; referenceWindow = false; table.commenceAnalysis(); 
		//sleep = false; window = new SudokuWindow(table);
	}
		
	public static void print(Object input, Object... args) 
	{
		System.out.println(Converter.format(Converter.stringify(input), args));
	}
}
