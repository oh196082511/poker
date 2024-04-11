package room;

import event.Event;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个房间里有x个玩家，各个房间逻辑互不干扰
 * 每个房间持有一个channel通道，用于玩家客户端向前端发送消息
 * 每个房间持有一个唯一房间号
 *
 */
@Slf4j
public class RoomManager {

    private ConcurrentHashMap<Channel, Room> key2Room = new ConcurrentHashMap<>();

    public void createRoom(Channel channel, int playerIdx, int totalPlayerNum) {
        if (key2Room.containsKey(channel)) {
            log.info("roomKey already exists");
            return;
        }
        key2Room.put(channel, new Room(channel, playerIdx, totalPlayerNum));
    }

    public void onNettyReceiveEvent(Event event, Channel channel) {
        if (event == null) {
            return;
        }
        if (!key2Room.containsKey(channel)) {
            createRoom(channel, event.getBaseEvent().getPlayerIdx(),
                    event.getBaseEvent().getTotalPlayerNum());
        }
        Room room = key2Room.get(channel);
        room.onNettyReceiveEvent(event);
    }

    public void removeRoomByChannel(Channel channel) {
        key2Room.remove(channel);
    }
}
