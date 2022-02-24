package frontend;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.Font;

import core.SudokuTable;

public class SudokuWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private int size;
	
	JButton solveButton = new JButton("Solve"),
			clearButton = new JButton("Clear");
	
	ArrayList<SudokuTextField> fields = new ArrayList<>();
	
	private SudokuTable table;
	
	public SudokuWindow(int size)
	{
		this(new SudokuTable(size));
	}
	
	public SudokuWindow(SudokuTable table)
	{
		this.size = table.size;
		this.table = table;
		
		JPanel panel = new JPanel();
		SudokuTextField field;
		
		Font font = new Font("SansSerif", Font.BOLD, 20);
		
		int 
			fieldX, 
			fieldY = 40,
			fieldW = 720/size,
			
			substep = (int)Math.sqrt((double)size);
		
		for(int y = 1; y <= size; y++)
		{
			fieldX = 40;
			for(int x = 1; x <= size; x++)
			{
				fields.add((field = new SudokuTextField(table.get(y, x).value)));
				panel.add(field);
				field.setBounds(fieldX, fieldY, fieldW, fieldW);
				field.setFont(font);
				field.setHorizontalAlignment(JTextField.CENTER);
				fieldX += fieldW;
				if(x%substep==0) fieldX += 5;
			}
			fieldY += fieldW;
			if(y%substep==0) fieldY += 5;
		}
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(840,980); //1440
		setTitle("Sudoku Solver");
		setResizable(false);
		setLocationRelativeTo(null);
		panel.setLayout(null);
		
		panel.add(solveButton);	solveButton.addActionListener((action) -> {solve();});
		panel.add(clearButton); clearButton.addActionListener((action) -> {clear();});
		
		solveButton.setBounds( 40, fieldY+40, 340, 80);
		clearButton.setBounds(420, fieldY+40, 340, 80);
		
		setContentPane(panel);
		setVisible(true);
	}
	
	public void clear()
	{
		for(SudokuTextField field: fields)
			field.setText("");
	}
	
	public void solve()
	{
		SudokuTextField field;
		for(int y = 1; y <= size; y++)
		{
			for(int x = 1; x <= size; x++)
			{
				table.get(y, x).value = (field = get(y, x)).get();
				field.setEditable(false);
			}
		}
		
		table.commenceAnalysis();
	}
	
	public void notifyAboutAnalysisCompletion()
	{
		for(int y = 1; y <= size; y++)
			for(int x = 1; x <= size; x++)
				get(y, x).set(table.get(y, x).value);
	}
	
	public void notifyAboutFinalization()
	{
		for(int y = 1; y <= size; y++)
			for(int x = 1; x <= size; x++)
				get(y, x).setEditable(true);
	}
	
	public SudokuTextField get(int row, int col)
	{
		return fields.get((row-1)*size + (col-1));
	}
}
