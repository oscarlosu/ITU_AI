package qlearning;

import java.util.Random;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;

import static pacman.game.Constants.DELAY;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO:
 * - Define rewards
 * 
 * @author Oscar
 *
 */
public class QLearning {    

    public static String getMoveName(int action){
        return MOVE.values()[action].toString();
    }

    static void runLearningLoop(QLController pacmanAgent, Controller<EnumMap<GHOST,MOVE>> ghostController) throws Exception {
        int moveCounter=0;
        Random rnd = new Random(0);
		Game game = new Game(rnd.nextLong());
		
        while(!game.gameOver()) {    
        	MOVE move = MOVE.NEUTRAL;
        	if(game.isJunction(game.getPacmanCurrentNodeIndex())) {
        		GameState state = new GameState(game);
                int action = pacmanAgent.qTable.getNextAction(state);
                move = MOVE.values()[action];
        	}
            
            game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
            moveCounter++;

            // REWARDS AND ADJUSTMENT OF WEIGHTS SHOULD TAKE PLACE HERE
            GameState newState = new GameState(game);
            float reward = 0;
            // TODO            
            pacmanAgent.qTable.updateQvalue(reward, newState);

            //Thread.sleep(1000);
        }
    }

    public static void main(String s[]) {
        QLController agent = new QLController();
        int generations = 10;
        String filename = "QLearning/qTable.json";
        
        try{
        	// Train
        	for(int i = 0; i < generations; ++i) {
        		runLearningLoop(agent, new StarterGhosts());
        	}
        	agent.qTable.Serialize(filename);
        	// Test
        	Executor exec = new Executor();
        	exec.runGameTimed(agent, new StarterGhosts(), true);
        	
        } catch (Exception e){
        	e.printStackTrace();;
            System.out.println("Thread.sleep interrupted!");
        }
    }
};

