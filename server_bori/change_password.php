<?php  
 
    require "init.php"; 

		
	if (isset($_POST["user_id"]) && isset($_POST["new_password"])){

	    $user_id = $_POST["user_id"];  
		$new_password= $_POST["new_password"]; 

		// var_dump($new_password); exit();

 		$sql = "select * from USERS where ID = '$user_id'";

 		$result = mysqli_query($conn,$sql);


		if(mysqli_num_rows($result)>0){
			// $new_pass_hash = password_hash($new_password, PASSWORD_BCRYPT);
			$new_pass_hash = md5($new_password);

			$sql = "UPDATE USERS SET PASSWORD='$new_pass_hash' WHERE ID=$user_id";

			$user_updated_result = mysqli_query($conn,$sql);

			if ($user_updated_result === TRUE){
				$responce = array(
					 "success" => true
				);

			} else {

				$responce = array(
					 "success" => false
					,"error" => "Couldn\'t update password. Try again later."
				);
				          
			};

			

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
				,"error" => "Please provide user id and password."
			);

	}

	mysqli_close($conn);

	echo json_encode($responce);

?>  				