let socket;

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
 * method that calls when the window is loaded
 */
window.onload = function () {
    let camera, scene, renderer, canvas;
    let cameraControls;

    let worldObjects = {};

    /**
     * function that initializes the world
     * @param array unsure
     * @param offset unsure
     */
    function init(array, offset) {
        //region Camera
        camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
        cameraControls = new THREE.OrbitControls(camera);
        cameraControls.target.set(15,5,15);
        cameraControls.maxPolarAngle = Math.PI / 2;
        cameraControls.minAzimuthAngle = -Math.PI / 5;
        cameraControls.maxAzimuthAngle = Math.PI / 5;
        cameraControls.enablePan = false;

        camera.position.z = 15;
        camera.position.y = 35;
        camera.position.x = 15;

        cameraControls.update();
        //endregion

        //sets canvas and renderer
        //region canvas
        scene = new THREE.Scene();

        canvas = document.querySelector('#view');
        renderer = new THREE.WebGLRenderer({ antialias: true, canvas});
        renderer.outputEncoding = THREE.sRGBEncoding;
        renderer.setPixelRatio(window.devicePixelRatio);
        renderer.setSize(window.innerWidth, window.innerHeight + 5);
        //endregion

        //adds event listener to call the window resize function
        window.addEventListener('resize', onWindowResize, false);

        //Sets all the models and meshes to form the background, adds them to scene
        //region Background
        const geometry = new THREE.PlaneGeometry(30, 30);
        const groundmaterial = new THREE.MeshPhongMaterial({ map: new THREE.TextureLoader().load("textures/concrete_floor.jpg"), side: THREE.DoubleSide });
        const ground = new THREE.Mesh(geometry, groundmaterial);

        let floorgrid = loadGLTF('/models/floor/scene.gltf',1);

        const material = new THREE.MeshPhongMaterial({ color: 0xffffdd, side: THREE.DoubleSide });
        const geometry2 = new THREE.PlaneGeometry(30,5);
        const wall = new THREE.Mesh(geometry2,material);

        const wall2 = new THREE.Mesh(geometry2,material);

        const plaingeometry = new THREE.BoxGeometry(100,5,100);
        const plainmaterial = new THREE.MeshPhongMaterial({ color: 0xaaffaa, side: THREE.DoubleSide });
        const plain = new THREE.Mesh(plaingeometry,plainmaterial);

        const dockgeometry = new THREE.BoxGeometry(5,5,30);
        const dockmaterial = new THREE.MeshPhongMaterial({ color: 0xffdddd, side: THREE.DoubleSide });
        const dock = new THREE.Mesh(dockgeometry,dockmaterial);

        ground.rotation.x = Math.PI / 2.0;
        ground.position.x = 15;
        ground.position.z = 15;

        floorgrid.rotation.y = -Math.PI / 2.0;
        floorgrid.position.x = 15;
        floorgrid.position.y = -0.1;
        floorgrid.position.z = 15;

        wall.position.x = 15;
        wall.position.y = 2.5;

        wall2.rotation.y = Math.PI / 2.0;
        wall2.position.z = 15;
        wall2.position.y = 2.5;

        plain.position.x = 15;
        plain.position.y = -2.51;
        plain.position.z = 15;

        dock.position.x = 32.5;
        dock.position.y = 2.5;
        dock.position.z = 15;

        const worldgroup = new THREE.Group();
        worldgroup.add(ground,wall,wall2,plain,dock,floorgrid);
        scene.add(worldgroup);
        //endregion

        //Sets all the lights, their intensities, and locations, then adds them to scene
        //region Lights
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
        scene.add(lightgroup);
        //endregion
    }

    /**
     * resizes the view when window size changes
     */
    function onWindowResize() {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(window.innerWidth, window.innerHeight);
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
     * JSON parses a string
     * @param input string to be parsed
     * @returns {any} the JSON parsed string
     */
    function parseCommand(input = "") {
        return JSON.parse(input);
    }

    /*
     * Hier wordt de socketcommunicatie geregeld. Er wordt een nieuwe websocket aangemaakt voor het webadres dat we in
     * de server geconfigureerd hebben als connectiepunt (/connectToSimulation). Op de socket wordt een .onmessage
     * functie geregistreerd waarmee binnenkomende berichten worden afgehandeld.
     */
    socket = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/connectToSimulation");
    socket.onmessage = function (event) {
        //Hier wordt het commando dat vanuit de server wordt gegeven uit elkaar gehaald
        const command = parseCommand(event.data);

        //Wanneer het commando is "object_update", dan wordt deze code uitgevoerd. Bekijk ook de servercode om dit goed te begrijpen.
        if (command.command === "object_update") {
            console.log(command.command);
            //Wanneer het object dat moet worden geupdate nog niet bestaat (komt niet voor in de lijst met worldObjects op de client),
            //dan wordt het 3D model eerst aangemaakt in de 3D wereld.
            if (Object.keys(worldObjects).indexOf(command.parameters.uuid) < 0) {
                //Wanneer het object een robot is, wordt de code hieronder uitgevoerd.
                if (command.parameters.type == "robot") {
                    const robotgeometry = new THREE.BoxGeometry(0.9, 0.3, 0.9);
                    const cubeMaterials = [
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_side.png"),
                            side: THREE.DoubleSide
                        }), //LEFT
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_side.png"),
                            side: THREE.DoubleSide
                        }), //RIGHT
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_top.png"),
                            side: THREE.DoubleSide
                        }), //TOP
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_bottom.png"),
                            side: THREE.DoubleSide
                        }), //BOTTOM
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_front.png"),
                            side: THREE.DoubleSide
                        }), //FRONT
                        new THREE.MeshBasicMaterial({
                            map: new THREE.TextureLoader().load("textures/robot_front.png"),
                            side: THREE.DoubleSide
                        }), //BACK
                    ];
                    const material = new THREE.MeshFaceMaterial(cubeMaterials);
                    let robot = new THREE.Mesh(robotgeometry, material);

                    scene.add(robot);
                    worldObjects[command.parameters.uuid] = robot;
                }
                else if (command.parameters.type == "rack"){
                    let rack = loadGLTF('/models/storage_rack/scene.gltf',1);
                    // const rackgeometry = new THREE.BoxGeometry(1,3,1);
                    // const rackmaterials = [
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_side.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_side.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_top_bottom.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_top_bottom.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_side.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/rack_side.png"),
                    //         side: THREE.DoubleSide
                    //     })
                    // ];
                    // const material = new THREE.MeshFaceMaterial(rackmaterials);
                    // let rack = new THREE.Mesh(rackgeometry,material);
                    scene.add(rack);
                    worldObjects[command.parameters.uuid] = rack;
                }
                else if (command.parameters.type == "truck"){
                    let truck = loadGLTF('/models/truck/scene.gltf',1);
                    // const truckgeometry = new THREE.BoxGeometry(2.2,2,8)
                    // const truckmaterials = [
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_right.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_left.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_top.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_bottom.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_rear.png"),
                    //         side: THREE.DoubleSide
                    //     }),
                    //     new THREE.MeshBasicMaterial({
                    //         map: new THREE.TextureLoader().load("textures/truck_front.png"),
                    //         side: THREE.DoubleSide
                    //     })
                    // ];
                    // const truckmaterial = new THREE.MeshFaceMaterial(truckmaterials);
                    // let truck = new THREE.Mesh(truckgeometry,truckmaterial);

                    scene.add(truck);
                    worldObjects[command.parameters.uuid] = truck;
                }
            }

            /*
             * Deze code wordt elke update uitgevoerd. Het update alle positiegegevens van het 3D object.
             */
            let object = worldObjects[command.parameters.uuid];

            object.position.x = command.parameters.x;
            object.position.y = command.parameters.y;
            object.position.z = command.parameters.z;

            object.rotation.x = command.parameters.rotationX;
            object.rotation.y = command.parameters.rotationY;
            object.rotation.z = command.parameters.rotationZ;
        } else if (command.command === "object_remove"){
            console.log("removing object: " + command.parameters.uuid);
            scene.remove(worldObjects[command.parameters.uuid]);
            worldObjects[command.parameters.uuid] = null;

        }

    }

    init();
    animate();
}
