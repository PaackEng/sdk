<?php


namespace Paack\Order\Logic;


use Exception;
use Paack\Order\Interfaces\ExtraDetailInterface;
use Paack\Order\Interfaces\CustomerSchemaInterface;
use Paack\Order\Interfaces\AddressSchemaInterface;
use Paack\Order\Interfaces\MoneySchemaInterface;
use Paack\Order\Interfaces\OrderSchemaInterface;
use Paack\Order\Interfaces\ParcelSchemaInterface;
use Paack\Order\Interfaces\TimeSlotInterface;

class OrderSchema implements OrderSchemaInterface {
    /**
     * @var string
     */
    public string $external_id = '';
    /**
     * @var string
     */
    public string $service_type = '';
    /**
     * @var CustomerSchemaInterface
     */
    public CustomerSchemaInterface $customer;
    /**
     * @var AddressSchemaInterface
     */
    public AddressSchemaInterface $delivery_address;
    /**
     * @var TimeSlotInterface
     */
    public TimeSlotInterface $expected_delivery_ts;
    /**
     * @var array|ParcelSchemaInterface
     */
    public array|ParcelSchemaInterface $parcels;
    /**
     * @var AddressSchemaInterface
     */
    public AddressSchemaInterface $pick_up_address;
    /**
     * @var array|ExtraDetailInterface
     */
    public  array|ExtraDetailInterface $order_details;
    /**
     * @var TimeSlotInterface
     */
    public TimeSlotInterface $expected_pick_up_ts;
    /**
     * @var AddressSchemaInterface
     */
    public AddressSchemaInterface $undeliverable_address;
    /**
     * @var MoneySchemaInterface
     */
    public MoneySchemaInterface $insured;
    /**
     * @var MoneySchemaInterface
     */
    public MoneySchemaInterface $cash_on_delivery;
    /**
     * @var array|string
     */
    public array|string $clusters;
    public bool $with_order_label;
    public bool $with_parcel_label;
    public bool $with_response;
    public string $delivery_instructions;
    public string $undeliverable_instructions;
    public string $pick_up_instructions;
    public string $delivery_type;


    /**
     * @throws Exception
     */
    public function __construct(
        string $external_id,
        string $service_type,
        string $delivery_type,
        CustomerSchemaInterface $customer,
        AddressSchemaInterface $delivery_address,
        TimeSlotInterface $expected_delivery_ts,
        array|ParcelSchemaInterface $parcels,
        AddressSchemaInterface $pick_up_address,
        array|ExtraDetailInterface $order_details,
        TimeSlotInterface $expected_pick_up_ts,
        AddressSchemaInterface $undeliverable_address,
        MoneySchemaInterface $insured,
        MoneySchemaInterface $cash_on_delivery,
        bool $with_order_label = false,
        bool $with_parcel_label = false,
        bool $with_response = false
    ) {
        $this->external_id = $external_id;
        $this->service_type = $service_type;
        $this->delivery_type = $delivery_type;
        $this->customer = $customer;
        $this->delivery_address = $delivery_address;
        $this->expected_delivery_ts = $expected_delivery_ts;
        $this->parcels = $parcels;
        $this->pick_up_address = $pick_up_address;
        $this->order_details = $order_details;
        $this->expected_pick_up_ts = $expected_pick_up_ts;
        $this->undeliverable_address = $undeliverable_address;
        $this->insured = $insured;
        $this->cash_on_delivery = $cash_on_delivery;
        $this->with_order_label = $with_order_label;
        $this->with_parcel_label = $with_parcel_label;
        $this->with_response = $with_response;


        if (empty($this->external_id)) {
            throw new Exception('External ID is require');
        }
        if (!empty($this->external_id) && strlen($this->external_id) > 128) {
            throw new Exception('External ID should have a 128 max length');
        }
        if (!is_array($this->parcels) && $this->parcels->barcode && strlen($this->parcels->barcode) <= 128) {
            throw new Exception('Barcode is require');
        }
        if (is_array($this->parcels)) {
            foreach ($this->parcels as  $value) {
                if (strlen($value->barcode) >= 128) {
                    throw new Exception('Barcode is require');
                }
            }
        }

        if (strlen($this->delivery_address->city) > 128) {
            throw new Exception('Error while validating payload: delivery_address city can take up to 58 characters');
        }

        if (strlen($this->delivery_address->line1) > 128 || (!empty($this->delivery_address->line2) && strlen($this->delivery_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }

        if (strlen($this->pick_up_address->country) > 128) {
            throw new Exception('Error while validating payload: delivery_address city can take up to 58 characters');
        }

        if (strlen($this->pick_up_address->line1) > 128 || (!empty($this->pick_up_address->line2) && strlen($this->pick_up_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }

        if (strlen($this->undeliverable_address->country) > 128) {
            throw new Exception('Error while validating payload: delivery_address city can take up to 58 characters');
        }

        if (strlen($this->undeliverable_address->line1) > 128 || (!empty($this->undeliverable_address->line2) && strlen($this->delivery_address->line2) > 60)) {
            throw new Exception('Error while validating payload: a max. of approx. 60 characters (2 graphic lines) can be displayed for delivery_address line1 and line2 combined');
        }
        $this->delivery_instructions = $this->delivery_address->instructions;
        $this->pick_up_instructions = $this->pick_up_address->instructions;
        $this->undeliverable_instructions = $this->undeliverable_address->instructions;

    }

}
