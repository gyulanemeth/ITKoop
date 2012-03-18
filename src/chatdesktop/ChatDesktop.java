package chatdesktop;

import java.net.BindException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Chuckie
 */
public class ChatDesktop extends Application {
    
    private MenuItem fileDisconnect, fileExit;
    private ChatPane root=new ChatPane();
    private LoginPane login=new LoginPane();
    private Canvas canvas=new Canvas();
    private boolean isConnected=false;
    public static final int base_width=620,base_height=300;
    @Override
    public void start(Stage primaryStage) {          
        //Init containers*******************************************************
        final BorderPane bpane=new BorderPane();               
        final StackPane sp=new StackPane();
        final MenuBar menubar=new MenuBar();
        Scene scene=new Scene(bpane); 
        //Setting containers****************************************************
        bpane.setPadding(Insets.EMPTY);
        //bpane.setPrefSize(base_width, base_height);
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
        fileDisconnect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if(isConnected){
                    root.clear();
                    isConnected=false;
                    root.setVisible(isConnected);                  
                    login.setVisible(!isConnected);
                }
            }});
        fileExit=new MenuItem("Exit");
        fileExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    System.exit(1);
            }});
        login.getSubmit().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                    if(login.isFilled()){
                        isConnected=true;
                        root.setName(login.getName());
                        login.clear();
                        root.setVisible(isConnected);                  
                        login.setVisible(!isConnected);
                }}});
        login.getPwField().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ev) {
                if(ev.getCode()==KeyCode.ENTER) if(login.isFilled()){
                    isConnected=true;
                    root.setName(login.getName());
                    login.clear();
                    root.setVisible(isConnected);                  
                    login.setVisible(!isConnected);
                }}});
        menuFile.getItems().addAll(fileDisconnect, fileExit);
        menubar.getMenus().add(menuFile);
        //positioning***********************************************************
        sp.getChildren().addAll(login, root);
        root.setVisible(false);
        bpane.setTop(menubar);
        bpane.setLeft(new Canvas());
        bpane.setRight(sp);
        //other*****************************************************************
        primaryStage.setTitle("ChatProgram 1.013c");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        /**
     * csatolófüggvény, megírásra szorul, mert nem értek a websocketekhez
     * @return JSONObjecttel tér vissza, benne a felhasználó login és pw-jével
     * @throws JSONException
     * @throws Exception ha, üresek a mezők
     */
    public JSONObject sendAuth() throws JSONException, Exception{
        JSONObject object=new JSONObject();
        if(login.isFilled()){                    
        object.append("name", login.getName());
        object.append("pw", login.getPwField().getText());}
        else throw new Exception("Error: Empty acc/pw field!");
        return object;
    }
    /**
     * csatolófüggvény
     * @param obj JSONObject, benne a szerverhez csatlakozott felhasználók
     *          neveivel
     * @throws JSONException 
     */
    public void addMembers(JSONObject obj) throws JSONException{       
            while(obj.has("name"))
            root.addMembers(obj.getString("name"));
    }
    /**
     * csatolófüggvény
     * @param obj JSONObject, benne a szerverről lecsatlakozott felhasználók
     *          neveivel
     * @throws JSONException 
     */
    public void removeMember(JSONObject obj) throws JSONException{
        while(obj.has("name"))
        root.removeMembers(obj.getString("name"));
    }
    /**
     * csatolófüggvény, ChatPane szövegmezőjébe küldi egy felhasználó üzenetét
     * @param obj JSONObject, benne a felhasználó nevével és üzenetével
     * @throws JSONException 
     */
    public void addMessage(JSONObject obj) throws JSONException{
        String name=obj.getString("name");
        StringBuilder sb=new StringBuilder();
        while(obj.has("msg")){
            sb.append(obj.getString("msg"));
        }
            root.addText(name, sb.toString());
    }
        public static void main(String[] args) {
        launch(args);
    }
        
}