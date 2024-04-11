package event.impl;

import com.google.gson.Gson;
import event.BaseEvent;
import event.Event;
import lombok.Data;
import test.test;

@Data
public class StartEvent extends Event {

    public StartEvent() {
    }

    public StartEvent(BaseEvent baseEvent) {
        super(baseEvent);
    }

    public static void main(String[] args) {
        StartEvent startEvent = test.getStartEvent();
        System.out.println(new Gson().toJson(startEvent));
    }
}
