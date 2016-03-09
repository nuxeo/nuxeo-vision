## Description
This plugin provides a wrapper for Computer Vision API. Currently it supports the Google Vision API.

## Important Note

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Requirements
Building requires the following software:
- git
- maven

## How to build
 
- Enable the Vision API from the google developer console
- As of march 2nd 2016, billing must be activated in your google account in order to use the Vision API
- Configure a [service account](https://developers.google.com/identity/protocols/OAuth2ServiceAccount) and set the NUXEO_GOOGLE_APPLICATION_CREDENTIALS ENV Variable with the JSON file path
 
```
git clone https://github.com/nuxeo-sandbox/nuxeo-computer-google-vision
cd nuxeo-labs-computer-vision
mvn clean install
```

## Deploying
- Install the marketplace package
- Upload the JSON key file on your instance
- Edit nuxeo.conf 

```
org.nuxeo.labs.google.credential=PATH_TO_JSON_CREDENTIAL_File
```

## Known limitations
This plugin is a work in progress.

## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
