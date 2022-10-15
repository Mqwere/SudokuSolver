package main.frontend;

import javax.swing.JTextField;

public class SudokuTextField extends JTextField {
	private static final long serialVersionUID = 1L;
	
	public SudokuTextField()
	{
		super();
	}
	
	public SudokuTextField(int value)
	{
		this();
		set(value);
	}
	
	public void set(int value)
	{
		if(value==0) 
			this.setText("");
		else
			this.setText(""+value);
	}
	
	public int get()
	{
		try {
			String text = this.getText();
			if(text.equals("") || text.length()>1) text = "0";
			return Integer.parseInt(text);
		}
		catch(Exception e)
		{
			return 0;
		}
	}
}
