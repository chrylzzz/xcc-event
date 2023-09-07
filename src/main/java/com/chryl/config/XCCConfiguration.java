package com.chryl.config;

import com.alibaba.fastjson2.JSONObject;
import com.chryl.chryl.ChrylConfigProperty;
import com.chryl.constant.XCCConstants;
import com.chryl.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 配置多台XSwitch,注意服务器多个网卡问题
 * Created by Chr.yl on 2023/5/30.
 *
 * @author Chr.yl
 */
@Slf4j
public class XCCConfiguration {

    public static void xccConfig(ChrylConfigProperty chrylConfigProperty) {
        log.warn("Local HostAddress: [{}] ", IpUtil.INTERNET_IP);
        try {
            //node
            if (chrylConfigProperty.isCluster()) {
                List<JSONObject> natsList = chrylConfigProperty.getNatsList();
                for (JSONObject nodeBean : natsList) {
                    String node = nodeBean.getString(XCCConstants.NODE);
                    if (IpUtil.INTERNET_IP.equals(node)) {
                        String natsUrl = nodeBean.getString(XCCConstants.NATS);
                        chrylConfigProperty.setNatsUrl(natsUrl);
                        log.warn("节点装配完成 node: [{}] , nats: [{}]", node, natsUrl);
                        break;
                    }
                }
            }
            //tts voice
            if (chrylConfigProperty.isTtsVoiceRule()) {
                List<String> ttsVoiceList = chrylConfigProperty.getTtsVoiceList();
                String voiceName;
                if (IpUtil.INTERNET_IP.equals(XCCConstants.IP_201)) {
                    voiceName = ttsVoiceList.get(0);
                } else {
                    voiceName = ttsVoiceList.get(1);
                }
                chrylConfigProperty.setTtsVoice(voiceName);
                log.warn("TTS装配完成 node: [{}] , voice-name: [{}]", IpUtil.INTERNET_IP, voiceName);
            }
            log.info("初始化 CHRYL_CONFIG_PROPERTY 成功");
        } catch (
                Exception e) {
            log.error("初始化 CHRYL_CONFIG_PROPERTY 失败");
            e.printStackTrace();
        }
    }
}
