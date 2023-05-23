<?php

namespace Paack\Cache;

use Cache\Adapter\Apcu\ApcuCachePool;


class PaackCache implements PaackCacheInterces {
    /**
     * @var ApcuCachePool
     */
    public ApcuCachePool $cache;

    public function __construct() {
        $this->cache = new ApcuCachePool();
    }

    public function getCache($id) {
        return $this->cache->get($id);
    }

    public function existId($id) {
        return $this->cache->has($id);
    }

    public function setCache($id, $content, $time) {
        return $this->cache->set($id, $content, $time);
    }

    public function deleteCache($id) {
        return $this->cache->delete($id);
    }
}
