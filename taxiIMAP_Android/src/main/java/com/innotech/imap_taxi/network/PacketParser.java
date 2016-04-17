package com.innotech.imap_taxi.network;

import android.util.Log;

import com.innotech.imap_taxi.network.packet.*;
/**
 * @class PacketParser -
 *
 * */

class PacketParser {
	Packet result;
	SrvTransferDataResponse tmp;

	public Packet parsePacket(byte[] data) {
		int id = selectID(data);
		tmp = null;
		if (id == Packet.SRV_TRANSFER_DATA_RESPONCE) {
			tmp = new SrvTransferDataResponse(data);
			if (tmp.body != null) {
				data = tmp.body;
				id = selectID(data); 
			}
		}
 
		switch (id) {
		case Packet.SERVER_ERROR_RESPONCE : result = new ServerErrorResponse(data); break;
		case Packet.LOGIN_RESPONCE : result = new LoginResponse(data); break;
		case Packet.PPCSETTINGS_RESPONCE : result = new PPCSettingsResponce(data); break;
		case Packet.CSBALANCE_RESPONCE : result = new GetCSBalanceResponce(data); break;
		case Packet.SRV_MESSAGE_RESPONCE : result = new SrvMessageResponce(data); break;
		case Packet.GET_ORDERS_RESPONCE : result = new GetOrdersResponse(data); break;
		case Packet.PING_RESPONCE : result = new PingResponce(data); break; 
		//            case Packet.ORDER_RESPONCE : result = new DispOrderResponse(data); break;
		//            case Packet.ORDER_RESPONCE2 : result = new DispOrderResponse2(data); break;
		//            case Packet.ORDER_RESPONCE3 : result = new DispOrderResponse3(data); break;
		case Packet.ORDER_RESPONCE4 : result = new DispOrderResponse4(data); break;
		case Packet.REGISTER_ON_TAXI_PARKING_RESPONCE : result = new RegisterOnTaxiParkingResponce(data); break;
		case Packet.TAXI_PARKINGS_RESPONCE : result = new TaxiParkingsResponce(data); break;
		case Packet.TAXI_PARKINGS_RESPONCE2 : result = new TaxiParkingsResponce2(data); break;
		case Packet.TAXI_PARKING_STATISTIC_RESPONCE : result = new TaxiParkingStatisticResponce(data); break;
		case Packet.DRIVER_PARKING_POSITION_RESPONCE : result = new DriverParkingPositionResponce(data); break;
		case Packet.UNREGISTER_ON_TAXI_PARKING_RESPONCE : result = new UnRegisterOnTaxiParkingResponce(data); break;
		case Packet.RELAY_COMMUNICATION_RESPONCE:
			result = new RelayCommunicationResponce(data); break;
		case Packet.PPSCHANGE_STATE_RESPONCE : result = new PPSChangeStateResponse(data); break;
		case Packet.SETTINGS_XML_RESPONCE : result = new SettingXmlResponse(data); break;
		case Packet.PING_STATE_ANSWER : result = new PingStateAnswer(data); break; 
		case Packet.SET_YOUR_ORDERS_ANSWER : result = new SetYourOrdersAnswer(data); break;
		case Packet.ETHEAR_ORDER_OVER_ANSWER : result = new SearchInEtherOrderOverResponse(data); break;
		case Packet.REFUSE_PRELIMINARY_ORDER_ANSWER: result = new RefusePreliminaryOrdeResponce(data); break;
		case Packet.PREORDERS_RESPONCE : result = new PreOrdersResponse(data); break;
		case Packet.GET_ROUTES_ANSWER : result = new GetRoutesResponse(data); break;
		case Packet.SIGN_PRELIM_ORDER_ANSWER: result = new SignPrelimOrdeResponce(data); break;
		case Packet.DRIVER_BLOCKED_PACK : result = new DriverBlockedPack(data); break;
		case Packet.TCPMESSAGE_RESPONCE : result = new TCPMessageResponce(data); break;
		case Packet.CALL_SIGNCHANGED_RESPONCE: result = new CallSignChangedResponce(data); break;
		case Packet.TAXIMETER_RATES: result = new GetTaximeterRates(data); break;
		//archivOrders
		case Packet.ARCHIV_ORDERS_RESPONSE: result = new DispOrderResponse4(data);  break;

		case Packet.DISTANCE_FROM_STAS: result = new FromStas4(data);  break;
		
		default : result = null;
		}

		//Номер пакета устанавливаем
		if (tmp != null && result != null)
		{
			result.setPacketNumber(tmp.getPacketNumber());
		}

		return result;
	}

