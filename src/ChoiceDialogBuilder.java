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
    private int result;


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
    public JDialog build(){
        JDialog dialog= new JDialog(parent, windowTitle, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        return dialog; //the product
    }

  @Override
    public int showAndGetResult(){
        result= defaultValue;
        JDialog dialog= build();
        dialog.setVisible(true);
        return result;
    }

}
