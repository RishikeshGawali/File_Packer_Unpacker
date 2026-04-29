import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;

class FileUnpacker extends Template implements ActionListener
{
    JButton btnSelectZip, btnSelectOutput, btnUnpack;
    JLabel lblZipPath, lblOutputPath, lblTitle, lblStatus;
    JTextArea txtLog;
    JProgressBar progressBar;

    File selectedZip = null;
    File outputFolder = null;

    FileUnpacker()
    {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // --- Title in header ---
        lblTitle = new JLabel("File Unpacker - Extract ZIP File");
        lblTitle.setForeground(Color.white);
        lblTitle.setFont(new Font("Century", Font.BOLD, 15));
        Dimension ts = lblTitle.getPreferredSize();
        lblTitle.setBounds(20, 30, ts.width + 10, ts.height);
        _header.add(lblTitle);

        // --- Select ZIP file section ---
        JLabel lblStep1 = new JLabel("Step 1: Select ZIP file to unpack");
        lblStep1.setBounds(20, 15, 300, 20);
        lblStep1.setForeground(Color.CYAN);
        lblStep1.setFont(new Font("Century", Font.BOLD, 12));
        _content.add(lblStep1);

        lblZipPath = new JLabel("No ZIP file selected.");
        lblZipPath.setBounds(20, 40, 360, 20);
        lblZipPath.setForeground(Color.white);
        lblZipPath.setFont(new Font("Monospaced", Font.PLAIN, 11));
        _content.add(lblZipPath);

        btnSelectZip = new JButton("📂  Browse ZIP");
        btnSelectZip.setBounds(385, 37, 120, 25);
        btnSelectZip.setBackground(new Color(0, 100, 180));
        btnSelectZip.setForeground(Color.white);
        btnSelectZip.setFocusPainted(false);
        btnSelectZip.addActionListener(this);
        _content.add(btnSelectZip);

        // --- Select output folder section ---
        JLabel lblStep2 = new JLabel("Step 2: Select folder where files will be extracted");
        lblStep2.setBounds(20, 75, 400, 20);
        lblStep2.setForeground(Color.CYAN);
        lblStep2.setFont(new Font("Century", Font.BOLD, 12));
        _content.add(lblStep2);

        lblOutputPath = new JLabel("No output folder selected.");
        lblOutputPath.setBounds(20, 100, 360, 20);
        lblOutputPath.setForeground(Color.white);
        lblOutputPath.setFont(new Font("Monospaced", Font.PLAIN, 11));
        _content.add(lblOutputPath);

        btnSelectOutput = new JButton("📁  Browse Folder");
        btnSelectOutput.setBounds(385, 97, 120, 25);
        btnSelectOutput.setBackground(new Color(0, 100, 180));
        btnSelectOutput.setForeground(Color.white);
        btnSelectOutput.setFocusPainted(false);
        btnSelectOutput.addActionListener(this);
        _content.add(btnSelectOutput);

        // --- Unpack button ---
        btnUnpack = new JButton("📦  UNPACK ZIP");
        btnUnpack.setBounds(160, 135, 200, 35);
        btnUnpack.setBackground(new Color(0, 160, 0));
        btnUnpack.setForeground(Color.white);
        btnUnpack.setFont(new Font("Century", Font.BOLD, 14));
        btnUnpack.setFocusPainted(false);
        btnUnpack.addActionListener(this);
        _content.add(btnUnpack);

        // --- Progress bar ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(20, 185, 490, 20);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 200, 0));
        progressBar.setBackground(new Color(0, 30, 80));
        _content.add(progressBar);

        // --- Status label ---
        lblStatus = new JLabel("Select a ZIP file and output folder, then click UNPACK.");
        lblStatus.setBounds(20, 210, 490, 20);
        lblStatus.setForeground(Color.CYAN);
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        _content.add(lblStatus);

        // --- Log area showing extracted files ---
        JLabel lblLog = new JLabel("Extracted Files Log:");
        lblLog.setBounds(20, 240, 200, 20);
        lblLog.setForeground(Color.CYAN);
        lblLog.setFont(new Font("Century", Font.BOLD, 12));
        _content.add(lblLog);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(0, 20, 60));
        txtLog.setForeground(Color.white);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBounds(20, 265, 490, 160);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        _content.add(scrollPane);

