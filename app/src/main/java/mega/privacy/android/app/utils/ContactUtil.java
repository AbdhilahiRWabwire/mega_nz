package mega.privacy.android.app.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import mega.privacy.android.app.DatabaseHandler;
import mega.privacy.android.app.MegaContactAdapter;
import mega.privacy.android.app.MegaContactDB;
import mega.privacy.android.app.lollipop.ManagerActivityLollipop;
import mega.privacy.android.app.lollipop.managerSections.ContactsFragmentLollipop;
import nz.mega.sdk.MegaApiJava;
import nz.mega.sdk.MegaStringList;
import nz.mega.sdk.MegaStringMap;
import nz.mega.sdk.MegaUser;
import mega.privacy.android.app.MegaApplication;

import static mega.privacy.android.app.utils.BroadcastConstants.*;
import static mega.privacy.android.app.utils.TextUtil.*;
import static mega.privacy.android.app.utils.LogUtil.*;

public class ContactUtil {

    public static MegaContactDB getContactDB(long contactHandle) {
        MegaApplication app = MegaApplication.getInstance();
        if (app != null) {
            DatabaseHandler dbH = app.getDbH();
            return dbH.findContactByHandle(String.valueOf(contactHandle));
        }
        return null;
    }

    public static String getMegaUserNameDB(MegaUser user) {
        if (user == null) return null;
        String nameContact = getContactNameDB(user.getHandle());
        if (nameContact != null) return nameContact;
        return user.getEmail();
    }

    public static String getContactNameDB(MegaContactDB contactDB) {
        String nicknameText = contactDB.getNickname();
        if (nicknameText != null) {
            return nicknameText;
        }

        String firstNameText = contactDB.getName();
        String lastNameText = contactDB.getLastName();

        String emailText = contactDB.getMail();
        String nameResult = buildFullName(firstNameText, lastNameText, emailText);
        return nameResult;
    }

    public static String getContactNameDB(long contactHandle) {
        MegaContactDB contactDB = getContactDB(contactHandle);
        if (contactDB != null) return getContactNameDB(contactDB);
        return null;
    }

    public static String getNicknameContact(long contactHandle) {
        MegaContactDB contactDB = getContactDB(contactHandle);
        if (contactDB == null) return null;
        String nicknameText = contactDB.getNickname();
        return nicknameText;
    }

    public static String buildFullName(String name, String lastName, String mail) {
        String fullName = "";
        if (!isTextEmpty(name)) {
            fullName = name;
            if (!isTextEmpty(lastName)) {
                fullName = fullName + " " + lastName;
            }
        } else if (!isTextEmpty(lastName)) {
            fullName = lastName;
        } else if (!isTextEmpty(mail)) {
            fullName = mail;
        }
        return fullName;
    }

    public static String getFirstNameDB(long contactHandle) {
        MegaContactDB contactDB = getContactDB(contactHandle);
        if (contactDB != null) {
            String nicknameText = contactDB.getNickname();
            if (nicknameText != null) return nicknameText;

            String firstNameText = contactDB.getName();
            if (!isTextEmpty(firstNameText)) return firstNameText;

            String lastNameText = contactDB.getLastName();
            if (!isTextEmpty(lastNameText)) return lastNameText;

            String emailText = contactDB.getMail();
            if (!isTextEmpty(emailText)) return emailText;
        }
        return "";
    }

    public static void updateDBNickname(MegaApiJava api, DatabaseHandler dbH, Context context, MegaStringMap map) {
        ArrayList<MegaContactAdapter> contactsDB = getContactsDBList(api);
        if (contactsDB == null || contactsDB.isEmpty()) return;
        //No nicknames
        if (map == null || map.size() == 0) {
            for (int i = 0; i < contactsDB.size(); i++) {
                long contactDBHandle = contactsDB.get(i).getMegaUser().getHandle();
                String nickname = getNicknameContact(contactDBHandle);
                if (nickname != null) {
                    dbH.setContactNickname(null, contactDBHandle);
                    notifyNicknameUpdate(context, contactDBHandle);
                }
            }
            return;
        }

        //Some nicknames
        MegaStringList listHandles = map.getKeys();
        for (int i = 0; i < contactsDB.size(); i++) {
            long contactDBHandle = contactsDB.get(i).getMegaUser().getHandle();
            String newNickname = null;
            for (int j = 0; j < listHandles.size(); j++) {
                long userHandle = MegaApiJava.base64ToUserHandle(listHandles.get(j));
                if (contactDBHandle == userHandle) {
                    newNickname = getNewNickname(map, listHandles.get(j));
                    break;
                }
            }
            String oldNickname = contactsDB.get(i).getMegaContactDB().getNickname();
            if ((newNickname == null && oldNickname == null) || (newNickname != null && oldNickname != null && newNickname.equals(oldNickname))) {
                continue;
            } else {
                dbH.setContactNickname(newNickname, contactDBHandle);
                notifyNicknameUpdate(context, contactDBHandle);
            }
        }
    }

    public static void notifyNicknameUpdate(Context context, long userHandle) {
        Intent intent = new Intent(BROADCAST_ACTION_INTENT_FILTER_NICKNAME);
        intent.putExtra(EXTRA_USER_HANDLE, userHandle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static String getNewNickname(MegaStringMap map, String key) {
        String nicknameEncoded = map.get(key);
        try {
            byte[] decrypt = Base64.decode(nicknameEncoded, Base64.DEFAULT);
            return new String(decrypt, StandardCharsets.UTF_8);
        } catch (java.lang.Exception e) {
            logError("Error retrieving new nickname");
            return null;
        }
    }

    private static ArrayList<MegaContactAdapter> getContactsDBList(MegaApiJava api) {
        ArrayList<MegaContactAdapter> visibleContacts = new ArrayList<>();
        ArrayList<MegaUser> contacts = api.getContacts();
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getVisibility() == MegaUser.VISIBILITY_VISIBLE) {
                long contactHandle = contacts.get(i).getHandle();
                MegaContactDB contactDB = getContactDB(contactHandle);
                String fullName = getContactNameDB(contactDB);
                MegaContactAdapter megaContactAdapter = new MegaContactAdapter(contactDB, contacts.get(i), fullName);
                visibleContacts.add(megaContactAdapter);
            }
        }
        return visibleContacts;
    }

    private static void updateView(Context context) {
        if (context != null && context instanceof ManagerActivityLollipop) {
            ContactsFragmentLollipop cFLol = ((ManagerActivityLollipop) context).getContactsFragment();
            if (cFLol != null) {
                cFLol.updateView();
            }
        }
    }

    public static void updateFirstName(Context context, DatabaseHandler dbH, String name, String email) {
        dbH.setContactName(name, email);
        updateView(context);
    }

    public static void updateLastName(Context context, DatabaseHandler dbH, String lastName, String email) {
        dbH.setContactLastName(lastName, email);
        updateView(context);
    }
}
