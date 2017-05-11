package io.dearas.entity;

/**
 * Created by tnp on 04/05/2017.
 */
public class RpcResponse {
    private Throwable error;    //异常
    private Object result;      //结果

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
