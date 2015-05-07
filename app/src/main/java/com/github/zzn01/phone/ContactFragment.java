package com.github.zzn01.phone;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ContactFragment extends Fragment {
  private static final String COLOR_KEY = "color_key";
  private static final String TEXT_KEY = "text_key";

  private int mColor;
  private String mText;

  public static ContactFragment newInstance() {
    final ContactFragment fragment = new ContactFragment();

    /*
    Bundle args = new Bundle();
    args.putInt(COLOR_KEY, color);
    args.putString(TEXT_KEY, text);

    fragment.setArguments(args);
    */

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    final Bundle bundle = getArguments();
    if (bundle != null) {
      mColor = bundle.getInt(COLOR_KEY);
      mText = bundle.getString(TEXT_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container, Bundle savedInstanceState) {

    if (container == null) {
      return null;
    }

    final TextView textView = (TextView) inflater.inflate( R.layout.contact_fragment, container, false);

    textView.setBackgroundColor(mColor);
    textView.setText(mText);

    return textView;
  }
}
