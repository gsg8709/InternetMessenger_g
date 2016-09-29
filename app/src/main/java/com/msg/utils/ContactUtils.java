package com.msg.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;

import com.msg.bean.ContactGroup;
import com.msg.bean.Contacts;
import com.msg.bean.Group;
import com.msg.server.IMStorageDataBase;

@SuppressWarnings("deprecation")
public class ContactUtils {

	/**
	 * 获取手机通讯录分组
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<Group> queryGroups(Context context) {
		ArrayList<Group> gs = new ArrayList<Group>();
		Cursor cursor = context.getContentResolver().query(Groups.CONTENT_URI,
				null, null, null, null);
		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(Groups.TITLE));
			Long id = cursor.getLong(cursor.getColumnIndex(Groups._ID));
			gs.add(new Group(id, name));
		}
		return gs;
	}

	public static ArrayList<Group> removeDuplicateWithOrder2(
			ArrayList<Group> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if ((list.get(j).getName().equals(list.get(i).getName()))) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	/**
	 * 添加通讯录分组
	 * 
	 * @param context
	 * @param name
	 */
	public static void insertGroup(Context context, String name) {
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, name);
		context.getContentResolver().insert(Groups.CONTENT_URI, values);
	}

	/**
	 * 刪除通讯录分组
	 * 
	 * @param context
	 * @param name
	 */
	public static void deleteGroup(Context context, long groupId) {
		context.getContentResolver().delete(
				Uri.parse(Groups.CONTENT_URI + "?"
						+ ContactsContract.CALLER_IS_SYNCADAPTER + "=true"),
				Groups._ID + "=" + groupId, null);
	}

	/**
	 * 重命名通讯录分组
	 * 
	 * @param context
	 * @param name
	 */
	public static void updateGroup(Context context, int groupId, String name) {
		Uri uri = ContentUris.withAppendedId(Groups.CONTENT_URI, groupId);
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, name);
		context.getContentResolver().update(uri, values, null, null);
	}

	/**
	 * 添加成员到指定组
	 * 
	 * @param context
	 * @param personId
	 * @param l
	 */
	public static void insertContactByGroup(Context context, int personId,
			long l) {
		ContentValues values = new ContentValues();
		values.put(
				ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
				personId);
		values.put(
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
				l);
		values.put(
				ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
				ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
		context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI,
				values);
	}

	/**
	 * 删除成员到指定组
	 * 
	 * @param context
	 * @param personId
	 * @param groupId
	 */
	public static void deleteContactByGroup(Context context, int personId,
			long groupId) {
		context.getContentResolver()
				.delete(ContactsContract.Data.CONTENT_URI,
						ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID
								+ "=? and "
								+ ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
								+ "=? and "
								+ ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE
								+ "=?",
						new String[] {
								"" + personId,
								"" + groupId,
								ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE });
	}

	/**
	 * 添加联系人
	 * 
	 * @param context
	 * @param name
	 */
	public static int insertContact(Context context, String name, String phone) {
		ContentValues values = new ContentValues();
		ContentResolver resolver = context.getContentResolver();
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.DISPLAY_NAME, name);
		context.getContentResolver().insert(Data.CONTENT_URI, values);

		values.put(Phone.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, phone);
		context.getContentResolver().insert(Data.CONTENT_URI, values);

		values.put(Phone.RAW_CONTACT_ID, rawContactId);
		values.put(
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
				0);
		return (int) rawContactId;
	}

	/**
	 * 更新联系人
	 * 
	 * @param context
	 * @param cid
	 * @param name
	 * @param phone
	 */
	public static void updateContact(Context context, int cid, String name,
			String phone) {
		ContentValues values = new ContentValues();
		values.put(Phone.NUMBER, phone);
		String Where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND "
				+ Data.MIMETYPE + " = ?";
		String[] WhereParams = new String[] { cid + "",
				Phone.CONTENT_ITEM_TYPE, };
		String[] WhereParams_2 = new String[] { cid + "",
				StructuredName.CONTENT_ITEM_TYPE, };
		context.getContentResolver().update(Data.CONTENT_URI, values, Where,
				WhereParams);

		values.clear();
		values.put(StructuredName.DISPLAY_NAME, name);
		context.getContentResolver().update(Data.CONTENT_URI, values, Where,
				WhereParams_2);
	}

	/**
	 * 删除联系人
	 * 
	 * @param context
	 * @param name
	 */
	public static void delContact(Context context, String name) {
		Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },
				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { name }, null);
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		if (cursor.moveToFirst()) {
			do {
				long Id = cursor.getLong(cursor
						.getColumnIndex(Data.RAW_CONTACT_ID));
				ops.add(ContentProviderOperation
						.newDelete(
								ContentUris.withAppendedId(
										RawContacts.CONTENT_URI, Id)).build());
				try {
					context.getContentResolver().applyBatch(
							ContactsContract.AUTHORITY, ops);
				} catch (Exception e) {
				}
			} while (cursor.moveToNext());
			cursor.close();
		}
	}

	/**
	 * 获取某个分组下的 所有联系人信息 思路：通过组的id 去查询 RAW_CONTACT_ID, 通过RAW_CONTACT_ID去查询联系人
	 * 要查询得到 data表的Data.RAW_CONTACT_ID字段
	 * 
	 * @param groupId
	 * @param name
	 * @return
	 */
	public static ArrayList<Contacts> getAllContactsByGroupId(Context context,
			Long groupId, String name) {

		String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID, };

		String RAW_CONTACTS_WHERE = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
				+ "=?"
				+ " and "
				+ ContactsContract.Data.MIMETYPE
				+ "="
				+ "'"
				+ ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE
				+ "'";

		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Data.CONTENT_URI, RAW_PROJECTION,
				RAW_CONTACTS_WHERE, new String[] { groupId + "" }, "data1 asc");
		ArrayList<Contacts> contactList = new ArrayList<Contacts>();
		while (cursor.moveToNext()) {
			int col = cursor.getColumnIndex("raw_contact_id");
			int raw_contact_id = cursor.getInt(col);
			Contacts ce = new Contacts();
			ce.setContactId(raw_contact_id);
			ce.setGROUPNAME(name);
			ce.setSEX(1);
			Uri dataUri = Uri.parse("content://com.android.contacts/data");
			Cursor dataCursor = context.getContentResolver().query(dataUri,
					null, "raw_contact_id=?",
					new String[] { raw_contact_id + "" }, null);

			while (dataCursor.moveToNext()) {
				String data1 = dataCursor.getString(dataCursor
						.getColumnIndex("data1"));
				String mime = dataCursor.getString(dataCursor
						.getColumnIndex("mimetype"));
				if ("vnd.android.cursor.item/phone_v2".equals(mime)) {
					ce.setTEL(data1);
				} else if ("vnd.android.cursor.item/name".equals(mime)) {
					ce.setNAME(data1);
				}
			}
			dataCursor.close();
			contactList.add(ce);
			ce = null;
		}
		cursor.close();
		return contactList;
	}

	/**
	 * 获取机器所有联系人
	 * 
	 * @param activity
	 * @return
	 */
	public static ArrayList<Contacts> getAllContacts(Activity activity) {
		ArrayList<Contacts> arrayList = new ArrayList<Contacts>();
		Cursor cur = activity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (cur.moveToFirst()) {
			do {
				Contacts samContact = new Contacts();
				int idColumn = cur
						.getColumnIndex(ContactsContract.Contacts._ID);
				int displayNameColumn = cur
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				String contactId = cur.getString(idColumn);
				String disPlayName = cur.getString(displayNameColumn);
				samContact.contactId = Integer.parseInt(contactId);
				samContact.NAME = disPlayName;
				samContact.SEX = 1;
				int phoneCount = cur
						.getInt(cur
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (phoneCount < 1) {
					continue;
				}
				Cursor phones = activity.getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
				if (phones.moveToFirst()) {
					do {
						String phoneNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						int phoneType = phones
								.getInt(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
							samContact.TEL = phoneNumber;
							arrayList.add(samContact);
							break;
						}
					} while (phones.moveToNext());
				}
				phones.close();

			} while (cur.moveToNext());
		}
		cur.close();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor cur2 = activity.getContentResolver().query(
				uri,
				null,
				null,
				null,
				ContactsContract.Contacts.DISPLAY_NAME
						+ " COLLATE LOCALIZED ASC");
		if (cur2.moveToFirst()) {
			do {
				try {
					Contacts samContact = new Contacts();
					int displayNameColumn = cur2.getColumnIndex(People.NAME);
					int phoneColumn = cur2.getColumnIndex(People.NUMBER);
					samContact.NAME = cur2.getString(displayNameColumn);
					samContact.SEX = 1;
					if (samContact.NAME == null) {
						continue;
					}
					samContact.TEL = cur2.getString(phoneColumn);
					if (samContact.TEL == null) {
						continue;
					}
					arrayList.add(samContact);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (cur2.moveToNext());
		}
		cur2.close();
		return arrayList;
	}

	public static ArrayList<Contacts> removeDuplicateWithOrder(
			ArrayList<Contacts> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if ((list.get(j).getNAME().equals(list.get(i).getNAME()))
						&& (list.get(j).getTEL().equals(list.get(i).getTEL()))) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	/**
	 * 读取手机联系人信息
	 * 
	 * @param activity
	 * @param handler
	 */
	public static void handlerMacContacts(final Activity activity,
			final Handler handler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				String uid = UserManager.getUserinfo(activity).getUID();
				ArrayList<ContactGroup> cgs = new ArrayList<ContactGroup>();
				ArrayList<Group> gs = ContactUtils.queryGroups(activity);
				IMStorageDataBase imdb = new IMStorageDataBase(activity);
				imdb.delUserTable();

				ArrayList<Contacts> all_contacts = getAllContacts(activity);
				all_contacts = removeDuplicateWithOrder(all_contacts);
				imdb.saveDefaultFriends(all_contacts, uid);

				for (Group g : gs) {
					Long id = g.getId();
					String name = g.getName();
					if (name.contains("My Contacts")) {
						continue;
					} else if (name.equalsIgnoreCase("Starred in Android")) {
						continue;
					} else if (name.contains("ICE")) {
						continue;
					} else if (name.contains("Friends")
							|| name.contains("friends")
							|| name.contains("Friend")
							|| name.contains("friend")) {
						name = "好友";
					} else if (name.contains("Family")
							|| name.contains("family")) {
						name = "家人";
					} else if (name.equalsIgnoreCase("Coworkers")
							|| name.equalsIgnoreCase("coworkers")) {
						name = "同事";
					}
					imdb.saveGroups(new Group(id, name), uid);
					ArrayList<Contacts> cs = ContactUtils
							.getAllContactsByGroupId(activity, id, name);
					ContactGroup cg = new ContactGroup(g.getId(), name, cs
							.size(), cs);

					if (cs.size() != 0) {
						cgs.add(cg);
						imdb.saveGourpFriends(cs, uid);
					}
				}
				handler.obtainMessage(1).sendToTarget();
			}
		}).start();
	}

	/**
	 * 从本地数据库读取联系人信息
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<ContactGroup> getAllContactsByGroup(Context context) {
		ArrayList<ContactGroup> cgs = new ArrayList<ContactGroup>();
		IMStorageDataBase imdb = new IMStorageDataBase(context);
		ArrayList<Group> allGroups = imdb.getAllGroups();
		for (Group group : allGroups) {
			ArrayList<Contacts> cs = imdb
					.getFriendsByGourpName(group.getName());
			cgs.add(new ContactGroup(group.getId(), group.getName(), cs.size(),
					cs));
		}
		return cgs;
	}
}
