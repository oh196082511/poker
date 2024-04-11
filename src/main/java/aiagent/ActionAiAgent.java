package aiagent;

import com.google.gson.Gson;
import data.PlayerGameInfoMemory;
import enums.CardDecisionTypeEnum;
import enums.EventType;
import event.Event;
import event.impl.ReplyActionEvent;
import gpt.GptCall;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 行为AI代理大脑
 */
@Slf4j
public class ActionAiAgent extends AbstractAiAgent {

    private Gson gson = new Gson();

    private int strategyId;

    public ActionAiAgent(AiAgentPlayer agentPlayer, int strategyId) {
        super(agentPlayer);
        this.strategyId = strategyId;
    }

    @Override
    protected Optional<Event> solveEvents(List<Event> events) {
        // 持续不断地事件涌入
        //StringBuilder prompt = getPrompt();
        StringBuilder prompt = new StringBuilder();
        // 拿全局比赛信息
        PlayerGameInfoMemory playerGameInfoMemory = aiAgentPlayer.getPlayerGameInfoMemory();
        prompt.append("当前对局信息: " + gson.toJson(playerGameInfoMemory));
        prompt.append("\n");
        // 全局历史事件
        List<Event> historyEvent = aiAgentPlayer.getRoom().getEventList(0, endPos).stream()
                .filter(event -> event.getBaseEvent() != null)
                .filter(event -> event.getBaseEvent().getType().equals(EventType.ACTION_REPLY.toValue()))
                .collect(Collectors.toList());
        aiAgentPlayer.filter(historyEvent);
        prompt.append("历史发生的信息: " + gson.toJson(historyEvent));
        prompt.append("\n");
        // 离上次处理，又发生的事件
        aiAgentPlayer.filter(events);
        prompt.append("距离上一次思考，又发生的事件: " + gson.toJson(events));
        prompt.append("\n");

        List<Event> selfEvents = historyEvent.stream().filter(event -> event.getIdx() == aiAgentPlayer.getIdx())
                .collect(Collectors.toList());
        prompt.append("我帮你总结出了你自己过去的事件，有决定也有发言: " + gson.toJson(selfEvents));
        prompt.append("\n");
        prompt.append("你后面的思考记得延续以前的思路啊\n");
        prompt.append("我希望你根据现有信息，轮到你做决定了，反馈一个内心活动，出牌决策和加注时的筹码数，必须以ReplyActionEvent的json形式返回，不要少任何一个属性！！只返回json，不能返回一个无法用ReplyActionEvent结构json解析的结果！！！！你返回给我的ReplyActionEvent结构不需要包含baseEvent的信息，只需要含有ReplyActionEvent内部的3个变量innerOs、decision、addBet即可\n");
//        prompt.append("{\n" + "    \"innerOs\":\"我现在牌一般，看起来玩家5有点激动，要不避其锋芒吧，这盘先放牌了\",\n" +
//                "    \"decision\":\"fold\",\n" + "    \"addBet\":0\n" + "}");
        // 拼装给gpt
        String finalPrompt = prompt.toString();
        return Optional.ofNullable(gson.fromJson(GptCall.callGPT(strategyId, finalPrompt), ReplyActionEvent.class));
    }

    @Override
    protected boolean isActionPlayer() {
        return true;
    }

    public static void main(String[] args) {
        String json = "{\"innerOs\":\"看这牌型，狗屎。\",\"decision\":\"放弃\",\"addBet\":0,\"idx\":-1,\"baseEvent\":{\"type\":\"action_reply\",\"currentTurn\":0,\"currentStage\":0,\"totalPot\":0,\"currentIndex\":0,\"finalActionIndex\":0,\"playerIdx\":0}}";
        ReplyActionEvent replyActionEvent1 = new Gson().fromJson(json, ReplyActionEvent.class);
        if (CardDecisionTypeEnum.findByValue(replyActionEvent1.getDecision()).isPresent()) {
            System.out.println("ok");
        }
    }
}