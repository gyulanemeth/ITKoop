/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Random;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Chuckie
 */
public final class Canvas extends Group{
    private int height=300, width=300;
    private Rectangle red, green, blue;
    private Point2D dragAnchor;
    private double initX, initY;
    public Canvas() {
        super();
        setBase();        
        initRectangle();
    }
    void setBase(){
        Rectangle rec=new Rectangle(width, height, Color.WHITESMOKE);
        this.getChildren().add(rec);
    }
    void initRectangle(){
        Random random=new Random();
        int randMaxWidth=width-30;
        red=new Rectangle((double)random.nextInt(width-30),(double)random.nextInt(height-30),30.0, 30.0);
        red.setFill(Color.RED);
        green=new Rectangle((double)random.nextInt(width-30),(double)random.nextInt(height-30),30.0, 30.0);
        green.setFill(Color.GREEN);
        blue=new Rectangle((double)random.nextInt(width-30),(double)random.nextInt(height-30),30.0, 30.0);
        blue.setFill(Color.BLUE);
        this.getChildren().addAll(red,green, blue);
        actionRectangle(red);
        actionRectangle(green);
        actionRectangle(blue);
    }
    void actionRectangle(final Rectangle rec){
        rec.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                double dragTransX=ev.getX()-dragAnchor.getX();
                double dragTransY=ev.getY()-dragAnchor.getY();
                double newPositionX=initX+dragTransX;
                double newPositionY=initY+dragTransY;
                if(newPositionX<=270 && newPositionX>=0)
                    rec.setX(newPositionX);
                else if(newPositionX>270)
                    rec.setX(270);
                else
                    rec.setX(0);
                if(newPositionY<=270 && newPositionY>=0)
                    rec.setY(newPositionY);
                else if(newPositionY>270)
                    rec.setY(270);
                else
                    rec.setY(0);
                //Ide jöhetne egy üzenetküldés.
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
            }
        });
    }
    void clearCanvas(){
        this.getChildren().clear();
    }
    
}
