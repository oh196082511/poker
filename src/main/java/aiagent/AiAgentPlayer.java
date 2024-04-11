package aiagent;

import com.google.gson.Gson;
import data.PlayerGameInfoMemory;
import event.BaseEvent;
import event.Event;
import lombok.extern.slf4j.Slf4j;
import room.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个玩家的AI代理
 * 实时接受各类游戏事件
 *
 * 维护
 * (短期记忆)当前游戏的所有信息
 * (长期记忆)随着时间推移，玩家会对曾经的对局总结出一些经验，以及对其它玩家有一定的了解
 *
 * 根据当前游戏信息，以及短期记忆和长期记忆，决定当前的行动
 */
@Slf4j
public class AiAgentPlayer {

    private PlayerGameInfoMemory playerGameInfoMemory = new PlayerGameInfoMemory();

    private Gson gson = new Gson();

    /**
     * 每个ai-agent玩家都有自己的序号
     */
    private int idx;

    /**
     * 每个ai-agent玩家都有自己的并行配置
     */
    private boolean parallel;

    /**
     * 每个ai-agent玩家都有自己的大脑
     * os大脑
     * 行动大脑
     */
    private List<AbstractAiAgent> abstractAiAgents = new ArrayList<>();

    /**
     * 每个玩家都有自己的房间号
     */
    private Room room;

    /**
     * 每个玩家都有自己的名字
     */
    private String name;

    public AiAgentPlayer(String name, boolean parallel, int idx, Room room, int actionStrategyId, int osStrategyId, int endStrategyId) {
        this.parallel = parallel;
        this.room = room;
        if (parallel) {
//            abstractAiAgents.add(new OsAiAgent(this, osStrategyId, endStrategyId));
            abstractAiAgents.add(new ActionAiAgent(this, actionStrategyId));
        } else {
            abstractAiAgents.add(new SimpleAiAgent(this, actionStrategyId, osStrategyId, endStrategyId));
        }
        for (AbstractAiAgent abstractAiAgent : abstractAiAgents) {
            abstractAiAgent.start();
        }
        this.idx = idx;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * 房间发生事件后，将事件通知到每个player
     * 根据player的大脑并行设置，有不同的处理逻辑
     * @param event
     */
    public void onAddEvent(Event event) {
        log.info("aiplayer: {} onEvent: {}", name, gson.toJson(event));
        if (event == null) {
            return;
        }
        // 更新比赛信息
        updateMemory(event);
        if (parallel) {
            // action 待消费队列内是否有action事件
            abstractAiAgents.stream()
                    .filter(abstractAiAgent -> needSolveEventRightNow(event, abstractAiAgent))
                    .forEach(abstractAiAgent -> abstractAiAgent.setEndPos(room.getHistoryEventsSize()));
        } else {
            // 串行
            abstractAiAgents.forEach(abstractAiAgent -> abstractAiAgent.setEndPos(room.getHistoryEventsSize()));
        }
    }

    public void updateMemory(Event event) {
        BaseEvent baseEvent = event.getBaseEvent();
        this.playerGameInfoMemory.setPlayerBOs(baseEvent.getPlayerBOs());
        this.playerGameInfoMemory.setCards(baseEvent.getIdx2cards().get(idx));
        this.playerGameInfoMemory.setCurrentIndex(baseEvent.getCurrentIndex());
        this.playerGameInfoMemory.setCurrentTurn(baseEvent.getCurrentTurn());
        this.playerGameInfoMemory.setCurrentStage(baseEvent.getCurrentStage());
        this.playerGameInfoMemory.setTotalPot(baseEvent.getTotalPot());
        this.playerGameInfoMemory.setDealerCards(baseEvent.getDealerCards());
        this.playerGameInfoMemory.setFinalActionIndex(baseEvent.getFinalActionIndex());
    }

    /**
     * 判断大脑agent是否需要处理事件
     * @param event
     * @param abstractAiAgent
     * @return
     */
    private boolean needSolveEventRightNow(Event event, AbstractAiAgent abstractAiAgent) {
        // 如果是actionPlayer且有action事件，且是自己，不发
        if (!abstractAiAgent.isActionPlayer()) {
            return true;

        }
        // 是action大脑
        boolean isActionEvent = event.isActionEvent();
        if (isActionEvent && event.getIdx() == idx) {
            return true;
        }
        return false;
    }

    public Room getRoom() {
        return room;
    }

    public int getIdx() {
        return idx;
    }

    public PlayerGameInfoMemory getPlayerGameInfoMemory() {
        return playerGameInfoMemory;
    }

    public void filter(List<Event> events) {
        // 把别的内心os置空
        int size = events.size();
        for (int i = 0; i < size; i++) {
            Event event = events.get(i);
            if (event.getIdx() != idx) {
                event.removeInnerOs();
                event.setBaseEvent(null);
            }
        }
    }

}