## Description
This plugin provides a wrapper for Computer Vision Services. Currently it supports the Google Vision API.

## Status

This is a moving project in beta stage, as Google Vision API (no API maintenance, no deprecation process, etc.).

## Requirements
Building requires the following software:
- git
- maven

## How to build
 
- Enable the Vision API from the google developer console
- As of march 2nd 2016, billing must be activated in your google account in order to use the Vision API
- Get an API key from the Google Developer Console and set the NUXEO_GOOGLE_APPLICATION_KEY Environment Variable with the key
 
```
git clone https://github.com/nuxeo-sandbox/nuxeo-computer-google-vision
cd nuxeo-labs-computer-vision
mvn clean install
```

## Deploying
- Install the marketplace package
- Configure a [service account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) and set the NUXEO_GOOGLE_APPLICATION_CREDENTIALS ENV Variable with the JSON file path
- Upload the JSON key file on your instance
- Edit nuxeo.conf 

```
org.nuxeo.labs.google.credential=PATH_TO_JSON_CREDENTIAL_FILE
```

## Known limitations
This plugin is a work in progress.

## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
