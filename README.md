# About / Synopsis
This plugin provides a wrapper for Computer Vision Services. Currently it supports the [Google Vision API](https://cloud.google.com/vision/).
  
# Installation
- Configure a [Google service account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount)
- Upload the JSON key file on your instance
- Edit nuxeo.conf
```
org.nuxeo.vision.google.credential=PATH_TO_JSON_CREDENTIAL_FILE
```
- From the Nuxeo Marketplace: install [the Sample Nuxeo Package](https://connect.nuxeo.com/nuxeo/site/marketplace/package/nuxeo-vision).
- From the Nuxeo server web UI "Admin / Update Center / Packages from Nuxeo Marketplace"
- From the command line: `nuxeoctl mp-install nuxeo-vision`
  
# Code
## QA
[![Build Status](https://qa.nuxeo.org/jenkins/buildStatus/icon?job=plugins_nuxeo-vision-master)](https://qa.nuxeo.org/jenkins/job/plugins_nuxeo-vision-master/)
 
## Requirements
Build requires the following software:
- git
- maven
 
## Limitations
N/A
 
## Build
- Enable the Vision API from the google developer console
- As of march 2nd 2016, billing must be activated in your google account in order to use the Vision API
- Get an API key from the Google Developer Console
```
git clone https://github.com/nuxeo/nuxeo-vision
cd nuxeo-vision
mvn clean install -Dorg.nuxeo.vision.test.credentail.key=MY_KEY
```
 
## Deploy (how to install build product)
- Install the marketplace package
- Configure a [service account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount)
- Upload the JSON key file on your instance
- Edit nuxeo.conf
```
org.nuxeo.vision.google.credential=PATH_TO_JSON_CREDENTIAL_FILE
```
 
# Resources (Documentation and other links)
[Plugin Documentation](https://doc.nuxeo.com/x/PYHZAQ)
[Google Vision](https://cloud.google.com/vision/)
 
# Contributing / Reporting issues
[JIRA](https://jira.nuxeo.com/browse/NXP/component/15408/)
 
# License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 
# About Nuxeo
The [Nuxeo Platform](http://www.nuxeo.com/products/content-management-platform/) is an open source customizable and extensible content management platform for building business applications. It provides the foundation for developing [document management](http://www.nuxeo.com/solutions/document-management/), [digital asset management](http://www.nuxeo.com/solutions/digital-asset-management/), [case management application](http://www.nuxeo.com/solutions/case-management/) and [knowledge management](http://www.nuxeo.com/solutions/advanced-knowledge-base/). You can easily add features using ready-to-use addons or by extending the platform using its extension point system.
 
The Nuxeo Platform is developed and supported by Nuxeo, with contributions from the community.
 
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with
SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris.
More information is available at [www.nuxeo.com](http://www.nuxeo.com).