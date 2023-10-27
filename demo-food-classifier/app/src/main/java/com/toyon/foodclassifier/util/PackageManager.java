package com.toyon.foodclassifier.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.toyon.foodclassifier.database.PictureReview;
import com.toyon.foodclassifier.database.PictureReviewRepository;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.missionpackage.MissionPackageMapComponent;
import com.atakmap.android.missionpackage.MissionPackageReceiver;
import com.atakmap.android.missionpackage.api.MissionPackageApi;
import com.atakmap.android.missionpackage.file.MissionPackageBuilder;
import com.atakmap.android.missionpackage.file.MissionPackageContent;
import com.atakmap.android.missionpackage.file.MissionPackageExtractor;
import com.atakmap.android.missionpackage.file.MissionPackageManifest;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.coremap.log.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/** Set of utility functions for generating, reading, and using plugin generated data packages  */
public class PackageManager {

    private static final String TAG = PackageManager.class.getSimpleName();

    /** consistent UID to use for data packages created for sharing reviews with other clients */
    private static final String PLUGIN_PACKAGE_UID = "com.toyon.foodclassifier.DATA_PACAKGE";

    /** data package file name used for sharing reviews with other TAK users (no extension) */
    public static final String PACKAGE_NAME = "foodreviews";

    /** data package file name used for exporting or backing up database reviews (no extension) */
    public static final String PACKAGE_NAME_EXPORT = "foodreviews-backup";

    /**
     * Add a Food Review item to a Data / Mission Package Manifest
     * Each review contributes 2 items to the data package. Textual details are included as a
     * CoT object file and the image is added as an associated file. This method adheres to the
     * standard conventions of TAK data packages to allow users without the plugin to view the
     * contents using the default Data Package Tool.
     *
     * @param manifest MissionPackageManifest object to be built in the export
     * @param review The Food Review object (from the local database)
     */
    public static void addReview(MissionPackageManifest manifest, PictureReview review) {
        try {
            // add the CoT representation of the review to the manifest
            manifest.addContent(review.toMissionPackageContent());
            // search "../atak/attachments" for a directory name equal to the review's map item UID
            File attachmentDir = FileSystemUtils.getItem("attachments");
            for (File f : Objects.requireNonNull(attachmentDir.listFiles())) {
                if (f.getName().equals(review.getUid())) {
                    Log.d(TAG, "Found attachments for food item " + review.getUid());
                    // there should only be one image in "atak/attachments/UID/"
                    final File pic = Objects.requireNonNull(f.listFiles())[0];
                    manifest.addFile(pic, review.getUid());
                }
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "Failed to add Review to Data Package Manifest\n", e);
        }
    }

    /**
     * Export the Food Reviews database to a TAK data package to have a backup copy
     * @param reviews list of food reviews to include in the backup package
     * @param mapView ATAK Map view
     */
    public static void exportReviews(List<PictureReview> reviews, MapView mapView) {
        MissionPackageManifest manifest = new MissionPackageManifest(
                PackageManager.PACKAGE_NAME_EXPORT, PackageManager.getIncomingDir());
        for (int i = 0; i < reviews.size(); i++) {
            PackageManager.addReview(manifest, reviews.get(i));
        }
        MissionPackageBuilder packageBuilder = new MissionPackageBuilder(null,
                manifest, mapView.getRootGroup());
        String savedPath = packageBuilder.build();
        boolean saved = MissionPackageApi.Save(mapView.getContext(), manifest, null);
        Log.d(TAG, saved ? "saved data package" : "failed to save data package");
        Log.d(TAG, String.format(Locale.US, "Data Package Manifest Export:" +
                        "\nFile Path: %s\nMap Items: %d\n%s",
                savedPath, manifest.getMapItemCount(), manifest.toXml(false)));

        Toast.makeText(mapView.getContext(),
                String.format(Locale.US, "Exported %d review(s) to %s", reviews.size(),
                        PackageManager.PACKAGE_NAME_EXPORT),
                Toast.LENGTH_LONG).show();

        // Open Data Package Tool to view contents of the created data package
        Intent dataPackageIntent = new Intent()
                .setAction(MissionPackageReceiver.MISSIONPACKGE_DETAIL)
                .putExtra("MissionPackageUID", manifest.getUID());
        AtakBroadcast.getInstance().sendBroadcast(dataPackageIntent);
    }

    /**
     * Create a "temporary" data package to send to send/share with other TAK clients
     * @param reviews List of food reviews to include in the exported and shared data package
     * @param mapContext ATAK Map Context
     */
    public static void shareReviews(List<PictureReview> reviews, Context mapContext) {
        MissionPackageManifest manifest = new MissionPackageManifest(
                PackageManager.PACKAGE_NAME, PLUGIN_PACKAGE_UID, PackageManager.getRootDir());
        for (int i = 0; i < reviews.size(); i++) {
            PackageManager.addReview(manifest, reviews.get(i));
        }
        // Build zip archive contents and send the data package to user specified contact
        boolean sent = MissionPackageApi.Send(mapContext, manifest, null, null);
        Log.d(TAG, "Package Sent? " + sent);//" location: " + savedPath);
        if (!sent)
            Toast.makeText(mapContext, "Can't Share Reviews", Toast.LENGTH_SHORT).show();
    }

