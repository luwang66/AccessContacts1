package com.example.accesscontacts1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button add, query;
	private EditText name, num;
	private ListView result;
	private ContentResolver resolver;
	private LinearLayout title;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		add = (Button) findViewById(R.id.add);
		query = (Button) findViewById(R.id.show);
		name = (EditText) findViewById(R.id.name);
		num = (EditText) findViewById(R.id.num);
		result = (ListView) findViewById(R.id.result);
		title=(LinearLayout)findViewById(R.id.title);
		title.setVisibility(View.INVISIBLE);
		resolver = getContentResolver();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		add.setOnClickListener(myOnClickListener);
		query.setOnClickListener(myOnClickListener);
	}

	private class MyOnClickListener implements OnClickListener {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add:
				addPerson();
				break;
			case R.id.show:
				title.setVisibility(View.VISIBLE);
				ArrayList<Map<String,String>> persons=queryPerson();
				SimpleAdapter adapter=new SimpleAdapter(MainActivity.this,persons, R.layout.result, new String[]{
						"id","name","num"}, new int[]{R.id.personid,R.id.personname,R.id.personnum});
				result.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	}

	public void addPerson() {// �����ϵ��
		String nameStr = name.getText().toString();// ��ȡ��ϵ������
		String numStr = num.getText().toString();// ��ȡ��ϵ�˺���
		ContentValues values = new ContentValues();// ����һ���յ�ContentValues
		// ��RawContacts.CONTENT_URIִ��һ����ֵ���룬Ŀ���ǻ�ȡ���ص�ID�š�
		Uri rawContactUri = resolver.insert(RawContacts.CONTENT_URI, values);
		long contactId = ContentUris.parseId(rawContactUri);// �õ�����ϵ�˵�ID��
		System.out.println(contactId);
		values.clear();
		values.put(Data.RAW_CONTACT_ID, contactId);// ����ID��
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);// ��������
		values.put(StructuredName.GIVEN_NAME, nameStr);// ��������
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI,
				values);// ����ϵ��Uri�����ϵ������
		values.clear();
		values.put(Data.RAW_CONTACT_ID, contactId);// ����ID��
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);// ��������
		values.put(Phone.NUMBER, numStr);// ���ú���
		values.put(Phone.TYPE, Phone.TYPE_MOBILE);// ���õ绰����
		resolver.insert(android.provider.ContactsContract.Data.CONTENT_URI,
				values);// ����ϵ�˵绰����Uri��ӵ绰����
		Toast.makeText(MainActivity.this, "��ϵ��������ӳɹ���", 1000).show();
	}

	public ArrayList<Map<String, String>> queryPerson() {
		ArrayList<Map<String, String>> detail = new ArrayList<Map<String, String>>();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		while (cursor.moveToNext()) {
			Map<String, String> person = new HashMap<String, String>();
			String personId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			String name = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			person.put("id", personId);
			person.put("name", name);
			Cursor nums = resolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ personId, null, null);
			if(nums.moveToNext()){
				String num = nums.getString(nums
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				person.put("num",num);
			}
			nums.close();			
			detail.add(person);
		}
		cursor.close();
		System.out.println(detail);
		return detail;
	}
}
