/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Random;
import javafx.animation.ScaleTransition;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public final class Canvas extends Group{
    private int height=300, width=300;
    Rectangle red, green, blue;
    private Point2D dragAnchor;
    private double initX, initY;
    private ScaleTransition scale;
    public Canvas() {
        super();
        setBase();        
        initRectangle();
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
    void clearCanvas(){
        this.getChildren().clear();
    }
    void setConnected(boolean isConnected){
        red.setVisible(isConnected);
        green.setVisible(isConnected);
        blue.setVisible(isConnected);
    }
}
