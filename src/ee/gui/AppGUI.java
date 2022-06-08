package ee.gui;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppGUI extends Container {
    private final int PANEL_WIDTH = 300;
    private final int PANEL_HEIGHT = 200;
    private final int NAME_MAXCHAR = 30;
    private final int AGE_DEFAULT = 1;
    private final int AGE_MIN = 1;
    private final int AGE_MAX = 120;
    private final int AGE_STEP = 1;
    private JPanel panelMain;
    private JButton openTableButton;
    private JButton enterButton;
    private JTextField nameText;

    private JLabel nameLabel;
    private JLabel ageLabel;
    private JSpinner ageInt;

    public String fileName = "data.txt";
    private List<InputData> inputData;

    public AppGUI() {
        panelMain.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        /* määrab vahemiku, mis vanust panna saab */
        ageInt.setModel(new SpinnerNumberModel(AGE_DEFAULT, AGE_MIN, AGE_MAX, AGE_STEP));
        ageInt.setToolTipText("Lubatud vanuse vahemik " + AGE_MIN + " kuni " + AGE_MAX);

        /* siin on tehtud mu oma klass eraldi, mis paneb textfieldile limiti */
        nameText.setDocument(new TextFieldLimit(NAME_MAXCHAR));
        nameText.setToolTipText("Nimi võib olla maksimaalselt " + NAME_MAXCHAR + " tähemärki!");

        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int age = (int) ageInt.getValue();
                String name = nameText.getText().trim();

                if (!name.isEmpty() && name.length() > 1) {
                    writeToFile(name, age);
                    nameText.setText("");
                    ageInt.setValue(AGE_DEFAULT);

                } else {
                    JOptionPane.showMessageDialog(null, "Nimi puudu või liiga lühike!");
                }

            }
        });
        openTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(null,"Siia tuleb tabel");
                try {
                    if (readFromFile()) {
                        JDialog dialog = new JDialog();
                        createInfoTable(dialog);
                        dialog.setModal(true);
                        dialog.pack();
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);

                    } else {
                        JOptionPane.showMessageDialog(null, "Pole midagi näidata, lisa enne andmed");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }

    public JPanel getPanelMain() {
        return panelMain;
    }

    private boolean readFromFile() throws IOException {
        inputData = new ArrayList<>();
        File f = new File(fileName);
        if (f.exists()) {
            if (f.length() > 0) {
                try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                    for (String line; (line = br.readLine()) != null; ) {
                        String name = line.split(";")[0];
                        int age = Integer.parseInt(line.split(";")[1]);
                        inputData.add(new InputData(name, age));
                    }
                    return true;
                }
            }
        }

        return false;
    }

    private void createInfoTable(JDialog dialog) {
        String[] columnNames = new String[]{"Name", "Age"};
        String[][] data = new String[inputData.size()][2];
        for (int x = 0; x < inputData.size(); x++) {
            data[x][0] = inputData.get(x).getName();
            data[x][1] = String.valueOf(inputData.get(x).getAge());
        }
        JTable table = new JTable(data, columnNames);
        dialog.add(new JScrollPane(table));
        dialog.setTitle("Data");
    }

    private void writeToFile(String name, int age) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            String line = name + ";" + age;
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            //throw new RuntimeException(e);
            JOptionPane.showMessageDialog(this, "Faili pole olemas");
        }
    }

}
