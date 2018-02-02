import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Joachim Pihlgren, joapih-6
 * A view for the quiz game
 */
@SuppressWarnings("ALL") public class QuizView extends JFrame implements Observer
{
	private JMenuBar menuBar;
	private JMenu file;
	private JMenuItem writeToFile;
	private JMenuItem readFromFile;
	private JMenuItem exit;
	private JMenu edit;
	private JMenuItem editQuestions;

	private JPanel mainPanel;

	private DefaultListModel<Player> playerListModel;

	private JButton addPlayer;
	private JButton removePlayer;
	private JList<Player> players;
	private JTextArea questionTextArea;
	private JTextField answerTextField;
	private JButton done;
	private JButton start;
	private JButton abort;

	public QuizView()
	{
		createMenuBar();
		createMainPanel();

		this.setTitle("Quiz master");
		this.setSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void createMenuBar()
	{
		writeToFile = new JMenuItem("Spara");
		readFromFile = new JMenuItem("Öppna");
		exit = new JMenuItem("Avsluta");

		file = new JMenu("Arkiv");
		file.add(writeToFile);
		file.add(readFromFile);
		file.add(exit);

		editQuestions = new JMenuItem("Frågor");
		edit = new JMenu("Ändra");
		edit.add(editQuestions);

		menuBar = new JMenuBar();
		menuBar.add(file);
		menuBar.add(edit);

		this.setJMenuBar(menuBar);
	}

	public void writeToFileAddActionLister(ActionListener actionListener)
	{
		writeToFile.addActionListener(actionListener);
	}

	public void readFromFileAddActionLister(ActionListener actionListener)
	{
		readFromFile.addActionListener(actionListener);
	}

	public void exitAddActionLister(ActionListener actionListener)
	{
		exit.addActionListener(actionListener);
	}

	public void editQuestionsAddActionListener(ActionListener actionListener)
	{
		editQuestions.addActionListener(actionListener);
	}

	public void setEditQuestionsEnabled(boolean bool)
	{
		editQuestions.setEnabled(bool);
	}

	private void createMainPanel()
	{

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		createPlayerPanel();
		createQuestionAnswerPanel();

		this.add(mainPanel);
	}

	private void createPlayerPanel()
	{
		playerListModel = new DefaultListModel<>();

		players = new JList<>(playerListModel);
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setLayoutOrientation(JList.VERTICAL_WRAP);
		players.setVisibleRowCount(JList.UNDEFINED_CONDITION);

		JScrollPane playerScroller = new JScrollPane();
		playerScroller.setBorder(BorderFactory.createTitledBorder("Spelare"));
		playerScroller.setViewportView(players);

		addPlayer = new JButton("Lägg till");
		removePlayer = new JButton("Ta bort");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(playerScroller);
		buttonPanel.add(addPlayer);
		buttonPanel.add(removePlayer);
		buttonPanel.add(Box.createRigidArea(new Dimension(100, 0)));
		mainPanel.add(buttonPanel, BorderLayout.WEST);
	}

	private void createQuestionAnswerPanel()
	{
		questionTextArea = new JTextArea("Test");
		questionTextArea.setBorder(BorderFactory.createTitledBorder("Fråga"));
		questionTextArea.setWrapStyleWord(true);
		questionTextArea.setLineWrap(true);

		answerTextField = new JTextField();
		answerTextField.setBorder(BorderFactory.createTitledBorder("Ditt svar"));

		done = new JButton("Klar");
		start = new JButton("Börja");
		abort = new JButton("Avbryt");

		JPanel answerPanel = new JPanel();
		answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.LINE_AXIS));
		answerPanel.add(answerTextField);
		answerPanel.add(done);

		JPanel startAbortPanel = new JPanel();
		startAbortPanel.setLayout(new BoxLayout(startAbortPanel, BoxLayout.LINE_AXIS));
		startAbortPanel.add(start);
		startAbortPanel.add(abort);

		JPanel questionPanel = new JPanel();
		questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.PAGE_AXIS));
		questionPanel.add(questionTextArea);
		questionPanel.add(answerPanel);
		questionPanel.add(startAbortPanel);

		mainPanel.add(questionPanel, BorderLayout.CENTER);
	}

	public void playersSetSelectionAllowed(boolean bool)
	{
		players.setEnabled(bool);
	}

	public void addPlayerAddActionListener(ActionListener actionListener)
	{
		addPlayer.addActionListener(actionListener);
	}

	public void addPlayerSetEnabled(boolean bool)
	{
		addPlayer.setEnabled(bool);
	}

	public void increaseSelectedPlayerScoreByOne()
	{
		players.getSelectedValue().increaseScoreByOne();
	}

	public List<Player> getPlayers()
	{
		List<Player> playerList = new ArrayList<>();
		for(int i = 0; i < playerListModel.getSize(); i++)
		{
			playerList.add(playerListModel.getElementAt(i));
		}
		return playerList;
	}

	public int getListModelSize()
	{
		return playerListModel.getSize();
	}

	public boolean setNextPlayerSelected()
	{
		if(players.getSelectedIndex() + 1 < playerListModel.getSize())
		{
			players.setSelectedIndex(players.getSelectedIndex() + 1);
			return true;
		}
		else
		{
			return false;

		}
	}

	public String getSelectedPlayerName()
	{
		return playerListModel.getElementAt(players.getSelectedIndex()).getName();
	}

	public void removePlayerAddActionListener(ActionListener actionListener)
	{
		removePlayer.addActionListener(actionListener);
	}

	public void removePlayerSetEnabled(boolean bool)
	{
		removePlayer.setEnabled(bool);
	}

	public void setQuestionTextAreaText(String text)
	{
		questionTextArea.setText(text);
	}

	public String getAnswerTextFieldText()
	{
		return answerTextField.getText();
	}

	public void clearAnswerTextField()
	{
		answerTextField.setText("");
	}

	public void doneAddActionListener(ActionListener actionListener)
	{
		done.addActionListener(actionListener);
	}

	public void doneSetEnabled(boolean bool)
	{
		done.setEnabled(bool);
	}

	public void startAddActionListener(ActionListener actionListener)
	{
		start.addActionListener(actionListener);
	}

	public void startSetEnabled(boolean bool)
	{
		start.setEnabled(bool);
	}

	public void abortAddActionListener(ActionListener actionListener)
	{
		abort.addActionListener(actionListener);
	}

	public void abortSetEnabled(boolean bool)
	{
		abort.setEnabled(bool);
	}

	public void addPlayer(Player player)
	{
		playerListModel.addElement(player);
	}

	public void removeSelectedPlayer()
	{
		if(!players.isSelectionEmpty())
		{
			playerListModel.remove(players.getSelectedIndex());
		}
	}

	public void removeAllPlayers()
	{
		playerListModel.removeAllElements();
	}

	public void selectPlayer(int index)
	{
		players.setSelectedIndex(index);
	}

	@Override public void update(Observable o, Object arg)
	{

	}
}
