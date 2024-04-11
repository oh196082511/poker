package event.impl;

import com.google.gson.Gson;
import data.PlayerBO;
import enums.EventType;
import event.BaseEvent;
import event.Event;
import lombok.Data;
import test.test;

import java.util.List;

@Data
public class EndEvent extends Event {

    public EndEvent() {
    }

    public EndEvent(BaseEvent baseEvent) {
        super(baseEvent);
    }

    public static void main(String[] args) {
        StartEvent startEvent = test.getStartEvent();
        BaseEvent baseEvent = startEvent.getBaseEvent();
        baseEvent.setCurrentIndex(5);
        baseEvent.setCurrentStage(10);
        baseEvent.setTotalPot(13000);
        List<PlayerBO> playerBOs = baseEvent.getPlayerBOs();
        playerBOs.get(0).setSendPod(500);
        playerBOs.get(1).setSendPod(1500);
        playerBOs.get(2).setSendPod(300);
        playerBOs.get(3).setSendPod(700);
        playerBOs.get(4).setSendPod(10000);
        baseEvent.setPlayerBOs(playerBOs);
        baseEvent.setType(EventType.END.toValue());
        EndEvent endEvent = new EndEvent(baseEvent);
        System.out.println(new Gson().toJson(endEvent));

    }

}