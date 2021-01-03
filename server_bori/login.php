<?php  
 
    require "init.php"; 

		
	if (isset($_POST["login_name"]) && isset($_POST["login_pass"])){

	    $user_name = $_POST["login_name"];  
		$user_pass= $_POST["login_pass"]; 

 		$sql = "select * from USERS where NAME = '$user_name'";

 		$result = mysqli_query($conn,$sql);


		if(mysqli_num_rows($result)>0){

			$row = mysqli_fetch_array($result);


			// if (password_verify($user_pass, $row['PASSWORD'])) {
			if (md5($user_pass) == $row['PASSWORD']) {
				$result_users = array(
					 "ID" => isset($row["ID"]) ? $row["ID"] : null 
					// ,"NAME"=>$row[1]
					,"NAME" => isset($row["NAME"]) ? $row["NAME"] : null 
					// ,"PASSWORD" => isset($row["PASSWORD"]) ? $row["PASSWORD"] : null 
					,"DATE_REGISTERES" => isset($row["DATE_REGISTERES"]) ? $row["DATE_REGISTERES"] : null
					
				);

				$responce = array(
					 "success" => true
					,"user" => $result_users
				);
			} else {
				$responce = array(
					'success' => false,
					'error' => 'Wrong password.'
				);
			}


		} else {
			// we CANT LOGIN, user not found

			$responce = array(
				 "success" => false
				,"error" => "User not found"
				// ,"user" => $user->ID
			);
		}

	} else {

		$responce = array(
				 "success" => false
				,"error" => "Please provide user name and password."
			);

	}


	echo json_encode($responce);

?>  				