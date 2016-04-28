package com.innotech.imap_taxi.datamodel;

/**
 * @field sourceOrderWhence - Источник заказа
 * @field orderCostForDriver - Стоимость заказа для водителя
 * @field canFirstForAnyParking - Признак того, что выполнение данного заказа предоставляет
 *      льготу стать первым на любую стоянку.
 * @field distanceToPointOfDelivery - Расстояние от водилы до точки подачи
 * @field concessional - Льготный заказ (списание с баланса = 0)
 * @field waitMinutes -  Время ожидания
 * @field waitMinutesPay - Стоимость времени ожидания
 * */
public class DispOrder4 extends DispOrder3 {
    public String sourceWhence = "";
    public float orderCostForDriver = 0;
    public boolean canFirstForAnyParking = false;
    public float distanceToPointOfDelivery = 0;
    public boolean concessional = false;
    public float waitMinutes;
    public float waitMinutesPay;
}
