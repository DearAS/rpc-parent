package io.dearas.entity;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcRequest {
    // 要请求的接口名
    private String interfaceName;
    // 调用的方法名
    private String methodName;
    // 参数类型
    private Class<?>[] parameterTypes;
    // 参数
    private Object[] parameters;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
