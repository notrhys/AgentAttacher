package me.rhys.attacher.app.ui;

import com.sun.tools.attach.VirtualMachine;
import me.mat1337.easy.gui.EasyGui;
import me.mat1337.easy.gui.graphics.element.button.Button;
import me.mat1337.easy.gui.graphics.element.input.InputField;
import me.mat1337.easy.gui.graphics.element.label.Label;
import me.mat1337.easy.gui.graphics.theme.impl.DarkTheme;
import me.mat1337.easy.gui.util.Constraint;
import me.mat1337.easy.gui.util.action.ActionType;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;

public class AttachUI {

    private Label statusLabel;
    private File agentFile;

    public void showUI() {
        EasyGui easyGui = new EasyGui("Attacher", 300, 270, new DarkTheme());
        easyGui.setVisible(true);
        easyGui.setResizable(false);

        InputField pidField = easyGui.inputField(50, 40, new Constraint(Constraint.Type.TOP_CENTER));
        pidField.setPlaceHolder("PID");

        InputField agentLocationField = easyGui.inputField(195, 30,
                new Constraint(Constraint.Type.TOP_CENTER).offset(5, 50));
        agentLocationField.setPlaceHolder("Click to select agent location");

        InputField agentArguments = easyGui.inputField(195, 30,
                new Constraint(Constraint.Type.TOP_CENTER).offset(5, 90));
        agentArguments.setPlaceHolder("Agent Arguments");

        this.statusLabel = easyGui.label("Waiting...", new Constraint(Constraint.Type.BOTTOM_CENTER));

        Button attachButton = easyGui.button("Attach",60, 30,
                new Constraint(Constraint.Type.BOTTOM_CENTER)
                .offset(0, -27));

        easyGui.registerActionHandler((element, type) -> {
            if (type.equals(ActionType.LEFT_CLICKED)) {

                if (element == attachButton) {
                    if (this.agentFile != null) {
                        String ID = pidField.getText();

                        if (ID.length() > 1) {
                            this.statusLabel.setText("Attaching...");
                            this.statusLabel.setTextColor(Color.MAGENTA);

                            new Thread(() -> {
                                try {
                                    this.attachAgent(ID, agentArguments.getText());
                                    this.statusLabel.setText("Agent was attached!");
                                    this.statusLabel.setTextColor(Color.GREEN);
                                } catch (Exception e) {
                                    this.statusLabel.setText("Attach Error.");
                                    this.statusLabel.setTextColor(Color.YELLOW);
                                    e.printStackTrace();
                                }
                            }).start();
                        } else {
                            this.statusLabel.setText("Invalid PID entry!");
                            this.statusLabel.setTextColor(Color.YELLOW);
                        }
                    } else {
                        this.statusLabel.setText("No file selected!");
                        this.statusLabel.setTextColor(Color.YELLOW);
                    }
                }

                if (element == agentLocationField) {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.showOpenDialog(null);

                    jFileChooser.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            return f.getName().endsWith(".jar");
                        }

                        @Override
                        public String getDescription() {
                            return null;
                        }
                    });

                    File selectedFile = jFileChooser.getSelectedFile();

                    if (selectedFile != null) {
                        this.agentFile = selectedFile;

                        agentLocationField.setText(selectedFile.getAbsolutePath());
                        this.statusLabel.setText("Ready!");
                        this.statusLabel.setTextColor(Color.GREEN);
                    } else {
                        this.statusLabel.setText("Invalid file format.");
                        this.statusLabel.setTextColor(Color.YELLOW);
                    }
                }

                agentArguments.setSelected(agentArguments.isHovered);
                pidField.setSelected(pidField.isHovered);
            }
        });

        easyGui.getDisplay().repaint();
        easyGui.repaint();
    }

    void attachAgent(String pid, String args) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach(pid);
        virtualMachine.loadAgent(this.agentFile.getAbsolutePath(), args);
        virtualMachine.detach();
    }
}
