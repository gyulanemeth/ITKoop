/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import javafx.scene.paint.Color;

/**
 * Éleket reprezentáló próbaosztály
 * @author Chuckie
 */
public class Edge extends javafx.scene.shape.Line{
    private Node startNode,endNode; //Csúcsok, amiket összeköt az él
    private Point start,end; //Kedző és végpontjai az élnek
    private String id; //Él azonosítója
    /**
     * 4 paraméteres konstruktor
     * @param arg0 él x koordinátája
     * @param arg1 él y koordinátája
     * @param startNode kezdeti csúcs amihez kötve van
     * @param id él azonosítója
     */
    public Edge(double arg0, double arg1, Node startNode, String id) {
        super(arg0, arg1, arg0, arg1);
        start=new Point(arg0, arg1);
        end=new Point(arg0, arg1);        
        this.startNode=startNode;
        setFill(Color.BLACK);
        this.id=id;
    }
    /**
     * Beállítja az él koordinátáit
     */
    private void setPosition(){
        setStartX(start.getX());
        setStartY(start.getY());
        setEndX(end.getX());
        setEndY(end.getY());
    }
    /**
     * Beállítja az él kezdőpontjának koordinátáit
     * @param arg0 kezdő x koordináta
     * @param arg1 kezdő y koordináta
     */
    public void setStart(double arg0, double arg1){
        start.setX(arg0);
        start.setY(arg1);
        setPosition();        
    }
    /*public Point getStart(){
        return start;
    }*/
    /**
     * Beállítja az él végpontjának koordinátáit
     * @param arg0 vég x koordináta
     * @param arg1 vég x koordináta
     */
    public void setEnd(double arg0,double arg1){
        end.setX(arg0);
        end.setY(arg1);
        setPosition();
    }
    /*public Point getEnd(){
        return end;
    }*/
    //getter-setterek a csúcsokra
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
    /**
     * Saját magam által megírt point osztály, próba
     */
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
        public void setCoord(double arg0, double arg1){
            x=arg0;
            x=arg1;
        }

        @Override
        public String toString() {
            return x+" "+y;
        }
        
    }
}
