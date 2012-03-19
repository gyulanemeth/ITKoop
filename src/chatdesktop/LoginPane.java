package chatdesktop;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public class LoginPane extends BorderPane{
    private TextField accountField=new TextField();
    private PasswordField pwField=new PasswordField();
    private Button submit=new Button("Submit");
    private Rectangle rect=new Rectangle(320, 300, Color.WHITE);;
    private FadeTransition fadeTransition;
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
        void play(float from, float to){
        rect.setVisible(true);
        fadeTransition = new FadeTransition(Duration.seconds(1), rect);
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        fadeTransition.setCycleCount(1);
        fadeTransition.play();
        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                rect.setVisible(false);
            }
        });
    }
    public LoginPane() {
        super();
        rect.setVisible(false);
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
        this.getChildren().add(rect);
    }   
}
