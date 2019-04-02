package com.ai.aif.log4x.agent.trans.adapter.impl.web;

import com.ai.aif.log4x.agent.deps.javassist.CtMethod;
import com.ai.aif.log4x.agent.trans.adapter.impl.AbsJavassistSwitchAdapter;
import com.ai.aif.log4x.agent.util.WrappedStringBuilder;

public class PageServletAdapter extends AbsJavassistSwitchAdapter {
    @Override
    public String[] getInjectedMethodHeaders() {
        return new String[] {
                "protected void doService(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)"
        };
    }

    @Override
    public String addBeforeInvoke(CtMethod ctMethod) {
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.message.format.Trace trace = com.ai.aif.log4x.Log4xManager.client().getTrace();");
        buf.appendln("trace.setCallType(\"WEB\");");
        buf.appendln("String referer = $1.getHeader(\"Referer\");");
        buf.appendln("if(referer != null){");
        buf.appendln("int startIndex = 0;");
        buf.appendln("if(referer.startsWith(\"http\") && referer.indexOf(\"/\", 7) > -1)");
        buf.appendln("{");
        buf.appendln("  startIndex = referer.indexOf(\"/\", 7) + 1;");
        buf.appendln("}");
        buf.appendln("int endIndex = referer.length();");
        buf.appendln("if(referer.indexOf(\"listener\") > -1 && referer.indexOf(\"&\", referer.indexOf(\"listener\")) > -1)");
        buf.appendln("{");
        buf.appendln("  endIndex = referer.indexOf(\"&\", referer.indexOf(\"listener\"));");
        buf.appendln("}");
        buf.appendln("if(referer.indexOf(\"listener\") < 0 && referer.indexOf(\"&\") > -1)");
        buf.appendln("{");
        buf.appendln("  endIndex = referer.indexOf(\"&\");");
        buf.appendln("}");
        buf.appendln("trace.setServiceName(referer.substring(startIndex, endIndex));");
        buf.appendln("}");
        buf.appendln("else {trace.setServiceName($1.getRequestURI());}");
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
