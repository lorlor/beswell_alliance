<?php
class ALSoapHandle{

	var $db = '';
	
	public function __construct(){
		$this->db = new PDO("sqlsrv:Server=192.168.0.2;Database=Beswell_v2", "sa", "asdf*123");
	}

	public function al_login($username, $pwd, $openid, $ip, $agent){
		$stCode = -1;
		$hashStr = '';

		$cmd = "exec [dbo].[PR_AL_Login] :uid, :pwd, :openid, :ip, :agent, :stCode, :hashStr";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":uid", $username, PDO::PARAM_STR);
		$st->bindParam(":pwd", $pwd, PDO::PARAM_STR);
		$st->bindParam(":openid", $openid, PDO::PARAM_STR);
		$st->bindParam(":ip", $ip, PDO::PARAM_STR);
		$st->bindParam(":agent", $agent, PDO::PARAM_STR);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);
		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR | PDO::PARAM_INPUT_OUTPUT, 32);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		if($stCode == 0)
			return $rs[0]["alid"].".".$hashStr;
		else
			return "False";
	}

	public function al_logout($uid, $hashStr){
		$stCode = -1;

		$cmd = "exec [dbo].[PR_AL_Logout] :uid, :hashStr, :stCode";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":uid", $uid, PDO::PARAM_STR);
		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$st->nextRowset();

		return $stCode;
	}

	//信息核验
	public function al_noticeCheck($hashStr, $alid){
		$stCode = -1;

		$cmd = "exec [dbo].[PR_AL_NoticeCheck] :hashStr, :alid, :stCode";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_STR);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		return $rs[0]['cc'].".".$rs[0]['ccdaycount'];
	}

	public function al_queryCCRecord($hashStr, $alid){
		$stCode = -1;

		$cmd = "exec [dbo].[PR_AL_CC_RecordQuery] :hashStr, :alid, :stCode";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_STR);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		if($stCode == 0){
			$res = array();
			for($i = 0; $i < count($rs); $i++){
				$res[$i][0] = $rs[$i]['ccTime'];
				$res[$i][1] = $rs[$i]['plate'];
				$res[$i][2] = $rs[$i]['consumeType'];
				$res[$i][3] = $rs[$i]['cctimes'];
			}

			return $res;
		}
		else
			return "False";
	}

	public function al_queryCCRecordByDate($hashStr, $alid, $dt1, $dt2){
		$stCode = -1;

		$cmd = "exec [dbo].[PR_AL_CC_RecordQueryByDate] :hashStr, :alid, :dt1, :dt2, :stCode";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_STR);
		$st->bindParam(":dt1", $dt1, PDO::PARAM_STR);
		$st->bindParam(":dt2", $dt2, PDO::PARAM_STR);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		if($stCode == 0){
			$res = array();
			for($i = 0; $i < count($rs); $i++){
				$res[$i][0] = $rs[$i]['ccTime'];
				$res[$i][1] = $rs[$i]['plate'];
				$res[$i][2] = $rs[$i]['ccType'];
				$res[$i][3] = $rs[$i]['cctimes'];
				$res[$i][4] = $rs[$i]['cardCode'];
				$res[$i][5] = $rs[$i]['star'];
				$res[$i][6] = $rs[$i]['comment'];
			}

			return $res;
		}
		else
			return "False";
	}

	public function al_query($hashStr, $alid, $flag){
		/* @flag
		*		0 - 未审核;
		*		1 - 已审核;
		*		99 - 全部;*/
		$stCode = -1;

		$cmd = "exec [dbo].[PR_AL_CC_SaleGet] :hashStr, :alid, :flag, :stCode";
		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_STR);
		$st->bindParam(":flag", $flag, PDO::PARAM_INT);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		if($stCode == 0){
			$res = array();
			for($i = 0; $i < count($rs); $i++){
				$res[$i][0] = $rs[$i]['CDT'];
				$res[$i][1] = $rs[$i]['cardcode'];
				$res[$i][2] = $rs[$i]['cctype'];
				$res[$i][3] = $rs[$i]['cost'];
				$res[$i][4] = $rs[$i]['plate'];
				$res[$i][5] = $rs[$i]['st'];
			}

			return $res;
		}
		else
			return "False";

		// return $rs;
	}

	public function al_info($hashStr, $alid){
		$stCode = -1;
		$cmd = "exec [dbo].[PR_AL_Info] :hashStr, :alid, :stCode";

		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_INT);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		return $rs;
	}

	public function al_getShopInfo($hashStr, $alid){
		$stCode = -1;
		$cmd = "exec [dbo].[PR_AL_CC_GetShopInfo] :hashStr, :alid, :stCode";

		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_INT);

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		$res = array();
		for($i = 0; $i < count($rs); $i++){
			$res[$i][0] = $rs[$i]['shopName'];
			$res[$i][1] = $rs[$i]['contactPerson'];
			$res[$i][2] = $rs[$i]['contactPhone'];
			$res[$i][3] = $rs[$i]['shopLoc'];
			$res[$i][4] = $rs[$i]['ccaCost'];
			$res[$i][5] = $rs[$i]['cca2Cost'];
			$res[$i][6] = $rs[$i]['ccbCost'];
			$res[$i][7] = $rs[$i]['st'];
		}

		return $res;
