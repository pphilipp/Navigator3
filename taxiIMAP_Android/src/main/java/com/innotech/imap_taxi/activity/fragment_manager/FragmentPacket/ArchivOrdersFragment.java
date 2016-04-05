package com.innotech.imap_taxi.activity.fragment_manager.FragmentPacket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.innotech.imap_taxi.activity.NavigatorMenuActivity;
import com.innotech.imap_taxi.activity.fragment_manager.FragmentTransactionManager;
import com.innotech.imap_taxi.adapters.OrdersAdapter;
import com.innotech.imap_taxi.core.OrderManager;
import com.innotech.imap_taxi.datamodel.DispOrder4;
import com.innotech.imap_taxi.datamodel.Order;
import com.innotech.imap_taxi.datamodel.ServerData;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi.network.ConnectionHelper;
import com.innotech.imap_taxi.network.MultiPacketListener;
import com.innotech.imap_taxi.network.OnNetworkPacketListener;
import com.innotech.imap_taxi.network.RequestBuilder;
import com.innotech.imap_taxi.network.Utils;
import com.innotech.imap_taxi.network.packet.GetOrdersResponse;
import com.innotech.imap_taxi.network.packet.Packet;
import com.innotech.imap_taxi.utile.DbArchHelper;
import com.innotech.imap_taxi3.R;

public class ArchivOrdersFragment extends FragmentPacket {
	private static ListView listView_orders;
	private static Button filter, delete, cleanArchive;
	private static TextView orderAmountTxt;
	private static TextView totalTxt;
	private Button back;
	static private Spinner datesSpinner;
	private static CheckBox incl;

	ServerData serv;
	AlertDialog dialog;
	private static List<Order> arch_list, arch_list_new;
	private static boolean isFilter;
	private static List<Long> datesReal;
	private static long date;

	private static AlertDialog alertDialog;
	private static long minVal;
	private static long maxVal;

	private final String TAG = "mOrders";

	LinearLayout actionBar;
	public static TextView dateFrom;
	public static TextView dateTill;
	LinearLayout dateLayout;
	static ImageView searchImg;
	ImageView switchView;
	public static EditText searchBox;
	Boolean searchBoxVisible = false;
	public Boolean longView = true;

	static OrdersAdapter mAdapter;

	int today;
	final static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yy");;

	public ArchivOrdersFragment() {
		super(ARCHIV);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.current_orders_2_new,
				container, false);

		// start tune actionBar elements
		actionBar = NavigatorMenuActivity.arhivLayout;
		dateFrom = (TextView) actionBar.findViewById(R.id.dateFrom);
		dateTill = (TextView) actionBar.findViewById(R.id.dateTill);
		dateLayout = (LinearLayout) actionBar.findViewById(R.id.dateLayout);
		searchBox = (EditText) actionBar.findViewById(R.id.searchBox);
		searchImg = (ImageView) actionBar.findViewById(R.id.searchImg);
		switchView = (ImageView) actionBar.findViewById(R.id.switchView);
		
