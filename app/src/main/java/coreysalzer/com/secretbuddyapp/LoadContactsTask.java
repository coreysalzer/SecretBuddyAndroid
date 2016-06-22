package coreysalzer.com.secretbuddyapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class LoadContactsTask extends AsyncTask<Void, Void, BaseAdapter> {

  public MainActivity activity;

  public LoadContactsTask(MainActivity activity) {
    this.activity = activity;
  }

  @Override protected CustomAdapter doInBackground(Void[] params) {
    // Check the SDK version and whether the permission is already granted or not.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        && activity.checkSelfPermission(Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
      activity.requestPermissions(new String[] { Manifest.permission.READ_CONTACTS }, 0);
      //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
      return null;
    }

    activity.allContactNames = new ArrayList<>();
    activity.allContactImageUriStrings = new ArrayList<>();

    //Get Contact Data
    Cursor cur = null;
    Cursor phones = null;
    ContentResolver cr = activity.getContentResolver();
    try {
      String[] projection = new String[] {
          ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_URI,
          ContactsContract.Contacts._ID, ContactsContract.Contacts.HAS_PHONE_NUMBER
      };
      cur = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

      if (cur == null || cur.getCount() == 0) {
        return null;
      }

      while (cur.moveToNext()) {
        activity.allContactNames.add(
            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
        activity.allContactImageUriStrings.add(
            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)));

        if (Integer.parseInt(
            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

          try {
            projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
            };

            phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[] { cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)) },
                null);

            if (phones == null || phones.getCount() == 0) {
              return null;
            }

            boolean hasMobile = false;
            while (phones.moveToNext()) {
              // Only adds the mobile number to be able to send SMS
              int phoneType =
                  phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
              if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                activity.allContactPhoneNumbers.add(
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                hasMobile = true;
                break;
              }
            }

            if(!hasMobile) {
              activity.allContactPhoneNumbers.add(null);
            }
          } finally {
            if(phones != null) {
              phones.close();
            }
          }
        } else {
          activity.allContactPhoneNumbers.add(null);
        }
      }
    } finally {
      if (cur != null) {
        cur.close();
      }
    }

    return new CustomAdapter(activity, activity.allContactNames, activity.allContactImageUriStrings,
        R.layout.list_view_add_buddies_item) {

      @Override protected boolean isAlreadyAdded(String name) {
        return activity.names.indexOf(name) != -1;
      }
    };
  }
}
