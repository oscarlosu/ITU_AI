package qlearning;

import java.util.Random;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.GameView;

import static pacman.game.Constants.DELAY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

/**
 * TODO:
 * - Improve rewards?
 * 		+ Reward for increasing abstract distance to ghosts
 * - Add escape junction direction to GameState
 * - Reverse direction in between junctions if ghosts closer than nearest junction
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

    static int runLearningLoop(QLController pacmanAgent, Controller<EnumMap<GHOST,MOVE>> ghostController, boolean visual, boolean randomInitPos, boolean training) throws Exception {
        Random rnd = new Random(0);
		Game game = new Game(rnd.nextLong(), randomInitPos);
		float reward = 0;
		GameView gv = null;
		if(visual) {
			gv = new GameView(game).showGame();
			MarkJunctions(game);
		}
			
        while(!game.gameOver()) {        	       	
        	boolean updateQTable = training && pacmanAgent.qTable.prevState != null;
        	boolean isAtJunction = game.isJunction(game.getPacmanCurrentNodeIndex());
        	// Choose action  
        	MOVE move = pacmanAgent.getMove(game,System.currentTimeMillis()+DELAY);
        	// Take action  
        	game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
        	if(visual) {
        		MarkJunctions(game);
	        	gv.repaint();
        	}
        	// Accumulate reward between junctions
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
            // Update QTable at junctions
            if(isAtJunction && updateQTable) {  
        		GameState newState = new GameState(game);
                //System.out.println("Reward " + reward);
                pacmanAgent.qTable.updateQvalue(reward, newState); 
                reward = 0;       
        	}

        	//Thread.sleep(300); 
             
        }
        if(game.getCurrentLevel() > 1) {
        	System.out.println("Reached level " + game.getCurrentLevel());
        }
        return game.getScore();
    }

    
    public static void MarkJunctions(Game game) {
    	GameView.addPoints(game, new Color(255, 0, 0), game.getJunctionIndices());
    }
    public static void main(String s[]) {
        QLController agent = new QLController();
        int generations = 10000;
        String filename = "QLearning/qTable.json";
        boolean visual = false;
        boolean randomInitPos = true;
        boolean train = false;
        try{
        	if(train) {
        		// Train
            	System.out.println("QLearning started:");
            	for(int i = 0; i < generations; ++i) {
            		int score = runLearningLoop(agent, new StarterGhosts(), visual, randomInitPos, train);
            		if(i % 1000 == 0) {
            			System.out.println("Generation " + i + ": Score: " + score + ". QTable entries: " + agent.qTable.table.size());
            		}        		
            	}
            	System.out.println("Saving qTable... (" + agent.qTable.table.size() + " entries)");
            	agent.qTable.Serialize(filename);
            	System.out.println("qTable saved.");
        	}        	
        	System.out.println("Testing...");
        	// Test
        	runLearningLoop(agent, new StarterGhosts(), true, randomInitPos, train);
        	System.out.println("Test ended.");
        	
        } catch (Exception e){
        	e.printStackTrace();;
            System.out.println("Thread.sleep interrupted!");
        }
    }
};

