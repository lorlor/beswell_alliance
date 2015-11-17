<?php
try{
	$client = new SoapClient(null, array('location'=>"http://192.168.0.100/ALServer.php", 'uri'=>"http://192.168.0.100/ALServer.php"));

	$username = "dell";
	$pwd = md5("asdfasdf");
	$openid = '';
	$ip = "";
	$agent = "";

	$st = "2015-10-19";
	$et = "2015-10-19";

	$hashStr = "434a4f48ed470a94a315f9869c6a6299";
	$alid = "6600";
	$flag = 99;

	$shopName = "dell";
	$shopLoc = "测试-文化路西";
	$ccaCost = 100;
	$cca2Cost = 200;
	$ccbCost = 300;
	$contact = "测试";
	$phone = "0000-12345678";
	$L = 0;
	$D = 0;

	echo $pwd."<br/>";
	echo "Before<br/>";

	// $res = $client->al_login($username, $pwd, $openid, $ip, $agent);
	// $res = $client->al_noticeCheck($hashStr, $alid);
	// $res = $client->al_getID(4);
	// $res = $client->al_getInfo($hashStr, $alid);
	// $res = $client->al_queryCCRecord($hashStr, $alid);
	// $res = $client->al_queryCCRecordByDate($hashStr, $alid, $st, $et);
	// $res = $client->al_query($hashStr, $alid, $flag);
	// $res = $client->al_info($hashStr, $alid);
	// $res = $client->al_getShopInfo($hashStr, $alid);
	$res = $client->al_submit($hashStr, $alid, $shopName, $shopLoc, $ccaCost, $cca2Cost, $ccbCost, $contact, $phone, $L, $D);

	echo "Biu----After<br/>";

	var_dump($res);
}
catch(SoapFault $f){
	$f->getMessage();
}

echo "<br>Done";
?>