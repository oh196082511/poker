package data;

import lombok.Data;

import java.util.List;

@Data
public class PlayerGameInfoMemory {

    private int currentTurn; //当前第几局
    private int currentStage; //当前第几轮
    private int totalPot; //奖金池筹码数
    private List<String> dealerCards; //庄家牌
    private int currentIndex; //轮到谁出牌的序号
    private int finalActionIndex; //最终行动人的序号
    private List<String> cards; //手里的牌
    private List<PlayerBO> playerBOs; //玩家信息

}
