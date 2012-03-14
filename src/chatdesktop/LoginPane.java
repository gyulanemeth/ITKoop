package chatdesktop;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Chuckie
 */
public class LoginPane extends BorderPane{
    private TextField accountField=new TextField();
    private PasswordField pwField=new PasswordField();
    private Button submit=new Button("Submit");
    /**
     * Segédfüggvény
     * @return névmező szövege
     */
    String getName(){
        return accountField.getText();
    }
    /**
     * Segédfüggvény
     * @return pwmezőre mutató pointer
     */
    PasswordField getPwField(){
        return pwField;
    }
    /**
     * Segédfüggvény
     * @return submit gombra mutató pointer
     */
    Button getSubmit(){
        return submit;
    }
    /**
     * Segédfüggvény
     * @return igaz, ha a szövegmezők ki vannak töltve, egyébként hamis
     */
    boolean isFilled(){
        return accountField.getText().equals("") && pwField.getText().equals("") ? false:true;
    }
    /**
     * szövegmezők szövegeit törli
     */
    void clear(){
        accountField.setText("");
        pwField.setText("");
    }
    public LoginPane() {
        super();
        //TOP*******************************************************************
        final Text title=new Text("Login");   
        title.setFont(Font.font("Engravers MT", 18));
        title.setEffect(new Reflection());
        title.setFill(new LinearGradient(0, 0, 1, 2, true, CycleMethod.REPEAT, new
         Stop[]{new Stop(0, Color.BLACK), new Stop(0.7f, Color.DARKGRAY)}));
        StackPane spane=new StackPane();
        spane.getChildren().add(title);
        spane.setPadding(new Insets(20, 0, 20, 0));
        //Center****************************************************************
        GridPane gp=new GridPane();
        gp.setHgap(8);
        gp.setVgap(8);
        gp.setPadding(new Insets(40, 40, 40, 40));
        Label header=new Label("Sign in:");
        header.setFont(Font.font("Tahoma", 14));
        accountField.setPromptText("account");
        pwField.setPromptText("password");
        gp.add(header, 0, 0);
        gp.add(new Label("Username:"), 0, 1);
        gp.add(accountField, 1, 1);
        gp.add(new Label("Password:"), 0, 2);
        gp.add(pwField, 1, 2);
        gp.add(submit, 1,3);
        //positioning***********************************************************
        setTop(spane);
        setCenter(gp);        
    }   
}
