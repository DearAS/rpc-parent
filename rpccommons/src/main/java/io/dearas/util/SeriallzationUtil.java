package io.dearas.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tnp on 04/05/2017.
 */
public class SeriallzationUtil {
    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
    /**
     * 序列化对象 使用protostuff
     * @param obj
     * @return
     */
    public static <T> byte[] seriallzate(T obj){
        // 获取class对象
        Class<T> clazz =  (Class<T>)obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(clazz);

            byte[] bytes = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return bytes;
        }catch (Exception err){
            err.printStackTrace();
            return null;
        }finally {
            buffer.clear();
        }
    }
    private static Objenesis objenesis = new ObjenesisStd(true);

    /**
     * 反序列化protostuff
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] bytes,Class<T> clazz){
        T t = objenesis.newInstance(clazz);
        Schema<T> schema = getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(bytes,t,schema);
        return t;
    }

    private static <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> schema = (Schema<T>)cachedSchema.get(clazz);
        if(schema==null){
            schema = RuntimeSchema.createFrom(clazz);
            if(schema!=null){
                cachedSchema.put(clazz,schema);
            }
        }
        return schema;
    }
}
