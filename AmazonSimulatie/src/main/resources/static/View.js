let socket;

let camera, scene, renderer, canvas;
let cameraControls;

let worldObjects = {};

/**
 * function that loads a GLTF model
 * @param path path to the model to be loaded
 * @param scaling scaling of the model
 * @returns {Group} Group of objects that were loaded from the model
 */
function loadGLTF(path, scaling) {
    let loader = new THREE.GLTFLoader();
    let group = new THREE.Group();

    loader.load(path, function(gltf) {
        gltf.scene.traverse(function( node ) {

            if ( node.isMesh )
            {   node.castShadow = true;
                node.receiveShadow = true
            }

        });
        let object3d = gltf.scene;
        object3d.scale.set(scaling,scaling,scaling);

        group.add(object3d);
    });
    return group;
}

/**
 * function that makes and sets camera and camera controls
 */
function addCamera(){
    camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    cameraControls = new THREE.OrbitControls(camera);
    cameraControls.target.set(15,5,25);
    cameraControls.maxPolarAngle = Math.PI / 2;
    cameraControls.minAzimuthAngle = -Math.PI / 5;
    cameraControls.maxAzimuthAngle = Math.PI / 5;
    cameraControls.enablePan = false;
    cameraControls.minDistance = 10;
    cameraControls.maxDistance = 40;

    camera.position.z = 15;
    camera.position.y = 35;
    camera.position.x = 15;

    cameraControls.update();
}

/**
 * function that makes and sets the background elements and their locations
 * @returns {Group} the group of background elements to be added to the scene
 */
function addBackground(){
    const groundgeometry = new THREE.PlaneGeometry(30, 30);
    const groundmaterial = new THREE.MeshPhongMaterial({ map: new THREE.TextureLoader().load("textures/concrete_floor.jpg"), side: THREE.DoubleSide });
    const ground = new THREE.Mesh(groundgeometry, groundmaterial);

    const floorgrid = loadGLTF('/models/floor/scene.gltf',1);

    const wallgeometry = new THREE.PlaneGeometry(30,5);
    const wallmaterial = new THREE.MeshPhongMaterial({ color: 0xffffdd, side: THREE.DoubleSide });
    const backwall = new THREE.Mesh(wallgeometry,wallmaterial);

    const sidewall = new THREE.Mesh(wallgeometry,wallmaterial);

    const plaingeometry = new THREE.BoxGeometry(100,5,100);
    const plainmaterial = new THREE.MeshPhongMaterial({ color: 0xaaffaa, side: THREE.DoubleSide });
    const plain = new THREE.Mesh(plaingeometry,plainmaterial);

    const dockgeometry = new THREE.BoxGeometry(5,5,30);
    const dockmaterial = new THREE.MeshPhongMaterial({ color: 0xffdddd, side: THREE.DoubleSide });
    const dock = new THREE.Mesh(dockgeometry,dockmaterial);
    
    const skyboxgeometry = new THREE.BoxGeometry(100,50,100);
    const skyboxmaterial = new THREE.MeshPhongMaterial({color: 0x03d7fc, side: THREE.DoubleSide})
    const skybox = new THREE.Mesh(skyboxgeometry,skyboxmaterial);

    ground.rotation.x = Math.PI / 2.0;
    ground.position.x = 15;
    ground.position.z = 15;

    floorgrid.rotation.y = -Math.PI / 2.0;
    floorgrid.position.x = 15;
    floorgrid.position.y = -0.1;
    floorgrid.position.z = 15;

    backwall.position.x = 15;
    backwall.position.y = 2.5;

    sidewall.rotation.y = Math.PI / 2.0;
    sidewall.position.z = 15;
    sidewall.position.y = 2.5;

    plain.position.x = 15;
    plain.position.y = -2.51;
    plain.position.z = 15;

    dock.position.x = 32.5;
    dock.position.y = 2.5;
    dock.position.z = 15;

    skybox.position.x = 15;
    skybox.position.y = 20;
    skybox.position.z = 15;

    const worldgroup = new THREE.Group();
    worldgroup.add(ground,backwall,sidewall,plain,dock,floorgrid,skybox);
    return worldgroup;
}

/**
 *  function that makes and sets the lighting elements and their locations
 * @returns {Group} the group of lights to be added to the scene
 */
function addLights(){
    const lightcolour = 0x404040;
    const pointlightintensity = 0.8;
    const pointlight = new THREE.PointLight(lightcolour, pointlightintensity,0,2);
    const pointlight2 = new THREE.PointLight(lightcolour, pointlightintensity,0,2);
    const pointlight3 = new THREE.PointLight(lightcolour, pointlightintensity,0,2);
    const pointlight4 = new THREE.PointLight(lightcolour, pointlightintensity,0,2);
    const pointlight5 = new THREE.PointLight(lightcolour, 2, 100, 2);
    pointlight.position.set(7.5, 10, 5);
    pointlight2.position.set(22.5,10,5);
    pointlight3.position.set(22.5,10,20);
    pointlight4.position.set(7.5,10,20);
    pointlight5.position.set(15,40,15);

    const amblightintensity = 0.4;
    const amblight = new THREE.AmbientLight(lightcolour,amblightintensity);

    const lightgroup = new THREE.Group();
    lightgroup.add(pointlight,pointlight2,pointlight3,pointlight4,pointlight5,amblight);
    return lightgroup;
}

