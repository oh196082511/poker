package event.impl;

import com.google.gson.Gson;
import enums.CardDecisionTypeEnum;
import enums.EventType;
import event.BaseEvent;
import event.Event;
import lombok.Data;
import test.test;

import java.util.Optional;

@Data
public class ReplyActionEvent extends Event {

    // 内心os
    private String innerOs;

    // 决策
    private String decision;
    // 本次新增筹码数（只有操作类型为CALL、RAxzISE、ALL_IN时才有值）
    private int addBet;

    public void removeInnerOs() {
        this.innerOs = "";
    }

    public ReplyActionEvent() {
    }

    public ReplyActionEvent(BaseEvent baseEvent) {
        super(baseEvent);
    }

    public Optional<CardDecisionTypeEnum> getDecisionEnum() {
        return CardDecisionTypeEnum.findByValue(decision);
    }

    public static void main(String[] args) {
        StartEvent startEvent = test.getStartEvent();
        BaseEvent baseEvent = startEvent.getBaseEvent();
        baseEvent.setType(EventType.ACTION_REPLY.toValue());
        ReplyActionEvent replyActionEvent = new ReplyActionEvent(baseEvent);
        replyActionEvent.setInnerOs("内心毫无波动");
        replyActionEvent.setDecision(CardDecisionTypeEnum.RAISE.getValue());
        replyActionEvent.setAddBet(100);
        System.out.println(new Gson().toJson(replyActionEvent));
    }

}
