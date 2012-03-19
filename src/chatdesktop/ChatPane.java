/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
 
/**
 *
 * @author Chuckie
 */
public final class ChatPane extends BorderPane{
    //messages: az üzenetek mutatására;  mytext: a saját üzenet szerkeztésére.
    private TextArea messages=new TextArea(), mytext=new TextArea();
    //submit: ezzel a gombbal is el lehet küldeni a saját üzenetünket
    private Button submit=new Button("Submit");
    //felhasználó neve
    private String name;
    //itt tárolom a szerverhez csatlakozott felhasználókat
    private VBox memberPanel=new VBox();
    private Rectangle rect=new Rectangle(400, 400, Color.WHITE);
    private FadeTransition fadeTransition;
    /***
     * messages szövegmezőbe lehet írni, átírásra szorul, hiszen ezzel csak
     * teszteltem
     * @return a csatlakozott felhasználó saját üzenete, miután elküldte
     */
    String sendMsg(){
        String msg=mytext.getText();
        mytext.setText("");
        addText(name, msg);
        return msg;
    }
    /**
     * Függvény, a szövegmezőbe írásért
     * @param member egyik felhasználó neve
     * @param text egyik felhasználó üzenete
     */
    void addText(String member, String text){
        messages.appendText("@"+member+": "+text+"\n");
    }
    /**
     * Név változó értékének beállítása
     * @param name felhazsnáló neve
     */
    void setName(String name){
        this.name=name;
    }
    /**
     * Szövegmező szövegeinek törlése
     */
    void clear(){
        messages.setText("");
        mytext.setText("");
    }
    /**
     * Szerveren más gépen lévőfelhasználók mutatására
     * @param name egy másik online felhasználó neve.
     */
    final void addMembers(String name){
            Label label=new Label(name);
            label.setFont(Font.font("Berlin Sans FB", 12));
            label.setPrefHeight(30);
            label.setWrapText(true);
            memberPanel.getChildren().add(label);     
    }
    /**
     * Ha egy felhasználó kijelentkezett, akkor ezt a fügvényt meghívhatjuk,
     * hogy töröljük a listáról.
     * @param name kijelentkezett felhasználó neve.
     */
    void removeMembers(String name){
        for(Node i:memberPanel.getChildren()){
            if(((Label)i).getText().equals(name))
                memberPanel.getChildren().remove(i);
        }
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
    ChatPane(){
        super();
        this.setMaxSize(300, 310);
        this.setMinSize(300, 310);
        this.setPrefSize(300, 310);
        //Center****************************************************************
        messages.setEditable(false);
        messages.autosize();
        messages.setWrapText(true);
        messages.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                messages.requestFocus();
            }
        });
        //Right*****************************************************************
        memberPanel.setPadding(new Insets(0, 0, 0, 5));
        memberPanel.setMinWidth(80);
        Label memberheader=new Label("Members: ");
        memberheader.setFont(Font.font("Berlin Sans FB", 14));
        memberPanel.getChildren().add(memberheader);
        addMembers("Csák Bálint Attila");
        addMembers("Szidor János");
        addMembers("Réti Dániel");
        addMembers("Stefanovics Richanovárd");
        //Bottom****************************************************************
        HBox chatPanel=new HBox();
        chatPanel.setSpacing(10);
        chatPanel.setPadding(new Insets(15, 12, 15, 12));
        mytext.setPrefHeight(20);
        mytext.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                mytext.requestFocus();
            }
        });
        mytext.setOnKeyPressed(new javafx.event.EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ev) {
                if(ev.isShiftDown() && ev.getCode()==KeyCode.ENTER)
                    sendMsg();
            }});
        submit.setMinSize(100, 20);
        submit.setPrefSize(100, 20);
        submit.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ev) {
                sendMsg();
            }});
        chatPanel.getChildren().addAll(mytext, submit);
        //positioning***********************************************************
        setBottom(chatPanel);
        setRight(memberPanel);
        setCenter(messages);
        this.getChildren().add(rect);
    }
}
