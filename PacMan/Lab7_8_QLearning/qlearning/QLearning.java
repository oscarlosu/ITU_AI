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
 * - Replace int distances with abstract distances
 * - Improve rewards?
 * - Rewards accumulated between junctions
 * - Initialize pacman in different positions
 * 
 * @author Oscar
 *
 */
public class QLearning {    
	public static float pillEatenReward = 1;
	public static float powerPillEatenReward = 0.5f;
	public static float ghostEatenReward = 10;
	public static float pacmanEatenReward = -20;
	
    public static String getMoveName(int action){
        return MOVE.values()[action].toString();
    }

    static int runLearningLoop(QLController pacmanAgent, Controller<EnumMap<GHOST,MOVE>> ghostController) throws Exception {
        Random rnd = new Random(0);
		Game game = new Game(rnd.nextLong());
        while(!game.gameOver()) {        	
        	MOVE move = MOVE.NEUTRAL;
        	boolean updateQTable = pacmanAgent.qTable.prevState != null;
        	if(game.isJunction(game.getPacmanCurrentNodeIndex())) {  
        		GameState state = new GameState(game);
        		// REWARDS AND ADJUSTMENT OF WEIGHTS SHOULD TAKE PLACE HERE
                if(updateQTable) {                	
                    float reward = 0; 
                    if(game.wasPillEaten()) {
                    	reward += pillEatenReward;
                    }
                    
                    if(game.wasPowerPillEaten()) {
                    	reward += powerPillEatenReward;
                    }
                    
                    for(GHOST g : GHOST.values()) {
                    	 if(game.wasGhostEaten(g)) {
                         	reward += ghostEatenReward;
                         }
                    }
                   
                    if(game.wasPacManEaten()) {
                    	reward += pacmanEatenReward;
                    }
                    
                    System.out.println("Reward " + reward);
                    pacmanAgent.qTable.updateQvalue(reward, state); 
                }    
        		
        		
    			int action = pacmanAgent.qTable.getNextAction(state);
                move = MOVE.values()[action];
                game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
                
        	} else {
        		game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
        	}


        	//Thread.sleep(1000); 
             
        }
        if(game.getCurrentLevel() > 1) {
        	System.out.println("Reached level " + game.getCurrentLevel());
        }
        return game.getScore();
    }

    public static void main(String s[]) {
        QLController agent = new QLController();
        int generations = 1000;
        String filename = "QLearning/qTable.json";
        
        try{
        	// Train
        	System.out.println("QLearning started:");
        	for(int i = 0; i < generations; ++i) {
        		int score = runLearningLoop(agent, new StarterGhosts());
        		System.out.println("Generation " + i + ". Score: " + score);
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

