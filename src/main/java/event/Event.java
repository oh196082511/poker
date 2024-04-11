package event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    private int idx = -1;    // 哪个序号玩家发起的

    private BaseEvent baseEvent;

    public Event(BaseEvent baseEvent) {
        this.baseEvent = baseEvent;
    }

    public String getRoomKey() {
        return baseEvent.getRoomKey();
    }

    public boolean isActionEvent() {
        return baseEvent.isActionEvent();
    }

    public int getIdx() {
        return idx;
    }

    public void removeInnerOs() {

    }
}
