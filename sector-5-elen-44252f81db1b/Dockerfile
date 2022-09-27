# Information
FROM dontpanic57/play-docker:20.06
LABEL maintainer="Sector5 <pierre@sector5.fr>"
EXPOSE 9000


# Health check
HEALTHCHECK --start-period=120s --interval=5s --timeout=2s --retries=1 CMD wget -q -O /dev/null -T 2 http://127.0.0.1:9000/healthcheck || exit 1


# PlayFramework
USER appuser
COPY --chown=appuser:appuser target/universal/stage/ .ci-data/bootstrap.sh /opt/playframework/
RUN chmod +x /opt/playframework/bootstrap.sh
CMD ["/opt/playframework/bootstrap.sh", "/opt/playframework/bin/elen"]
