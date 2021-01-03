<?php  
 
    require "init.php"; 

		
	if (isset($_POST["user_id"]) && isset($_POST["search_name"])){

	    $user_id = $_POST["user_id"];
	    $search_name =  strtolower($_POST["search_name"]);

 		$sql = "SELECT ID, NAME FROM USERS WHERE ID NOT IN 	(SELECT CASE WHEN USER_ID_ONE <> $user_id THEN USER_ID_ONE ELSE USER_ID_TWO END AS USER_ID_FRIEND FROM RELATIONS WHERE (USER_ID_ONE = $user_id OR USER_ID_TWO = $user_id) AND REQUEST_STATUS = 1) AND ID <> $user_id AND LOWER(NAME) LIKE '%$search_name%' order by 2;";

 		$result = mysqli_query($conn,$sql);


		if($result !== false && mysqli_num_rows($result)>0){

			$users_not_friends = array();
			while($row = mysqli_fetch_array($result)){

				array_push($users_not_friends,array(
													 "ID" => isset($row["ID"]) ? $row["ID"] : null 
													,"NAME" => isset($row["NAME"]) ? $row["NAME"] : null
							)
				);
				//array_push($users_not_friends,array("NAME"=>$row[1]));
			}

			$responce = array(
				 "success" => true
				,"users" => $users_not_friends
			);


		}  else {
			// we CANT LOGIN, user not found

			$responce = array(
				 "success" => false
				,"error" => "No users found"
				// ,"user" => $user->ID
			);
		}

	} else {

		$responce = array(
				 "success" => false
				,"error" => "Please provide user id and search string"
			);

	}


	echo json_encode($responce);

				

/*
SELECT ID, NAME FROM USERS 
WHERE ID NOT IN (SELECT 
                    CASE 
                        WHEN USER_ID_ONE <> 3 THEN USER_ID_ONE
                        ELSE USER_ID_TWO
                        END AS USER_ID_FRIEND
                    FROM RELATIONS
                    WHERE (USER_ID_ONE = 3 OR USER_ID_TWO = 3) AND REQUEST_STATUS = 1)
	AND ID <> 3
    AND LOWER(NAME) LIKE '%y%'
    order by 2  
*/

?>  