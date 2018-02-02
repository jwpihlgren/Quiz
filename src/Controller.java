import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joachim Pihlgren, joapih-6
 * The controller for the quiz game view and question handler view
 */
public class Controller
{
	private final String[][] DEFAULT_VALUES = new String[][] { { "Special Agent Cooper",
			"Twin Peaks - Vad hette FBI-agenten som kom till Twin Peaks för att lösa mordet på Laura Palmer och äta paj?" },
			{ "Danmark",
					"Vilket nordiskt land ställde in betalningarna, dvs gick i konkurs, 1813 som en följd av Napoleonkriget?" },
			{ "Jan Hugo Stenbeck",
					"Vilken svensk affärsman byggde upp bland annat Comviq, Metro, Strix Television, Tele 2, och TV-Shop?" },
			{ "1983", "Vilket år lanserades Coca Cola Light i Sverige?" },
			{ "Pablo Picasso", "Vem räknas, tillsammans med Georges Braque, som kubismens fader?" },
			{ "Tallinn", "Vilken stad är Estlands huvudstad?" },
			{ "Rom", "I vilken italiensk stad hör fotbollsklubben SS Lazio hemma?" },
			{ "1718", "Vilket år dog Karl XII?" },
			{ "En slända", "Vilket föremål stack sig Törnrosa på?", "En slända" },
			{ "Helsingborg", "Vilka stad har följande slogan: Här börjar Kontinenten?" } };

	private final JFileChooser FILE_CHOOSER = new JFileChooser();
	private final String CURRENT_DIRECTORY = "src" + File.separator + "files";
	private final String DEFAULT_FILE_EXTENSION = ".bin";
	private final String DEFAULT_FILE = "default";
	private final String ERROR_FILE = "error.txt";

	private Question question;

	private QuizView quizView;
	private QuestionView questionView;
	private Quiz model;

	public Controller()
	{

		quizView = new QuizView();
		questionView = new QuestionView();
		model = new Quiz();
		setUpFileChooser();
		if(!defaultFileLoaded())
		{
			loadDefaultValues();
		}
		model.addObserver(quizView);
		model.addObserver(questionView);
		addListeners();
		resetQuizView();
	}

	/**
	 * Set up the filechooser with appropriate path
	 */
	private void setUpFileChooser()
	{
		FILE_CHOOSER.setCurrentDirectory(new File(Paths.get(CURRENT_DIRECTORY).toString()));
	}

	private void loadDefaultValues()
	{
		for(String[] question : DEFAULT_VALUES)
		{
			model.addQuestion(question[1], question[0]);
		}
	}

	private void addListeners()
	{
		quizView.writeToFileAddActionLister(e -> save());
		quizView.readFromFileAddActionLister(e -> load());
		quizView.exitAddActionLister(e -> exit());
		quizView.editQuestionsAddActionListener(e -> handleQuestions());
		quizView.addPlayerAddActionListener(e -> addPlayer());
		quizView.removePlayerAddActionListener(e -> removePlayer());
		quizView.doneAddActionListener(e -> validateGuess());
		quizView.startAddActionListener(e -> startNewGame());
		quizView.abortAddActionListener(e -> abortGame());

		questionView.searchAddActionListener(e -> clearSearch());
		questionView.searchAddFocusListener(new FocusListener()
		{
			@Override public void focusGained(FocusEvent e)
			{
				if(questionView.getSearchFieldText().isEmpty())
				{
					questionView.setSearchTextFieldText("");
				}
			}

			@Override public void focusLost(FocusEvent e)
			{
				if(questionView.getSearchFieldText().isEmpty())
				{
					questionView.setSearchTextFieldText("");
				}
			}
		});
		questionView.tableAddMouseListener(new MouseListener()
		{
			@Override public void mouseClicked(MouseEvent e)
			{

			}

			@Override public void mousePressed(MouseEvent e)
			{

			}

			@Override public void mouseReleased(MouseEvent e)
			{
				if(questionView.getTableEnabled())
				{
					if(questionView.getSelectedRow() != -1)
					{
						questionView.changeButtonSetEnabled(true);
						questionView.deleteButtonSetEnabled(true);
						questionView.setQuestionTextAreaText(questionView.getSelectedQuestion());
						questionView.setAnswerTextAreaText(questionView.getSelectedAnswer());
					}
				}
				else
				{
					showConfirmMessage(questionView,
							"Du kan inte välja en fråga när du\nlägger till eller ändrar en fråga. ",
							new Object[] { "Ok" });
				}
			}

			@Override public void mouseEntered(MouseEvent e)
			{

			}

			@Override public void mouseExited(MouseEvent e)
			{

			}
		});
		questionView.searchAddKeyListener(new KeyListener()
		{
			@Override public void keyTyped(KeyEvent e)
			{

			}

			@Override public void keyPressed(KeyEvent e)
			{

			}

			@Override public void keyReleased(KeyEvent e)
			{
				questionView.newFilter();
			}
		});
		questionView.newButtonAddActionListener(e -> addQuestion());
		questionView.changeButtonAddActionListener(e -> editQuestion());
		questionView.saveButtonAddActionListener(e -> saveQuestion());
		questionView.deleteButtonAddActionListener(e -> deleteQuestion());

	}

