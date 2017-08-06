import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JButton;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The UI class is intended to generate and control the main UI for the application.
 * A note that the majority of the actual code controlling connections and communication
 * with the server is actually controlled and executed by the Client object.
 *
 * @author Maxence Weyrich
 *
 */
public class UI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private static final String INTRO_MESSAGE = "Welcome to JavaChat... to begin connect to a server, or host your own!\n\n";
	private static final int MAX_CHARS = 250000;

	private Client client;
	private ConnectionUI connectionDialog;


	private JPanel contentPane;
	private JTextField messageBox;
	private JTextPane messageArchive;
	private JButton sendButton;
	private StyledDocument archiveDoc;
	private JMenuBar menuBar;
	private JButton newConnection;
	private Component horizontalGlue;
	private Component horizontalGlue_1;
	private JButton disconnect;
	private Component horizontalStrut;
	private JScrollPane messageArchivePanel;

	/**
	 * Create the main UI frame.
	 * @param client a Client object.
	 */
	public UI(Client client) {
		this.client = client;
		this.connectionDialog = new ConnectionUI(client);



		contentPane = new JPanel();
		contentPane.setFont(new Font("Tahoma", Font.PLAIN, 14));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);

		horizontalGlue = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue);

		newConnection = new JButton("New Connection...");
		newConnection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				connectionDialog.setVisible(true);
				connectionDialog.requestFocus();
			}
		});
		newConnection.setMargin(new Insets(3, 40, 3, 40));
		menuBar.add(newConnection);
		newConnection.setFont(new Font("Tahoma", Font.PLAIN, 14));

		horizontalStrut = Box.createHorizontalStrut(20);
		menuBar.add(horizontalStrut);

		disconnect = new JButton("Disconnect");
		disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.close();
			}
		});
		disconnect.setEnabled(false);
		disconnect.setMargin(new Insets(3, 40, 3, 40));
		disconnect.setFont(new Font("Tahoma", Font.PLAIN, 14));
		menuBar.add(disconnect);

		horizontalGlue_1 = Box.createHorizontalGlue();
		menuBar.add(horizontalGlue_1);

		JPanel subPanel = new JPanel();
		contentPane.add(subPanel, BorderLayout.SOUTH);
		subPanel.setLayout(new BorderLayout(0, 0));

		messageBox = new JTextField();
		messageBox.setEnabled(false);
		messageBox.addActionListener(this);
		subPanel.add(messageBox, BorderLayout.CENTER);
		messageBox.setMargin(new Insets(10, 10, 10, 10));
		messageBox.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		messageBox.setColumns(8);

		sendButton = new JButton("Send");
		sendButton.setEnabled(false);
		sendButton.addActionListener(this);
		sendButton.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		sendButton.setPreferredSize(new Dimension(80, 23));
		sendButton.setActionCommand("send");
		subPanel.add(sendButton, BorderLayout.EAST);


		messageArchivePanel = new JScrollPane();
		messageArchivePanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(messageArchivePanel, BorderLayout.CENTER);

		messageArchive = new JTextPane();
		messageArchive.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		messageArchive.setDragEnabled(true);
		messageArchive.setEditable(false);
		((DefaultCaret)messageArchive.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		// Define the document styles that are available to choose from
		archiveDoc = messageArchive.getStyledDocument();
		Style s = archiveDoc.addStyle("bold", null);
		StyleConstants.setBold(s, true);
		s = archiveDoc.addStyle("italic", null);
		StyleConstants.setItalic(s, true);
		s = archiveDoc.addStyle("command", null);
		StyleConstants.setFontFamily(s, "Monospaced");
		s = archiveDoc.addStyle("error", null);
		StyleConstants.setFontFamily(s, "Monospaced");
		StyleConstants.setForeground(s, Color.RED);


		messageArchivePanel.setViewportView(messageArchive);

		try {
			archiveDoc.insertString(0, INTRO_MESSAGE, null);
		} catch(Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				client.close();
			}
		});

        this.setVisible(true);

        messageBox.requestFocusInWindow();
	}

	/**
	 * Adds the specified text into the document with the given style.
	 * @param text String of the text to add
	 * @param style String of the style of the text. Ensure that the style has been defined ahead of time in the constructor.
	 */
	private void addText(String text, String style) {

		Style textStyle;

		if(style == null) {
			textStyle = null;
		} else {
			textStyle = archiveDoc.getStyle(style);
		}

		try {
			archiveDoc.insertString(archiveDoc.getLength(), text, textStyle);

			if(archiveDoc.getLength() + text.length() > MAX_CHARS) {
				archiveDoc.remove(0, Math.min(archiveDoc.getLength(), archiveDoc.getLength() + messageBox.getText().length() - MAX_CHARS));
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		messageArchive.setCaretPosition(archiveDoc.getLength()); //set position to end
	}


	/**
	 * Handle the Send button press or the press of the return key while still in the message box
	 * Will process the message, update UI, and send it to the server.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(sendButton) || e.getSource().equals(messageBox)) {
			if(messageBox.getText().startsWith("//")) { //if a server command (starts with "//")
				client.sendCommand(messageBox.getText().substring(2));

				addText(messageBox.getText() + '\n', "command");

				messageBox.setText("");
			} else if(messageBox.getText().length() > 0) { //if normal text; then it's a normal message
				client.sendMessage(messageBox.getText());

				addText("Me: ", "bold");
				addText(messageBox.getText() + '\n', null);

				messageBox.setText("");
			}
			messageBox.requestFocusInWindow();
		} else {
			System.out.println(e.toString());
		}
	}

	/**
	 * Adds the specified message to the UI with the user as the sender of the message
	 * @param newMessage String message to add to the screen
	 * @param user	 String user that send the new message.
	 */
	public void addMessage(String newMessage, String user) {

		addText(user + ": ", "bold");
		addText(newMessage.trim() + '\n', "normal");

	}

	/**
	 * Adds the specified text to the UI with the "error" style
	 * @param notif String error message to add to the screen
	 */
	public void addError(String notif) {
		addText("Error - " + notif + '\n', "error");
	}

	/**
	 * Adds the specified text to the UI with the formatting for a server command response (to differentiate from normal messages)
	 * @param notif String text to add to the UI
	 */
	public void addCommandResponse(String notif) {
		addText(notif + '\n', "command");
	}

	/**
	 * Adds a "notification" to the UI, it has a different formatting to indicate that is it not a normal message.
	 * This is used whenever the server sends a message or notification to a user to differentiate it form a normal message.
	 * @param notif  String message to send the user.
	 */
	public void addNotification(String notif) {

		addText(notif.trim() + '\n', "italic");

	}

	/**
	 * Sets the result text for the connection dialog
	 * @param result String result message
	 */
	public void showConnectionResult(String result) {
		connectionDialog.setConnectionResult(result);
	}

	/**
	 * Updates the UI based on the value of connected.
	 * E.g. will enable buttons that would normally be disabled when the connection turns on
	 * @param connected
	 */
	public void setConnected(boolean connected) {
		messageBox.setEnabled(connected);
		sendButton.setEnabled(connected);
		newConnection.setEnabled(!connected);
		disconnect.setEnabled(connected);
		if(connected) {
			connectionDialog.setVisible(false);
			connectionDialog.reset();
		}
	}
}
