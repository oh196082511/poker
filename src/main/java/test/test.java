package test;

import data.PlayerBO;
import enums.EventType;
import event.BaseEvent;
import event.impl.ActionEvent;
import event.impl.EndEvent;
import event.impl.StartEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    public static void main(String[] args) {

    }

    public static ActionEvent getActionEvent() {
        int idx = 4;
        StartEvent startEvent = getStartEvent();
        BaseEvent baseEvent = startEvent.getBaseEvent();
        baseEvent.setType(EventType.ACTION.toValue());
        return new ActionEvent(idx, baseEvent);
    }

    public static EndEvent getEndEvent() {
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
        //System.out.println(new Gson().toJson(endEvent));
        return endEvent;
    }

    public static StartEvent getStartEvent() {
        int idx = 1;
        Map<Integer, List<String>> idx2cards = new HashMap<>();
        idx2cards.put(0, Arrays.asList("红心A", "黑桃2"));
        idx2cards.put(1, Arrays.asList("红心2", "黑桃3"));
        idx2cards.put(2, Arrays.asList("红心3", "黑桃4"));
        idx2cards.put(3, Arrays.asList("红心4", "黑桃5"));
        idx2cards.put(4, Arrays.asList("红心5", "黑桃6"));
        List<PlayerBO> playerBOS = new ArrayList<>();
        playerBOS.add(new PlayerBO(0, 1000, 0));
        playerBOS.add(new PlayerBO(1, 1000, 0));
        playerBOS.add(new PlayerBO(2, 1000, 0));
        playerBOS.add(new PlayerBO(3, 1000, 0));
        playerBOS.add(new PlayerBO(4, 1000, 0));


        BaseEvent baseEvent =
                new BaseEvent(EventType.START.toValue(), "asfd", 0, 0, 0, Arrays.asList("红心K", "黑桃A", "红心3"), idx, idx,
                        idx2cards, playerBOS, 3);
        return new StartEvent(baseEvent);
    }


//    public static StartEvent getActionEvent() {
//        int idx = 3;
//        BaseEvent baseEvent =
//                new BaseEvent(EventType.ACTION, "asfd", 1, 1, 1000, Arrays.asList("红心K", "黑桃A", "红心3"), idx, idx,
//                        Arrays.asList("红心K", "黑桃A"), Arrays.asList(new PlayerBO(1, 1000, 1000, 1)));
//        return new ActionEvent(idx, baseEvent);
//    }

}