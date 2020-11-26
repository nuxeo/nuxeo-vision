ARG PLATFORM_VERSION
FROM docker-private.packages.nuxeo.com/nuxeo/nuxeo:${PLATFORM_VERSION}

# FFmpeg
USER 0
RUN yum -y localinstall --nogpgcheck https://mirrors.rpmfusion.org/free/el/rpmfusion-free-release-7.noarch.rpm && \
    yum -y install ffmpeg
USER 900

ARG PLATFORM_VERSION
ARG VERSION
ARG SCM_REF
ARG BUILD_TAG
LABEL org.nuxeo.version="${VERSION}"
LABEL org.nuxeo.scm-ref="${SCM_REF}"
LABEL org.nuxeo.scm-url="git@github.com:nuxeo/nuxeo-vision.git"
LABEL org.nuxeo.build-tag="${BUILD_TAG}"
LABEL org.opencontainers.image.version="${VERSION}"
LABEL org.opencontainers.image.revision="${SCM_REF}"
LABEL org.opencontainers.image.source="git@github.com:nuxeo/nuxeo-vision.git"
LABEL org.opencontainers.image.title="Nuxeo Vision"
LABEL org.opencontainers.image.description="Nuxeo Platform ${PLATFORM_VERSION} with Nuxeo Vision addon"
LABEL org.opencontainers.image.authors="Nuxeo AI <ai-team@nuxeo.com>"
LABEL org.opencontainers.image.vendor="Nuxeo"
LABEL org.opencontainers.image.licenses=" Apache-2.0"

COPY nuxeo-vision-marketplace-*.zip /tmp/nuxeo-vision-marketplace.zip
COPY nuxeo-web-ui.zip /tmp/nuxeo-web-ui.zip
RUN /install-packages.sh /tmp/nuxeo-vision-marketplace.zip /tmp/nuxeo-web-ui.zip
