/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatdesktop;

import java.util.Enumeration;
import java.util.Hashtable;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Chuckie
 */
public final class Canvas extends Group{
    public static int WIDTH=800, HEIGHT=600; //canvas méretei
    private static Hashtable objects=new Hashtable(); //id- tárolására szükséges hashtábla
    Rectangle backgroundrec; //canvas háttere
    Edge temporaryEdge; //próbaverziós él
    public Canvas() {
        super();
        //BackGround
        backgroundrec=new Rectangle(WIDTH, HEIGHT, Color.WHITESMOKE);
        this.getChildren().add(backgroundrec);
        //Border
        DropShadow shadow=new DropShadow();
        shadow.setOffsetX(4);
        shadow.setOffsetY(5);
        this.setEffect(shadow);
        //base attributes*******************************************************
        setConnected(false);
    }
    /**
     * Létrehoz egy csúcsot
     * @param handler jwsc kliens, ami később összeköti a csúcsot a szerverrel
     * @param id csúcs azonosítója
     * @param x kezdeti koordinátája
     * @param y kezdeti koordinátája
     * @param zOrder látási rend, egyenlőre nincs használva
     * @param userData csúcsban tárolt infó
     */
    public void initRectangleNode(final JWSClient handler, String id, int x, int y, int zOrder, String userData){
       int nodeWidth=userData.length()*8+60; 
       int nodeHeight=30;
       Text text=new Text(x+15, y+15, userData);
       text.setFont(Font.font("Courier New", 14.0));
       text.setFill(Color.WHITE);
       text.setDisable(true);       
       final Node rec=new Node(x, y, nodeWidth, nodeHeight, text, id);
       add(rec);
       rec.addEvent(handler);
    }
    /**
     * tárolókba helyezi a csúcsot
     * @param rec adott csúcs
     */
    public void add(Node rec){
        if(!objects.contains(rec)){
            this.getChildren().add(rec.getGraphics());
            this.getChildren().add(rec.getText());
            objects.put(rec.getObjId(), rec);
        }
    }
    /**
     * kiveszi a tárolókból a csúcsot
     * @param rec adott csúcs
     */
    public void remove(Node rec){
        if(containsObject(rec.getObjId())){
            this.getChildren().remove(rec.getGraphics());
            this.getChildren().remove(rec.getText());
            objects.remove(rec.getObjId());
            rec.setActive(false);
        }
    }
    /**
     * tárolókba helyezi a csúcsot
     * @param rec adott csúcs
     */
    public void add(Edge edge){
       if(!objects.contains(edge)){
            this.getChildren().add(edge.getGraphics());
            objects.put(edge.getObjId(), edge);
       }
    }
    /**
     * kiveszi a tárolókból a csúcsot
     * @param rec adott csúcs
     */
    public void remove(Edge edg){
        if(containsObject(edg.getObjId())){
            this.getChildren().remove(edg.getGraphics());
            objects.remove(edg.getObjId());
            edg.setActive(false);
        }
    }
    /**
     * adott azonosító alapján megmondja, hogy benne van-e a tárolókban az
     * adott csúcs
     * @param objId adott azonosító
     * @return logikai érték, igaz ha benne van, egyébként hamis
     */
    public boolean containsObject(String objId){
        return objects.containsKey(objId);
    }
    /**
     * Visszaadja a rákattintott csúcsra mutató pointert
     * adott x,y értékek alapján
     * @param x ahova kattintottunk
     * @param y ahova kattintottunk
     * @return ha csúcsra kattintottunk, csúcs pointerét, egyébként null
     */
    public Node clickIn(double x, double y){
        for(Enumeration elem=objects.elements();elem.hasMoreElements();){
            Object obj=elem.nextElement();
            if(obj instanceof Node)
            if(((Node)obj).getGraphics().contains(x, y))
                return ((Node)obj);
        }
        return null;
    }
    /**
     * törli a canvas csúcsait
     */
    public void clear(){
        this.getChildren().clear();
    }
    /**
     * eltünteti a négyzeteet, az alapjn hogy csatlakozva van e a felhasználó
     * @param isConnected true, ha csatlakozott, egyébként nem
     */
    public void setConnected(boolean isConnected){
        for(Enumeration elem=objects.elements();elem.hasMoreElements();){
            Object obj=elem.nextElement();
            if(obj instanceof Node)
                ((Node)obj).setEnabled(isConnected);
            else if(obj instanceof Edge)
                ((Edge)obj).setEnabled(isConnected);               
        }
    }
    /**
     * Azonosító alapján visszaadja egy csúcs pointerét
     * @param objId adott csúcs azonosítója
     * @return csúcsra mutató pointer
     */
    public Node getObject(String objId) {
        return (Node)objects.get(objId);
    }
    /**
     * Azonsító alapján törli az adott csúcsot
     * @param objId adott csúcs azonosítója
     * @return true, ha sikerült törölni, egyébként false
     */
    public boolean deleteObject(String objId) {
        if(containsObject(objId)){            
            objects.remove(objId);
            return true;
        }
        return false;
    }
    public static void refresh(){
        for(Enumeration elem=objects.elements();elem.hasMoreElements();){
            Object obj=elem.nextElement();
            if(obj instanceof Edge){
                Edge edge=((Edge)obj);
                if(edge.isBroken()){
                    System.out.println("törött");
                    edge.getGraphics().setDisable(true);
                    edge.getGraphics().setVisible(false);
                }else{
                    edge.rePosition();
                }
            }
        }
    }
}