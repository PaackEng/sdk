<?php


use Paack\Init;
use Paack\Order\Logic\AddressSchema;
use Paack\Order\Logic\CustomerSchema;
use Paack\Order\Logic\ExtraDetail;
use Paack\Order\Logic\MoneySchema;
use Paack\Order\Logic\OrderExchangeRequest;
use Paack\Order\Logic\OrderSchema;
use Paack\Order\Logic\ParcelSchema;
use Paack\Order\Logic\TimeSlot;
use PHPUnit\Framework\TestCase;

class TestOrder extends TestCase
{

    private Init $init;

    public function setUp(): void
    {
        //$this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
        $this->init = new Init('47SoYRqAZWdd26Cify0fwLlxtsO50F4R', 'MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp', 'staging');$this->createOrderId();
    }

    public function createOrderId() {
        return "200000V3009" . sprintf("%d", rand(0, 1000));
    }

    public function testCreateWithWarehouse() {
        $order = $this->order();
        $assets = $this->init->order->createWithWarehouse($order);
        $this->assertNull($assets);
    }

    public function testCreateZplLabel()
    {
        $order = $this->order();
	    $labelFormat = 'zpl';
        $assets = $this->init->order->createWithWarehouse($order, $labelFormat);
        $this->assertNull($assets);

    }

    public function testCreateWithStore()
    {
        $order = $this->order();
        $order->external_id = $this->createOrderId();
        $assets = $this->init->order->createWithStore($order);
        $this->assertNull($assets);
    }

    public function testGetById()
    {
        $content = $this->init->order->getById("200000V30090");
        $assets =  $content['success'];
	    $this->assertNotNull($content['body']);
	}

	public function testUpdateParcel() {
        $order_details[] = new ExtraDetail('Jose', 'string', '10000');
        $parcel = new ParcelSchema(
            '0347248734534534545364351',
            '20.2',
            '100.30',
            '3.30',
            '8.30',
            '15.4',
            'standard',
            'kg',
            'mm',
            $order_details);
        $assets = $this->init->order->updateParcel('200000V30090', $parcel);

        $this->assertNotNull($assets);

    }

    public function testUpdateDeliveryAddress() {
        $delivery_address =  new AddressSchema(
            'Bacerlona',
            'ES',
            'Hello Testing',
            '08021',
            '',
            'Comtat de Barcelona',
            'Leave at the door'
        );
        $assets = $this->init->order->updateDeliveryAddress('200000V30090', $delivery_address);
        $this->assertNotNull($assets);
    }

    public function testUpdateCustomerContactDetails() {

        $email = 'jose.medina@globant.com';
        $phone = '+34397341804';
        $has_gdpr_consent = TRUE;

        $assets = $this->init->order->updateCustomerContactDetails('200000V30090', $email, $phone, $has_gdpr_consent);
        $this->assertNotNull($assets);
    }

    public function testUpsertWithWarehouse() {
        $order = $this->orderUpdate();
        $assets = $this->init->order->upsertOrderWithStore($order);
        $this->assertNotNull($assets);
    }

    public function testExchangeWithWarehouse() {
        $direct = $this->order();
        $direct->external_id = $this->createOrderId();

        $reverse = $this->order();
        $reverse->external_id = $this->createOrderId();


        $order_exchange = new OrderExchangeRequest($direct, $reverse);
        $this->init->order->exchangeWithWarehouse($order_exchange);
    }

    public function order(): OrderSchema
    {
        $customer = new CustomerSchema('Jose', 'medina', 'josmera01@gmail.com', '+34397341804', '1',
            'Comtat de Barcelona', 'es');

        $delivery_address =  new AddressSchema('Barcelona', 'ES', '4, apartment block 100, floort 10', '08021', '', 'Comtat de Barcelona', 'Leave at the door');

        $start = new DateTime('2023-09-20 08:00:00');
        $end = new DateTime('2023-09-20 10:00:00');
        $expected_delivery_ts = new TimeSlot($start, $end);
        $order_details[] = new ExtraDetail('Jose', 'string', '10000');
        $parcels[] = new ParcelSchema(
            '0347248734534534545364351',
            '12.2',
            '1.30',
            '1.30',
            '1.30',
            '14.4',
            'standard',
            'kg',
            'cm',
            $order_details);
        $pick_up_address = new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Check opening hours');

        $undeliverable_address = new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Open parcel');
        $insured = new MoneySchema('1', '1');
        $clusters = '3e8b9bdf-d1a1-4e6c-a3af-76ba29ct9bcc';
        $order_id = $this->createOrderId();
         return new OrderSchema(
             $order_id,
            'CFA',
            'direct',
            $customer,
            $delivery_address,
            $expected_delivery_ts,
            $parcels,
            $pick_up_address,
            $order_details,
            $expected_delivery_ts,
            $undeliverable_address,
            $insured,
            $insured,
            $clusters
        );
    }

    public function orderUpdate(): OrderSchema
    {
        $customer = new CustomerSchema('Jose', 'medina', 'josmera01@gmail.com', '+34397341804', '1',
            'Comtat de Barcelona', 'es');
        $delivery_address =  new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Leave at the door');

        $start = new DateTime('2023-09-20 08:00:00');
        $end = new DateTime('2023-09-20 10:00:00');
        $expected_delivery_ts = new TimeSlot($start, $end);
        $order_details[] = new ExtraDetail('Jose', 'string', '10000');
        $parcels[] = new ParcelSchema(
            '0347248734534534545364351',
            '12.2',
            '1.30',
            '1.30',
            '1.30',
            '14.4',
            'standard',
            'kg',
            'cm',
            $order_details);
        $pick_up_address = new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Check opening hours');

        $undeliverable_address = new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Open parcel');
        $insured = new MoneySchema('1', '1');
        $clusters = '3e8b9bdf-d1a1-4e6c-a3af-76ba29ct9bcc';
        $order_id = $this->createOrderId();
        return new OrderSchema(
            $order_id,
            'CFA',
            'direct',
            $customer,
            $delivery_address,
            $expected_delivery_ts,
            $parcels,
            $pick_up_address,
            $order_details,
            $expected_delivery_ts,
            $undeliverable_address,
            $insured,
            $insured,
            $clusters
        );
    }

}

