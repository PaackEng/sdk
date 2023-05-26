<?php

use Paack\Init;
use Paack\Label\Logic\LabelSchema;
use Paack\Order\Logic\AddressSchema;
use Paack\Order\Logic\CustomerSchema;
use Paack\Order\Logic\ExtraDetail;
use Paack\Order\Logic\MoneySchema;
use Paack\Order\Logic\OrderSchema;
use Paack\Order\Logic\ParcelSchema;
use Paack\Order\Logic\TimeSlot;
use PHPUnit\Framework\TestCase;
use Paack\Label\Interfaces\LabelFormat;

class TestLabel extends TestCase {

    protected array $obj_array = [];
    private Init $init;

    public function setUp(): void
    {
        $this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
    }

    public function testLabelCreatePdf() {
        $order = $this->order();
        $type = LabelFormat::PDF;
        $label = new LabelSchema($order);
        $assets = $this->init->label->LabelCreate($label, $type);
        $this->assertNull(NULL);
    }

    public function testLabelSngleZplLabel() {
        $order = $this->order();
        $type = LabelFormat::SINGLEZPLE;
        $label = new LabelSchema($order);
        $assets = $this->init->label->LabelCreate($label, $type);
        $this->assertNull(NULL);
    }


    public function order(): OrderSchema
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
        //$order_id = $this->createOrderId();
        $order_id = '37b0f85e-ab58-4d51-93d7-a8a37cd6d226';
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

