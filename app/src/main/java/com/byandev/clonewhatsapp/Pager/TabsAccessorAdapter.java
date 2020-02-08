package com.byandev.clonewhatsapp.Pager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.byandev.clonewhatsapp.Fragment.ChatFragment;
import com.byandev.clonewhatsapp.Fragment.ContactsFragment;
import com.byandev.clonewhatsapp.Fragment.FragmentRequest;
import com.byandev.clonewhatsapp.Fragment.GroupFragment;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

  public TabsAccessorAdapter(@NonNull FragmentManager fm) {
    super(fm);
  }

  @NonNull
  @Override
  public Fragment getItem(int position) {

    switch (position) {
      case 0:
        ChatFragment chatFragment = new ChatFragment();
        return chatFragment;
      case 1:
        GroupFragment groupFragment = new GroupFragment();
        return groupFragment;
      case 2:
        ContactsFragment contactsFragment = new ContactsFragment();
        return contactsFragment;
      case 3:
        FragmentRequest request = new FragmentRequest();
        return  request;

        default:
          return null;
    }
  }

  @Override
  public int getCount() {
    return 4;
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    switch (position) {
      case  0:
        return "Chats";
      case  1:
        return "Groups";
      case  2:
        return "Contacts";
      case  3:
        return "Requests";

      default:
        return null;
    }
  }
}
