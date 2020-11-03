package com.nhlstenden.amazonsimulatie.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Deze class is een versie van het model van de simulatie. In dit geval is het
 * de 3D wereld die we willen modelleren (magazijn). De zogenaamde domain-logic,
 * de logica die van toepassing is op het domein dat de applicatie modelleerd, staat
 * in het model. Dit betekent dus de logica die het magazijn simuleert.
 */
public class World implements Model {
    /*
     * De wereld bestaat uit objecten, vandaar de naam worldObjects. Dit is een lijst
     * van alle objecten in de 3D wereld. Deze objecten zijn in deze voorbeeldcode alleen
     * nog robots. Er zijn ook nog meer andere objecten die ook in de wereld voor kunnen komen.
     * Deze objecten moeten uiteindelijk ook in de lijst passen (overerving). Daarom is dit
     * een lijst van Object3D onderdelen. Deze kunnen in principe alles zijn. (Robots, vrachrtwagens, etc)
     */
    private List<Object3D> worldObjects;
    private List<Robot> robotList;
    private List<Rack> rackList;

    /*
     * Dit onderdeel is nodig om veranderingen in het model te kunnen doorgeven aan de controller.
     * Het systeem werkt al as-is, dus dit hoeft niet aangepast te worden.
     */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    /*
     * De wereld maakt een lege lijst voor worldObjects aan. Daarin wordt nu één robot gestopt.
     * Deze methode moet uitgebreid worden zodat alle objecten van de 3D wereld hier worden gemaakt.
     */
    public World(int percentageFilled, int amountRobots) {
        this.worldObjects = new ArrayList<>();
        this.robotList = new ArrayList<>();
        this.rackList = new ArrayList<>();

        addRobots(amountRobots);

        populateRacks(percentageFilled);
    }

    /**
     * takes an Object3D object, checks if it's an instance of robot or rack, and adds to respective list
     * then adds the object to the worldobjects list to be rendered
     * @param object object to be added
     */
    public void addObject(Object3D object){
        if(object instanceof Robot){
            robotList.add((Robot)object);
        }else if(object instanceof Rack){
            rackList.add((Rack)object);
        }
        this.worldObjects.add(object);
    }
    public void addRobots(int amountRobots){
        for(int i = 1; i <= amountRobots; i++){
            if(i <= 29){
                addObject(new Robot(29,i));
            } else{
                addObject(new Robot(28, i-29));
            }
        }
    }

    /**
     * populates the grid randomly with storage racks, only in permitted areas
     * @param percentage percentage of slots to be filled with storage racks
     */
    public void populateRacks(int percentage){
        Random random = new Random();
        boolean checkgrid,checkrandom;
        for (int i = 2; i < 25; i++){
            for (int j = 2; j < 29; j++){
                checkrandom = random.nextInt(100)+1 <= percentage;
                checkgrid = !(i==4||i==7||i==10||i==13||i==16||i==19||i==22||j==15);
                if(checkrandom&&checkgrid){
                    addObject(new Rack(i,j));
                }
            }
        }
    }

    /**
     * goes through list of racks and robots, and assigns racks to robots.
     * sleeps 100 ms to prevent resource usage
     */
    public void pickupRacks(){
        while(rackList.size() != 0){
            for(int i = 0; i < rackList.size(); i++){
                for(Robot robot : robotList){
                    if(!robot.isBusy()&&!rackList.get(i).isBusy()){
                        robot.pickup(rackList.get(i));
                        removeRack(rackList.get(i));
                    }
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * removes rack from rackList, checks if the rack isn't null
     * @param rack rack to be removed
     */
    public void removeRack(Rack rack){
        try{
            rackList.remove(rack);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /*
     * Deze methode wordt gebruikt om de wereld te updaten. Wanneer deze methode wordt aangeroepen,
     * wordt op elk object in de wereld de methode update aangeroepen. Wanneer deze true teruggeeft
     * betekent dit dat het onderdeel daadwerkelijk geupdate is (er is iets veranderd, zoals een positie).
     * Als dit zo is moet dit worden geupdate, en wordt er via het pcs systeem een notificatie gestuurd
     * naar de controller die luisterd. Wanneer de updatemethode van het onderdeel false teruggeeft,
     * is het onderdeel niet veranderd en hoeft er dus ook geen signaal naar de controller verstuurd
     * te worden.
     */
    @Override
    public void update() {
        for (Object3D object : this.worldObjects) {
            if(object instanceof Updatable) {
                if (((Updatable)object).update()) {
                    pcs.firePropertyChange(Model.UPDATE_COMMAND, null, new ProxyObject3D(object));
                }
            }
        }
    }

    /*
     * Standaardfunctionaliteit. Hoeft niet gewijzigd te worden.
     */
    @Override
    public void addObserver(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    /*
     * Deze methode geeft een lijst terug van alle objecten in de wereld. De lijst is echter wel
     * van ProxyObject3D objecten, voor de veiligheid. Zo kan de informatie wel worden gedeeld, maar
     * kan er niks aangepast worden.
     */
    @Override
    public List<Object3D> getWorldObjectsAsList() {
        ArrayList<Object3D> returnList = new ArrayList<>();

        for(Object3D object : this.worldObjects) {
            returnList.add(new ProxyObject3D(object));
        }

        return returnList;
    }
}