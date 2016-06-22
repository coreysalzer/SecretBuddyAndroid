package coreysalzer.com.secretbuddyapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

  private List<String> names;
  private List<String> imageUriStrings;
  int layoutId;
  private LayoutInflater inflater;
  private ArrayList<Integer> contactIndexesToAdd;
  private ArrayList<Integer> contactIndexesToRemove;

  public CustomAdapter(Context context, List<String> names, List<String> imageUriStrings,
      int layoutId) {
    this.names = names;
    this.imageUriStrings = imageUriStrings;
    this.layoutId = layoutId;
    contactIndexesToAdd = new ArrayList<Integer>();
    contactIndexesToRemove = new ArrayList<Integer>();
    this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override public int getCount() {
    return names.size();
  }

  @Override public String getItem(int position) {
    return names.get(position);
  }

  @Override public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
  }

  @Override public void notifyDataSetInvalidated() {
    super.notifyDataSetInvalidated();
  }

  @Override public boolean areAllItemsEnabled() {
    return super.areAllItemsEnabled();
  }

  @Override public boolean isEnabled(int position) {
    return super.isEnabled(position);
  }

  @Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return super.getDropDownView(position, convertView, parent);
  }

  @Override public int getItemViewType(int position) {
    return super.getItemViewType(position);
  }

  @Override public int getViewTypeCount() {
    return super.getViewTypeCount();
  }

  @Override public boolean isEmpty() {
    return super.isEmpty();
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(layoutId, null);
    }

    final String name = getItem(position);

    TextView nameView = (TextView) convertView.findViewById(R.id.name);
    ImageView imageView = (ImageView) convertView.findViewById(R.id.picture);
    final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.contact_check_box);

    if (checkBox != null) {
      checkBox.setChecked(isAlreadyAdded(name));

      checkBox.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (checkBox.isChecked() && isAlreadyAdded(name)) {
            contactIndexesToRemove.remove(position);
          } else if (checkBox.isChecked() && !isAlreadyAdded(name)) {
            contactIndexesToAdd.add(position);
          } else if (!checkBox.isChecked() && isAlreadyAdded(name)) {
            contactIndexesToRemove.add(position);
          } else if (!checkBox.isChecked() && !isAlreadyAdded(name)) {
            contactIndexesToAdd.remove(position);
          }
        }
      });
    }
    nameView.setText(name);
    if (imageUriStrings.get(position) != null) {
      imageView.setImageURI(Uri.parse(imageUriStrings.get(position)));
    } else {
      imageView.setImageResource(R.mipmap.ic_launcher);
    }
    return convertView;
  }

  protected ArrayList<Integer> getContactIndexesToAdd() {
    Collections.sort(contactIndexesToAdd);
    return contactIndexesToAdd;
  }

  protected ArrayList<Integer> getContactIndexesToRemove() {
    Collections.sort(contactIndexesToRemove);
    return contactIndexesToRemove;
  }

  protected boolean isAlreadyAdded(String name) {
    return false;
  }
}
