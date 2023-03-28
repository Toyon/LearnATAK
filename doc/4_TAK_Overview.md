# TAK Overview

*Updated: 7 December 2022*

This section will help introduce the Team Awareness Kit (TAK) and familiarize readers with terminology commonly used in the community. With numerous websites on the internet with slightly varying descriptions of TAK it can become disorienting to new developers trying to figure out what exactly TAK is and does. The authors of this repository are not representatives of the TAK Product Team so the descriptions provided below are coming from an outside organizations perspective. This goal of this section is to compile a list of these resources, listed in the [Additional Resources](##-Additional-TAK-Documentation-Resources) section at the end of this section, and provide a simplified explanation of some key components within the TAK ecosystem that you will want to ensure you have a decent understanding of to help you understand how your software can be included as a plugin.

## What is TAK?

The Team Awareness Kit, also referred to as the Tactical Assault Kit in military settings, (TAK) is an extensible geospatial and situational awareness platform. It was originally developed as a high quality mapping application for Android devices making it a self-sufficient situational awareness tool. Since it's origins TAK has evolved into a suite of applications developed to communicate with each other sharing data to collectively build a better understanding of the environment and events occurring around them in real time. 

### ATAK

The original mapping platform has now become the forefront of the TAK client systems known as the Android TAK (ATAK). The Android system has been the primary target of client interface development given the more flexible nature of the Android operating system in addition to the fact common commercial off-the-shelf (COTS) mobile devices or tables are more suitable for users on the move. ATAK and all of the core functions run like any other mobile app and doesn't require any special hardware or device modifications. The application can interface with external peripherals such as software defined radios (SDRs) to extend networking capabilities. The core application with out any additional hardware or software is capable of provides users a high quality map rendering tool, messaging capabilities, and the ability to automatically plot other user locations in addition to being able to mark areas or points of interest on the map for a user group. 

### Other TAK Client Systems

It is also worth mentioning that TAK client applications have also been developed for Windows (WinTAK) and iOS (iTAK), but we will not cover those platforms in any more detail than to mention their existence in the TAK ecosystem.

### TAK Server

The backbone of the communication capabilities of all TAK client systems is the TAK Server. The primary function of this server is to secure, broker, and store data for all client systems. The TAK Server is required when clients are not operating in a peer-to-peer network or there is a need to encrypt and store mission data.

### Product Variants

The TAK Product Lines are offered in various release formats. For the purposes of the documentation available in this collection of documents and examples we will be focused on the Civilian versions, but it is good to be aware of that there are other versions for military (TAK-MIL) and government (TAK-GOV) use. 

### Plugins and SDKs

Each TAK product is designed in a way to allow other developers to enhance the core capabilities beyond just mapping and messaging. They all provide a Software Development Kit (SDK) which allow third-party plugin developers to leverage the core functions of TAK platform they are trying to enhance. The next sections and example plugins provided in this repo are designed to provide you with a better understanding of how to leverage the SDK and develop plugins for ATAK devices.

### TLDR

TAK encompasses all applications that provide users with a way to share information and visualize that data on high quality maps. 
ATAK is a specialized mapping application that can be enhanced by plugins and communication networks.
TAK Server is an application that stores and routes messages to ATAK devices.

## TAK Applications

[US Forest Service using TAK for fighting Wildfire Fires.](https://www.cofiretech.org/feature-projects/team-awareness-kit-tak)

[Department of Homeland Security use for public safety operations and and national security special events like the Super Bowl and New Years Celebration in Las Vegas.](https://www.dhs.gov/science-and-technology/news/2020/05/05/snapshot-growing-impact-tak)

[Department of Homeland Security analysis of TAK and it's benefits in scenarios like responding to natural disasters like Hurricane response](https://www.dhs.gov/sites/default/files/publications/tactical_awareness_kit_508.pdf)

## Additional TAK Documentation Resources

Information in this section is derived from the following sources which is good reading material to further enhance your understanding of TAK and see it's applications in real world scenarios.

[Official CivTAK Website](https://www.civtak.org/)

[Official CivTAK Wiki](https://wiki.civtak.org/index.php?title=Main_Page)

[ATAK Reddit Forum](https://www.reddit.com/r/ATAK/wiki/index/)

[ATAK Civilian Tutorial Video Series for how to use ATAK (YouTube)](https://www.youtube.com/playlist?list=PLD4gdaBHX0b7GpPkuy0mbPaCw9kG3YgfB)

[Technical blog articles on ATAK](https://www.ballantyne.online/category/atak/)