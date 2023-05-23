<?php

namespace Paack\Label\Logic;

use Exception;
use Paack\Label\Interfaces\LabelSchemaInterface;
use Paack\Order\Interfaces\OrderSchemaInterface;

class LabelSchema implements LabelSchemaInterface {

    public OrderSchemaInterface $order;

    /**
     * LabelSchema constructor.
     * @param OrderSchemaInterface $order
     * @throws Exception
     */
    public function __construct(OrderSchemaInterface $order)
    {
        $this->order = $order;

        if (empty($this->order->external_id)) {
            throw new Exception('External ID is require');
        }
        if (!empty($this->order->external_id) && strlen($this->order->external_id) > 128) {
            throw new Exception('External ID should have a 30 max length');
        }
        if (!is_array($order->parcels) && $order->parcels->barcode && strlen($order->parcels->barcode) <= 21) {
            throw new Exception('Barcode is require');
        }

        if (is_array($order->parcels)) {
            foreach ($order->parcels as $key => $value) {
                if (strlen($value->barcode) >= 128) {
                    throw new Exception('Barcode is require');
                }
            }
        }

        if (strlen($this->order->delivery_address->city) > 58) {
            throw new Exception('Error while validating payload: delivery_address city can take up to 58 characters');
        }

        if (strlen($this->order->delivery_address->line1) > 60 || (!empty($this->order->delivery_address->line2) && strlen($this->order->delivery_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }

        if (strlen($this->order->pick_up_address->country) > 43) {
            throw new Exception('Error while validating payload: delivery_address city can take up to 58 characters');
        }

        if (strlen($this->order->pick_up_address->line1) > 43 || (!empty($this->order->pick_up_address->line2) && strlen($this->order->pick_up_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }

        if (strlen($this->order->undeliverable_address->country) > 43) {
            throw new Exception('Error while validating payload: undeliverable_address city can take up to 58 characters');
        }

        if (strlen($this->order->undeliverable_address->line1) > 60 || (!empty($this->order->undeliverable_address->line2) && strlen($this->order->undeliverable_address->delivery_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }

    }
}
