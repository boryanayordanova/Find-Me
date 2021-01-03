<?php  

	require "init.php";  

	if (isset($_POST["user_id_one"]) && isset($_POST["user_id_two"])){

		$user_id_one = $_POST["user_id_one"];  
		$user_id_two= $_POST["user_id_two"]; 


		// CHECK IF ITS A PENDING REQUEST, IF YES, DONT ADD A NEW ONE
		$select_sql_query = "select * from RELATIONS where (USER_ID_ONE = '$user_id_one' and USER_ID_TWO = '$user_id_two') OR (USER_ID_ONE = '$user_id_two' and USER_ID_TWO = '$user_id_one') and REQUEST_STATUS = 0;"; //" name = '$user_name';";

		// var_dump($select_sql_query);
		$result = mysqli_query($conn,$select_sql_query); 
		// var_dump($result);

		if( mysqli_num_rows($result)>0 ){
			// error, record exists

			$responce = array(
				 "success" => false
				,"error" => "Friend request exists and its pending." //.mysqli_error($conn)
			);

		} else {

			// NOT FRIENDS AND DONT HAVE A PENDING STATE
			$insert_sql_query = "insert into RELATIONS(USER_ID_ONE, USER_ID_TWO, REQUEST_STATUS) values('$user_id_one','$user_id_two', 0);"; // create a pending request

			$user_inserted_result = mysqli_query($conn, $insert_sql_query);  


			if ($user_inserted_result === TRUE){
				$found_inserted = mysqli_query($conn, $select_sql_query);
				$user = $found_inserted->fetch_object();
				
				// var_dump($user);
				$responce = array(
					 "success" => true
					// ,"user" => $user->ID
				);

			} else {

				$responce = array(
					 "success" => false
					,"error" => "Friend request failed, please try again later"
				);
				          
			};

			mysqli_close($conn);

		}


	 } else {

	 	$responce = array(
				 "success" => false
				,"error" => "Please provide user id one and user id two."
			);

	 }

	echo json_encode($responce);
	// mysqli_free_result($result);
	
?>  			