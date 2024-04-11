package aiagent;

import event.Event;

import java.util.List;
import java.util.Optional;

public class SimpleAiAgent extends AbstractAiAgent {

    /**
     * 单独大脑同时拥有os和action两个事件
     */
    private OsAiAgent osAgentPlayer;

    private ActionAiAgent actionAgent;

    @Override
    protected boolean isActionPlayer() {
        return false;
    }

    @Override
    protected Optional<Event> solveEvents(List<Event> events) {
        // 包含action，就调用action代码，否则调用os代码
        if (containsAction(events)) {
            return actionAgent.solveEvents(events);
        }
        return osAgentPlayer.solveEvents(events);
    }

    public SimpleAiAgent(AiAgentPlayer agentPlayer, int actionStrategyId, int osStrategyId, int endStrategyId) {
        super(agentPlayer);
        this.osAgentPlayer = new OsAiAgent(agentPlayer, osStrategyId, endStrategyId);
        this.actionAgent = new ActionAiAgent(agentPlayer, actionStrategyId);
    }

    private boolean containsAction(List<Event> events) {
        Optional<Event> actionEvent = events.stream().filter(Event::isActionEvent).findAny();
        return actionEvent.filter(event -> event.getIdx() == aiAgentPlayer.getIdx()).isPresent();
    }
}
