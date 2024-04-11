package event.impl;

import com.google.gson.Gson;
import enums.EventType;
import event.BaseEvent;
import event.Event;
import lombok.Data;
import test.test;

@Data
public class ReplyOsEvent extends Event {

    // 发言
    private String expression;
    // 内心os
    private String innerOs;

    @Override
    public void removeInnerOs() {
        this.innerOs = "";
    }

    public ReplyOsEvent() {
    }

    public ReplyOsEvent(BaseEvent baseEvent) {
        super(baseEvent);
    }

    public static void main(String[] args) {
        StartEvent startEvent = test.getStartEvent();
        BaseEvent baseEvent = startEvent.getBaseEvent();
        baseEvent.setType(EventType.OS_REPLY.toValue());
        ReplyOsEvent replyOsEvent = new ReplyOsEvent(baseEvent);
        replyOsEvent.setInnerOs("内心毫无波动");
        replyOsEvent.setExpression("我这牌太烂了");
        System.out.println(new Gson().toJson(replyOsEvent));
    }
}
