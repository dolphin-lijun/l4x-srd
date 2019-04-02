package com.ai.aif.log4x.agent.trans.adapter.impl.web;

import com.ai.aif.log4x.agent.deps.javassist.CtMethod;
import com.ai.aif.log4x.agent.trans.adapter.impl.AbsJavassistSwitchAdapter;
import com.ai.aif.log4x.agent.util.WrappedStringBuilder;

public class HttpHandlerServletAdapter extends AbsJavassistSwitchAdapter {
    @Override
    public String[] getInjectedMethodHeaders() {
        return new String[] {
                "public void service(javax.servlet.ServletRequest req, javax.servlet.ServletResponse res)"
        };
    }

    @Override
    public String addBeforeInvoke(CtMethod ctMethod) {
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.message.format.Trace trace = com.ai.aif.log4x.Log4xManager.client().getTrace();");
        buf.appendln("trace.setCallType(\"WEB\");");
        buf.appendln("trace.setServiceName($1.getParameter(\"clazz\"));");
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
