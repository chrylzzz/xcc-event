package com.chryl.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.chryl.boot.IVRInit;
import com.chryl.entry.ChannelEvent;
import com.chryl.service.IVRService;
import com.chryl.util.XCCUtil;
import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * IVR Control
 * Created By Chr.yl on 2023-02-08.
 *
 * @author Chr.yl
 */
@Slf4j
@Component
public class IVRControllerBak {

    @Autowired
    private IVRService ivrService;

    /**
     * waiting for incoming call
     */
    public void domain() {
        try {
            //获取nats连接
            Connection nc = Nats.connect(IVRInit.CHRYL_CONFIG_PROPERTY.getNatsUrl());
            //从nats获取订阅主题
            Subscription sub = nc.subscribe(IVRInit.CHRYL_CONFIG_PROPERTY.getXctrlSubject());
            log.info("Ivr Controller started");
            JSONArray jsonArray = new JSONArray();
            while (true) {
                //订阅消息
                Message subMsg = sub.nextMessage(Duration.ofMillis(50000));
                if (subMsg == null) {
                    log.warn(" this subMsg is null ");
                } else {

                    //订阅事件
                    //XNode收到呼叫后，向NATS广播来话消息（Event.Channel（state = START）），Ctrl收到后进行处理。
                    String eventStr = new String(subMsg.getData(), StandardCharsets.UTF_8);
                    JSONObject eventJson = JSONObject.parseObject(eventStr);

                    //log.info("订阅事件 eventJson:{}", eventJson);

                    //event状态:Event.Channel（state=START）
                    String method = eventJson.getString("method");
                    log.warn("xcc method: {}", method);
                    /*
                    if (XCCConstants.EVENT_CHANNEL.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "Channel");
                        JSONObject params = eventJson.getJSONObject("params");
                        //convert param
                        ChannelEvent event = IVRHandler.convertParams(params);
                        //asr domain
                        ivrService.handlerChannelEvent(nc, event);

                    } else if (XCCConstants.EVENT_DETECTED_FACE.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "DetectedFace");
                    } else if (XCCConstants.EVENT_NATIVE_EVENT.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "NativeEvent");
                        JSONObject params = eventJson.getJSONObject("params");
                        //channel_state
                        //cs_new->cs_init->cs_routing->cs_execute

                        String nodeUuid = params.getString("node_uuid");
                        JSONObject event = params.getJSONObject("event");
                        String eventName = event.getString("Event-Name");
                        String channelId = event.getString("Channel-Call-UUID");
                        String channelState = event.getString("Channel-State");

                        //asr domain
                        ChannelEvent aevent = IVRHandler.convertParams(params);
                        ivrService.handlerChannelEvent(nc, aevent);

                        if (XCCConstants.DETECTED_SPEECH.equals(eventName)) {
                            log.info("===DETECTED_SPEECH===");
                        }
                    } else if (XCCConstants.EVENT_DETECTED_SPEECH.equals(method)) {
                        log.info("事件 event======Event-Name : {}", "DetectedSpeech");
                    }
                    */


                    JSONObject params = eventJson.getJSONObject("params");

                    String nodeUuid = params.getString("node_uuid");
                    JSONObject event = params.getJSONObject("event");
                    String eventName = event.getString("Event-Name");
                    String channelId = event.getString("Channel-Call-UUID");
                    String channelState = event.getString("Channel-State");
                    String answerState = event.getString("Answer-State");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("method", method);
                    String state = params.getString("state");
                    jsonObject.put("state", state);
                    jsonObject.put("nodeUuid", nodeUuid);
                    jsonObject.put("eventName", eventName);
                    jsonObject.put("channelId", channelId);
                    jsonObject.put("channelState", channelState);
                    jsonObject.put("answerState", answerState);
                    jsonArray.add(jsonObject);
                    log.warn("jsonArr:{}", jsonArray);


                    if ("cs_new".equals(answerState)) {
                        //asr domain
                        ChannelEvent aevent = new ChannelEvent();
                        aevent.setNodeUuid(nodeUuid);
                        aevent.setUuid(channelId);
                        XCCUtil.answer(nc, aevent);
                    }

                }


            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("IVRController 发生异常：{}", e);
        }
    }

}

// we have to serialize the params into a string and parse it again
// unless we can find a way to convert JsonElement to protobuf class
//                        xctrl.Xctrl.ChannelEvent.Builder cevent = xctrl.Xctrl.ChannelEvent.newBuilder();
//                        JsonFormat.parser().ignoringUnknownFields().merge(params.toString(), cevent);
//                        log.info("订阅事件 cevent======:{}", cevent);
