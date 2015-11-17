<?php
require_once("ALSoapHandle.class.php");

try{
	$server = new SoapServer(null, array('uri'=>"http://192.168.0.100/ALServer.php"));
	$server->setClass('ALSoapHandle');
	$server->handle();
}
catch(SoapFault $f){
	$f->faultString;
}
?>