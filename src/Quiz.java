import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

/**
 * @author Joachim Pihlgren, joapih-6
 * A quiz model, it holds questions and allows manipulation of questions and generates a random question from the list
 * of questions.
 */
@SuppressWarnings("ALL") public class Quiz extends Observable implements Serializable
{
	private List<Question> questions;
	private Random random;

	public Quiz()
	{
		questions = new ArrayList<>();
		random = new Random();
	}

	public void addQuestion(String question, String answer)
	{
		questions.add(new Question(question, answer));
		setChanged();
		notifyObservers();
	}

	public void removeQuestion(Question copyOfQuestion)
	{
		Question toRemove = null;
		for(Question question : questions)
		{
			if(question.getId() == copyOfQuestion.getId())
			{
				toRemove = question;
			}
		}
		questions.remove(toRemove);
		setChanged();
		notifyObservers();
	}

	public void changeQuestion(Question copyOfQuestion)
	{
		for(Question question : questions)
		{
			if(question.getId() == copyOfQuestion.getId())
			{
				question.setQuestion(copyOfQuestion.getQuestion());
				question.setAnswer(copyOfQuestion.getAnswer());
			}
		}
		setChanged();
		notifyObservers();
	}

	public List<Question> getQuestions()
	{
		List<Question> copyOfQuestions = new ArrayList<>();
		for(Question question : questions)
		{
			Question copy = new Question(question.getQuestion(), question.getAnswer());
			copy.setId(question.getId());
			copyOfQuestions.add(copy);
		}
		return copyOfQuestions;
	}

	public Question getRandomQuestion()
	{
		Question question = questions.get(random.nextInt(questions.size() - 1));
		return new Question(question.getQuestion(), question.getAnswer());
	}

	public void createNewRandom()
	{
		random = new Random();
	}
}
