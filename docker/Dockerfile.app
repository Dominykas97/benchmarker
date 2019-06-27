FROM flink

ADD components.yaml ./
ADD config.yaml ./
ADD target/benchmarker-0.1.jar ./
ADD docker/app-entrypoint.sh ./

RUN chmod +x app-entrypoint.sh && \
    echo "metrics.latency.interval: 1000\n\
    metrics.reporters: prom\n\
    metrics.reporter.prom.class: org.apache.flink.metrics.prometheus.PrometheusReporter\n\
    metrics.reporter.prom.port: 9250-9260\n\
    jobmanager.rpc.address: jobmanager" >> conf/flink-conf.yaml && \
    echo "flink run benchmarker-0.1.jar" >> /docker-entrypoint.sh && \
    mv opt/flink-metrics-prometheus-* lib/

ENTRYPOINT [ "/docker-entrypoint.sh" ]
