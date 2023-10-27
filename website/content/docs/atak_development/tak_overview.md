---
title: "TAK Overview"
icon: icon/svg/kit.svg
date: 2023-08-11T15:26:15Z
description: What is ATAK?
draft: false
weight: 120
---



*Updated: 7 December 2022*

This section will help introduce the Team Awareness Kit (TAK) and familiarize readers with terminology commonly used in the community. The authors of this repository are not representatives of the TAK Product Team, so the documentation in this repo should be considered unofficial. Please defer to the TAK Product Team if this documentation appears conflicting or out of date compared to official resources. The goal of this section is to compile a list of [documentation resources](#additional-tak-documentation-resources) and provide an explanation of key components for development within the TAK ecosystem.

## What is TAK?

The Team Awareness Kit (TAK), or Tactical Assault Kit, is an extensible geospatial and situational awareness platform. It was originally developed as a high quality mapping application for Android devices and a self-sufficient situational awareness tool. TAK has evolved into a suite of applications that communicate and share data to build a better understanding of real-time environments. 

### ATAK

The TAK client system on Android is known as ATAK. Android provides a flexible operating system and is compatible with commercial off-the-shelf (COTS) mobile devices. ATAK runs like any other mobile app and doesn't require any special hardware or device modifications. The application can interface with external peripherals such as software defined radios (SDRs) to extend networking capabilities. The core application provides a high quality map rendering tool, messaging capabilities, and location plotting. 

### Other TAK Client Systems

TAK client applications have also been developed for Windows (WinTAK) and iOS (iTAK), but we will not cover those platforms in any more detail other than to mention their existence in the TAK ecosystem.

### TAK Server

The backbone of the communication capabilities of all TAK client systems is the TAK Server. The primary function of this server is to secure, broker, and store data for all client systems. The TAK Server is required when clients are not operating in a peer-to-peer network, or when there is a need to encrypt and store mission data.

### Product Variants

The TAK Product Lines are offered in various release formats. We will be focused on the Civilian version in this documentation, but be aware that there are other versions for military (TAK-MIL) and government (TAK-GOV) use. 

### Plugins and SDKs

Each TAK product is designed to allow other developers to enhance the core capabilities beyond just mapping and messaging. They provide a Software Development Kit (SDK) which allow third-party plugin developers to leverage the core functions of TAK platform. The documentation and example plugins provided in this repo will give you a better understanding of how to leverage the SDK and develop plugins for ATAK.

### TL;DR

TAK encompasses all applications that provide users with a way to share information and visualize it on high quality maps. 
ATAK is a specialized mapping application that can be enhanced by plugins and communication networks.
TAK Server is an application that stores and routes messages to ATAK devices.

## TAK Applications

[US Forest Service using TAK for fighting Wildfire Fires.](https://www.cofiretech.org/feature-projects/team-awareness-kit-tak)

[Department of Homeland Security use for public safety operations and and national security special events like the Super Bowl and New Years Celebration in Las Vegas.](https://www.dhs.gov/science-and-technology/news/2020/05/05/snapshot-growing-impact-tak)

[Department of Homeland Security analysis of TAK and its benefits in scenarios like responding to natural disasters like Hurricane response](https://www.dhs.gov/sites/default/files/publications/tactical_awareness_kit_508.pdf)

## Additional TAK Documentation Resources

Information in this section is derived from the following sources. These are good reading material to learn more about TAK and see its applications in real-world scenarios.

[Official CivTAK Website](https://www.civtak.org/)

[Official CivTAK Wiki](https://wiki.civtak.org/index.php?title=Main_Page)

[ATAK Reddit Forum](https://www.reddit.com/r/ATAK/wiki/index/)

[ATAK Civilian Tutorial Video Series for how to use ATAK (YouTube)](https://www.youtube.com/playlist?list=PLD4gdaBHX0b7GpPkuy0mbPaCw9kG3YgfB)

[Technical blog articles on ATAK](https://www.ballantyne.online/category/atak/)

<br>
