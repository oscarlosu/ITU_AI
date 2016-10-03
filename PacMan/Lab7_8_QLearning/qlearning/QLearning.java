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
 * - Serialize QTable
 * - Define rewards
 * - Define states
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
            GameState state = new GameState();
            // TODO
            int action = pacmanAgent.qTable.getNextAction(state);
            game.advanceGame(MOVE.values()[action], ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
            moveCounter++;

            // REWARDS AND ADJUSTMENT OF WEIGHTS SHOULD TAKE PLACE HERE
            GameState newState = new GameState();
            // TODO
            float reward = 0;
            // TODO            
            pacmanAgent.qTable.updateQvalue(reward, newState);

            //Thread.sleep(1000);
        }
    }

    public static void main(String s[]) {
        QLController agent = new QLController();
        int generations = 10;
        
        try{
        	// Train
        	for(int i = 0; i < generations; ++i) {
        		runLearningLoop(agent, new StarterGhosts());
        	}
        	// Test
        	Executor exec = new Executor();
        	exec.runGameTimed(agent, new StarterGhosts(), true);
        	
        } catch (Exception e){
        	e.printStackTrace();;
            System.out.println("Thread.sleep interrupted!");
        }
    }
};

