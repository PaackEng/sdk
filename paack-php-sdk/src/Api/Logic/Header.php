<?php

namespace Paack\Api\Logic;

abstract class Header {
    static public function defaultHeader($token = '') {
    	if ($token) {
    		return [
    			"Content-Type" => "application/json",
                "Authorization" => "Bearer {$token}",
             ];
        }
        else {
        	return [
        		"Content-Type" => "application/json",
            ];
        }
   }
}
