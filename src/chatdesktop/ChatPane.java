/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
 
/**
 *
 * @author Chuckie
 */
public final class ChatPane extends BorderPane{
    //messages: az üzenetek mutatására;  mytext: a saját üzenet szerkeztésére.
    TextArea messages=new TextArea(), mytext=new TextArea();
    //submit: ezzel a gombbal is el lehet küldeni a saját üzenetünket
    Button submit=new Button("Submit");
    //felhasználó neve
    String name;
    //itt tárolom a szerverhez csatlakozott felhasználókat
    private VBox memberPanel=new VBox();
    
    /***
     * messages szövegmezőbe lehet írni, átírásra szorul, hiszen ezzel csak
     * teszteltem
     * @return a csatlakozott felhasználó saját üzenete, miután elküldte
     */
    String sendMsg(){
        String msg=mytext.getText();
        mytext.setText("");
        return msg;
    }
    /**
     * Függvény, a szövegmezőbe írásért
     * @param member egyik felhasználó neve
     * @param text egyik felhasználó üzenete
     */
    void addText(String member, String text){
        messages.appendText(member+" says: "+text+"\n");
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

    ChatPane(){
        super();
        this.setMinWidth(300);
        this.setMaxWidth(300);
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
        memberPanel.setPrefWidth(120);
        Label memberheader=new Label("Members: ");
        memberheader.setFont(Font.font("Berlin Sans FB", 14));
        memberPanel.getChildren().add(memberheader);
        //Bottom****************************************************************
        HBox chatPanel=new HBox();
        chatPanel.setSpacing(10);
        chatPanel.setPadding(new Insets(15, 12, 15, 12));
        mytext.setPrefHeight(20);
        DropShadow shadow=new DropShadow();
        shadow.setOffsetX(4);
        shadow.setOffsetY(5);
        mytext.setEffect(shadow);
        messages.setEffect(shadow);
        chatPanel.getChildren().addAll(mytext, submit);
        chatPanel.setAlignment(Pos.BOTTOM_LEFT);
        //positioning***********************************************************
        setBottom(chatPanel);
        //setRight(memberPanel);
        setCenter(messages);
        //base attributes*******************************************************
        setVisible(false);
    }
}
