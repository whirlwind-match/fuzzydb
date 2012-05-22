package whirlwind.config.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.model.attributes.OptionsSource;

public class ConfigEnumsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTextField nameTextField = null;

	private JLabel jLabel = null;

	private JTextArea valuesTextArea = null;

	private JLabel jLabel1 = null;

	private JButton okButton = null;

	private JButton cancelButton = null;

	private JScrollPane jScrollPane = null;

	private boolean isOk = false; 
	private final OptionsSource def;
	
	/**
	 * @param null
	 */
	public ConfigEnumsDialog(String name, EnumDefinition def) {
		super();
		this.def = def;
		initialize();
		getNameTextField().setText(name);
		for (String value: def.getValues()) {
			getValuesTextArea().append(value + "\n");
		}
	}

	private void onOk()
	{
		isOk = true;
		
		
		
		setVisible(false);
	}
	
	private void onCancel()
	{
		setVisible(false);
	}
	
	public OptionsSource getDef() {
		return def;
	}

	public boolean isOk() {
		return isOk;
	}

	@Override
	public String getName() {
		return getNameTextField().getText();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(269, 272);
		this.setTitle("Enum Configuration");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setPreferredSize(new Dimension(40, 18));
			jLabel1.setLocation(new Point(1, 21));
			jLabel1.setSize(new Dimension(40, 18));
			jLabel1.setText("Values");
			jLabel = new JLabel();
			jLabel.setText("Name");
			jLabel.setSize(new Dimension(60, 18));
			jLabel.setPreferredSize(new Dimension(60, 18));
			jLabel.setLocation(new Point(1, 1));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getNameTextField(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getOkButton(), null);
			jContentPane.add(getCancelButton(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setSize(new Dimension(200, 18));
			nameTextField.setPreferredSize(new Dimension(200, 18));
			nameTextField.setLocation(new Point(61, 1));
		}
		return nameTextField;
	}

	/**
	 * This method initializes valuesTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getValuesTextArea() {
		if (valuesTextArea == null) {
			valuesTextArea = new JTextArea();
			valuesTextArea.setPreferredSize(new Dimension(256, 180));
		}
		return valuesTextArea;
	}

	/**
	 * This method initializes okButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setBounds(new Rectangle(16, 221, 100, 18));
			okButton.setText("Ok");
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onOk();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setBounds(new Rectangle(148, 220, 100, 18));
			cancelButton.setText("Cancel");
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					onCancel();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setLocation(new Point(1, 40));
			jScrollPane.setPreferredSize(new Dimension(256, 180));
			jScrollPane.setViewportView(getValuesTextArea());
			jScrollPane.setSize(new Dimension(258, 180));
		}
		return jScrollPane;
	}

}  //  @jve:decl-index=0:visual-constraint="22,10"
