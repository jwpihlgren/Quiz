/**
 * @author Joachim Pihlgren, joapih-6
 * A player model for keeping track of names and scores.
 */
public class Player
{
	private String name;
	private int score;

	Player(String name)
	{
		this.name = name;
		score = 0;
	}

	@Override public String toString()
	{
		return name;
	}

	public void increaseScoreByOne()
	{
		score++;
	}

	public int getScore()
	{
		return score;
	}

	public String getName()
	{
		return name;
	}
}
