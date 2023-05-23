<?php

namespace Paack\Order\Interfaces;

use Paack\Order\Interfaces\ContactInfoInterface;
use Paack\Order\Interfaces\ParcelSchema;
use Paack\Order\Interfaces\AddressSchema;
use Paack\Order\Interfaces\OrderSchemaInterface;

interface OrderApiInterface {

    public function createOrderSuccess(OrderSchemaInterface $order);

    public function updateOrderResponse(string $order_id, ParcelSchemaInterface $parcel);

    public function createWithWarehouse(OrderSchemaInterface $order, $label_format = '');

    public function createWithStore(OrderSchemaInterface $order, $type);

    public function updateParcels(string $order_id, array|ParcelSchemaInterface $parcelList);

    public function getById(string $order_id);

    public function updateParcel(string $order_id, ParcelSchemaInterface $parcel);

    public function updateDeliveryAddress(string $order_id, AddressSchemaInterface $address);

    public function updateCustomerContactDetails(string $order_id, $email, $phone, $has_gdpr_consent);

    public function updateOrder(OrderSchemaInterface $order);

    public function upsertOrderWithWarehouse(OrderSchemaInterface $order);

    public function upsertOrderWithStore(OrderSchemaInterface $order);

    public function exchangeWithWarehouse(OrderExchangeRequestInterface $request);

    public function exchangeWithStore(OrderExchangeRequestInterface $request);

    public function cancelRequest(string $order_id);

    public function deleteOrderResponse(string $order_id);

    public function deliveryVerifications(string $order_id);

}
