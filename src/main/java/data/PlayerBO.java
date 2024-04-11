package data;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yaoyayao
 */
@Data
@AllArgsConstructor
public class PlayerBO {
    private int idx;//座位排序
    private int pod;//拥有筹码
    private int sendPod; //已出筹码
}