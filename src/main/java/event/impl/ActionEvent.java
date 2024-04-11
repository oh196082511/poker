package event.impl;

import event.BaseEvent;
import event.Event;
import lombok.Data;

@Data
public class ActionEvent extends Event {

    public ActionEvent(int idx, BaseEvent baseEvent) {
        super(idx, baseEvent);
    }

}
