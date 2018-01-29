import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Joachim Pihlgren, joapih-6
 */
public class View extends JFrame implements Observer
{
	private JMenuBar menuBar;
	private JMenu file;
	private JMenuItem writeToFile;
	private JMenuItem readFromFile;
	private JMenuItem exit;

	private JPanel mainPanel;

	private DefaultListModel<String> playerListModel;
	private JList<String> players;

	private JButton addPlayer;
	private JButton removePlayer;

	public View()
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

		menuBar = new JMenuBar();
		menuBar.add(file);

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

	private void createMainPanel()
	{
		playerListModel = new DefaultListModel<>();

		players = new JList<>(playerListModel);
		players.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		players.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		players.setVisibleRowCount(JList.UNDEFINED_CONDITION);

		JScrollPane playerScroller = new JScrollPane();
		playerScroller.setViewportView(players);

		addPlayer = new JButton("Lägg till");
		removePlayer = new JButton("Ta bort");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
		buttonPanel.add(playerScroller);
		buttonPanel.add(addPlayer);
		buttonPanel.add(removePlayer);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.WEST);

		this.add(mainPanel);
	}

	public void addPlayer(String name)
	{
		playerListModel.addElement(name + " 0");
	}

	public void removePlayer()
	{
		playerListModel.remove(players.getSelectedIndex());
	}

	@Override public void update(Observable o, Object arg)
	{

	}
}
