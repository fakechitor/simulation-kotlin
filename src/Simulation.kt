import actions.InitActions
import entities.*
import map.Map
import map.Coordinates
import map.MapRenderer
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

// Codes for process user input
private const val START_LOOP_SIMULATION_CODE =  1
private const val NEXT_TURN_SIMULATION_CODE = 2
private const val STOP_SIMULATION_CODE = 3

class Simulation {
    private var amountOfTurns : Int = 1
    private lateinit var map : Map
    private val initActions = InitActions()
    private val gameData = GameData()
    private var printFullGameInfoFlag = true

    fun startSimulation() {
        printFullGameInfoFlag = true
        initSimulation()
        printGameInfo()
        askUserResponse()
    }

    private fun initSimulation(){
        map = initActions.createSimulationMap()
    //        map = Map()
//        map.createMap()
//        initAllTypesOfEntities()
    }

    private fun nextTurn() {
        amountOfTurns++
        turnActions()
        plantGrass()
        checkGameState()
    }


    private fun startGameLoop(){
        val running = AtomicBoolean(true)
        val gameLoopThread = Thread {
            while (running.get()) {
                nextTurn()
                printGameInfo()
                checkGameState()
                Thread.sleep(750)
            }
            pauseSimulation()
        }
        printFullGameInfoFlag = false
        gameLoopThread.start()

        while (running.get()) {
            val input = readLine()
            if (input == "3") {
                running.set(false)
            }
        }
        gameLoopThread.join()
        printFullGameInfoFlag = true
        askUserResponse()

    }


    private fun pauseSimulation() {
        printFullGameInfoFlag = true
        println("Симуляция приостановлена")
        println("Введите $START_LOOP_SIMULATION_CODE, чтобы запустить цикл симуляции")
        println("Введите $NEXT_TURN_SIMULATION_CODE, чтобы запустить следующий ход симуляции")
        askUserResponse()
    }

    private fun askUserResponse() {
        val userInput = getValidUserInput()
        when(userInput){
            "1" -> startGameLoop()
            "2" -> {
                nextTurn()
                printGameInfo()
                askUserResponse()
            }
            "3" -> pauseSimulation()
        }
    }

    private fun getValidUserInput(): String {
        val validValues = listOf("1","2","3")
        while (true){
            val userInput = readln()
            if (userInput in validValues){
                return userInput
            }
            println("Введите корректное значение! $START_LOOP_SIMULATION_CODE/$NEXT_TURN_SIMULATION_CODE/$STOP_SIMULATION_CODE")
        }
    }

    private fun printGameInfo(){
        printMap()
        if (printFullGameInfoFlag){
            println("Введите $START_LOOP_SIMULATION_CODE, чтобы запустить цикл симуляции")
            println("Введите $NEXT_TURN_SIMULATION_CODE, чтобы запустить следующий ход симуляции")
            println("Введите $STOP_SIMULATION_CODE, чтобы остановть симуляцию")
        }
        else{
            println("Введите $STOP_SIMULATION_CODE, чтобы остановть симуляцию")
        }
        println()


    }

    private fun printMap(){
        println("Текущий ход: $amountOfTurns")
        MapRenderer().renderMap(map)
    }



    private fun turnActions() {
        val currentMap = map.map.toMap()
        val iterator = currentMap.entries.iterator()
        while (iterator.hasNext()) {
            val (coordinate, entity) = iterator.next()
            if (entity is Herbivore) {
                entity.decreaseHealthPointsByHunger()
                removeDiedEntities(coordinate, entity)
                map = entity.makeMove(coordinate, map)
            }
            else if (entity is Predator) {
                entity.decreaseHealthPointsByHunger()
                removeDiedEntities(coordinate, entity)
                map = entity.makeMove(coordinate, map)
            }
        }
    }

    private fun checkGameState() {
        val currentGameData = gameData.getCurrentGameStats(map)
        if (currentGameData["Rabbit"] == 0){
            println("Симуляция завершена. Все зайцы вымерли :(")
            println()
            println()
            amountOfTurns = 1
            println("Создана новая карта для симуляции:")
            startSimulation()
        }
        else if (currentGameData["Wolf"] == 0){
            println("Симуляция завершена. Все волки вымерли :(")
            println()
            println()
            amountOfTurns = 1
            println("Создана новая карта для симуляции:")
            startSimulation()

        }
    }


    private fun plantGrass(){
        val randomNumber = Random.nextInt(1, 3)
        if (randomNumber == 1){
            map.createEntityOnRandomCoordinates(entities.Grass())
        }
    }

    private fun removeDiedEntities(coordinates: Coordinates, entity : Creature){
        if (entity.healthPoints <= 0 ){
            map.setEntity("",coordinates)
        }
    }
//    private fun initAllTypesOfEntities(){
//        for (entity in Entities.entries){
//            when(entity){
//                Rabbit -> {createEntity("Rabbit")}
//                Wolf -> {createEntity("Wolf")}
//                Rock -> {createEntity("Rock")}
//                Tree -> {createEntity("Tree")}
//                Grass -> {createEntity("Grass")}
//            }
//        }
//    }

//    private fun createEntity(entity: String) {
//        val amountOfEntities = AMOUNT_OF_ENTITIES_FOR_SPAWN[entity]
//        if (amountOfEntities is Int) {
//            for (i in 1..amountOfEntities) {
//                when (entity) {
//                    "Rabbit" -> map.createEntityOnRandomCoordinates(Herbivore())
//                    "Wolf" -> map.createEntityOnRandomCoordinates(Predator())
//                    "Rock" -> map.createEntityOnRandomCoordinates(entities.Rock())
//                    "Tree" -> map.createEntityOnRandomCoordinates(entities.Tree())
//                    "Grass" -> map.createEntityOnRandomCoordinates(entities.Grass())
//                }
//            }
//        }
//    }
}