/**
 * function that makes, and returns, a robot object
 * @returns {Mesh} robot mesh to be added to the scene
 */
function makeRobot(){
    const robotgeometry = new THREE.BoxGeometry(0.9, 0.3, 0.9);
    const cubeMaterials = [
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_side.png"),
            side: THREE.DoubleSide
        }), //LEFT
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_side.png"),
            side: THREE.DoubleSide
        }), //RIGHT
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_top.png"),
            side: THREE.DoubleSide
        }), //TOP
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_bottom.png"),
            side: THREE.DoubleSide
        }), //BOTTOM
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_front.png"),
            side: THREE.DoubleSide
        }), //FRONT
        new THREE.MeshPhongMaterial({
            map: new THREE.TextureLoader().load("textures/robot_front.png"),
            side: THREE.DoubleSide
        }), //BACK
    ];
    const material = new THREE.MeshFaceMaterial(cubeMaterials);
    const robot = new THREE.Mesh(robotgeometry, material);
    return robot;
}

/**
 * function that makes, and returns, a rack object
 * @returns {Group} rack groupp to be added to the scene
 */
function makeRack(){
    const rack = loadGLTF('/models/storage_rack/scene.gltf',1);
    return rack;
}

/**
 * function that makes, and returns, a truck object
 * @returns {Group} truck group to be added to the scene
 */
function makeTruck(){
    const truck = loadGLTF('/models/truck/scene.gltf',1);
    return truck;
}

/**
 * function that initializes the world,
 * makes a scene, selects the canvas, adds a renderer,
 * adds all elements to the scene
 */
function init() {
    //sets canvas and renderer
    //region canvas
    scene = new THREE.Scene();

    canvas = document.querySelector('#view');
    renderer = new THREE.WebGLRenderer({ antialias: true, canvas});
    renderer.outputEncoding = THREE.sRGBEncoding;
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight + 5);
    //endregion

    /**
     * resizes the view when window size changes
     */
    function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
    }

    //sets camera settings
    addCamera();

    //adds event listener to call the window resize function
    window.addEventListener('resize', onWindowResize, false);

    //Sets all the models and meshes to form the background, adds them to scene
    let background = addBackground();
    scene.add(background);

    //Sets all the lights, their intensities, and locations, adds them to scene
    let lights = addLights();
    scene.add(lights);
}

/**
 * JSON parses a string
 * @param input string to be parsed
 * @returns {any} the JSON parsed string
 */
function parseCommand(input = "") {
    return JSON.parse(input);
}

/**
 * request animation frame from browser, updates camera controls, and renders the scene
 */
function animate() {
    requestAnimationFrame(animate);
    cameraControls.update();
    renderer.render(scene, camera);
}


/**
 * method that calls when the window is loaded
 */
window.onload = function () {
    //initialize everything
    init();

    //Socket communication: makes a websocket, and parses commands from there, then executes them
    socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/connectToSimulation");
    socket.onmessage = function (event) {
        //Parses command from the websocket connection
        const command = parseCommand(event.data);

        //Checks which command to execute
        if (command.command === "object_update") {
            //Checks if object already exists, if not, create the object
            if (Object.keys(worldObjects).indexOf(command.parameters.uuid) < 0) {
                //Checks which object is going to be added, then adds them
                if (command.parameters.type == "robot") {
                    const robot = makeRobot();
                    scene.add(robot);
                    worldObjects[command.parameters.uuid] = robot;
                }
                else if (command.parameters.type == "rack"){
                    const rack = makeRack();
                    scene.add(rack);
                    worldObjects[command.parameters.uuid] = rack;
                }
                else if (command.parameters.type == "truck"){
                    const truck = makeTruck();
                    scene.add(truck);
                    worldObjects[command.parameters.uuid] = truck;
                }
            }

            //This code is executed with every update command, and updates the location of the corresponding object in the scene
            let object = worldObjects[command.parameters.uuid];

            object.position.x = command.parameters.x;
            object.position.y = command.parameters.y;
            object.position.z = command.parameters.z;

            object.rotation.x = command.parameters.rotationX;
            object.rotation.y = command.parameters.rotationY;
            object.rotation.z = command.parameters.rotationZ;
        } else if (command.command === "object_remove"){
            //removes the specified object from the scene and worldObjects list
            scene.remove(worldObjects[command.parameters.uuid]);
            worldObjects[command.parameters.uuid] = null;
        }

    }

    //Calls the animate function, so the scene is actually rendered
    animate();
}
