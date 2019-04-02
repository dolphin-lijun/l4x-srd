/*
 * Copyright (c) 1993-2018. Asiainfo.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Asiainfo Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Asiainfo.com.
 */
package com.ai.aif.log4x.agent.trans.adapter.impl.srd.wade;

import com.ai.aif.log4x.agent.deps.javassist.CtMethod;
import com.ai.aif.log4x.agent.trans.adapter.impl.AbsJavassistSwitchAdapter;
import com.ai.aif.log4x.agent.util.WrappedStringBuilder;

/**
 * 添加 wade-cache memcached TextClient 埋点
 *
 * @auther steven.zhou
 */
public class TextClientAdapter extends AbsJavassistSwitchAdapter {

    @Override
    public String[] getInjectedMethodHeaders() {

        return new String[] {
                "public Object get(String cacheKey)",
                "private boolean set(String bizCacheKey, Object value, int secTTL, int flag)"
        };

    }

    @Override
    public String addBeforeInvoke(CtMethod ctMethod) {


        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.message.format.Trace trace = com.ai.aif.log4x.Log4xManager.client().getTrace();");
        buf.appendln("trace.setCallType(\"MEMCACHE\");");
        buf.appendln("trace.setServiceName($0.getClass().getName() + \"." + ctMethod.getName() + "\");");
        buf.appendln("trace.setProtocol(\"SOCKET\");");
        buf.appendln("trace.setReqBody(com.ai.aif.log4x.util.Strings.join($1));");
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().startTrace(trace);");
        return buf.toString();
    }

    @Override
    public String addAfterInvoke(CtMethod ctMethod, String retType, String retName) {
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().finishTrace(true);");
        return buf.toString();
    }

    @Override
    public String addInExceptionCatch(CtMethod ctMethod, String exName, String exValue) {
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("trace.setRetCode(\"-1\");");
        buf.appendln("trace.setRetMsg(\"failed\");");
        buf.appendln("trace.setThrowable(" + exValue + ");");
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().finishTrace(false);");
        return buf.toString();
    }

}
