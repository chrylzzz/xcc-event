package com.chryl.service.impl.event;

import com.chryl.chryl.client.XCCConnection;
import com.chryl.chryl.ivr.DispatcherIVR;
import com.chryl.constant.XCCConstants;
import com.chryl.entry.ChannelEvent;
import com.chryl.entry.IVREvent;
import com.chryl.entry.NGDEvent;
import com.chryl.entry.XCCEvent;
import com.chryl.handler.IVRHandler;
import com.chryl.handler.NGDHandler;
import com.chryl.handler.XCCHandler;
import com.chryl.model.NGDNodeMetaData;
import com.chryl.service.IVRService;
import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * event-V0版本:
 * 基于V6
 *
 * @author Chr.yl
 */
@Slf4j
@Primary
@Component
public class IVREventServiceV0 implements IVRService {

    @Override
    public void handlerChannelEvent(Connection nc, ChannelEvent channelEvent) {

    }


}
