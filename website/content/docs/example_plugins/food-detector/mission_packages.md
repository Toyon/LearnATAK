---
title: "Mission Packages"
icon: icon/svg/mission_package.svg
description: An Overview on Mission Packages and how to use them to send Map data to other ATAK users.
date: 2023-08-11T15:26:15Z
lastmod: 2023-08-11T15:26:15Z
weight: 293
draft: false
---

## What are Mission Packages?

A Mission Package (also called a Data Package) is an object that primarily stores items/markers/points from the  ATAK map. These Mission Packages can be saved on to the Android device and sent to other Android devices. When other users receive these Mission Packages, they can add the objects within the Mission Package to their own  ATAK map.

Mission Packages contain a `MissionPackageManifest`, which we will use to store/transmit data. The Manifest contains elements called `MissionPackageContent`, which we can use to keep the data in a known order.


### Mission Package Format

The `MissionPackage` is outlined with an XML manifest file. Below is a sample `MissionPackageManifest` with two `MissionPackageContent` objects. 

```xml
<MissionPackageManifest version="2">
    <Configuration>
        <Parameter name="uid" value="491ec25c-3eb3-4322-8d4c-626b1f7e3db7"/>
        <Parameter name="name" value="export5"/>
        <Parameter name="onReceiveImport" value="true"/>
        <Parameter name="onReceiveDelete" value="false"/>
    </Configuration>
    <Contents>
    <Content ignore="false" zipEntry="491ec25c-3eb3-4322-8d4c-626b1f7e3db7">
        <Parameter name="name" value="Fudge"/>
        <Parameter name="latitude" value="34.433867"/>
        <Parameter name="longitude" value="-119.865435"/>
    </Content>
    <Content ignore="false" zipEntry="491ec25c-3eb3-4322-8d4c-626b1f7e3db7">
        <Parameter name="name" value="Barbacoa"/>
        <Parameter name="latitude" value="34.433784"/>
        <Parameter name="longitude" value="-119.865446"/>
    </Content>
    </Contents>
</MissionPackageManifest>
```

### Building a Mission Package Content object

First, we're going to demonstrate how to add two `MissionPackageContent` objects to a `MissionPackageManifest`.

```java
MissionPackageManifest manifest = MissionPackageApi.CreateTempManifest("ExampleManifest"); //create the MissionPackageManifest
String manifestLocation = manifest.getLastSavedPath(); // save the path of the MissionPackage
        
MissionPackageContent content1 = new MissionPackageContent(); //create the MissionPackageContent object
content1.setManifestUid(manifest.getUID()); //this is essential to link the MissionPackageContent object with the correct Manifest
content1.setParameter("name", "location1"); //this sets a parameter (HashMap) with "name" as the key and "location1" as the value
content1.setParameter("latitude", "0.0");
content1.setParameter("longitude", "100.0");

MissionPackageContent content2 = new MissionPackageContent(); //create a second MissionPackageContent object
content2.setManifestUid(manifest.getUID());
content2.setParameter("foodtype", "fudge");
content2.setParameter("number", "1");

//add the MissionPackageContent objects to the MissionPackageManifest
manifeset.getContents().setContent(content1);
manifest.getContents().setContent(content2);
```

Now, let's save the Manifest

```java
boolean saveResult = MissionPackageApi.Save(mapView.getContext(), manifest, null);
```

You should see the `MissionPackage` named as "ExampleManifest" in the Data Packages menu on the ATAK app's starting menu
(in between "Contacts" and "Digital Pointer"). 

Now, we can import the objects from the zip file

```java
MissionPackageExtractor extractor = new MissionPackageExtractor(); //create the MissionPackageExtractor object
MissionPackageManifest manifest = extractor.getManifest(manifestLocation); //get the MissionPackageManifest

MissionPackageContents contents = manifest.getContents(); //get the contents of the MissionPackage

List<MissionPackageContent> contentList = contents.getContents(); //get the contents as a List of MissionPackageContent objects

for(int i = 0; i < contentList.size(); i++){
    MissionPackageContent content = contentList.get(i);
    // do something with the content object, like 
    String name = content.getParameterValue("name");
    //note: if content does not contain the getParameterValue("input") key, it will return null
}
```

