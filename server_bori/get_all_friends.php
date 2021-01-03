<?php  
 
    require "init.php"; 

		
	if (isset($_POST["user_id"])){

	    $user_id = $_POST["user_id"];

 		$sql = "SELECT DISTINCT USERS.ID, USERS.NAME, COORDINATES.COOR_X, COORDINATES.COOR_Y, COORDINATES.REC_DATE FROM 
				(SELECT CASE WHEN USER_ID_ONE <> $user_id THEN USER_ID_ONE ELSE USER_ID_TWO END AS FRIEND_ID FROM RELATIONS WHERE (USER_ID_ONE = $user_id OR USER_ID_TWO = $user_id) AND REQUEST_STATUS = 1) FRIENDS
				LEFT OUTER JOIN USERS ON FRIENDS.FRIEND_ID = USERS.ID
				LEFT OUTER JOIN (SELECT USER_ID, MAX(REC_DATE) AS MAX_REC_DATE FROM COORDINATES GROUP BY USER_ID) MAX_DATE ON FRIENDS.FRIEND_ID = MAX_DATE.USER_ID
				LEFT OUTER JOIN COORDINATES ON FRIENDS.FRIEND_ID = COORDINATES.USER_ID AND COORDINATES.REC_DATE = MAX_DATE.MAX_REC_DATE
				order by 2;";

 		$result = mysqli_query($conn,$sql);


		if($result !== false && mysqli_num_rows($result)>0){ 

			$user_friends = array();
			while($row = mysqli_fetch_array($result)){

				array_push($user_friends,array(
													 "ID" => isset($row["ID"]) ? $row["ID"] : null 
													,"NAME" => isset($row["NAME"]) ? $row["NAME"] : null
													,"COOR_X" => isset($row["COOR_X"]) ? $row["COOR_X"] : null
													,"COOR_Y" => isset($row["COOR_Y"]) ? $row["COOR_Y"] : null
													,"REC_DATE" => isset($row["REC_DATE"]) ? $row["REC_DATE"] : null
							)
				);
				//array_push($user_friends,array("NAME"=>$row[1]));
			}

			$responce = array(
				 "success" => true
				,"users" => $user_friends
			);


		}  else {
			// we CANT LOGIN, user not found

			$responce = array(
				 "success" => false
				,"error" => "No friends found"
				// ,"user" => $user->ID
			);
		}

	} else {

		$responce = array(
				 "success" => false
				,"error" => "Please provide user id."
			);

	}


	echo json_encode($responce);

				

/*
SELECT DISTINCT USERS.ID, USERS.NAME, COORDINATES.COOR_X, COORDINATES.COOR_Y, COORDINATES.REC_DATE FROM 
(SELECT CASE WHEN USER_ID_ONE <> 4 THEN USER_ID_ONE ELSE USER_ID_TWO END AS FRIEND_ID FROM RELATIONS WHERE (USER_ID_ONE = 4 OR USER_ID_TWO = 4) AND REQUEST_STATUS = 1) FRIENDS
LEFT OUTER JOIN USERS ON FRIENDS.FRIEND_ID = USERS.ID
LEFT OUTER JOIN (SELECT USER_ID, MAX(REC_DATE) AS MAX_REC_DATE FROM COORDINATES GROUP BY USER_ID) MAX_DATE ON FRIENDS.FRIEND_ID = MAX_DATE.USER_ID
LEFT OUTER JOIN COORDINATES ON FRIENDS.FRIEND_ID = COORDINATES.USER_ID AND COORDINATES.REC_DATE = MAX_DATE.MAX_REC_DATE
order by 2
*/

?>  