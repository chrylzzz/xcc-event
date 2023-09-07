package com.chryl.handler;

import com.alibaba.fastjson2.JSON;
import com.chryl.boot.IVRInit;
import com.chryl.constant.XCCConstants;
import com.chryl.entry.IVREvent;
import com.chryl.entry.NGDEvent;
import com.chryl.enumerate.EnumXCC;
import com.chryl.model.IVRModel;
import com.chryl.util.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 营销系统业务处理
 * Created by Chr.yl on 2023/7/11.
 *
 * @author Chr.yl
 */
@Slf4j
public class PMSHandler {

    /**
     * 保存来话意图信息
     *
     * @param ivrEvent
     * @param ngdEvent
     */
    public static void saveIntent(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String ivrStartTime = ivrEvent.getIvrStartTime();
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getFsCallerId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        String intent = ngdEvent.getIntent();

        IVRModel ivrModel = new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, ivrStartTime, intent, "", "", "");
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("SaveZnIVRLhytForGx,pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.SAVE_INTENT_URL, jsonParam);
        log.info("SaveZnIVRLhytForGx,pms接口出参:{}", postJson);
    }

    /**
     * 保存通话数据信息
     *
     * @param ivrEvent
     */
    public static void saveCallData(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String ivrStartTime = ivrEvent.getIvrStartTime();
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getFsCallerId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        boolean transferFlag = ivrEvent.isTransferFlag();
        String artificialType, ivrValidCallType, ivrCallEndNormalType;//是否转人工,是否有效通话,是否正常结束: 0否1是

        if (transferFlag) {
            artificialType = "1";
        } else {
            artificialType = "0";
        }
        ivrValidCallType = "1";
        ivrCallEndNormalType = "1";
        new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, ivrStartTime, artificialType, ivrValidCallType, ivrCallEndNormalType);

    }

    /**
     * 保存满意度
     *
     * @param ivrEvent
     * @param ngdEvent
     */
    public static void saveRate(IVREvent ivrEvent, NGDEvent ngdEvent) {
        String cidPhoneNumber = ivrEvent.getCidPhoneNumber();
        String fsCallerId = ivrEvent.getFsCallerId();
        String icdCallerId = ivrEvent.getIcdCallerId();
        String rate = ngdEvent.getRate();
        if (StringUtils.isBlank(rate)) {
            rate = EnumXCC.IVR_RATE_NEUTRAL.getValue();
        }
        IVRModel ivrModel = new IVRModel(cidPhoneNumber, fsCallerId, icdCallerId, "", "", rate);
        String jsonParam = JSON.toJSONString(ivrModel);
        log.info("SAVE_RATE_DATA_URL, pms接口入参:{}", jsonParam);
        String postJson = HttpClientUtil.doPostJson(IVRInit.CHRYL_CONFIG_PROPERTY.getPmsUrl() + XCCConstants.SAVE_RATE_DATA_URL, jsonParam);
        log.info("SAVE_RATE_DATA_URL, pms接口出参:{}", postJson);

    }


}
