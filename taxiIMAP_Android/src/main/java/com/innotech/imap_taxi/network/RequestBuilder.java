package com.innotech.imap_taxi.network;

import android.util.Log;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class RequestBuilder {
	public static final String LOG_TAG = RequestBuilder.class.getSimpleName();
	public static final String TO_DISPETCHER = "Dispatcher";
	public static final String POINT_TO_POINT = "PointToPoint";
	public static final String REASON_REFUSE = "RefuseOrder";
	public static final String DEFAULT_CONNECTION_TYPE = "PointToGroup";
	public static final int DEFAULT_DESTINATION_ID = 2;

	public static byte[] createLogin(int GroupID, String loginName, String password,
									 boolean IsUniqConnection) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SrvLogin", true);

			//GroupID
			stream.writeInt16(GroupID);
			//login
			stream.write(loginName);
			//password
			stream.write(password);
			//IsUniqConnection
			stream.write(IsUniqConnection);
			//PacketNumber
			stream.writeInt32(-1);

			Log.d(LOG_TAG, stream.toByteArray().toString());

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createLogout(int srvID, byte[] guid) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SrvLogOut", true);

			//srvID
			stream.writeInt32(srvID);
			//guid
			stream.write(guid);
			//PacketNumber
			stream.writeInt32(-1);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createPing() {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SrvPing", true);

			//PacketNumber
			stream.writeInt32(-1);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createPingState(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PingState", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createVersionDeviceSoftware(String deviceType, String versionDevice, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.VersionDeviceSoftware", false);

			//Device
			stream.write(deviceType);

			//VersionDevice
			stream.write(versionDevice);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createSrvTransfereData(String connectionType,
												int srvID,
												int destinationID,
												byte[] guid,
												boolean log,
												byte[] body) {
		return createSrvTransfereData(connectionType,
				srvID,
				destinationID,
				guid,
				log,
				body,
				-1);
	}

	public static byte[] createSrvTransfereData(String connectionType,
												int srvID,
												int destinationID,
												byte[] guid,
												boolean log,
												byte[] body,
												int packetNumber) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SrvTransfereData", true);

			//connectionType
			stream.write(connectionType);
			//srvID
			stream.writeInt32(srvID);
			//destinationID
			stream.writeInt32(destinationID);
			//guid
			stream.write(guid);
			//log
			stream.write(log);
			//body
			stream.write(body);
			//PacketNumber
			stream.writeInt32(packetNumber);

			return stream.toByteArray();
		} catch (Exception ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createIMapMessage(String destination, String callSign, String message, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.IMapMessage", false);

			//Destination
			stream.write(destination);
			//CallSign
			stream.write(callSign);
			//Message
			stream.write(message);
			//peopleID
			stream.writeInt32(peopleID);


			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyRegisterOnRelay(String nick, boolean fromDriver, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPCRegisterOnRelay", false);
			//nick
			stream.write(nick);
			//fromDriver
			stream.write(fromDriver);
			//peopleID
			stream.writeInt32(peopleID);
			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetRoutes(int orderId, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetRoutes", false);
			//orderId
			stream.writeInt32(orderId);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyCarOnAddressRequest(int orderId, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.CarOnAddressRequest", false);
			//orderId
			stream.writeInt32(orderId);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyRegisterOnTaxiParking(int parkingID, String nick, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.RegisterOnTaxiParking", false);
			//parkingID
			stream.writeInt32(parkingID);
			//nick
			stream.write(nick);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodySetYourOrders(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SetYourOrders", false);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyPPSChangeState(String driverState, int orderID, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPSChangeState", false);

			//DriverState State
			stream.write(driverState);

			//uint OrderID
			stream.writeInt32(orderID);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetOrdersOfDriver(String filter, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetOrdersOfDriver", false);

			//WhereStatement
			stream.write(filter);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetOrders(String filter) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetOrders", false);

			//WhereStatement
			stream.write(filter);

			return stream.toByteArray();
		} catch (IOException ioex) {return (new byte[]{});}
		finally {
			try {stream.close();}
			catch (Exception ex) {ex.printStackTrace();}
		}
	}

	public static byte[] createBodyCostOfDriverPack(int orderId, int costOfDriver,int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.CostOfDriverPack", false);

			//orderId
			stream.writeInt32(orderId);

			//costOfDriver
			stream.writeInt32(costOfDriver);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createReqPreOrders(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetPrelimOrders", false);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyPPSOrderCancelRequest(int orderID, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPSOrderCancelRequest", false);
			//orderID
			stream.writeInt32(orderID);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createFilteredTaxiParkings(String[] regions, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.FilteredTaxiPаrkings", false);
			//Размер массива
			stream.writeInt32(regions.length);
			//Массив
			for (int i = 0; i < regions.length; ++i) {
				stream.write(regions[i]);
			}
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyOrderCancel(int orderID, String reason, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.OrderCancel", false);
			//orderID
			stream.writeInt32(orderID);
			//reason
			stream.write(reason);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyPickingUpCharge(int orderID, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PickingUpCharge", false);
			//orderID
			stream.writeInt32(orderID);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyCW_ConnectDriverClient(int orderID, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.CW_ConnectDriverClient", false);
			//orderID
			stream.writeInt32(orderID);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyOrderAnswer(int orderID, String reason, String addition, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.OrderAnswer", false);
			//orderID
			stream.writeInt32(orderID);
			//reason
			stream.write(reason);
			//addition
			stream.write(addition);
			//peopleID
			stream.writeInt32(peopleID);
            return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createGetWorkReport(long from, long to, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetWorkReport", false);
			//from
			stream.write(from);
			//to
			stream.write(to);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetDriverState(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetDriverState", false);

			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetDriverParkingPosition(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetDriverParkingPosition", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createDangerWarning(int geoX, int geoY, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.DangerWarning", false);

			//GeoX
			stream.writeInt32(geoX);

			//GeoY
			stream.writeInt32(geoY);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createMoveBackInQueue(int countPositions, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.MoveBackInQueue", false);

			//CountPositions;
			stream.writeInt32(countPositions);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createGetPPCSettingsXML(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetPPCSettingsXML", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetPPCSettings(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetPPCSettings", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetTaxiParkings(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetTaxiParkings", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyUpdateJavaConnectionState(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.UpdateJavaConnectionState", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyP(byte speed, int direction, long time, float xCoord, float yCoord, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.P", false);

			//Speed;
			stream.write(speed);

			//Direction;
			stream.writeInt16(direction);
			//Time;
			stream.writeDate(time);

			//XCoord;
			stream.writeFloat(xCoord);

			//YCoord;
			stream.writeFloat(yCoord);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyPPCOnLineData(float xCoord, float yCoord, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPCOnLineData", false);

			//XCoord;
			stream.writeFloat(xCoord);

			//YCoord;
			stream.writeFloat(yCoord);

			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createBodyGetTaxiParkingsLastChangeDate(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetTaxiParkingsLastChangeDate", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createGetBalanceData(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetCSBalance", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createUnRegisterOnTaxiParking(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.UnRegisterOnTaxiParking", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createGetTaxiParkingStatistic(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetTaxiParkingStatistic", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createTrackContinue(int orderID, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.TrackContinue", false);
			//orderID
			stream.writeInt32(orderID);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createDriverWaiting(int orderID, int delay, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.DriverWaiting", false);
			//orderID
			stream.writeInt32(orderID);
			//delay
			stream.writeInt32(delay);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createRefusePreliminaryOrder(int orderID, String callSign, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.RefusePreliminaryOrder", false);
			//orderID
			stream.writeInt32(orderID);
			//callSign
			stream.write(callSign);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createSignPrelimOrder(int orderID, String callSign, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.SignPrelimOrder", false);
			//orderID
			stream.writeInt32(orderID);
			//callSign
			stream.write(callSign);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createFeedTime(int orderID, int time, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPSOrderDriverTimeUpdate", false);
			//orderID
			stream.writeInt32(orderID);
			//time
			stream.writeInt32(time);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createDriverDelay(int orderID, int delay, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.DriverDelay", false);
			//orderID
			stream.writeInt32(orderID);
			//delay
			stream.writeInt32(delay);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createSQL(String SQLString) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.ExequteQuerryCommand", false);
			//SQLString
			stream.write(SQLString);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createPassengerOut(int orderID, int delay, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PassengerOut", false);
			//orderID
			stream.writeInt32(orderID);
			//delay
			stream.writeInt32(delay);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createDispatcherCall(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.DispatcherCall", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createPPCUnRegisterOnRelay(boolean fromDriver, String type, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.PPCUnRegisterOnRelay", false);
			//fromDriver
			stream.write(fromDriver);
			//type
			stream.write(type);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createRequestAir(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.RequestAir", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createConnectDriverClient(int orderid, int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.CW_ConnectDriverClient", false);
			//orderid
			stream.writeInt32(orderid);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}

	public static byte[] createGetTaximeterRates(int peopleID) {
		BuilderStream stream = null;
		try {
			stream = new BuilderStream("IMAP.Net.GetTaximeterRates", false);
			//peopleID
			stream.writeInt32(peopleID);

			return stream.toByteArray();
		} catch (IOException ioex) {
			return (new byte[]{});
		} finally {
			try {
				stream.close();
			} catch (Exception ex) {
			}
		}
	}


}
