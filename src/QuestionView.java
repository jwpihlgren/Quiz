import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @AUTHOR Joachim Pihlgren, joapih-6
 */
public class QuestionView extends JFrame implements Observer
{

	private final Object[] COLUMN_NAMES = new Object[] { "Fråga", "Svar" };
	private JPanel mainPanel;
	private JTextField searchTextField;
	private JButton clearButton;
	private JPanel searchPanel;
	private TableRowSorter<DefaultTableModel> sorter;
	private DefaultTableModel tableModel;
	private JTable table;
	private JScrollPane jScrollPane;

	private JTextArea questionTextArea;
	private JTextField answerTextField;
	private JButton saveButton;
	private JPanel savePanel;

	private JButton newButton;
	private JButton changeButton;
	private JButton deleteButton;
	private JPanel buttonPanel;

	private JPanel detailsPanel;

	public QuestionView()
	{
		createSearchPanel();
		createTablePanel();
		createDetailsPanel();
		createMainPanel();
		this.setTitle("Frågehanterare");
		this.setSize(new Dimension(800, 600));
		this.setMinimumSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setVisible(false);
	}

	private void createMainPanel()
	{
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(searchPanel, BorderLayout.NORTH);
		mainPanel.add(jScrollPane, BorderLayout.WEST);
		mainPanel.add(detailsPanel, BorderLayout.CENTER);
		this.add(mainPanel);
	}

	private void createSearchPanel()
	{
		searchTextField = new JTextField();
		searchTextField.setBorder(BorderFactory.createTitledBorder("Sök"));
		clearButton = new JButton("Rensa");
		searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
		searchPanel.add(searchTextField);
		searchPanel.add(clearButton);
	}

	private void createTablePanel()
	{
		tableModel = new DefaultTableModel();
		tableModel.setColumnIdentifiers(COLUMN_NAMES);
		sorter = new TableRowSorter<>(tableModel);
		table = new JTable(tableModel);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(table);
	}

	public void newFilter()
	{
		RowFilter<TableModel, Object> rowFilter;
		try
		{
			rowFilter = RowFilter.regexFilter("(?i)" + searchTextField.getText(), 0, 1);
		}
		catch(java.util.regex.PatternSyntaxException e)
		{
			return;
		}
		sorter.setRowFilter(rowFilter);
	}

	private void createDetailsPanel()
	{
		questionTextArea = new JTextArea("");
		questionTextArea.setBorder(BorderFactory.createTitledBorder("Fråga"));
		questionTextArea.setWrapStyleWord(true);
		questionTextArea.setLineWrap(true);
		answerTextField = new JTextField("");
		answerTextField.setBorder(BorderFactory.createTitledBorder("Svar"));
		saveButton = new JButton("Spara");
		savePanel = new JPanel();
		savePanel.setLayout(new BoxLayout(savePanel, BoxLayout.LINE_AXIS));
		savePanel.add(saveButton);
		savePanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		newButton = new JButton("Ny");
		changeButton = new JButton("Ändra");
		deleteButton = new JButton("Delete");
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(newButton);
		buttonPanel.add(changeButton);
		buttonPanel.add(deleteButton);
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.PAGE_AXIS));
		detailsPanel.add(questionTextArea);
		detailsPanel.add(answerTextField);
		detailsPanel.add(savePanel);
		detailsPanel.add(Box.createRigidArea(new Dimension(0, 25)));
		detailsPanel.add(buttonPanel);
	}

	public String getSearchFieldText()
	{
		return searchTextField.getText();
	}

	public void setSearchTextFieldText(String text)
	{
		searchTextField.setText(text);
	}

	public void setSearchTextFieldEnabled(boolean bool)
	{
		searchTextField.setEnabled(bool);
	}

	public void searchAddFocuslistener(FocusListener focusListener)
	{
		searchTextField.addFocusListener(focusListener);
	}

	public void searchAddKeyListener(KeyListener keyListener)
	{
		searchTextField.addKeyListener(keyListener);
	}

	public void tableAddMouseListener(MouseListener mouseListener)
	{
		table.addMouseListener(mouseListener);
	}

	public String getQuestionFieldText()
	{
		return questionTextArea.getText();
	}

	public void setQuestionTextArea(String text)
	{
		questionTextArea.setText(text);
	}

	public void setQuestionTextFieldEditable(boolean bool)
	{
		questionTextArea.setEditable(bool);
	}

	public String getAswerFieldText()
	{
		return answerTextField.getText();
	}

	public void setAnswerTextFieldText(String text)
	{
		answerTextField.setText(text);
	}

	public void setAnswerTextFieldEditable(boolean bool)
	{
		answerTextField.setEditable(bool);
	}

	public void searchAddActionListener(ActionListener actionListener)
	{
		clearButton.addActionListener(actionListener);
	}

	public void searchButtonSetEnabled(boolean bool)
	{
		clearButton.setEnabled(bool);
	}

	public void saveButtonAddActionListener(ActionListener actionListener)
	{
		saveButton.addActionListener(actionListener);
	}

	public void saveButtonSetEnabled(boolean bool)
	{
		saveButton.setEnabled(bool);
	}

	public void newButtonAddActionListener(ActionListener actionListener)
	{
		newButton.addActionListener(actionListener);
	}

	public void newButtonSetEnabled(boolean bool)
	{
		newButton.setEnabled(bool);
	}

	public void changeButtonAddActionListener(ActionListener actionListener)
	{
		changeButton.addActionListener(actionListener);
	}

	public void changeButtonSetEnabled(boolean bool)
	{
		changeButton.setEnabled(bool);
	}

	public void deleteButtonAddActionListener(ActionListener actionListener)
	{
		deleteButton.addActionListener(actionListener);
	}

	//table.getSelectionModel().setSelectionInterval(table.getSelectedRow() - 1, table.getSelectedRow());

	public void deleteButtonSetEnabled(boolean bool)
	{
		deleteButton.setEnabled(bool);
	}

	@Override public void update(Observable o, Object arg)
	{
		tableModel.setRowCount(0);
		List<Question> questionList = ((Quiz) o).getQuestions();
		for(Question question : questionList)
		{
			tableModel.addRow(new Object[] { question.getQuestion(), question.getAnswer() });
		}
	}
}
