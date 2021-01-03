<?php  
 
    require "init.php"; 

		
	if (isset($_POST["user_id"]) && isset($_POST["search_date"])){

	    $user_id = $_POST["user_id"];  
		$search_date= $_POST["search_date"]; 

 		$sql = "select * from COORDINATES where USER_ID = '$user_id' AND DATE_FORMAT(REC_DATE,'%y-%m-%d') = DATE_FORMAT('$search_date','%y-%m-%d')";
 		// $sql = "select coor.* from COORDINATES coor left outer join users on coor.USER_ID = users.ID where users.NAME = '$user_name' AND DATE_FORMAT(coor.REC_DATE,'%y-%m-%d') = DATE_FORMAT('$search_date','%y-%m-%d')";

 		$result = mysqli_query($conn,$sql);


		if(mysqli_num_rows($result)>0){

			$result_coordinates = array();
			while($row = mysqli_fetch_array($result)){

				array_push($result_coordinates,array(
													 "COOR_X" => isset($row["COOR_X"]) ? $row["COOR_X"] : null 
													,"COOR_Y" => isset($row["COOR_Y"]) ? $row["COOR_Y"] : null
													,"REC_DATE" => isset($row["REC_DATE"]) ? $row["REC_DATE"] : null
							)
				);
				//array_push($result_coordinates,array("NAME"=>$row[1]));
			}

			$responce = array(
				 "success" => true
				,"user" => $result_coordinates
			);


		} else {
			// we CANT LOGIN, user not found

			$responce = array(
				 "success" => false
				,"error" => "Day or user not found"
				// ,"user" => $user->ID
			);
		}

	} else {

		$responce = array(
				 "success" => false
				,"error" => "Please provide user name and search date"
			);

	}


	echo json_encode($responce);

				

/*
SELECT * FROM coordinates 
WHERE USER_ID = 2 
AND DATE_FORMAT(REC_DATE,'%y-%m-%d') = DATE_FORMAT('2016-07-15 00:00:00','%y-%m-%d')


SELECT COOR_X, COOR_Y, users.NAME FROM coordinates
left outer join users on coordinates.USER_ID = users.ID
WHERE USER_ID = 2 AND DATE_FORMAT(REC_DATE,'%y-%m-%d') = DATE_FORMAT('2016-07-15 00:00:00','%y-%m-%d')
*/

?>  