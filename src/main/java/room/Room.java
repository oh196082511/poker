package room;

import aiagent.AiAgentPlayer;
import com.google.gson.Gson;
import enums.EventType;
import event.Event;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class Room {

    /**
     * 房间是否正在游戏中
     */
    private boolean playing;

    /**
     * 可供选择的aiagent策略
     * 每个元素代表一个aiagent的策略
     * 每个策略的三个id分别代表：action的策略id和比赛中os的策略id还有赛后策略id
     * 例如：{1,2}代表action的策略id为1，os的策略id为2,
     */
    private int[][] strategy = new int[][]{{61186, 61146, 61200}, {61187, 61188, 61201}, {61190, 61189, 61202}, {61192, 61191, 61203}};

    /**
     * 真实玩家的序号
     */
    private int realPlayerIdx;

    private Gson gson = new Gson();

    /**
     * 房间拥有所有历史事件，有序排列
     */
    private CopyOnWriteArrayList<Event> historyEvents = new CopyOnWriteArrayList<>(); // 全局所有数据

    /**
     * 房间拥有所有Ai-Agent的玩家信息
     */
    private List<AiAgentPlayer> aiAgentPlayerProxies = new ArrayList<>();

    /**
     * 房间拥有独有的channel通信
     */
    private Channel channel;

    public Room(Channel channel, int playerIdx, int totalPlayerNum) {
        this.realPlayerIdx = playerIdx;
        this.channel = channel;
        for (int i = 0, pos = 0; i < totalPlayerNum; i++) {
            if (i != playerIdx) {
                int[] strategy = this.strategy[pos % this.strategy.length];
                pos++;
                aiAgentPlayerProxies.add(new AiAgentPlayer("玩家" + i, true, i, this, strategy[0], strategy[1], strategy[2]));
            }
        }
    }

    public void onNettyReceiveEvent(Event event) {
        if (event == null) {
            return;
        }
        this.historyEvents.add(event);
        if (event.getBaseEvent().getType().equals(EventType.END.toValue())) {
            log.info("game end");
            playing = false;
        } else if (event.getBaseEvent().getType().equals(EventType.START.toValue())) {
            log.info("game start");
            playing = true;
        }
        aiAgentPlayerProxies.forEach(aiAgentPlayer -> aiAgentPlayer.onAddEvent(event));
    }

    public int getHistoryEventsSize() {
        return this.historyEvents.size();
    }

    /**
     * room对应channel，所有事件通过room发送给前端
     * 发牌员逻辑在前端事件
     * @param agentPlayer
     * @param event
     */
    public void sendEvent(AiAgentPlayer agentPlayer, Event event) {
        event.setIdx(agentPlayer.getIdx());
        String msg = gson.toJson(event);
        log.info("player: {}, send type : {} event: {}", agentPlayer.getName(), event.getBaseEvent().getType(), msg);
        channel.writeAndFlush(new TextWebSocketFrame(msg));
    }

    public List<Event> getEventList(int startPos, int endPos) {
        List<Event> events = new ArrayList<>();
        for (int i = startPos; i < endPos; i++) {
            events.add(historyEvents.get(i));
        }
        return events;
    }

    public boolean isPlaying() {
        return playing;
    }

}