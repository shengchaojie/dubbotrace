package com.scj.dubbotrace.filter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.scj.dubbotrace.RequestIDGenerator;
import com.scj.dubbotrace.TraceConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 如果有RequestId从context带过来，那么把RequestId设置到MDC中去
 * 如果不存在，创建一个新的
 * @author 10064749
 * @description ${DESCRIPTION}
 * @create 2018-09-29 16:03
 */
@Slf4j
@Activate(group = Constants.PROVIDER)
public class TraceProviderFilter implements Filter{
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> attachments = invocation.getAttachments();
        String requestId = null;
        String userIdentify = null;
        if(attachments != null){
            requestId = attachments.get(TraceConstants.RequestId);
            userIdentify = attachments.get(TraceConstants.UserIdentify);
        }
        if(userIdentify!=null){
            MDC.put(TraceConstants.UserIdentify,userIdentify);
        }
        if(requestId==null){
            requestId = String.valueOf(RequestIDGenerator.getInstance().next());
        }
        MDC.put(TraceConstants.RequestId,requestId);
        return invoker.invoke(invocation);
    }
}
