import java.awt.*;
import javax.swing.*;

public class ChoiceDialog extends JDialog {

    private int result;

    ChoiceDialog(Window parent, String windowTitle, int defaultValue) {
        super(parent, windowTitle, ModalityType.APPLICATION_MODAL);
        this.result = defaultValue;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    void choose(int value) {
        result = value;
        dispose();
    }

    public int showAndGetResult() {
        setVisible(true);  
        return result;
    }
}