/*
		if($stCode == 0){
			$res = array();
			for($i = 0; $i < count($rs); $i++){
				$res[$i][0] = $rs[$i]['shopName'];
				$res[$i][1] = $rs[$i]['contactPerson'];
				$res[$i][2] = $rs[$i]['contactPhone'];
				$res[$i][3] = $rs[$i]['shopLoc'];
				$res[$i][4] = $rs[$i]['ccaCost'];
				$res[$i][5] = $rs[$i]['cca2Cost'];
				$res[$i][6] = $rs[$i]['ccbCost'];
				$res[$i][7] = $rs[$i]['st'];
			}

			return $res;
		}
		else
			return "False";

		// return $rs;*/
	}

	public function al_submit($hashStr, $alid, $shopName, $shopLoc, $ccaCost, $cca2Cost, $ccbCost, $contact, $phone, $L, $D){
		$stCode = -1;
		$cmd = "exec [dbo].[PR_AL_CC_Apply] :hashStr, :alid, :shopName, :shopLoc, :ccaCost, :cca2Cost, :ccbCost, :contact, :phone, :L, :D, :stCode";

		$st = $this->db->prepare($cmd);

		$st->bindParam(":hashStr", $hashStr, PDO::PARAM_STR);
		$st->bindParam(":alid", $alid, PDO::PARAM_STR);
		$st->bindParam(":shopName", $shopName, PDO::PARAM_STR);
		$st->bindParam(":shopLoc", $shopLoc, PDO::PARAM_STR);
		$st->bindParam(":ccaCost", $ccaCost, PDO::PARAM_INT);
		$st->bindParam(":cca2Cost", $cca2Cost, PDO::PARAM_INT);
		$st->bindParam(":ccbCost", $ccbCost, PDO::PARAM_INT);
		$st->bindParam(":contact", $contact, PDO::PARAM_STR);
		$st->bindParam(":phone", $phone, PDO::PARAM_STR);
		$st->bindParam(":L", $L, PDO::PARAM_INT);			//纬度
		$st->bindParam(":D", $D, PDO::PARAM_INT);			//经度

		$st->bindParam(":stCode", $stCode, PDO::PARAM_INT | PDO::PARAM_INPUT_OUTPUT, PDO::SQLSRV_PARAM_OUT_DEFAULT_SIZE);

		$st->execute();
		$rs = $st->fetchAll();
		$st->nextRowset();

		if($stCode == 0)
			return "Success";
		else
			return "Failure";
	}
}
?>