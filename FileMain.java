import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class FileLogin extends Template implements ActionListener, Runnable
{
    JButton SUBMIT;
    JLabel label1, label2, label3, TopLabel;
    // BUG FIX: text2 must be JPasswordField type (not just JTextField) to use getPassword()
    final JTextField text1;
    final JPasswordField text2;
    // BUG FIX: variable was named "attemp" (typo) but used as "attempt" in if-check — made consistent
    private static int attempt = 3;

    FileLogin()
    {
        TopLabel = new JLabel();
        TopLabel.setHorizontalAlignment(SwingConstants.CENTER);
        TopLabel.setText("File Packer Unpacker : Login");   // renamed from Marvellous
        TopLabel.setForeground(Color.BLUE);
        TopLabel.setFont(new Font("Century", Font.BOLD, 16));

        Dimension topsize = TopLabel.getPreferredSize();
        TopLabel.setBounds(50, 30, topsize.width + 20, topsize.height);
        _header.add(TopLabel);

        label1 = new JLabel();
        label1.setText("Username:");
        label1.setForeground(Color.white);

        Dimension size = label1.getPreferredSize();
        label1.setBounds(50, 80, size.width, size.height);
        label1.setHorizontalAlignment(SwingConstants.CENTER);

        text1 = new JTextField(15);
        Dimension tsize = text1.getPreferredSize();
        text1.setBounds(200, 80, tsize.width, tsize.height);
        text1.setToolTipText("ENTER USERNAME");

        label2 = new JLabel();
        label2.setText("Password:");
        label2.setBounds(50, 130, size.width, size.height);
        label2.setForeground(Color.white);
        label2.setHorizontalAlignment(SwingConstants.CENTER);

        text2 = new JPasswordField(15);
        text2.setBounds(200, 130, tsize.width, tsize.height);
        text2.setToolTipText("ENTER PASSWORD");

        // BUG FIX: was "test2" (typo) instead of "text2"
        // BUG FIX: FocusListener is an interface with TWO methods — must implement both focusGained AND focusLost
        text2.addFocusListener(new FocusAdapter()   // FocusAdapter lets us override only what we need
        {
            public void focusGained(FocusEvent e)
            {
                label3.setText(" ");
            }
        });

        label3 = new JLabel(" ");
        label3.setForeground(Color.RED);
        label3.setBounds(200, 165, 200, 20);

        SUBMIT = new JButton("SUBMIT");
        SUBMIT.setHorizontalAlignment(SwingConstants.CENTER);
        Dimension ssize = SUBMIT.getPreferredSize();
        SUBMIT.setBounds(180, 200, ssize.width, ssize.height);

        // BUG FIX: "Thraed t" — was a typo. Correct spelling is "Thread"
        Thread t = new Thread(this);
        t.setDaemon(true);   // daemon thread: auto-stops when app closes
        t.start();

        _content.add(label1);
        _content.add(text1);
        _content.add(label2);
        _content.add(text2);
        _content.add(label3);
        _content.add(SUBMIT);

        ClockHome();
        setVisible(true);
        this.setSize(500, 500);
        this.setResizable(false);
        setLocationRelativeTo(null);
        SUBMIT.addActionListener(this);
    }

    // Validates that both username and password are at least 8 characters
    public boolean Validate(String Username, String password)
    {
        return (Username.length() >= 8 && password.length() >= 8);
    }

    public void actionPerformed(ActionEvent ae)
    {
        String value1 = text1.getText();
        // BUG FIX: value2 was reading from text1 instead of text2 — both were getting same value
        String value2 = new String(text2.getPassword());   // correct way to get password field text

        if(ae.getSource() == exit)
        {
            this.setVisible(false);
            System.exit(0);
        }
        if(ae.getSource() == minimize)
        {
            this.setState(JFrame.ICONIFIED);
        }
        if(ae.getSource() == SUBMIT)
        {
            // BUG FIX: logic error — original code checked Validate() then STILL checked credentials
            // even when Validate() returned false. Added "return" to stop execution after showing error.
            if(!Validate(value1, value2))
            {
                text1.setText("");
                text2.setText("");
                JOptionPane.showMessageDialog(this,
                    "Username and Password must be at least 8 characters!",
                    "File Packer Unpacker",
                    JOptionPane.ERROR_MESSAGE);
                return;   // Stop here — don't continue to credential check
            }

            // BUG FIX: credentials renamed from "MarvelleousAdmin" to "FileAdmin" (no Marvellous name)
            if(value1.equals("FileAdmin") && value2.equals("FileAdmin123"))
            {
                NextPage page = new NextPage(value1);
                this.setVisible(false);
            }
            else
            {
                // BUG FIX: was "attemp--" (typo) — now consistent with variable name "attempt"
                attempt--;

                if(attempt == 0)
                {
                    JOptionPane.showMessageDialog(this,
                        "Number of attempts finished. Application will close.",
                        "File Packer Unpacker",
                        JOptionPane.ERROR_MESSAGE);
                    this.dispose();
                    System.exit(0);
                }
                JOptionPane.showMessageDialog(this,
                    "Incorrect Username or Password. Attempts remaining: " + attempt,
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // BUG FIX: run() was placed OUTSIDE the class braces — it is a method of FileLogin, must be inside
    public void run()
    {
        for(;;)   // infinite loop running on background thread
        {
            try
            {
                Thread.sleep(200);   // check every 200ms — prevents CPU hogging
            }
            catch(InterruptedException ie)
            {
                break;
            }

            if(text2.isFocusOwner())
            {
                // Check CAPS LOCK state
                if(Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK))
                {
                    text2.setToolTipText("Warning: CAPS LOCK is on");
                }
                else
                {
                    text2.setToolTipText("ENTER PASSWORD");
                }

                // Show password strength hint
                String pwd = new String(text2.getPassword());
                if(pwd.length() < 8)
                {
                    label3.setText("Weak Password");
                }
                else
                {
                    label3.setText("");
                }
            }
        }
    }
}

// BUG FIX: class renamed from "MarvelleousMain" to "FileMain" (no Marvellous name)
// This must also be the public class name matching the filename
public class FileMain
{
    public static void main(String arg[])
    {
        // Run GUI on the Event Dispatch Thread — best practice for Swing
        SwingUtilities.invokeLater(() ->
        {
            try
            {
                new FileLogin();
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
