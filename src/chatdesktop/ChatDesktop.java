package chatdesktop;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public class ChatDesktop extends Application {
    
    private MenuItem fileDisconnect, fileExit;
    private final MenuItem newRect=new MenuItem("Add new Rectangle");
    private JWSClient wsClient = JWSClient.getInstance();
    private Canvas canvas=new Canvas();
    private ChatPane chat=new ChatPane();
    private LoginPane login=new LoginPane(); 
    private boolean isConnected=false;
    private ContextMenu contextMenu;
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
        //Init popup menu*******************************************************
        contextMenu=new ContextMenu();
        contextMenu.getItems().add(newRect);
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
        bpane.setTop(menubar);
        bpane.setLeft(canvas);
        bpane.setRight(spane);
        //Events****************************************************************
        windowEvent(primaryStage, scene);
        menuEvent();
        loginEvent();
        chatEvent();
        canvasEvent();
        //other*****************************************************************
        primaryStage.setTitle("ChatProgram 1.013c");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    void windowEvent(final Stage stage, final Scene scene){
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
                wsClient.disconnect();
                System.exit(1);
            }
        });
        scene.widthProperty().addListener( 
            new ChangeListener() {
            @Override
                public void changed(ObservableValue observable, 
                                    Object oldValue, Object newValue) {
                    Canvas.WIDTH=((Double)newValue).intValue()-310;
                    canvas.backgroundrec.setWidth(Canvas.WIDTH);
                }
            });
        scene.heightProperty().addListener( 
            new ChangeListener() {
            @Override
                public void changed(ObservableValue observable, 
                                    Object oldValue, Object newValue) {
                    Canvas.HEIGHT=((Double)newValue).intValue()-30;
                    canvas.backgroundrec.setHeight(Canvas.HEIGHT);
                }
            });
    }
    void menuEvent(){
        fileDisconnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(isConnected){
                    changeAnimation();
                    chat.clear();
                    wsClient.disconnect();
                    isConnected=false;
                    canvas.setConnected(isConnected);                   
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
                            changeAnimation();
                            isConnected=true;
                            chat.setName(login.accountField.getText());
                            login.clear();
                            canvas.setConnected(isConnected);
                            
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
                            changeAnimation();
                            isConnected=true;
                            chat.setName(login.accountField.getText());
                            login.clear();
                            canvas.setConnected(isConnected);                        
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
            @Override
            public void handle(KeyEvent ev) {
                if(ev.getCode()==KeyCode.ENTER)
                    if(ev.isShiftDown())
                        chat.mytext.appendText("\n");
                    else
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
                    newRect.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent arg0) {
                            canvas.initRectangleNode(wsClient, ((Integer)new Random().nextInt(10000)).toString(), (int)ev.getX(), (int)ev.getY(), 0, "newRectangle");
                            //wsClient.sendCreateObject("newRectangle", (int)ev.getX(), (int)ev.getY(), 0);
                        }
                    });
                    contextMenu.show(canvas, ev.getScreenX(), ev.getScreenY());
                }else{
                    contextMenu.hide();
                }
            }
        });
    }
    void changeAnimation(){
        final FadeTransition fadelogin=new FadeTransition(new Duration(500), login);
        final FadeTransition fadechat=new FadeTransition(new Duration(500), chat);
        if(!isConnected){
        chat.setOpacity(0.0);
        fadechat.setFromValue(0.0);
        fadechat.setToValue(1.0);
        fadechat.setCycleCount(1);
        fadelogin.setFromValue(1.0);
        fadelogin.setToValue(0.0);
        fadelogin.setCycleCount(1);
        fadelogin.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                login.setVisible(false);
                chat.setVisible(true);
                fadechat.play();
            }
        });
        fadelogin.play();
        }else{
            login.setOpacity(0.0);
            fadelogin.setFromValue(0.0);
            fadelogin.setToValue(1.0);
            fadelogin.setCycleCount(1);
            fadechat.setFromValue(1.0);
            fadechat.setToValue(0.0);
            fadechat.setCycleCount(1);
            fadechat.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent arg0) {
                    login.setVisible(true);
                    chat.setVisible(false);
                    fadelogin.play();
                }
            });
            fadechat.play();
        }
    }
    public void newMessage(String userName, String message){
        chat.addText(userName, message);
    }
    
    public static void main(String[] args) {
        launch(args);
    }        
}
