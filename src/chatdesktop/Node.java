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
 * Csúcsok reprezentálására szolgáló osztály
 * @author Chuckie
 */
public class Node{
    private Rectangle rect;
    private final String id; //csúcs azonosítója
    private Text text; //csúcsban tárolt szöveg grafikus vetülése
    private Point2D dragAnchor; //ebben tároljuk el, hol fogtuk meg a csúcsot
    //Edge.Point point; //próba él
    private double initX, initY; // csúcs mozgatásához szükséges pontok
    private ScaleTransition scale; //grafikus boost
    private boolean active=true; //működik-e még az elem
    /**
     * Konstruktor, ami átadja a koordinátákat az ősosztálynak
     * @param arg0 négyzet bf koordinátája
     * @param arg1 négyzet jf koordinátája
     * @param arg2 négyzet bl koordinátája
     * @param arg3 négyzet jl koordinátája
     * @param text szöveget tároló beépített javafx osztály
     * @param id csúcs azonosítója
     */
    public Node(double arg0, double arg1, double arg2, double arg3,Text text, String id) {
        rect=new Rectangle(arg0, arg1, arg2, arg3);
        this.text=text;
        this.id=id;
        rect.setArcWidth(15.0);
        rect.setArcHeight(15.0);
        setColor();        
        rect.setOpacity(0.8);

        setEnabled(true);
        scale=new ScaleTransition(Duration.seconds(0.1),rect);
    }
    /**
     * szöveget tudjuk itt beállítani
     * @param arg0 adott string
     */
    public void setText(String arg0){
        text.setText(arg0);
        resize();
    }
    /**
     * Visszaadja a szöveget tároló osztály pointerét
     * @return Text
     */
    public Text getText(){
        return text;
    }
    /**
     * Random színt generál a csúcsnak
     */
    private void setColor(){
        Random random=new Random();
        rect.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }
    /**
     * Kiszámolja a csúcs elmozfulását, attól függően, hogy hol fogtuk meg
     * @param x int koordináta-x
     * @param y int koordináta-y
     */
    public void move(int x, int y){
        if(x<=Canvas.WIDTH-rect.getWidth() && x>=0)
            rect.setX(x);
        else if(x>Canvas.WIDTH-rect.getWidth())
            rect.setX(Canvas.WIDTH-rect.getWidth());
        else
            rect.setX(0);
        if(y<=Canvas.HEIGHT-rect.getHeight() && y>=0)
            rect.setY(y);
        else if(y>Canvas.HEIGHT-rect.getHeight())
            rect.setY(Canvas.HEIGHT-rect.getHeight());
        else
            rect.setY(0);
        moveText();
    }
    /**
     * a csúcshoz passzintjuk a szöveget
     */
    private void moveText(){
        text.setX(rect.getX()+15);
        text.setY(rect.getY()+15);                
    }
    /**
     * Visszaadja a csúcs azonosítóját
     * @return 
     */
    public String getObjId()
    {
        return this.id;
    }
    /**
     * Attól függően, hogy a param true vagy false, a csúcs látható.
     * @param visible bool
     */
    public final void setEnabled(final boolean visible){
        final FadeTransition recfade=new FadeTransition(new Duration(300),rect);
        final FadeTransition textfade=new FadeTransition(new Duration(300), text);
        rect.setVisible(true);
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
                        rect.setVisible(visible);
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
    /**
     * A csúcs előre helyezése a láthatósági sorrendben
     */
    public void toFront(){
        rect.toFront();
        text.toFront();        
    }
    /**
     * Csúcs átméretezése
     */
    public void resize(){
        rect.setWidth(text.getText().length()*9+30);
    }
    /**
     * Eseménykezelők hozzárendelése a csúcshoz
     * @param handler 
     */
    void addEvent(final JWSClient handler){
        rect.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                double dragTransX=ev.getX()-dragAnchor.getX();
                double dragTransY=ev.getY()-dragAnchor.getY();
                double newPositionX=initX+dragTransX;
                double newPositionY=initY+dragTransY;
                if(newPositionX<=Canvas.WIDTH-rect.getWidth() && newPositionX>=0)
                    rect.setX(newPositionX);
                else if(newPositionX>Canvas.WIDTH-rect.getWidth())
                    rect.setX(Canvas.WIDTH-rect.getWidth());
                else
                    rect.setX(0);
                if(newPositionY<=Canvas.HEIGHT-rect.getHeight() && newPositionY>=0)
                    rect.setY(newPositionY);
                else if(newPositionY>Canvas.HEIGHT-rect.getHeight())
                    rect.setY(Canvas.HEIGHT-rect.getHeight());
                else
                    rect.setY(0);
                moveText();
                handler.sendMoveObject(ChatDesktop.name, id, (int)newPositionX, (int)newPositionY, false);
                Canvas.refresh();
            }            
        });
        rect.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                handler.sendMoveObject(ChatDesktop.name, id, (int)rect.getX(), (int)rect.getY(), true);
            }
        });
        rect.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                initX=rect.getX();
                initY=rect.getY();
                dragAnchor=new Point2D(ev.getX(),ev.getY());
            }
        });
        rect.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ev) {
                toFront();
                rect.setOpacity(1.0);
            }
        });
        rect.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                rect.setOpacity(0.8);
            }
        });
    }
    public Rectangle getGraphics(){
        return rect;
    }
    public void setActive(boolean arg0){
        active=arg0;
    }
    public boolean getActive(){
        return active;
    }
}
