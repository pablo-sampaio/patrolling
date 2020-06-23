package algorithms.grav_distributed;

import algorithms.grav_distributed.core.ForceCombination;
import algorithms.grav_distributed.core.ForcePropagation;
import algorithms.grav_distributed.core.MassGrowth;
import yaps.graph.Graph;
import yaps.simulator.core.AgentPosition;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.simulator.multiagent.SimpleMultiagentAlgorithm;


public class GravitationalAgentsAlgorithm extends SimpleMultiagentAlgorithm {
	private String name;
	private ForcePropagation propagation;
	private MassGrowth growth;
	private double distExponent;
	private ForceCombination combination;
	
	public GravitationalAgentsAlgorithm(ForcePropagation forceProp,
			MassGrowth growth, double distExponent, ForceCombination comb) {
		super("--manter o padrao de nome--");
//	    String exponentStr = String.format(Locale.US, "%1.2f", distExponent);
//	    this.name = "grav(" + forceProp + "," + growth + "," + exponentStr + "," + comb + ")";
		this.propagation = forceProp;
		this.growth = growth;
		this.distExponent = distExponent;
		this.combination = comb;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void onSimulationEnd() {
		//does nothing
	}

	@Override
	public SimpleAgent[] createTeam(AgentPosition[] positions, Graph g, int time) {
		int agentsNumber = positions.length;
		// instanciar os GravAgent, passando a classe compartilhada para trocar mensagens (se for o caso)
		MessageManager messageManager = new MessageManager(agentsNumber);
		SimpleAgent[] agents = new SimpleAgent[agentsNumber];
		
		for (int i = 0; i < agentsNumber; i++) {
			GravityManager gravManager = new GravityManager(this.propagation, this.growth, this.distExponent, this.combination);
			SimpleAgent simpleAgent = new GravAgent(g, gravManager, messageManager);
			agents[i] = simpleAgent;
		}
		return agents;
	}

}
