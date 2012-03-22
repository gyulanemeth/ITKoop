/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 *
 * @author Chuckie
 */
public class Rectangle extends javafx.scene.shape.Rectangle{
    String id;
    Text text;
    public Rectangle(double arg0, double arg1, double arg2, double arg3,Text text, String id) {
        super(arg0, arg1, arg2, arg3);
        this.text=text;
        this.id=id;
    }
    void setColor(){
        Random random=new Random();
        super.setFill(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
    }
    void moveText(){
        text.setX(super.getX());
        text.setY(super.getY()+20);
    }
    @Override
    public void toFront(){
        super.toFront();
        text.toFront();        
    }
}
