package cn.patterncat.qrcode.core.bean;

import lombok.Builder;
import lombok.Data;

/**
 * Created by patterncat on 2017-10-28.
 */
@Data
@Builder
public class DetectInfo {

    DetectPositionType positionType;

    InOutType inOutType;
}
