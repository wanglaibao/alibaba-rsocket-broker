package com.alibaba.rsocket.listen;

import com.alibaba.rsocket.listen.impl.RSocketListenerBuilderImpl;
import io.rsocket.SocketAcceptor;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.plugins.DuplexConnectionInterceptor;
import io.rsocket.plugins.RSocketInterceptor;
import io.rsocket.plugins.SocketAcceptorInterceptor;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * RSocket listener: support multi ports and protocols
 *
 * @author leijuan
 */
public interface RSocketListener {

    Collection<String> serverUris();

    void start() throws Exception;

    void stop() throws Exception;

    Integer getStatus();

    static Builder builder() {
        return new RSocketListenerBuilderImpl();
    }

    interface Builder {

        Builder listen(String schema, int port);

        Builder errorConsumer(Consumer<Throwable> errorConsumer);

        Builder sslContext(Certificate certificate, PrivateKey privateKey);

        Builder payloadDecoder(PayloadDecoder payloadDecoder);

        Builder addResponderInterceptor(RSocketInterceptor interceptor);

        Builder addSocketAcceptorInterceptor(SocketAcceptorInterceptor interceptor);

        Builder addConnectionInterceptor(DuplexConnectionInterceptor interceptor);

        Builder acceptor(SocketAcceptor acceptor);

        RSocketListener build();
    }
}
