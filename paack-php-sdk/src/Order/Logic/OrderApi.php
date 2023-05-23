<?php

namespace Paack\Order\Logic;

use ErrorException;
use Paack\Config\PaackConfig;
use Paack\Api\Interfaces\BaseApi;
use Paack\Order\Interfaces\AddressSchema;
use Paack\Order\Interfaces\ContactInfoInterface;
use Paack\Order\Interfaces\AddressSchemaInterface;
use Paack\Order\Interfaces\OrderExchangeRequestInterface;
use Paack\Order\Interfaces\OrderSchemaInterface;
use Paack\Order\Interfaces\ParcelSchema;
use Paack\Order\Interfaces\OrderApiInterface;
use Paack\Order\Interfaces\ParcelSchemaInterface;

class OrderApi  implements OrderApiInterface {
    /**
     * @var BaseApi
     */
    protected BaseApi $apiClient;
    /**
     * @var ENTITY_TYPE
     */
    protected const ENTITY_TYPE = 'order';

    /**
     * @var PaackConfig
     */
    private PaackConfig $configuration;

    /**
     * OrderApi constructor.
     * @param BaseApi $api
     */
    public function __construct(BaseApi $api) {
        $this->apiClient = $api;
        $this->configuration = $api->configuration;
    }

    /**
     * @throws ErrorException
     */
    public function createOrderSuccess(OrderSchemaInterface $order): array
    {
        $data = $this->createToNormalization($order);
        return $this->apiClient->post($this->getPath(), $data);
    }

    /**
     * @throws ErrorException
     */
    public function updateOrderResponse(string $order_id, ParcelSchemaInterface $parcel): mixed {
        $path = $this->getPath() . '/' . $order_id;
        $data = $this->createToNormalization($parcel);
        return $this->apiClient->put($path, $data);
    }

    public function deleteOrderResponse(string $order_id){
        $path = $this->getPath() . '/' . $order_id;
        return $this->apiClient->delete($path);
    }

    public function deliveryVerifications(string $order_id)
    {
        $path = $this->getPath() . '/' . $order_id;
        return $this->apiClient->get($path, []);
    }

    /**
     * @throws ErrorException
     */
    protected function createToNormalization(object $object)
    {
        if (is_object($object)) {
            return json_decode(json_encode( (array) $object ), TRUE);
        }
        throw new ErrorException("There isn't an OrderSchemaInterface");
    }

    /**
     * @throws \Exception
     */
    private function getPath() {
        return $this->configuration->getConfigOrder() . $this->configuration->getConfigResources(self::ENTITY_TYPE);
    }

    /**
     * @throws ErrorException
     * @throws \Exception
     */
    public function createWithWarehouse(OrderSchemaInterface $order, $label_format = '')
    {
        $data = $this->createToNormalization($order);

        if ($label_format != '') {
            $path = $this->getPath() . "?include=label&labelFormat=$label_format";
        }
        else {
            $path = $this->getPath();
        }

        return $this->apiClient->post($path, $data);
    }

    /**
     * @throws ErrorException
     */
    public function createWithStore(OrderSchemaInterface $order, $type = '')
    {
        if (empty($order->expected_delivery_ts) && empty($order->expected_pick_up_ts)) {
            throw new Exception('Error while mapping order to CreateRequest payload');
        }
        return $this->createWithWarehouse($order, $type);
    }

    /**
     * @throws ErrorException
     */
    public function updateParcels(string $order_id, array|ParcelSchemaInterface $parcelList)
    {
        if (is_array($parcelList)) {
            foreach ($parcelList as $value) {
                $this->updateParcel($order_id, $value);
            }
        }
        else {
            $this->updateParcel($order_id, $parcelList);
        }
    }

    /**
     * @throws ErrorException
     */
    public function updateParcel(string $order_id, ParcelSchemaInterface $parcel)
    {
        $order = $this->getById($order_id);
        if (isset($order['success'])) {
            $order['success']['parcels'][] = $parcel;
            // Get the URL.
            return $this->updateOrder($order);
        }

    }

    /**
     * @throws \Exception
     */
    public function getById(string $order_id)
    {
        $path = $this->getPath() . '/' . $order_id;
        $content = $this->apiClient->get($path, []);
        return json_decode($content['body'], true);
    }

    /**
     * @throws \Exception
     */
    public function updateDeliveryAddress(string $order_id, AddressSchemaInterface $address)
    {
        $order = $this->GetById($order_id);
        if (isset($order['success'])) {
            $order['success']['delivery_address'] = $address;
            // Get the URL.
            return $this->updateOrder($order);
        }
    }

    /**
     * @throws \Exception
     */
    public function updateCustomerContactDetails(string $order_id,$email, $phone, $has_gdpr_consent)
    {
        $order = $this->GetById($order_id);
        if (isset($order['success'])) {
            $order['success']['customer']['email'] = $email;
            $order['success']['customer']['phone'] = $phone;
            $order['success']['customer']['has_gdpr_consent'] = $has_gdpr_consent;
            // Get the URL.
            return $this->updateOrder($order);
        }

    }

    /**
     * @throws ErrorException
     */
    public function updateOrder($order)
    {
        $path = $this->getPath() . '/' . $order['success']['external_id'];
        return $this->apiClient->put($path, $order['success']);
    }

    /**
     * @throws \Exception
     */
    public function upsertOrderWithWarehouse(OrderSchemaInterface $order)
    {
        $get_order = $this->GetById($order->external_id);
        if (empty($get_order)) {
            $this->createOrderWithWarehouse($order);
        }
        else {
            return $this->updateOrder($order);
        }
    }

    /**
     * @throws \Exception
     */
    public function upsertOrderWithStore(OrderSchemaInterface $order)
    {
        $get_order = $this->GetById($order->external_id);
        if (empty($get_order)) {
            return $this->createOrderWithStore($order);
        }
        else {
            return $this->updateOrder($order);
        }

    }

    public function exchangeWithWarehouse(OrderExchangeRequestInterface  $request)
    {
        $direct = $request->direct;
        $direct->delivery_type = 'direct';
        $this->createWithStore($direct);

        $reverse = $request->reverse;
        $reverse->delivery_type = 'return';
        $this->createWithStore($reverse);
    }

    public function exchangeWithStore(OrderExchangeRequestInterface $request)
    {
        // TODO: Implement ExchangeWithStore() method.
    }

    public function cancelRequest(string $order_id)
    {
        $path = $this->getPath() . '/' . $order_id;
        return $this->apiClient->delete($path);
    }
}
