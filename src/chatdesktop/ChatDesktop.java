package chatdesktop;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Chuckie
 */
public class ChatDesktop extends Application {
    
    private MenuItem fileDisconnect, fileExit;
    private JWSClient wsClient = JWSClient.getInstance();
    private Canvas canvas=new Canvas();
    private ChatPane chat=new ChatPane();
    private LoginPane login=new LoginPane(); 
    private boolean isConnected=false;
    public static final int WIDTH=1110,HEIGHT=630;    
    
    @Override
    public void start(Stage primaryStage) {          
        wsClient.setDesktop(this);
        wsClient.setCanvas(canvas);
        //Init containers*******************************************************
        final BorderPane bpane=new BorderPane();               
        final StackPane spane=new StackPane();
        final MenuBar menubar=new MenuBar();
        Scene scene=new Scene(bpane,WIDTH,HEIGHT); 
        //Setting containers****************************************************
        primaryStage.setResizable(true);
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
        fileExit=new MenuItem("Exit");
        menuFile.getItems().addAll(fileDisconnect, fileExit);
        menubar.getMenus().add(menuFile);
        //positioning***********************************************************
        spane.getChildren().addAll(chat,login);
        chat.setVisible(false);
        canvas.setConnected(isConnected);
        bpane.setTop(menubar);
        bpane.setLeft(canvas);
        bpane.setRight(spane);
        //Events****************************************************************
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
                wsClient.disconnect();
                System.exit(1);
            }
        });
        windowEvent(scene);
        menuEvent();
        loginEvent();
        chatEvent();
        canvasEvent();
        //other*****************************************************************
        primaryStage.setTitle("ChatProgram 1.013c");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    void windowEvent(final Scene scene){
        scene.widthProperty().addListener( 
            new ChangeListener() {
            @Override
                public void changed(ObservableValue observable, 
                                    Object oldValue, Object newValue) {
                    Canvas.WIDTH=((Double)newValue).intValue()-310;
                    canvas.rec.setWidth(Canvas.WIDTH);
                }
            });
        scene.heightProperty().addListener( 
            new ChangeListener() {
            @Override
                public void changed(ObservableValue observable, 
                                    Object oldValue, Object newValue) {
                    Canvas.HEIGHT=((Double)newValue).intValue()-30;
                    canvas.rec.setHeight(Canvas.HEIGHT);
                }
            });
    }
    void menuEvent(){
        fileDisconnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(isConnected){
                    chat.clear();
                    wsClient.disconnect();
                    isConnected=false;
                    chat.setVisible(isConnected);                  
                    login.setVisible(!isConnected);
                    canvas.setConnected(isConnected);
                    login.play(1.0f, 0.0f);
                    
                }
            }});
        fileExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(!isConnected)
                    System.exit(1);
            }});
    }
    void loginEvent(){
        login.submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    if(login.isFilled()){
                        /*if(!wsClient.login(login.accountField.getText(), login.pwField.getText(),login.secure.selectedProperty().getValue())) {
                            Logger.getLogger(ChatDesktop.class.getName()).log(Level.SEVERE, null, "Login Failed");
                            System.err.println("Login Failed");
                        }else{*/
                            isConnected=true;
                            chat.setName(login.accountField.getText());
                            login.clear();
                            login.setVisible(!isConnected);
                            chat.setVisible(isConnected);
                            canvas.setConnected(isConnected);
                            chat.play(1.0f,0.0f);
                            canvas.initRectangleNode(wsClient, "111", 40, 20, 0, "111");
                            canvas.initRectangleNode(wsClient, "222", 100, 60, 0, "222");
        //}
            }}});
        login.pwField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ev) {
                    if(ev.getCode()==KeyCode.ENTER) 
                        if(login.isFilled()){
                        if(!wsClient.login(login.accountField.getText(), login.pwField.getText(), login.secure.selectedProperty().getValue())) {
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
    }
    void chatEvent(){
        chat.mytext.setOnMouseEntered(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                chat.mytext.requestFocus();
            }
        });
        chat.mytext.setOnKeyPressed(new javafx.event.EventHandler<KeyEvent>() {
            ///TODO ezt megkéne forditani, hogy inkább shift enterre legyen ujsor, enterre meg kuldje.
            @Override
            public void handle(KeyEvent ev) {
                if(ev.getCode()==KeyCode.ENTER)
                    wsClient.sendText(chat.name, chat.sendMsg());
            }});
        chat.submit.setMinSize(100, 20);
        chat.submit.setPrefSize(100, 20);
        chat.submit.setOnAction(new javafx.event.EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ev) {
                wsClient.sendText(chat.name, chat.sendMsg());
            }});
    }
    
    public void canvasEvent(){
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent ev) {
                if(isConnected)
                if(ev.getButton()==MouseButton.SECONDARY){
                    ContextMenu contextMenu=new ContextMenu();
                    MenuItem newRect=new MenuItem("Add new Rectangle");
                    newRect.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent arg0) {
                            canvas.initRectangleNode(wsClient, "333", (int)ev.getX(), (int)ev.getY(), 0, "newRectangle");
                        }
                    });
                    contextMenu.getItems().add(newRect);
                    contextMenu.show(canvas, ev.getScreenX(), ev.getScreenY());
                }
            }
        });
    }
    public void newMessage(String userName, String message){
        chat.addText(userName, message);
    }
    
    public static void main(String[] args) {
        launch(args);
    }        
}
