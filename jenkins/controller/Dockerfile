FROM jenkins/jenkins:lts-jdk21

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Xms512m -Xmx1024m" \
    CASC_JENKINS_CONFIG=/usr/local/share/jenkins/casc.yaml

COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

COPY --chown=jenkins:jenkins casc.yaml /usr/local/share/jenkins/casc.yaml
COPY --chown=jenkins:jenkins jobs/ /usr/local/share/jenkins/jobs_dsl/

HEALTHCHECK --interval=15s --timeout=5s --start-period=45s --retries=8 \
  CMD curl -fsS http://127.0.0.1:8080/login >/dev/null || exit 1
