package com.ai.aif.log4x.agent.trans.adapter.impl.aee;

import com.ai.aif.log4x.agent.deps.javassist.CtMethod;
import com.ai.aif.log4x.agent.trans.adapter.impl.AbsJavassistSwitchAdapter;
import com.ai.aif.log4x.agent.util.WrappedStringBuilder;

public class BiItemArchSVCAdapter  extends AbsJavassistSwitchAdapter
{
    @Override
    public String addAfterInvoke(CtMethod arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().finishTrace(true);");
        return buf.toString();
    }

    @Override
    public String addBeforeInvoke(CtMethod ctMethod) {
        // TODO Auto-generated method stub

        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("com.ai.aif.log4x.message.format.Trace trace = com.ai.aif.log4x.Log4xManager.client().getTrace();");
        buf.appendln("trace.setServiceName($0.getClass().getName() + \"." + ctMethod.getName() + "\");");
        buf.appendln("trace.setCallType(\"AEE\");");
        buf.appendln("java.util.Map<String, String> extdata = com.ai.apaas.common.service.impl.bidao.BiDao.qryBiExtendByIntId(connNameJour, intactId, yyyymm);");
        buf.appendln("trace.setTraceId(extdata.get(\"TRACE_ID\"));");
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().fillSqlParam(trace);");
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().startTrace(trace);");
        return buf.toString();
    }

    @Override
    public String addInExceptionCatch(CtMethod method, String exName, String vName) {
        // TODO Auto-generated method stub
        WrappedStringBuilder buf = new WrappedStringBuilder();
        buf.appendln("trace.setThrowable(" + vName + ");");
        buf.appendln("com.ai.aif.log4x.Log4xManager.client().finishTrace(false);");
        return buf.toString();
    }

    @Override
    public String[] getInjectedMethodHeaders() {
        // TODO Auto-generated method stub
        return new String[] { "public void doService()" };
    }
}
