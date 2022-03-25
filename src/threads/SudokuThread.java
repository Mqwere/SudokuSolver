package threads;

import java.util.ArrayList;

import core.Program;
import core.SudokuField;
import core.SudokuTable;

public class SudokuThread extends Thread{
	private Executable exec;
	public volatile boolean hasFinished;
	public boolean wasSuccesful = true;
		
	private SudokuThread (Executable exec, String name)
	{
		this.exec = exec;
        this.setName(name);
	}
	
	public static SudokuThread getSudokuFieldAnalyzer
	(
		SudokuTable table, 
		int row, 
		int col
	)
	{
		return new SudokuThread
		(
			() -> {
				SudokuField field = table.get(row, col);
				if(field.value!=0) return;
				for(int i=1; i<10; i++)
				{
					if(table.isValidMove(row, col, i)) field.addPossibleValue(i);
				}
			},
			String.format("SudokuFieldAnalyzer-R%dC%d", row, col)
		);
	}
	
	public static SudokuThread getTableAnalyzer(SudokuTable table)
	{
		return new SudokuThread
		(
			() -> {
				//Program.print(table.toTableString() + "\n\n");
				
				ArrayList<SudokuThread> threads = new ArrayList<>();
				SudokuThread thread;
				for(int y = 1; y <= table.size; y++)
				{
					for(int x = 1; x <= table.size; x++)
					{
						threads.add(thread = getSudokuFieldAnalyzer(table,y,x));
						thread.start();
					}
				}
				
				boolean canStop;
				
				do {
					canStop = true;
					for(SudokuThread t: threads)
					{
						if(!t.hasFinished) {
							canStop = false;
							break;
						}
					}
				} while(!canStop);
				//Program.print(table);
				try {
					if(Program.sleep) sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				table.notifyAboutAnalysisCompletion();
			},
			"SudokuTableAnalyzer"
		);
	}
	
    public void run()
    {
        try 
        {
        	hasFinished = false;
			exec.execute();
		}
        catch (Exception e) 
        {
			Program.print("%s has encountered an error:\n", this.getName());
            e.printStackTrace();
            wasSuccesful = false;
		}
        finally {
			hasFinished = true;
        }
    }
}
