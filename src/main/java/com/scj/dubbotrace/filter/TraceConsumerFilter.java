package com.scj.dubbotrace.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.scj.dubbotrace.RequestIDGenerator;
import com.scj.dubbotrace.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消费者Filter判断MDC中是否有RequestID,如果有放入上下文，没有，新建
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-09-29 16:02
 */
@Slf4j
@Activate(group = Constants.CONSUMER)
public class TraceConsumerFilter implements Filter{


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String requestId = MDC.get(TraceConstants.RequestId);
        String userIdentify = MDC.get(TraceConstants.UserIdentify);
        //MDC中不存在requestId
        if(requestId==null|| Objects.equals(requestId, "")){
            requestId = String.valueOf(RequestIDGenerator.getInstance().next());
        }
        Map<String, String> attachments = invocation.getAttachments();
        if (attachments != null) {
            attachments = new HashMap<String, String>(attachments);
            attachments.put(TraceConstants.RequestId,requestId);
            if(userIdentify!=null&&userIdentify.length()>0){
                attachments.put(TraceConstants.UserIdentify,userIdentify);
            }
        }
        if (attachments != null) {
            if (RpcContext.getContext().getAttachments() != null) {
                RpcContext.getContext().getAttachments().putAll(attachments);
            } else {
                RpcContext.getContext().setAttachments(attachments);
            }
        }
        return invoker.invoke(invocation);
    }
}
