/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Enumeration;
import java.util.Hashtable;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
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
    Rectangle backgroundrec;
    public Canvas() {
        super();
        //BackGround
        backgroundrec=new Rectangle(WIDTH, HEIGHT, Color.WHITESMOKE);
        this.getChildren().add(backgroundrec);
        //Border
        DropShadow shadow=new DropShadow();
        shadow.setOffsetX(4);
        shadow.setOffsetY(5);
        this.setEffect(shadow);
        //base attributes*******************************************************
        setConnected(false);
    }

    public void initRectangleNode(final JWSClient handler, String id, int x, int y, int zOrder, String userData){
       int nodeWidth=userData.length()*10+80; 
       int nodeHeight=50;
       Text text=new Text(x+30, y+30, userData);
       text.setFont(Font.font("Courier New", 18.0));
       text.setFill(Color.WHITE);
       text.setDisable(true);
       
       final Node rec=new Node(x, y, nodeWidth, nodeHeight, text, id);      
       add(rec);
       rec.addEvent(handler);
    }
    
    public void add(Node rec){
       this.getChildren().add(rec); 
       this.getChildren().add(rec.getText());
       objects.put(rec.getNodeId(), rec);
    }
    
    public void remove(Node rec){
        if(containsObject(rec.getNodeId())){
            this.getChildren().remove(rec);
            this.getChildren().remove(rec.getText());
            objects.remove(rec.getNodeId());
        }
    }
    
    public boolean containsObject(String objId){
        return objects.containsKey(objId);
    }
    
    public Node clickIn(double x, double y){
        for(Enumeration<Node> rects=objects.elements();rects.hasMoreElements();){
            Node rect=rects.nextElement();
            if(rect.contains(x, y))
                return rect;                
        }
        return null;
    }
    
    public void clearCanvas(){
        this.getChildren().clear();
    }
    
    public void setConnected(boolean isConnected){
        for(Enumeration<Node> rects=objects.elements();rects.hasMoreElements();){
            rects.nextElement().setEnabled(isConnected);
        }
    }

    public Node getObject(String objId) {
        return (Node)objects.get(objId);
    }

    public boolean deleteObject(String objId) {
        if(containsObject(objId)){            
            objects.remove(objId);
            return true;
        }
        return false;
    }
}