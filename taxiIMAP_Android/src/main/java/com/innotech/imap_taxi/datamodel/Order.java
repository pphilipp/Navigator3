/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.imap_taxi.datamodel;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.innotech.imap_taxi.datamodel.DispOrder.DispSubOrder;
import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Order {
	public static final byte ORDER_1 = 1;

	public static final int STATE_NEW = 1;
	public static final int STATE_PERFORMING = 2;
	public static final int STATE_PERFORMED = 3;
	public static final int STATE_TAKEN = 4;
	public static final int STATE_MISSED = 5;
	public static final int STATE_CANCELLED = 6;
	public static final int STATE_KRYG_ADA = 7;
	public static final int STATE_PRELIM = 8;
	public static final String FOLDER_DOIN = "Исполняемый";
	public static final String FOLDER_DONE = "Выполненный";
	public static final String FOLDER_NOT_DONE = "Невыполненный";
	public static final String FOLDER_TRASH = "Корзина";
	public static final String FOLDER_INBOX = "Входящий";
	public static final String FOLDER_RECEIVED = "Принятый";
	public static final int COND = 3;
	public static final int POS = 15;
	public static final int ANIMAL = 34;
	public static final int WIFI = 33;
	public static final int NOSMOKE = 35;
	public static final int BAG = 11;
	public static final int TAXOMETER = 111;
	public boolean canFirstForAnyParking;
	public boolean concessional;
	public boolean arrived;
	public long dateNoClient;
	public boolean accepted;
	public boolean isArchive;
	// Данные класса
	protected int orderID;
	protected byte clientType;// 0 - случайный,1- постоянный
	protected long date;// Дата подачи заказа
	protected long date3; // время прибытия
	protected float fare;// Цена поездки
	protected String dispatcherName;// Имя диспетчера
	protected String phoneNumber;// Телефон заказчика
	protected String distanceToOrderPlace;// Distance
	// protected Vector address;//Коллекция адресов
	protected List<DispSubOrder> address;
	protected String comments;
	protected String region;
	protected float bonusSum;
	protected String flat;
	protected String parade;
	protected String building;
	protected int status;
	// Статус заказа,1 - новый,2 - выполняется,3 - выполненный,4 - заказ взят,но
	// еще не подтвержден диспетчером,5 - пропущенный;6 - отмененный
	// Данные для автоудаления заказа
	protected long reciveTime; // Время получения заказа
	public long getReciveTime() {
		return reciveTime;
	}

	protected int N; // Номер типа заказа
	protected boolean efirOrder;
	protected String partnerPreffix;
	protected boolean signed;
	protected int autoTariffClassUID;
	protected String folder;
	protected String orderType;
	protected String street;
	protected String house;
	protected String clientName;
	protected boolean nonCashPay; // безнал
	String shortDesc, fullDescLine;
	float orderCostForDriver;
	private boolean preliminary;
	private boolean fromServer;
	private List<String> people_signed;
	private String prelimDesc;
	private String sourceWhence;
	public String autoClass;

	private float waitMinutes;
	private float waitMinutesPay;
	private String entrance;
	private String addressFact;
	private List<String> features;

	public int colorClass;
	public String agentName;
	public int relayID;
	public float distanceToPointOfDelivery;


	public Order() {}

	public Order(DispOrder4 order) {

		this.orderID = order.orderID;
		clientType = 0;
		status = STATE_NEW;
		reciveTime = System.currentTimeMillis();
		N = 1;
		comments = order.comments;
		fare = order.fare;
		street = order.streetName;
		house = order.house;
		region = order.region;
		bonusSum = order.bonusSum;
		flat = order.flat;
		parade = "";
		building = order.building;
		efirOrder = false;
		phoneNumber = order.phoneNumber;
		dispatcherName = order.dispatcherName;
		partnerPreffix = order.partnerPreffix;
		signed = false;
		autoTariffClassUID = order.autoTariffClassUID;
		date = order.date;
		folder = order.folder;
		orderType = order.orderType;
		address = order.subOrders;
		clientName = order.clientName;
		nonCashPay = order.nonCashPay;
		shortDesc = order.orderShortDesc;
		fullDescLine = order.orderFullDesc;
		prelimDesc = order.orderPrelimFullDesc;
		preliminary = order.preliminary;
		people_signed = new ArrayList<String>();
		canFirstForAnyParking = order.canFirstForAnyParking;
		orderCostForDriver = order.orderCostForDriver;
		sourceWhence = order.sourceWhence;
		concessional = order.concessional;
		autoClass = order.autoClass;
		waitMinutes = order.waitMinutes;
		waitMinutesPay = order.waitMinutesPay;
		entrance = order.entrance;
		addressFact = order.addressFact;
		features = order.features;
		accepted = false;
		isArchive = false;
		colorClass = order.colorClass;
		agentName = order.agentName;
		relayID = order.relayID;
		distanceToPointOfDelivery = order.distanceToPointOfDelivery;
		//date3 = order.date;
	}

	public String getClientName() {
		return clientName;
	}

	public String getSourceWhence() {
		return sourceWhence;
	}

	public String getPrelimDesc() {
		return prelimDesc;
	}

	public void setPrelimDesc(String prelimDesc) {
		this.prelimDesc = prelimDesc;
	}

	// public void addNickToPrePeopList(String nick) {
	// people_signed.add(nick);
	// }

	public List<String> getPeople_signed() {
		return people_signed;
	}

	public boolean isFromServer() {
		return fromServer;
	}

	public void setFromServer(boolean fromServer) {
		this.fromServer = fromServer;
	}

	/*
	 * public byte[] toByte() { ByteArrayOutputStream result = new
	 * ByteArrayOutputStream(); DataOutputStream dos = new
	 * DataOutputStream(result);
	 * 
	 * try { writeFields(dos);
	 * 
	 * return result.toByteArray(); } catch(IOException ioex) { return (new
	 * byte[]{}); } finally { try { dos.close(); result.close();
	 * }catch(Exception ex){} } }
	 */

	/*
	 * protected void readFields(DataInputStream dis) throws IOException { //int
	 * orderID; orderID = dis.readInt(); //byte clientType; clientType =
	 * dis.readByte(); //long date;//Дата подачи заказа date = dis.readLong();
	 * //double fare;//Цена поездки fare = dis.readDouble(); //String
	 * dispatcherName;//Имя диспетчера dispatcherName = dis.readUTF(); //String
	 * phoneNumber;//Телефон заказчика phoneNumber = dis.readUTF();
	 * 
	 * //Массив адресов int count = dis.readInt(); address = new Vector(count);
	 * for (int i = 0; i < count; ++i) { address.addElement(dis.readUTF()); }
	 * 
	 * //int status status = dis.readInt(); //String comments; comments =
	 * dis.readUTF(); //double bonusSum; bonusSum = dis.readDouble(); //String
	 * region; region = dis.readUTF(); //String flat; flat = dis.readUTF();
	 * //String parade; parade = dis.readUTF(); //String building; building =
	 * dis.readUTF(); // String partnerPreffix; partnerPreffix = dis.readUTF();
	 * //int autoTariffClassUID; autoTariffClassUID = dis.readInt();
	 * 
	 * //folder folder = dis.readUTF();
	 * 
	 * //Пропускаем тип заказа dis.read(); }
	 * 
	 * protected void writeFields(DataOutputStream dos) throws IOException {
	 * //int orderID; dos.writeInt(orderID); //byte clientType;
	 * dos.writeByte(clientType); //long date;//Дата подачи заказа
	 * dos.writeLong(date); //double fare;//Цена поездки dos.writeDouble(fare);
	 * //String dispatcherName;//Имя диспетчера dos.writeUTF(dispatcherName);
	 * //String phoneNumber;//Телефон заказчика dos.writeUTF(phoneNumber);
	 * 
	 * 
	 * //Массив адресов dos.writeInt(address.size()); for (int i = 0; i <
	 * address.size(); ++i) { dos.writeUTF((String)address.elementAt(i)); }
	 * 
	 * //int status dos.writeInt(status); //String comments;
	 * dos.writeUTF(comments); //double bonusSum; dos.writeDouble(bonusSum);
	 * //String region; dos.writeUTF(region); //String flat; dos.writeUTF(flat);
	 * //String parade; dos.writeUTF(parade); //String building;
	 * dos.writeUTF(building); // String partnerPreffix;
	 * dos.writeUTF(partnerPreffix); //int autoTariffClassUID;
	 * dos.writeInt(autoTariffClassUID); //folder dos.writeUTF(folder);
	 * 
	 * //Помечаем тип заказа dos.write(ORDER_1); }
	 */

	public String getFullDescLine() {
		return fullDescLine;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String newRegion) {
		region = newRegion;
	}

	public String getBonusSum() {
		if (bonusSum > 0) {
			return Double.toString(bonusSum);
		} else {
			return "";
		}
	}

	public void setBonusSum(float newBonus) {
		bonusSum = newBonus;
	}

	public boolean isNewEfirOrder() {
		return (status == STATE_NEW && N == 3);
	}

	public int getN() {
		return N;
	}

	public void setN(int newN) {
		if ((newN > 3) || (newN < 1)) {
			newN = 3;
		}
		N = newN;
		efirOrder = (N == 3);
	}

	public int getOrderID() {
		return orderID;
	}

	public void setOrderID(int id) {
		orderID = id;
	}

	public void setClientType(int type) {
		if (type == 1) {
			clientType = 1;
		} else {
			clientType = 0;
		}
	}

	public void setAutoClass(String autoClass) {
		this.autoClass = autoClass;
	}

	public int getClientType() {
		return clientType;
	}

	public void setClientType(String type) {
		if (type.equals("Patron")) {
			clientType = 1;
		} else {
			clientType = 0;
		}
	}

	public String getClientTypeString() {
		if (clientType == 1) {
			return "Постоянный";
		} else {
			return "Случайный";
		}
	}

	public long getDate() {
		return date;
	}

	public void setDate(long newDate) {
		date = newDate;
	}

	public long getDate3() {
		return date3;
	}

	public void setDate3(long newDate3) {
		date3 = newDate3;
	}

	public float getFare() {
		return fare;
	}

	public void setFare(float newFare) {
		fare = newFare;
	}

	public String getDispatcherName() {
		return dispatcherName;
	}

	public void setDispatcherName(String newName) {
		dispatcherName = newName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String newPhone) {
		phoneNumber = newPhone;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int newStatus) {
		status = newStatus;
	}

	public void addAddres(DispSubOrder addres) {
		address.add(addres);
	}

	public List<DispSubOrder> getAddress() {
		return address;
	}

	public boolean isDeleted(int[] T) {
		if ((status == STATE_NEW)
				&& ((System.currentTimeMillis() - reciveTime) > T[N - 1])) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isTooOld(int[] T) {
		if (status == STATE_NEW) {
			if ((System.currentTimeMillis() - reciveTime) > T[N - 1]) {
				if (N != 3)// Если заказ не из эфира - сохраняем как
							// проигнорированный
				{
					status = STATE_MISSED;
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String newComments) {
		comments = newComments;
	}

	public String getFlat() {
		return flat;
	}

	public void setFlat(String newFlat) {
		flat = newFlat;
	}

	public String getParade() {
		return parade;
	}

	public void setParade(String newParade) {
		parade = newParade;
	}

	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String newBuilding) {
		building = newBuilding;
	}

	public boolean isEfirOrder() {
		return efirOrder;
	}

	public String getPartnerPreffix() {
		return partnerPreffix;
	}

	public void setPartnerPreffix(String newValue) {
		partnerPreffix = newValue;
	}

	public boolean isPreliminary() {
		return preliminary;
	}

	public void setPreliminary(boolean preliminary) {
		this.preliminary = preliminary;
	}

	public int getAutoTariffClassUID() {
		return autoTariffClassUID;
	}

	public void setAutoTariffClassUID(int autoTariffClassUID) {
		this.autoTariffClassUID = autoTariffClassUID;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getShortDescription() {

		String result = formatPreffix(getPartnerPreffix());
		if (getFare() >= 0) {
			result += "(" + region + ") " + street + " " + house + "["
					+ getFare() + "грн]";
		} else {
			result += "(" + region + ") " + street + " " + house
					+ "[без суммы]";
		}

		return result;
	}

	/*
	 * public String getOrderFullDesc1() { String result = "";
	 * //[время]([цена])[улица]д[дом],п[парадная],кв[квартира]>[следующий
	 * адрес]\[примечания]
	 * 
	 * //Префикс партнера и Дата String tmp = formatPreffix(getPartnerPreffix())
	 * + (Utils.dateToString(getDate()) + "(");
	 * 
	 * //цена if (fare>=0) { tmp += Double.toString(fare); String bonusSumStr =
	 * getBonusSum(); if (!bonusSumStr.equals("") && getStatus() != STATE_NEW) {
	 * tmp += " в офисе " + bonusSum + " грн"; } tmp +=")"; } else { tmp +=
	 * "без суммы)"; }
	 * 
	 * tmp += "(" + region + ") " + street + building + house + "["; if
	 * (address.size()>0) { for (int j=0;j<address.size();j++) { tmp +=
	 * address.get(j).from + " > " + address.get(j).to + "\n";
	 * 
	 * } }
	 * 
	 * //Коментарии tmp += "]\n\\" + getComments();
	 * 
	 * result = tmp + "\\" + folder + '\n';
	 * 
	 * result += ("Дата : " + Utils.dateToDateString(getDate()) + '\n'); result
	 * += ("Тип клиента : " + getClientTypeString() + '\n');
	 * 
	 * if (getPhoneNumber().equals("")) { result += "Тел.: нет\n"; } else {
	 * result += ("Тел. : " + getPhoneNumber() + "\n"); }
	 * 
	 * result += ("Имя диспетчера : " + getDispatcherName() + '\n'); result +=
	 * ("ID заказа : " + Integer.toString(getOrderID()) + '\n');
	 * 
	 * return result; }
	 */
	public String getOrderFullDesc() {
		return getFullDescLine();
		/*
		 * String result = "";
		 * 
		 * if (canFirstForAnyParking) { result +=
		 * "<b><font color='red'> Первый на стоянке </font></b>"; }
		 * 
		 * if (concessional) { result +=
		 * "<b><font color='green'> Льгота </font></b>"; }
		 * 
		 * if (preliminary){ result +=
		 * "<b><font color='red'> ПРЕДВАРИТЕЛЬНЫЙ </font></b>"; }
		 * 
		 * /* result += "Адрес подачи: " + getStreet() + " " + getAddressFact();
		 * if (!building.equals("")){ result += ", корп." + building; } if
		 * (!entrance.equals("")) result += ", п." + entrance; if
		 * (!flat.equals("")) result += ", кв " + flat; result += "<br>"; /
		 * 
		 * if (!getAddr().equals("")) { result += "Маршрут: " + getAddr() +
		 * "<br>"; } else{ result += "Адрес подачи: " + getStreet() + " " +
		 * getAddressFact(); if (!building.equals("")){ result += ", корп." +
		 * building; } if (!entrance.equals("")) result += ", п." + entrance; if
		 * (!flat.equals("")) result += ", кв " + flat; result += "<br>"; } if
		 * (orderCostForDriver > 0.0) { result +=
		 * "<b><font color='red'> Оплата фирме за заказ: " + orderCostForDriver
		 * + " грн. </font></b><br>"; }
		 * 
		 * if (!SettingsFromXml.getInstance().isNotSendPrice() && getFare() !=
		 * 0d) { result += "Цена: " + getFare() + " грн.<br>"; }
		 * 
		 * result += "Комментарии: " + getComments() + "<br>";
		 * 
		 * if (!clientName.equals("")) { result += "Имя клиента: " + clientName
		 * + "<br>"; }
		 * 
		 * if (nonCashPay) { result +=
		 * "<b><font color='red'> Безнал </font></b><br>"; }
		 * 
		 * result += "Дата: " + Utils.dateToString(getDate()) + "<br>";
		 * 
		 * if (getPhoneNumber().equals("")) { // result += "Тел.: нет<br>"; }
		 * else { result += ("Тел.: " + getPhoneNumber() + "<br>"); }
		 * 
		 * if (waitMinutes != 0) { result += ("Плата за ожидание - " +
		 * waitMinutesPay + " грн., время ожидания - " + waitMinutes + "<br>");
		 * } if (features!=null && features.size()!=0){
		 * result+="Дополнительные услуги:"; for (String dop : features){
		 * result+="<br>"+dop; } }
		 * 
		 * return result;
		 */
	}

	public String getAddressFact() {
		return addressFact;
	}

	public void setAddressFact(String addressFact) {
		this.addressFact = addressFact;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public String getOrderFullDescOther() {

		String result = "Тип клиента: " + getClientTypeString() + "\n"
				+ "Папка: " + folder + "\n";

		if (!getDispatcherName().equals("")) {
			result += ("Имя диспетчера: " + getDispatcherName() + "\n");
		}
		result += "Класс заказа: " + autoClass + "\n";
		result += "Район подачи: " + getRegion() + "\n";
		result += "ID заказа: " + Integer.toString(getOrderID()) + "\n";
		result += "colorClass" + colorClass + "\n";
		result += "agentName" + agentName + "\n";

		return result;
	}

	public String getAddr() {
		String addr = "";
		// addr += address.size();
		if (address.size() != 0) {
			addr += address.get(0).from;
			if (!building.equals("")) {
				addr += ", корп." + building;
			}
			if (!entrance.equals(""))
				addr += ", п." + entrance;
			if (!flat.equals(""))
				addr += ", кв. " + flat;
		}

		for (DispSubOrder disp : address) {
			addr += " -> " + disp.to + " (" + disp.tariff + ")";
		}

		return addr;
	}

	/*
	 * public String getOrderPrelimFullDesc() { String[] temp = getAddress();
	 * 
	 * String result = (temp.length > 0 ? temp[0] : ""); //Добавляем остальные
	 * адреса for (int i = 1; i < temp.length; ++i) { if (!temp[i].equals("")) {
	 * result += ">" + temp[i]; } } result += "\n"; result += "Дата: " +
	 * Utils.dateToDateString(getDate()) + "\n"; result += "Время: " +
	 * Utils.dateToTimeString(getDate()) + "\n"; result += "Подписались: " +
	 * getSigned() + "\n"; result += "Цена: " + Double.toString(getFare()) +
	 * "\n";
	 * 
	 * return result; }
	 */

	protected String formatPreffix(String value) {
		if (!value.equals("")) {
			return ("[" + value + "]");
		} else {
			return "";
		}
	}

	public boolean isAlien() {
		return false;
	}

	public String getStreet() {
		return street;
	}

	public String getHouse() {
		return house;
	}

	public void setPreSigned(List<String> signed2) {
		people_signed = signed2;
	}

	public void setPressedArrived() {
		arrived = true;
	}

	public boolean isNonCashPay() {
		return nonCashPay;
	}

	public String toArchive() {
		JSONObject archive = new JSONObject();
		JSONArray JSONAddressArray = new JSONArray();
		JSONObject temp = new JSONObject();
		isArchive = true;
		try {
			archive.put("orderID", orderID);
			archive.put("status", status);
			archive.put("reciveTime", reciveTime);
			archive.put("N", N);
			archive.put("comments", comments);
			archive.put("fare", fare);
			archive.put("street", street);
			archive.put("house", house);
			archive.put("region", region);
			archive.put("bonusSum", bonusSum);
			archive.put("flat", flat);
			archive.put("parade", parade);
			archive.put("building", building);
			archive.put("efirOrder", efirOrder);
			archive.put("phoneNumber", phoneNumber);
			archive.put("dispatcherName", dispatcherName);
			archive.put("partnerPreffix", partnerPreffix);
			archive.put("signed", signed);
			archive.put("autoTariffClassUID", autoTariffClassUID);
			archive.put("date", date);
			archive.put("folder", folder);
			archive.put("orderType", orderType);
			for (DispSubOrder dispSubOrder : address) {
				JSONAddressArray.put(dispSubOrder.toArchive());
			}
			archive.put("address", JSONAddressArray);
			archive.put("clientName", clientName);
			archive.put("nonCashPay", nonCashPay);
			archive.put("shortDesc", shortDesc);
			archive.put("fullDescLine", fullDescLine);
			archive.put("prelimDesc", prelimDesc);
			archive.put("preliminary", preliminary);
			// archive.put("people_signed", people_signed);
			archive.put("canFirstForAnyParking", canFirstForAnyParking);
			archive.put("orderCostForDriver", orderCostForDriver);
			archive.put("sourceWhence", sourceWhence);
			archive.put("concessional", concessional);
			archive.put("autoClass", autoClass);
			archive.put("waitMinutes", waitMinutes);
			archive.put("waitMinutesPay", waitMinutesPay);
			archive.put("entrance", entrance);
			archive.put("addressFact", addressFact);
			// !!!
			archive.put("colorClass", colorClass);
			JSONArray featuresArray = new JSONArray(features);
			archive.put("features", featuresArray);
			if (agentName != null && !agentName.equals(""))
				archive.put("agentName", agentName);
			else
				archive.put("agentName", "NO_VALUE");
			// !!!
			archive.put("accepted", accepted);
			archive.put("isArchive", isArchive);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		// this.orderID = order.orderID;
		// clientType = 0;
		// status = STATE_NEW;
		// reciveTime = System.currentTimeMillis();
		// N = 1;
		// comments = order.comments;
		// fare = order.fare;
		// street = order.streetName;
		// house = order.house;
		// region = order.region;
		// bonusSum = order.bonusSum;
		// flat = order.flat;
		// parade = "";
		// building = order.building;
		// efirOrder = false;
		// phoneNumber = order.phoneNumber;
		// dispatcherName = order.dispatcherName;
		// partnerPreffix = order.partnerPreffix;
		// signed = false;
		// autoTariffClassUID = order.autoTariffClassUID;
		// date = order.date;
		// folder = order.folder;
		// orderType = order.orderType;
		// address = order.subOrders;
		// clientName = order.clientName;
		// nonCashPay = order.nonCashPay;
		// shortDesc = order.orderShortDesc;
		// fullDescLine = order.orderFullDesc;
		// prelimDesc = order.orderPrelimFullDesc;
		// preliminary = order.preliminary;
		// people_signed = new ArrayList<String>();
		// canFirstForAnyParking = order.canFirstForAnyParking;
		// orderCostForDriver = order.orderCostForDriver;
		// sourceWhence = order.sourceWhence;
		// concessional = order.concessional;
		// autoClass = order.autoClass;
		// waitMinutes = order.waitMinutes;
		// waitMinutesPay = order.waitMinutesPay;
		// entrance = order.entrance;
		// addressFact = order.addressFact;
		// features = order.features;
		// accepted = false;
		return archive.toString();
	}

	public void fromArchive(JSONObject archive) {
		try {
			orderID = archive.getInt("orderID");
			// clientType = Byte.parseByte(archive.getString("clientType"));
			status = archive.getInt("status");
			reciveTime = archive.getLong("reciveTime");
			N = archive.getInt("N");
			comments = archive.getString("comments");
			fare = Float.parseFloat(archive.getString("fare"));
			street = archive.getString("street");
			house = archive.getString("house");
			region = archive.getString("region");
			bonusSum = Float.parseFloat(archive.getString("bonusSum"));
			flat = archive.getString("flat");
			parade = archive.getString("parade");
			building = archive.getString("building");
			efirOrder = archive.getBoolean("efirOrder");
			phoneNumber = archive.getString("phoneNumber");
			dispatcherName = archive.getString("dispatcherName");
			partnerPreffix = archive.getString("partnerPreffix");
			signed = archive.getBoolean("signed");
			autoTariffClassUID = archive.getInt("autoTariffClassUID");
			date = archive.getLong("date");
			folder = archive.getString("folder");
			orderType = archive.getString("orderType");
			JSONArray JSONAdressArray = archive.getJSONArray("address");
			address = new ArrayList<DispSubOrder>();
			for (int i = 0; i < JSONAdressArray.length(); i++) {
				JSONObject temp = JSONAdressArray.getJSONObject(i);
				DispSubOrder dispSubOrder = new DispSubOrder(
						temp.getString("tariff"), temp.getString("from"),
						temp.getString("to"), Float.parseFloat(temp
								.getString("geoXFrom")), Float.parseFloat(temp
								.getString("geoYFrom")), Float.parseFloat(temp
								.getString("geoXTo")), Float.parseFloat(temp
								.getString("geoYTo")));
				address.add(dispSubOrder);

			}
			clientName = archive.getString("clientName");
			nonCashPay = archive.getBoolean("nonCashPay");
			shortDesc = archive.getString("shortDesc");
			fullDescLine = archive.getString("fullDescLine");
			prelimDesc = archive.getString("prelimDesc");
			preliminary = archive.getBoolean("preliminary");
			// people_signed = archive.get("people_signed");
			canFirstForAnyParking = archive.getBoolean("canFirstForAnyParking");
			orderCostForDriver = Float.parseFloat(archive
					.getString("orderCostForDriver"));
			sourceWhence = archive.getString("sourceWhence");
			concessional = archive.getBoolean("concessional");
			autoClass = archive.getString("autoClass");
			waitMinutes = Float.parseFloat(archive.getString("waitMinutes"));
			waitMinutesPay = Float.parseFloat(archive
					.getString("waitMinutesPay"));
			entrance = archive.getString("entrance");
			addressFact = archive.getString("addressFact");
			// !!!
			Log.d("myLogs", "orderFromArchiv, agentName");
			agentName = (archive.getString("agentName") != null && !archive
					.getString("agentName").equals("NO_VALUE")) ? archive
					.getString("agentName") : "";
			colorClass = archive.getInt("colorClass");

			JSONArray featuresJSONArray = archive.getJSONArray("features");
			List<String> temp_features = new ArrayList<String>();
			if (featuresJSONArray.length() != 0) {
				for (int i = 0; i < featuresJSONArray.length(); i++) {
					temp_features.add(featuresJSONArray.get(i).toString());
				}
			}
			setFeatures(temp_features);
			// !!!
			accepted = archive.getBoolean("accepted");
			isArchive = true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// archive.put("status", status);
		// archive.put("reciveTime", reciveTime);
		// archive.put("N",N);
		// archive.put("comments", comments);
		// archive.put("fare", fare);
		// archive.put("street", street);
		// archive.put("house", house);
		// archive.put("region", region);
		// archive.put("bonusSum", bonusSum);
		// archive.put("flat", flat);
		// archive.put("parade", parade);
		// archive.put("building", building);
		// archive.put("efirOrder", efirOrder);
		// archive.put("phoneNumber", phoneNumber);
		// archive.put("dispatcherName", dispatcherName);
		// archive.put("partnerPreffix", partnerPreffix);
		// archive.put("signed", signed);
		// archive.put("autoTariffClassUID", autoTariffClassUID);
		// archive.put("date", date);
		// archive.put("folder", folder);
		// archive.put("orderType", orderType);
		// for (DispSubOrder dispSubOrder : address){
		// JSONAddressArray.put(dispSubOrder.toArchive());
		// }
		// archive.put("address", JSONAddressArray);
		// archive.put("clientName", clientName);
		// archive.put("nonCashPay", nonCashPay);
		// archive.put("shortDesc", shortDesc);
		// archive.put("fullDescLine", fullDescLine);
		// archive.put("prelimDesc", prelimDesc);
		// archive.put("preliminary", preliminary);
		// archive.put("people_signed", people_signed);
		// archive.put("canFirstForAnyParking", canFirstForAnyParking);
		// archive.put("orderCostForDriver", orderCostForDriver);
		// archive.put("sourceWhence", sourceWhence);
		// archive.put("concessional", concessional);
		// archive.put("autoClass", autoClass);
		// archive.put("waitMinutes", waitMinutes);
		// archive.put("waitMinutesPay", waitMinutesPay);
		// archive.put("entrance", entrance);
		// archive.put("addressFact", addressFact);
		// archive.put("features", features);
		// archive.put("accepted", accepted);
		// archive.put("isArchive", isArchive);
	}

	public SpannableString getDistanceToOrderPlace() {
		// print distance
//		distanceToPointOfDelivery != 0 ? distanceToPointOfDelivery / 1000 : 0

		float distToPoint = 22.43586f;
		String dist = (distToPoint > 100) ? ">100"
				+ ContextHelper.getInstance().getCurrentContext()
				.getString(R.string.km) : String.format("%.1f",
				distToPoint)
				+ ContextHelper.getInstance().getCurrentContext()
				.getString(R.string.km);

		SpannableString ss = null;
		ss = new SpannableString(dist);
		ss.setSpan(new RelativeSizeSpan(0.4f), dist.length() - 2,
				dist.length(), 0);
		ss.setSpan(new ForegroundColorSpan(ContextHelper.getInstance()
				.getCurrentContext().getResources()
				.getColor(R.color.greyText)),
		dist.length() - 2, dist.length(), 0);

		return ss;
	}

	@Override
	public String toString() {
		return getOrderFullDesc();
	}

	public void setDistanceToOrderPlace(String distanceToOrderPlace) {
		this.distanceToOrderPlace = distanceToOrderPlace;
	}
}
