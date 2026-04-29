import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;
import java.util.ArrayList;

class FilePacker extends Template implements ActionListener
{
    JButton btnAddFiles, btnAddFolder, btnRemove, btnPack, btnClear;
    JList<String> fileList;
    DefaultListModel<String> listModel;
    JLabel lblStatus, lblTitle;
    JProgressBar progressBar;
    ArrayList<File> selectedFiles;

    FilePacker()
    {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        selectedFiles = new ArrayList<>();

        // --- Title in header ---
        lblTitle = new JLabel("File Packer - Select Files / Folder to ZIP");
        lblTitle.setForeground(Color.white);
        lblTitle.setFont(new Font("Century", Font.BOLD, 15));
        Dimension ts = lblTitle.getPreferredSize();
        lblTitle.setBounds(20, 30, ts.width + 10, ts.height);
        _header.add(lblTitle);

        // --- File list display ---
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setBackground(new Color(0, 30, 90));
        fileList.setForeground(Color.white);
        fileList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fileList.setSelectionBackground(new Color(0, 100, 200));

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setBounds(20, 20, 440, 220);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        _content.add(scrollPane);

        // --- Buttons ---
        btnAddFiles = new JButton("+ Add Files");
        btnAddFiles.setBounds(20, 255, 130, 30);
        btnAddFiles.setBackground(new Color(0, 100, 180));
        btnAddFiles.setForeground(Color.white);
        btnAddFiles.setFocusPainted(false);
        btnAddFiles.addActionListener(this);
        _content.add(btnAddFiles);

        btnAddFolder = new JButton("+ Add Folder");
        btnAddFolder.setBounds(160, 255, 130, 30);
        btnAddFolder.setBackground(new Color(0, 100, 180));
        btnAddFolder.setForeground(Color.white);
        btnAddFolder.setFocusPainted(false);
        btnAddFolder.addActionListener(this);
        _content.add(btnAddFolder);

        btnRemove = new JButton("- Remove Selected");
        btnRemove.setBounds(300, 255, 160, 30);
        btnRemove.setBackground(new Color(150, 0, 0));
        btnRemove.setForeground(Color.white);
        btnRemove.setFocusPainted(false);
        btnRemove.addActionListener(this);
        _content.add(btnRemove);

        btnClear = new JButton("Clear All");
        btnClear.setBounds(20, 300, 120, 30);
        btnClear.setBackground(new Color(80, 80, 80));
        btnClear.setForeground(Color.white);
        btnClear.setFocusPainted(false);
        btnClear.addActionListener(this);
        _content.add(btnClear);

        btnPack = new JButton("📦  PACK  →  ZIP");
        btnPack.setBounds(160, 300, 300, 35);
        btnPack.setBackground(new Color(0, 160, 0));
        btnPack.setForeground(Color.white);
        btnPack.setFont(new Font("Century", Font.BOLD, 14));
        btnPack.setFocusPainted(false);
        btnPack.addActionListener(this);
        _content.add(btnPack);

        // --- Progress bar ---
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(20, 350, 440, 20);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 200, 0));
        progressBar.setBackground(new Color(0, 30, 80));
        _content.add(progressBar);

        // --- Status label ---
        lblStatus = new JLabel("Add files or a folder, then click PACK.");
        lblStatus.setBounds(20, 378, 440, 20);
        lblStatus.setForeground(Color.CYAN);
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        _content.add(lblStatus);

        ClockHome();
        this.setSize(500, 560);
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

        // --- Add individual files ---
        if(ae.getSource() == btnAddFiles)
        {
            JFileChooser fc = new JFileChooser();
            fc.setMultiSelectionEnabled(true);   // allow selecting multiple files at once
            fc.setDialogTitle("Select Files to Pack");
            int result = fc.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File[] files = fc.getSelectedFiles();
                for(File f : files)
                {
                    if(!selectedFiles.contains(f))
                    {
                        selectedFiles.add(f);
                        listModel.addElement(f.getAbsolutePath());
                    }
                }
                lblStatus.setText(selectedFiles.size() + " file(s) in list.");
            }
        }

        // --- Add entire folder ---
        if(ae.getSource() == btnAddFolder)
        {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  // folders only
            fc.setDialogTitle("Select Folder to Pack");
            int result = fc.showOpenDialog(this);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File folder = fc.getSelectedFile();
                addFolderFiles(folder, folder);
                lblStatus.setText(selectedFiles.size() + " file(s) in list.");
            }
        }

        // --- Remove selected item from list ---
        if(ae.getSource() == btnRemove)
        {
            int index = fileList.getSelectedIndex();
            if(index != -1)
            {
                selectedFiles.remove(index);
                listModel.remove(index);
                lblStatus.setText(selectedFiles.size() + " file(s) in list.");
            }
        }

        // --- Clear all ---
        if(ae.getSource() == btnClear)
        {
            selectedFiles.clear();
            listModel.clear();
            progressBar.setValue(0);
            lblStatus.setText("List cleared.");
        }

        // --- PACK into ZIP ---
        if(ae.getSource() == btnPack)
        {
            if(selectedFiles.isEmpty())
            {
                JOptionPane.showMessageDialog(this,
                    "Please add at least one file or folder first!",
                    "No Files", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Ask user where to save the ZIP
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save ZIP File As...");
            fc.setSelectedFile(new File("packed_files.zip"));
            int result = fc.showSaveDialog(this);

            if(result == JFileChooser.APPROVE_OPTION)
            {
                File zipFile = fc.getSelectedFile();
                // Ensure .zip extension
                if(!zipFile.getName().endsWith(".zip"))
                {
                    zipFile = new File(zipFile.getAbsolutePath() + ".zip");
                }

                final File finalZipFile = zipFile;

                // Run packing in background thread so UI doesn't freeze
                new Thread(() ->
                {
                    packFiles(finalZipFile);
                }).start();
            }
        }
    }

    // Recursively adds all files inside a folder to the list
    private void addFolderFiles(File folder, File root)
    {
        File[] files = folder.listFiles();
        if(files == null) return;
        for(File f : files)
        {
            if(f.isDirectory())
            {
                addFolderFiles(f, root);   // recurse into subfolders
            }
            else
            {
                if(!selectedFiles.contains(f))
                {
                    selectedFiles.add(f);
                    listModel.addElement(f.getAbsolutePath());
                }
            }
        }
    }

    // Performs the actual zipping
    private void packFiles(File zipFile)
    {
        try
        {
            SwingUtilities.invokeLater(() -> {
                btnPack.setEnabled(false);
                lblStatus.setText("Packing... please wait.");
                progressBar.setValue(0);
            });

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            zos.setLevel(ZipOutputStream.STORED);   // max compatibility

            int total = selectedFiles.size();

            for(int i = 0; i < total; i++)
            {
                File file = selectedFiles.get(i);
                addToZip(file, file.getName(), zos);

                final int progress = (int)(((i + 1) / (double) total) * 100);
                final String name = file.getName();
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(progress);
                    lblStatus.setText("Packing: " + name);
                });
            }

            zos.close();
            fos.close();

            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
                lblStatus.setText("✅ Done! Saved to: " + zipFile.getName());
                btnPack.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                    "Files packed successfully!\n\nSaved to:\n" + zipFile.getAbsolutePath(),
                    "Pack Complete", JOptionPane.INFORMATION_MESSAGE);
            });
        }
        catch(Exception ex)
        {
            SwingUtilities.invokeLater(() -> {
                lblStatus.setText("❌ Error: " + ex.getMessage());
                btnPack.setEnabled(true);
                JOptionPane.showMessageDialog(this,
                    "Error while packing:\n" + ex.getMessage(),
                    "Pack Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    // Adds a single file into the ZipOutputStream
    private void addToZip(File file, String entryName, ZipOutputStream zos) throws IOException
    {
        if(file.isDirectory()) return;

        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];
        int len;
        while((len = fis.read(buffer)) > 0)
        {
            zos.write(buffer, 0, len);
        }
        fis.close();
        zos.closeEntry();
    }
}
