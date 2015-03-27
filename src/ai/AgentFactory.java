package ai;

import ai.agents.Agent;
import ai.agents.PassiveAgent;
import ai.agents.RandomAgent;
import ai.agents.AngryAgent;
import ai.agents.GreedyAgent;
import ai.agents.ContinentalAgent;
import ai.agents.FuriousAgent;
import ai.AgentTypes.Type;
import player.PlayerController;

public class AgentFactory {

    public static Agent buildAgent(Type type){
        switch(type){
//            case PASSIVE:
//                return new PassiveAgent();
//            case RANDOM:
//                return new RandomAgent();
            case ANGRY:
                return new AngryAgent();
            case GREEDY:
                return new GreedyAgent();
            case CONTINENTAL:
                return new ContinentalAgent();
            case FURIOUS:
                return new FuriousAgent();
            default:
                assert false : type;
        }
        return null;
    }
}
// Implemented

// Random - Randomly decides what to do
// Angry - Always attacks, fortifies outwards
// Greedy - Tries to draw a card every turn (and no more) Trades in cards ASAP
// Continental - Place initial armies to fill up small continents first,
// Furious - Always attacks if it has an advantage, also continental
//
// Ideas

// Continents2 - Focus efforts towards capturing whole continents
// Cluster - Cluster expands from his biggest set of armies
// Yakool - Yakool builds on Cluster by, focusing on enemies that are too strong, slower attacks than cluster, tries to get a card every turn.
// Boscoe - Boscoe is Yakool with a slowed down attack strategy
// Bort - Bort is slow and steady, 1 attack per turn, slower than boscoe
// Communist - Spreads armies evenly among all owned territories, attacks the weakest enemy territory
// Defendo - Tries to hold on to the territories it starts with, starts by trying to hold a small continent
// Pixie - Attacks continents that have the fewest border points, fortifies to the front lines
// Trotsky - Communist with some extra features
// Vulture - Singles out weak opponents and focuses on eliminating them
// EvilPixie - Pixie, does not fortify to places where it already has an advantage
// KillBot - Combines EvilPixie and Vulture behaviour 
// Shaft - Looks ahead and attacks into as few borders as possible
// Quo - Shaft that tries to obtain a card every turn

