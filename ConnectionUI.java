import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

/**
 * Connection UI class defines the dialog that appears when pressing "new connection"
 * Separate from the UI class as it has a different UI appearance, and has a significant amount of
 * unique functionality not shared with the UI.
 *
 * @author Maxence Weyrich
 *
 */
public class ConnectionUI extends JDialog {

	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	private JTextField IPAddress;
	private JTextField portNumber;
	private JTextField userNameField;
	private JPasswordField passwordField;

	private boolean hasCustomUserName, hasPort, hasIPAddress, hasPassword;
	private JButton connectButton;
	private JLabel connectionResults;


	private Client client;


	/**
	 * Create the dialog.
	 * @param client the Client object needed to perform non-UI tasks
	 */
	public ConnectionUI(Client client) {

		this.client = client;
		hasCustomUserName = hasPort = hasIPAddress = hasPassword = false;

		setType(Type.UTILITY);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);

		setBounds(100, 100, 640, 100);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			IPAddress = new JTextField();
			IPAddress.setCaretColor(Color.BLACK);
			IPAddress.setText("IP Address");
			IPAddress.setFont(new Font("Tahoma", Font.PLAIN, 14));
			IPAddress.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent arg0) {
					if(!hasIPAddress) {
						IPAddress.setText("");
						hasIPAddress = true;
					}
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if(IPAddress.getText().equals("")) {

						IPAddress.setText("IP Address");
						hasIPAddress = false;
					} else {
						hasIPAddress = true;
					}
				}
			});
			IPAddress.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleClick();
				}
			});
			IPAddress.setBounds(325, 15, 130, 20);
			contentPanel.add(IPAddress);
			IPAddress.setColumns(10);
		}
		{
			portNumber = new JTextField();
			portNumber.setText("58755");
			hasPort = true;
			portNumber.setFont(new Font("Tahoma", Font.PLAIN, 14));
			portNumber.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent arg0) {
					if(!hasPort) {
						portNumber.setText("");
						hasPort = true;
					}
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if(portNumber.getText().equals("")) {
						portNumber.setText("Port");
						hasPort = false;
					} else {
						hasPort = true;
					}
				}
			});
			portNumber.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleClick();
				}
			});
			portNumber.setBounds(466, 15, 50, 20);
			contentPanel.add(portNumber);
			portNumber.setColumns(10);
		}
		{
			userNameField = new JTextField();
			userNameField.setText("Username");
			userNameField.setFont(new Font("Tahoma", Font.PLAIN, 14));
			userNameField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent arg0) {
					if(!hasCustomUserName) {
						userNameField.setText("");
						hasCustomUserName = true;
					}
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if(userNameField.getText().equals("")) {
						userNameField.setText("Username");
						hasCustomUserName = false;
					} else {
						hasCustomUserName = true;
					}
				}
			});
			userNameField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleClick();
				}
			});
			userNameField.setBounds(10, 15, 150, 20);
			contentPanel.add(userNameField);
			userNameField.setColumns(10);
		}
		{
			passwordField = new JPasswordField();
			passwordField.setText("Password");
			passwordField.setEchoChar('\0');
			passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
			passwordField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent arg0) {
					if(!hasPassword) {
						passwordField.setText("");
						passwordField.setEchoChar((char)8226);
						hasPassword = true;
					}
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if(passwordField.getPassword().length == 0) {
						passwordField.setText("Password");
						passwordField.setEchoChar('\0');
						hasPassword = false;
					} else {
						hasPassword = true;
					}
				}
			});
			passwordField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleClick();
				}
			});
			passwordField.setBounds(165, 15, 150, 20);
			contentPanel.add(passwordField);
		}
		{
			connectButton = new JButton("Connect...");
			connectButton.setFocusable(false);
			connectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					handleClick();
				}
			});
			connectButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			connectButton.setBounds(526, 15, 100, 20);
			contentPanel.add(connectButton);
		}

		JLabel seperatorText = new JLabel(":");
		seperatorText.setFocusable(false);
		seperatorText.setFont(new Font("Tahoma", Font.BOLD, 14));
		seperatorText.setBounds(458, 14, 5, 20);
		contentPanel.add(seperatorText);

		connectionResults = new JLabel("Not connected");
		connectionResults.setFont(new Font("Tahoma", Font.PLAIN, 12));
		connectionResults.setBounds(10, 45, 620, 14);
		contentPanel.add(connectionResults);


		connectionResults.requestFocusInWindow();

	}

	/**
	 * Disables the input boxes when attempting to connect
	 * Serves to show user that program is currently working, and to prevent user from changing input once started
	 *
	 * @param enabled boolean: true will enable all buttons, false will disable everything
	 */
	private void changeEnabledStatus(boolean enabled) {
		IPAddress.setEnabled(enabled);
		portNumber.setEnabled(enabled);
		userNameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		connectButton.setEnabled(enabled);
	}

	/**
	 * Handles when the user has pressed the connect button.
	 * Will begin to process all input data, verify that data is correctly entered, and attempt to connect.
	 * On failure, it will also update the connection result text with a reason for the failure, and re-enable
	 * all fields for the user to update.
	 *
	 * Successfully connecting to the server will dismiss the connection dialog.
	 */
	public void handleClick() {
		changeEnabledStatus(false);
		InetAddress addr;
		int portNo;
		String userName;
		char[] password;

		userName = userNameField.getText().trim();
		if(userName.length() == 0 || !hasCustomUserName) {
			connectionResults.setText("Connection failed - Enter a valid username");
			changeEnabledStatus(true);
			userNameField.requestFocusInWindow();
			return;
		} else if(!userName.matches("\\w{1,10}")) {
			connectionResults.setText("Connection failed - Username must be alphanumeric of 1 - 10 characters");
			changeEnabledStatus(true);
			userNameField.requestFocusInWindow();
			return;
		}

		if(!hasPort) {
			connectionResults.setText("Connection failed - Enter a port number");
			changeEnabledStatus(true);
			portNumber.requestFocusInWindow();
			return;
		}

		if(!hasIPAddress) {
			connectionResults.setText("Connection failed - Enter an IP address");
			changeEnabledStatus(true);
			IPAddress.requestFocusInWindow();
			return;
		}

		try {
			portNo = Integer.parseInt(portNumber.getText());
			if(portNo < 0 || portNo > 65535) {
				connectionResults.setText("Connection failed - Port number must be between 0 - 65535");
				changeEnabledStatus(true);
				portNumber.requestFocusInWindow();
				return;
			}
		} catch(Exception e) {
			connectionResults.setText("Connection failed - Bad port number");
			changeEnabledStatus(true);
			portNumber.requestFocusInWindow();
			return;
		}

		try {
			if(!IPAddress.getText().matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
				throw new UnknownHostException();
			}
			addr = InetAddress.getByName(IPAddress.getText());
		} catch(Exception e) {
			connectionResults.setText("Connection failed - Bad IP address");
			changeEnabledStatus(true);
			IPAddress.requestFocusInWindow();
			return;
		}

		if(hasPassword) {
			password = passwordField.getPassword();

		} else {
			password = new char[]{}; //empty character array
		}

		// input has passed all checks. now attempt to connect.
		connectionResults.setText("Connecting...");

		Thread t = new Thread(new Runnable(){
			public void run() {
				try {
					client.connect(userName, password, addr, portNo);
				} catch(IOException e) {
					connectionResults.setText("Connection failed - " + e.getMessage());
					changeEnabledStatus(true);
				}
			}
		});

		t.start();

	}

	/**
	 * Sets the connection result to "Connection failed - REASON" where REASON is the
	 * string given as an argument
	 * @param message String the reason for which the connection failed
	 */
	public void setConnectionResult(String message) {
		connectionResults.setText("Connection failed - " + message.trim());
		changeEnabledStatus(true);
	}

	/**
	 * Re-enables the text fields, and resets the connection result text area to defaults.
	 * NOTE that it will keep previously entered data rather than clearing it.
	 */
	public void reset() {
		connectionResults.setText("Not connected.");
		changeEnabledStatus(true);
	}
}
