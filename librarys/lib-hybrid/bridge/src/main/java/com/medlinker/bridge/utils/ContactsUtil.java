package com.medlinker.bridge.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @time 2015/11/12 16:10
 */
public class ContactsUtil {
    private static ContactsUtil mInstance;

    public static ContactsUtil getInstance() {
        if (mInstance == null) {
            synchronized (ContactsUtil.class) {
                if (mInstance == null) {
                    mInstance = new ContactsUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     *
     * @param context
     * @return
     */
    public List<ContactsEntity> readContactsList(Context context) {
        List<ContactsEntity> contactsEntitieList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
        if (cursor == null) {
            return contactsEntitieList;
        }
        int contactIdIndex = 0;
        int nameIndex = 0;
        if (cursor.getCount() > 0) {
            contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        }
        ContactsEntity contactsEntity = null;
        Cursor phoneCursor = null;
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(contactIdIndex);
            String name = cursor.getString(nameIndex);
            // 查找联系人的电话信息
            phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            if (phoneCursor == null) {
                continue;
            }
            int phoneIndex = 0;
            if (phoneCursor.getCount() > 0) {
                phoneIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                while (phoneCursor.moveToNext()) {
                    String phoneNumber = phoneCursor.getString(phoneIndex);
                    phoneNumber = phoneNumber.replace(" ", "");

                    contactsEntity = new ContactsEntity();
                    contactsEntity.setId(contactId);
                    contactsEntity.setName(name);
                    contactsEntity.setPhoneNumber(phoneNumber);

                    contactsEntitieList.add(contactsEntity);
                }
            } else {
                // 用户只存了名字
                contactsEntity = new ContactsEntity();
                contactsEntity.setId(contactId);
                contactsEntity.setName(name);
                contactsEntity.setPhoneNumber("");

                contactsEntitieList.add(contactsEntity);
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (phoneCursor != null && !phoneCursor.isClosed()) {
            phoneCursor.close();
        }
        return contactsEntitieList;
    }

    /**
     *
     * @param context
     * @return
     */
    public List<String> readContactsMobileList(Context context) {
        List<String> phoneNumberList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, null, null);
        int phoneIndex = 0;
        if (cursor != null && cursor.getCount() > 0) {
            phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(phoneIndex);
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("+86", "");
                phoneNumberList.add(phoneNumber);
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return phoneNumberList;
    }

    /**
     *
     * @param context
     * @return
     */
    public HashMap<String, String> readContacts(Context context) {
        HashMap<String, String> contactsMap = new HashMap<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, null, null);
        int phoneIndex = 0;
        int nameIndex = 0;
        if (cursor != null && cursor.getCount() > 0) {
            phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                String phoneNumber = cursor.getString(phoneIndex);
                String name = cursor.getString(nameIndex);
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("+86", "");
                contactsMap.put(phoneNumber, name);
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactsMap;
    }

    public class ContactsEntity {
        /**
         * 联系人ID
         */
        private String id;
        /**
         * 联系人名称
         */
        private String name;
        /**
         * 联系人手机号
         */
        private String phoneNumber;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        @Override
        public String toString() {
            return "ContactsEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    '}';
        }
    }
}
