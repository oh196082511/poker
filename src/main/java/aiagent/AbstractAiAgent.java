package aiagent;

import event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;


@Slf4j
public abstract class AbstractAiAgent extends Thread {

    /**
     * 每个ai-agent都属于一个player
     */
    protected AiAgentPlayer aiAgentPlayer;

    /**
     * 每个ai-agent都有自己的历史事件消费终点
     * 通过上层控制，永远不能消费超过endPos的事件
     */
    protected volatile int endPos;

    /**
     * 当前消费位置
     */
    private int currentPos;

    public AbstractAiAgent(AiAgentPlayer agentPlayer) {
        this.aiAgentPlayer = agentPlayer;
    }

    /**
     * 设置消费位置
     * @param endPos
     */
    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    /**
     * 如果还没到消费终点，则拉取数据，交给gpt大脑处理
     * 通过room发送结果给前端(规则中心)
     */
    @Override
    public void run() {
        while (true) {
            while (currentPos != endPos) {
                log.info("player: {} currentPos: {} endPos : {} , begin to think", aiAgentPlayer.getName(), currentPos, endPos);
                List<Event> events = this.aiAgentPlayer.getRoom().getEventList(currentPos, endPos);
                Optional<Event> eventOp = solveEvents(events);
                if (eventOp.isPresent()) {
                    Event event = eventOp.get();
                    aiAgentPlayer.getRoom().sendEvent(aiAgentPlayer, event);
                }
                currentPos = endPos;
            }
        }
    }

    /**
     * 对接GPT的核心逻辑
     * @param events
     * @return
     */
    protected abstract Optional<Event> solveEvents(List<Event> events);

    /**
     * 是否是action player
     * @return
     */
    protected abstract boolean isActionPlayer();


}
