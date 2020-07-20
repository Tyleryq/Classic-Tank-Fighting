

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JPasswordField;

public class Login extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField name;
	MysqlBean my;
	ResultSet rs=null;
	private JPasswordField pwd;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			Login dialog = new Login();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * Create the dialog.
	 */
	public Login() {
		setTitle("\u767B\u5F55");
		setAlwaysOnTop(true);
		my=new MysqlBean();
		
		try {
			rs=my.getRs("select * from infor");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		setBounds(300, 200, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel name_l = new JLabel("\u7528\u6237\u540D:");
		name_l.setBounds(55, 22, 48, 15);
		contentPanel.add(name_l);
		
		JLabel pwd__l = new JLabel("\u5BC6  \u7801:");
		pwd__l.setBounds(55, 60, 54, 15);
		contentPanel.add(pwd__l);
		
		name = new JTextField();
		name.setBounds(158, 19, 131, 21);
		contentPanel.add(name);
		name.setColumns(10);
		
		JLabel tip = new JLabel("  ");
		tip.setForeground(Color.RED);
		tip.setBounds(55, 116, 314, 31);
		contentPanel.add(tip);
		
		pwd = new JPasswordField();
		pwd.setBounds(158, 57, 131, 21);
		contentPanel.add(pwd);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String in_na=name.getText();
						String in_pa=pwd.getText();
						if(rs!=null)
							try {
								while(rs.next()) {
									try {
										if((rs.getString("name").equals(in_na))&&(rs.getString("password").equals(in_pa))) {
											System.out.println("s");
											setVisible(false);
											dispose();
										}else {
											tip.setText("用户名或密码错误!");
										}
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						
						
					}
				});
				buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("exit");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
