<?php

namespace Paack\Api\Interfaces;

interface BaseApi {

    public const OK = 200;
    public const CREATED = 201;
    public const PARTIAL_CONTENT = 206;

    public const BAD_REQUEST = 400;
    public const UNAUTHORIZED = 401;
    public const NOT_FOUND = 404;
    public const FORBIDDEN = 403;
    public const METHOD_NOT_ALLOWED = 405;
    public const REQUEST_TIMEOUT = 408;

    public const SERVER_GATEWAY_TIMEOUT = 504;
    public const INTERNAL_SERVER_ERROR = 500;
    public const NOT_IMPLEMENTED = 501;

    /**
     * @param string $url
     * @param array $params
     * @return mixed
     */
    public function get(string $url, array $params): mixed;

    /**
     * @param string $url
     * @param array $payload
     * @return mixed
     */
    public function post(string $url, array $payload);

    /**
     * @param string $url
     * @param array $params
     * @return mixed
     */
    public function put(string $url, array $params);

    /**
     * @param string $url
     * @param array $params
     * @return mixed
     */
    public function patch(string $url, array $params);

    /**
     * @param string $url
     * @param array $params
     * @return mixed
     */
    public function delete(string $url): mixed;

    /**
     * @return array
     */
    public function getConfiguration(): array;

    /**
     * @param array $config
     * @return array
     */
    public function setConfiguration(array $config): array;
}