	//QuestionView Logic
	private void handleQuestions()
	{
		questionView.setVisible(true);
		questionView.update(model, null);
	}

	private void editQuestion()
	{
		questionView.setTableEnabled(false);
		questionView.setSearchTextFieldEnabled(false);
		questionView.saveButtonSetEnabled(false);
		questionView.newButtonSetEnabled(false);
		questionView.deleteButtonSetEnabled(false);
		questionView.saveButtonSetEnabled(true);
		questionView.setQuestionTextAreaEditable(true);
		questionView.setAnswerTextAreaEditable(true);
	}

	private void addQuestion()
	{
		questionView.setAnswerTextAreaText("");
		questionView.setQuestionTextAreaText("");
		questionView.setTableEnabled(false);
		questionView.setSearchTextFieldEnabled(false);
		questionView.changeButtonSetEnabled(false);
		questionView.deleteButtonSetEnabled(false);
		questionView.saveButtonSetEnabled(true);
		questionView.setQuestionTextAreaEditable(true);
		questionView.setAnswerTextAreaEditable(true);
	}

	private void saveQuestion()
	{

		String newQuestion = questionView.getQuestionAreaText();
		String newAnswer = questionView.getAnswerAreaText();

		if(questionView.getChangeButtonEnabled())
		{
			saveChangedQuestion(newQuestion, newAnswer);
		}
		else
		{
			saveNewQuestion(newQuestion, newAnswer);
		}

		resetQuestionView();
	}

	private void saveChangedQuestion(String newQuestion, String newAnswer)
	{
		List<Question> questions = model.getQuestions();
		for(Question question1 : questions)
		{
			if(questionView.getSelectedQuestion().toLowerCase().equals(question1.getQuestion().toLowerCase()))
			{
				if(newQuestionIsValid(newQuestion, newAnswer))
				{
					question1.setQuestion(newQuestion);
					question1.setAnswer(newAnswer);
					model.changeQuestion(question1);
				}
			}
		}
	}

	private void saveNewQuestion(String newQuestion, String newAnswer)
	{
		boolean unique = true;
		List<Question> questions = model.getQuestions();
		for(Question question1 : questions)
		{
			if(newQuestion.toLowerCase().equals(question1.getQuestion().toLowerCase()))
			{
				unique = false;
			}
		}
		if(unique)
		{
			if(newQuestionIsValid(newQuestion, newAnswer))
			{
				model.addQuestion(newQuestion, newAnswer);
			}
			else
			{
				showErrorMessage(questionView, "Fälten får inte vara tomma");
			}
		}
		else
		{
			showErrorMessage(questionView, "Frågan finns redan");
		}
	}

	private void deleteQuestion()
	{
		List<Question> questions = model.getQuestions();
		for(Question question1 : questions)
		{
			if(questionView.getSelectedQuestion().toLowerCase().equals(question1.getQuestion().toLowerCase()))
			{
				model.removeQuestion(question1);
			}
		}
		resetQuestionView();
	}

