import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class NextPage extends Template implements ActionListener
{
    JLabel label;
    JButton btnPack, btnUnpack;

    NextPage(String value)
    {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        label = new JLabel("Welcome: " + value);
        Dimension size = label.getPreferredSize();
        label.setBounds(40, 30, size.width + 60, size.height);
        label.setFont(new Font("Century", Font.BOLD, 17));
        label.setForeground(Color.white);
        _header.add(label);

        btnPack = new JButton("Pack Files");
        btnPack.setBounds(80, 100, 150, 45);
        btnPack.setBackground(new Color(0, 120, 0));
        btnPack.setForeground(Color.white);
        btnPack.setFont(new Font("Century", Font.BOLD, 13));
        btnPack.setFocusPainted(false);
        btnPack.addActionListener(this);
        _content.add(btnPack);

        btnUnpack = new JButton("Unpack Files");
        btnUnpack.setBounds(260, 100, 150, 45);
        btnUnpack.setBackground(new Color(0, 80, 160));
        btnUnpack.setForeground(Color.white);
        btnUnpack.setFont(new Font("Century", Font.BOLD, 13));
        btnUnpack.setFocusPainted(false);
        btnUnpack.addActionListener(this);
        _content.add(btnUnpack);

        JLabel info = new JLabel("Select an option above to get started.");
        info.setBounds(80, 165, 350, 20);
        info.setForeground(Color.CYAN);
        info.setFont(new Font("Monospaced", Font.PLAIN, 12));
        _content.add(info);

        ClockHome();
        this.setSize(500, 500);
        this.setResizable(false);
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == exit)
        {
            this.setVisible(false);
            System.exit(0);
        }
        if(ae.getSource() == minimize)
        {
            this.setState(JFrame.ICONIFIED);
        }
        if(ae.getSource() == btnPack)
        {
            this.setVisible(false);
            new FilePacker();
        }
        if(ae.getSource() == btnUnpack)
        {
            this.setVisible(false);
            new FileUnpacker();
        }
    }
}
