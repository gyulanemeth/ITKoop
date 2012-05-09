/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Éleket reprezentáló próbaosztály
 * @author Chuckie
 */
public class Edge{
    private Line line;
    private Node startNode,endNode; //Csúcsok, amiket összeköt az él
    private String id; //Él azonosítója
    private JWSClient client;
    private boolean finished=false;
    private boolean active=true; //működik-e még az elem
    /**
     * 4 paraméteres konstruktor
     * @param arg0 él x koordinátája
     * @param arg1 él y koordinátája
     * @param startNode kezdeti csúcs amihez kötve van
     * @param id él azonosítója
     */
    public Edge(Node startNode, String id,JWSClient client) {
        double x=(2*startNode.getGraphics().getX()+startNode.getGraphics().getWidth())/2;
        double y=(2*startNode.getGraphics().getY()+startNode.getGraphics().getHeight())/2;
        line=new Line(x,y,x,y);      
        this.startNode=startNode;
        this.id=id;
        this.client=client;
        line.setFill(Color.BLACK);        
    }
    /**
     * Beállítja az él koordinátáit
     */
    public void rePosition(){
        Rectangle start=startNode.getGraphics();
        line.setStartX((2*start.getX()+start.getWidth())/2);
        line.setStartY((2*start.getY()+start.getHeight())/2);
        if(finished){
            Rectangle end=endNode.getGraphics();
            line.setEndX((2*end.getX()+end.getWidth())/2);
            line.setEndY((2*end.getY()+end.getHeight())/2);
        }
        /**
         * Itt küldhetjük el a (line.getStartX,line.getStartY)-(line.getEndX,
         * line.getEndy) pozíciókat,, illetve, ha endNode nem null, akkor 
         * az él két végén lévő csúcsok idjét is. (Mivelhogy az is megvan)
         */
    }
    public void setEnd(double arg0, double arg1){
        line.setEndX(arg0);
        line.setEndY(arg1);
    }
    //getter-setterek a csúcsokra
    public void setStartNode(Node arg0){
        startNode=arg0;
    }
    public Node getStartNode(){
        return startNode;
    }
    public void setEndNode(Node arg0){
        endNode=arg0;
        finished=true;
    }
    public Node getEndNode(){
        return endNode;
    }
    public void setEnabled(boolean arg0){
        line.setOpacity(arg0?1.0:0.0);
    }
    public Line getGraphics(){
        return line;
    }
    public String getObjId(){
        return id;
    }
    boolean isBroken(){
        return (finished && (!startNode.getActive() ||!endNode.getActive()))?true:false;
    }
    public void setActive(boolean arg0){
        active=true;
    }
    public boolean getActive(){
        return active;
    }
}
