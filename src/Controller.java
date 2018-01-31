import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author Joachim Pihlgren, joapih-6
 */
public class Controller
{

	private final String[][] DEFAULT_VALUES = new String[][] { { " Special Agent Cooper",
			"Twin Peaks - Vad hette FBI-agenten som kom till Twin Peaks för att lösa mordet på Laura Palmer och äta paj?" },
			{ "Danmark",
					"Vilket nordiskt land ställde in betalningarna, dvs gick i konkurs, 1813 som en följd av Napoleonkriget?" },
			{ "Jan Hugo Stenbeck",
					"Vilken svensk affärsman byggde upp bland annat Comviq, Metro, Strix Television, Tele 2, och TV-Shop?" },
			{ "1983", "Vilket år lanserades Coca Cola Light i Sverige?" },
			{ "Pablo Picasso", "Vem räknas, tillsammans med Georges Braque, som kubismens fader?" },
			{ "Tallinn", "Vilken stad är Estlands huvudstad?" },
			{ "Rom", " I vilken italiensk stad hör fotbollsklubben SS Lazio hemma?" },
			{ "1718", "Vilket år dog Karl XII?" },
			{ "En slända", "Vilket föremål stack sig Törnrosa på?", "En slända" },
			{ "Helsingborg", "Vilka stad har följande slogan: Här börjar Kontinenten?" } };

	private final JFileChooser FILE_CHOOSER = new JFileChooser();
	private final String CURRENT_DIRECTORY = "src" + File.separator + "files";
	private final String DEFAULT_FILE_EXTENSION = ".bin";
	private final String DEFAULT_FILE = "default";
	private final String ERROR_FILE = "error.txt";

	private Question question;

	private View view;
	private Quiz model;

	public Controller()
	{
		view = new View();
		model = new Quiz();
		model.addObserver(view);

		setUpFileChooser();
		if(!defaultFileLoaded())
		{
			loadDefaultValues();
		}
		addListeners();
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
		view.writeToFileAddActionLister(e -> save());
		view.readFromFileAddActionLister(e -> load());
		view.exitAddActionLister(e -> exit());
		view.addPlayerAddActionListener(e -> view.addPlayer(new Player(getUserInput("Ange ditt namn"))));
		view.removePlayerAddActionListener(e -> view.removeSelectedPlayer());
		view.doneAddActionListener(e -> validateQuestion());
		view.startAddActionListener(e -> startNewGame());
		view.abortAddActionListener(e -> abortGame());
	}

	private void validateQuestion()
	{
		if(question.getAnswer().toLowerCase().equals(view.getAnswerTextFieldText().toLowerCase()))
		{
			view.increaseSelectedPlayerScoreByOne();
		}
		else
		{
			if(!view.setNextPlayerSelected())
			{
				displayEndGameScore();
				resetView();
			}
			else
				showErrorMessage("Fel");
		}
	}

	private void displayEndGameScore()
	{
		List<Player> players = view.getPlayers();
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
		playerScores.append("Vinnaren var ");
		playerScores.append(winner.getScore());
		showSuccessMessage(playerScores.toString(), "Quizet slut!");
	}

	private void startNewGame()
	{
		view.playersSetSelectionAllowed(false);
		view.removePlayerSetEnabled(false);
		view.addPlayerSetEnabled(false);
		displayNextQuestion();

	}

	private void displayNextQuestion()
	{
		question = model.getRandomQuestion();
		view.setQuestionTextAreaText(question.getQuestion());
	}

	private void abortGame()
	{
		int choice = showConfirmMessage("Vill du verkligen avbrtya spelet?", new Object[] { "Ja", "nej" });
		if(choice == JOptionPane.YES_OPTION)
		{
			resetView();
		}
	}

	private void resetView()
	{
		view.setQuestionTextAreaText("");
		view.clearAnswerTextField();
		view.removeAllPlayers();
		view.playersSetSelectionAllowed(true);
		view.addPlayerSetEnabled(true);
		view.removePlayerSetEnabled(true);
	}

	private void exit()
	{
		if(showConfirmMessage("Vill du spara innan du avslutar?", new Object[] { "Ja", "Avbryt" })
				== JOptionPane.YES_OPTION)
		{
			save();
		}
		System.exit(0);
	}

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
			//todo update view
			return true;
		}
		catch(StreamCorruptedException sce)
		{
			showErrorMessage("Filen var korrupt och kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
			printToErrorFile(sce);
		}
		catch(IOException ioe)
		{
			showErrorMessage("Filen kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
			printToErrorFile(ioe);
		}
		catch(ClassNotFoundException cnfe)
		{
			showErrorMessage("Det inlästa objektet stämmer inte\nmed det förväntade objektet\n Se " + ERROR_FILE
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

		int userChoice = FILE_CHOOSER.showOpenDialog(view);
		if(userChoice == JFileChooser.APPROVE_OPTION)
		{
			try(FileInputStream fileInputStream = new FileInputStream(FILE_CHOOSER.getSelectedFile());
					ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream))
			{
				Question.setQuestionCount(objectInputStream.readInt());
				this.model = (Quiz) objectInputStream.readObject();
				//todo Update view
			}
			catch(StreamCorruptedException sce)
			{
				showErrorMessage("Filen var korrupt och kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
				printToErrorFile(sce);
			}
			catch(IOException ioe)
			{
				showErrorMessage("Filen kunde inte öppnas\n Se " + ERROR_FILE + " för mer information");
				printToErrorFile(ioe);
			}
			catch(ClassNotFoundException cnfe)
			{
				showErrorMessage("Det inlästa objektet stämmer inte\nmed det förväntade objektet\n Se " + ERROR_FILE
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

		int userChoice = FILE_CHOOSER.showSaveDialog(view);
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
					showErrorMessage("Filen kunde inte skrivas till\n Se " + ERROR_FILE + " för mer information");
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
			int userChoice = showConfirmMessage(
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
			showErrorMessage(stackTrace.toString());
		}
	}

	/**
	 * Show an error JOptionPane
	 *
	 * @param message the message to display as a String
	 */
	private void showErrorMessage(String message)
	{
		JOptionPane.showMessageDialog(view, message, "Fel", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Show JOptionPane for requesting confirmation of operation
	 *
	 * @param message the message to display as a String
	 * @return the confirmation or lack there of as an int
	 */
	private int showConfirmMessage(String message, Object[] options)
	{
		return JOptionPane.showOptionDialog(view, message, "Bekräfta", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
	}

	/**
	 * @param userMessage the message to print
	 * @return the user input
	 */
	private String getUserInput(String userMessage)
	{
		JTextField userInput = new JTextField();

		Object[] message = { userMessage, userInput };
		Object[] options = { "Ok", "Avbryt" };

		int choice = JOptionPane
				.showOptionDialog(view, message, "Ange", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
						options, options[1]);

		if(choice == JOptionPane.OK_OPTION)
		{
			return userInput.getText();
		}
		return "";
	}

	/**
	 * Show JOptionPane for a succesful operation
	 *
	 * @param message the message to display as a String
	 * @param title   the title of the JOptionPane
	 */
	private void showSuccessMessage(String message, String title)
	{
		JOptionPane.showMessageDialog(view, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
}
