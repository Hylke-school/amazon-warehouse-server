package com.nhlstenden.amazonsimulatie.models;

import com.nhlstenden.amazonsimulatie.controllers.GraphController;
import com.nhlstenden.amazonsimulatie.controllers.RobotController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.nhlstenden.amazonsimulatie.controllers.LoadingBayController;

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

    /*
     * Dit onderdeel is nodig om veranderingen in het model te kunnen doorgeven aan de controller.
     * Het systeem werkt al as-is, dus dit hoeft niet aangepast te worden.
     */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    LoadingBayController lbc = new LoadingBayController();
    GraphController graph = new GraphController();
    RobotController robotController;

    /**
     * initializes class,
     * makes a robotController, gives percentageFilled and amountRobots
     * gets racklist and robotlist from robotcontroller and adds them to world
     * @param percentageFilled percentage of slots to be filled with racks
     * @param amountRobots amount of robots to be added
     */
    public World(int percentageFilled, int amountRobots) {
        this.worldObjects = new ArrayList<>();
        robotController = new RobotController(percentageFilled,amountRobots);
        graph.populateGraph();

        for (Truck truck : lbc.truckList) {
            addObject(truck);
        }

        for (Rack rack : robotController.getRackList()){
            addObject(rack);
        }

        for(Robot robot : robotController.getRobotList()){
            addObject(robot);
        }

    }

    /**
     * takes an Object3D object, and adds it to the worldObjects list
     * @param object object to be added
     */
    public void addObject(Object3D object){
        this.worldObjects.add(object);
    }

    /**
     * calls pickupRack method on robotController, fires remove command to server on the object that gets removed
     */
    public void pickupRack() {
        robotController.pickupRack();
        robotController.pickupRackFromTruck();
        if (lbc.getTrucksAvailable() > 0) {
            Rack rackToTruck = robotController.pickupRackToTruck();
            if (rackToTruck != null) {
                worldObjects.remove(rackToTruck);
                lbc.loadPackageOffRobot();
                try {
                    if (rackToTruck.update()) {
                        pcs.firePropertyChange(Model.REMOVE_COMMAND, null, new ProxyObject3D(rackToTruck));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        if(lbc.getRackAmount() > 0){
            Rack rack = robotController.addRack();
            if(rack != null){
                worldObjects.add(rack);
                lbc.loadRackOnRobot();
            }
        }

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