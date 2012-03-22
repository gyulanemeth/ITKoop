package chatdesktop;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Chuckie
 */
public class ChatDesktop extends Application {
    
    private MenuItem fileDisconnect, fileExit;
    ChatPane chat=new ChatPane();
    LoginPane login=new LoginPane(); 
    Canvas canvas=new Canvas();   
    private boolean isConnected=false;
    public static int base_width=600,base_height=330;
    private JWSCHandler wsHandler = JWSCHandler.getInstance();
    @Override
    public void start(Stage primaryStage) {          
        wsHandler.setDesktop(this);
        //Init containers*******************************************************
        final BorderPane bpane=new BorderPane();               
        final StackPane spane=new StackPane();
        final MenuBar menubar=new MenuBar();
        Scene scene=new Scene(bpane,base_width,base_height); 
        //Setting containers****************************************************
        primaryStage.setResizable(false);
        //Background************************************************************
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        Rectangle colors = new Rectangle(bounds.getWidth(), bounds.getHeight(),
             new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new 
         Stop[]{
            new Stop(0, Color.CADETBLUE),
            new Stop(0.5, Color.AZURE),
            new Stop(1, Color.CADETBLUE)}));
        bpane.getChildren().add(colors);
        //Menu with eventhandler*********************************************
        Menu menuFile=new Menu("File");
        fileDisconnect=new MenuItem("Disconnect");
        fileDisconnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(isConnected){
                    chat.clear();
                    isConnected=false;
                    chat.setVisible(isConnected);                  
                    login.setVisible(!isConnected);
                    canvas.setConnected(isConnected);
                    login.play(1.0f, 0.0f);
                }
            }});
        fileExit=new MenuItem("Exit");
        fileExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    System.exit(1);
            }});
        login.submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    if(login.isFilled()){
                        if(!wsHandler.login(login.accountField.getText(), login.pwField.getText(),login.secure.selectedProperty().getValue())) {
                            Logger.getLogger(ChatDesktop.class.getName()).log(Level.SEVERE, null, "Login Failed");
                            System.err.println("Login Failed");
                        }else{
                            isConnected=true;
                            chat.setName(login.accountField.getText());
                            login.clear();
                            login.setVisible(!isConnected);
                            chat.setVisible(isConnected);
                            canvas.setConnected(isConnected);
                            chat.play(1.0f,0.0f);
        }}}});
        login.pwField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ev) {
                    if(ev.getCode()==KeyCode.ENTER) 
                        if(login.isFilled()){
                        if(!wsHandler.login(login.accountField.getText(), login.pwField.getText(), login.secure.selectedProperty().getValue())) {
                            Logger.getLogger(ChatDesktop.class.getName()).log(Level.SEVERE, null, "Login Failed");
                            System.err.println("Login Failed");
                        }else{
                            isConnected=true;
                            chat.setName(login.accountField.getText());
                            login.clear();
                            login.setVisible(!isConnected);
                            chat.setVisible(isConnected);
                            canvas.setConnected(isConnected);
                            chat.play(1.0f,0.0f);                            
        }}}});
        menuFile.getItems().addAll(fileDisconnect, fileExit);
        menubar.getMenus().add(menuFile);
        //positioning***********************************************************
        spane.getChildren().addAll(login, chat);
        chat.setVisible(false);
        canvas.setConnected(isConnected);
        bpane.setTop(menubar);
        bpane.setLeft(canvas);
        bpane.setRight(spane);
        //other*****************************************************************
        primaryStage.setTitle("ChatProgram 1.013c");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        public static void main(String[] args) {
        launch(args);
    }
        
}
