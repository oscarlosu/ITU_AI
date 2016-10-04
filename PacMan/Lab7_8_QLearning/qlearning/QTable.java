package qlearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import pacman.game.util.IO;
import tacticalAStar.TacticalAStar;


public class QTable {
    /**
     * for creating random numbers
     */
    Random randomGenerator;
    /**
     * the table variable stores the Q-table, where the state is saved
     * directly as the actual map. Each map state has an array of Q values
     * for all the actions available for that state.
     */
    @Expose
    HashMap<Integer, float[]> table;
    /**
     * the actionRange variable determines the number of actions available
     * at any map state, and therefore the number of Q values in each entry
     * of the Q-table.
     */
    @Expose
    int actionRange;

    // E-GREEDY Q-LEARNING SPECIFIC VARIABLES
    /**
     * for e-greedy Q-learning, when taking an action a random number is
     * checked against the explorationChance variable: if the number is
     * below the explorationChance, then exploration takes place picking
     * an action at random. Note that the explorationChance is not a final
     * because it is customary that the exploration chance changes as the
     * training goes on.
     */
    float explorationChance=0.4f;
    /**
     * the discount factor is saved as the gammaValue variable. The
     * discount factor determines the importance of future rewards.
     * If the gammaValue is 0 then the AI will only consider immediate
     * rewards, while with a gammaValue near 1 (but below 1) the AI will
     * try to maximize the long-term reward even if it is many moves away.
     */
    float gammaValue=0.9f;
    /**
     * the learningRate determines how new information affects accumulated
     * information from previous instances. If the learningRate is 1, then
     * the new information completely overrides any previous information.
     * Note that the learningRate is not a final because it is
     * customary that the learningRate changes as the
     * training goes on.
     */
    float learningRate=0.15f;

    //PREVIOUS STATE AND ACTION VARIABLES
    /**
     * Since in Q-learning the updates to the Q values are made ONE STEP
     * LATE, the state of the world when the action resulting in the reward
     * was made must be stored.
     */
    GameState prevState;
    /**
     * Since in Q-learning the updates to the Q values are made ONE STEP
     * LATE, the index of the action which resulted in the reward must be
     * stored.
     */
    int prevAction;

    /**
     * Q table constructor, initiates variables.
     * @param the number of actions available at any map state
     */
    QTable(int actionRange){
        randomGenerator = new Random();
        this.actionRange=actionRange;
        table = new HashMap<Integer, float[]>();
    }
    
    QTable(){
        randomGenerator = new Random();
        table = new HashMap<Integer, float[]>();
    }

    int getNextAction(GameState state){
        prevState = state.clone();
        if(randomGenerator.nextFloat()<explorationChance){
            prevAction=explore(state);
        } else {
            prevAction=getBestAction(state);
        }
        return prevAction;
    }

    /**
     * The getBestAction function uses a greedy approach for finding
     * the best action to take. Note that if all Q values for the current
     * state are equal (such as all 0 if the state has never been visited
     * before), then getBestAction will always choose the same action.
     * If such an action is invalid, this may lead to a deadlock as the
     * map state never changes: for situations like these, exploration
     * can get the algorithm out of this deadlock.
     *
     * @param the current map (state)
     * @return the action with the highest Q value
     */
    int getBestAction(GameState state){
    	float qvalues[] = getActionsQValues(state);
    	float bestValue = -Float.MIN_VALUE;
    	ArrayList<Integer> bestActions = new ArrayList<Integer>();
    	for(int i = 0; i < qvalues.length; ++i) {
    		if(qvalues[i] > bestValue) {
    			bestValue = qvalues[i];
    			bestActions.clear();
    			bestActions.add(i);
    		} else if(qvalues[i] == bestValue) {
    			bestActions.add(i);
    		}
    	}
        return bestActions.get(randomGenerator.nextInt(bestActions.size()));
    }

    /**
     * The explore function is called for e-greedy algorithms.
     * It can choose an action at random from all available,
     * or can put more weight towards actions that have not been taken
     * as often as the others (most unknown).
     *
     * @return index of action to take
     */
    int explore(GameState state) {    
    	return randomGenerator.nextInt(actionRange);
    }


    /**
     * The updateQvalue is the heart of the Q-learning algorithm. Based on
     * the reward gained by taking the action prevAction while being in the
     * state prevState, the updateQvalue must update the Q value of that
     * {prevState, prevAction} entry in the Q table. In order to do that,
     * the Q value of the best action of the current map state must also
     * be calculated.
     *
     * @param reward at the current map state
     * @param the current map state (for finding the best action of the
     * current map state)
     */
    void updateQvalue(float reward,  GameState state){
    	float qValues[] = getActionsQValues(prevState);
    	float oldQValue = qValues[prevAction];
    	float futureQValues[] = getActionsQValues(state);
    	float bestFutureQValue = Float.MIN_VALUE;
    	for(int i = 0; i < futureQValues.length; ++i) {
    		if(futureQValues[i] > bestFutureQValue) {
    			bestFutureQValue = futureQValues[i];
    		}
    	}
    	float newQValue = oldQValue + learningRate * (reward + gammaValue * bestFutureQValue - oldQValue);
    	qValues[prevAction] = newQValue;
    	table.put(prevState.hashCode(), qValues);
    }

    /**
     * The getActionsQValues function returns an array of Q values for
     * all the actions available at any state. Note that if the current
     * map state does not already exist in the Q table (never visited
     * before), then it is initiated with Q values of 0 for all of the
     * available actions.
     *
     * @param the current map (state)
     * @return an array of Q values for all the actions available at any state
     */
    float[] getActionsQValues(GameState state){
        float[] actions = getValues(state);
        if(actions==null){
            float[] initialActions = new float[actionRange];
            for(int i=0;i<actionRange;i++) initialActions[i]=0.f;
            table.put(state.hashCode(), initialActions);
            return initialActions;
        }
		return actions;
    }

    float[] getValues(GameState state){
        if(table.containsKey(state)){
            return table.get(state);
        }
        return null;
    }
    
    void Serialize(String filename) {
    	// Create json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = gson.toJson(this);
		// Save to file in "myData/" + filename
		IO.saveFile(filename, json, false);
    }
    static QTable Deserialize(String filename) {
    	// Read json string
		GsonBuilder builder = new GsonBuilder();
	    builder.excludeFieldsWithoutExposeAnnotation();
	    builder.setPrettyPrinting();
	    Gson gson = builder.create();
		String json = IO.loadFile(filename);
		QTable q = gson.fromJson(json, QTable.class);
		return q;
    }
}