import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

class ClockLabel extends JLabel implements ActionListener
{
    String type;
    SimpleDateFormat sdf;

    public ClockLabel(String type)
    {
        this.type = type;
        setForeground(Color.green);

        switch(type)
        {
            case "date":
                sdf = new SimpleDateFormat("MMMM dd yyyy");
                setFont(new Font("sans-serif", Font.PLAIN, 12));   // BUG FIX: font name, style, size are 3 separate args, not one string
                setHorizontalAlignment(SwingConstants.LEFT);
                break;

            case "time":
                sdf = new SimpleDateFormat("hh:mm:ss a");
                setFont(new Font("sans-serif", Font.PLAIN, 40));   // BUG FIX: same font fix
                setHorizontalAlignment(SwingConstants.CENTER);
                break;

            case "day":
                sdf = new SimpleDateFormat("EEEE");
                setFont(new Font("sans-serif", Font.PLAIN, 16));   // BUG FIX: same font fix
                setHorizontalAlignment(SwingConstants.RIGHT);
                break;

            default:
                sdf = new SimpleDateFormat();
                break;
        }

        Timer t = new Timer(1000, this);
        t.start();
    }

    public void actionPerformed(ActionEvent ae)
    {
        Date d = new Date();              // BUG FIX: was "Data d" — wrong class name
        setText(sdf.format(d));           // BUG FIX: was "sdt.format" — wrong variable name
    }
}

class Template extends JFrame implements Serializable, ActionListener
{
    // BUG FIX: was "JPanel_header" etc. — underscore was glued to type name, making it an invalid identifier
    JPanel _header;
    JPanel _content;
    JPanel _top;

    ClockLabel dayLable;
    ClockLabel timeLable;
    ClockLabel dateLable;

    JButton minimize, exit;   // BUG FIX: was "minimum" in Template but "minimize" used everywhere else

    public Template()
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        GridBagLayout grid = new GridBagLayout();
        setLayout(grid);

        // --- TOP PANEL (for minimize/close buttons) ---
        _top = new JPanel();
        _top.setBackground(Color.LIGHT_GRAY);
        _top.setLayout(null);
        _top.setPreferredSize(new Dimension(500, 25));

        getContentPane().add(_top, new GridBagConstraints(
            0, 0, 1, 1, 1, 5,
            GridBagConstraints.BASELINE,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));   // BUG FIX: was "new Inserts" — no such class

        // --- HEADER PANEL (for clock/title) ---
        // BUG FIX: _header panel was never created — used everywhere but never initialized
        _header = new JPanel();
        _header.setLayout(null);
        _header.setBackground(new Color(0, 30, 80));
        _header.setPreferredSize(new Dimension(500, 80));

        getContentPane().add(_header, new GridBagConstraints(
            0, 1, 1, 1, 1, 20,
            GridBagConstraints.BASELINE,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        // --- CONTENT PANEL (main area) ---
        _content = new JPanel();
        _content.setLayout(null);
        _content.setBackground(new Color(0, 50, 120));
        _content.setPreferredSize(new Dimension(500, 400));

        JScrollPane jsp = new JScrollPane(_content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getContentPane().add(jsp, new GridBagConstraints(
            0, 2, 1, 1, 1, 75,
            GridBagConstraints.BASELINE,
            GridBagConstraints.BOTH,
            new Insets(0, 0, 0, 0), 0, 0));

        setTitle("File Packer-Unpacker");

        CloseAndMin();
    }

    void CloseAndMin()
    {
        minimize = new JButton("-");
        minimize.setBackground(Color.LIGHT_GRAY);
        // BUG FIX: MAXIMIZED_HORIZ is a constant (Integer.MAX_VALUE) — wrong to use as x position
        // Using a fixed position relative to window width instead
        minimize.setBounds(390, 0, 45, 20);

        exit = new JButton("X");
        exit.setHorizontalAlignment(SwingConstants.CENTER);
        exit.setBackground(Color.LIGHT_GRAY);
        exit.setBounds(435, 0, 45, 20);

        _top.add(minimize);
        _top.add(exit);

        exit.addActionListener(this);
        minimize.addActionListener(this);
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
            setState(JFrame.ICONIFIED);
        }
    }

    // Used by inner pages (NextPage etc.) — clock in header panel
    void Clock()
    {
        dateLable = new ClockLabel("date");
        timeLable = new ClockLabel("time");
        dayLable  = new ClockLabel("day");

        dateLable.setForeground(Color.blue);
        timeLable.setForeground(Color.blue);
        dayLable.setForeground(Color.blue);

        // BUG FIX: all three setBounds were applied to dayLable only (copy-paste error)
        // Each label now gets its own bounds
        dateLable.setFont(new Font("Century", Font.BOLD, 12));
        dateLable.setBounds(10, 10, 180, 60);

        timeLable.setFont(new Font("Century", Font.BOLD, 15));
        timeLable.setBounds(200, 10, 200, 60);

        dayLable.setFont(new Font("Century", Font.BOLD, 15));
        dayLable.setBounds(410, 10, 180, 60);

        _header.add(dateLable);
        _header.add(timeLable);
        _header.add(dayLable);
    }

    // Used by login page — slightly different layout
    void ClockHome()
    {
        dateLable = new ClockLabel("date");
        timeLable = new ClockLabel("time");
        dayLable  = new ClockLabel("day");

        dateLable.setForeground(Color.blue);
        timeLable.setForeground(Color.blue);
        dayLable.setForeground(Color.blue);

        // BUG FIX: same copy-paste bug — all setBounds were on dayLable
        dateLable.setFont(new Font("Century", Font.BOLD, 12));
        dateLable.setBounds(10, 10, 150, 60);

        timeLable.setFont(new Font("Century", Font.BOLD, 15));
        timeLable.setBounds(160, 10, 180, 60);

        dayLable.setFont(new Font("Century", Font.BOLD, 15));
        dayLable.setBounds(350, 10, 150, 60);

        _header.add(dateLable);
        _header.add(timeLable);
        _header.add(dayLable);
    }
}