		// get today date
		final Calendar today = Calendar.getInstance();
		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1);
		final String formatedDate = df.format(today.getTime());

		Typeface t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/BebasNeueRegular.ttf");
		dateFrom.setTypeface(t);
		dateTill.setTypeface(t);
		t = Typeface.createFromAsset(ContextHelper.getInstance()
				.getCurrentContext().getAssets(), "fonts/RobotoCondensed-Regular.ttf");
		searchBox.setTypeface(t);

		if (savedInstanceState != null) {
			Log.d("myLogs", "savedInstance");
			dateFrom.setText(savedInstanceState.getString("dateFrom"));
			dateTill.setText(savedInstanceState.getString("dateTill"));
			searchBox.setText(savedInstanceState.getString("searchText"));
			longView = savedInstanceState.getBoolean("switchList");
		} else {
			dateFrom.setText(df.format(yesterday.getTime()));
			dateTill.setText(formatedDate);
		}

		View.OnClickListener dateClick = new View.OnClickListener() {

			@Override
			public void onClick(final View v) {
				DatePickerDialog dateDialog = new DatePickerDialog(
						ContextHelper.getInstance().getCurrentContext(),
						new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								monthOfYear++;
								SimpleDateFormat dfNew = new SimpleDateFormat(
										"dd.MM.yyyy");
								String pickedDate = dayOfMonth + "."
										+ monthOfYear + "." + year;
								Date chosenDate = null;
								Date cD = null;
								Date till = null;
								Date from = null;
								try {
									chosenDate = dfNew.parse(pickedDate);
									cD = df.parse(pickedDate);
									till = df.parse(dateTill.getText()
											.toString());
									from = df.parse(dateFrom.getText()
											.toString());
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								if (today.getTime().after(chosenDate)) {
									pickedDate = df.format(chosenDate);
								} else {
									pickedDate = formatedDate;
								}
								switch (v.getId()) {
								case R.id.dateFrom:
									if (cD.before(till)) {
										dateFrom.setText(pickedDate);
										from = cD;
									} else {
										dateFrom.setText(df.format(till));
										from = till;
									}
									break;
								case R.id.dateTill:
									if (cD.after(from)) {
										dateTill.setText(pickedDate);
										till = cD;
									} else {
										dateTill.setText(df.format(from));
										till = from;
									}
									Log.d("myLogs", "till");
									break;
								}
								displayOrders();
							}
						}, today.get(Calendar.YEAR), today.get(Calendar.MONTH),
						today.get(Calendar.DAY_OF_MONTH));
				dateDialog.show();

			}
		};

		dateFrom.setOnClickListener(dateClick);
		dateTill.setOnClickListener(dateClick);

		switchView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				longView = !longView;
				mAdapter.setLongView(longView);
				listView_orders.setAdapter(mAdapter);
			}
		});

		searchImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) ContextHelper
						.getInstance().getCurrentContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				if (searchBoxVisible) {
					dateLayout.setVisibility(View.VISIBLE);
					searchBox.setVisibility(View.GONE);
					imm.toggleSoftInput(0, 0);
					searchBox.setText("");
				} else {
					dateLayout.setVisibility(View.GONE);
					searchBox.setVisibility(View.VISIBLE);
					imm.toggleSoftInput(0, 0);
				}
				searchBoxVisible = !searchBoxVisible;
			}
		});

		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				displayOrders();
			}
		});

		// end tune actionBar elements
		arch_list = new ArrayList<Order>();

		mAdapter = new OrdersAdapter(arch_list, ContextHelper.getInstance()
				.getCurrentContext());

		listView_orders = (ListView) myView.findViewById(R.id.listView_orders);
		listView_orders.setAdapter(mAdapter);
		orderAmountTxt = (TextView) myView.findViewById(R.id.ordersAmount);
		totalTxt = (TextView) myView.findViewById(R.id.total);
		Typeface mTypeFace = Typeface.createFromAsset(ContextHelper
				.getInstance().getCurrentContext().getAssets(),
				"fonts/Roboto-Condensed.ttf");
		orderAmountTxt.setTypeface(mTypeFace);
		totalTxt.setTypeface(mTypeFace);

		// cv.put("idorder", orderID);
		// cv.put("fulldescother", ord.getOrderFullDescOther());
		// cv.put("date", ord.getDate());
		// cv.put("phone", ord.getPhoneNumber());
		// cv.put("tarif", "");
		// cv.put("comment", ord.getComments());
		// cv.put("status", ord.getStatus());

		listView_orders.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});

		filter = (Button) myView.findViewById(R.id.btn_filter);
		delete = (Button) myView.findViewById(R.id.btn_delete);
		// filterOff = (Button) view.findViewById(R.id.filter_off);

		cleanArchive = (Button) myView.findViewById(R.id.cleanArchive);
		cleanArchive.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DbArchHelper dbHelper = new DbArchHelper(ContextHelper
						.getInstance().getCurrentContext());
				SQLiteDatabase db = dbHelper.getWritableDatabase();

				String table_name = "order_arch";
				String where = null;
				String[] whereArgs = null;

				db.delete(table_name, where, whereArgs);
				dbHelper.close();
				displayOrders();
			}
		});

		back = (Button) myView.findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.ORDERS);
			}
		});

		datesSpinner = (Spinner) myView.findViewById(R.id.spinner_date);

		incl = (CheckBox) myView.findViewById(R.id.chkb_incl);

		// add listener for incoming packet of archiv mOrders
		MultiPacketListener.getInstance().addListener(
				Packet.ARCHIV_ORDERS_RESPONSE, new OnNetworkPacketListener() {

					@Override
					public void onNetworkPacket(Packet packet) {
						Log.d("STATE", "ArchveListener was activared");
						arch_list_new = new ArrayList<Order>();
						final GetOrdersResponse pack = (GetOrdersResponse) packet;
						for (int i = 0; i < pack.count(); i++) {
							if (pack.getOrder(i).folder
									.equals(Order.FOLDER_DONE)) {
								Order order = new Order(pack.getOrder(i));
								arch_list_new.add(order);
							}
						}
						if (arch_list_new.size() > 0) {
							DbArchHelper dbHelper = new DbArchHelper(
									ContextHelper.getInstance()
											.getCurrentContext());
							SQLiteDatabase db = dbHelper.getWritableDatabase();
							try {
								db.beginTransaction();
								for (Order ord : arch_list_new) {
									ContentValues cv = new ContentValues();
									cv.put("idorder", ord.getOrderID());
									cv.put("fulldescline", ord.toArchive());
									cv.put("fulldesc", ord.getOrderFullDesc());
									cv.put("fulldescother",
											ord.getOrderFullDescOther());
									cv.put("date", ord.getDate());
									long rowID = db.insert("order_arch", null,
											cv);
									Log.d("DbArchiv", "row inserted, ID = "
											+ rowID);
								}
								db.setTransactionSuccessful();
							} catch (Exception e) {
							} finally {
								db.endTransaction();
								displayOrders();
							}
						}
					}
				});

		return myView;
	}

	/**
	 * Gets archive mOrders from server between specified dates
	 * 
	 * @param from
	 *            - date from which archive mOrders will be taken from server
	 * @param till
	 *            - date till which archive mOrders will be taken from server
	 */
	private static void getArchivOrdersFromServerByDate(long from, long till) {

		//dates in MySql format
		
		String filter = "AutoDriverID = "
				+ ServerData.getInstance().getPeopleID()
				+ " AND Folder = 23 AND OrderDateTime BETWEEN "
				+ "'" + Utils.getDateStringServerFormat(from) + "'" + " AND "
				+ "'" + Utils.getDateStringServerFormat(till) + "'";
		byte[] body = RequestBuilder.createBodyGetOrders(filter);
		byte[] data = RequestBuilder.createSrvTransfereData(
				RequestBuilder.DEFAULT_CONNECTION_TYPE, ServerData
						.getInstance().getSrvID(),
				RequestBuilder.DEFAULT_DESTINATION_ID, ServerData.getInstance()
						.getGuid(), true, body);
		ConnectionHelper.getInstance().send(data);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		displayOrders();
	}

	@Override
	public void onResume() {
		super.onResume(); // To change body of overridden methods use File |
							// Settings | File Templates.
		Log.d("myLogs", "ArchivFragment onResume");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		// currDate.setText(dateFormat.format(date));
		// balance.setText(UIData.getInstance().getBalance());
		displayOrders();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	

	public static void displayOrders() {

		isFilter = false;

		DbArchHelper dbHelper = new DbArchHelper(ContextHelper.getInstance()
				.getCurrentContext());
		// подключаемся к БД
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String from = dateFrom.getText().toString();
		String till = dateTill.getText().toString();
		
		long fromForRequest = -1;
		long tillForRequest = -1;
		String searchText = (!searchBox.getText().toString().equals("")) ? "%"
				+ searchBox.getText().toString() + "%" : "%";

		String selection = " fulldesc LIKE ? AND date BETWEEN ? AND ?";
		try {
			from = String.valueOf(df.parse(from).getTime());
			till = String.valueOf(df.parse(till).getTime() + 86400000);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			from = "0";
			till = "0";
			
		}
		Log.d("STATE",
				"from = "
						+ Utils.getDateStringServerFormat(Long.parseLong(from)) +
						", till = " + Utils.getDateStringServerFormat(Long.parseLong(till)));
		
		Log.d("STATE",
				"need = ");
		Log.d("STATE", "from = " + from + ", till = " + till);
		
		String[] selectionArgs = new String[] { searchText, from, till };
		Cursor cr = db.query("order_arch", null, selection, selectionArgs,
				null, null, null);
		
		Log.d("myLogs", "cursor.size = " + cr.getCount());

		arch_list = new ArrayList<Order>();

		// ставим позицию курсора на первую строку выборки
		// если в выборке нет строк, вернется false
		if (cr.moveToFirst()) {

			// определяем номера столбцов по имени в выборке
			int idColIndex = cr.getColumnIndex("id");
			int idOrder = cr.getColumnIndex("idorder");
			int fulldescline = cr.getColumnIndex("fulldescline");
			int fulldesc = cr.getColumnIndex("fulldesc");
			int fulldescother = cr.getColumnIndex("fulldescother");
			int date = cr.getColumnIndex("date");

			do {

				Log.d("MA",
						"ID = " + cr.getInt(idColIndex) + ", orderId = "
								+ cr.getInt(idOrder) + ", fulldescline = "
								+ cr.getString(fulldescline) + ", fulldesc = "
								+ cr.getString(fulldesc) + ", fulldescother = "
								+ cr.getString(fulldescother) + ", date = "
								+ cr.getLong(date));

				try {
					String toJson = cr.getString(fulldescline);
					JSONObject temp = new JSONObject(toJson);
					Order order = new Order();
					order.fromArchive(temp);
					arch_list.add(order);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} while (cr.moveToNext());
		} else {
			Log.d("MA", "0 rows");
			if (fromForRequest != Long.parseLong(from)
					|| tillForRequest != Long.parseLong(till)) {
				fromForRequest = Long.parseLong(from);
				tillForRequest = Long.parseLong(till);
				getArchivOrdersFromServerByDate(fromForRequest, tillForRequest);
			}
		}

		cr.close();

		dbHelper.close();
		mAdapter.updateMyList(arch_list);
		String amounOfOrders = ContextHelper.getInstance().getCurrentContext()
				.getString(R.string.ofOrders)
				+ ": " + arch_list.size();
		orderAmountTxt.setText(amounOfOrders);
		float total = 0;
		for (Order mOrder : arch_list) {
			total += mOrder.getFare();
		}
		String price = String.format("%.2f", total);
		totalTxt.setText(mAdapter.getPriceFormat(price));

		// Collections.sort(arch_list, new CustomComparator()); //sort
		//
		// ContextHelper.getInstance().runOnCurrentUIThread(
		// new Runnable() {
		// @Override
		// public void run() {
		// mAdapter.notifyDataSetChanged();
		// }
		// });
		//
		// List<String> dates = new ArrayList<String>();
		// datesReal = new ArrayList<Long>();
		//
		// //getting all dates
		// for (arch item : arch_list) {
		//
		// if (dates.size()==0) {
		// dates.add(Utils.dateToDateString(item.getDate()));
		// datesReal.add(item.getDate());
		// continue;
		// }
		//
		// boolean found = false;
		//
		// for (String str : dates) {
		//
		// if (Utils.dateToDateString(item.getDate()).equals(str)) {
		// found = true;
		// break;
		// }
		// }
		//
		// if (!found) {
		// dates.add(Utils.dateToDateString(item.getDate()));
		// datesReal.add(item.getDate());
		// }
		//
		// }
		//
		// ArrayAdapter<String> adapter = new
		// ArrayAdapter<String>(ContextHelper.getInstance().getCurrentContext(),
		// android.R.layout.simple_dropdown_item_1line, dates);
		// datesSpinner.setAdapter(adapter);
		// datesSpinner.setSelection(dates.size()-1);
		//
		// /*datesSpinner.setOnItemSelectedListener(new OnItemSelectedListener()
		// {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view, int
		// position, long id) {
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		//
		// }
		// });*/
		//
		listView_orders.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {

				OrderDetails.dispArchOrder(arch_list.get(arg2));
				FragmentTransactionManager.getInstance().openFragment(
						FragmentPacket.ORDER_DETAILS);

			}
		});
		//
		// filter.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		//
		// if (datesReal.size()<2) {
		// AlertDHelper.showDialogOk("Архив слишком мал!");
		// return;
		// }
		//
		// AlertDialog.Builder builder;
		//
		// builder = new
		// AlertDialog.Builder(ContextHelper.getInstance().getCurrentActivity());
		//
		// builder.setTitle("Выберите способ задания промежутка")
		// .setItems(new CharSequence[] {"Быстрый", "Вручную"}, new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (which == 0)
		// quickFilter();
		// else if (which == 1)
		// manualFilter();
		//
		// }
		// });
		//
		// alertDialog = builder.create();
		// alertDialog.setCancelable(false);
		// alertDialog.show();
		//
		// }
		// });
		//
		// /*filterOff.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		// isFilter = false;
		//
		// ContextHelper.getInstance().runOnCurrentUIThread(
		// new Runnable() {
		// @Override
		// public void run() {
		// mAdapter.notifyDataSetChanged();
		// }
		// });
		// }
		// });*/
		//
		// delete.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View view) {
		//
		// if (datesReal.size()==0) {
		// AlertDHelper.showDialogOk("Архив пуст!");
		// return;
		// }
		//
		// long dateOld = datesReal.get(datesSpinner.getSelectedItemPosition());
		// long msInDay = 1000 * 60 * 60 * 24;
		// long msPortion = dateOld % msInDay;
		// date = dateOld - msPortion;
		//
		// System.out.println("Date " + Utils.dateToString(date));
		//
		// String alertMsg = "Вы уверены что хотите удалить все заказы до " +
		// Utils.dateToDateString(date) + "?";
		//
		// if (incl.isChecked()) {
		// alertMsg = "Вы уверены что хотите удалить все заказы до " +
		// Utils.dateToDateString(date) + " включительно?";
		// date += msInDay;
		// System.out.println("Date " + Utils.dateToString(date));
		// }
		//
		// AlertDialog.Builder builder;
		//
		// builder = new
		// AlertDialog.Builder(ContextHelper.getInstance().getCurrentActivity());
		//
		// builder.setMessage(alertMsg)
		// .setTitle("Уведомление")
		// .setPositiveButton("Да", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// DbArchHelper dbHelper = new
		// DbArchHelper(ContextHelper.getInstance().getCurrentContext());
		// SQLiteDatabase db = dbHelper.getWritableDatabase();
		//
		// String table_name = "order_arch";
		// String where = "date < " + date;
		// String[] whereArgs = null;
		//
		// db.delete(table_name, where, whereArgs);
		// dbHelper.close();
		//
		// displayOrders();
		// }
		// }).setNegativeButton("Нет", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		//
		// alertDialog = builder.create();
		// alertDialog.setCancelable(false);
		// alertDialog.show();
		//
		//
		// }
		// });
	}

	//
	// private static void quickFilter() {
	//
	// String msg = "Укажите временной промежуток";
	//
	// RangeSeekBar<Long> seekBar = new RangeSeekBar<Long>(datesReal.get(0),
	// datesReal.get(datesReal.size()-1),
	// ContextHelper.getInstance().getCurrentContext());
	// if (minVal!=0 && maxVal!=0) {
	// seekBar.setSelectedMinValue(minVal);
	// seekBar.setSelectedMaxValue(maxVal);
	// msg = "Отфильтровать с " + Utils.dateToDateString(minVal) + " по " +
	// Utils.dateToDateString(maxVal);
	// }
	// seekBar.setOnRangeSeekBarChangeListener(new
	// OnRangeSeekBarChangeListener<Long>() {
	// @Override
	// public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long
	// minValue, Long maxValue) {
	// // handle changed range values
	// //Log.i("TEST", "User selected new date range: MIN=" + new Date(minValue)
	// + ", MAX=" + new Date(maxValue));
	// minVal = minValue;
	// maxVal = maxValue;
	// alertDialog.setMessage("Отфильтровать с " +
	// Utils.dateToDateString(minValue) + " по " +
	// Utils.dateToDateString(maxValue));
	// }
	// });
	//
	//
	// AlertDialog.Builder builder = new
	// AlertDialog.Builder(ContextHelper.getInstance().getCurrentActivity())
	// .setTitle("Фильтр по дате")
	// .setMessage(msg)
	// .setView(seekBar)
	// .setPositiveButton("Применить", new OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	//
	// long msInDay = 1000 * 60 * 60 * 24;
	// long msPortion = minVal % msInDay;
	// long minVal2 = minVal - msPortion;
	//
	// msPortion = maxVal % msInDay;
	// long maxVal2 = maxVal - msPortion + msInDay;
	//
	// System.out.println(Utils.dateToString(minVal2) + " - " +
	// Utils.dateToString(maxVal2));
	//
	// filterbyExtremums(minVal, maxVal);
	//
	// }
	// });
	//
	// alertDialog = builder.create();
	// alertDialog.show();
	// }
	//
	// private static void manualFilter() {
	// DialogFragment newFragment = new DatePickerFragment();
	// newFragment.show(((FragmentActivity)ContextHelper.getInstance().getCurrentActivity()).getSupportFragmentManager(),
	// "datePicker");
	// }
	//
	// private static void filterbyExtremums(long min, long max) {
	//
	// arch_list_new = new ArrayList<arch>();
	//
	// for (int i=0; i<arch_list.size(); i++) {
	//
	// if (arch_list.get(i).getDate()>min && arch_list.get(i).getDate()<max) {
	// arch_list_new.add(arch_list.get(i));
	// }
	//
	// }
	//
	// isFilter = true;
	//
	// ContextHelper.getInstance().runOnCurrentUIThread(
	// new Runnable() {
	// @Override
	// public void run() {
	// mAdapter.notifyDataSetChanged();
	// }
	// });
	//
	// }

	static class ViewHolder {
		public TextView date, orderFrom, orderTo;
	}


	// @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	// public static class DatePickerFragment extends DialogFragment implements
	// DatePickerDialog.OnDateSetListener {
	//
	// @Override
	// public Dialog onCreateDialog(Bundle savedInstanceState) {
	// // Use the current date as the default date in the picker
	// final Calendar c = Calendar.getInstance();
	// int year = c.get(Calendar.YEAR);
	// int month = c.get(Calendar.MONTH);
	// int day = c.get(Calendar.DAY_OF_MONTH);
	//
	// // Create a new instance of DatePickerDialog and return it
	// return new DatePickerDialog(ContextHelper.getInstance()
	// .getCurrentActivity(), this, year, month, day);
	// }
	//
	// boolean fired = false;
	// public void onDateSet(DatePicker view, int year, int month, int day) {
	//
	// if (fired == true) {
	// //Log.i("PEW PEW", "Double fire occured. Silently-ish returning");
	// return;
	// } else {
	//
	// Calendar cal = Calendar.getInstance();
	// cal.set(year, month, day, 2, 0);
	// System.out.println("min - " + Utils.dateToString(cal.getTimeInMillis()));
	//
	// minVal = cal.getTimeInMillis();
	//
	// DialogFragment newFragment = new DatePickerFragment2();
	// newFragment.show(((FragmentActivity)ContextHelper.getInstance().getCurrentActivity()).getSupportFragmentManager(),
	// "datePicker2");
	//
	// fired = true;
	// }
	//
	// }
	// }
	//
	// @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	// public static class DatePickerFragment2 extends DialogFragment implements
	// DatePickerDialog.OnDateSetListener {
	//
	// @Override
	// public Dialog onCreateDialog(Bundle savedInstanceState) {
	// // Use the current date as the default date in the picker
	// final Calendar c = Calendar.getInstance();
	// int year = c.get(Calendar.YEAR);
	// int month = c.get(Calendar.MONTH);
	// int day = c.get(Calendar.DAY_OF_MONTH);
	//
	// // Create a new instance of DatePickerDialog and return it
	// return new DatePickerDialog(ContextHelper.getInstance()
	// .getCurrentActivity(), this, year, month, day);
	// }
	//
	// boolean fired = false;
	// public void onDateSet(DatePicker view, int year, int month, int day) {
	// if (fired == true) {
	// //Log.i("PEW PEW", "Double fire occured. Silently-ish returning");
	// return;
	// } else {
	//
	// Calendar cal = Calendar.getInstance();
	// cal.set(year, month, day, 2, 0);
	// System.out.println("max - " + Utils.dateToString(cal.getTimeInMillis() +
	// (1000 * 60 * 60 * 24)));
	//
	// maxVal = cal.getTimeInMillis();
	//
	// filterbyExtremums(minVal, maxVal + (1000 * 60 * 60 * 24)); //plus msInDay
	//
	// fired = true;
	// }
	// }
	// }

}
