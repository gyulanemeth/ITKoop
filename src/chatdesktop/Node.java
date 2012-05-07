/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 *
 * @author Chuckie
 */
public class Node extends javafx.scene.shape.Rectangle{
    private final String id;
    private Text text;
    private Point2D dragAnchor;
    //Edge.Point point;
    private double initX, initY;
    private ScaleTransition scale;
    public Node(double arg0, double arg1, double arg2, double arg3,Text text, String id) {
        super(arg0, arg1, arg2, arg3);
        this.text=text;
        this.id=id;
        setArcWidth(15.0);
        setArcHeight(15.0);
        setColor();        
        setOpacity(0.8);

        setEnabled(true);
        scale=new ScaleTransition(Duration.seconds(0.1),this);
    }

    public void setText(String value){
        text.setText(value);
        autosize();
    }
    public Text getText(){
        return text;
    }
    public String getNodeId(){
        return id;
    }
    private void setColor(){
        Random random=new Random();
        setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }
    public void move(int x, int y){
        if(x<=Canvas.WIDTH-getWidth() && x>=0)
            setX(x);
        else if(x>Canvas.WIDTH-getWidth())
            setX(Canvas.WIDTH-getWidth());
        else
            setX(0);
        if(y<=Canvas.HEIGHT-getHeight() && y>=0)
            setY(y);
        else if(y>Canvas.HEIGHT-getHeight())
            setY(Canvas.HEIGHT-getHeight());
        else
            setY(0);
        moveText();
    }
    private void moveText(){
        text.setX(getX()+30);
        text.setY(getY()+30);                
    }
    public String getObjId()
    {
        return this.id;
    }
    public final void setEnabled(final boolean visible){
        final FadeTransition recfade=new FadeTransition(new Duration(300),this);
        final FadeTransition textfade=new FadeTransition(new Duration(300), text);
        final Rectangle rec=this;
        rec.setVisible(true);
        text.setVisible(true);
        if(visible){
            recfade.setFromValue(0.0);
            recfade.setToValue(1.0);
            textfade.setFromValue(0.0);
            textfade.setToValue(1.0);
        }else{

            recfade.setFromValue(1.0);
            recfade.setToValue(0.0);
            textfade.setFromValue(1.0);
            textfade.setToValue(0.0);
        }
        recfade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                        rec.setVisible(visible);
            }
        });
        textfade.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {                
                        text.setVisible(visible);
            }
        });
        recfade.play();
        textfade.play();
    }
    @Override
    public void toFront(){
        super.toFront();
        text.toFront();        
    }
    
    public void resize(){
        this.setWidth(text.getText().length()*10+80);
    }
    void addEvent(final JWSClient handler){
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
                /*point.setX(newPositionX);
                point.setY(newPositionY);
                System.out.println(point);*/
                handler.sendMoveObject(ChatDesktop.name, id, (int)newPositionX, (int)newPositionY, false);
            }            
        });
        this.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                handler.sendMoveObject(ChatDesktop.name, id, (int)getX(), (int)getY(), true);
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
                setOpacity(1.0);
            }
        });
        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                setOpacity(0.8);
            }
        });
    }
}
