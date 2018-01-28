import java.io.Serializable;

/**
 * @author Joachim Pihlgren, joapih-6
 */
public class Question implements Serializable
{
	private static int questionCount = 0;
	private String question;
	private String answer;
	private int id;

	public Question(String question, String answer)
	{
		setQuestion(question);
		setAnswer(answer);
		setId(getQuestionCount());
		setQuestionCount(getQuestionCount() + 1);
	}

	public static int getQuestionCount()
	{
		return questionCount;
	}

	public static void setQuestionCount(int questionCount)
	{
		Question.questionCount = questionCount;
	}

	public String getQuestion()
	{
		return question;
	}

	public void setQuestion(String question)
	{
		this.question = question;
	}

	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

}
