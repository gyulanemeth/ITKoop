/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Enumeration;
import java.util.Hashtable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Chuckie
 */
public final class Canvas extends Group{
    public static int WIDTH=800, HEIGHT=600;
    private Hashtable objects=new Hashtable();
    Rectangle rec;
    public Canvas() {
        super();
        //BackGround
        rec=new Rectangle(WIDTH, HEIGHT, Color.WHITESMOKE);
        this.getChildren().add(rec);
        //Border
        DropShadow shadow=new DropShadow();
        shadow.setOffsetX(4);
        shadow.setOffsetY(5);
        this.setEffect(shadow);
    }

    public void initRectangleNode(final JWSClient handler, String id, int x, int y, int zOrder, String userData){
       int nodeWidth=userData.length()*10+80; 
       int nodeHeight=50;
       Text text=new Text(x+30, y+30, userData);
       text.setFont(Font.font("Courier New", 18.0));
       text.setFill(Color.WHITE);
       text.setDisable(true);
       
       final GraphRectangle rec=new GraphRectangle(x, y, nodeWidth, nodeHeight, text, id);      
       this.getChildren().add(rec); 
       this.getChildren().add(text);
       objects.put(id, rec);
       rec.actionRectangleNode(handler);
    }
    
    public boolean containsObject(String objId){
        return objects.containsKey(objId);
    }
    
    public void clearCanvas(){
        this.getChildren().clear();
    }
    
    public void setConnected(boolean isConnected){
        for(Enumeration<GraphRectangle> rects=objects.elements();rects.hasMoreElements();){
            rects.nextElement().setVisible(isConnected);
        }
    }

    public GraphRectangle getObject(String objId) {
        return (GraphRectangle)objects.get(objId);
    }

    public boolean deleteObject(String objId) {
        if(containsObject(objId)){            
            objects.remove(objId);
            return true;
        }
        return false;
    }
}
