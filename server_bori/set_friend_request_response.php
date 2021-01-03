<?php  
 
    require "init.php"; 

		
	if (isset($_POST["user_id_one"]) && isset($_POST["user_id_two"]) && isset($_POST["answer"])){

	    $user_id_one = $_POST["user_id_one"];
	    $user_id_two = $_POST["user_id_two"];
	    $answer = $_POST["answer"]; // 0 - deny, 1 - allow friendship

 		$sql = "SELECT ID FROM RELATIONS WHERE (USER_ID_ONE = $user_id_one AND USER_ID_TWO = $user_id_two) OR (USER_ID_ONE = $user_id_two AND USER_ID_TWO = $user_id_one) AND REQUEST_STATUS = 0";

 		$result = mysqli_query($conn,$sql);


		if($result !== false && mysqli_num_rows($result)>0){

			$row = mysqli_fetch_array($result);
			$record_id = isset($row["ID"]) ? $row["ID"] : null ;

			if ($record_id != null ){

				// check answer
				if ($answer != null && $answer == 1){

					$sql = "update RELATIONS set REQUEST_STATUS = 1 where ID = $record_id;";

					$result_update = mysqli_query($conn,$sql);

					if($result_update !== false){
						
						$responce = array(
							 "success" => true
							// ,"user" => $users_friends
						);

					} else {

						$responce = array(
							 "success" => false
							,"error" => "Updated not suckessfull"
							// ,"user" => $users_friends
						);
					}


				} else if ($answer != null && $answer == 0) {

						// remove relation
						$sql = "delete from RELATIONS where ID = $record_id;";

						$result_remove = mysqli_query($conn,$sql);

						if($result_remove !== false){
							
							$responce = array(
								 "success" => true
								// ,"user" => $users_friends
							);

						} else {

							$responce = array(
								 "success" => false
								,"error" => "Remove not suckessfull"
								// ,"user" => $users_friends
							);
						}

				} else {

					$responce = array(
								 "success" => false
								,"error" => "Wrong parameters"
								// ,"user" => $users_friends
							);

				}

			}


		}  else {
			// we CANT LOGIN, user not found

			$responce = array(
				 "success" => false
				,"error" => "No connection between users"
				// ,"user" => $user->ID
			);
		}

	} else {

		$responce = array(
				"success" => false
				,"error" => "Please provide user id one, user id two and answer to request"
			);

	}


	echo json_encode($responce);

				

/*
SELECT 
CASE 
	WHEN USER_ID_ONE <> 3 THEN USER_ID_ONE
    ELSE USER_ID_TWO
    END AS USER_ID_FRIEND
FROM RELATIONS
WHERE (USER_ID_ONE = 3 OR USER_ID_TWO = 3) AND REQUEST_STATUS = 1
*/

?>  
