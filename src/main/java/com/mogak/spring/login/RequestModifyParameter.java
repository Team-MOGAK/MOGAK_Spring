package com.mogak.spring.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestModifyParameter extends HttpServletRequestWrapper {

    Map<String, String[]> params;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public RequestModifyParameter(HttpServletRequest request) {
        super(request);
        this.params = new HashMap(request.getParameterMap());
    }

    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);

        if (paramArray != null && paramArray.length > 0) {
            return paramArray[0];
        } else {
            return null;
        }
    }

//    @Override
//    public Map<String, String[]> getParameterMap() {
//        return Collections.unmodifiableMap(params);
//    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {

        String[] result = null;
        String[] temp = params.get(name);

        if (temp != null) {
            result = new String[temp.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
        }

        return result;
    }

    public void setParameter(String name, String value) {
        String[] oneParam = {value};
        setParameter(name, oneParam);
    }

    public void setParameter(String name, String[] value) {
        params.put(name, value);
    }

//    public void setParameter(String name, String[] values) {
//        params.put(name, values);
//    }

    @Override
    public String toString() {
        return "RequestModifyParameter{" +
                "params=" + params +
                '}';
    }
}
