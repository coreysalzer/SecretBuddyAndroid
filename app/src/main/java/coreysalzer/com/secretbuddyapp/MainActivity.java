package coreysalzer.com.secretbuddyapp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private Dialog dialog;
  private Button createPairingButton;
  private Button addBuddiesButton;
  private ListView listView;
  private ListView dialogListView;
  protected List<String> names;
  protected List<String> imageUriStrings;
  protected List<String> phoneNumbers;
  protected List<String> allContactNames;
  protected List<String> allContactImageUriStrings;
  protected List<String> allContactPhoneNumbers;
  private Button sendSMSButton;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    createPairingButton = (Button) findViewById(R.id.create_pairing);
    listView = (ListView) findViewById(R.id.listView);
    addBuddiesButton = (Button) findViewById(R.id.add_buddies);
    sendSMSButton = (Button) findViewById(R.id.send_sms);
    dialog = new Dialog(this);
    dialog.setContentView(R.layout.dialog_add_buddies);
    dialog.setTitle(R.string.add_buddies_button);

    names = new ArrayList<>();
    imageUriStrings = new ArrayList<>();

    dialogListView = (ListView) dialog.findViewById(R.id.contacts_list_view);

    addBuddiesButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        executeLoadContactsTask();
      }
    });

    createPairingButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        createPairingButton.setVisibility(View.INVISIBLE);
        sendSMSButton.setVisibility(View.VISIBLE);
        createPairings();
      }
    });

    sendSMSButton.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(View v) {

      }
    });
  }

  private void executeLoadContactsTask() {
    new LoadContactsTask(MainActivity.this) {

      @Override protected void onPreExecute() {
        addBuddiesButton.setEnabled(false);
        //TODO - show loading dialog
      }

      @Override protected void onPostExecute(BaseAdapter baseAdapter) {
        addBuddiesButton.setEnabled(true);
        dialogListView.setAdapter(baseAdapter);

        if (baseAdapter.isEmpty()) {
          showErrorDialog();
          return;
        }

        dialog.show();

        processDialog();
      }
    }.execute();
  }

  private void processDialog() {
    Button dialogCancelAddBuddiesButton = (Button) dialog.findViewById(R.id.cancel_add_buddies);

    dialogCancelAddBuddiesButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dialog.dismiss();
      }
    });

    Button dialogAddSelectedBuddiesButton =
        (Button) dialog.findViewById(R.id.add_selected_buddies_button);
    dialogAddSelectedBuddiesButton.setOnClickListener(new View.OnClickListener() {
      @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) @Override public void onClick(View v) {
        CustomAdapter adapter = (CustomAdapter) dialogListView.getAdapter();
        ArrayList<Integer> indexesToAdd = adapter.getContactIndexesToAdd();
        for (int i = 0; i < indexesToAdd.size(); i++) {
          names.add(allContactNames.get(indexesToAdd.get(i)));
          imageUriStrings.add(allContactImageUriStrings.get(indexesToAdd.get(i)));
          phoneNumbers.add(allContactPhoneNumbers.get(indexesToAdd.get(i)));
        }

        ArrayList<Integer> indexesToRemove = adapter.getContactIndexesToRemove();
        for (int i = 0; i < indexesToRemove.size(); i++) {
          names.remove(allContactNames.get(indexesToRemove.get(i)));
          imageUriStrings.remove(allContactImageUriStrings.get(indexesToRemove.get(i)));
          phoneNumbers.remove(allContactPhoneNumbers.get(indexesToRemove.get(i)));
        }

        dialog.dismiss();
        listView.setAdapter(
            new CustomAdapter(MainActivity.this, names, imageUriStrings, R.layout.list_view_item));
        createPairingButton.setVisibility(View.VISIBLE);
        sendSMSButton.setVisibility(View.INVISIBLE);
        RelativeLayout.LayoutParams params =
            (RelativeLayout.LayoutParams) addBuddiesButton.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        addBuddiesButton.setLayoutParams(params);
        addBuddiesButton.setText(R.string.add_remove_buddies);
      }
    });
  }

  private void showErrorDialog() {
    final Dialog errorDialog = new Dialog(this);
    LinearLayout layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    TextView errorTextView = new TextView(this);
    errorTextView.setText(R.string.no_contacts_error_message);
    errorTextView.setTextColor(Color.RED);
    layout.addView(errorTextView);
    Button okButton = new Button(this);
    okButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        errorDialog.dismiss();
      }
    });
    okButton.setText(R.string.ok);
    layout.addView(okButton);
    errorDialog.addContentView(layout,
        new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    errorDialog.show();
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 0) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        executeLoadContactsTask();
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private void createPairings() {
    if (names.size() == 0) {
      return;
    }

    List<Integer> buddyIndexes = new ArrayList<Integer>();
    for (int i = 0; i < names.size(); i++) {
      buddyIndexes.add(i);
    }

    for (int i = 0; i < names.size(); i++) {
      int random = (int) (Math.random() * buddyIndexes.size());
      if (i == buddyIndexes.get(random) || (i == names.size() - 2 &&
          buddyIndexes.get(buddyIndexes.size() - 1) == names.size() - 1 &&
          buddyIndexes.get(random) != names.size() - 1)) {
        i--;
        continue;
      }

      int index = buddyIndexes.remove(random);

      listView.getChildAt(i).findViewById(R.id.toggle_buddy_button).setVisibility(View.VISIBLE);
      TextView buddyName = (TextView) listView.getChildAt(i).findViewById(R.id.buddy_name);
      buddyName.setText(names.get(index));
      ImageView buddyImage = (ImageView) listView.getChildAt(i).findViewById(R.id.buddy_picture);
      if (imageUriStrings.get(index) != null) {
        buddyImage.setImageURI(Uri.parse(imageUriStrings.get(index)));
      } else {
        buddyImage.setImageResource(R.mipmap.ic_launcher);
      }
    }
  }

  public void toggleBuddy(View v) {
    View listItemView = listView.getChildAt(listView.indexOfChild((View) (v.getParent())));
    if (((ToggleButton) v).isChecked()) {
      listItemView.findViewById(R.id.buddy_name).setVisibility(View.VISIBLE);
      listItemView.findViewById(R.id.buddy_picture).setVisibility(View.VISIBLE);
    } else {
      listItemView.findViewById(R.id.buddy_name).setVisibility(View.GONE);
      listItemView.findViewById(R.id.buddy_picture).setVisibility(View.GONE);
    }
  }
}

