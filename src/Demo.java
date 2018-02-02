import javax.swing.*;

/**
 * @author Joachim Pihlgren, joapih-6
 * Main class, constructs new Controller on swing thread
 */
public class Demo
{
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(Controller::new);
	}
}
