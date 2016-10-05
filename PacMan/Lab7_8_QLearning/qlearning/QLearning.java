package qlearning;

import java.util.Random;

import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Game;
import pacman.game.GameView;
import qlearning.GameState.DISTANCE;

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
 * 		+ Punishment for staying near ghosts
 * - Add escape junction direction to GameState
 * - Hack junctions for faster training
 * 
 * @author Oscar
 *
 */
public class QLearning {    
	public static float pillEatenReward = 1;
	public static float powerPillEatenReward = 0.5f;
	public static float ghostEatenReward = 20;
	public static float pacmanEatenReward = -40;
	public static float ghostNearReward = -2;
	public static float pillNotEatenReward = -0.1f;
	
	public static HashMap<Integer, Integer> nearestJunctionLookup;
	public static int currentLevel = -1;
	
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
			//MarkJunctions(game);
		}
			
        while(!game.gameOver()) {
        	// If new level is reached, cache nearest junction
//        	if(currentLevel != game.getCurrentLevel()) {
//        		CreateNearestJunctionLookup(game);
//        		pacmanAgent.nearestJunctionLookup = nearestJunctionLookup;
//        		currentLevel = game.getCurrentLevel();
//        	}
        	boolean updateQTable = training && pacmanAgent.qTable.prevState != null;
        	boolean isAtJunction = game.isJunction(game.getPacmanCurrentNodeIndex());
        	// Choose action  
        	MOVE move = pacmanAgent.getMove(game,System.currentTimeMillis()+DELAY);
        	// Take action  
        	game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
        	if(visual) {
        		//MarkJunctions(game);
	        	gv.repaint();
        	}
        	// Accumulate reward between junctions
        	if(game.wasPillEaten()) {
            	reward += pillEatenReward;
            } else {
            	reward += pillNotEatenReward;
            }
            
            if(game.wasPowerPillEaten()) {
            	reward += powerPillEatenReward;
            }
            
            int pacmanNode = game.getPacmanCurrentNodeIndex();
            for(GHOST g : GHOST.values()) {
            	int ghostNode = game.getGhostCurrentNodeIndex(g);
            	 if(game.wasGhostEaten(g)) {
                 	reward += ghostEatenReward;
                 } else if(!game.isGhostEdible(g) && game.getGhostLairTime(g) <= 0 && 
                		 	GameState.getDistance(game.getShortestPathDistance(pacmanNode, ghostNode)) == DISTANCE.values()[0]) {
            		 reward += ghostNearReward;
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
                //System.out.println("Reward " + reward);
                reward = 0;       
        	}

            if(visual) {
            	Thread.sleep(40); 
            }
        }
        if(game.getCurrentLevel() > 1) {
        	System.out.println("Reached level " + game.getCurrentLevel());
        }
        return game.getScore();
    }

    
    public static void MarkJunctions(Game game) {
    	GameView.addPoints(game, new Color(255, 0, 0), game.getJunctionIndices());
    }
    
    public static void CreateNearestJunctionLookup(Game game) {
    	if(nearestJunctionLookup == null) {
    		nearestJunctionLookup = new HashMap<Integer, Integer>();
    	} else {
    		nearestJunctionLookup.clear();
    	}   	
    	
    	int pills[] = game.getPillIndices();
    	int junctions[] = game.getJunctionIndices();
    	for(int p : pills) {
    		for(MOVE m : MOVE.values()) {
    			int key = new NodeMOVE(p, m).hashCode();
    			// Find nearest junction
    			int minDist = Integer.MAX_VALUE;
    			int nearestJunction = 0;
    			for(Integer j : junctions) {
    				int dist = game.getShortestPathDistance(p, j, m);
    				if(dist < minDist) {
    					minDist = dist;
    					nearestJunction = j;
    				}
    			}
    			// Save to lookup
    			nearestJunctionLookup.put(key,  nearestJunction);
    		}
    		
    		
    	}
    }
    
    public static void main(String s[]) {
        QLController agent = new QLController();
        int generations = 500000;
        String filename = "QLearning/qTable.json";
        boolean visual = false;
        boolean randomInitPos = true;
        boolean train = true;
        try{
        	if(train) {
        		// Train
            	System.out.println("QLearning started:");
            	long startTime = System.currentTimeMillis();
            	for(int i = 0; i < generations; ++i) {
            		int score = runLearningLoop(agent, new StarterGhosts(), visual, randomInitPos, true);
            		if(i % 1000 == 0) {
            			long endTime = System.currentTimeMillis();
            			System.out.println("Generation " + i + "(" + (endTime - startTime)+ " ms): Score: " + score + ". QTable entries: " + agent.qTable.table.size());
            			startTime = System.currentTimeMillis();
            		}        		
            	}
            	System.out.println("Saving qTable... (" + agent.qTable.table.size() + " entries)");
            	agent.qTable.Serialize(filename);
            	System.out.println("qTable saved.");
        	}        	
        	System.out.println("Testing...");
        	// Test
        	runLearningLoop(agent, new StarterGhosts(), true, false, false);
        	System.out.println("Test ended.");
        	
        } catch (Exception e){
        	e.printStackTrace();;
            System.out.println("Thread.sleep interrupted!");
        }
    }
};