	private boolean newQuestionIsValid(String question, String answer)
	{
		return !question.isEmpty() && !answer.isEmpty();
	}

	private void resetQuestionView()
	{
		questionView.setAnswerTextAreaText("");
		questionView.setQuestionTextAreaText("");
		questionView.deslectRow();
		questionView.setQuestionTextAreaEditable(false);
		questionView.setAnswerTextAreaEditable(false);
		questionView.changeButtonSetEnabled(false);
		questionView.saveButtonSetEnabled(false);
		questionView.newButtonSetEnabled(true);
		questionView.setTableEnabled(true);
		questionView.setSearchTextFieldEnabled(true);
	}

	private void clearSearch()
	{
		questionView.setSearchTextFieldText("");
		questionView.newFilter();
	}

	//Game Logic
	private void addPlayer()
	{
		String playerName = getUserInput(quizView);
		if(playerName != null)
		{
			if(playerName.equals(""))
			{
				showErrorMessage(quizView, "Spelarnamn får inte vara tomt");
			}
			else
			{
				quizView.addPlayer(new Player(playerName));
				quizView.startSetEnabled(true);
				quizView.removePlayerSetEnabled(true);
				quizView.selectPlayer(0);
			}
		}
	}

	private void removePlayer()
	{
		quizView.removeSelectedPlayer();
		if(quizView.getListModelSize() < 1)
		{
			quizView.removePlayerSetEnabled(false);
		}
	}

	private void validateGuess()

	{
		if(question.getAnswer().toLowerCase().equals(quizView.getAnswerTextFieldText().toLowerCase()))
		{
			quizView.increaseSelectedPlayerScoreByOne();
			showSuccessMessage(quizView, "Rätt", "Rätt");
			quizView.clearAnswerTextField();
			displayNextQuestion();
		}
		else
		{
			showErrorMessage(quizView, "Fel");
			if(!quizView.setNextPlayerSelected())
			{
				displayEndGameScore();
				resetQuizView();
			}
			else
			{
				showConfirmMessage(quizView, "Det är spelare " + quizView.getSelectedPlayerName() + "s tur",
						new Object[] { "ok" });
				quizView.clearAnswerTextField();
				displayNextQuestion();
			}
		}
	}

	private void displayEndGameScore()
	{
		List<Player> players = quizView.getPlayers();
		StringBuilder playerScores = new StringBuilder();
		Player winner = players.get(0);
		for(Player player : players)
		{
			if(player.getScore() > winner.getScore())
			{
				winner = player;
			}
			playerScores.append(player.getName());
			playerScores.append(" fick ");
			playerScores.append(player.getScore());
			playerScores.append(" poäng\n");
		}
		playerScores.append(winner.getName());
		playerScores.append(" vann!");

		showSuccessMessage(quizView, playerScores.toString(), "Quizet slut!");
	}

	private void startNewGame()
	{
		model.createNewRandom();
		final boolean DEBUG_MODE = false; ///If true, allow user to view the question handler while playing.
		quizView.setEditQuestionsEnabled(DEBUG_MODE);
		quizView.playersSetSelectionAllowed(false);
		quizView.removePlayerSetEnabled(false);
		quizView.addPlayerSetEnabled(false);
		quizView.startSetEnabled(false);
		quizView.abortSetEnabled(true);
		quizView.doneSetEnabled(true);
		quizView.selectPlayer(0);
		showConfirmMessage(quizView, "Det är spelare " + quizView.getSelectedPlayerName() + "s tur",
				new Object[] { "ok" });
		displayNextQuestion();

	}

	private void displayNextQuestion()
	{
		question = model.getRandomQuestion();
		quizView.setQuestionTextAreaText(question.getQuestion());
	}

	private void abortGame()
	{
		int choice = showConfirmMessage(quizView, "Vill du verkligen avbrtya spelet?", new Object[] { "Ja", "nej" });
		if(choice == JOptionPane.YES_OPTION)
		{
			resetQuizView();
		}
	}

