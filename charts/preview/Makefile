OS := $(shell uname)

ifeq ($(OS),Darwin)
SED=sed -i ''
else ifeq ($(OS),Linux)
SED=sed -i
else
	echo "Platform $(OS) not supported"
	exit -1
endif

init:
	helm init --client-only
	helm repo add jenkins-x http://chartmuseum.jenkins-x.io
	helm repo add jenkins-x-local http://jenkins-x-chartmuseum:8080
	helm repo add storage.googleapis.com https://storage.googleapis.com/chartmuseum.jenkins-x.io
	helm repo add chartmuseum.platform.dev.nuxeo.com https://chartmuseum.platform.dev.nuxeo.com
	helm repo add chartmuseum.ai.dev.nuxeo.com https://chartmuseum.ai.dev.nuxeo.com
	helm repo add stable https://kubernetes-charts.storage.googleapis.com

preview: clean #init
	envsubst < values.tmpl.yaml > values.yaml
	$(SED) -e "s/version:.*/version: $(PREVIEW_VERSION)/" Chart.yaml
	jx step helm build --verbose
#	helm lint .

clean:
	rm -rf charts requirements.lock
