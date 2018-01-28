import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * @author Joachim Pihlgren, joapih-6
 */
public class Quiz extends Observable implements Serializable
{
	private List<Question> questions;

	public Quiz()
	{
		questions = new ArrayList<>();
	}

	public void addQuestion(String question, String answer)
	{
		questions.add(new Question(question, answer));
	}

	public void removeQuestion(Question copyOfQuestion)
	{
		for(Question question : questions)
		{
			if(question.getId() == copyOfQuestion.getId())
			{
				questions.remove(question);
			}
		}
	}

	public void changeQuestion(Question copyOfQuestion)
	{
		for(Question question : questions)
		{
			if(question.getId() == copyOfQuestion.getId())
			{
				question.setAnswer(copyOfQuestion.getAnswer());
			}
		}
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
}
