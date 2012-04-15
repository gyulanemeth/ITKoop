/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Random;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public class GraphRectangle extends javafx.scene.shape.Rectangle{
    private final String id;
    private Text text;
    private Point2D dragAnchor;
    private double initX, initY;
    private ScaleTransition scale;
    public GraphRectangle(double arg0, double arg1, double arg2, double arg3,Text text, String id) {
        super(arg0, arg1, arg2, arg3);
        super.setArcWidth(15.0);
        super.setArcHeight(15.0);
        this.text=text;
        this.id=id;
        scale=new ScaleTransition(Duration.seconds(0.1),this);
        setColor();
        setVisible(true); 
    }
    
    private void setColor(){
        Random random=new Random();
        super.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }
    private void setAlpha(double value){
        super.setOpacity(value);
    }
    void moveText(){
        text.setX(super.getX()+30);
        text.setY(super.getY()+30);
    }
    @Override
    public void toFront(){
        super.toFront();
        text.toFront();        
    }

    void setData(String data) {
        text=new Text(data);
    }
    void actionRectangleNode(final JWSClient handler){
        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                double dragTransX=ev.getX()-dragAnchor.getX();
                double dragTransY=ev.getY()-dragAnchor.getY();
                double newPositionX=initX+dragTransX;
                double newPositionY=initY+dragTransY;
                if(newPositionX<=Canvas.WIDTH-getWidth() && newPositionX>=0)
                    setX(newPositionX);
                else if(newPositionX>Canvas.WIDTH-getWidth())
                    setX(Canvas.WIDTH-getWidth());
                else
                    setX(0);
                if(newPositionY<=Canvas.HEIGHT-getHeight() && newPositionY>=0)
                    setY(newPositionY);
                else if(newPositionY>Canvas.HEIGHT-getHeight())
                    setY(Canvas.HEIGHT-getHeight());
                else
                    setY(0);
                moveText();
                //handler.sendMoveObject("hiba", id, (int)newPositionX, (int)newPositionY, false);
            }            
        });
        this.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent arg0) {
                //handler.sendMoveObject("hiba", id, (int)getX(), (int)getY(), true);
            }
        });
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
            }
        });
        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                initX=getX();
                initY=getY();
                dragAnchor=new Point2D(ev.getX(),ev.getY());
            }
        });
        this.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                toFront();
                setAlpha(0.8);
            }
        });
        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                setAlpha(1.0);
            }
        });
    }
}
