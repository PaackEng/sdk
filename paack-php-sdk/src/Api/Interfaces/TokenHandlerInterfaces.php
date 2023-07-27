<?php


namespace Paack\Api\Interfaces;


interface TokenHandlerInterfaces {
    /**
     * @var string
     */
    const GRANT_TYPE = 'client_credentials';

    /**
     * @return mixed|void
     */
    public function retrieveToken();
}