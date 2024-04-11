package aiagent;

import com.google.gson.Gson;
import data.PlayerGameInfoMemory;
import enums.EventType;
import event.BaseEvent;
import event.Event;
import event.impl.ReplyOsEvent;
import gpt.GptCall;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class OsAiAgent extends AbstractAiAgent {


    private Gson gson = new Gson();

    private int strategyId;

    /**
     * 结束讨论策略
     */
    private int endStrategyId;

    public OsAiAgent(AiAgentPlayer agentPlayer, int strategyId, int endStrategyId) {
        super(agentPlayer);
        this.strategyId = strategyId;
        this.endStrategyId = endStrategyId;
    }


    @Override
    public Optional<Event> solveEvents(List<Event> events) {
        if (new Random().nextInt(1000) < 900) {
            log.info("random, 放弃此次发言;");
            return Optional.empty();
        }
        // prompt策略 os降低速率
        try {
            Thread.sleep(20000 + new Random().nextInt(10000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 持续不断地事件涌入
        //StringBuilder prompt = getPrompt();
        StringBuilder prompt = new StringBuilder();

        // 拿全局比赛信息
        PlayerGameInfoMemory playerGameInfoMemory = aiAgentPlayer.getPlayerGameInfoMemory();
        prompt.append("当前对局信息: " + gson.toJson(playerGameInfoMemory));
        prompt.append("\n");
        // 全局历史事件
        List<Event> historyEvent = aiAgentPlayer.getRoom().getEventList(0, endPos);
        aiAgentPlayer.filter(historyEvent);
        prompt.append("历史发生的信息: " + gson.toJson(historyEvent));
        prompt.append("\n");
        // 离上次处理，又发生的事件
        aiAgentPlayer.filter(events);
        prompt.append("距离上一次思考，又发生的事件: " + gson.toJson(events));
        prompt.append("\n");

        prompt.append("我希望你根据现有信息，反馈一个包含你心里活动和思考的内容os、一个能表达心里状态或者假装欺骗牌友的发言，必须以ReplyOsEvent的json形式返回，只返回json，切记！！不能返回一个无法用ReplyOsEvent结构json解析的结果！！而且你返回给我的ReplyOsEvent结构不需要包含baseEvent的信息，只需要含有ReplyOsEvent内部的2个变量expression、innerOs即可。\n");
        // 拼装给gpt
        String finalPrompt = prompt.toString();
        String s;
        if (!aiAgentPlayer.getRoom().isPlaying()) {
            log.info("开始讨论比赛");
            s = GptCall.callGPT(endStrategyId, finalPrompt);
        } else {
            log.info("开始比赛中发言");
            s = GptCall.callGPT(strategyId, finalPrompt);
        }
        log.info("strategyId:{} player: {} os-agent response: {}", strategyId, aiAgentPlayer.getName(), s);
        try {
            ReplyOsEvent replyOsEvent = gson.fromJson(s, ReplyOsEvent.class);
            replyOsEvent.setBaseEvent(new BaseEvent());
            replyOsEvent.getBaseEvent().setType(EventType.OS_REPLY.toValue());
            return Optional.of(replyOsEvent);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }

    }


    @Override
    protected boolean isActionPlayer() {
        return false;
    }
}