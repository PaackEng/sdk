<?php

namespace Paack\Cache;

interface PaackCacheInterces {
    /**
     * @param $id
     * @return mixed
     */
    public function getCache($id);

    /**
     * @param $id
     * @param $content
     * @param $time
     * @return mixed
     */
    public function setCache($id, $content, $time);

    /**
     * @param $id
     * @return mixed
     */
    public function deleteCache($id);

    /**
     * @param $id
     * @return mixed
     */
    public function existId($id);
}