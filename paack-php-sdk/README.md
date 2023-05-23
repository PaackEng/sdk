# Introducción a PHP

Supported PHP Versions:

* PHP 8.1
* PHP 8.2
* PHP 8.3

## Pre-installation

You need to install APCU for the work the SDK. It helps to generation the cache and avoid multiple API calls

### Apcu PSR-6 Cache pool

This is a PSR-6 cache implementation using Apcu. It is a part of the PHP Cache organisation. To read about features like tagging and hierarchy support please read the shared documentation at www.php-cache.com.

## Installation

To use paack-php-sdk in your project initialize php modules then run:

```bash
composer install
```

## Getting Started


When constructing the Paack object to utilize the SDK you must pass your Paack credentials via the constructor. The credentials (clientId and clientSecret) must match the environment you plan to use the SDK with. Using the “domain” argument you must specify either the Staging or Production environment.

### Authentication

The SDK handles the authentication with Paack APIs automatically. You don’t need to worry about it.
Using the credentials provided it will generate a token with the required audience for each API you plan to use. The token is generated lazy, when you first call that API.
Once the token was generated, it will cache it for future use. Every time you call an API it will automatically add

```php
$init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), getenv('ENV'));
```
### Create a new Order with Warehouse Model

```php
$order = new OrderSchema(...);
$init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), getenv('ENV'));
$init->order->CreateWithWarehouse($order);
```

### Query an Order by ID

```php
$init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), getenv('ENV'));
$init->order->getById(ExternalID);
```

### Create and return a label for an order

```php
$init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), getenv('ENV'));
$label = new LabelSchema(...);
$label_format =  LabelFormat::SINGLEZPLE;
$init->label->LabelCreate($label, $label_format);
```

### Retrieve the last status of the order with the specified ID

```php
$init = new Init( getenv('CLIENT_ID'), getenv('CLIENT_SECRET'), getenv('ENV'));
$init->tracking->orderStatusGet(ExternalID);
```

The SDK of PHP use cache 