	private void resetQuizView()
	{
		quizView.setQuestionTextAreaText("");
		quizView.clearAnswerTextField();
		quizView.removeAllPlayers();
		quizView.playersSetSelectionAllowed(true);
		quizView.addPlayerSetEnabled(true);
		quizView.removePlayerSetEnabled(true);
		quizView.setEditQuestionsEnabled(true);
		quizView.startSetEnabled(false);
		quizView.abortSetEnabled(false);
		quizView.doneSetEnabled(false);
		quizView.removePlayerSetEnabled(false);
	}

	private void exit()
	{
		if(showConfirmMessage(quizView, "Vill du spara innan du avslutar?", new Object[] { "Ja", "Avbryt" })
				== JOptionPane.YES_OPTION)
		{
			save();
		}
		System.exit(0);
	}

	// IO operations

	/**
	 * Load the default file
	 *
	 * @return true if the file was loaded, else false
	 */
	private boolean defaultFileLoaded()
	{
		File defaultFile = new File(CURRENT_DIRECTORY + File.separator + DEFAULT_FILE + DEFAULT_FILE_EXTENSION);
		try(FileInputStream fileInputStream = new FileInputStream(defaultFile);
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream))
		{
			Question.setQuestionCount(objectInputStream.readInt());
			this.model = (Quiz) objectInputStream.readObject();
			quizView.update(model, null);
			return true;
		}
		catch(StreamCorruptedException sce)
		{
			showErrorMessage(quizView,
					"Filen var korrupt och kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
			printToErrorFile(sce);
		}
		catch(IOException ioe)
		{
			showErrorMessage(quizView, "Filen kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
			printToErrorFile(ioe);
		}
		catch(ClassNotFoundException cnfe)
		{
			showErrorMessage(quizView,
					"Det inlästa objektet stämmer inte\nmed det förväntade objektet\n Se " + ERROR_FILE
							+ " för mer information");
			printToErrorFile(cnfe);
		}
		return false;
	}

	/**
	 * Load a model object from file.
	 */
	private void load()
	{
		FILE_CHOOSER.setSelectedFile(new File(""));
		FILE_CHOOSER.setFileFilter(
				new FileNameExtensionFilter(DEFAULT_FILE_EXTENSION, DEFAULT_FILE_EXTENSION.replaceAll("\\.", "")));

		int userChoice = FILE_CHOOSER.showOpenDialog(quizView);
		if(userChoice == JFileChooser.APPROVE_OPTION)
		{
			try(FileInputStream fileInputStream = new FileInputStream(FILE_CHOOSER.getSelectedFile());
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream))
			{
				Question.setQuestionCount(objectInputStream.readInt());
				this.model = (Quiz) objectInputStream.readObject();
				model.addObserver(quizView);
				model.addObserver(questionView);
				quizView.update(model, null);
			}
			catch(StreamCorruptedException sce)
			{
				showErrorMessage(quizView,
						"Filen var korrupt och kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
				printToErrorFile(sce);
			}
			catch(IOException ioe)
			{
				showErrorMessage(quizView, "Filen kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
				printToErrorFile(ioe);
			}
			catch(ClassNotFoundException cnfe)
			{
				showErrorMessage(quizView,
						"Det inlästa objektet stämmer inte\nmed det förväntade objektet\n Se " + ERROR_FILE
								+ " för mer information");
				printToErrorFile(cnfe);
			}
		}
	}

	/**
	 * Save a current model object and its' state to a file.
	 */
	private void save()
	{
		FILE_CHOOSER.setSelectedFile(new File(DEFAULT_FILE + DEFAULT_FILE_EXTENSION));
		FILE_CHOOSER.setFileFilter(
				new FileNameExtensionFilter(DEFAULT_FILE_EXTENSION, DEFAULT_FILE_EXTENSION.replaceAll("\\.", "")));

		int userChoice = FILE_CHOOSER.showSaveDialog(quizView);
		if(userChoice == JFileChooser.APPROVE_OPTION)
		{
			if(overWriteExistingFile(FILE_CHOOSER.getSelectedFile()))
			{

				String fileToSave = checkFileExtension(FILE_CHOOSER.getSelectedFile().toString(),
						DEFAULT_FILE_EXTENSION);
				try
				{
					FileOutputStream fileOutputStream = new FileOutputStream(new File(fileToSave));
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
					objectOutputStream.writeInt(Question.getQuestionCount());
					objectOutputStream.writeObject(this.model);
				}
				catch(IOException ioe)
				{
					showErrorMessage(quizView,
							"Filen kunde inte skrivas till\n Se " + ERROR_FILE + " för mer information");
					printToErrorFile(ioe);
				}
			}
		}
	}

	/**
	 * See if file to write to already exists, if it does promppt user for action
	 *
	 * @return true if file does not exists or if user enters yes from prompt, else false
	 */
	private boolean overWriteExistingFile(File fileToOverWrite)
	{
		if(fileToOverWrite.exists())
		{
			int userChoice = showConfirmMessage(quizView,
					"Filen du vill skriva till finns redan.\n Vill du skriva " + "överbefintlig data?",
					new Object[] { "Ja", "Nej" });

			return userChoice == JOptionPane.YES_OPTION;
		}
		return true;
	}

	/**
	 * Take the file String representation and see if it contains the valid extension, if it does not then add it.
	 *
	 * @param fileToCheck       the user inputed file String representation
	 * @param acceptedExtension the correct extension
	 * @return a string representing a file with the correct file extension
	 */
	private String checkFileExtension(String fileToCheck, String acceptedExtension)
	{
		String checkedFile = fileToCheck;
		if(!fileToCheck.endsWith(acceptedExtension))
		{
			checkedFile += acceptedExtension;
		}
		return checkedFile;
	}

	/**
	 * Take an exception and try to write it to file. If not, display stacktrace to user.
	 *
	 * @param exception the exception to write to file.
	 */
	private void printToErrorFile(Exception exception)
	{
		try(PrintWriter printWriter = new PrintWriter(new File(CURRENT_DIRECTORY + File.separator + ERROR_FILE)))
		{
			exception.printStackTrace(printWriter);
		}

		//If we can't write the stacktrace to a file, as a last resort, display the stackTrace to the user.
		catch(IOException ioe)
		{
			StringBuilder stackTrace = new StringBuilder();
			stackTrace.append("Exception that couldn't be written to file");
			//Add the exception that couldn't be written to file
			for(StackTraceElement stackTraceElement : Arrays.asList(exception.getStackTrace()))
			{
				stackTrace.append(stackTraceElement.toString()).append(System.lineSeparator());
			}
			stackTrace.append("Exception that arose when trying to write the previous exception to file: ");
			//Add the exception that arose from trying to write the previous exception to file
			for(StackTraceElement stackTraceElement : Arrays.asList(ioe.getStackTrace()))
			{
				stackTrace.append(stackTraceElement.toString()).append(System.lineSeparator());
			}
			showErrorMessage(quizView, stackTrace.toString());
		}
	}

	/**
	 * Show an error JOptionPane
	 *
	 * @param message the message to display as a String
	 */
	private void showErrorMessage(JFrame view, String message)
	{
		JOptionPane.showMessageDialog(view, message, "Fel", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show JOptionPane for requesting confirmation of operation
	 *
	 * @param message the message to display as a String
	 * @return the confirmation or lack there of as an int
	 */
	private int showConfirmMessage(JFrame view, String message, Object[] options)
	{
		return JOptionPane.showOptionDialog(view, message, "Bekräfta", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
	}

	/**
	 * @return the user input
	 */
	private String getUserInput(JFrame view)
	{
		JTextField userInput = new JTextField();

		Object[] message = { "Ange ditt namn", userInput };
		Object[] options = { "Ok", "Avbryt" };

		int choice = JOptionPane
				.showOptionDialog(view, message, "Ange", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[1]);

		if(choice == JOptionPane.OK_OPTION)
		{
			return userInput.getText();
		}
		return null;
	}

	/**
	 * Show JOptionPane for a succesful operation
	 *
	 * @param message the message to display as a String
	 * @param title   the title of the JOptionPane
	 */
	private void showSuccessMessage(JFrame view, String message, String title)
	{
		JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
