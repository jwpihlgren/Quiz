/**
 * @author Joachim Pihlgren, joapih-6
 */
public class Controller
{

	private String[] DEFAULT_VALUES = new String[] { "Joachim 0" };
	private View view;
	private Quiz model;

	public Controller()
	{
		view = new View();
		model = new Quiz();
		model.addObserver(view);

		loadDefaultValues();
	}

	private void loadDefaultValues()
	{
		for(String default_value : DEFAULT_VALUES)
		{
			view.addPlayer(default_value);
		}
	}
}
