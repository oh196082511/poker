package enums;

import room.Room;

import java.util.Objects;
import java.util.Optional;

/**
 * @author yaoyayao
 * 出牌决策类型
 */
public enum CardDecisionTypeEnum {
    FOLD("弃牌", "fold"),
    CALL("跟注", "call"),
    RAISE("加注", "raise"),
    CHECK("过牌", "check"),
    ALL_IN("全押", "allin");

    private final String desc;

    private final String value;

    CardDecisionTypeEnum(String desc,String value) {
        this.value = value;
        this.desc = desc;
    }

    public static Optional<CardDecisionTypeEnum> findByValue(String value) {
        String finalValue = value.toLowerCase();
        for (CardDecisionTypeEnum item : CardDecisionTypeEnum.values()) {
            if (Objects.equals(item.value, finalValue)) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public String getValue() {
        return value;
    }


}