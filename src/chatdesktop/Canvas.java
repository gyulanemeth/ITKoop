/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Enumeration;
import java.util.Hashtable;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public final class Canvas extends Group{
    private int width=800, height=600;
    Hashtable objects=new Hashtable();
    JWSClient wsHandler;
    private Point2D dragAnchor;
    private double initX, initY;
    private ScaleTransition scale;
    public Canvas(JWSClient wsHandler) {
        super();
        this.wsHandler=wsHandler;
        setBase();
        setBorder();
    }
    void setBase(){
        Rectangle rec=new Rectangle(width, height, Color.WHITESMOKE);
        this.getChildren().add(rec);
    }
    void setBorder(){
        DropShadow shadow=new DropShadow();
        shadow.setOffsetX(4);
        shadow.setOffsetY(5);
        this.setEffect(shadow);
    }
    void initRectangleNode(final JWSClient handler, String id, int x, int y, int zOrder, String text){
       ///TODO szélességét magasságát találja ki!!
       int nodeWidth=50; int nodeHeight=30; 
       Text realtext=new Text(x, y+20, text);
       realtext.setFill(Color.WHITE);
       RectangleNode rec=new RectangleNode(x, y, nodeWidth, nodeHeight, realtext, id);
       rec.setColor();
       rec.setVisible(true);       
       this.getChildren().add(rec); 
       this.getChildren().add(realtext);
       objects.put(id, rec);
       actionRectangleNode(rec, handler);    
    }
    
    void actionRectangleNode(final chatdesktop.RectangleNode rec,final JWSClient handler){
        rec.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                double dragTransX=ev.getX()-dragAnchor.getX();
                double dragTransY=ev.getY()-dragAnchor.getY();
                double newPositionX=initX+dragTransX;
                double newPositionY=initY+dragTransY;
                if(newPositionX<=width-rec.getWidth() && newPositionX>=0)
                    rec.setX(newPositionX);
                else if(newPositionX>width-rec.getWidth())
                    rec.setX(width-rec.getWidth());
                else
                    rec.setX(0);
                if(newPositionY<=height-rec.getHeight() && newPositionY>=0)
                    rec.setY(newPositionY);
                else if(newPositionY>height-rec.getHeight())
                    rec.setY(height-rec.getHeight());
                else
                    rec.setY(0);
                rec.moveText();
                handler.sendMoveObject("hiba", rec.id, (int)newPositionX, (int)newPositionY, false);
            }            
        });
        rec.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                handler.sendMoveObject("hiba", rec.id, (int)rec.getX(), (int)rec.getY(), true);
            }
        });
                
        rec.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                initX=rec.getX();
                initY=rec.getY();
                dragAnchor=new Point2D(ev.getX(),ev.getY());
            }
        });
        rec.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                rec.toFront();
                scale=new ScaleTransition(Duration.seconds(0.1), rec);
                scale.setToX(0.8f);
                scale.setToY(0.8f);
                scale.play();
            }
        });
        rec.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                scale=new ScaleTransition(Duration.seconds(0.1), rec);
                scale.setToX(1.0f);
                scale.setToY(1.0f);
                scale.play();
            }
        });
    }
    
    public boolean containsObject(String objId){
        return objects.containsKey(objId);
    }
    
    void clearCanvas(){
        this.getChildren().clear();
    }
    void setConnected(boolean isConnected){
        for(Enumeration<RectangleNode> rects=objects.elements();rects.hasMoreElements();){
            rects.nextElement().setVisible(isConnected);
        }
    }

    RectangleNode getObject(String objId) {
        return (RectangleNode)objects.get(objId);
    }

    void deleteObject(String objId) {
        objects.remove(objId);
    }
}
