package oslo.qlearning;

import java.util.Random;

import oslo.qlearning.GameState.DISTANCE;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
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
 * 		+ Punishment for staying near ghosts
 * 		+ Pill rewards based on distance
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
	
	public static float score;
	public static float time;
	public static float level;
	
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
		float lastScore = 0;
        while(!game.gameOver()) {
        	boolean updateQTable = training && pacmanAgent.qTable.prevState != null;
        	boolean isAtJunction = game.isJunction(game.getPacmanCurrentNodeIndex());
        	if(isAtJunction && !updateQTable) {
        		lastScore = game.getScore();
        	}
        	// Choose action  
        	MOVE move = pacmanAgent.getMove(game,System.currentTimeMillis()+DELAY);
        	// Take action  
        	game.advanceGame(move, ghostController.getMove(game.copy(),System.currentTimeMillis()+DELAY));
        	if(visual) {
        		//MarkJunctions(game);
	        	gv.repaint();
        	}

            // Update QTable at junctions
            if(isAtJunction && updateQTable) { 
            	// Reward is score increment or zero if pacman was eaten
            	reward = game.getScore() - lastScore;
            	reward = game.wasPacManEaten() ? 0 : reward;
                pacmanAgent.qTable.updateQvalue(reward, new GameState(game)); 
                //System.out.println("Reward " + reward); 
                // Update last score
                lastScore = game.getScore();
        	}

            if(visual) {
            	Thread.sleep(40); 
            }
        }
        if(game.getCurrentLevel() > 1) {
        	System.out.println("Reached level " + game.getCurrentLevel());
        }
        
        score += game.getScore();
    	time += game.getTotalTime();
    	level += game.getCurrentLevel() + 1;
        
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
        boolean randomInitPos = false;
        boolean train = true;
        try{
        	if(train) {
        		// Train
            	System.out.println("QLearning started:");
            	long startTime = System.currentTimeMillis();
            	for(int i = 0; i < generations; ++i) {
            		int score = runLearningLoop(agent, new Legacy2TheReckoning(), visual, randomInitPos, true);
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
        	score = 0;
        	time = 0;
        	level = 0;
        	for(int i = 0; i < 100; ++i) {
        		runLearningLoop(agent, new StarterGhosts(), false, false, false);
        	}
        	System.out.println("Starter Ghosts. Score: " + score / 100.0f + " Time: " + time / 100.0f + " Level: " + level / 100.0f);
        	
        	score = 0;
        	time = 0;
        	level = 0;
        	for(int i = 0; i < 100; ++i) {
        		runLearningLoop(agent, new Legacy(), false, false, false);
        	}
        	System.out.println("Legacy Ghosts. Score: " + score / 100.0f + " Time: " + time / 100.0f + " Level: " + level / 100.0f);
        	
        	score = 0;
        	time = 0;
        	level = 0;
        	for(int i = 0; i < 100; ++i) {
        		runLearningLoop(agent, new Legacy2TheReckoning(), false, false, false);
        	}
        	System.out.println("Legacy 2 The Reckoning Ghosts. Score: " + score / 100.0f + " Time: " + time / 100.0f + " Level: " + level / 100.0f);
        	
        	System.out.println("Test ended.");
        	
        } catch (Exception e){
        	e.printStackTrace();;
            System.out.println("Thread.sleep interrupted!");
        }
    }
};

