/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import javafx.scene.paint.Color;

/**
 *
 * @author Chuckie
 */
public class Edge extends javafx.scene.shape.Line{
    private Node startNode,endNode;
    public Point start,end;
    private String id;
    public Edge(double arg0, double arg1, Node startNode, String id) {
        super(arg0, arg1, arg0, arg1);
        start=new Point(arg0, arg1);
        end=new Point(arg0, arg1);        
        this.startNode=startNode;
        setFill(Color.BLACK);
        this.id=id;
    }
    private void setPosition(){
        setStartX(start.getX());
        setStartY(start.getY());
        setEndX(end.getX());
        setEndY(end.getY());
    }
    public void setStart(double arg0, double arg1){
        start.setX(arg0);
        start.setY(arg1);
        setPosition();        
    }
    public Point getStart(){
        return start;
    }
    public void setEnd(double arg0,double arg1){
        end.setX(arg0);
        end.setY(arg1);
        setPosition();
    }
    public Point getEnd(){
        return end;
    }
    public void setStartNode(Node arg0){
        startNode=arg0;
    }
    public Node getStartNode(){
        return startNode;
    }
    public void setEndNode(Node arg0){
        endNode=arg0;
    }
    public Node getEndNode(){
        return endNode;
    }
    class Point{
        private double x,y;
        Point(double arg0, double arg1){
            this.x=arg0;
            this.y=arg1;
        }
        public void setX(double arg0){
            x=arg0;
        }
        public double getX(){
            return x;
        }
        public void setY(double arg0){
            y=arg0;
        }
        public double getY(){
            return y;
        }

        @Override
        public String toString() {
            return x+" "+y;
        }
        
    }
}
