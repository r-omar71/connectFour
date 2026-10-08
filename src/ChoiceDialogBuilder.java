import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ChoiceDialogBuilder implements DialogBuilder{
    //خصائص الزر
    private static class ButtonSpec{
        final String label;
        final Color color;
        final int value;    
        
        ButtonSpec(String label, Color color, int value){
            this.label= label;
            this.color= color;
            this.value= value;
        }
    }

    private final Window parent;
    private  String windowTitle= "";
    private String title= "";
    private String subtitle= null;
    private final List<ButtonSpec> buttons= new ArrayList<>();

    //خصائص الdialog الافتراضية
    private int buttonWidth= 260;
    private int buttonHeight= 50;
    private int fontSize= 15;
    private boolean horizontal= false;
    private int defaultValue= -1;


    public ChoiceDialogBuilder(Window parent){
        this.parent= parent;
    }

    //setters
    @Override
     public DialogBuilder setWindowTitle(String windowTitle){
        this.windowTitle= windowTitle;
        return  this;
    }

    @Override
     public DialogBuilder setTitle(String title){
        this.title= title;
        return this;
    }

    @Override
     public DialogBuilder setSubtitle(String subtitle){
        this.subtitle= subtitle;
        return this;
    } 

    @Override
     public DialogBuilder addButton(String label, Color color, int value){
        buttons.add(new ButtonSpec(label, color, value));
        return this;
    }

    @Override
     public DialogBuilder setButtonSize(int width, int height){
        this.buttonWidth= width;
        this.buttonHeight= height;
        return this;
    }
     
    @Override
     public DialogBuilder setButtonFont(int size){
        this.fontSize= size;
        return this;
    }

    @Override
     public DialogBuilder setHorizontal(boolean horizontal){
        this.horizontal= horizontal;
        return this;
    }

  @Override
    public DialogBuilder setDefaultValue(int value){
        this.defaultValue= value;
        return this;
    }
    
  @Override
    public ChoiceDialog build(){
        ChoiceDialog dialog= new ChoiceDialog(parent, windowTitle, defaultValue);
        
        //القالب الاساسي
        JPanel panel = new JPanel(new BorderLayout(0, horizontal ? 24 : 20));
        panel.setBackground(new Color(10, 10, 28));
        panel.setBorder(horizontal
                ? BorderFactory.createEmptyBorder(36, 48, 32, 48)
                : BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // النص الكبير
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        //قالب الازرار
        JPanel buttonsPanel = new JPanel(horizontal
                ? new GridLayout(1, buttons.size(), 16, 0)
                : new GridLayout(buttons.size(), 1, 0, 12));
        buttonsPanel.setOpaque(false);
        for (ButtonSpec spec : buttons) {
            buttonsPanel.add(createButton(spec, dialog));
        }

        // ترتيب العناصر
        if (horizontal) {
            panel.add(titleLabel, BorderLayout.CENTER);
            panel.add(buttonsPanel, BorderLayout.SOUTH);
        } else {
            panel.add(titleLabel, BorderLayout.NORTH);

            JPanel center = new JPanel(new BorderLayout(0, 14));
            center.setOpaque(false);
            if (subtitle != null) {
                JLabel subtitleLabel = new JLabel(subtitle, SwingConstants.CENTER);
                subtitleLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
                subtitleLabel.setForeground(new Color(160, 160, 200));
                center.add(subtitleLabel, BorderLayout.NORTH);
            }
            center.add(buttonsPanel, BorderLayout.CENTER);
            panel.add(center, BorderLayout.CENTER);
        }

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog; //the product
    }

  
    //creating a button - same logic as before but cleaner
    private JButton createButton(ButtonSpec spec, ChoiceDialog dialog){
        Color accent= spec.color;
        JButton btn= new JButton(spec.label){
            @Override 
            protected void paintComponent(Graphics g){
                Graphics2D g2= (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? accent : new Color(25, 25, 55));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12,12);
                g2.setColor(getModel().isRollover() ? Color.BLACK : Color.WHITE);
                g2.setFont(new Font("Georgia", Font.BOLD, fontSize));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> dialog.choose(spec.value));

        return btn;
    }

}