	private int selectID(byte[] data) {
		//Определяем что за пакет
		byte[] buffer4 = new byte[4];
		int offset = 0;
		System.arraycopy(data, offset, buffer4, 0, buffer4.length);
		offset += buffer4.length;
		int size = Utils.byteToInt(buffer4);
		byte[] buffer = new byte[size];
		System.arraycopy(data, offset, buffer, 0, buffer.length);
		String val = StringUtils.bytesToStr(buffer);

		Log.i("KVEST_TAG","[" + val + "]");
		Log.d("ClToSrv", "incoming packet = " + val);
		if (val.equals("IMAP.Net.SrvTransfereError")) {
			return Packet.SERVER_ERROR_RESPONCE;
		}
		else if (val.equals("IMAP.Net.SrvLoginAnswer")) {
			return Packet.LOGIN_RESPONCE;
		}
		else if (val.equals("IMAP.Net.SrvPingAnswer")) {
			return Packet.PING_RESPONCE;
		}
		else if (val.equals("IMAP.Net.SrvMessage")) {
			return Packet.SRV_MESSAGE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.SrvTransfereData")) {
			return Packet.SRV_TRANSFER_DATA_RESPONCE;
		}
		else if (val.equals("IMAP.Net.DispOrder4")) {
			return Packet.ORDER_RESPONCE4;
		}
		else if (val.equals("IMAP.Net.RegisterOnTaxiParking_answer")) {
			return Packet.REGISTER_ON_TAXI_PARKING_RESPONCE;
		}
		else if (val.equals("IMAP.Net.RelayCommunication")) {
			return Packet.RELAY_COMMUNICATION_RESPONCE;
		}
		else if (val.equals("IMAP.Net.DispOrder")) {
			return Packet.ORDER_RESPONCE;
		}
		else if (val.equals("IMAP.Net.DispOrder2")) {
			return Packet.ORDER_RESPONCE2;
		}
		else if (val.equals("IMAP.Net.DispOrder3")) {
			return Packet.ORDER_RESPONCE3;
		}
		else if (val.equals("IMAP.Net.TCPMessage")) {
			return Packet.TCPMESSAGE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.RequestConfirmation")) {
			return Packet.REQUEST_CONFIRMATION_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetTaxiParkingStatistic_answer")) {
			return Packet.TAXI_PARKING_STATISTIC_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetTaxiParkingsLastChangeDate_answer")) {
			return Packet.TAXI_PARKING_LAST_CHANGE_DATE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetWorkReport_answer")) {
			return Packet.WORK_REPORT_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetTaxiParkings_answer")) {
			return Packet.TAXI_PARKINGS_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetTaxiParkings_answer2")) {
			return Packet.TAXI_PARKINGS_RESPONCE2;
		}
		else if (val.equals("IMAP.Net.UnRegisterOnTaxiParking_answer")) {
			return Packet.UNREGISTER_ON_TAXI_PARKING_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetDriverParkingPosition_answer")) {
			return Packet.DRIVER_PARKING_POSITION_RESPONCE;
		}
		else if (val.equals("IMAP.Net.PPCSettings")) {
			return Packet.PPCSETTINGS_RESPONCE;
		}
		else if (val.equals("IMAP.Net.CallSignChanged")) {
			return Packet.CALL_SIGNCHANGED_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetOrders_answer")
				|| val.equals("IMAP.Net.InnerNamespace.GetOrders_answer")) {
			return Packet.GET_ORDERS_RESPONCE;
		}
		else if (val.equals("IMAP.Net.ExequteQuerryCommand_answer")) {
			return Packet.SQL_RESPONCE;
		}
		else if (val.equals("IMAP.Net.DriverBalanceChange")) {
			return Packet.DRIVER_BALANCE_CHANGED_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetPreliminaryOrders_answer")) {
			return Packet.PREORDERS_RESPONCE;
		}
		else if (val.equals("IMAP.Net.GetCSBalanceAnswer")) {
			return Packet.CSBALANCE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.DriverMessage")) {
			return Packet.DRIVER_MESSAGE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.PPSChangeState")) {
			return Packet.PPSCHANGE_STATE_RESPONCE;
		}
		else if (val.equals("IMAP.Net.SettingXml")) {
			return Packet.SETTINGS_XML_RESPONCE;
		}
		else if (val.equals("IMAP.Net.PingStateAnswer")) {
			return Packet.PING_STATE_ANSWER;
		}
		else if (val.equals("IMAP.Net.SetYourOrders_Answer")) {
			return Packet.SET_YOUR_ORDERS_ANSWER;
		}
		else if (val.equals("IMAP.Net.SearchInEtherOrderOver")) {
			return Packet.ETHEAR_ORDER_OVER_ANSWER;
		}
		else if (val.equals("IMAP.Net.RefusePreliminaryOrder_answer")) {
			return Packet.REFUSE_PRELIMINARY_ORDER_ANSWER;
		}
		else if (val.equals("IMAP.Net.GetRoutesAnswer")) {
			return Packet.GET_ROUTES_ANSWER;
		}
		else if (val.equals("IMAP.Net.SignPrelimOrder_answer")) {
			return Packet.SIGN_PRELIM_ORDER_ANSWER;
		}
		else if (val.equals("IMAP.Net.DriverBlockedPack")) {
			return Packet.DRIVER_BLOCKED_PACK;
		}
		else if (val.equals("IMAP.Net.GetTaximeterRatesAnswer")) {
			return Packet.TAXIMETER_RATES;
		}
		//archivOreders
		else if (val.equals("IMAP.Net.InnerNamespace.GetDoneOrdersAnswer")) {
			Log.d("archivTest", "archivResponse123");
			return Packet.ARCHIV_ORDERS_RESPONSE;
		}
		else if(val.equals("IMAP.Net.From.Stas")) {
			Log.d("archivTest", "archivResponse123");
			return Packet.DISTANCE_FROM_STAS;
		}

		return Packet.UNKNOWN_RESPONCE;
	}
}
