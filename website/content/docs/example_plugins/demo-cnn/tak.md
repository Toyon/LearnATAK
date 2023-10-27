---
title: "TAK Specifics"
description: A breakdown of the code used to retrieve map items and keep track of our classified pets
icon: icon/svg/kit.svg
date: 2023-08-11T15:26:15Z
draft: false
weight: 260
---

# TAK Specifics

*Updated: 11 May 2023*

This document will highlight some key sections of code that enable the plugin to interact with map items, files, and the TAK network connections.

### Contents

- [Map Events:](#map-events) Enables our plugin to listen to new records provided by others
- [Map Items](#map-items): Stores each pet sighting record
- [CoT & Dispatcher](#cot-and-dispatcher): Enables us to share our pet finding records with others

---

<br>

## Map Events

Source Code: [`MainDropDown`](https://github.com/Toyon/LearnATAK/tree/master/democnn/app/src/main/java/com/atakmap/android/democnn/MainDropDown.java)

```java
public class MainDropDown extends DropDownReceiver
        implements DropDown.OnStateListener, MapEventDispatcher.MapEventDispatchListener {
    
    @Override
    public void onDropDownVisible(boolean visible) {
        if (visible) {
            refreshAdapter();
            getMapView().getMapEventDispatcher().addMapEventListener(this);
        } else
            getMapView().getMapEventDispatcher().removeMapEventListener(this);
    }
    
    // MapEventDispatcher.MapEventDispatchListener
    @Override
    public void onMapEvent(MapEvent mapEvent) {
        if (Objects.equals(mapEvent.getType(), MapEvent.ITEM_ADDED) ||
                Objects.equals(mapEvent.getType(), MapEvent.ITEM_REMOVED)) {
            refreshAdapter();
        }
    }
}
```

We could have the option to create a `new MapEventDispatcher.MapEventDispatchListener` with the `onMapEvent(MapEvent mapEvent)` function implemented as a member variable. However, to reduce boilerplate code we implemented the event dispatch listener in our primary broadcast receiver class as this enabled us to simply add the `@Override  public void onMapEvent(MapEvent mapEvent)` method to our class. We add an additional check to make sure we only take action when a `MapItem` is added or removed from the map. In our case we only want to look update our list of pet sightings when an item is added or removed from the map and only when the pane is visible. This is why we un/register the map event listener in the `@Override public void onDropDownVisible(boolean visible)`method to avoid unnecessary processing. [&#8657;](#contents)

<br>

## Map Items

Source Code: [`MainDropDown`](https://github.com/Toyon/LearnATAK/tree/master/democnn/app/src/main/java/com/atakmap/android/democnn/MainDropDown.java), [`Animal`](https://github.com/Toyon/LearnATAK/tree/master/democnn/app/src/main/java/com/atakmap/android/democnn/adapters/Animal.java)

```java
public class MainDropDown extends DropDownReceiver
        implements DropDown.OnStateListener, MapEventDispatcher.MapEventDispatchListener {
    private void refreshAdapter() {
        ArrayList<Animal> petSightings = new ArrayList<>();
        Collection<MapItem> allItems =  getMapView().getRootGroup().getAllItems();
        allItems.forEach(mapItem -> {
            if (mapItem != null && mapItem.getGroup().getFriendlyName().equals("Animals")) {
                Animal tempAnimal = new Animal(mapItem);
                if (tempAnimal.img != null)
                    petSightings.add(tempAnimal);
            }
        });
        animalAdapter.updateData(petSightings);
    }
}

public class Animal {

    private static final String TAG = Animal.class.getSimpleName();
    public String mapItemUid;
    public String animalType;
    public String timeLocationText;
    public String notes;
    public String certainty = "n/a";
    public Bitmap img;
    public String dmsCoordinates = "n/a"; // DMS == Degree Minute Seconds
    public String author = "n/a";

    public Animal(MapItem mapItem) {
        List<File> attachments = AttachmentManager.getAttachments(mapItem.getUID());
        if (attachments.size() != 1) return;
        mapItemUid = mapItem.getUID();
        animalType = mapItem.getTitle();
        img = BitmapFactory.decodeFile(attachments.get(0).getAbsolutePath());
        notes = mapItem.getRemarks();
        CotEvent petCot = CotEventFactory.createCotEvent(mapItem);
        Log.d(TAG, petCot.toString());
        timeLocationText = petCot.getTime().toString();
        dmsCoordinates = CoordinateFormatUtilities.formatToShortString(
                petCot.getGeoPoint(), CoordinateFormat.DMS);
        CotDetail details = petCot.getDetail();
        CotDetail linkDetail = details.getChild("link");
        try {
            author = linkDetail.getAttribute("parent_callsign");
        } catch (Exception e) {
            Log.w(TAG, "unable to extract author from CoT\n" + details);
        }
        
		// ...
    }
}


```

In our case all of the map items created by our plugin utilize the "paw" icon which is under the "Animals" group. We search through all items in the root map group to identify those which are likely candidates to populate in our pet sighting feed. The `Animal` class helps us determine if the `MapItem` is compatible with our plugin view by using the `AttachmentManager` to see if an image is associated with the marker as that is required in the rendering of the items in our `RecyclerView`. The constructor extracts the `Bitmap` for rendering. In addition to the attachments, the constructor also demonstrates how to convert the `MapItem` to a `CotEvent` which is a flexible format that enables us to extract info like the time of creation, the coordinates, and the author of the `MapItem`. [&#8657;](#contents)

<br>

## CoT and Dispatcher

Source Code: [`ReportDropDown`](../../democnn/app/src/main/java/com/atakmap/android/democnn/ReportDropDown.java#70-119), [`AnimalAdapter`](../../democnn/app/src/main/java/com/atakmap/android/democnn/adapters/AnimalAdapter.java#117-143)

```java
// Adding Map Items Locally and Remotely
protected ReportDropDown(MapView mapView, final Context pluginContext) {
    submitReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleAnimalType = animalType.getText().toString().trim().replaceAll("[^a-zA-Z0-9]", "-");
                String remarks = locationInfo.getText().toString() + " ~ " + note.getText().toString() + " ~ ";
                String uid = UUID.randomUUID().toString();
                MapItem item = new PlacePointTool.MarkerCreator(getMapView().getCenterPoint().get())
                        .setUid(uid).setType("a-n-G").showCotDetails(true)
                        .setIconPath("34ae1613-9645-4222-a9d2-e5f243dea2865/Animals/pawprint.png")
                        .setCallsign(titleAnimalType).setColor(Color.YELLOW)
                        .placePoint();
                item.setMetaBoolean("archive", true);
                item.setRemarks(remarks);

                File markImg = Util.bitmapToFile(img, String.format(Locale.US,"attachments/%s/%s.png", uid, titleAnimalType));
                if (!FileSystemUtils.isFile(markImg)) {
                    Log.w(TAG, "Cannot send invalid file: " + markImg);
                    return;
                }

                Log.d(TAG, "Sending file via Mission Package Tool: " + markImg.getName());
                // Create the Mission Package containing the file
                // receiving device will delete after file is imported
                MissionPackageManifest manifest = MissionPackageApi.CreateTempManifest(markImg.getName(), true, true, null);
                manifest.addMapItem(uid);
                manifest.addFile(markImg, uid);

                Contacts contactsInstance = Contacts.getInstance();
                List<Contact> netContacts =  contactsInstance.getAllContacts();

                // instead send null contact list to make user select destination
                // delete local mission package after sent
                MissionPackageApi.Send(pluginContext, manifest, DeleteAfterSendCallback.class, netContacts.toArray(new Contact[0]));
            }
        });
}

// Deleting Map Items Locally and Remotely
public AnimalAdapter(List<Animal> dataSet, MapView mapView) {
    @Override
    public void dialogCallback(int selectedIndex) {
        ImageView image = new ImageButton(mapView.getContext());
        Animal pet = localDataSet.get(selectedIndex);
        image.setImageBitmap(pet.img);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mapView.getContext())
            ...
            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MapItem item = mapView.getRootGroup().deepFindUID(pet.mapItemUid);

                    CotEvent deleteEvent = new CotEvent();
                    deleteEvent.setUID("any_uid_is_good");
                    deleteEvent.setHow("m-g");
                    deleteEvent.setType("t-x-d-d");
                    CoordinatedTime currentTime =
                        new CoordinatedTime(CoordinatedTime.currentTimeMillis());
                    deleteEvent.setStale(currentTime);
                    deleteEvent.setStart(currentTime);
                    deleteEvent.setTime(currentTime);

                    CotDetail deleteDetails = new CotDetail();
                    CotDetail linkDetail = new CotDetail("link");
                    linkDetail.setAttribute("uid", pet.mapItemUid);
                    linkDetail.setAttribute("relation", "none");
                    linkDetail.setAttribute("type", "none");

                    deleteDetails.addChild(linkDetail);
                    deleteDetails.addChild(new CotDetail("__forcedelete"));
                    deleteEvent.setDetail(deleteDetails);
                    Log.d(TAG, "Send Delete Event to others " + deleteEvent);

                    MapGroup group = item.getGroup();
                    if (group != null)
                        group.removeItem(item);
                    CotMapComponent.getExternalDispatcher().dispatch(deleteEvent);
                }
            });
        dialogBuilder.create().show();
    }
    };
}
```

In order to create a local map item we use the `PlacePointTool.MarkerCreator` in the ATAK API with the `.placePoint()` adding the marker to the map. Holding onto the reference of the `MapItem` we create `*.png` from the `Bitmap` of the animal within the ATAK managed directories. Specifically after examination of the "Quick Pic" tool code it and looking at the file system on the Android device it appears the convention within ATAK is to place files that are associated with a Map Item Marker in the `../atak/attachments/<MAP-ITEM-UID>` directory. After we have the attachment file in place, then use the `MissionPAckageManifest` and `MissionPackageApi` to deliver the map item record and image to other contacts on the network. It is important to note that Cursor-on-Target (CoT) messages have a limited size for images embedded within the XML. Therefore using this Mission API approach we can deliver the complete pet sighting record to all contacts within the TAK network if there is a server in the loop or just a local subnet. Below is an example of the message emitted with the Mission API.

```xml
<event version='2.0' uid='c1620fd6-e2af-4bbb-ba43-9dd396566341' type='b-f-t-r' 
       time='2023-05-09T04:22:19.069Z' start='2023-05-09T04:22:19.069Z' stale='2023-05-09T04:22:29.069Z' how='h-e'>
    <point lat='###' lon='###' hae='-12.754' ce='10.9' le='9999999.0' />
    <detail>
        <fileshare senderUid='ANDROID-e5fcded3b6218c44' 
                   senderUrl='http://172.20.20.14:8080/getfile?file=1&amp;sender=Strawberry' 
                   filename='pup.jpg.zip' 
                   sizeInBytes='145078' 
                   sha256='23ed39b29c448fcbd71161753833fbf6480cf385714fb1cba8eb9a8bef41920a' 
                   peerHosted='true' name='pup.jpg' httpsPort='8443' senderCallsign='Strawberry'/>
        <ackrequest uid='307120f7-a82a-49cf-a1b6-114f778d3ffc' ackrequested='true' tag='pup.jpg'/>
    </detail>
</event>
```

When a client ATAK, WinTAK, iTAK receives a message like this, it is coded to automatically trigger a request to the HTTP endpoint to extract the map item and associated image. It is important to note that we went with this approach due to the fact that the Free TAK Server which is used with the StemX Kit does not have the Mission API supported on the server, so we rely on each device to be responsible for distributing the records. It is possible that records can be missed by others since messages are transmitted using the User Datagram Protocol (UDP) which does not guarantee delivery to the recipient. If this happens you can click on the marker and open up the details then press the "Send" button and walk through the prompts to deliver the item to your intended recipients. 

In addition to being able to send and share pet sightings with others, we also wanted to make it possible for users to remove pet sightings when they become old or after the pet and owner are reconnected. So we have a "Delete" option in the Dialog for each pet record. Here you will see how to remove a local marker by getting the group from the marker item itself `MapGroup group = item.getGroup();`, then removing the item from the group `group.removeItem(item);`. There is no guarantee that finding the marker from the root group will enable you remove the item. Also the method we are taking to delete the marker on remote devices can't be applied to the local system even though it you would think the CoT processor would handle external and internal messages similarly. To notifying other devices to remove a map item and associated files check we create a CoT Event message to dispatch to others. The key elements are proper formatted `<event>` tag with the event tags `type="t-x-d-d"` and `how="m-g"`. You also need to include a `<__forcedelete/>` tag within the `<detail>` tag as well as a `<link uid="<MAP-ITEM-ID-TO-DELETE/>"` within the `CotDetails (<detail>)` . [&#8657;](#contents)