        ClockHome();
        this.setSize(550, 590);
        this.setResizable(false);
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource() == exit)
        {
            this.dispose();
        }
        if(ae.getSource() == minimize)
        {
            this.setState(JFrame.ICONIFIED);
        }

        // --- Browse for ZIP file ---
        if(ae.getSource() == btnSelectZip)
        {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select ZIP File to Unpack");
            // Only show ZIP files
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("ZIP files (*.zip)", "zip"));
            int result = fc.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                selectedZip = fc.getSelectedFile();
                // Show shortened path if too long
                String path = selectedZip.getAbsolutePath();
                lblZipPath.setText(path.length() > 50 ? "..." + path.substring(path.length() - 47) : path);
                lblStatus.setText("ZIP selected. Now choose an output folder.");
            }
        }

        // --- Browse for output folder ---
        if(ae.getSource() == btnSelectOutput)
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setDialogTitle("Select Folder to Extract Into");
            int result = fc.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                outputFolder = fc.getSelectedFile();
                String path = outputFolder.getAbsolutePath();
                lblOutputPath.setText(path.length() > 50 ? "..." + path.substring(path.length() - 47) : path);
                lblStatus.setText("Output folder selected. Click UNPACK when ready.");
            }
        }

        // --- UNPACK ---
        if(ae.getSource() == btnUnpack)
        {
            if(selectedZip == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select a ZIP file first!",
                    "No ZIP Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if(outputFolder == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select an output folder first!",
                    "No Folder Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Run in background thread so UI stays responsive
            new Thread(() -> {
                unpackZip();
            }).start();
        }
    }

    private void unpackZip()
    {
        try
        {
            SwingUtilities.invokeLater(() -> {
                btnUnpack.setEnabled(false);
                txtLog.setText("");
                progressBar.setValue(0);
                lblStatus.setText("Unpacking... please wait.");
            });

            ZipInputStream zis = new ZipInputStream(new FileInputStream(selectedZip));

            // First pass: count total entries for progress bar
            ZipFile zf = new ZipFile(selectedZip);
            int totalEntries = zf.size();
            zf.close();

            ZipEntry entry;
            int count = 0;
            byte[] buffer = new byte[4096];

            while((entry = zis.getNextEntry()) != null)
            {
                String entryName = entry.getName();
                File outFile = new File(outputFolder, entryName);

                // Security check: prevent zip slip attack (malicious paths like ../../etc)
                if(!outFile.getCanonicalPath().startsWith(outputFolder.getCanonicalPath()))
                {
                    zis.closeEntry();
                    continue;
                }

                if(entry.isDirectory())
                {
                    outFile.mkdirs();  // create folder structure
                }
                else
                {
                    // Make sure parent folders exist
                    outFile.getParentFile().mkdirs();

                    FileOutputStream fos = new FileOutputStream(outFile);
                    int len;
                    while((len = zis.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }

                count++;
                final int progress = (int)((count / (double) totalEntries) * 100);
                final String name = entryName;

                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                    lblStatus.setText("Extracting: " + name);
                    txtLog.append("✅ " + name + "\n");
                    // Auto scroll log to bottom
                    txtLog.setCaretPosition(txtLog.getDocument().getLength());
                });

                zis.closeEntry();
            }

            zis.close();

            final int finalCount = count;  // lambda requires effectively final variable
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                lblStatus.setText("✅ Unpacking complete! " + finalCount + " file(s) extracted.");
                btnUnpack.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                    finalCount + " file(s) extracted successfully!\n\nSaved to:\n" + outputFolder.getAbsolutePath(),
                    "Unpack Complete", JOptionPane.INFORMATION_MESSAGE);
            });
        }
        catch(Exception ex)
        {
            SwingUtilities.invokeLater(() -> {
                lblStatus.setText("❌ Error: " + ex.getMessage());
                btnUnpack.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                    "Error while unpacking:\n" + ex.getMessage(),
                    "Unpack Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }
}