    /**
     * Import new food review records into local database from the provided data package
     * @param manifest data package manifest object used for import logic
     * @param ctx ATAK Map context
     * @param repository Room Database repository object for local Picture Reviews
     */
    public static void importReviews(MissionPackageManifest manifest, Context ctx,
                                     PictureReviewRepository repository) {
        if (manifest == null) {
            Toast.makeText(ctx, "No backup review package to import", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Can't import null manifest");
            return;
        }
        List<MissionPackageContent> contentList = manifest.getContents().getContents();
        File dataPackage = new File(manifest.getLastSavedPath());
        // iterate at step count of 2 since CoT and associate image should appear as pairs
        for (int i = 0; i < contentList.size(); i+=2) {
            try {
                MissionPackageContent reviewContent = contentList.get(i);
                MissionPackageContent imageContent = contentList.get(i + 1);
                String imageName = imageContent.getManifestUid().split(File.separatorChar+"")[1];
                String markerUid = imageContent.getParameter("uid").getValue();
                String imageOutputPath = FileSystemUtils.getItem("attachments" +
                        File.separatorChar + markerUid + File.separatorChar + imageName)
                        .getAbsolutePath();
                // set this local path param to help extractor place marker image in proper marker attachment directory
                imageContent.setParameter("localpath", imageOutputPath);

                // Use MissionPackageExtractor to create map marker and image attachment
                String cotString = MissionPackageExtractor.ExtractCoT(ctx, dataPackage,
                        reviewContent, true);
                boolean extracted = MissionPackageExtractor.ExtractFile(dataPackage, imageContent);

                // validate proper extraction and populate database records
                if (cotString == null || !extracted) {
                    Log.e(TAG, "Failed to unpack image review");
                    continue;
                }
                PictureReview review = new PictureReview().fromCotEvent(CotEvent.parse(cotString));
                review.setBitmapData(Files.readAllBytes(Paths.get(imageOutputPath)));
                repository.insert(review);
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Review Packages expect 2 items per review. ABORT IMPORT.\n" + e);
            } catch (IOException e) {
                Log.e(TAG, "Issue reading extracted image for review record\n" + e);
            }
        }
    }

    /**
     * Map markers should be loaded onto map automatically when package is received
     * just record the new reviews in the database and clear out the data package
     * @param context
     * @param repository
     */
    public static void extractSharedReviews(Context context, PictureReviewRepository repository) {
        File sharePackage = new File(getPathSharePluginPackage());
        if (!sharePackage.exists()) {
            Log.d(TAG, "Shared Reviews Package Does Not Exist (DNE) ABORT EXTRACT");
            return;
        }
        MissionPackageManifest manifest = MissionPackageExtractor.GetManifest(sharePackage);
        importReviews(manifest, context, repository);
        if (sharePackage.delete())
            Log.d(TAG, "Extracted shared reviews and cleaned data package");
        else
            Log.d(TAG, "Failed to delete shared review package");
    }

    /**
     * Delete the file / zipped data archive at the provided file path
     * @param path absolute file path to delete
     */
    public static void removePackage(String path) {
        File dataPackage = new File(path);
        try {
            if (dataPackage.delete()) Log.d(TAG, "DELETED " + path);
        } catch (Exception e) {
            Log.e(TAG, "FAILED TO REMOVE PACKAGE\n" + e);
        }
    }

    /**
     * Get the absolute file path to backup or exported plugin generated data package
     * @return ".../atak/tools/datapackage/incoming/foodreviews-backup.zip"
     */
    public static String getPathExportPluginPackage() {
        return PackageManager.getIncomingDir() + File.separatorChar +
                PackageManager.PACKAGE_NAME_EXPORT + ".zip";
    }

    /**
     * Get the absolute file path to plugin generated data package to be sent or shared with other
     * TAK clients
     * @return ".../atak/tools/datapackage/foodreviews.zip"
     */
    public static String getPathSharePluginPackage() {
        return PackageManager.getRootDir() + File.separatorChar +
                PackageManager.PACKAGE_NAME + ".zip";
    }

    /**
     * Get the absolute path to the root directory of ATAK's managed Data Packages
     * @return ".../atak/tools/datapackage"
     */
    public static String getRootDir() {
        return MissionPackageMapComponent.getInstance().getFileIO().getMissionPackagePath();
    }

    /**
     * Get the absolute path to the "incoming" directory of ATAK's managed Data Packages
     * @return ".../atak/tools/datapackage/incoming"
     */
    public static String getIncomingDir() {
        return MissionPackageMapComponent.getInstance().getFileIO()
                .getMissionPackageIncomingDownloadPath();
    }
}
