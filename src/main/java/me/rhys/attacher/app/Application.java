package me.rhys.attacher.app;

import lombok.Getter;
import me.rhys.attacher.app.ui.AttachUI;

import javax.swing.*;

@Getter
public class Application {
    @Getter private static Application instance;
    private final AttachUI attachUI = new AttachUI();

    public Application() {
        instance = this;
        this.setUI();
        this.attachUI.showUI();
    }

    void setUI() {
        //Sets systems default UI

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}
