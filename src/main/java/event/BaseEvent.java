package event;

import data.PlayerBO;
import enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEvent {

    private String type; //事件类型

    private String roomKey; // 房间唯一标识

    private int currentTurn; //当前第几局
    private int currentStage; //当前第几轮
    private int totalPot; //奖金池筹码数
    private List<String> dealerCards; //庄家牌
    private int currentIndex; //轮到谁出牌的序号
    private int finalActionIndex; //最终行动人的序号
    private Map<Integer, List<String>> idx2cards; //手里的牌
    private List<PlayerBO> playerBOs; //玩家信息
    private int playerIdx;  // 真实玩家序号

    public int getTotalPlayerNum() {
        return playerBOs.size();
    }

    public boolean isActionEvent() {
        Optional<EventType> eventType = getEventType();
        if (!eventType.isPresent()) {
            return false;
        }
        return eventType.get().equals(EventType.ACTION);
    }

    public Optional<EventType> getEventType() {
        return EventType.findByValue(type);
    }
}
