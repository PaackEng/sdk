<?php


use Paack\Init;
use Paack\Order\Interfaces\AddressSchemaInterface;
use Paack\Order\Logic\AddressSchema;
use Paack\Retailer\Logic\RetailerSchema;
use PHPUnit\Framework\TestCase;

class TestRetailer extends TestCase
{

    private Init $init;

    public function setUp(): void
    {
        //$this->init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), 'staging');
        $this->init = new Init('47SoYRqAZWdd26Cify0fwLlxtsO50F4R', 'MxVunSMrU-8UDOqpUeR7aTzVF7lB4XVz-8jjEjsO4tN_4xZlg54_iTGlnQYN9Plp', 'staging');
    }
    //{"success":{"retailer_location_id":"9024a6d9-41c5-42a8-99a2-2d30519726c8"}}
    public function testCreateRetailerLocation() {
        $retailer = $this->getRetailer();
        $assets = $this->init->retailer->createRetailerLocation($retailer);
        $this->assertNotNull($assets);
    }

    public function testGetRetailerLocation() {
        $content = [
            'retailer_id' => "cfbd703d-724d-41f1-b768-262948ab9a3f",
        ];
        $assets = $this->init->retailer->getRetailerLocation($content);
        $this->assertNotNull($assets);
    }

    public function testUpdateRetailerLocation() {
        $id = "9024a6d9-41c5-42a8-99a2-2d30519726c8";
        $update = $this->getRetailer();
        $update->alias = 'Maremagnum8';
        $update->location_name = 'The Maremagnum8';
        $assets = $this->init->retailer->updateRetailerLocation($id, $update);
        $this->assertNotNull($assets);
    }

    public function getRetailer() {
        $address = new AddressSchema('Barcelona', 'ES', 'Via Augusta', '08021', '', 'Comtat de Barcelona', 'Open parcel');
        $retailer_name = 'H&M';
        $location_name = 'The Maremagnum7';
        $alias = 'Maremagnum7';
        $type = 'Store';
        $retailer_id = $this->createId();
        return new RetailerSchema($retailer_name,  $location_name,  $address,  $alias, $type,  $retailer_id);
    }

    public function createId() {
        // Generate 16 bytes (128 bits) of random data or use the data passed into the function.
        $data = $data ?? random_bytes(16);
        assert(strlen($data) == 16);

        // Set version to 0100
        $data[6] = chr(ord($data[6]) & 0x0f | 0x40);
        // Set bits 6-7 to 10
        $data[8] = chr(ord($data[8]) & 0x3f | 0x80);

        // Output the 36 character UUID.
        return vsprintf('%s%s-%s-%s-%s-%s%s%s', str_split(bin2hex($data), 4));
    }

}

