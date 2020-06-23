package algorithms.grav_distributed;

import java.util.List;
import yaps.graph.Graph;
import yaps.simulator.multiagent.SimpleAgent;
import yaps.util.IdlenessManager;

public class GravAgent extends SimpleAgent {
	private IdlenessManager nodeIdlenesses;
	private MessageManager messageManager;
	private GravityManager gravityManager;
	
	GravAgent(Graph g, GravityManager gravityManager, MessageManager messageManager) {
		this.nodeIdlenesses = new IdlenessManager();
		this.nodeIdlenesses.setup(g);
		
		this.gravityManager = gravityManager;
		this.gravityManager.setup(g);
		
		this.messageManager = messageManager;
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onTurn(int nextTurn) {
		//checar mensagens ---> classe compartilhada ou singleton para trocar mensagens --> cada agente lê suas mensagens do turno atual
		List<AgentMessage> messages = this.messageManager.checkMessages(this.identifier);
		
		for (AgentMessage message : messages) {
			if (message.messageType == AgentMessage.NODE_ARRIVAL) {
				//setar no -idleness manager quando um (este ou outro) agente chegar no nó
				this.nodeIdlenesses.updateForAgent(nextTurn, message.node);
				//setar no NOVO grav-manager quando um agente informar que chegou no nó
				this.gravityManager.removeScheduledVisit(message.node);
			}
			else if (message.messageType == AgentMessage.VISIT_SCHEDULE) {
				//setar no NOVO grav-manager quando um agente informar que está indo para um nó x
				this.gravityManager.addScheduledVisit(message.node);
				
			}
		}	
	}

	@Override
	public int onArrivalInNode(int nextTurn) {
		int currentNode = this.position.getCurrentNode();
		
		//setar no -idleness manager quando este agente chegar no nó
		this.nodeIdlenesses.updateForAgent(nextTurn, currentNode);
		//setar no NOVO grav-manager quando um agente informar que chegou no nó
		this.gravityManager.removeScheduledVisit(currentNode);
		
		//novo gravity-manager --> recalcular as gravidades (atraindo dos N outros nós para este, somente)
		this.gravityManager.updateMasses(this.nodeIdlenesses);
		
		//escolher a aresta com maior força resultante
		int targetNode = this.gravityManager.selectGoalNode(this.identifier, currentNode);
		
		//mandar mensagem para todos os outros
		messageManager.broadcastNodeArrival(this.identifier, currentNode, nextTurn);
		messageManager.broadcastVisitIntent(this.identifier, targetNode, nextTurn);
		
		//setar no NOVO grav-manager a intenção deste agente ir para targetNode
		this.gravityManager.addScheduledVisit(targetNode);
		return targetNode;
	}

}
