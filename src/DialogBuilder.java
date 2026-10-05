import java.awt.*;
import javax.swing.*;

public interface DialogBuilder {
    DialogBuilder setWindowTitle(String windowTitle);
    DialogBuilder setTitle(String title);
    DialogBuilder setSubtitle(String subtitle);
    DialogBuilder addButton(String label, Color color, int value);
    DialogBuilder setButtonSize(int width, int height);
    DialogBuilder setButtonFont(int size);
    DialogBuilder setHorizontal(boolean horizontal);
    DialogBuilder setDefaultValue(int value);
    JDialog build();
    int showAndGetResult();
}