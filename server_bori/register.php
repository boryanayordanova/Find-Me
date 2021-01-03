<?php  

	require "init.php";  

	if (isset($_POST["reg_name"]) && isset($_POST["reg_pass"]) && isset($_POST["reg_date"])){

		$user_name = $_POST["reg_name"];  
		$user_pass= $_POST["reg_pass"]; 
		$user_reg_date= $_POST["reg_date"]; 

		$select_sql_query = "select * from USERS where NAME = '$user_name';";//" and password = '$user_pass';";

		// var_dump($select_sql_query);
		$result = mysqli_query($conn,$select_sql_query);  
		if(mysqli_num_rows($result)>0 ){
			// error, record exists

			$responce = array(
				 "success" => false
				,"error" => "User exists" //.mysqli_error($conn)
			);

		} else {
			
			// we can create new user
			// $user_pass = password_hash($user_pass, PASSWORD_BCRYPT);
			$user_pass = md5($user_pass);
			$insert_sql_query = "insert into USERS(NAME, PASSWORD, DATE_REGISTERES) values('$user_name','$user_pass','$user_reg_date');";
			$user_inserted_result = mysqli_query($conn, $insert_sql_query);  

			if ($user_inserted_result === TRUE){
				$responce = array(
					 "success" => true
					// ,"user" => $user->ID
				);

			} else {

				$responce = array(
					 "success" => false
					,"error" => "Registration failed, please try again later"
				);
				          
			};

			mysqli_close($conn);

		}


	 } else {

	 	$responce = array(
				 "success" => false
				,"error" => "Please provide user name, password and registration date"
			);

	 }

	echo json_encode($responce);
	
?>  			