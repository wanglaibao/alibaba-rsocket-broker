package com.alibaba.rsocket.invocation;

import com.alibaba.rsocket.metadata.RSocketMimeType;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.rsocket.frame.FrameType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * reactive method metadata for service interface
 *
 * @author leijuan
 */
public class ReactiveMethodMetadata {
    private String classFullName;
    private String name;
    private int parameterCount;
    /**
     * rsocket frame type
     */
    private FrameType rsocketFrameType;
    /**
     * param encoding
     */
    private RSocketMimeType paramEncoding;
    /**
     * result type
     */
    private Class<?> returnType;
    /**
     * inferred class for result type
     */
    private Class<?> inferredClassForResult;

    public ReactiveMethodMetadata(Method method, RSocketMimeType defaultEncoding) {
        this.classFullName = method.getDeclaringClass().getCanonicalName();
        this.name = method.getName();
        //result type & generic type
        this.returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType != null) {
            //http://tutorials.jenkov.com/java-reflection/generics.html
            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericReturnType;
                Type[] typeArguments = type.getActualTypeArguments();
                if (typeArguments.length > 0) {
                    final Type typeArgument = typeArguments[0];
                    if (typeArgument instanceof ParameterizedType) {
                        this.inferredClassForResult = (Class<?>) ((ParameterizedType) typeArgument).getActualTypeArguments()[0];
                    } else {
                        this.inferredClassForResult = (Class<?>) typeArgument;
                    }
                }
            }
            if (this.inferredClassForResult == null) {
                this.inferredClassForResult = (Class<?>) genericReturnType;
            }
        }
        //parameter
        this.parameterCount = method.getParameterCount();
        if (parameterCount == 1) {
            paramEncoding = defaultEncoding;
        }
        if (paramEncoding == null) {
            paramEncoding = defaultEncoding;
        }
        //bi direction check: type is Flux for 1st param or type is flux for 2nd param
        if (parameterCount == 1 && method.getParameterTypes()[0].equals(Flux.class)) {
            rsocketFrameType = FrameType.REQUEST_CHANNEL;
        } else if (parameterCount == 2 && method.getParameterTypes()[1].equals(Flux.class)) {
            rsocketFrameType = FrameType.REQUEST_CHANNEL;
            ;
        }
        if (this.rsocketFrameType == null) {
            assert inferredClassForResult != null;
            if (returnType.equals(Void.TYPE) || (returnType.equals(Mono.class) && inferredClassForResult.equals(Void.TYPE))) {
                this.rsocketFrameType = FrameType.REQUEST_FNF;
            } else if (returnType.equals(Flux.class) || returnType.equals(Flowable.class) || returnType.equals(Observable.class)) {
                this.rsocketFrameType = FrameType.REQUEST_STREAM;
            } else {
                this.rsocketFrameType = FrameType.REQUEST_RESPONSE;
            }
        }
    }

    public String getClassFullName() {
        return classFullName;
    }

    public void setClassFullName(String classFullName) {
        this.classFullName = classFullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public FrameType getRsocketFrameType() {
        return rsocketFrameType;
    }

    public Class<?> getInferredClassForResult() {
        return inferredClassForResult;
    }

    public void setInferredClassForResult(Class<?> inferredClassForResult) {
        this.inferredClassForResult = inferredClassForResult;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }

    public RSocketMimeType getParamEncoding() {
        return paramEncoding;
    }

    public void setParamEncoding(RSocketMimeType paramEncoding) {
        this.paramEncoding = paramEncoding;
    }

}
