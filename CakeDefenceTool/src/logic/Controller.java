/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import Frame.MainFrame;
import entity.main.GeneralSettings;
import entity.main.Routine;
import java.awt.Desktop;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Leon
 */
public class Controller {
   
    private static Controller instance;
    
    private MainFrame mainFrame;
    
    private Routine routine;
    private String loadedFilePath;
    
    public Controller(MainFrame mainFrame) {
        instance = this;
        this.mainFrame = mainFrame;
    }
    
    public Routine getRoutine() {
        return routine;
    }

    public MainFrame getFrame() {
        return mainFrame;
    }
    
    public Routine createNewRoutine() {
        routine = new Routine();
        loadedFilePath = null;
        return routine;
    }
    
    private JFileChooser createFileChooser(String title) {
        String filePath = System.getProperty("user.home") + File.separator + "StouxGames";
        File f = new File(filePath);
        if (!f.exists()) f.mkdirs();
        
        JFileChooser fileChooser = new JFileChooser(f);
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("StouxGames Cake Defence files (.stx)", "stx");
        fileChooser.setFileFilter(filter);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle(title);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        return fileChooser;
    }
    
    public boolean loadRoutine() {
        return loadRoutine(true);
    }
    
    
    public boolean loadRoutine(boolean checkReplacing) {
        JFileChooser fileChooser = createFileChooser("Load Routine");
        
        int result = fileChooser.showOpenDialog(mainFrame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File selectedFile = fileChooser.getSelectedFile();
        
        Routine foundRoutine;
        try {
            Object foundObject;
            try (FileInputStream fis = new FileInputStream(selectedFile); ObjectInputStream ois = new ObjectInputStream(fis)) {
                foundObject = ois.readObject();
            }
            if (!(foundObject instanceof Routine)) {
                MainFrame.throwError("The file you selected isn't a Cake Defence Routine!");
                return false;
            }
            
            foundRoutine = (Routine) foundObject;
        } catch (IOException | ClassNotFoundException e) {
            MainFrame.throwError("<html>Something went wrong with loading the file!<br>Thrown error: " + e.getMessage() + "</html>");
            return false;
        }
        
        if (checkReplacing) {
            if (routine != null) {
                if (routine.getGeneralSettings() != null) {
                    int overwrite = JOptionPane.showConfirmDialog(mainFrame, "All unsaved progress will be lost if you load this routine! Do you want to continue?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (overwrite != 0) {
                        return false;
                    }
                }
            }
        }
        
        routine = foundRoutine;
        loadedFilePath = selectedFile.getAbsolutePath();
        JOptionPane.showMessageDialog(mainFrame, 
                "The routine '" + routine.getGeneralSettings().getRoutinename() + "' by '" + routine.getGeneralSettings().getAuthor() + "' has been loaded!", 
                "Routine loaded!", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }
    
    public boolean saveRoutine() {
        GeneralSettings gs = routine.getGeneralSettings();
        if (gs == null) {
            MainFrame.throwError("Nothing to save!");
            return false;
        }
        
        String filePath = System.getProperty("user.home") + File.separator + "StouxGames";
        File f = new File(filePath);
        if (!f.exists()) f.mkdirs();
        
        JFileChooser fileChooser = createFileChooser("Save Routine");
        
        boolean setDefaultFile = true;
        if (loadedFilePath != null) {
            File oldFile = new File(loadedFilePath);
            if (oldFile.exists()) {
                fileChooser.setSelectedFile(oldFile);
                setDefaultFile = false;
            }
        }
        
        if (setDefaultFile) {
            String fileName = (gs.getAuthor() + "-" + gs.getRoutinename() + ".stx").replace(" ", "_");
            File newFile = new File(filePath + File.separator + fileName);
            fileChooser.setSelectedFile(newFile);
        }
        
        int result = fileChooser.showSaveDialog(mainFrame);
        if (result != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        
        File saveFile = fileChooser.getSelectedFile();
        System.out.println(saveFile.getAbsolutePath());
        if (saveFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to overwrite the file?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (overwrite != 0) {
                return false;
            }
        }
        
        String fileName = saveFile.getName();
        boolean append = true;
        if (fileName.toLowerCase().contains(".stx")) {
            String lastChars = fileName.substring(fileName.length() - 4).toLowerCase();
            if (lastChars.equals(".stx")) {
                append = false;
            }
        }
        
        if (append) {
            saveFile = new File(saveFile.getAbsolutePath() + ".stx");
        }
        
        try {
            try (FileOutputStream fos = new FileOutputStream(saveFile); 
                    ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(routine);
            }
            String[] options = new String[]{"Open Folder", "No"};
            int option = JOptionPane.showOptionDialog(mainFrame, 
                    "<html>The file has been saved!<br>Saved to location: " + saveFile.getAbsolutePath() + "<br>Do you want to open the folder?</html>", "Choose an option!", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (option == 0) {
                try {
                    Desktop.getDesktop().open(saveFile.getParentFile());
                } catch (Exception e) {
                    MainFrame.throwError("Couldn't open the folder. Sorry!");
                }
            }
            loadedFilePath = saveFile.getAbsolutePath();
            return true;
        } catch (IOException | HeadlessException e) {
            MainFrame.throwError("<html><center>Something went wrong! The following error has been thrown:<br>" + e.getMessage() + "</center></html>");
            return false;
        }
    }

    public static Controller getInstance() {
        return instance;
    }

    public String getLoadedFilePath() {
        return loadedFilePath;
    }

    public void setLoadedFilePath(String loadedFilePath) {
        this.loadedFilePath = loadedFilePath;
    }
    
}
