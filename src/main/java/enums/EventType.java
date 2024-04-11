package enums;

import java.util.Objects;
import java.util.Optional;

public enum EventType {

    START("一盘比赛开局了", "start"),
    ACTION("轮到你出牌了", "action"),
    ACTION_REPLY("我下注/放弃之类的", "action_reply"),

    OS_REPLY( "神态os", "os_reply"),

    END("游戏结束", "end");

    private String desc;

    private String value;

    EventType(String desc, String value) {
        this.desc = desc;
        this.value = value;
    }

    public static Optional<EventType> findByValue(String value) {
        for (EventType item : EventType.values()) {
            if (Objects.equals(item.value, value)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public String toValue() {
        return value;
    }
}
