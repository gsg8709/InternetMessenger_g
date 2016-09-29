package com.msg.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.msg.bean.Contacts;
import com.msg.bean.Group;
import com.msg.bean.ShowMsg;
import com.msg.common.Configs;
import com.msg.utils.AuxiliaryUtils;
import com.msg.utils.ContactUtils;
import com.msg.utils.NotificationUtils;

/**
 * 数据库操作类
 * 
 * @author gongchao
 * 
 */
public class IMStorageDataBase {
	private IMDBOpenHelper dbOpenHelper;

	public IMStorageDataBase(Context context) {
		this.dbOpenHelper = new IMDBOpenHelper(context);
	}

	/**
	 * 清空表
	 */
	public void delTable() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from friends");
			db.execSQL("delete from messages");
			db.execSQL("delete from groups");
			db.close();
		}
	}

	/**
	 * 清空表
	 */
	public void delUserTable() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from groups");
			db.close();
		}
	}

	/**
	 * 保存分组信息
	 * 
	 * @param group
	 */
	public void saveGroups(Group group, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"insert into groups (GROUPNAME,UID,UPDATEDATA) values(?,?,?)",
					new Object[] { group.getName(), uid,
							System.currentTimeMillis() });
			db.close();
		}
	}

	/**
	 * 更新分组信息
	 * 
	 * @param group
	 */
	public void updateGroups(String name, Long id, String old) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("UPDATE groups SET GROUPNAME  = ? where _id = ?",
					new Object[] { name, id });
			db.execSQL("UPDATE friends SET GROUPNAME  = ? where GROUPNAME = ?",
					new Object[] { name, old });
			db.close();
		}
	}

	/**
	 * 删除分组
	 * 
	 * @param username
	 */
	public void delGroups(String gid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from groups where _id=?", new Object[] { gid });
			db.close();
		}
	}

	/**
	 * 获取分组
	 * 
	 * @param username
	 */
	public ArrayList<Group> getAllGroups() {
		ArrayList<Group> gps = new ArrayList<Group>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from groups", null);
			while (cursor.moveToNext()) {
				long GID = Long.parseLong(cursor.getString(0));
				String GROUPNAME = cursor.getString(1);
				gps.add(new Group(GID, GROUPNAME));
			}
			cursor.close();
			db.close();
		}
		return gps;
	}

	/**
	 * 保存消息
	 * 
	 * @param msg
	 */
	public void saveMsg(ShowMsg msg) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"insert into messages "
							+ "(fromJid,fromName,toJid,msgType,msgOrient,msgOpened,imageUrl,imagePath,voiceUrl,voicePath,title,msg,creationData,miniPic)"
							+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { msg.getFromJid(), msg.getFromName(),
							msg.getToJid(), msg.getMsgType(),
							msg.getMsgOrient(), msg.getMsgOpened(),
							msg.getImageUrl(), msg.getImagePath(),
							msg.getVoiceUrl(), msg.getVoicePath(),
							msg.getTitle(), msg.getMsg(),
							msg.getCreationData(),msg.getMiniPic() });
			db.execSQL("UPDATE friends SET UPDATEDATA  = ? where TEL = ?",
					new Object[] { msg.getCreationData(), msg.getFromJid() });
			db.close();
		}
	}

	/**
	 * 更新消息为已读
	 * 
	 * @param msg
	 */
	public void updateMsgForRead(ShowMsg msg) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("UPDATE messages SET msgOpened = 0 WHERE fromJid = ?",
					new Object[] { msg.getFromJid() });
			db.close();
		}
	}

	/**
	 * 更新消息列表联系人
	 * 
	 * @param msg
	 */
	public void updateMsgForFromJid(String jid, String name, String oldJid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"UPDATE messages SET fromJid = ? , fromName = ?  WHERE fromJid = ?",
					new Object[] { jid, name, oldJid });
			db.close();
		}
	}

	/**
	 * 保存好友
	 * 
	 * @param user
	 */
	public void saveFriend(Contacts user, String uid) {
		try {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			if (db.isOpen()) {
				Cursor cursor = db.rawQuery(
						"select * from friends where _id=?",
						new String[] { user.getContactId() + "" });
				if (cursor.moveToFirst()) {
					String CID = cursor.getString(0);
					String NAME = cursor.getString(1);
					String PINYIN = cursor.getString(2);
					String FIRSTPY = cursor.getString(3);
					String UID = cursor.getString(4);
					String TEL = cursor.getString(5);
					int SEX = cursor.getInt(6);
					String BRITHDAY = cursor.getString(7);
					String COMPANY = cursor.getString(8);
					String BRANCH = cursor.getString(9);
					String JOB = cursor.getString(10);
					String GROUPNAME = cursor.getString(11);
					String CTIME = cursor.getString(12);
					String UPDATEDATA = cursor.getString(13);
					int SHOW = cursor.getInt(14);
					Contacts c_old = new Contacts(CID, PINYIN, FIRSTPY, NAME,
							UID, TEL, GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY,
							BRANCH, JOB, UPDATEDATA, SHOW);
					updateGroup(c_old, user, uid);
					delFriend(user);
					saveFriend(user, uid);
				} else {
					if (!user.getGROUPNAME().equals("联系人")) {
						db.execSQL(
								"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
								new Object[] {
										user.getNAME(),
										AuxiliaryUtils.getFullSpell(user
												.getNAME()),
										AuxiliaryUtils.getFirstSpell(user
												.getNAME()), uid,
										user.getTEL(), user.getGROUPNAME(),
										user.getCTIME(), user.getSEX(),
										user.getBRITHDAY(), user.getCOMPANY(),
										user.getBRANCH(), user.getJOB(),
										System.currentTimeMillis(), 0 });
					}

					db.execSQL(
							"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] {
									user.getNAME(),
									AuxiliaryUtils.getFullSpell(user.getNAME()),
									AuxiliaryUtils.getFirstSpell(user.getNAME()),
									uid, user.getTEL(), "联系人", user.getCTIME(),
									user.getSEX(), user.getBRITHDAY(),
									user.getCOMPANY(), user.getBRANCH(),
									user.getJOB(), System.currentTimeMillis(),
									0 });
				}
				cursor.close();
				db.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateGroup(Contacts old_c, Contacts new_c, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where NAME=? AND TEL=? AND UID=? AND GROUPNAME<>?",
							new String[] { old_c.getNAME(), old_c.getTEL(),
									uid, "联系人" });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String GROUPNAME = cursor.getString(5);
				db.execSQL(
						"UPDATE friends SET NAME = ?,TEL =?,GROUPNAME=?,PINYIN=?  WHERE _id = ?",
						new Object[] { new_c.getNAME(), new_c.getTEL(),
								GROUPNAME,
								AuxiliaryUtils.getFullSpell(new_c.getNAME()),
								CID });
			}
			cursor.close();
			db.close();
		}
	}

	/**
	 * 保存好友
	 * 
	 * @param user
	 */
	public void saveFriendByGroup(Contacts user, String uid) {
		try {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			if (db.isOpen()) {
				Cursor cursor = db
						.rawQuery(
								"select * from friends where NAME=? AND GROUPNAME=? AND TEL=? AND UID=?",
								new String[] { user.getNAME(),
										user.getGROUPNAME(), user.getTEL(), uid });
				if (!cursor.moveToFirst()) {
					db.execSQL(
							"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] {
									user.getNAME(),
									AuxiliaryUtils.getFullSpell(user.getNAME()),
									AuxiliaryUtils.getFirstSpell(user.getNAME()),
									uid, user.getTEL(), user.getGROUPNAME(),
									user.getCTIME(), user.getSEX(),
									user.getBRITHDAY(), user.getCOMPANY(),
									user.getBRANCH(), user.getJOB(),
									System.currentTimeMillis(), 0 });
				}
				cursor.close();
				db.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存多个好友，初始化時候使用
	 * 
	 * @param cg
	 */
	public void saveDefaultFriends(ArrayList<Contacts> users, String uid) {
		try {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			if (db.isOpen()) {
				for (Contacts user : users) {
					db.execSQL(
							"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] {
									user.getNAME(),
									AuxiliaryUtils.getPinYinHeadChar(user.getNAME()),
									AuxiliaryUtils.getPinYinHeadChar(user.getNAME()),
									uid, user.getTEL(), "联系人", user.getCTIME(),
									user.getSEX(), user.getBRITHDAY(),
									user.getCOMPANY(), user.getBRANCH(),
									user.getJOB(), System.currentTimeMillis(),
									0 });
				}
				db.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 保存多个好友
	 * 
	 * @param cg
	 */
	public void saveGourpFriends(ArrayList<Contacts> users, String uid) {
		try {
			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
			if (db.isOpen()) {
				for (Contacts user : users) {
					db.execSQL(
							"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
							new Object[] {
									user.getNAME(),
									AuxiliaryUtils.getFullSpell(user.getNAME()),
									AuxiliaryUtils.getFirstSpell(user.getNAME()),
									uid, user.getTEL(), user.getGROUPNAME(),
									user.getCTIME(), user.getSEX(),
									user.getBRITHDAY(), user.getCOMPANY(),
									user.getBRANCH(), user.getJOB(),
									System.currentTimeMillis(), 0 });
				}
				db.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除好友
	 * 
	 * @param username
	 */
	public void delFriend(Contacts c) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from friends where _id=?",
					new Object[] { c.getContactId() });
			db.close();
		}
	}

	/**
	 * 删除好友
	 * 
	 * @param username
	 */
	public void delFriendByGroup(String gname, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from friends where GROUPNAME=? AND UID=?",
					new Object[] { gname, uid });
			db.close();
		}
	}

	/**
	 * 删除好友
	 * 
	 * @param username
	 */
	public void delFriendByNameAndTel(Contacts c, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from friends where NAME=? AND TEL=? AND UID=?",
					new Object[] { c.getNAME(), c.getTEL(), uid });
			db.close();
		}
	}

	/**
	 * 删除多个好友
	 * 
	 * @param users
	 */
	public void delFriends(List<Contacts> users) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < users.size(); i++) {
				String cid = users.get(i).getContactId() + "";
				db.execSQL("delete from friends where _id=?",
						new Object[] { cid });
				db.execSQL("delete from messages where fromJid=? or toJid = ?",
						new Object[] { cid, cid });
			}
			db.close();
		}
	}

	/**
	 * 获取所有好友
	 * 
	 * @return
	 */
	public ArrayList<Contacts> getAllFriendForMsg(String uid) {
		ArrayList<Contacts> users = new ArrayList<Contacts>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where GROUPNAME='联系人' AND UID=? order by UPDATEDATA DESC",
							new String[] { uid });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				users.add(new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW));
			}
			cursor.close();
			db.close();
		}
		return ContactUtils.removeDuplicateWithOrder(users);
	}

	/**
	 * 获取所有好友
	 * 
	 * @return
	 */
	public ArrayList<Contacts> getAllFriend(String uid) {
		ArrayList<Contacts> users = new ArrayList<Contacts>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where GROUPNAME='联系人' AND UID=? AND SHOW=0 order by PINYIN ASC",
							new String[] { uid });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				users.add(new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW));
			}
			cursor.close();
			db.close();
		}
		return ContactUtils.removeDuplicateWithOrder(users);
	}

	/**
	 * 模糊查询好友
	 * 
	 * @return
	 */
	public ArrayList<Contacts> getAllFriend(String keyword, String uid) {
		ArrayList<Contacts> users = new ArrayList<Contacts>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where UID=? AND TEL like ? or NAME like ? order by PINYIN desc",
							new String[] { uid, keyword + "%", keyword + "%" });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				users.add(new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW));
			}
			cursor.close();
			db.close();
		}
		return users;
	}

	/**
	 * 获取所有好友的最后一条所有消息
	 * 
	 * @return
	 */
	public ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> getAllMessage(
			String uid) {
		ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>> mMessages = new ArrayList<HashMap<Contacts, ArrayList<ShowMsg>>>();
		ArrayList<Contacts> friends = getAllFriendForMsg(uid);
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			for (int i = 0; i < friends.size(); i++) {
				HashMap<Contacts, ArrayList<ShowMsg>> map = new HashMap<Contacts, ArrayList<ShowMsg>>();
				Contacts user = friends.get(i);
				ArrayList<ShowMsg> msgs = new ArrayList<ShowMsg>();
				Cursor cursor = db
						.rawQuery(
								"select * from messages where fromJid = ? AND fromName = ? order by creationData desc",
								new String[] { user.getTEL(), user.getNAME() });
				while (cursor.moveToNext()) {
					String fromJid = cursor.getString(1);
					String fromName = cursor.getString(2);
					String toJid = cursor.getString(3);
					int msgType = cursor.getInt(4);
					int msgOrient = cursor.getInt(5);
					int msgOpened = cursor.getInt(6);
					String imageUrl = cursor.getString(7);
					String imagePath = cursor.getString(8);
					String voiceUrl = cursor.getString(9);
					String voicePath = cursor.getString(10);
					String title = cursor.getString(11);
					String msg = cursor.getString(12);
					String creationData = cursor.getString(13);
					String miniPic = cursor.getString(14);
					msgs.add(new ShowMsg(fromJid, fromName, toJid, msgType,
							msgOrient, msgOpened, imageUrl, imagePath,
							voiceUrl, voicePath, title, msg, creationData,miniPic));
				}
				if (msgs.size() != 0) {
					map.put(user, msgs);
					mMessages.add(map);
				}
				cursor.close();
			}
			db.close();
		}
		return mMessages;
	}

	/**
	 * 获取所有好友的最后一条所有消息
	 * 
	 * @return
	 */
	public int getUnreadMessageCount(String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		Cursor cursor = db
				.rawQuery(
						"select * from messages where fromJid = ? OR toJid = ? order by creationData desc",
						new String[] { uid, uid });
		while (cursor.moveToNext()) {
			int msgOpened = cursor.getInt(6);
			if (msgOpened == Configs.MSG_UNREADED) {
				count++;
			}
		}
		cursor.close();
		db.close();
		return count;
	}

	/**
	 * 查询指定联系人的所有消息
	 * 
	 * @param username
	 * @return
	 */
	public ArrayList<ShowMsg> getMessageForJid(String tel, int page) {
		ArrayList<ShowMsg> msgs = new ArrayList<ShowMsg>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		int count = 0;
		String num = "";
		if (db.isOpen()) {
			Cursor c = db.rawQuery(
					"select count(*) from messages where fromJid = ?",
					new String[] { tel });
			if (c.moveToFirst())
				count = c.getInt(0);
			int maxPage = (count / 10) + ((count % 10 != 0) ? 1 : 0);
			if (page <= maxPage) {
				num = String.valueOf(count);
				if (count > 0) {
					Cursor cursor = db
							.rawQuery(
									"select * from messages where fromJid = ? limit ?-10*?,?-10*?",
									new String[] { tel, num,
											String.valueOf(page), num,
											String.valueOf(page - 1) });
					while (cursor.moveToNext()) {
						String fromJid = cursor.getString(1);
						String fromName = cursor.getString(2);
						String toJid = cursor.getString(3);
						int msgType = cursor.getInt(4);
						int msgOrient = cursor.getInt(5);
						int msgOpened = cursor.getInt(6);
						String imageUrl = cursor.getString(7);
						String imagePath = cursor.getString(8);
						String voiceUrl = cursor.getString(9);
						String voicePath = cursor.getString(10);
						String title = cursor.getString(11);
						String msg = cursor.getString(12);
						String creationData = cursor.getString(13);
						String miniPic = cursor.getString(14);
						msgs.add(new ShowMsg(fromJid, fromName, toJid, msgType,
								msgOrient, msgOpened, imageUrl, imagePath,
								voiceUrl, voicePath, title, msg, creationData,miniPic));
					}
					cursor.close();
				}
				c.close();
			}
			db.close();
		}
		return msgs;
	}

	/**
	 * 删除指定用户消息
	 * 
	 * @param username
	 */
	public void delMessageForJid(String cid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from messages where fromJid=?",
					new Object[] { cid });
			db.close();
		}
	}

	/**
	 * 删除指定用户消息
	 * 
	 * @param username
	 */
	public void delMessageForTime(ShowMsg msg) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL(
					"delete from messages where fromJid=? AND creationData=?",
					new Object[] { msg.getFromJid(), msg.getCreationData() });
			db.close();
		}
	}

	/**
	 * 查询指定好友名称
	 * 
	 * @param jid
	 * @return
	 */
	public String getFriendNameOrInsertContact(String tel, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where TEL = ? AND GROUPNAME = '联系人' AND SHOW = 0",
							new String[] { tel });
			if (cursor.moveToFirst()) {
				tel = cursor.getString(1);
			} else {
				db.execSQL(
						"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { tel, tel, tel, uid, tel, "联系人",
								System.currentTimeMillis(), 1, "", "", "", "",
								System.currentTimeMillis(), 1 });

			}
			cursor.close();
			db.close();
		}
		return tel;
	}

	/**
	 * 查询指定好友名称
	 * 
	 * @param jid
	 * @return
	 */
	public void initWelcome(Context context, String uid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where TEL = ? AND GROUPNAME = '联系人' AND SHOW = 1",
							new String[] { "18888888" });
			if (!cursor.moveToFirst()) {
				db.execSQL(
						"INSERT INTO friends (NAME,PINYIN,FIRSTPY,UID,TEL,GROUPNAME,CTIME,SEX,BRITHDAY,COMPANY,BRANCH,JOB,UPDATEDATA,SHOW) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[] { "互联信使团队", "hulianxinxituandui",
								"hlxstd", uid, "18888888", "联系人",
								System.currentTimeMillis(), 1, "", "", "", "",
								System.currentTimeMillis(), 1 });
				saveMsg(new ShowMsg("18888888", "互联信使团队", uid,
						Configs.MSG_TYPE_INTER_SMS, Configs.RECEIVE_MSG,
						Configs.MSG_UNREADED, null, null, null, null, null,
						"欢迎您使用互联信使，点击右上角的u+图标选择一个联系人给他发一条附带有声照片的短信试试看吧！如果您有问题可以点击工具-设置-使用帮助来查询。",

						System.currentTimeMillis() + "",""));
				NotificationUtils.notifi(context);
			}
			cursor.close();
			db.close();
		}
	}

	/**
	 * 查询指定好友信息
	 * 
	 * @param jid
	 * @return
	 */
	public Contacts getFriendInfo(String jid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Contacts user = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from friends where TEL = ?",
					new String[] { jid });
			if (cursor.moveToFirst()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				user = new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW);
			}
			cursor.close();
			db.close();
		}
		return user;
	}

	/**
	 * 查询指定好友信息
	 * 
	 * @param jid
	 * @return
	 */
	public Contacts getFriendInfo(int cid) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Contacts user = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from friends where _id = ?",
					new String[] { cid + "" });
			if (cursor.moveToFirst()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				user = new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW);
			}
			cursor.close();
			db.close();
		}
		return user;
	}

	/**
	 * 模糊查询好友
	 * 
	 * select * from friends where uid=13601156965 and (PINYIN like('%SZ%') or
	 * PINYIN2 like('%SZ%') order by PINYIN DESC
	 * 
	 * @param keyword
	 * @return
	 */
	public ArrayList<Contacts> getFriendsByKeyword(String uid, String keyword) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ArrayList<Contacts> friends = new ArrayList<Contacts>();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where uid=? and SHOW=0 and (PINYIN like(?) or FIRSTPY like(?)) order by PINYIN DESC",
							new String[] { uid, "%" + keyword + "%",
									"%" + keyword + "%" });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				friends.add(new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW));
			}
			cursor.close();
			db.close();
		}
		return friends;
	}

	/**
	 * 分组名称查询好友
	 * 
	 * @param keyword
	 * @return
	 */
	public ArrayList<Contacts> getFriendsByGourpName(String groups_name) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ArrayList<Contacts> friends = new ArrayList<Contacts>();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from friends where GROUPNAME = ? AND SHOW = 0 order by PINYIN ASC",
							new String[] { groups_name });
			while (cursor.moveToNext()) {
				String CID = cursor.getString(0);
				String NAME = cursor.getString(1);
				String PINYIN = cursor.getString(2);
				String FIRSTPY = cursor.getString(3);
				String UID = cursor.getString(4);
				String TEL = cursor.getString(5);
				int SEX = cursor.getInt(6);
				String BRITHDAY = cursor.getString(7);
				String COMPANY = cursor.getString(8);
				String BRANCH = cursor.getString(9);
				String JOB = cursor.getString(10);
				String GROUPNAME = cursor.getString(11);
				String CTIME = cursor.getString(12);
				String UPDATEDATA = cursor.getString(13);
				int SHOW = cursor.getInt(14);
				friends.add(new Contacts(CID, PINYIN, FIRSTPY, NAME, UID, TEL,
						GROUPNAME, CTIME, SEX, BRITHDAY, COMPANY, BRANCH, JOB,
						UPDATEDATA, SHOW));
			}
			cursor.close();
			db.close();
		}
		return friends;
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.close();
		}
	}
}
